package org.picmg.test.unitTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.Device;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeviceTest {

    Device device;

    @Before
    public void setUp() {
        // load the default hardware profile
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromResource("microsam_new2.json");

        device = new Device(hardware);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetCapabilitiesFruRecordByName() {
        assertNull(device.getCapabilitiesFruRecordByName("Not a Capability"));

        JsonObject fruRecord = device.getCapabilitiesFruRecordByName("test");
        assertEquals("412", fruRecord.getValue("vendorIANA"));
        JsonArray jsonArray = (JsonArray) fruRecord.get("fields");
        assertEquals(6, jsonArray.size());
    }

    @Test
    public void testGetLogicalEntityCapabilityByName() {
        assertNull(device.getLogicalEntityCapabilityByName("Not a Logical Entity"));

        JsonObject returnedLogicalEntity = device.getLogicalEntityCapabilityByName("simple1");
        assertEquals("simple1", returnedLogicalEntity.getValue("name"));
        assertEquals("false", returnedLogicalEntity.getValue("required"));
    }

    @Test
    public void testGetConfiguredBindingValueFromKey() {
        assertNull(device.getConfiguredBindingValueFromKey("Not a Binding", ""));
    }

    @Test
    public void testGetPinsUsedByChannel() {
        ArrayList pins = device.getPinsUsedByChannel("digital_in3");
        assertEquals(pins.size(), 1);
        pins = device.getPinsUsedByChannel("step_dir_out1");
        assertEquals(pins.size(), 3);
        for (int i = 0; i < 3; i++) {
            String value = "J1." + (i + 6);
            assertEquals(value, pins.get(i));
        }
        pins = device.getPinsUsedByChannel("adigital_in3");
        assertEquals(pins.size(), 0);
    }
}