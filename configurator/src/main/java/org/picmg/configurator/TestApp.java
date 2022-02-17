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
    public void clickSensors(Scene scene)
    {
        Window area = scene.getWindow();
        Node sensorTab = scene.lookup("#sensorTab");
        Robot robot = new Robot();
        Point2D point = sensorTab.localToScene(0,0);
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
            robot.mouseMove(point.getX() + scene.getX() + area.getX()+10, point.getY() + scene.getY() + area.getY()+10);
            robot.mouseClick(MouseButton.PRIMARY);
            Thread thread = new Thread(()->{
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    List<Window> temp = Stage.getWindows().stream().collect(Collectors.toList());
                    Window resetOk = temp.get(1);
                    Node ok = resetOk.getScene().lookup("#resetOk");
                    Point2D okArea = ok.localToScene(0,0);robot.mouseMove((double)(okArea.getX() + resetOk.getX() + resetOk.getScene().getX()), (double)( okArea.getY()+ resetOk.getY() + resetOk.getScene().getY()));
                    robot.mousePress(MouseButton.PRIMARY);
                    robot.mouseRelease(MouseButton.PRIMARY);

                });

            });
            thread.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}