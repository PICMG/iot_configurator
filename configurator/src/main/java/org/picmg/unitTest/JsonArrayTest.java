package org.picmg.unitTest;

import org.junit.Before;
import org.junit.Test;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class JsonArrayTest {

    String lineSeparator;

    @Before
    public void handlePlatform() {
        lineSeparator = System.getProperty("line.separator");
    }

    @Test
    public void testGetValue() {
        JsonArray jsonArray = new JsonArray();
        assertEquals("", jsonArray.getValue("0.")); // isEmpty case
        jsonArray.add(0, new JsonValue("ABC"));
        jsonArray.add(1, new JsonValue("EFG"));
        assertEquals("ABCEFG", jsonArray.getValue(""));
        assertEquals("ABC", jsonArray.getValue("0."));
        assertEquals("EFG", jsonArray.getValue("1."));
        assertEquals("", jsonArray.getValue("2.")); // Array Index out of bounds
    }

    @Test
    public void testGetBoolean() {
        JsonArray jsonArray = new JsonArray();
        assertFalse(jsonArray.getBoolean("0.")); // isEmpty case
        jsonArray.add(0, new JsonValue("true"));
        jsonArray.add(1, new JsonValue("false"));
        jsonArray.add(2, new JsonValue("notABool"));
        assertTrue(jsonArray.getBoolean("0."));
        assertFalse(jsonArray.getBoolean("1."));
        assertFalse(jsonArray.getBoolean("2."));
        assertFalse(jsonArray.getBoolean("3.")); // Array Index out of bounds
    }

    @Test
    public void testGetInteger() {
        JsonArray jsonArray = new JsonArray();
        assertEquals(0, jsonArray.getInteger("0.")); // isEmpty case
        jsonArray.add(0, new JsonValue("1"));
        jsonArray.add(1, new JsonValue("-1"));
        assertEquals(1, jsonArray.getInteger("0."));
        assertEquals(-1, jsonArray.getInteger("1."));
        assertEquals(0, jsonArray.getInteger("2.")); // Array Index out of bounds
    }

    @Test
    public void testGetDouble() {
        JsonArray jsonArray = new JsonArray();
        assertEquals(0, jsonArray.getDouble("0."), 0); // isEmpty case
        jsonArray.add(0, new JsonValue("1.1"));
        jsonArray.add(1, new JsonValue("-1.1"));
        assertEquals(1.1, jsonArray.getDouble("0."), 0);
        assertEquals(-1.1, jsonArray.getDouble("1."), 0);
        assertEquals(0, jsonArray.getDouble("2."), 0); // Array Index out of bounds
    }

    @Test
    public void testGetHandle() {
        JsonArray jsonArray = new JsonArray();
        assertEquals("", jsonArray.getHandle("0.")); // isEmpty case
        jsonArray.add(0, new JsonValue("ABC"));
        assertEquals("ABC", jsonArray.getHandle("0."));
        assertEquals("", jsonArray.getHandle("1."));
    }

    @Test
    public void testFindMatching() {
        JsonArray jsonArray = new JsonArray();
        assertNull(jsonArray.findMatching("number", "2"));
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("number", new JsonValue("2"));
        jsonArray.add(0, jsonObject);
        assertNull(jsonArray.findMatching("number", "3"));
        assertNull(jsonArray.findMatching("string", "2"));
        assertEquals(jsonObject, jsonArray.findMatching("number", "2"));
    }

    @Test
    public void testContainsAny() {
        JsonArray jsonArray = new JsonArray();
        assertFalse(jsonArray.containsAny((JsonArray) null));
        assertFalse(jsonArray.containsAny((ArrayList<String>) null));

        jsonArray.add(new JsonValue("ABC"));
        jsonArray.add(new JsonValue("EFG"));
        jsonArray.add(new JsonValue("HIJ"));
        jsonArray.add(new JsonValue("KLM"));
        jsonArray.add(new JsonValue("NOP"));

        JsonArray emptyArray = new JsonArray();
        ArrayList<String> emptyStringArray = new ArrayList<>();
        assertFalse(jsonArray.containsAny(emptyArray));
        assertFalse(jsonArray.containsAny(emptyStringArray));

        JsonArray sampleArray = new JsonArray();
        sampleArray.add(new JsonValue("ABC"));
        sampleArray.add(new JsonValue("EFG"));
        assertTrue(jsonArray.containsAny(sampleArray));

        JsonArray uniqueArray = new JsonArray();
        uniqueArray.add(new JsonValue("XYZ"));
        ArrayList<String> uniqueStringArray = new ArrayList<>();
        uniqueStringArray.add("XYZ");
        assertFalse(jsonArray.containsAny(uniqueArray));
        assertFalse(jsonArray.containsAny(uniqueStringArray));
    }

    @Test
    public void testDump() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        System.out.print("START"); // Prevents unintentional trimming of indent

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(new JsonValue("ABC"));
        jsonArray.dump(3);
        assertEquals("START   [" + lineSeparator +
                "      ABC" + lineSeparator +
                "   ]", byteArrayOutputStream.toString().trim());
    }

    @Test
    public void testCloneConstructor() {
        JsonArray stringJsonArray = new JsonArray();
        stringJsonArray.add(new JsonValue("ABC"));
        stringJsonArray.add(new JsonValue("EFG"));

        JsonArray objectJsonArray = new JsonArray();
        JsonValue boolValue = new JsonValue("true");
        JsonObject boolObj = new JsonObject();
        boolObj.put("bool", boolValue);
        objectJsonArray.add(boolObj);

        JsonArray arrayJsonArray = new JsonArray();
        JsonArray array = new JsonArray();
        JsonValue sampleValue = new JsonValue("123");
        array.add(sampleValue);
        arrayJsonArray.add(array);

        JsonArray clonedJsonArray = new JsonArray(stringJsonArray);
        assertEquals("ABC", clonedJsonArray.getValue("0."));
        assertEquals("EFG", clonedJsonArray.getValue("1."));

        clonedJsonArray = new JsonArray(objectJsonArray);
        assertEquals("true", clonedJsonArray.getValue("0.bool"));

        clonedJsonArray = new JsonArray(arrayJsonArray);
        assertEquals("123", clonedJsonArray.getValue("0.0."));
    }

    @Test
    public void testWriteToFile() throws UnsupportedEncodingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(0, new JsonValue("Position 0"));
        jsonArray.add(1, new JsonValue("Position 1"));
        assertTrue(jsonArray.writeToFile(bufferedWriter));
    }

}
