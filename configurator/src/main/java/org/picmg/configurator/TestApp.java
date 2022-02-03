package org.picmg.configurator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

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
            clickSensors();
            System.out.println("Here is where the test would go");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void clickSensors()
    {
        Robot robot = new Robot();
        robot.mouseMove(600,150);
        robot.mousePress(MouseButton.PRIMARY);
        System.out.println("Done With testing");
    }

}
