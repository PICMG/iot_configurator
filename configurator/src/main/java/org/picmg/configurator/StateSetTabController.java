//*******************************************************************
//    SensorsTabController.java
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import org.picmg.jsonreader.*;

import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.UnaryOperator;

public class StateSetTabController implements Initializable {
	@FXML private TableView<StateSetTableData> stateSetTableView;
	@FXML private TableColumn<StateSetTableData, String> vendorName;
	@FXML private TableColumn<StateSetTableData, String> vendorIANA;
	@FXML private TableColumn<StateSetTableData, String> stateSetID;

	@FXML private TableView<ValueRecord> stateSetValueRecords;
	@FXML private TableColumn<ValueRecord, String> stateName;

	@FXML private TextField stateSetId;
	@FXML private TextField stateSetVendorNameTextfield;
	@FXML private TextField stateSetVendorIANA;

	@FXML private Button saveChangesButton;
	@FXML private Button saveAsChangesButton;

	@FXML private ImageView vendorNameImage;
	@FXML private ImageView oemStateSetValueRecordImage;
	@FXML private ImageView stateSetIDImage;
	@FXML private ImageView vendorIANAImage;
	@FXML private TableColumn<StateSetTableData, String> vendorNameColumn;
	@FXML private TableColumn<StateSetTableData, String> vendorIANAColumn;
	@FXML private TableColumn<StateSetTableData, String> stateSetIDColumn;

	//TODO:change to use state sensor data class
	StateSetTableData workingData = new StateSetTableData();

	UnaryOperator<TextFormatter.Change> decimalOnlyOperator = change -> {
		int selectedRow = stateSetValueRecords.getSelectionModel().getSelectedIndex();
		ValueRecord info = stateSetValueRecords.getSelectionModel().getSelectedItem();

		if (info!=null) {
			// change is allowed
			System.out.println("Value Record=" + info.toString());
			return change;
		}
		System.out.println("Value Record =  null");
		// make no change
		change.setText("");
		change.setRange( change.getRangeStart(),change.getRangeStart());
		return change;
	};

	private final static String VALUE_PLACEHOLDER = "Insert";

	public interface ValueRecord {
		String getStateName();
		void setStateName(String stateName);
	}

	public class EmptyValueRecord implements ValueRecord {
		public SimpleStringProperty stateName = new SimpleStringProperty(VALUE_PLACEHOLDER);
		@Override public String getStateName() { return VALUE_PLACEHOLDER; }
		@Override public void setStateName(String stateName) {
			System.out.println("set the state name " + stateName);
			// add an state value record
//			List list = getStateSetTableData().getOemStateValueRecords();
//			list.add(new OEMStateValueRecord(-1, -1, "en", stateName));
//			getStateSetTableData().setOemStateValueRecords(list);
		}
	}

	public static EmptyValueRecord EMPTY_STATE_NAME;


	public StateSetTableData getStateSetTableData() {
		return workingData;
	}
	public class StateSetTableData{
		SimpleStringProperty name = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorName = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorIANA = new SimpleStringProperty();
		SimpleStringProperty stateSetId = new SimpleStringProperty();
		List<ValueRecord> oemStateValueRecords = new LinkedList<>();
		Path savePath = null;

		boolean valid;
		public StateSetTableData(Path path) {
			savePath = path;
			JsonAbstractValue json = new JsonResultFactory().buildFromFile(path);
			valid = isValid((JsonObject)json);
			if (valid) populate((JsonObject)json);
		}

		private static final boolean isValid(JsonObject json) {
			if (json == null) return false;
			if (json.get("name") == null
					|| !json.get("name").getClass().isAssignableFrom(JsonValue.class)) return false;
			if (json.get("stateSetId") == null
					|| !json.get("stateSetId").getClass().isAssignableFrom(JsonValue.class)
					|| !App.isUnsignedInteger(json.getValue("stateSetId"))) return false;
			if (json.get("vendorIANA") == null
					|| !json.get("vendorIANA").getClass().isAssignableFrom(JsonValue.class)
					|| !App.isUnsignedInteger(json.getValue("vendorIANA"))) return false;
			if (json.get("vendorName") == null
					|| !json.get("vendorName").getClass().isAssignableFrom(JsonValue.class)) return false;
			if (json.get("oemStateValueRecords") == null
					|| !json.get("oemStateValueRecords").getClass().isAssignableFrom(JsonArray.class)) return false;

			JsonArray states = (JsonArray) json.get("oemStateValueRecords");
			// verify that oemStateValueRecords has at least one object
			if (states.isEmpty()) return false;
			// verify the values in each state record
			for (JsonAbstractValue state : states) {
				JsonObject stateObj = (JsonObject) state;
				if (stateObj.get("minStateValue") == null
						|| !stateObj.get("minStateValue").getClass().isAssignableFrom(JsonValue.class)
						|| !App.isUnsignedInteger(stateObj.getValue("minStateValue"))) return false;
				if (stateObj.get("maxStateValue") == null
						|| !stateObj.get("maxStateValue").getClass().isAssignableFrom(JsonValue.class)
						|| !App.isUnsignedInteger(stateObj.getValue("maxStateValue"))) return false;
				if (stateObj.get("languageTags") == null
						|| !stateObj.get("languageTags").getClass().isAssignableFrom(JsonArray.class)
						|| "".equals(stateObj.get("languageTags").getValue("0."))) return false;
				if (stateObj.get("stateName") == null
						|| !stateObj.get("stateName").getClass().isAssignableFrom(JsonArray.class)
						|| "".equals(stateObj.get("stateName").getValue("0."))) return false;
			}
			return true;
		}

