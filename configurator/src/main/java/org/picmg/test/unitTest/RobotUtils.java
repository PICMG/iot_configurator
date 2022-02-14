package org.picmg.test.unitTest;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Window;

public class RobotUtils {


    public static boolean debug = false;
    private static Robot robot = new Robot();
    /**
     * This method clicks the component with the #sensorTab id on the scene passed in
     * @param scene
     */
    public static void clickSensors(Scene scene)
    {
        Window area = scene.getWindow();
        Node sensorTab = scene.lookup("#sensorTab");
        Point2D point = sensorTab.localToScene(0,0);
        if(debug)
            System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(point.getX()+scene.getX()+area.getX(),point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

    /**
     * ClickEffecters
     * This method would click the effecters tab using robot. Similar to the other click methods
     * @param scene - main scene being shown on the screen
     */
    public static void clickEffecters(Scene scene)
    {
        Window area = scene.getWindow();
        Node effecterTab = scene.lookup("#effectersTab");
        Point2D point = effecterTab.localToScene(0,0);
        if(debug)
            System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(point.getX()+scene.getX()+area.getX(),point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

}
