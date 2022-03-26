package org.picmg.test.unitTest;

import org.junit.Before;
import org.junit.Test;
import org.picmg.configurator.SensorsTabController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
