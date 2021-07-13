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
