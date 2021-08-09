//*******************************************************************
//    ParameterPaneController.java
//
//    More information on the PICMG IoT data model can be found within
//    the PICMG family of IoT specifications.  For more information,
//    please visit the PICMG web site (www.picmg.org)
//
//    Copyright (C) 2020,  PICMG
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
package org.picmg.configurator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * ParameterPaneController class
 * This class provides the control function for the parameter pane
 *
 * here are some rules for parameter setting from the specification
 * (paraphrased)
 * - parameters with values set in the capabilities section cannot
 *   be altered during configuration
 * - values for numeric parameters must lie between the maximum and
 *   minimum values (if specified)
 * - values for enumerated parameters must be chosen from the array
 *   of allowed choices
 * - values for parameters
 */
public class ParameterPaneController implements Initializable {
	// member data
	private JsonArray jsonParameters;
	private JsonArray jsonCapabilitiesParameters;

	// objects declared in the FXML definition
	@FXML private TableView<ParameterTableInfo> tableViewParameters;
	@FXML private TableColumn<ParameterTableInfo, String> tableColumnParameterName;
	@FXML private TableColumn<ParameterTableInfo, String> tableColumnParameterType;
	@FXML private TableColumn<ParameterTableInfo, String> tableColumnParameterValue;

	// data model for the parameter table
	public class ParameterTableInfo {
		private SimpleStringProperty name = new SimpleStringProperty();
		private SimpleStringProperty type = new SimpleStringProperty();
		private SimpleStringProperty value = new SimpleStringProperty();
		private SimpleStringProperty maxValue = new SimpleStringProperty();
		private SimpleStringProperty minValue = new SimpleStringProperty();
		private SimpleStringProperty description = new SimpleStringProperty();
		private SimpleStringProperty state = new SimpleStringProperty();
		private boolean editable;
		JsonObject parameterObject;
		public ParameterTableInfo(JsonObject parameter, JsonObject capabilitiesParameter) {
			update(parameter, capabilitiesParameter);
		}
		public void update(JsonObject parameter, JsonObject capabilitiesParameter) {
			parameterObject = parameter;
			if ((capabilitiesParameter.get("value") != null) &&
					(capabilitiesParameter.getValue("value") != null) &&
					(!capabilitiesParameter.getValue("value").toLowerCase().contains("null"))) {
				// capabilities value is specified
				editable = false;
				value.set(capabilitiesParameter.getValue("value"));
			} else {
				if ((parameter.get("value") != null) &&
						(parameter.getValue("value") != null) &&
						(!parameter.getValue("value").toLowerCase().contains("null"))) {
					// configuration already has a value - use it
					value.set(parameter.getValue("value"));
				} else {
					if ((parameter.get("defaultValue") != null) &&
							(parameter.getValue("defaultValue") != null) &&
							(!parameter.getValue("defaultValue").toLowerCase().contains("null"))) {
						value.set(parameter.getValue("defaultValue"));

						// set the value in the json structure to the default value also
						((JsonValue)getJsonObject().get("value")).set(parameter.getValue("defaultValue"));
					} else {
						value.set("");
					}
				}
			}
			name.set(capabilitiesParameter.getValue("name"));
			type.set(capabilitiesParameter.getValue("type"));
			description.set(capabilitiesParameter.getValue("description"));
			maxValue.set(capabilitiesParameter.getValue("maxValue"));
			minValue.set(capabilitiesParameter.getValue("minValue"));

			// set the state of this parameter, since parameter values are always
			// required if they are specified in the capabilities section, incorrect
			// parameter values constitute an error.
			if (isValueValid(parameter.getValue("value"), parameter)) {
				setState("valid");
			} else {
				setState("error");
			}
		}
		public String getName() { return name.get();}
		public void setName(String name) {this.name.set(name);}
		public String getType() {return type.get();	}
		public void setType(String type) {this.type.set(type);}
		public String getState() {return state.get();	}
		public void setState(String state) {this.state.set(state);}
		public String getValue() {return value.get();	}
		public String getMaxValue() {return maxValue.get();	}
		public String getMinValue() {return minValue.get();	}
		public void setValue(String value) {this.value.set(value);}
		public String getDescription() {return description.get();	}
		public boolean getEditable() {return editable;};
		public void setDescription(String description) {this.description.set(description);}
		public JsonObject getJsonObject() {return parameterObject;}
		public boolean isEditable() {return editable;}
		public ObservableList<String> getChoices() {
			// this getter provides a list of allowed values for enumerated parameters
			// otherwise it returns null.
			if (parameterObject.get("allowedValues")==null) return null;
			if (!(parameterObject.get("allowedValues") instanceof JsonArray)) return null;
			JsonArray choices = (JsonArray) parameterObject.get("allowedValues");
			if (choices.size()==0) return null;
			ObservableList<String> result = FXCollections.observableArrayList();
			choices.forEach((choice) -> result.add(choice.getValue("")));
			return result;
		}
	}

