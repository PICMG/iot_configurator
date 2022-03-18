package org.picmg.test.integrationTest;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.picmg.configurator.App;
import org.picmg.test.integrationTest.RobotUtils;

import java.io.IOException;


import java.io.IOException;

public class TestApp extends App {

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
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void clicks(Scene scene) {
        RobotUtils.clickSensors(scene);
        RobotUtils.clickEffecters(scene);
        RobotUtils.clickDevice(scene);
        RobotUtils.clickReset(scene);
        //RobotUtils.close();
    }

}