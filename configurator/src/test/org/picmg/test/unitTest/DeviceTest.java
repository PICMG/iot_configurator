package org.picmg.test.unitTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.Device;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import static org.junit.Assert.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeviceTest {

    Device device;

    @Before
    public void setUp() {
        // load the default hardware profile
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromResource("microsam_new2_test.json");
        device = new Device(hardware);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetJson() {
        JsonObject json = device.getJson();
        assertTrue(json != null);
        assertTrue(!json.isEmpty());
        assertNotNull(json.getValue("capabilities"));
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
        assertEquals("true", returnedLogicalEntity.getValue("required"));
    }

    @Test
    public void testGetConfiguredBindingValueFromKey() {
        assertNull(device.getConfiguredBindingValueFromKey("Not a Binding", ""));
        assertEquals("412", device.getConfiguredBindingValueFromKey("GlobalInterlockSensor", "stateSetVendorIANA"));
        assertEquals("96", device.getConfiguredBindingValueFromKey("GlobalInterlockSensor", "stateSet"));
    }

    @Test
    public void  testGetConfiguredBindingFromName(){
        assertNull(device.getConfiguredBindingFromName("Not a Binding"));
        JsonObject binding = device.getConfiguredBindingFromName("GlobalInterlockSensor");
        assertEquals("412", binding.getValue("stateSetVendorIANA"));
        assertEquals("96", binding.getValue("stateSet"));
    }

    @Test
    public void testGetBindingValueFromKey() {
        assertNull(device.getBindingValueFromKey("Not a Binding", "boundChannel"));
        assertNull(device.getBindingValueFromKey("GlobalInterlockSensor", "Not a Binding Key"));
        assertEquals("interlock_in", device.getBindingValueFromKey("GlobalInterlockSensor", "boundChannel"));
    }

    @Test
    public void testGetInterfaceTypeFromName() {
        assertNull(device.getInterfaceTypeFromName("Not a Channel Name"));
        assertEquals("digital_in", device.getInterfaceTypeFromName("digital_in1"));
        assertEquals("count_in", device.getInterfaceTypeFromName("count_in2"));
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

    @Test
    public void testAddRecordConfigurationByName()
    {
        // Check for null
        JsonAbstractValue fru = device.addFruRecordConfigurationByName("temp");
        assertNull(fru);
        fru = device.addFruRecordConfigurationByName("test");
        assertNotNull(fru);
        assertEquals("412", fru.getValue("vendorIANA"));
        assertEquals("true", fru.getValue("required"));
        assertEquals("test", fru.getValue("name"));
        assertEquals(null, fru.getValue("description"));
    }
}