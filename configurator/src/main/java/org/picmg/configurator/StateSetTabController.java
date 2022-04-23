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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.picmg.jsonreader.*;

import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StateSetTabController implements Initializable {
	@FXML private TableView<StateSetTableData> stateSetTableView;
	@FXML private TableColumn<StateSetTableData, String> manufacturerColumn;
	@FXML private TableColumn<StateSetTableData, String> modelColumn;
	@FXML private TableColumn<StateSetTableData, String> typeColumn;
	@FXML private TableView<ValueRecords> stateSetValueRecords;
	@FXML private TableColumn<ValueRecords, String> minStateValue;
	@FXML private TableColumn<ValueRecords, String> maxStateValue;
	@FXML private TableColumn<ValueRecords, String> languageTags;
	@FXML private TableColumn<ValueRecords, String> stateName;
	@FXML private TextField stateSetId;
	@FXML private TextField stateSetNameTextField;
	@FXML private TextField stateSetVendorTextField;
	@FXML private TextField stateSetVendorIANA;
	@FXML private Button saveChangesButton;
	@FXML private Button saveAsChangesButton;
	@FXML private ImageView vendorNameImage;
	@FXML private ImageView oemStateSetValueRecordImage;
	@FXML private ImageView stateSetIDImage;
	@FXML private ImageView vendorIANAImage;
	@FXML private ImageView stateSetValueRecordImage;
	@FXML private ImageView auxUnitImage;
	@FXML private ImageView minusAccuracyImage;
	@FXML private ImageView outputCurveImage;
	@FXML private ImageView outputUnitsImage;
	@FXML private ImageView plusAccuracyImage;
	@FXML private TableColumn<StateSetTableData, String> vendorNameColumn;
	@FXML private TableColumn<StateSetTableData, String> vendorIANAColumn;
	@FXML private TableColumn<StateSetTableData, String> stateSetIDColumn;

	//TODO:change to use state sensor data class
	//SensorTableData workingData = new SensorTableData();

	//	public SensorTableData getSensorTableData() {
	//		return workingData;
	//	}
	public class StateSetTableData{
		SimpleStringProperty name = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorName = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorIANA = new SimpleStringProperty();
		SimpleStringProperty stateSetId = new SimpleStringProperty();
		List<OEMStateValueRecord> oemStateValueRecords = new LinkedList<>();
		Path savePath = null;

		boolean valid;
		public StateSetTableData(Path path) {
			savePath = path;
			JsonAbstractValue json = new JsonResultFactory().buildFromFile(path);
			populate((JsonObject)json);
		}

		private final boolean validate(JsonObject json) {
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
			valid = validate(json);
			if (!valid) return;

			name.set(json.getValue("name"));
			stateSetVendorName.set(json.getValue("vendorName"));
			stateSetVendorIANA.set(json.getValue("vendorIANA"));
			stateSetId.set(json.getValue("stateSetId"));

			JsonArray states = (JsonArray)json.get("oemStateValueRecords");
			for (JsonAbstractValue jsonAbstractValue : states) {
				if (!jsonAbstractValue.getClass().isAssignableFrom(JsonObject.class)) continue;
				JsonObject state = (JsonObject) jsonAbstractValue;
				state.dump(4);
				oemStateValueRecords.add(new OEMStateValueRecord(state));
			}
		}

		public StateSetTableData() {
			valid = false;
		}

		public List<OEMStateValueRecord> getOemStateValueRecords() {
			return oemStateValueRecords;
		}

		public void setOemStateValueRecords(List<OEMStateValueRecord> oemStateValueRecords) {
			this.oemStateValueRecords = oemStateValueRecords;
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

		public String getStateSetVendorName(String stateSetVendorName) {
			return this.stateSetVendorName.get();
		}

		public void setStateSetVendorName(String stateSetVendorName) {
			this.stateSetVendorName.set(stateSetVendorName);
		}

	}
	public class ValueRecords{

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
		//TODO: fill out with needed initialize
		vendorNameColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorName"));
		vendorIANAColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorIANA"));
		stateSetIDColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetId"));

		initializeTable();
		selectDefaultStateSet();
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

	private void selectDefaultStateSet() {
	}
}