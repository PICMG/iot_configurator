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
            typeTest();
            System.out.println("Here is where the test would go");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void typeTest() {
        RobotUtils.runLater(1000,
                () -> RobotUtils.clickEffecters(),
                () -> RobotUtils.click("#manufacturerTextfield"),
                () -> RobotUtils.type("This is Sample Text!!"),
                () -> RobotUtils.click("#partNumberTextField"),
                () -> RobotUtils.type("MORE Sample Text..."),
                RobotUtils::close
       );
    }

    public void clicks() {
        RobotUtils.clickSensors();
        RobotUtils.clickEffecters();
        RobotUtils.clickDevice();
        RobotUtils.clickReset();
        //RobotUtils.close();
    }

}