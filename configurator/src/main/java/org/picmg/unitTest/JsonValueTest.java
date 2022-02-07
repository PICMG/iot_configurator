package org.picmg.unitTest;

import org.junit.Test;
import org.picmg.jsonreader.JsonValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonValueTest {

    @Test
    public void testSetValue() {
        JsonValue jsonValue = new JsonValue();
        assertNull(jsonValue.getValue(""));
        jsonValue.set("ABC");
        assertEquals("ABC", jsonValue.getValue(""));
    }
}
