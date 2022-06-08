//*******************************************************************
//    FruPaneController.java
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

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * FruPaneController class
 * This class provides the control function for the FRU info pane for both
 * Standard and OEM FRU records.
 */
public class FruPaneController implements Initializable {
	// member data
	private JsonObject jsonFru;
	private JsonObject jsonCapabilitiesFru;
	private TreeItem<MainScreenController.TreeData> selectedNode;
	private MainScreenController mainController;

	private final int DMTF_IANA = 412;
	private final String[] typeChoices = {"Chassis Type","Model","Part Number", "Serial Number","Manufacturer",
			"Manufacture Date", "Vendor", "Name", "SKU", "Version", "Asset Tag",
			"Description", "Engineering Change Level", "Other Information","Vendor IANA"};
	private final String[] formatChoices = {"uint8","sint8","uint16","sint16","uint32","sint32","uint64","sint64",
			"string","bool8","real32","real64","timestamp104","bytes"};

	// objects declared in the FXML definition
	@FXML private ChoiceBox<String> choiceBoxFruType;
	@FXML private TextField textFieldOemId;
	@FXML private TableView<FruTableInfo> tableViewFruInfo;
	@FXML private TableColumn<FruTableInfo, String> tableColumnDescription;
	@FXML private TableColumn<FruTableInfo, String> tableColumnType;
	@FXML private TableColumn<FruTableInfo, String> tableColumnOEMType;
	@FXML private TableColumn<FruTableInfo, String> tableColumnValue;
	@FXML private TableColumn<FruTableInfo, String> tableColumnFormat;
	@FXML private Button buttonAdd;
	@FXML private Button buttonDelete;

	// data model for the fru information table
	public static class FruTableInfo {
		String description;
		String type;
		String oemType;
		String value;
		String format;
		public boolean description_editable;
		public boolean type_editable;
		public boolean value_editable;
		public boolean format_editable;
		public boolean oemtype_editable;
		public String getOemType() { return oemType;}
		public void setOemType(String oemType) { this.oemType = oemType;}
		public String getFormat() { return format;}
		public void setFormat(String format) { this.format = format;}
		public String getType() { return type;}
		public void setType(String type) { this.type = type;	}
		public String getValue() { return value;}
		public void setValue(String value) { this.value = value;}
		public String getDescription() { return description;}
		public void setDescription(String name) { this.description = name;}
	}

