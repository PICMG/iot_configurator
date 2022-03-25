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
import java.util.stream.Collectors;

public class RobotUtils {


    private static final int offset = 5;
    public static boolean debug = true;
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
        robot.mouseMove(offset+point.getX()+scene.getX()+area.getX(),offset+point.getY()+scene.getY()+area.getY());
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
        robot.mouseMove(offset+point.getX()+scene.getX()+area.getX(),offset+point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

    /**
     * This method clicks on the device configuration tab
     * @param scene
     */
    public static void clickDevice(Scene scene)
    {
        Window area = scene.getWindow();
        Node deviceTab = scene.lookup("#deviceTab");
        Point2D point = deviceTab.localToScene(0,0);
        if(debug)
            System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(point.getX()+scene.getX()+area.getX(),point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

    /**
     * This method clicks the reset button
     * @param scene
     */
    public static void clickReset(Scene scene) {
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


    public static void click(Scene scene, String value)
    {
        Window area = scene.getWindow();
        Node node = scene.lookup(value);
        Point2D point = node.localToScene(0,0);
        if(debug)
            System.out.println(scene.getX()+" "+scene.getY());
        robot.mouseMove(offset+point.getX()+scene.getX()+area.getX(),offset+point.getY()+scene.getY()+area.getY());
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

    /**
     * Run the given function with FX threading after delaying on a generic Java thread. This threading hot potato
     * gives the FX threads processing time so UI components can catch up before the function is added to the FX thread
     * queue.
     * @param runnable A runnable object to execute on the FX thread after some delay
     * @param delay The wait time in milliseconds (e.g. delay of 1000 is 1 second)
     */
    public static void runLater(Runnable runnable, int delay) {
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
     * @param runnables A list of runnable objects to execute on the FX thread after some delay
     * @param delay The wait time between each thread in milliseconds (e.g. delay of 1000 is 1 second)
     */
    public static void runLater(Runnable[] runnables, int delay) {
        Thread t = new Thread(() -> {
            for (Runnable r : runnables) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    System.out.println("Exception in wait queue. "); e.printStackTrace();
                }
                Platform.runLater(r);
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
