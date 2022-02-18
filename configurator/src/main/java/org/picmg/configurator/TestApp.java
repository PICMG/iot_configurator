package org.picmg.configurator;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeView;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.picmg.test.unitTest.RobotUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestApp extends App{

    @Override
    public void start(Stage stage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
            Scene scene = new Scene(root, 1024, 768);
            stage.setTitle("PICMG Configurator");
            stage.setScene(scene);
            stage.show();
            System.out.println("Here is where the test would go");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void clicks(Scene scene) throws InterruptedException {
        RobotUtils.clickReset(scene);
        RobotUtils.clickSensors(scene);
        RobotUtils.clickEffecters(scene);
        RobotUtils.close();
    }
}