package org.picmg.test.unitTest;

import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.Device;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeviceTest {

    Device device;

    @Before
    public void mockDevice() {
        JsonObject ioBinding = new JsonObject();
        ioBinding.put("boundChannel", new JsonValue("Channel 1"));

        JsonArray ioBindings = new JsonArray();
        ioBindings.add(ioBinding);

        JsonArray fruRecords = new JsonArray();

        JsonObject channel = new JsonObject();
        channel.put("name", new JsonValue("Channel 1"));
        JsonArray channelPins = new JsonArray();
        channel.put("pins", channelPins);

        JsonObject channel2 = new JsonObject();
        channel2.put("name", new JsonValue("Channel 2"));
        JsonArray channel2Pins = new JsonArray();

        JsonObject pin1 = new JsonObject();
        JsonObject pin2 = new JsonObject();
        JsonObject pin3 = new JsonObject();

        pin1.put("name", new JsonValue("J1.1"));
        pin2.put("name", new JsonValue("J1.2"));
        pin3.put("name", new JsonValue("J1.3"));

        channel2Pins.add(pin1);
        channel2Pins.add(pin2);
        channel2Pins.add(pin3);

        channel2.put("pins", channel2Pins);

        JsonObject logicalEntity = new JsonObject();
        logicalEntity.put("name", new JsonValue("My Logical Entity"));
        logicalEntity.put("required", new JsonValue("true"));
        logicalEntity.put("ioBindings", ioBindings);

        JsonArray channels = new JsonArray();
        channels.add(channel);
        channels.add(channel2);

        JsonArray logicalEntities = new JsonArray();
        logicalEntities.add(logicalEntity);

        JsonObject capabilities = new JsonObject();
        capabilities.put("logicalEntities", logicalEntities);
        capabilities.put("channels", channels);
        capabilities.put("fruRecords", fruRecords);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("capabilities", capabilities);

        device = new Device(jsonObject);
    }

    @Test
    public void testGetLogicalEntityCapabilityByName() {
        assertNull(device.getLogicalEntityCapabilityByName("Not a Logical Entity"));

        JsonObject returnedLogicalEntity = device.getLogicalEntityCapabilityByName("My Logical Entity");
        assertEquals("My Logical Entity", returnedLogicalEntity.getValue("name"));
        assertEquals("true", returnedLogicalEntity.getValue("required"));
    }

    @Test
    public void testGetPinsUsedByChannel() {
        ArrayList pins = device.getPinsUsedByChannel("Channel 1");
        assertEquals(pins.size(), 0);
        pins = device.getPinsUsedByChannel("Channel 2");
        assertEquals(pins.size(), 3);
        for (int i = 0; i < 3; i++) {
            String value = "J1." + (i + 1);
            assertEquals(value, pins.get(i));
        }
    }
}
