package org.picmg.test.unitTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.Device;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

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
}