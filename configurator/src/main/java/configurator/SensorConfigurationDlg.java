package configurator;

import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jsonreader.*;

public class SensorConfigurationDlg extends Stage {
	private Scene scene;
    private TableView<ConfigurationParameter> table = new TableView<ConfigurationParameter>();
    private ObservableList<ConfigurationParameter> data ;
    private boolean valid;    
    private boolean result = false;
    
    @Override
    public void showAndWait() {
    	if (!valid) return;
    	super.showAndWait();
    }
    
	public SensorConfigurationDlg(JsonObject sensor,JsonObject meta, ArrayList<String> channelChoices) {
		super();
		valid = false;
		scene = new Scene(new Group());
        setTitle("Edit Sensor Parameters");
        setWidth(500);
        setHeight(600);
 
        data = FXCollections.observableArrayList();
        
        // read the sensor configuration information and place it in the table
    	sensor.forEach((key,value) -> {
    		if (value.getClass().isAssignableFrom(JsonValue.class)) {
    			// here for simple json values
    			if (meta.containsKey(key)) {
    				// default metadata exists for this key - use it
    				JsonObject metaObj = (JsonObject)meta.get(key);
    				switch (metaObj.getValue("type")) {
    				case "float":
    					data.add(new DoubleConfigurationParameter(
    						key, value.getDouble(""), metaObj.getValue("description"),
    						metaObj.getDouble("min_value"), metaObj.getDouble("max_value"),
    						(JsonArray)metaObj.get("access")));
    					break;
    				case "int":
    					data.add(new DoubleConfigurationParameter(
    						key, value.getInteger(""), metaObj.getValue("description"),
    						metaObj.getInteger("min_value"), metaObj.getInteger("max_value"),
    						(JsonArray)metaObj.get("access")));
    					break;
    				default:
    					data.add(new StringConfigurationParameter(
        						key, value.getValue(""), metaObj.getValue("description"),
        						metaObj.getInteger("length"),(JsonArray)metaObj.get("access")));
        					break;
    				}
    			} else {
    				// assume that the parameter is a string and use the default value
    				JsonObject metaObj = (JsonObject)meta.get("_default");
					data.add(new StringConfigurationParameter(
    						key, value.getValue(""), metaObj.getValue("description"),
    						metaObj.getInteger("length"),(JsonArray)metaObj.get("access")));    				
    			}
    		} else {
    			// do nothing if the parameter is an object or array
    		}
    	});
    
    	// Create an entry for the channel choices
		JsonObject metaObj = (JsonObject)meta.get("channel");
		ObservableList<String> chanList = FXCollections.observableArrayList();
		channelChoices.forEach(s -> chanList.add(s));
    	data.add(new EnumConfigurationParameter(
    			"channel", chanList, metaObj.getValue("description"), (JsonArray)metaObj.get("access")));

    	TableColumn<ConfigurationParameter,String> parameterCol = new TableColumn<ConfigurationParameter,String>("Parameter");
        parameterCol.setCellValueFactory(
        	new PropertyValueFactory<ConfigurationParameter,String>("parameterName")
        );

        TableColumn<ConfigurationParameter,String> valueCol = new TableColumn<ConfigurationParameter,String>("Value");
        valueCol.setCellValueFactory(
            	new PropertyValueFactory<ConfigurationParameter,String>("control")
            );

        TableColumn<ConfigurationParameter,String> accessCol = new TableColumn<ConfigurationParameter,String>("Access");
        accessCol.setCellValueFactory(
            	new PropertyValueFactory<ConfigurationParameter,String>("access")
            );

        table.setEditable(true);
        table.setItems(data);
        table.getColumns().add(parameterCol);
        table.getColumns().add(valueCol);
        table.getColumns().add(accessCol);
        table.setMinWidth(400);
 
        //====================================================
        // configure the layout

        Button bLinearize = new Button("Linearization Table");
        Button bOk = new Button("OK");
        Button bCancel = new Button("Cancel");
        
        HBox hbox = new HBox();
	    hbox.setPadding(new Insets(25, 25, 25, 25));
	    hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(bLinearize, bOk,bCancel);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(table,hbox);

        /*
         * Button functions
         */
        bLinearize.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                LinearizationDlg ld = new LinearizationDlg();
                // TODO: add the sensor linearization curve to the data
                ld.setDataValues(null);
                ld.showAndWait(); 
            }
        });
        bOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	result = true;
            	close();
            }
        });
        bCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	close();
            }
        });
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        setScene(scene);
        valid = true;
	}
	
	public boolean getResult() {
		return true;
	}
	
	public String getChannel() {
		String str = "";
		for (int i=0;i<data.size();i++) {
			ConfigurationParameter cp = data.get(i);
			if (cp.getParameterName().equals("channel")) {
				str = ((ComboBox<String>)cp.getControl()).getValue();
				break;
			}
		};
		return str;
	}
}
