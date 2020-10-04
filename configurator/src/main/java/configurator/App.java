package configurator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jsonreader.*;

public class App extends Application {
	JsonObject appConfig;
	JsonObject hardware;
	JsonObject sensors;
	JsonObject effectors;
	JsonObject device;
	JsonObject defaultMeta;
	TextArea   configText;
	
    /*
     * find which pins have been used so far
     * the first query searches all sensors attached to the device to get the channel used,
     * once the channel type is known, a search is made in the hardware definition
     * to find the pins associated with it.
     * the second query does the same, but for the effectors attached to the device
	 */
	private ArrayList<String> getUsedPins() {
        ArrayList<String> usedPins = new ArrayList<String>(); 
        ((JsonArray)device.get("sensors")).forEach( 
        	s -> ((JsonArray)device.get("channels")).forEach(
        		c -> { if (((JsonObject)c).getValue("name").equals(((JsonObject)s).getValue("channel"))&&
        				((JsonObject)c).getValue("direction").equals("input")) {
        			((JsonArray)((JsonObject)c).get("pins")).forEach( p -> usedPins.add(p.getValue("")) ); 
        		} } ) );
        ((JsonArray)device.get("effectors")).forEach( 
            s -> ((JsonArray)device.get("channels")).forEach(
            	c -> { if (((JsonObject)c).getValue("name").equals(((JsonObject)s).getValue("channel"))&&
            			((JsonObject)c).getValue("direction").equals("output")) {
            		((JsonArray)((JsonObject)c).get("pins")).forEach( p -> usedPins.add(p.getValue("")) ); 
            	} } ) );
        return usedPins;
	}

	/* 
	 * Return a list of sensors that can be connected to the device based on the 
	 * pins/channels that are currently used.
	 */
	private ArrayList<String> getPossibleSensors(ArrayList<String> usedPins) {
        ArrayList<String> possibleSensors = new ArrayList<String>();
        ((JsonArray)sensors.get("sensors")).forEach( 
            	s -> ((JsonArray)device.get("channels")).forEach(
            		c -> { if (((JsonObject)c).getValue("type").equals(((JsonObject)s).getValue("channel_type"))&&
            				((JsonObject)c).getValue("direction").equals("input")) {
            			// here if a channel match has been found - if the pins have not been used
            			// and the sensor is not already in the list, add it.
            			if (!((JsonArray)((JsonObject)c).get("pins")).containsAny(usedPins)) 
            				if (!possibleSensors.contains(s.getValue("name"))) 
            					possibleSensors.add(s.getValue("name"));
            		} } ) );
        return possibleSensors;
	}
	
	/* 
	 * Return a list of sensors that can be connected to the device based on the 
	 * pins/channels that are currently used.
	 */
	private ArrayList<String> getPossibleEffectors(ArrayList<String> usedPins) {
	    ArrayList<String> possibleEffectors = new ArrayList<String>();
	    ((JsonArray)effectors.get("effectors")).forEach( 
	        	s -> ((JsonArray)device.get("channels")).forEach(
	        		c -> { if (((JsonObject)c).getValue("type").equals(((JsonObject)s).getValue("channel_type"))&&
	        				((JsonObject)c).getValue("direction").equals("output")) {
	        			// here if a channel match has been found - if the pins have not been used
	        			// and the effector is not already in the list, add it.
	        			if (!((JsonArray)((JsonObject)c).get("pins")).containsAny(usedPins)) 
	        				if (!possibleEffectors.contains(s.getValue("name"))) 
	        					possibleEffectors.add(s.getValue("name"));
	        		} } ) ); 
	    return possibleEffectors;
	}
	
	private ArrayList<String> getListOfChannels(String type, String direction, ArrayList<String> usedPins) {
		ArrayList<String> availableChannels = new ArrayList<String>();
        ((JsonArray)device.get("channels")).forEach(
    		c -> { if (((JsonObject)c).getValue("type").equals(type)&&
    				((JsonObject)c).getValue("direction").equals(direction)) {
    			// here if a channel match has been found - if the pins have not been used
    			// and the channel to the list.
    			if (!((JsonArray)((JsonObject)c).get("pins")).containsAny(usedPins)) 
    				if (!availableChannels.contains(c.getValue("name"))) 
    					availableChannels.add(c.getValue("name"));
        		} } ); 
		return availableChannels;
	}
	
	/*
	 * update the text in the configuration display
	 */	
	void updateText()
	{
		StringWriter sw = new StringWriter();
		
		JsonArray sensors = (JsonArray)device.get("sensors");
		sensors.forEach(s -> {
			sw.write("\"");
			sw.write(((JsonObject)s).getValue("name"));
			sw.write("\" ");
			sw.write(((JsonObject)s).getValue("channel"));
			sw.write("\n");
		});
		JsonArray effectors = (JsonArray)device.get("effectors");
		effectors.forEach(e -> {
			sw.write("\"");
			sw.write(((JsonObject)e).getValue("name"));
			sw.write("\" ");
			sw.write(((JsonObject)e).getValue("channel"));
			sw.write("\n");
		});
		configText.setText(sw.toString());
	}
	
	void writeFile() {
        try (FileWriter writer = new FileWriter("output.json");
                BufferedWriter bw = new BufferedWriter(writer)) {

               device.writeToFile(bw);
               bw.close();
           } catch (IOException e) {
               System.err.format("IOException: %s%n", e);
           }
	}
		
