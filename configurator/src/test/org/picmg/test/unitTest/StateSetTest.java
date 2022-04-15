package org.picmg.test.unitTest;

import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.OEMStateValueRecord;
import org.picmg.configurator.StateSet;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class StateSetTest {

    StateSet stateSet;

    @Before
    public void setUp() {
        JsonResultFactory factory = new JsonResultFactory();
        JsonObject hardware = (JsonObject) factory.buildFromResource("state_sets/DMTF_1.json");
        stateSet = new StateSet(hardware);
    }

    @Test
    public void testGetName() {
        assertEquals("Health State", stateSet.getName());
    }

    @Test
    public void testGetVendorName() {
        assertEquals("DMTF", stateSet.getVendorName());
    }

    @Test
    public void testGetStateSetId() {
        assertEquals(1, stateSet.getStateSetID());
    }

    @Test
    public void testGetVendorIANA() {
        assertEquals(412, stateSet.getVendorIANA());
    }

    @Test
    public void testGetOemStateValueRecords() {
        ArrayList<OEMStateValueRecord> oemStateValueRecords = stateSet.getOemStateValueRecords();

        OEMStateValueRecord record0 = oemStateValueRecords.get(0);
        assertEquals(1, record0.getMinStateValue());
        assertEquals(1, record0.getMaxStateValue());
        assertEquals("en", record0.getLanguageTags().get(0));
        assertEquals(1, record0.getLanguageTags().size());
        assertEquals("Normal", record0.getStateName().get(0));
        assertEquals(1, record0.getStateName().size());

        OEMStateValueRecord record1 = oemStateValueRecords.get(1);
        assertEquals(2, record1.getMinStateValue());
        assertEquals(2, record1.getMaxStateValue());
        assertEquals("en", record1.getLanguageTags().get(0));
        assertEquals(1, record1.getLanguageTags().size());
        assertEquals("Non-Critical", record1.getStateName().get(0));
        assertEquals(1, record1.getStateName().size());
    }

    @Test
    public void testSetName() {
        stateSet.setName("My New State Set");
        assertEquals("My New State Set", stateSet.getName());
    }

    @Test
    public void testSetVendor() {
        stateSet.setVendorName("My New Vendor");
        assertEquals("My New Vendor", stateSet.getVendorName());
    }

    @Test
    public void testToJSON() {
        JsonObject json = stateSet.toJSON();

        assertEquals("Health State", json.getValue("name"));
        assertEquals("DMTF", json.getValue("vendorName"));
        assertEquals(1, json.getInteger("stateSetId"));
        assertEquals(412, json.getInteger("vendorIANA"));

        JsonArray oemStateValueRecords = (JsonArray) json.get("oemStateValueRecords");

        assertEquals(1, oemStateValueRecords.get(0).getInteger("minStateValue"));
        assertEquals(1, oemStateValueRecords.get(0).getInteger("maxStateValue"));
        assertEquals("en", ((JsonArray) ((JsonObject) oemStateValueRecords.get(0)).get("languageTags")).get(0).getValue(""));
        assertEquals("Normal", ((JsonArray) ((JsonObject) oemStateValueRecords.get(0)).get("stateName")).get(0).getValue(""));

        assertEquals(2, oemStateValueRecords.get(1).getInteger("minStateValue"));
        assertEquals(2, oemStateValueRecords.get(1).getInteger("maxStateValue"));
        assertEquals("en", ((JsonArray) ((JsonObject) oemStateValueRecords.get(1)).get("languageTags")).get(0).getValue(""));
        assertEquals("Non-Critical", ((JsonArray) ((JsonObject) oemStateValueRecords.get(1)).get("stateName")).get(0).getValue(""));
    }
}
