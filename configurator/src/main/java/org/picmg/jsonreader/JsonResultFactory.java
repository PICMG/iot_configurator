package org.picmg.jsonreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/*
 * Author: Douglas L. Sandy
 * Copyright (C) 2020, Arizona State University
 * All Rights Reserved
 */


/**
 * Represents an abstract factory class that builds JsonAbstractValues based on a Json-formatted string
 */
public class JsonResultFactory {
    int strpos;
    String str;

    /* helper function to skip past white space */
    private void skipWhitespace() {
    	while (strpos<str.length()) {
    		if (str.charAt(strpos)<=' ') {
    			strpos++;
    		} else {
    			break;
    		}
    	}
    }
    
    /* 
     * helper function used by builder to extract a double quoted string 
     */
    private String getString() {
        if (str.charAt(strpos)!='\"') return null;
        strpos ++;
        int start = strpos;
        boolean ignoreNext = false;
        while (strpos<str.length()) {
            if ((str.charAt(strpos) == '\"') && (!ignoreNext)) {
                String result = str.substring(start,strpos);
                strpos++;
                return result;
            }
            ignoreNext = false;
            if (str.charAt(strpos)=='\\') ignoreNext = true;
            strpos++;
        }
        return null;
    }
    
    /*
     * helper function used by builder to extract a string that is delimited by 
     * JSON ending delimiters.
     */
    private String getRaw() {
        int start = strpos;
        while (strpos<str.length()) {
            if ((str.charAt(strpos) == ',') || (str.charAt(strpos) == '}') ||
                    (str.charAt(strpos) == ']')) {
                String result = str.substring(start,strpos).trim();
                return result;
            } 
            strpos++;
        }
        return null;
    }

    /**
     * entry point for the builder.  Builds a JsonAbstractValue based on the input string
     * @param str - the JSON formatted string that specifies the structure to build
     * @return A JsonAbstractValue structure that matches the input string
     */
    public JsonAbstractValue build(String str) {
        // trim leading and trailing whitespace
        this.str = new String(str.trim());
        strpos = 0;
        return builder();
    }
    
    /**
     * entry point for the builder.  Builds a JsonAbstractValue from a file
     * @param resource - the JSON formatted string that specifies the structure to build
     * @return A JsonAbstractValue structure that matches the input string
     * @throws  
     */
    public JsonAbstractValue buildFromResource(String resource) {
	    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	    InputStream is = classLoader.getResourceAsStream(resource);
	    if (is == null) return null;
	    InputStreamReader isr = new InputStreamReader(is,StandardCharsets.UTF_8);
    	BufferedReader reader = new BufferedReader(isr); 
        String st = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        // convert the string that was read into a JsonObject
        return build(st.toString());
    }

    /**
     * entry point for the builder.  Builds a JsonAbstractValue from a file
     * @param path - a path object to the file to read.
     * @return A JsonAbstractValue structure that matches the input string
     * @throws
     */
    public JsonAbstractValue buildFromFile(Path path) {
        try {
            // open the file for reading
            InputStream is = Files.newInputStream(path);
            if (is == null) return null;
            InputStreamReader isr = new InputStreamReader(is,StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);

            // read the entire file
            String st = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            // convert the string that was read into a JsonObject
            return build(st.toString());
        } catch (IOException e) {
            return null;
        }
    }

    /*
     * helper class to build the JsonAbstractValue
     */
    private JsonAbstractValue builder() {
        if (str.isEmpty()) return null;
        
        if (str.charAt(strpos)=='[') {
            strpos++;
            
            skipWhitespace();
            
            // here if we need to create a value set
            JsonArray cs = new JsonArray();
            while ((strpos<str.length())&&((str.charAt(strpos)=='"')||str.charAt(strpos)=='{')) {
                // create and build the object or string
                JsonAbstractValue obj = builder();
                if (obj==null) {
                    System.err.println("null object returned at "+String.valueOf(strpos));
                    return null;
                }
                cs.add(obj);

                // next character should either be a comma or an end brace
                skipWhitespace();
                if (strpos>=str.length()) {
                    System.err.println("unexpected end of string");
                    return null;
                }
                if (str.charAt(strpos)==']') break;
                if (str.charAt(strpos)==',') strpos++;  
                skipWhitespace();
            }
            skipWhitespace();
            if (str.charAt(strpos)!=']') {
                System.err.println("']' expected but none found"+String.valueOf(strpos));               
                return null;
            }
            strpos++;
            return cs;
        }

        if (str.charAt(strpos)=='{') {
            strpos++;
            // here if we need to create a json object
            JsonObject co = new JsonObject();
            
            skipWhitespace();
            
            // check for an empty object.
            if ((strpos<str.length()+1)&&(str.charAt(strpos)=='}')) {
                strpos+=2;
                return co;
            }
            
            while (strpos<str.length()) {
                String key = getString();
                if (key == null) return null;
                if (strpos>=str.length()) {
                    System.err.println("unexpected end of string");
                    return null;
                }

                skipWhitespace();
                if (str.charAt(strpos)!=':') {
                    System.err.println("keyword separator expected.  None found");
                    return null;
                }
                strpos++;

                // create and build the value
                skipWhitespace();
                JsonAbstractValue obj = builder();
                if (obj==null) return null;
                co.put(key,obj);

                // next character should either be a comma or an end brace
                skipWhitespace();
                if (strpos>=str.length()) {
                    System.err.println("Unexpected end of string");
                    return null;
                }
                if (str.charAt(strpos)=='}') break;
                if (str.charAt(strpos)==',') strpos++;                
                skipWhitespace();
            }
            skipWhitespace();
            if (str.charAt(strpos)!='}') return null;
            strpos++;
            return co;
        }
        
        // here if the line is a value primitive
        if (str.charAt(strpos)=='"') {
            String s = getString();
            s.trim();
            if (s==null) {
                System.err.println("Null string returned");
                return null;
            }
            JsonValue cv = new JsonValue(s);
            skipWhitespace();
            return cv;
        } 
        // here if the value primitive is not quoted
        String s = getRaw();
        if (s==null) {
            System.err.println("Null raw value returned");
            return null;
        }
        JsonValue cv = new JsonValue(s);
        skipWhitespace();
        return cv;
        
        
    } 
}