	@Override
    public void start(Stage stage) {
        
        JsonResultFactory factory = new JsonResultFactory();
        appConfig = (JsonObject)factory.buildFromResource("config.json");
        hardware = (JsonObject)factory.buildFromResource("microsam.json");
        sensors = (JsonObject)factory.buildFromResource("sensors.json");
        effectors = (JsonObject)factory.buildFromResource("effectors.json");
        defaultMeta = (JsonObject)factory.buildFromResource("default_meta.json");
        		
        // create an empty device based on the hardware configuration file
        device = new JsonObject();
        device.put("sensors",new JsonArray());
        device.put("effectors",new JsonArray());
        device.put("controllers",hardware.get("controllers"));
        device.put("channels",hardware.get("channels"));
        device.put("pins",hardware.get("pins"));
        
		HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: #336699;");

	    BorderPane border = new BorderPane();
		border.setTop(hbox);

        Button addSensorButton = new Button("+Sensor");
        Button addEffectorButton = new Button("+Effector");
	    hbox.getChildren().addAll(addSensorButton, addEffectorButton);

	    ComboBox<String> cb = new ComboBox<String>();
        cb.getItems().add("test");
        cb.getItems().add("test2");
        cb.setEditable(true);
        cb.getStyleClass().clear();  // this removes the drop-down
        hbox.getChildren().add(cb);

	    ComboBox<String> cb2 = new ComboBox<String>();
        cb2.getItems().add("test");
        cb2.getItems().add("test2");
        cb2.setEditable(true);
        hbox.getChildren().add(cb2);

	    configText = new TextArea();
	    border.setBottom(configText);
	    
        addSensorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                ArrayList<String> usedPins = getUsedPins();
                ChoiceDialog<String> d = new ChoiceDialog<String>("",getPossibleSensors(usedPins));
                d.showAndWait();
                // add the new sensor
                String selection = d.getSelectedItem();
                if ((selection!=null)&&(!selection.isEmpty())) {
                	// find the named sensor in the sensor list and create a clone
                	JsonObject obj = new JsonObject(((JsonArray)sensors.get("sensors")).findMatching("name", d.getSelectedItem()));

                	// get a list of channels the sensor could connect to
                	// based on the currently used pins
                	ArrayList<String> channelChoices = getListOfChannels(
                			obj.getValue("channel_type"),
                			"input",
                			usedPins
                			);
                		
                	// connect the sensor to a channel
                	if (channelChoices.size()==1) {
                    	// if there is only one channel available, 
                    	// connect the sensor to that channel
                		obj.put("channel",new JsonValue(channelChoices.get(0)));
                	} else {
                		// otherwise request the channel to connect the
                		// sensor to add the new sensor to the device
                        ChoiceDialog<String> d2 = new ChoiceDialog<String>(channelChoices.get(0),channelChoices);
                        d2.showAndWait();
                		obj.put("channel",new JsonValue(d2.getSelectedItem()));                		
                	}
                	
                	// add the sensor to the device
                	((JsonArray)device.get("sensors")).add(obj);
                	updateText();
                	writeFile();
                }
            }
        });
        
        addEffectorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	ArrayList<String> usedPins = getUsedPins();
                ChoiceDialog<String> d = new ChoiceDialog<String>("",getPossibleEffectors(usedPins));
                d.showAndWait();
                // add the new sensor
                String selection = d.getSelectedItem();
                if ((selection!=null)&&(!selection.isEmpty())) {
                	// find the named effector in the effector list and create a clone
                	JsonObject obj = new JsonObject(((JsonArray)effectors.get("effectors")).findMatching("name", d.getSelectedItem()));

                	// get a list of channels the sensor could connect to
                	// based on the currently used pins
                	ArrayList<String> channelChoices = getListOfChannels(
                			obj.getValue("channel_type"),
                			"output",
                			usedPins
                			);
                		
                	// connect the sensor to a channel
                	if (channelChoices.size()==1) {
                    	// if there is only one channel available, 
                    	// connect the sensor to that channel
                		obj.put("channel",new JsonValue(channelChoices.get(0)));
                	} else {
                		// otherwise request the channel to connect the
                		// sensor to add the new sensor to the device
                        ChoiceDialog<String> d2 = new ChoiceDialog<String>(channelChoices.get(0),channelChoices);
                        d2.showAndWait();
                		obj.put("channel",new JsonValue(d2.getSelectedItem()));                		
                	}
                	
                	// add the sensor to the device
                	((JsonArray)device.get("effectors")).add(obj);
                	updateText();
                	writeFile();
                }
            }
        });

		Scene scene = new Scene(border, 640, 480);
    	updateText();
		stage.setScene(scene);
        stage.show();  
        
        ParameterTable pt = new ParameterTable(FXCollections.observableArrayList(
        		new EnumConfigurationParameter   ("Enumerated", FXCollections.observableArrayList("First","Second"),"An enumerated value", FXCollections.observableArrayList("Hidden","ReadOnly","Full")),
        		new BooleanConfigurationParameter("Boolean",    true, "", FXCollections.observableArrayList("Hidden","ReadOnly","Full")),
        		new StringConfigurationParameter("String", "default", "A string entry.", 20, FXCollections.observableArrayList("Hidden","ReadOnly")),
        		new DoubleConfigurationParameter("Double", 3.14159, "A floating point entry", 0, 100, FXCollections.observableArrayList("Hidden","ReadOnly","Full")),
        		new IntegerConfigurationParameter("Integer", 5, "An integer entry", 0, 10, FXCollections.observableArrayList("Hidden","ReadOnly","Full"))
        		));
        pt.showAndWait();
        
        LinearizationDlg ld = new LinearizationDlg();
        ld.setDataValues(null);
        ld.showAndWait(); 

    }

    public static void main(String[] args) {
        launch();
    }
}
	