package configurator;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import jsonreader.JsonArray;
import jsonreader.JsonObject;
import jsonreader.JsonValue;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

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
		private boolean editable;
		JsonObject parameterObject;
		public ParameterTableInfo(JsonObject parameter, JsonObject capabilitiesParameter) {
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
					} else {
						value.set("");
					}
				}
			}
			name.set(capabilitiesParameter.getValue("name"));
			type.set(capabilitiesParameter.getValue("type"));
		}
		public String getName() { return name.get();}
		public void setName(String name) {this.name.set(name);}
		public String getType() {return type.get();	}
		public void setType(String type) {this.type.set(type);}
		public String getValue() {return value.get();	}
		public void setValue(String type) {this.value.set(type);}
		public JsonObject getJsonObject() {return parameterObject;}
		public boolean isEditable() {return editable;}
	}

	/**
	 * This function is used to check the text entered into the value field
	 * of the table as it is written.
	 */
	UnaryOperator<TextFormatter.Change> numericOnlyOperator = change -> {
		ParameterTableInfo info = tableViewParameters.getSelectionModel().getSelectedItem();

		if (info!=null) {
			String regex = "-?([1-9][0-9]*\\.?[0-9]*)?";
			if (change.getText().matches(regex)) {
				// change is allowed because it matches the regex
				return change;
			}
		}
		// make no change
		change.setText("");
		change.setRange( change.getRangeStart(),change.getRangeStart());
		return change;
	};

	/**
	 * initialize()
	 * initialize all the controls on in the pane
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableColumnParameterName.setCellValueFactory(new PropertyValueFactory<>("name"));
		//tableColumnParameterName.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumnParameterName.setSortable(false);
		tableColumnParameterName.setEditable(false);

		tableColumnParameterType.setCellValueFactory(new PropertyValueFactory<>("type"));
		//tableColumnParameterType.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumnParameterType.setSortable(false);
		tableColumnParameterType.setEditable(false);

		// TODO: Create custom cell factory that builds either a list box or a textedit box based on type
		// TODO: Create custom cell factory that includes tool tips (description max/min)
		// TODO: create a text formatter that changes the color of the textbox if the value is invalid
		tableColumnParameterValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		tableColumnParameterValue.setCellFactory(ValidatedTextFieldTableCell.forTableColumn(numericOnlyOperator));
		tableColumnParameterValue.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumnParameterValue.setSortable(false);

		tableViewParameters.setEditable(true);
	}

	/**
	 * valueCellCommit()
	 * The value of the value cell has been committed - update the json value field
	 *
	 * @param e - a Cell Edit Event for the cell.
	 *
	 */
	@FXML private void valueCellCommit(TableColumn.CellEditEvent e) {
		int row = e.getTablePosition().getRow();
		ParameterTableInfo info = (ParameterTableInfo)e.getTableView().getItems().get(row);
		String newValue = (String)e.getNewValue();

		// TODO - commit the change to json only if there are no errors
		// TODO - abort the property value commit if there are errors
		// TODO - Move this code to a text checker
		// TODO - create a checker member function
		switch (info.getType()) {
			case "integer":
				break;
			case "real":
				// check to make sure the string is a numeric format
				if (!newValue.matches("^-?\\d+(\\.\\d+(e\\d+)?)?$")) {
					// regex failed
					Alert alertDlg = new Alert(Alert.AlertType.ERROR);
					alertDlg.setContentText("Entry must be a valid real number");
					alertDlg.show();
					return;
				}
				double valueDouble = Double.parseDouble(newValue);
				// check against the maximum and minimum values
				if ((info.getJsonObject().get("maxValue") != null) &&
					(info.getJsonObject().getValue("maxValue")!=null) &&
					(!info.getJsonObject().getValue("maxValue").toLowerCase().contains("null"))) {
					double maxValue = info.getJsonObject().getDouble("maxValue");
					if (valueDouble > maxValue) {
						Alert alertDlg = new Alert(Alert.AlertType.ERROR);
						alertDlg.setContentText("Entry must be equal or less than "+ maxValue);
						alertDlg.show();
						// set the cell value to the old value
						return;
					}
				}
				if ((info.getJsonObject().get("minValue") != null) &&
					(info.getJsonObject().getValue("minValue")!=null) &&
					(!info.getJsonObject().getValue("minValue").toLowerCase().contains("null"))) {
					double minValue = info.getJsonObject().getDouble("minValue");
					if (valueDouble < minValue) {
						Alert alertDlg = new Alert(Alert.AlertType.ERROR);
						alertDlg.setContentText("Entry must be equal or greater than "+ minValue);
						alertDlg.show();
						return;
					}
				}
			case "enum":
			default:  // assume string
				// There is no format or range checking for these types
				// just update the json value
				((JsonValue)info.getJsonObject().get("value")).set(newValue);
		}
	}

	/** updateControls()
	 * update the pane controls based on the current configuration
	 * of the Json representation.
	 **/
	private void updateControls()
	{
		// update the tableview
		tableViewParameters.getItems().clear();
		for (int i=0;i<jsonParameters.size();i++) {
			JsonObject parameter = (JsonObject)jsonParameters.get(i);
			JsonObject capParam = (JsonObject)jsonCapabilitiesParameters.get(i);
			ParameterTableInfo info = new ParameterTableInfo(parameter, capParam);
			// TODO - set the color of the row if the value is invalid
			tableViewParameters.getItems().add(info);
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

		updateControls();
	}
}