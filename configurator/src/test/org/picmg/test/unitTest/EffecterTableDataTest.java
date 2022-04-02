package org.picmg.test.unitTest;

import javafx.geometry.Point2D;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.EffectersTabController;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class EffecterTableDataTest {

    EffectersTabController.EffecterTableData effecterTableData;

    /**
     * Loads the default hardware profile for testing purposes
     */
    @Before
    public void setUp() {
        EffectersTabController effectersTabController = new EffectersTabController();
        effecterTableData = effectersTabController.getWorkingData();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetName() {
        assertNull(effecterTableData.getName());
    }

    @Test
    public void testGetType() {
        assertEquals("none_per_none_none_none",effecterTableData.getType());
    }

    @Test
    public void testSetName() {
        assertNull(effecterTableData.getName());
        effecterTableData.setName("New Name");
        assertEquals("New Name", effecterTableData.getName());
    }

    @Test
    public void testGetManufacturer() {
        assertNull(effecterTableData.getManufacturer());
    }

    @Test
    public void testSetManufacturer() {
        assertNull(effecterTableData.getManufacturer());
        effecterTableData.setManufacturer("New Manufacturer");
        assertEquals("New Manufacturer", effecterTableData.getManufacturer());
    }

    @Test
    public void testGetModel() {
        assertNull(effecterTableData.getModel());
    }


    @Test
    public void testSetModel() {
        assertNull(effecterTableData.getModel());
        effecterTableData.setModel("New Model");
        assertEquals("New Model", effecterTableData.getModel());
    }

    @Test
    public void testGetDescription() {
        assertNull(effecterTableData.getDescription());
    }

    @Test
    public void testSetDescription() {
        assertNull(effecterTableData.getDescription());
        effecterTableData.setDescription("New Description");
        assertEquals("New Description", effecterTableData.getDescription());
    }

    @Test
    public void testIsAnalog() {
        assertFalse(effecterTableData.isAnalog());
    }

    @Test
    public void testSetAnalog() {
        assertFalse(effecterTableData.isAnalog());
        effecterTableData.setAnalog(true);
        assertTrue(effecterTableData.isAnalog());
        effecterTableData.setAnalog(false);
        assertFalse(effecterTableData.isAnalog());
    }

    @Test
    public void testIsDigital() {
        assertFalse(effecterTableData.isDigital());
    }

    @Test
    public void testSetDigital() {
        assertFalse(effecterTableData.isDigital());
        effecterTableData.setDigital(true);
        assertTrue(effecterTableData.isDigital());
        effecterTableData.setDigital(false);
        assertFalse(effecterTableData.isDigital());
    }

   @Test
    public void testIsRate() {
        assertFalse(effecterTableData.isRate());
    }

    @Test
    public void testSetRate() {
        assertFalse(effecterTableData.isRate());
        effecterTableData.setRate(true);
        assertTrue(effecterTableData.isRate());
        effecterTableData.setRate(false);
        assertFalse(effecterTableData.isRate());
    }

    @Test
    public void testGetMaxSampleRate() {
        assertNull(effecterTableData.getMaxSampleRate());
    }

    @Test
    public void testSetMaxSampleRate() {
        assertNull(effecterTableData.getMaxSampleRate());
        effecterTableData.setMaxSampleRate("5.0");
        assertEquals("5.0", effecterTableData.getMaxSampleRate());
    }

    @Test
    public void testGetBaseUnit() {
        assertNull(effecterTableData.getBaseUnit());
    }

    @Test
    public void testSetBaseUnit() {
        assertNull(effecterTableData.getBaseUnit());
        effecterTableData.setBaseUnit("Ounces");
        assertEquals("Ounces", effecterTableData.getBaseUnit());
    }

    @Test
    public void testGetUnitModifier() {
        assertNull(effecterTableData.getUnitModifier());
    }

    @Test
    public void testSetUnitModifier() {
        assertNull(effecterTableData.getUnitModifier());
        effecterTableData.setUnitModifier("10");
        assertEquals("10", effecterTableData.getUnitModifier());
    }

    @Test
    public void testGetRateUnit() {
        assertNull(effecterTableData.getRateUnit());
    }

    @Test
    public void testSetRateUnit() {
        assertNull(effecterTableData.getRateUnit());
        effecterTableData.setRateUnit("Ounces");
        assertEquals("Ounces", effecterTableData.getRateUnit());
    }

    @Test
    public void testGetAuxUnit() {
        assertNull(effecterTableData.getAuxUnit());
    }

    @Test
    public void testSetAuxUnit() {
        assertNull(effecterTableData.getAuxUnit());
        effecterTableData.setAuxUnit("Ounces");
        assertEquals("Ounces", effecterTableData.getAuxUnit());
    }

    @Test
    public void testGetAuxModifier() {
        assertNull(effecterTableData.getAuxModifier());
    }

    @Test
    public void testSetAuxModifier() {
        assertNull(effecterTableData.getAuxModifier());
        effecterTableData.setAuxModifier("10");
        assertEquals("10", effecterTableData.getAuxModifier());
    }

    @Test
    public void testGetRel() {
        assertNull(effecterTableData.getRel());
    }

    @Test
    public void testSetRel() {
        assertNull(effecterTableData.getRel());
        effecterTableData.setRel("10");
        assertEquals("10", effecterTableData.getRel());
    }

    @Test
    public void testGetAuxRateUnit() {
        assertNull(effecterTableData.getAuxRateUnit());
    }

    @Test
    public void testSetAuxRateUnit() {
        assertNull(effecterTableData.getAuxRateUnit());
        effecterTableData.setAuxRateUnit("10");
        assertEquals("10", effecterTableData.getAuxRateUnit());
    }

    @Test
    public void testGetPlusAccuracy() {
        assertNull(effecterTableData.getPlusAccuracy());
    }

    @Test
    public void testSetPlusAccuracy() {
        assertNull(effecterTableData.getPlusAccuracy());
        effecterTableData.setPlusAccuracy("1");
        assertEquals("1", effecterTableData.getPlusAccuracy());
    }

    @Test
    public void testGetMinusAccuracy() {
        assertNull(effecterTableData.getMinusAccuracy());
    }

    @Test
    public void testSetMinusAccuracy() {
        assertNull(effecterTableData.getMinusAccuracy());
        effecterTableData.setMinusAccuracy("1");
        assertEquals("1", effecterTableData.getMinusAccuracy());
    }

    @Test
    public void testGetOutputUnits() {
        assertNull(effecterTableData.getOutputUnits());
    }

    @Test
    public void testSetOutputUnits() {
        assertNull(effecterTableData.getOutputUnits());
        effecterTableData.setOutputUnits("Ounces");
        assertEquals("Ounces", effecterTableData.getOutputUnits());
    }

    @Test
    public void testGetOutputCurve() {
        ArrayList<Point2D> emptyOutputCurve = new ArrayList<Point2D>();
        assertEquals(emptyOutputCurve, effecterTableData.getOutputCurve());
        assertTrue(effecterTableData.getOutputCurve().isEmpty());
    }

    @Test
    public void testGetSavePath() {
        assertNull(effecterTableData.getSavePath());
    }

    @Test
    public void testSaveToFile(){
        File defaultPath = (effecterTableData.getSavePath() != null)
                ? effecterTableData.getSavePath().toFile()
                : new File(System.getProperty("user.dir")+"/lib/effecters/" + effecterTableData.getName()+".json");
        effecterTableData.SaveToFile(defaultPath.toString());

    }


}
