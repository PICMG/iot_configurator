package configurator;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jsonreader.*;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
        
//        JsonResultFactory factory = new JsonResultFactory();
//        JsonObject appConfig = (JsonObject)factory.buildFromResource("config.json");
//        JsonObject hardware = (JsonObject)factory.buildFromResource("microsam.json");
//        JsonObject config;
//        
//        // find which pins have been used so far
//        ArrayList<String> usedPins = new ArrayList<String>(); 
//        ((JsonArray)config.get("sensors")).forEach(s -> ((JsonArray)((JsonObject)s).get("pins")).forEach(p -> usedPins.add(p.getValue(""))));
//        ((JsonArray)config.get("effectors")).forEach(e -> ((JsonArray)((JsonObject)e).get("pins")).forEach(p -> usedPins.add(p.getValue(""))));
//
//        // pick channels first.
//        // channel must not use pins that have already been assigned to a sensor or effector
//        ArrayList<String> validChannel = new ArrayList<String>();
//        ((JsonArray)hardware.get("channels")).forEach( c -> ((JsonArray)c.getValue("pins")).;
//        // query to see if a specific channel type is available
//        // for each channel type found in the hardware's channels list
//        //    for each sensor in the sensors list
//        //       if channel pins are part of pins used by channel of sensor
//        //          invalid channel - > go to next channel
//        //    channel is valid
//        ArrayList<String> validSensors = new ArrayList<String>();
        
    }

    public static void main(String[] args) {
        launch();
    }

}