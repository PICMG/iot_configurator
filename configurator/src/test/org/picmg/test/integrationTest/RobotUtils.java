package org.picmg.test.integrationTest;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    public static final boolean debug = true;
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
    public static void clickEffecters()
    {
        click("#effectersTab");
    }

    /**
     * Click and open the device configuration tab
     */
    public static void clickDevice() { click("#effectersTab"); }

    /**
     * Manually call reset from menu option
     */
    public static void clickReset() {
        try {
            click("#resetMenu");
            runLater(3000, ()->click("#resetOk"));
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
        List<Scene> scenes = Stage.getWindows().stream().map(Window::getScene).collect(Collectors.toList());
        Node foundNode = null;
        for (Scene scene : scenes) {
            Node newNode = scene.lookup(value);
            if (newNode != null) foundNode = newNode;
        }
        return foundNode == null
                ? Optional.empty()
                : Optional.of(foundNode);
    }


    public static void click(String value) {
        Optional<Node> lookupResult = lookup(value);
        if (lookupResult.isEmpty()) {
            System.out.println("Error: Unable to locate FXID " + value + " in any existing stage.");
            return;
        }
        Node node = lookupResult.get();
        double offH = OFFSET, offW = OFFSET;
        Scene scene = node.getScene();
        Window window = scene.getWindow();
        Point2D point = node.localToScene(0,0);
        robot.mouseMove(offH+point.getX()+scene.getX()+window.getX(),offW+point.getY()+scene.getY()+window.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

    /**
     * Run the given function with FX threading after delaying on a generic Java thread. This threading hot potato
     * gives the FX threads processing time so UI components can catch up before the function is added to the FX thread
     * queue.
     * @param delay The wait time in milliseconds (e.g. delay of 1000 equals 1 second)
     * @param runnable A runnable object to execute on the FX thread after some delay
     */
    public static void runLater(int delay, Runnable runnable) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Exception in wait queue. "); e.printStackTrace();
            }
            Platform.runLater(runnable);
        });
        t.start();
    }

    /**
     * Run one or many runnables with equivalent, non-FX delay time between sequential deployment to FX thread queue.
     * @param delay The wait time between each thread in milliseconds (e.g. delay of 1000 is 1 second)
     * @param runnables A list of runnable objects to execute on the FX thread after some delay
     */
    public static void runLater(int delay, Runnable... runnables) {
        Thread t = new Thread(() -> {
            for (Runnable runnable : runnables) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    System.out.println("Exception in wait queue. "); e.printStackTrace();
                }
                Platform.runLater(runnable);
            }
        });
        t.start();
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
            } else {
                robot.keyType(KeyCode.X);
            }
        }
    }

    public static Scene switchScene(int sceneNum) {
        int counter = 0;
        List<Scene> scenes = Stage.getWindows().stream().map(Window::getScene).collect(Collectors.toList());
        if (counter > scenes.size()) return scenes.get(0);
        if (scenes.iterator().hasNext()) {
            scenes.iterator().next();
            if (sceneNum == counter) {
                return scenes.get(counter);
            }
            counter++;
        }
        return scenes.get(0);
    }

    public static void close()
    {
        Platform.exit();
    }

}
