package org.picmg.unitTest;

import org.junit.Before;
import org.junit.Test;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.io.*;

import static org.junit.Assert.*;

public class JsonObjectTest {

    String lineSeparator;

    @Before
    public void handlePlatform() {
        lineSeparator = System.getProperty("line.separator");
    }

    @Test
    public void testGetBoolean() {
        JsonObject jsonObject = new JsonObject();
        assertFalse(jsonObject.getBoolean("isTrue")); // Test isEmpty case
        jsonObject.put("isTrue", new JsonValue("true"));
        assertFalse(jsonObject.getBoolean("notAKey")); // Test unknown key
        assertTrue(jsonObject.getBoolean("isTrue")); // Happy Day Case

        JsonObject childObject = new JsonObject();
        childObject.put("isTrue", new JsonValue("true"));
        jsonObject.put("child", childObject);
        assertTrue(jsonObject.getBoolean("child.isTrue")); // Get child value
    }

    @Test
    public void testGetValue() {
        JsonObject jsonObject = new JsonObject();
        assertEquals("", jsonObject.getValue("foo")); // Test isEmpty case
        jsonObject.put("foo", new JsonValue("bar"));
        assertEquals("bar", jsonObject.getValue("foo")); // Happy Day Case
        assertEquals("foo:bar\n", jsonObject.getValue("")); // Get full object
        assertNull(jsonObject.getValue("notAKey")); // Test unknown key

        JsonObject childObject = new JsonObject();
        childObject.put("position", new JsonValue("inside"));
        jsonObject.put("child", childObject);
        assertEquals("inside", jsonObject.getValue("child.position")); // Get child value

        jsonObject.put("nullValue", new JsonValue("null"));
        assertNull(jsonObject.getValue("nullValue")); // Test explicit null value

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(0, new JsonValue("Position 0"));
        jsonArray.add(1, new JsonValue("Position 1"));
        jsonObject.put("array", jsonArray);
        assertEquals("Position 0", jsonObject.getValue("array.0.")); // Test array
        assertEquals("Position 1", jsonObject.getValue("array.1."));
    }

    @Test
    public void testGetInteger() {
        JsonObject jsonObject = new JsonObject();
        assertEquals(0, jsonObject.getInteger("number")); // Test isEmpty case
        jsonObject.put("number", new JsonValue("1"));
        assertEquals(0, jsonObject.getInteger("notAKey")); // Test unknown key
        assertEquals(1, jsonObject.getInteger("number")); // Happy Day Case

        JsonObject childObject = new JsonObject();
        childObject.put("number", new JsonValue("5"));
        jsonObject.put("child", childObject);
        assertEquals(5, jsonObject.getInteger("child.number")); // Get child value
    }

    @Test
    public void testGetDouble() {
        JsonObject jsonObject = new JsonObject();
        assertEquals(0.0, jsonObject.getDouble("double"), 0); // Test isEmpty case
        jsonObject.put("double", new JsonValue("1.2"));
        assertEquals(0.0, jsonObject.getDouble("notAKey"), 0); // Test unknown key
        assertEquals(1.2, jsonObject.getDouble("double"), 0); // Happy Day Case

        JsonObject childObject = new JsonObject();
        childObject.put("number", new JsonValue("8.2"));
        jsonObject.put("child", childObject);
        assertEquals(8.2, jsonObject.getDouble("child.number"), 0); // Get child value
    }

    @Test
    public void testCloneConstructor() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("bool", new JsonValue("true"));
        jsonObject.put("string", new JsonValue("ABC"));
        jsonObject.put("number", new JsonValue("5"));
        jsonObject.put("double", new JsonValue("1.2"));
        jsonObject.put("null", new JsonValue("null"));
        JsonObject childObject = new JsonObject();
        childObject.put("string", new JsonValue("EFG"));
        jsonObject.put("child", childObject);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(0, new JsonValue("Position 0"));
        jsonArray.add(1, new JsonValue("Position 1"));
        jsonObject.put("array", jsonArray);