	/**
	 * This function is used to check the text entered into the value field
	 * of the fru information table as it is written.
	 * TODO: Some of the regular expressions can be improved to be more
	 *       restrictive
	 */
	UnaryOperator<TextFormatter.Change> decimalOnlyOperator = change -> {
		int selectedRow = tableViewFruInfo.getSelectionModel().getSelectedIndex();
		FruTableInfo info = tableViewFruInfo.getSelectionModel().getSelectedItem();

		if (info!=null) {
			String format = info.getFormat();
			String regex;

			switch (format) {
				case "uint8":
				case "uint16":
				case "uint32":
				case "uint64":
					regex = "([1-9][0-9]*)?";
					break;
				case "sint8":
				case "sint16":
				case "sint32":
				case "sint64":
					regex = "-?([1-9][0-9]*)?";
					break;
				default:
					// assume string - accept anything
					regex = "^.*$";
			}
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

	void setMainController(MainScreenController mainController) {
		this.mainController = mainController;
	}

	@Override
	/**
	 * initialize()
	 * initialize all the controls on in the pane
	 */
	public void initialize(URL location, ResourceBundle resources) {
		tableColumnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		tableColumnDescription.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumnDescription.setCellFactory(tableColumn -> {
			TextFieldTableCell<FruTableInfo, String> cell = new TextFieldTableCell<FruTableInfo, String>();
			ChangeListener<String> infoListener = (obs, oldvalue, newvalue) -> {
				cell.setDisable(false);
				if (newvalue != null) {
					// set the new settings for the new cell;
					TableRow<FruTableInfo> row = cell.getTableRow();
					FruTableInfo item = row.getItem();
					if (item != null) {
						cell.setDisable(!((FruTableInfo)cell.getTableRow().getItem()).description_editable);
					}
				}
			};
			cell.itemProperty().addListener(infoListener);
			return cell ;
		});
		tableColumnDescription.setSortable(false);

		tableColumnType.setCellValueFactory(new PropertyValueFactory<>("type"));
		tableColumnType.setCellFactory(tableColumn -> {
			ChoiceBoxTableCell<FruTableInfo, String> cell = new ChoiceBoxTableCell<>(typeChoices);
			ChangeListener<String> infoListener = (obs, oldstr, newstr) -> {
				cell.setDisable(false);
				if (newstr != null) {
					// set the new settings for the new cell;
					TableRow<FruTableInfo> row = cell.getTableRow();
					FruTableInfo item = row.getItem();
					if (item != null) {
						cell.setDisable(!((FruTableInfo)cell.getTableRow().getItem()).type_editable);
					}
				}
			};
			cell.itemProperty().addListener(infoListener);
			return cell ;
		});
		tableColumnType.setSortable(false);

		tableColumnOEMType.setCellValueFactory(new PropertyValueFactory<>("oemType"));
		tableColumnOEMType.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumnOEMType.setCellFactory(tableColumn -> {
			TextFieldTableCell<FruTableInfo, String> cell = new TextFieldTableCell<FruTableInfo, String>();
			ChangeListener<String> infoListener = (obs, oldvalue, newvalue) -> {
				cell.setDisable(false);
				if (newvalue != null) {
					// set the new settings for the new cell;
					TableRow<FruTableInfo> row = cell.getTableRow();
					FruTableInfo item = row.getItem();
					if (item != null) {
						cell.setDisable(!((FruTableInfo)cell.getTableRow().getItem()).oemtype_editable);
					}
				}
			};
			cell.itemProperty().addListener(infoListener);
			return cell ;
		});
		tableColumnOEMType.setSortable(false);

		tableColumnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		tableColumnValue.setCellFactory(tableColumn -> {
			ValidatedTextFieldTableCell<FruTableInfo, String> cell = new ValidatedTextFieldTableCell<FruTableInfo, String>(decimalOnlyOperator);
			ChangeListener<String> infoListener = (obs, oldvalue, newvalue) -> {
				cell.setDisable(false);
				if (newvalue != null) {
					// set the new settings for the new cell;
					TableRow<FruTableInfo> row = cell.getTableRow();
					FruTableInfo item = row.getItem();
					if (item != null) {
						cell.setDisable(!((FruTableInfo)cell.getTableRow().getItem()).value_editable);
					}
				}
			};
			cell.itemProperty().addListener(infoListener);
			return cell ;
		});
		tableColumnValue.setSortable(false);

		tableColumnFormat.setCellValueFactory(new PropertyValueFactory<>("format"));
		//tableColumnFormat.setCellFactory(ChoiceBoxTableCell.forTableColumn(formatChoices));
		tableColumnFormat.setCellFactory(tableColumn -> {
			ChoiceBoxTableCell<FruTableInfo, String> cell = new ChoiceBoxTableCell<>(formatChoices);
			ChangeListener<String> infoListener = (obs, oldstr, newstr) -> {
				cell.setDisable(false);
				if (newstr != null) {
					// set the new settings for the new cell;
					TableRow<FruTableInfo> row = cell.getTableRow();
					FruTableInfo item = row.getItem();
					if (item != null) {
						cell.setDisable(!((FruTableInfo)cell.getTableRow().getItem()).format_editable);
					}
				}
			};
			cell.itemProperty().addListener(infoListener);
			return cell;
		});
		tableColumnFormat.setSortable(false);

		tableViewFruInfo.setEditable(true);

		// set a listener for when the tableview selection changes
		tableViewFruInfo.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> setDeleteButtonState());
	}

	/**
	 * addRow()
	 * Called when the add row button is pressed.  Add a row to the table
	 */
	@FXML private void addRow() {
		boolean isStandard = jsonFru.getInteger("vendorIANA") == DMTF_IANA;
		JsonArray fields = (JsonArray) jsonFru.get("fields");
		if (fields == null) return;

		// create a new fru entry based on whether or not it is a standard type
		JsonObject field = new JsonObject();
		if (isStandard) {
			field.put("type",new JsonValue("null"));
			field.put("required",new JsonValue("true"));
			field.put("description",new JsonValue("null"));
			field.put("format",new JsonValue("null"));
			field.put("length",new JsonValue("null"));
			field.put("value",new JsonValue("null"));
		} else {
			field.put("type",new JsonValue("null"));
			field.put("required",new JsonValue("true"));
			field.put("description",new JsonValue("null"));
			field.put("format",new JsonValue("null"));
			field.put("length",new JsonValue("null"));
			field.put("value",new JsonValue("null"));
		}
		fields.add(field);
		updateControls();

		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}

	/**
	 * deleteRow()
	 * This function is called when the delete row button is depressed.
	 * The function deletes the selected row from the table.
	 */
	@FXML private void deleteRow() {
		JsonArray fields = (JsonArray) jsonFru.get("fields");
		if (fields == null) return;

		// delete the selected row from the Json array
		SelectionModel<FruTableInfo> sm = tableViewFruInfo.getSelectionModel();
		if (sm.isEmpty()) return;  // there are no selected items

		int selected = sm.getSelectedIndex();

		fields.remove(selected);
		updateControls();

		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}

	/**
	 * typeChanged()
	 * The value of the type cell has changed - update the json type field and
	 * possibly the format field.  Since the type column will be hidden for OEM
	 * fru records, this function will only be called for standard records.
	 * @param e - a Cell Edit Event for the cell.
	 *
	 */
	@FXML private void typeChanged(TableColumn.CellEditEvent e) {
		int row = e.getTablePosition().getRow();

		// update the json field
		JsonArray fields = (JsonArray)jsonFru.get("fields");
		JsonObject field = (JsonObject)fields.get(row);

		// get the numeric index for the type and set the Json Value
		for (int i=0;i<typeChoices.length;i++) {
			if (e.getNewValue().equals(typeChoices[i])) {
				((JsonValue)(field.get("type"))).set(Integer.toString(i+1));
			}
		}

		if (e.getNewValue().equals("Manufacture Date")) {
			// if the type was set to "Manufacture Date", the format should be
			// changed to "timestamp104"
			((JsonValue)(field.get("format"))).set("timestamp104");
		} else if (e.getNewValue().equals("Vendor IANA")) {
			// if the type was set to "Vendor IANA", the format should be
			// changed to "uint32"
			((JsonValue)(field.get("format"))).set("uint32");
		} else {
			// otherwise the format field should be set to "string"
			((JsonValue) (field.get("format"))).set("string");
		}

		// clear any existing value
		((JsonValue) (field.get("value"))).set("null");

		updateControls();

		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}

	/**
	 * valueCellCommit()
	 * The value of the value cell has been committed - update the json value field
	 *
	 * @param e - a Cell Edit Event for the cell.
	 * TODO: validate the new value to make sure it matches the specified format
	 */
	@FXML private void valueCellCommit(TableColumn.CellEditEvent e) {
		int row = e.getTablePosition().getRow();

		// update the json field
		JsonArray fields = (JsonArray)jsonFru.get("fields");
		JsonObject field = (JsonObject)fields.get(row);

		((JsonValue)(field.get("value"))).set((String)e.getNewValue());
		updateControls();
		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}

	/**
	 * formatCellCommit()
	 * The value of the format cell has been committed - update the json value field
	 *
	 * @param e - a Cell Edit Event for the cell.
	 */
	@FXML private void formatCellCommit(TableColumn.CellEditEvent e) {
		int row = e.getTablePosition().getRow();

		// update the json field
		JsonArray fields = (JsonArray)jsonFru.get("fields");
		JsonObject field = (JsonObject)fields.get(row);

		((JsonValue)(field.get("format"))).set((String)e.getNewValue());
		((JsonValue)(field.get("value"))).set("");
		updateControls();

		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}

	/**
	 * oemTypeCommit()
	 * The value of the oem type cell has been committed - update the json type field
	 * @param e - a Cell Edit Event for the cell.
	 * TODO: validate the new value if it is an OEM type.
	 */
	@FXML private void oemTypeCommit(TableColumn.CellEditEvent e) {
		int row = e.getTablePosition().getRow();

		// update the json field
		JsonArray fields = (JsonArray)jsonFru.get("fields");
		JsonObject field = (JsonObject)fields.get(row);

		((JsonValue)(field.get("type"))).set((String)e.getNewValue());
		updateControls();

		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}

	/**
	 * setDeleteButtonState()
	 * set the state of the delete button to either enabled or disabled based on
	 * whether or not the fru record is specified in the configuration section of
	 * the input file.
	 **/
	private void setDeleteButtonState() {
		// disable the delete button by default
		buttonDelete.setDisable(true);

		// if the fru record is not in the capabilities section, it can be deleted
		if (jsonCapabilitiesFru==null) buttonDelete.setDisable(false);
	}

	/** updateControls()
	 * update the field controls based on the current configuration
	 * of the Json representation.
	 **/
	private void updateControls()
	{
		boolean isStandard = false;
		boolean isExtensible = (jsonCapabilitiesFru==null);

		// create the choices for the type
		choiceBoxFruType.getItems().clear();
		choiceBoxFruType.getItems().add("not selected");
		choiceBoxFruType.getItems().add("standard");
		choiceBoxFruType.getItems().add("oem");

		// select the current choice
		// oem fru record
		if (jsonFru.getInteger("vendorIANA") == DMTF_IANA) { // standard
			choiceBoxFruType.getSelectionModel().select(1);
			isStandard = true;
		} else {
			choiceBoxFruType.getSelectionModel().select(2);
		}

		// disable this control in order to prevent changes.
		choiceBoxFruType.setDisable(true);

		// update the vendor id field
		textFieldOemId.setDisable(false);
		if (jsonFru.getValue("vendorIANA")!=null) {
			textFieldOemId.setText(jsonFru.getValue("vendorIANA"));

			// if the capabilities value for this field has been set or the
			// fru record type is standard, disable this field
			if ((isStandard) ||
					((jsonCapabilitiesFru!=null)&&
							(jsonCapabilitiesFru.getValue("vendorIANA")!=null))) {
				textFieldOemId.setDisable(true);
			}
		} else {
			textFieldOemId.clear();
		}

		// update the tableview
		tableViewFruInfo.getItems().clear();
		JsonArray fruFields = (JsonArray)jsonFru.get("fields");
		for (int fruidx = 0; fruidx<fruFields.size(); fruidx++) {
			JsonObject fruField = (JsonObject) fruFields.get(fruidx);

			// get a reference to the corresponding fru record from the
			// device's capabilities section (if the fru record exists).
			JsonObject fruCapField = null;
			if (jsonCapabilitiesFru!=null) {
				if (jsonCapabilitiesFru.containsKey("fields")) {
					JsonArray fruCapFields = (JsonArray)jsonCapabilitiesFru.get("fields");
					if (fruidx<fruCapFields.size()) {
						fruCapField = (JsonObject) fruCapFields.get(fruidx);
					}
				}
			}

			// create a new FruTableInfo object associated with this fru record
			FruTableInfo info = new FruTableInfo();

			// by default, every field is editable
			info.description_editable = true;
			info.type_editable = true;
			info.value_editable = true;
			info.format_editable = true;
			info.oemtype_editable = true;

			// if the field is defined in the capabilities fru record, it is immutable
			if (fruCapField!=null) {
				info.description_editable = (fruCapField.getValue("description") == null);
				info.type_editable = (fruCapField.getValue("type") == null);
				info.value_editable = (fruCapField.getValue("value") == null);
				info.format_editable = (fruCapField.getValue("format") == null);
				info.oemtype_editable = (fruCapField.getValue("type") == null);
			}
			if (isStandard) info.format_editable = false;

			// set field values
			if (fruField.getValue("description") != null) {
				info.setDescription(fruField.getValue("description"));
			} else {
				info.setDescription("");
			}

			if (fruField.getValue("format") != null) {
				info.setFormat(fruField.getValue("format"));
			} else {
				info.setFormat("");
			}

			if (fruField.getValue("type") != null) {
				if (isStandard) {
					int typeNum = fruField.getInteger("type");
					if ((typeNum > typeChoices.length) || (typeNum < 1)) typeNum = 1;
					info.setType(typeChoices[typeNum - 1]);
					info.setOemType("");
				} else {
					info.setType("OEM");
					info.setOemType(fruField.getValue("type"));
				}
			} else {
				// not yet defined
				if (isStandard) {
					info.setType("");
				} else {
					info.setType("OEM");
					info.setOemType("");
				}
			}

			if (fruField.getValue("value") != null) {
				info.setValue(fruField.getValue("value"));
			} else {
				info.setValue("");
			}

			tableViewFruInfo.getItems().add(info);
		}

		// now hide columns based on FRU record type
		tableColumnOEMType.setVisible(true);
		tableColumnType.setVisible(false);
		if (isStandard) {
			tableColumnOEMType.setVisible(false);
			tableColumnType.setVisible(true);
		}

		// Refresh the table so that all the changes take place
		tableViewFruInfo.refresh();

		// refresh the main screen tree view errors (if any)
		if (mainController!=null) mainController.errorCheck();

		// disable the "add" button if the fru record is not extendable
		buttonAdd.setDisable(!isExtensible);
		buttonDelete.setDisable(true);
	}

	/** update()
	 * This function is called when the pane is changed from the main menu.
	 * It updates all the controls based on the java objects that it corresponds to.
	 * @param fru the json object for this fru record (from the configuration section of the device)
	 * @param capabilitiesFru the json object for the corresponding fru record in the
	 *                        capabilities section of the input file
	 */
	public void update( TreeItem<MainScreenController.TreeData> selectedNode, JsonObject fru, JsonObject capabilitiesFru)
	{
		// do any configuration required prior to making the pane visible
		jsonFru = fru;
		jsonCapabilitiesFru = capabilitiesFru;
		this.selectedNode = selectedNode;

		updateControls();
		// signal a change to the tree node
		selectedNode.getValue().error.set(false);
		selectedNode.getValue().error.set(Device.isFruRecordValid((JsonObject)selectedNode.getValue().leaf));
	}
}