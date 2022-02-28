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