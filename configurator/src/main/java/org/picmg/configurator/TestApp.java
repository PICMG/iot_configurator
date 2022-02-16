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
            clickEffecters(scene);
            System.out.println("Here is where the test would go");
            close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void clickSensors(Scene scene)
    {
        Window area = scene.getWindow();
        Node sensorTab = scene.lookup("#sensorTab");
        Robot robot = new Robot();
        Point2D point = sensorTab.localToScene(0,0);
        System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(point.getX()+scene.getX()+area.getX(),point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
        System.out.println("Done With testing");
    }

    /**
     * ClickEffecters
     * This method would click the effecters tab using robot. Similar to the other click methods
     * @param scene - main scene being shown on the screen
     */
    public void clickEffecters(Scene scene)
    {
        Window area = scene.getWindow();
        Node effecterTab = scene.lookup("#effectersTab");
        Robot robot = new Robot();
        Point2D point = effecterTab.localToScene(0,0);
        System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(point.getX()+scene.getX()+area.getX(),point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
    }
}