        JsonObject clonedJsonObject = new JsonObject(jsonObject);
        assertTrue(clonedJsonObject.getBoolean("bool"));
        assertEquals("ABC", clonedJsonObject.getValue("string"));
        assertEquals(5, clonedJsonObject.getInteger("number"));
        assertEquals(1.2, clonedJsonObject.getDouble("double"), 0);
        assertNull(clonedJsonObject.getValue("null"));
        assertEquals("EFG", clonedJsonObject.getValue("child.string"));
        assertEquals("Position 0", clonedJsonObject.getValue("array.0."));
        assertEquals("Position 1", clonedJsonObject.getValue("array.1."));
    }

    @Test
    public void testCopy() {
        JsonObject sourceObject = new JsonObject();
        sourceObject.put("bool", new JsonValue("true"));
        sourceObject.put("string", new JsonValue("ABC"));
        sourceObject.put("number", new JsonValue("5"));
        sourceObject.put("double", new JsonValue("1.2"));
        sourceObject.put("null", new JsonValue("null"));
        JsonObject childObject = new JsonObject();
        childObject.put("string", new JsonValue("EFG"));
        sourceObject.put("child", childObject);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(0, new JsonValue("Position 0"));
        jsonArray.add(1, new JsonValue("Position 1"));
        sourceObject.put("array", jsonArray);

        JsonObject copyObject = new JsonObject();
        copyObject.copy(sourceObject);
        assertTrue(copyObject.getBoolean("bool"));
        assertEquals("ABC", copyObject.getValue("string"));
        assertEquals(5, copyObject.getInteger("number"));
        assertEquals(1.2, copyObject.getDouble("double"), 0);
        assertNull(copyObject.getValue("null"));
        assertEquals("EFG", copyObject.getValue("child.string"));
        assertEquals("Position 0", copyObject.getValue("array.0."));
        assertEquals("Position 1", copyObject.getValue("array.1."));
    }

    @Test
    public void testDump() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("bool", new JsonValue("true"));
        jsonObject.put("string", new JsonValue("ABC"));
        jsonObject.put("number", new JsonValue("5"));
        jsonObject.put("double", new JsonValue("1.2"));
        jsonObject.put("null", new JsonValue("null"));
        JsonObject childObject = new JsonObject();
        childObject.put("string", new JsonValue("EFG"));
        jsonObject.put("child", childObject);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(0, new JsonValue("Position 0"));
        jsonArray.add(1, new JsonValue("Position 1"));
        jsonObject.put("array", jsonArray);

        System.out.print("START"); // Prevents unintentional trimming of indent
        jsonObject.dump(3);
        assertEquals("START   {" + lineSeparator +
                "      array:      [" + lineSeparator +
                "         Position 0" + lineSeparator +
                "         Position 1" + lineSeparator +
                "      ]" + lineSeparator +
                "      bool:      true" + lineSeparator +
                "      child:      {" + lineSeparator +
                "         string:         EFG" + lineSeparator +
                "      }" + lineSeparator +
                "      double:      1.2" + lineSeparator +
                "      null:      null" + lineSeparator +
                "      number:      5" + lineSeparator +
                "      string:      ABC" + lineSeparator +
                "   }", byteArrayOutputStream.toString().trim());
    }

    @Test
    public void testHandle() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("string", new JsonValue("ABC"));
        assertEquals("ABC", jsonObject.getHandle("string"));
    }

    @Test
    public void testWriteToFile() throws UnsupportedEncodingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("bool", new JsonValue("true"));
        jsonObject.put("string", new JsonValue("ABC"));
        jsonObject.put("number", new JsonValue("5"));
        jsonObject.put("double", new JsonValue("1.2"));
        jsonObject.put("null", new JsonValue("null"));
        JsonObject childObject = new JsonObject();
        childObject.put("string", new JsonValue("EFG"));
        jsonObject.put("child", childObject);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(0, new JsonValue("Position 0"));
        jsonArray.add(1, new JsonValue("Position 1"));
        jsonObject.put("array", jsonArray);
        assertTrue(jsonObject.writeToFile(bufferedWriter));
    }
}
