package org.picmg.test.unitTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.Device;
import org.picmg.jsonreader.*;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;


public class DeviceTest {

    Device device;

    /**
     * Loads the default hardware profile for testing purposes
     */
    @Before
    public void setUp() {
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromResource("microsam_new2_test.json");
        device = new Device(hardware);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConstructor() {
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromResource("microsam_new2_test.json");
        Device testDevice = new Device(hardware);

        assertEquals("412", testDevice.getCapabilitiesFruRecordByName("test").getValue("vendorIANA"));
        assertEquals("simple1", testDevice.getLogicalEntityCapabilityByName("simple1").getValue("name"));
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
    public void testGetConfiguredBindingFromName() {
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
    public void testFruAddRecordConfigurationByName() {
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

    @Test
    public void testSetConfiguredBindingFromKey() {
        device.setConfiguredBindingValueFromKey("GlobalInterlockSensor", "stateSetVendorIANA", "422");
        JsonObject binding = device.getConfiguredBindingFromName("GlobalInterlockSensor");
        assertNotEquals("412", binding.getValue("stateSetVendorIANA"));
        assertEquals("422", binding.getValue("stateSetVendorIANA"));
        // changing the configuration back to original value
        device.setConfiguredBindingValueFromKey("GlobalInterlockSensor", "stateSetVendorIANA", "412");
        assertEquals("412", binding.getValue("stateSetVendorIANA"));
    }

    @Test
    public void testGetCapabilitiesBindingFromName() {
        assertNull(device.getCapabilitiesBindingFromName("simple1", "Not a Binding"));
        assertNull(device.getCapabilitiesBindingFromName("Not a Logical Entity", "GlobalInterlockSensor"));

        JsonObject binding = device.getCapabilitiesBindingFromName("simple1", "GlobalInterlockSensor");
        assertEquals("stateSensor", binding.getValue("bindingType"));
        assertEquals("GlobalInterlockSensor", binding.getValue("name"));
        assertEquals("true", binding.getValue("includeInPdr"));
        assertEquals("true", binding.getValue("required"));
        assertEquals("false", binding.getValue("isVirtual"));
        assertEquals("Global interlock input binding", binding.getValue("description"));
        assertEquals("interlock_in", binding.getValue("boundChannel"));
        assertEquals("412", binding.getValue("stateSetVendorIANA"));
        assertEquals("96", binding.getValue("stateSet"));
        assertEquals("1", binding.getValue("stateWhenLow"));
        assertEquals("2", binding.getValue("stateWhenHigh"));
        assertEquals("6", binding.getValue("usedStates"));
    }

    @Test
    public void testAddLogicalEntityConfigurationByName() {
        JsonAbstractValue newEntity = device.addLogicalEntityConfigurationByName("simple1");
        JsonObject hardware = device.getJson();
        JsonObject cfg = (JsonObject) hardware.get("configuration");
        JsonArray cfgEntities = (JsonArray) cfg.get("logicalEntities");
        boolean isExists = false;
        JsonObject edef = null;
        for (JsonAbstractValue logicalEntity : cfgEntities) {
            edef = (JsonObject) logicalEntity;
            if (edef.getValue("name").equals("simple1")) {
                isExists = true;
            }
        }
        assertTrue(isExists);
        JsonAbstractValue newEntity1 = device.addLogicalEntityConfigurationByName("simple");
        assertNull(newEntity1);
        JsonArray bindings = (JsonArray) edef.get("ioBindings");
        JsonArray result = new JsonArray();
        JsonObject point1 = new JsonObject();
        JsonObject point2 = new JsonObject();
        point1.put("in", new JsonValue("0"));
        point1.put("out", new JsonValue("0"));
        point2.put("in", new JsonValue("1000"));
        point2.put("out", new JsonValue("1000"));
        result.add(0, point1);
        result.add(1, point2);
        for (JsonAbstractValue val : bindings) {
            JsonObject binding = (JsonObject) val;
            // if the binding has an input curve that is null, set it to a default
            // linear response.
            if ((binding.containsKey("inputCurve"))) {
                JsonArray inputCurveActual = (JsonArray) binding.get("inputCurve");
                assertTrue(inputCurveActual.containsAny(result));
            }
            if ((binding.containsKey("outputCurve"))) {
                JsonArray outputCurveActual = (JsonArray) binding.get("outputCurve");
                assertTrue(outputCurveActual.containsAny(result));
            }
        }
    }

    @Test
    public void testRemoveLogicalEntityConfigurationByName() {
        assertNotNull(device.getLogicalEntityConfigurationByName("simple1"));
        device.removeLogicalEntityConfigurationByName("simple1");
        assertNull(device.getLogicalEntityConfigurationByName("simple1"));

        assertNull(device.getLogicalEntityConfigurationByName("notALogicalEntity"));
        device.removeLogicalEntityConfigurationByName("notALogicalEntity");
        assertNull(device.getLogicalEntityConfigurationByName("notALogicalEntity"));
    }

    @Test
    public void testGetLogicalEntityConfigurationByName() {
        assertNotNull(device.getLogicalEntityConfigurationByName("simple1"));
        assertEquals("PICMG Simple Sensor/Effecter", device.getLogicalEntityConfigurationByName("simple1").getValue("description"));
        assertNull(device.getLogicalEntityConfigurationByName("notALogicalEntity"));
    }

    @Test
    public void testIsConfigurationBindingFieldEditable() {
        assertFalse(device.isConfigurationBindingFieldEditable("notABindingName", "notAField"));
        assertTrue(device.isConfigurationBindingFieldEditable("GlobalInterlockSensor", "notAField"));
    }

    @Test
    public void testCanEntityBeAdded() {
        assertFalse(device.canEntityBeAdded("notAnEntity"));
        assertFalse(device.canEntityBeAdded("simple1"));
        assertFalse(device.canEntityBeAdded("pid1"));
    }

    @Test
    public void testGetPossibleChannelsForBinding() {
        JsonObject binding = new JsonObject();
        binding.put("bindingType", new JsonValue("stateSensor"));
        binding.put("boundChannel", new JsonValue("interlock_in"));

        JsonArray interfaceTypes = new JsonArray();
        interfaceTypes.add(new JsonValue("digital_in"));

        binding.put("allowedInterfaceTypes", interfaceTypes);

        ArrayList<String> channels = device.getPossibleChannelsForBinding(binding);
        assertEquals(channels.get(0), "interlock_in");
        assertEquals(channels.get(1), "digital_in1");
        assertEquals(channels.get(2), "digital_in2");
        assertEquals(channels.get(3), "digital_in3");
        assertEquals(channels.get(4), "digital_in4");
        assertEquals(channels.get(5), "digital_in5");
    }

    @Test
    public void testGetPossibleChannelsTypesForBinding() {
        JsonObject binding = new JsonObject();
        binding.put("bindingType", new JsonValue("stateSensor"));
        binding.put("boundChannel", new JsonValue("interlock_in"));

        JsonArray interfaceTypes = new JsonArray();
        interfaceTypes.add(new JsonValue("digital_in"));

        binding.put("allowedInterfaceTypes", interfaceTypes);

        ArrayList<String> usedPins = new ArrayList<>();
        ArrayList<String> channelTypes = device.getPossibleChannelsTypesForBinding(binding, usedPins);
        assertEquals(channelTypes.get(0), "digital_in");
        assertEquals(channelTypes.get(1), "digital_in");
        assertEquals(channelTypes.get(2), "digital_in");
        assertEquals(channelTypes.get(3), "digital_in");
        assertEquals(channelTypes.get(4), "digital_in");
        assertEquals(channelTypes.get(5), "digital_in");
    }

    @Test
    public void testRemoveChannelBinding() {
        JsonObject virtualIOBinding = new JsonObject();
        virtualIOBinding.put("isVirtual", new JsonValue("true"));
        assertFalse(device.removeChannelBinding(virtualIOBinding));

        JsonObject unboundIOBinding = new JsonObject();
        unboundIOBinding.put("isVirtual", new JsonValue("false"));
        assertTrue(device.removeChannelBinding(unboundIOBinding));

        JsonObject boundIOBinding = new JsonObject();
        boundIOBinding.put("isVirtual", new JsonValue("false"));
        boundIOBinding.put("boundChannel", new JsonValue("interlock_in"));
        assertTrue(device.removeChannelBinding(boundIOBinding));
    }

    @Test
    public void testSetChannelBinding() {
        JsonObject virtualIOBinding = new JsonObject();
        virtualIOBinding.put("isVirtual", new JsonValue("true"));
        assertFalse(device.setChannelBinding(virtualIOBinding, ""));

        JsonObject unboundIOBinding = new JsonObject();
        unboundIOBinding.put("isVirtual", new JsonValue("false"));
        assertFalse(device.setChannelBinding(unboundIOBinding, "interlock_in"));

        JsonObject boundIOBinding = new JsonObject();
        boundIOBinding.put("isVirtual", new JsonValue("false"));
        boundIOBinding.put("boundChannel", new JsonValue("interlock_in"));
        assertTrue(device.setChannelBinding(boundIOBinding, "interlock_in"));
    }

    @Test
    public void testRestoreBindingToDefaults(){
        JsonAbstractValue newEntity = device.restoreBindingToDefaults("GlobalInterlockSensor");
        JsonObject hardware = device.getJson();
        JsonObject cfg = (JsonObject) hardware.get("configuration");
        JsonArray cfgEntities = (JsonArray) cfg.get("logicalEntities");
        boolean isExists = false;
        JsonObject edef = null;
        for (JsonAbstractValue logicalEntity : cfgEntities) {
            edef = (JsonObject) logicalEntity;
            if (edef.getValue("name").equals("simple1")) {
                isExists = true;
            }
        }
        assertTrue(isExists);
        JsonAbstractValue newEntity1 = device.addLogicalEntityConfigurationByName("simple");
        assertNull(newEntity1);
        JsonArray bindings = (JsonArray) edef.get("ioBindings");
        JsonArray result = new JsonArray();
        JsonObject point1 = new JsonObject();
        JsonObject point2 = new JsonObject();
        point1.put("in", new JsonValue("0"));
        point1.put("out", new JsonValue("0"));
        point2.put("in", new JsonValue("1000"));
        point2.put("out", new JsonValue("1000"));
        result.add(0, point1);
        result.add(1, point2);
        for (JsonAbstractValue val : bindings) {
            JsonObject binding = (JsonObject) val;
            // if the binding has an input curve that is null, set it to a default
            // linear response.
            if ((binding.containsKey("inputCurve"))) {
                JsonArray inputCurveActual = (JsonArray) binding.get("inputCurve");
                assertTrue(inputCurveActual.containsAny(result));
            }
            if ((binding.containsKey("outputCurve"))) {
                JsonArray outputCurveActual = (JsonArray) binding.get("outputCurve");
                assertTrue(outputCurveActual.containsAny(result));
            }
        }
    }

    @Test
    public void testSetSensorFromFile(){
        device.setSensorFromFile("GlobalInterlockSensor","Calt_DYLY_103_Ounces");
        JsonObject binding = device.getConfiguredBindingFromName("GlobalInterlockSensor");
        assertEquals("41", binding.get("sensor").getValue("baseUnit"));
        assertEquals("0", binding.get("sensor").getValue("auxUnit"));
        assertEquals("Pull Pressure Force S-type Load Cell Sensor with Cable 10KG ", binding.get("sensor").getValue("description"));
        assertEquals("Calt_DYLY_103_Ounces", binding.get("sensor").getValue("name"));
        assertEquals("Amps", binding.get("sensor").getValue("outputUnits"));
    }

    @Test
    public void testExportConfiguration(){
        //exprot the file
        File outputFile  = new File("exportConfigurationTestOutput.json");
        device.exportConfiguration(outputFile);
        //read the exported file
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromFile(Path.of("exportConfigurationTestOutput.json"));
        Device deviceCopy = new Device(hardware);

        JsonObject capabilitiesFruRecordByName = device.getCapabilitiesFruRecordByName("test");
        JsonObject copyCapabilitiesFruRecordByName = deviceCopy.getCapabilitiesFruRecordByName("test");
        assertEquals(capabilitiesFruRecordByName.getValue("vendorIANA"), copyCapabilitiesFruRecordByName.getValue("vendorIANA"));

        JsonObject returnedLogicalEntity = device.getLogicalEntityCapabilityByName("simple1");
        JsonObject copyReturnedLogicalEntity = deviceCopy.getLogicalEntityCapabilityByName("simple1");
        assertEquals(returnedLogicalEntity.getValue("entityVendorIANA"), copyReturnedLogicalEntity.getValue("entityVendorIANA"));

        JsonObject binding = device.getConfiguredBindingFromName("GlobalInterlockSensor");
        JsonObject copyBinding = deviceCopy.getConfiguredBindingFromName("GlobalInterlockSensor");
        assertEquals(binding.getValue("stateSet"), copyBinding.getValue("stateSet"));

    }
}