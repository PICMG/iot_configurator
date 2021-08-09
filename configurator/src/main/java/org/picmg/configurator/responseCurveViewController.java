//*******************************************************************
//    responseCurveViewController.java
//
//    More information on the PICMG IoT data model can be found within
//    the PICMG family of IoT specifications.  For more information,
//    please visit the PICMG web site (www.picmg.org)
//
//    Copyright (C) 2020,  PICMG
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
package org.picmg.configurator;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class responseCurveViewController {
    @FXML private LineChart<Double, Double> responseChart;

    public void setDataPoints(ArrayList<Point2D> points) {
        responseChart.setCreateSymbols(false);
        responseChart.setAnimated(false);
        responseChart.getData().clear();
        // add data to the chart
        XYChart.Series<Double,Double> series = new XYChart.Series<>();
        for (Point2D p:points) {
            XYChart.Data<Double,Double> datapoint = new XYChart.Data<>(p.getX(),p.getY());
            series.getData().add(datapoint);
        }
        responseChart.getData().setAll(series);
    }
}
