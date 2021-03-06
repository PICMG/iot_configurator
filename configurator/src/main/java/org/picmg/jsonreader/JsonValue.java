package org.picmg.jsonreader;
/*
 * Author: Douglas L. Sandy
 * Copyright (C) 2020, Arizona State University
 * All Rights Reserved
 */


import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Represents a concrete JSON value.  Note that this class represents the 
 * all JSON value types as a string.  Getter functions interpret the string as 
 * specific data types.
 */
public class JsonValue implements JsonAbstractValue {
    private String value;

    public JsonValue() {
    }
    
	/*
	 * create a deep clone of the specified json value
	 */
	public JsonValue(JsonValue val) {
		if (val.value == null) {
			this.value = null;
		} else {
			this.value = new String(val.value);
		}
	}

    @Override
    /**
     * diagnostic function to dump the value to the system output device
     */
    public void dump(int indent) {
        for (int i=0;i<indent;i++) System.out.print(" ");
        System.out.println(value);
    }
    
    /**
     * constructor - create the value and initialize it from the provided string.
     */
    public JsonValue(String value) {
        if ((value==null)||(value.equalsIgnoreCase("null"))) {
        	this.value = null;
        } else {
        	this.value = value;
        }
    }

    /**
     * set()
     * Set the value of the json object to match the specified string.
     * @param value - the new value to be set.
     */
    public void set(String value) {
        if ((value==null)||(value.equalsIgnoreCase("null"))) {
            this.value = null;
        } else {
            this.value = value;
        }
    }

    @Override
    /**
     * return the value as a string 
     */
    public String  getValue(String specifier) {
        if (specifier.isEmpty()) {
        	if ((value == null) || (value.equalsIgnoreCase("null"))) return null;
        	return value; 
        }
        return "";
    }
    
    @Override
    /**
     * return the value as an integer
     */
    public int     getInteger(String specifier) {
        if (specifier.isEmpty()) {
            try {
                return Integer.parseInt(value); 
            }
            catch (NumberFormatException e) {
            }
        }
        return 0;
    }

    @Override
    /**
     * return the value as a double
     */
    public double  getDouble(String specifier) {
    	if (specifier.isEmpty()) {
            try {
            	if (value==null) {
            		return 0.0;
            	}
                return Double.parseDouble(value);
            }
            catch (NumberFormatException e) {
            }
        }
        return 0;
    }
    
    @Override
    /**
     * return the value as a boolean
     */
    public boolean getBoolean(String specifier) {
        if (specifier.isEmpty()) {
            return Boolean.parseBoolean(value); 
        }
        return false;        
    }
    
    @Override
    /**
     * return as a JSON handle represented as a string
     */
    public String  getHandle(String specifier) {
        return getValue(specifier);
    };   
    
    @Override
    /**
     * write the JsonValue to the file specified by the BufferedWriter parameter
     * 
     * @param br - a buffered writer associated with the output file
     * @return true on success, otherwise false
     */
    public boolean writeToFile(BufferedWriter br) {
        boolean isNumber = false;
        if (value == null) {
            try {
                br.append("null");
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        try {
            Double.parseDouble(value);
            isNumber = true;
        } catch (NumberFormatException nfe) {}    
        try {
            Long.parseLong(value);
            isNumber = true;
        } catch (NumberFormatException nfe) {}    
        try {
            if (!isNumber) br.append('"');
            br.append(value);
            if (!isNumber) br.append('"');
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
}
