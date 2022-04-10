package org.picmg.test.integrationTest;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.picmg.configurator.App;

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
            // here is where tests go
//            evalTest();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void evalTest() {
        new RobotThread(500, () -> RobotUtils.clickEffecters())
//                .then(500,  () -> RobotUtils.click("#stepCheckbox"))
//                .then(50,   () -> RobotUtils.check("#stepCheckbox", "true"))
//                .then(20,   () -> RobotUtils.click("#stepCheckbox"))
//                .then(50,   () -> RobotUtils.check("#stepCheckbox", "false"))
//                .then(400,  () -> RobotUtils.click("#descriptionTextArea"))
//                .then(50,   () -> RobotUtils.type("auto desc"))
//                .then(400,  () -> RobotUtils.check("#descriptionTextArea", "auto desc"))
//                .then(() -> RobotUtils.click("#manufacturerTextfield"))
//                .then(20,   () -> RobotUtils.type("manufacturer 1"))
//                .then(20,   () -> RobotUtils.check("#manufacturerTextfield", "manufacturer 1"))
//                .wait(5000)
//                .then(RobotUtils::close)
                .run();
    }

    public void clicks() {
        RobotUtils.clickSensors();
        RobotUtils.clickEffecters();
        RobotUtils.clickDevice();
        RobotUtils.clickReset();
        //RobotUtils.close();
    }

}