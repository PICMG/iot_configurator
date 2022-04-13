package org.picmg.test.unitTest;

import org.junit.Test;
import org.picmg.configurator.StateSet;
import org.picmg.jsonreader.JsonObject;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class StateSetTest {

    StateSet stateSet;

    @Test
    public void testGetName() {
        stateSet = new StateSet("My State Set", "My Vendor");
        assertEquals("My State Set", stateSet.getName());
    }

    @Test
    public void testGetVendor() {
        stateSet = new StateSet("My State Set", "My Vendor");
        assertEquals("My Vendor", stateSet.getVendorName());
    }

    @Test
    public void testSetName() {
        stateSet = new StateSet("My State Set", "My Vendor");
        stateSet.setName("My New State Set");
        assertEquals("My New State Set", stateSet.getName());
    }

    @Test
    public void testSetVendor() {
        stateSet = new StateSet("My State Set", "My Vendor");
        stateSet.setVendorName("My New Vendor");
        assertEquals("My New Vendor", stateSet.getVendorName());
    }

    @Test
    public void testToJSON() {
        stateSet = new StateSet("My State Set", 123,"My Vendor", 678, new HashMap<>());
        JsonObject json = stateSet.toJSON();

        assertEquals("My State Set", json.getValue("name"));
        assertEquals("My Vendor", json.getValue("vendorName"));
        assertEquals(123, json.getInteger("stateSetID"));
        assertEquals(678, json.getInteger("vendorIANA"));
    }
}
