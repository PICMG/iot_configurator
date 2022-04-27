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
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.picmg.jsonreader.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Path;

public class StateSetTabController implements Initializable {
	@FXML private TableView<StateSetTableData> stateSetTableView;
	@FXML private TextField stateSetVendorNameTextField;
	@FXML private TextField stateSetVendorIANATextField;
	@FXML private TextField stateSetIdTextField;
	@FXML private TableView<OEMStateValueRecord> stateSetValueRecords;
	@FXML private TableColumn<OEMStateValueRecord, String> stateName;

	@FXML private Button saveChangesButton;
	@FXML private Button saveAsChangesButton;

	@FXML private ImageView vendorNameImage;
	@FXML private ImageView oemValueRecordImage;
	@FXML private ImageView stateSetIDImage;
	@FXML private ImageView vendorIANAImage;
	@FXML private TableColumn<StateSetTableData, String> vendorNameColumn;
	@FXML private TableColumn<StateSetTableData, String> vendorIANAColumn;
	@FXML private TableColumn<StateSetTableData, String> stateSetIDColumn;

	StateSetTabController.StateSetTableData workingData = new StateSetTableData();

	public StateSetTableData getWorkingData() {
		return workingData;
	}

	public class StateSetTableData{
		SimpleStringProperty stateSetVendorName = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorIANA = new SimpleStringProperty();
		SimpleStringProperty stateSetId = new SimpleStringProperty();
		SimpleListProperty oemStateValueRecords = new SimpleListProperty();
		Path savePath = null;

		boolean valid;
		public StateSetTableData(Path path) {
			savePath = path;
			JsonAbstractValue json = new JsonResultFactory().buildFromFile(path);
			valid = isValid((JsonObject)json);
			if (valid) populate((JsonObject)json);
		}

		public String getStateSetName() {
			return stateSetId.getName();
		}

		public StateSet getStateSet() {
			ArrayList<OEMStateValueRecord> valueRecords = new ArrayList<>();
			for (int i = 0; i < oemStateValueRecords.size(); i++) {
				valueRecords.add((OEMStateValueRecord) oemStateValueRecords.get(i));
			}
			return new StateSet(this.getStateSetName(), Integer.valueOf(this.getStateSetId()), this.getStateSetVendorName(), Integer.valueOf(this.getStateSetVendorIANA()), valueRecords);
		}

    	public void setSavePath(Path savePath) {this.savePath = savePath;}

        public Path getSavePath() {return savePath;}

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
			stateSetVendorName.set(json.getValue("vendorName"));
			stateSetVendorIANA.set(json.getValue("vendorIANA"));
			stateSetId.set(json.getValue("stateSetId"));

			JsonArray states = (JsonArray)json.get("oemStateValueRecords");
			List<OEMStateValueRecord> list = new LinkedList<>();
			for (JsonAbstractValue jsonAbstractValue : states) {
				if (!jsonAbstractValue.getClass().isAssignableFrom(JsonObject.class)) continue;
				JsonObject state = (JsonObject) jsonAbstractValue;
				list.add(new OEMStateValueRecord(state));
			}
			oemStateValueRecords.set(FXCollections.observableList(list));
		}

		public StateSetTableData() {
			valid = false;
		}

		public ObservableList<OEMStateValueRecord> getOemStateValueRecords() {
			return oemStateValueRecords.get();
		}

