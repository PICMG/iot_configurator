package configurator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jsonreader.*;

public class SensorConfigurationDlg extends Stage {
	private Scene scene;
    private TableView<ConfigurationParameter> table = new TableView<ConfigurationParameter>();
    private ObservableList<ConfigurationParameter> data ;
    private boolean valid;    
    
    @Override
    public void showAndWait() {
    	if (!valid) return;
    	super.showAndWait();
    }
    
	public SensorConfigurationDlg(JsonObject sensor,JsonObject meta) {
		super();
		valid = false;
		scene = new Scene(new Group());
        setTitle("Edit Sensor Parameters");
        setWidth(500);
        setHeight(500);
 
        this.data = data;
 
        // read the sensor configuration information and place it in the table
    	ConfigurationParameter cp = null;
    	sensor.forEach((key,value) -> {
    		if (value.getClass().isAssignableFrom(JsonValue.class)) {
    			// here for simple json values
    			if (meta.containsKey(key)) {
    				// default metadata exists for this key - use it
    				switch (meta.getValue("type")) {
    				case "float":
//    					cp = new DoubleConfigurationParameter(
//        						key, value.getDouble(key), meta.getValue("description"),
//        						meta.getDouble("min_value"), meta.getDouble("max_value"),
//        						(JsonArray)meta.getValue("access");
//        					);
        				break;
        			case "int":
    					break;
    				default:
    					break;
    				}
    			}
    		} else {
    			// do nothing if the parameter is an object or array
    		}
    	});
        
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
 
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(table);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        setScene(scene);
	}
}
