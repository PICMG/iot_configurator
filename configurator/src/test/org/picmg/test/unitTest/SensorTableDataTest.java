package org.picmg.test.unitTest;

import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.SensorsTabController;

import static org.junit.Assert.*;

public class SensorTableDataTest {

    SensorsTabController.SensorTableData sensorTableData;

    @Before
    public void setUp() {
        SensorsTabController sensorsTabController = new SensorsTabController();
        sensorTableData = sensorsTabController.getSensorTableData();
    }

    @Test
    public void testGetName() {
        assertNull(sensorTableData.getName());
    }

    @Test
    public void testSetName() {
        assertNull(sensorTableData.getName());
        sensorTableData.setName("New Name");
        assertEquals("New Name", sensorTableData.getName());
    }

    @Test
    public void testGetManufacturer() {
        assertNull(sensorTableData.getManufacturer());
    }

    @Test
    public void testSetManufacturer() {
        assertNull(sensorTableData.getManufacturer());
        sensorTableData.setManufacturer("New Manufacturer");
        assertEquals("New Manufacturer", sensorTableData.getManufacturer());
    }

    @Test
    public void testGetModel() {
        assertNull(sensorTableData.getModel());
    }

    @Test
    public void testSetModel() {
        assertNull(sensorTableData.getModel());
        sensorTableData.setModel("New Model");
        assertEquals("New Model", sensorTableData.getModel());
    }

    @Test
    public void testGetDescription() {
        assertNull(sensorTableData.getDescription());
    }

    @Test
    public void testSetDescription() {
        assertNull(sensorTableData.getDescription());
        sensorTableData.setDescription("New Description");
        assertEquals("New Description", sensorTableData.getDescription());
    }

    @Test
    public void testIsAnalog() {
        assertFalse(sensorTableData.isAnalog());
    }

    @Test
    public void testSetAnalog() {
        assertFalse(sensorTableData.isAnalog());
        sensorTableData.setAnalog(true);
        assertTrue(sensorTableData.isAnalog());
        sensorTableData.setAnalog(false);
        assertFalse(sensorTableData.isAnalog());
    }

    @Test
    public void testIsDigital() {
        assertFalse(sensorTableData.isDigital());
    }

    @Test
    public void testSetDigital() {
        assertFalse(sensorTableData.isDigital());
        sensorTableData.setDigital(true);
        assertTrue(sensorTableData.isDigital());
        sensorTableData.setDigital(false);
        assertFalse(sensorTableData.isDigital());
    }

    @Test
    public void testIsCount() {
        assertFalse(sensorTableData.isCount());
    }

    @Test
    public void testSetCount() {
        assertFalse(sensorTableData.isCount());
        sensorTableData.setCount(true);
        assertTrue(sensorTableData.isCount());
        sensorTableData.setCount(false);
        assertFalse(sensorTableData.isCount());
    }

    @Test
    public void testIsRate() {
        assertFalse(sensorTableData.isRate());
    }

    @Test
    public void testSetRate() {
        assertFalse(sensorTableData.isRate());
        sensorTableData.setRate(true);
        assertTrue(sensorTableData.isRate());
        sensorTableData.setRate(false);
        assertFalse(sensorTableData.isRate());
    }
}
