package org.picmg.jsonreader;
/*
 * Author: Douglas L. Sandy
 * Copyright (C) 2020, Arizona State University
 * All Rights Reserved
 */


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A JSON array object.  This is a concrete implementation of the JavaAbstractValue class.
 */
public class JsonArray extends ArrayList<JsonAbstractValue> implements JsonAbstractValue {
	private static final long serialVersionUID = 1L;

	public JsonArray() {
		super();
	}
	
	/*
	 * create a deep clone of the specified json array
	 */
	public JsonArray(JsonArray ary) {
		super();
		ary.forEach(value -> {
			  if (value.getClass().isAssignableFrom(JsonArray.class)) {
				  this.add(new JsonArray((JsonArray)value));
			  } else if (value.getClass().isAssignableFrom(JsonObject.class)) {
				  this.add(new JsonObject((JsonObject)value));
			  } else {
				  this.add(new JsonValue((JsonValue)value));
			  }
		});
	}
	
	@Override
	/* 
	 * diagnostic function - dumps the contents of the json array to the system output device
	 */
    public void dump(int indent) {
        for (int i=0;i<indent;i++) System.out.print(" ");
        System.out.println("[");
        this.forEach((obj) -> {
            obj.dump(indent+3);
        });
        for (int i=0;i<indent;i++) System.out.print(" ");
        System.out.println("]");        
    }

    @Override
    /*
     * returns a string value of the specified element where the specifier
     * is of the form [index].specifier
     */
    public String getValue(String specifier) {
        if (isEmpty()) return "";
        
        if (specifier.isEmpty()) {
            // The specifier is empty - return values for all records in the
            // object (this should not be normal)
            StringBuilder sb = new StringBuilder();
            this.forEach((obj) -> {sb.append(obj.getValue(""));});
            return sb.toString();
        }
        // use the leftmost part of the specifier as the key
        String[] key = specifier.split("[.]",2);
        int index = Integer.valueOf(key[0]);
        if (index<size()) {
            return get(index).getValue(key[1]);
        }
        return "";
    }

    @Override
    /*
     * returns a boolean value of the specified element where the specifier
     * is of the form [index].specifier
     */
    public boolean getBoolean(String specifier) {
        if (isEmpty()) return false;
        // use the leftmost part of the specifier as the key
        String[] key = specifier.split("[.]",2);
        int index = Integer.valueOf(key[0]);
        if (index<size()) {
            return get(index).getBoolean(key[1]);
        }
        return false;    
    }

    @Override
    /*
     * returns the object handle of the specified element where the specifier
     * is of the form [index].specifier
     */
    public String getHandle(String specifier) {
        if (isEmpty()) return "";
        // use the leftmost part of the specifier as the key
        String[] key = specifier.split("[.]",2);
        int index = Integer.valueOf(key[0]);
        if (index<size()) {
            return get(index).getHandle(key[1]);
        }
        return "";    
    }

    @Override
    /*
     * returns the integer value of the specified element where the specifier
     * is of the form [index].specifier
     */
    public int getInteger(String specifier) {
        if (isEmpty()) return 0;
        // use the leftmost part of the specifier as the key
        String[] key = specifier.split("[.]",2);
        int index = Integer.valueOf(key[0]);
        if (index<size()) {
            return get(index).getInteger(key[1]);
        }
        return 0;    
    }

    @Override
    /*
     * returns the double value of the specified element where the specifier
     * is of the form [index].specifier
     */
    public double getDouble(String specifier) {
        if (isEmpty()) return 0;
        // use the leftmost part of the specifier as the key
        String[] key = specifier.split("[.]",2);
        int index = Integer.valueOf(key[0]);
        if (index<size()) {
            return get(index).getDouble(key[1]);
        }
        return 0;    
    }
    
    @Override
    /*
     * write the array to the file specified by the buffered writer
     */
    public boolean writeToFile(BufferedWriter br) {
        try {
            br.append('[');
            boolean isFirst = true;
            for (JsonAbstractValue value: this) {
                if (!isFirst) br.append(',');
                isFirst = false;
                value.writeToFile(br);
            }
            br.append(']');
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    /*
     * returns true if the array contains any of the elements in the
     * specified parameter
     */
    public boolean containsAny(JsonArray ary) {
    	if (ary==null) return false;
    	if (ary.isEmpty()) return false;
    	for (int i=0;i<ary.size();i++) {
    		for (int j=0;j<this.size();j++) {
    			if (this.get(j).getValue("").equals(ary.get(i).getValue(""))) return true;
    		}
    	}
    	return false;
    }
    
    /*
     * returns true if the array values contain any elements whose values
     * match the specified string array
     */
    public boolean containsAny(ArrayList<String> ary) {
    	if (ary==null) return false;
    	if (ary.isEmpty()) return false;
    	for (int i=0;i<ary.size();i++) {
    		for (int j=0;j<this.size();j++) {
    			if (this.get(j).getValue("").equals(ary.get(i))) return true;
    		}
    	}
    	return false;
    }
    
    /*
     * find an object in the array that contains a key value pair
     * that matches the specified parameters.  
     */
    public JsonObject findMatching(String key, String value) {
    	for (int i=0;i<this.size();i++) {
			if (this.get(i).getClass().isAssignableFrom(JsonObject.class)) {
				// here if the current array element is a json object
				JsonObject jo = (JsonObject)this.get(i);
				if (jo.containsKey(key)&&(jo.getValue(key).equals(value))) {
					// return the matching object
					return jo;
				}
			}
		}
    	return null;
    }
}