		public void setOemStateValueRecords(ObservableList<OEMStateValueRecord> oemStateValueRecords) {
			this.oemStateValueRecords.set(oemStateValueRecords);
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
			oemStateValueRecords.set(selectedData.getOemStateValueRecords());
		}
	}

	@FXML
	void onStateSetVendorNameAction(ActionEvent event) {
		workingData.setStateSetVendorName(stateSetVendorNameTextField.getText());
		saveChangesButton.setDisable(!isValid() || workingData.getSavePath() == null);
		saveAsChangesButton.setDisable(!isValid());
	}
	@FXML
	void onStateSetVendorIANAAction(ActionEvent event) {
		workingData.setStateSetVendorIANA(stateSetVendorIANATextField.getText());;
		saveChangesButton.setDisable(!isValid() || workingData.getSavePath() == null);
		saveAsChangesButton.setDisable(!isValid());
	}
	@FXML
	void onStateSetIDAction(ActionEvent event) {
		workingData.setStateSetId(stateSetIdTextField.getText());;
		saveChangesButton.setDisable(!isValid() || workingData.getSavePath() == null);
		saveAsChangesButton.setDisable(!isValid());
	}

    @FXML
    void onSaveChangesAction(ActionEvent event) {
        String fileName = workingData.getStateSetVendorName() + '_' + workingData.getStateSetId();
        File defaultPath = (workingData.getSavePath() != null)
                ? workingData.getSavePath().toFile()
                : new File(System.getProperty("user.dir")+"/lib/state_sets/" + fileName+".json");
        saveToFile(workingData.getStateSet(), defaultPath.toString());
    }

    @FXML
    void onSaveAsChangesAction(ActionEvent event) {
        File path = promptSavePath();
        if (path == null) {
            return;
        }
        workingData.setSavePath(path.toPath());
        saveToFile(workingData.getStateSet(), path.toString());
    }

    private File promptSavePath() {
        File defaultPath = (workingData.getSavePath() != null)
                ? workingData.getSavePath().toFile()
                : new File(System.getProperty("user.dir")+"/lib/state_sets/" + workingData.getStateSetVendorName() + '_' + workingData.getStateSetId()+".json");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        fileChooser.setTitle("Save As");
        fileChooser.setInitialDirectory(defaultPath.getParentFile());
        fileChooser.setInitialFileName(workingData.getStateSetVendorName() + '_' + workingData.getStateSetId() + ".json");
        File result = fileChooser.showSaveDialog(saveChangesButton.getScene().getWindow());
        if (result == null) {
            return null;
        }
        if (result.canWrite()) {
            System.out.println("Unable to save to readonly file.");
            return null;
        }
        return result;
    }

	public boolean isValid() {
		if(vendorNameImage.isVisible()) return  false;
		if(vendorIANAImage.isVisible()) return  false;
		if(stateSetIDImage.isVisible()) return  false;
		if(this.getWorkingData().getOemStateValueRecords() != null || this.getWorkingData().getOemStateValueRecords().size() == 0) return false;
		return true;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vendorNameColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorName"));
		vendorIANAColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorIANA"));
		stateSetIDColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetId"));
		stateName.setCellValueFactory(new PropertyValueFactory<>("stateName"));

		initializeTable();
		selectDefaultStateSet();

		// fire action events if focus is lost on our text fields - this allows the normal action handler
		// to update and check values.
		stateSetVendorNameTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { stateSetVendorNameTextField.fireEvent(new ActionEvent()); }}});

		stateSetVendorIANATextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { stateSetVendorNameTextField.fireEvent(new ActionEvent()); }}});

		stateSetIdTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { stateSetIdTextField.fireEvent(new ActionEvent()); }}});


		// bind images to their input constraints
		vendorNameImage.visibleProperty().bind(Bindings.createBooleanBinding(() ->
						stateSetVendorNameTextField.textProperty().getValueSafe().isBlank(),
				stateSetVendorNameTextField.textProperty()));

		vendorIANAImage.visibleProperty().bind(Bindings.createBooleanBinding(() ->
						stateSetVendorIANATextField.textProperty().getValueSafe().isBlank() ||  !App.isInteger(stateSetVendorIANATextField.textProperty().getValueSafe()),
				stateSetVendorIANATextField.textProperty()));

		stateSetIDImage.visibleProperty().bind(Bindings.createBooleanBinding(() ->
						stateSetIdTextField.textProperty().getValueSafe().isBlank() || !App.isInteger(stateSetIdTextField.textProperty().getValueSafe()),
				stateSetIdTextField.textProperty()));
		// TODO visibility should update on changes to list of given working data also
		oemValueRecordImage.visibleProperty().bind(Bindings.createBooleanBinding(() -> getWorkingData().oemStateValueRecords.isEmpty(),
				stateSetTableView.getSelectionModel().selectedItemProperty()));

		ObservableList<StateSetTableData> tableSelection = stateSetTableView.getSelectionModel().getSelectedItems();
		tableSelection.addListener(new ListChangeListener<StateSetTableData>() {
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
		stateSetVendorNameTextField.setText(data.getStateSetVendorName());
		stateSetVendorIANATextField.setText(data.getStateSetVendorIANA());
		stateSetIdTextField.setText(data.getStateSetId());

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

	public void saveToFile(StateSet stateSet, String path) {
		try {
			FileWriter fileWriter;
			fileWriter = new FileWriter(path);
			BufferedWriter br = new BufferedWriter(fileWriter);
			stateSet.toJSON().writeToFile(br);
			br.close();
		} catch (IOException e) {
			System.out.println("IOException occurred while writing to file");
			e.printStackTrace();
		}
	}
}
