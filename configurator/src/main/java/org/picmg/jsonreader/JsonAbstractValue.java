package org.picmg.jsonreader;
/*
 * Author: Douglas L. Sandy
 * Copyright (C) 2020, Arizona State University
 * All Rights Reserved
 */


import java.io.BufferedWriter;

/**
 * representation for abstract base class for Json objects.  This class should never
 * be directly instantiated. 
 */
public interface JsonAbstractValue {
    void    dump(int indent);
    boolean writeToFile(BufferedWriter br);
    String  getValue(String specifier);
    int     getInteger(String specifier);
    double  getDouble(String specifier);
    boolean getBoolean(String specifier);
    String  getHandle(String specifier);    
}
