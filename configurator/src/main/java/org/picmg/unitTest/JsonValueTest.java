package org.picmg.unitTest;

import org.junit.Test;
import org.picmg.jsonreader.JsonValue;

import java.io.*;

import static org.junit.Assert.*;

public class JsonValueTest {

    @Test
    public void testGetValue() {
        JsonValue jsonValue = new JsonValue("ABC");
        assertEquals("", jsonValue.getValue("some specifier"));
        assertEquals("ABC", jsonValue.getValue(""));
    }

    @Test
    public void testSetValue() {
        JsonValue jsonValue = new JsonValue();
        assertNull(jsonValue.getValue(""));
        jsonValue.set("ABC");
        assertEquals("ABC", jsonValue.getValue(""));
    }

    @Test
    public void testGetInteger() {
        JsonValue jsonValue = new JsonValue("12");
        assertEquals(12, jsonValue.getInteger(""));
        assertEquals(0, jsonValue.getInteger("some specifier"));

        JsonValue nonIntegerJsonValue = new JsonValue("twelve");
        assertEquals(0, nonIntegerJsonValue.getInteger(""));
    }

    @Test
    public void testGetDouble() {
        JsonValue jsonValue = new JsonValue("12.3");
        assertEquals(12.3, jsonValue.getDouble(""), 0);
        assertEquals(0, jsonValue.getDouble("some specifier"), 0);

        JsonValue nonDoubleJsonValue = new JsonValue("twelve point three");
        assertEquals(0, nonDoubleJsonValue.getDouble(""), 0);

        JsonValue nullJsonValue = new JsonValue();
        nullJsonValue.set("null");
        assertEquals(0.0, nullJsonValue.getDouble(""), 0);
    }

    @Test
    public void testGetBoolean() {
        JsonValue jsonValue = new JsonValue("true");
        assertEquals(true, jsonValue.getBoolean(""));
        assertEquals(false, jsonValue.getBoolean("some specifier"));

        JsonValue falseJsonValue = new JsonValue("false");
        assertEquals(false, falseJsonValue.getBoolean(""));
        assertEquals(false, falseJsonValue.getBoolean("some specifier"));

        JsonValue nonBooleanJsonValue = new JsonValue("not true or false");
        assertEquals(false, nonBooleanJsonValue.getBoolean(""));
    }

    @Test
    public void testGetHandle() {
        JsonValue jsonValue = new JsonValue();
        jsonValue.set("ABC");
        assertEquals("ABC", jsonValue.getHandle(""));
    }

    @Test
    public void testDump() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));

        JsonValue jsonValue = new JsonValue();
        jsonValue.set("ABC");
        System.out.print("START"); // Prevents unintentional trimming of indent
        jsonValue.dump(3);
        assertEquals("START   ABC", byteArrayOutputStream.toString().trim());
    }

    @Test
    public void testValueConstructor() {
        JsonValue jsonValue = new JsonValue("CBA");
        assertEquals("CBA", jsonValue.getValue(""));

        JsonValue nullJsonValue = new JsonValue("null");
        assertEquals(null, nullJsonValue.getValue(""));
    }

    @Test
    public void testWriteToFile() throws UnsupportedEncodingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        JsonValue jsonValue = new JsonValue("XYZ");
        assertTrue(jsonValue.writeToFile(bufferedWriter));

        JsonValue doubleJsonValue = new JsonValue("12.3");
        assertTrue(doubleJsonValue.writeToFile(bufferedWriter));

        JsonValue nullJsonValue = new JsonValue("null");
        assertTrue(nullJsonValue.writeToFile(bufferedWriter));
    }

    @Test
    public void testCloneConstructor() {
        JsonValue jsonValue = new JsonValue("XYZ");

        JsonValue clonedJsonValue = new JsonValue(jsonValue);
        assertEquals("XYZ", clonedJsonValue.getValue(""));

        JsonValue nullJsonValue = new JsonValue();
        nullJsonValue.set("null");

        JsonValue clonedNullJsonValue = new JsonValue(nullJsonValue);
        assertEquals(null, clonedNullJsonValue.getValue(""));
    }
}
