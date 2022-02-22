package unitTest;

import org.junit.Assert;
import org.picmg.configurator.Device;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;



class DeviceTest {

    Device device;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // load the default hardware profile
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromResource("microsam_new2.json");

        device = new Device(hardware);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void testGetCapabilitiesFruRecordByName() {
        JsonObject fruRecord = device.getCapabilitiesFruRecordByName("test");
        Assert.assertEquals("412",fruRecord.getValue("vendorIANA"));
        JsonArray jsonArray = (JsonArray) fruRecord.get("fields");
        Assert.assertEquals(6, jsonArray.size());
    }
}