	/**
	 * initialize()
	 * initialize all the controls on in the pane
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableColumnParameterName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnParameterName.setSortable(false);
		tableColumnParameterName.setEditable(false);

		tableColumnParameterType.setCellValueFactory(new PropertyValueFactory<>("type"));
		tableColumnParameterType.setSortable(false);
		tableColumnParameterType.setEditable(false);

		tableColumnParameterValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		tableColumnParameterValue.setCellFactory(combobox -> {
			// create a new list for each combo box.  This will allow the drop-down options to be
			// different for each row.
			ObservableList<String> list = FXCollections.observableArrayList();
			return new UniqueComboBoxTableCell<>(null, list);
		});
		tableColumnParameterValue.setSortable(false);

		tableViewParameters.setEditable(true);
	}

	/**
	 * isValueValid
	 * check to see if the cell value is of the right format and
	 * range.
	 * @param newValue - the value to check
	 * @param paramObject - the parameter object for this object
	 * @returns true if the value is valid, otherwise, false.
	 */
	private boolean isValueValid(String newValue, JsonObject paramObject) {
		switch (paramObject.getValue("type")) {
			case "integer":
				// check to make sure the new value is a numeric format
				if (!App.isInteger(newValue)) {
					return false;
				}
				int valueInteger = Integer.parseInt(newValue,10);
				// check against the maximum and minimum values
				if ((paramObject.get("maxValue") != null) &&
						(paramObject.getValue("maxValue")!=null) &&
						(!paramObject.getValue("maxValue").toLowerCase().contains("null"))) {
					int maxValue = paramObject.getInteger("maxValue");
					if (valueInteger > maxValue) {
						return false;
					}
				}
				if ((paramObject.get("minValue") != null) &&
						(paramObject.getValue("minValue")!=null) &&
						(!paramObject.getValue("minValue").toLowerCase().contains("null"))) {
					int minValue = paramObject.getInteger("minValue");
					if (valueInteger < minValue) {
						return false;
					}
				}
				break;
			case "real":
				// check to make sure the new value is a numeric format
				if (!App.isFloat(newValue)) {
					// regex failed
					return false;
				}
				double valueDouble = Double.parseDouble(newValue);
				// check against the maximum and minimum values
				if ((paramObject.get("maxValue") != null) &&
						(paramObject.getValue("maxValue")!=null) &&
						(!paramObject.getValue("maxValue").toLowerCase().contains("null"))) {
					double maxValue = paramObject.getDouble("maxValue");
					if (valueDouble > maxValue) {
						return false;
					}
				}
				if ((paramObject.get("minValue") != null) &&
						(paramObject.getValue("minValue")!=null) &&
						(!paramObject.getValue("minValue").toLowerCase().contains("null"))) {
					double minValue = paramObject.getDouble("minValue");
					if (valueDouble < minValue) {
						return false;
					}
				}
				break;
			case "enum":
				// since enums are assigned from a choiceBox, there is no need
				// to check the value specifically - just make sure it is not
				// empty or null.
				if (newValue == null) return false;
				if (newValue.equals("")) return false;
				break;
			default:  // assume string
		}
		return true;
	}

	/**
	 * valueCellCommit()
	 * The value of the value cell has been committed - update the json value field
	 *
	 * @param e - a Cell Edit Event for the cell.
	 *
	 */
	@FXML private void onValueCommit(TableColumn.CellEditEvent e) {
		int row = e.getTablePosition().getRow();
		ParameterTableInfo info = (ParameterTableInfo)e.getTableView().getItems().get(row);
		String newValue = (String)e.getNewValue();

		((JsonValue) info.getJsonObject().get("value")).set(newValue);
		updateControls();
	}

	/** updateControls()
	 * update the pane controls based on the current configuration
	 * of the Json representation.
	 **/
	private void updateControls()
	{
		// update the tableview
		for (int i=0;i<jsonParameters.size();i++) {
			JsonObject parameter = (JsonObject)jsonParameters.get(i);
			JsonObject capParam = (JsonObject)jsonCapabilitiesParameters.get(i);
			if (i<tableViewParameters.getItems().size()) {
				tableViewParameters.getItems().get(i).update(parameter, capParam);
			} else {
				ParameterTableInfo info = new ParameterTableInfo(parameter, capParam);
				tableViewParameters.getItems().add(info);
			}
		}
	}

	/** update()
	 * This function is called when the pane is changed from the main menu.
	 * It updates all the controls based on the java objects that it corresponds to.
	 * @param parameters the json object for the parameters
	 * @param capabilitiesParameters the json object for the corresponding parameters in the
	 *                        capabilities section of the input file
	 */
	public void update(JsonArray parameters, JsonArray capabilitiesParameters)
	{
		// do any configuration required prior to making the pane visible
		jsonParameters = parameters;
		jsonCapabilitiesParameters = capabilitiesParameters;
		tableViewParameters.getItems().clear();

		updateControls();
	}

}