package org.picmg.configurator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
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
            clickReset(scene);
            System.out.println("Here is where the test would go");
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
        robot.mouseRelease(MouseButton.PRIMARY);
        System.out.println("Done With testing");
    }
    public void clickReset(Scene scene) {
        try {

            Window area = scene.getWindow();
            Node resetButton = scene.lookup("#resetMenu");
            Robot robot = new Robot();
            Point2D point = resetButton.localToScene(0, 0);
            System.out.println(scene.getX() + " " + scene.getY());
            robot.mouseMove(point.getX() + scene.getX() + area.getX()+10, point.getY() + scene.getY() + area.getY()+10);
            robot.mousePress(MouseButton.PRIMARY);
            robot.mouseRelease(MouseButton.PRIMARY);
            Node resetOk = area.getScene().lookup("#resetOk");
            Point2D okLocation = resetOk.localToScene(0, 0);
            robot.mouseMove(okLocation.getX(), okLocation.getY());
            robot.mousePress(MouseButton.PRIMARY);
            robot.mouseRelease(MouseButton.PRIMARY);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}