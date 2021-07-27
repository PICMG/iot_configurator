/*
 * This class implements a JavaFX dialog window that contains an 
 * editable table of parameters.  The first column in the table is the
 * parameter name, the second column is the value which is editable with
 * range checking.  The final column is the access mode.
 *  
 */
package org.picmg.configurator;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParameterTable extends Stage {
	private Scene scene;
    private TableView<ConfigurationParameter> table = new TableView<ConfigurationParameter>();
    private ObservableList<ConfigurationParameter> data;
   
	public ParameterTable(ObservableList<ConfigurationParameter> data) {
		super();
		scene = new Scene(new Group());
        setTitle("Edit Parameters");
        setWidth(500);
        setHeight(500);
 
        this.data = data;
 
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
