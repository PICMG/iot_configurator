package org.picmg.test.unitTest;

import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.SensorsTabController;

import java.util.ArrayList;

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

    @Test
    public void testIsQuadrature() {
        assertFalse(sensorTableData.isQuadrature());
    }

    @Test
    public void testSetQuadrature() {
        assertFalse(sensorTableData.isQuadrature());
        sensorTableData.setQuadrature(true);
        assertTrue(sensorTableData.isQuadrature());
        sensorTableData.setQuadrature(false);
        assertFalse(sensorTableData.isQuadrature());
    }

    @Test
    public void testGetMaxSampleRate() {
        assertNull(sensorTableData.getMaxSampleRate());
    }

    @Test
    public void testSetMaxSampleRate() {
        assertNull(sensorTableData.getMaxSampleRate());
        sensorTableData.setMaxSampleRate("5.0");
        assertEquals("5.0", sensorTableData.getMaxSampleRate());
    }

    @Test
    public void testGetBaseUnit() {
        assertNull(sensorTableData.getBaseUnit());
    }

    @Test
    public void testSetBaseUnit() {
        assertNull(sensorTableData.getBaseUnit());
        sensorTableData.setBaseUnit("Ounces");
        assertEquals("Ounces", sensorTableData.getBaseUnit());
    }

    @Test
    public void testGetUnitModifier() {
        assertNull(sensorTableData.getUnitModifier());
    }

    @Test
    public void testSetUnitModifier() {
        assertNull(sensorTableData.getUnitModifier());
        sensorTableData.setUnitModifier("10");
        assertEquals("10", sensorTableData.getUnitModifier());
    }

    @Test
    public void testGetRateUnit() {
        assertNull(sensorTableData.getRateUnit());
    }

    @Test
    public void testSetRateUnit() {
        assertNull(sensorTableData.getRateUnit());
        sensorTableData.setRateUnit("Ounces");
        assertEquals("Ounces", sensorTableData.getRateUnit());
    }

    @Test
    public void testGetAuxUnit() {
        assertNull(sensorTableData.getAuxUnit());
    }

    @Test
    public void testSetAuxUnit() {
        assertNull(sensorTableData.getAuxUnit());
        sensorTableData.setAuxUnit("Ounces");
        assertEquals("Ounces", sensorTableData.getAuxUnit());
    }

    @Test
    public void testGetAuxModifier() {
        assertNull(sensorTableData.getAuxModifier());
    }

    @Test
    public void testSetAuxModifier() {
        assertNull(sensorTableData.getAuxModifier());
        sensorTableData.setAuxModifier("10");
        assertEquals("10", sensorTableData.getAuxModifier());
    }

    @Test
    public void testGetRel() {
        assertNull(sensorTableData.getRel());
    }

    @Test
    public void testSetRel() {
        assertNull(sensorTableData.getRel());
        sensorTableData.setRel("10");
        assertEquals("10", sensorTableData.getRel());
    }

    @Test
    public void testGetAuxRateUnit() {
        assertNull(sensorTableData.getAuxRateUnit());
    }

    @Test
    public void testSetAuxRateUnit() {
        assertNull(sensorTableData.getAuxRateUnit());
        sensorTableData.setAuxRateUnit("10");
        assertEquals("10", sensorTableData.getAuxRateUnit());
    }

    @Test
    public void testGetPlusAccuracy() {
        assertNull(sensorTableData.getPlusAccuracy());
    }

    @Test
    public void testSetPlusAccuracy() {
        assertNull(sensorTableData.getPlusAccuracy());
        sensorTableData.setPlusAccuracy("1");
        assertEquals("1", sensorTableData.getPlusAccuracy());
    }

    @Test
    public void testGetMinusAccuracy() {
        assertNull(sensorTableData.getMinusAccuracy());
    }

    @Test
    public void testSetMinusAccuracy() {
        assertNull(sensorTableData.getMinusAccuracy());
        sensorTableData.setMinusAccuracy("1");
        assertEquals("1", sensorTableData.getMinusAccuracy());
    }

    @Test
    public void testGetOutputUnits() {
        assertNull(sensorTableData.getOutputUnits());
    }

    @Test
    public void testSetOutputUnits() {
        assertNull(sensorTableData.getOutputUnits());
        sensorTableData.setOutputUnits("Ounces");
        assertEquals("Ounces", sensorTableData.getOutputUnits());
    }

    @Test
    public void testGetOutputCurve() {
        ArrayList<Point2D> emptyOutputCurve = new ArrayList<Point2D>();
        assertEquals(emptyOutputCurve, sensorTableData.getOutputCurve());
        assertTrue(sensorTableData.getOutputCurve().isEmpty());
    }

    @Test
    public void testGetSavePath() {
        assertNull(sensorTableData.getSavePath());
    }
}
