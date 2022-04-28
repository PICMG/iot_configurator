package org.picmg.test.integrationTest;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RobotUtils {
    private static Robot robot = new Robot();
    public static final boolean debug = false;
    private static final int OFFSET = 10;

    /**
     * Click and open the sensor tab
     */
    public static void clickSensors() {
        click("#sensorTab");
    }

    /**
     * Click and open the effecters tab
     */
    public static void clickEffecters() {
        click("#effectersTab");
    }

    /**
     * Click and open the device configuration tab
     */
    public static void clickDevice() {
        click("#deviceTab");
    }

    /**
     * Manually call reset from menu option
     */
    public static void clickReset() {
        try {
            click("#resetMenu");
            new RobotThread(1000, ()->click("#resetOk")).run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Find a node given in any scene given its FXML ID. If multiple results are found, this returns the component in
     * the higher indexed stage.
     * @param value FXID of desired node
     * @return Optional of result node if any is found. Otherwise, returns an empty optional.
     */
    public static Optional<Node> lookup(String value) {
        List<Scene> scenes = Stage.getWindows().stream().map(Window::getScene).toList();
        Node foundNode = null;
        for (Scene scene : scenes) {
            Node newNode = scene.lookup(value);
            if (newNode != null) foundNode = newNode;
        }
        if (debug) System.out.println("Node=" + foundNode);
        return Optional.ofNullable(foundNode);
    }


    public static void click(String value) {
        System.out.println("Click");
        Optional<Node> lookup = lookup(value);
        if (lookup.isEmpty()) {
            System.out.println("Error: Unable to locate FXID " + value + " in any existing stage.");
            return;
        }
        Node node = lookup.get();
        Scene scene = node.getScene();
        Window window = scene.getWindow();
        Point2D point = node.localToScene(0,0);
        if (debug) System.out.println(OFFSET+point.getX()+scene.getX()+window.getX() + "\t\t" + OFFSET+point.getY()+scene.getY()+window.getY());
        robot.mouseMove(OFFSET+point.getX()+scene.getX()+window.getX(),OFFSET+point.getY()+scene.getY()+window.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }


    
    public static void type(String message) {
        for (char c : message.toCharArray()) {
            String key = KeyEvent.getKeyText(KeyEvent.getExtendedKeyCodeForChar(c));
            KeyCode keyCode = KeyCode.getKeyCode(key);
            if (debug) {
                System.out.println("Trying to convert " + c + " to " + key + " as " + keyCode);
            }
            if (keyCode != null) {
                robot.keyType(keyCode);
            }
        }
    }

    public static void check(String fxId, String value) {
        Optional<Node> lookup = lookup(fxId);
        if (lookup.isEmpty()) {
            return;
        }
        Node node = lookup.get();
        String text = getText(node);
        if (value.equals(text)) {
            System.out.println("CHECK SUCCESS: " + fxId + " evaluated to '''" + value + "'''");
        } else {
            System.out.println("CHECK ERROR: Could not evaluate FXID " + fxId + " value '''"
                    + text + "''' to expected value '''" + value + "'''");
        }
    }

    private static String getText(Node node) {
        if (node instanceof TextField) {
            return ((TextField)node).getText();
        } else if (node instanceof TextArea) {
            return ((TextArea)node).getText();
        } else if (node instanceof CheckBox) {
            return ((CheckBox)node).isSelected() ? "true" : "false";
        } else if (node instanceof ChoiceBox<?>) {
            return String.valueOf(((ChoiceBox)node).getValue());
        }
        return null;
    }

    public static void close()
    {
        Platform.exit();
    }

}
