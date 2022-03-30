package org.picmg.test.unitTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.EffectersTabController;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

}