		private final void populate(JsonObject json) {
			name.set(json.getValue("name"));
			stateSetVendorName.set(json.getValue("vendorName"));
			stateSetVendorIANA.set(json.getValue("vendorIANA"));
			stateSetId.set(json.getValue("stateSetId"));

			JsonArray states = (JsonArray)json.get("oemStateValueRecords");
			for (JsonAbstractValue jsonAbstractValue : states) {
				if (!jsonAbstractValue.getClass().isAssignableFrom(JsonObject.class)) continue;
				JsonObject state = (JsonObject) jsonAbstractValue;
				oemStateValueRecords.add(new OEMStateValueRecord(state));
			}
			addEmptyStateName();
		}

		public StateSetTableData() {
			valid = false;
		}

		public List<ValueRecord> getOemStateValueRecords() {
			return oemStateValueRecords;
		}

		public void setOemStateValueRecords(List<ValueRecord> oemStateValueRecords) {
			this.oemStateValueRecords.clear();
			this.oemStateValueRecords.addAll(oemStateValueRecords);
			addEmptyStateName();
		}

		public String getStateSetId() {
			return stateSetId.get();
		}

		public void setStateSetId(String stateSetId) {
			this.stateSetId.set(stateSetId);
		}

		public void setStateSetVendorIANA(String stateSetVendorIANA) {
			this.stateSetVendorIANA.set(stateSetVendorIANA);
		}

		public String getStateSetVendorIANA() {
			return this.stateSetVendorIANA.get();
		}

		public String getStateSetVendorName() {
			return this.stateSetVendorName.get();
		}

		public void setStateSetVendorName(String stateSetVendorName) {
			this.stateSetVendorName.set(stateSetVendorName);
		}

		public void set(StateSetTableData selectedData) {
			setStateSetVendorName(selectedData.getStateSetVendorName());
			setStateSetVendorIANA(selectedData.getStateSetVendorIANA());
			setStateSetId(selectedData.getStateSetId());

			oemStateValueRecords.clear();
			oemStateValueRecords.addAll(selectedData.getOemStateValueRecords());
			addEmptyStateName();
		}

		private void addEmptyStateName() {
			if (oemStateValueRecords.contains(EMPTY_STATE_NAME)) {
				oemStateValueRecords.add(EMPTY_STATE_NAME);
			}
		}
	}

	/**
	 * valueCellCommit()
	 * The value of the value cell has been committed - update the json value field
	 *
	 * @param e - a Cell Edit Event for the cell.
	 * TODO: validate the new value to make sure it matches the specified format
	 */
	@FXML private void valueCellCommit(TableColumn.CellEditEvent e) {
		// get item from selected row, manually set ValueRecord name
		if (e.getRowValue() != null && e.getRowValue() instanceof ValueRecord) {
			String value = (String) e.getNewValue();
			((ValueRecord) e.getRowValue()).setStateName(value);
		}
	}

	@FXML
	void onStateSetVendorNameAction(ActionEvent event) {
		//TODO: finish
	}
	@FXML
	void onStateSetVendorIANAAction(ActionEvent event) {
		//TODO: finish
	}
	@FXML
	void onStateSetIDAction(ActionEvent event) {
		//TODO: finish
	}
	@FXML
	void onSaveChangesAction(ActionEvent event) {
		//TODO: finish
	}
	@FXML
	void onSaveAsChangesAction(ActionEvent event) {
		//TODO: finish
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vendorNameColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorName"));
		vendorIANAColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorIANA"));
		stateSetIDColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetId"));
		stateName.setCellValueFactory(new PropertyValueFactory<>("stateName"));
		stateName.setCellFactory(TextFieldTableCell.forTableColumn());
//		stateName.setCellFactory(ValidatedTextFieldTableCell.forTableColumn(change -> change));
		stateSetValueRecords.setEditable(true);

		initializeTable();
		selectDefaultStateSet();

		ObservableList<StateSetTabController.StateSetTableData> tableSelection = stateSetTableView.getSelectionModel().getSelectedItems();
		tableSelection.addListener(new ListChangeListener<StateSetTabController.StateSetTableData>() {
			@Override
			public void onChanged(Change<? extends StateSetTabController.StateSetTableData> c) {
				// here if a new selection has been made from the table - populate the
				// controls with the data
				StateSetTabController.StateSetTableData data = stateSetTableView.getSelectionModel().getSelectedItem();
				if (data==null) return;
				workingData.set(data);
				setStateSetData(workingData);

//				modified = false;
//				setSaveAvailability(false);
			}
		});
	}

	private void initializeTable() {
		stateSetTableView.getItems().clear();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(System.getProperty("user.dir")+"/lib/state_sets"))) {
			for (Path path : stream) {
				if (!Files.isDirectory(path)) {
					StateSetTableData data = new StateSetTableData(path);
					if (data.valid) {
						// place the data in the table
						stateSetTableView.getItems().add(data);
					}
				}
			}
		} catch (IOException e) {
			// unable to find the directory
		}
	}

	private void setStateSetData(StateSetTableData data) {
		stateSetVendorNameTextfield.setText(data.getStateSetVendorName());
		stateSetVendorIANA.setText(data.getStateSetVendorIANA());
		stateSetId.setText(data.getStateSetId());

		stateSetValueRecords.getItems().clear();
		stateSetValueRecords.getItems().addAll(data.getOemStateValueRecords());
	}

	private void selectDefaultStateSet() {
		stateSetTableView.getSelectionModel().select(0);
//		setSaveAvailability(false);
//		modified = false;
		StateSetTabController.StateSetTableData selectedData = stateSetTableView.getSelectionModel().getSelectedItem();
		if (selectedData == null) return;
		workingData.set(selectedData);
		setStateSetData(selectedData);
	}
}
