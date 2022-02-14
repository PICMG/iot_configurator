package org.picmg.configurator;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.picmg.test.unitTest.RobotUtils;

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

    public void clicks(Scene scene) throws InterruptedException {
        RobotUtils.clickSensors(scene);
        RobotUtils.clickEffecters(scene);
        RobotUtils.close();
    }
    public void clickReset(Scene scene)
    {
        Window area = scene.getWindow();
        Node effecterTab = scene.lookup("#resetMenu");
        Robot robot = new Robot();
        Point2D point = effecterTab.localToScene(0,0);
        System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(point.getX()+scene.getX()+area.getX(),point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
    }
    public void addSimple(Scene scene){
        try {
            Window area = scene.getWindow();
            Node sensorTab = scene.lookup("#logicalEntity");
            TreeView sensorTab1 = (TreeView) sensorTab;
            ObservableList<Node> list= sensorTab1.getChildrenUnmodifiable();
            Robot robot = new Robot();
            System.out.println(scene.getX() + " " + scene.getY());
            try {
                list.forEach((node)->{
                    System.out.println(node.localToScene(0, 0).toString());
                });
                Point2D point = sensorTab1.localToScene(0, 0);
                robot.mouseMove(point.getX() + scene.getX() + area.getX(), point.getY() + scene.getY() + area.getY());
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.mousePress(MouseButton.PRIMARY);
            System.out.println("Done With testing");
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
}