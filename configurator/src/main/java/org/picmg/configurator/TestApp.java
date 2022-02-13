package org.picmg.configurator;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.picmg.unitTest.RobotUtils;

import java.io.IOException;

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
            clicks(scene);
            System.out.println("Here is where the test would go");
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    public void clicks(Scene scene) throws InterruptedException {
        RobotUtils.clickSensors(scene);
        RobotUtils.clickEffecters(scene);
    }
}
