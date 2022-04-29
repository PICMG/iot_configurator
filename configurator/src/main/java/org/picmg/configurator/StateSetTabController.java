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

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
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
import java.nio.file.Path;
import java.util.*;
import java.util.function.UnaryOperator;

public class StateSetTabController implements Initializable {
	@FXML private TableView<StateSetTableData> stateSetTableView;

	@FXML private TextField stateSetVendorNameTextField;
	@FXML private TextField stateSetVendorIANATextField;
	@FXML private TextField stateSetIdTextField;
	@FXML private TableView<OEMStateValueRecord> stateSetValueRecords;
	@FXML private TableColumn<OEMStateValueRecord, String> stateNameColumn;

	@FXML private Button saveChangesButton;
	@FXML private Button saveAsChangesButton;

	@FXML private ImageView vendorNameImage;
	@FXML private ImageView oemValueRecordImage;
	@FXML private ImageView stateSetIDImage;
	@FXML private ImageView vendorIANAImage;
	@FXML private TableColumn<StateSetTableData, String> vendorNameColumn;
	@FXML private TableColumn<StateSetTableData, String> vendorIANAColumn;
	@FXML private TableColumn<StateSetTableData, String> stateSetIDColumn;
	@FXML private TextField addStateTextField;

	StateSetTabController.StateSetTableData workingData = new StateSetTableData();

	UnaryOperator<TextFormatter.Change> valueRecordOperator = change -> {
		int selectedRow = stateSetValueRecords.getSelectionModel().getSelectedIndex();
		OEMStateValueRecord info = stateSetValueRecords.getSelectionModel().getSelectedItem();

		if (info!=null) {
			modified = true;
			return change;
		}
		// make no change
		change.setText("");
		change.setRange( change.getRangeStart(),change.getRangeStart());
		return change;
	};

	/**
	 * Applies a new index for the min and max states on adding/removing.
	 * @param stateNames
	 */
	private static void resetStateValues(List<OEMStateValueRecord> stateNames) {
		int index = 0;
		for (OEMStateValueRecord stateName : stateNames) {
			stateName.setMinStateValue(index);
			stateName.setMaxStateValue(index);
			index++;
		}
	}

	boolean modified = false;

	public StateSetTableData getWorkingData() {
		return workingData;
	}

	public class StateSetTableData{
		SimpleStringProperty stateSetVendorName = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorIANA = new SimpleStringProperty();
		SimpleStringProperty stateSetId = new SimpleStringProperty();
		ObservableList<OEMStateValueRecord> oemStateValueRecords = FXCollections.emptyObservableList();
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
				valueRecords.add(oemStateValueRecords.get(i));
			}
			return new StateSet(this.getStateSetName(), Integer.parseInt(this.getStateSetId()), this.getStateSetVendorName(), Integer.parseInt(this.getStateSetVendorIANA()), valueRecords);
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

			JsonArray stateObjs = (JsonArray)json.get("oemStateValueRecords");
			List<OEMStateValueRecord> states = new LinkedList<>();
			for (JsonAbstractValue jsonAbstractValue : stateObjs) {
				if (!jsonAbstractValue.getClass().isAssignableFrom(JsonObject.class)) continue;
				JsonObject state = (JsonObject) jsonAbstractValue;
				states.add(new OEMStateValueRecord(state));
			}
			setOemStateValueRecords(states);
		}

		public StateSetTableData() {
			valid = false;
		}

		public ObservableList<OEMStateValueRecord> getOemStateValueRecords() {
			return oemStateValueRecords;
		}

		public void setOemStateValueRecords(List<OEMStateValueRecord> records) {
			this.oemStateValueRecords = FXCollections.observableList(records);
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
			setSavePath(selectedData.getSavePath());

			setOemStateValueRecords(selectedData.getOemStateValueRecords());
		}

		public void addState(OEMStateValueRecord record) {
			oemStateValueRecords.add(record);
			resetStateValues(oemStateValueRecords);
		}

		public void removeState(OEMStateValueRecord record) {
			// add default state if
			if (!oemStateValueRecords.contains(record)) return;
			if (oemStateValueRecords.size() == 1) {
				addState(new OEMStateValueRecord("Default"));
			}
			oemStateValueRecords.remove(record);

			resetStateValues(oemStateValueRecords);
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
		updateCell((OEMStateValueRecord) e.getRowValue(), (String) e.getNewValue());
	}

	private void updateCell(OEMStateValueRecord row, String newValue) {
		// get item from selected row, manually set OEMStateValueRecord name
		if (row != null) {
			if ("".equals(newValue)) {
				Platform.runLater(() -> getWorkingData().removeState(row));
			} else {
				row.setStateName(newValue);
			}
			modified = true;
		}
		refreshSave();
	}

	@FXML
	public void onAddStateAction(ActionEvent actionEvent) {
		String stateSetName = addStateTextField.getText();
		if("".equals(stateSetName)) return;
		OEMStateValueRecord record = new OEMStateValueRecord(stateSetName);
		getWorkingData().addState(record);
//		updateCell(record, stateSetName);
		modified = true;
		refreshSave();
	}

	@FXML
	void onStateSetVendorNameAction(ActionEvent event) {
		workingData.setStateSetVendorName(stateSetVendorNameTextField.getText());
		modified = true;
		refreshSave();
	}
	@FXML
	void onStateSetVendorIANAAction(ActionEvent event) {
		workingData.setStateSetVendorIANA(stateSetVendorIANATextField.getText());
		modified = true;
		refreshSave();
	}
	@FXML
	void onStateSetIDAction(ActionEvent event) {
		workingData.setStateSetId(stateSetIdTextField.getText());
		modified = true;
		refreshSave();
	}

    @FXML
    void onSaveChangesAction(ActionEvent event) {
        String fileName = workingData.getStateSetVendorName() + '_' + workingData.getStateSetId();
        File defaultPath = (workingData.getSavePath() != null)
                ? workingData.getSavePath().toFile()
                : new File(System.getProperty("user.dir")+"/lib/state_sets/" + fileName+".json");
        saveToFile(workingData.getStateSet(), defaultPath.toString());
		modified = false;
		refreshSave();
    }

    @FXML
    void onSaveAsChangesAction(ActionEvent event) {
        File path = promptSavePath();
        if (path == null) {
            return;
        }
        workingData.setSavePath(path.toPath());
        saveToFile(workingData.getStateSet(), path.toString());
		modified = false;
		refreshSave();
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
		if(this.getWorkingData().getOemStateValueRecords() != null && this.getWorkingData().getOemStateValueRecords().size() == 0) return false;
		return true;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vendorNameColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorName"));
		vendorIANAColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetVendorIANA"));
		stateSetIDColumn.setCellValueFactory(new PropertyValueFactory<>("stateSetId"));
		stateNameColumn.setCellValueFactory(new PropertyValueFactory<>("stateName"));
		stateNameColumn.setCellFactory(ValidatedTextFieldTableCell.forTableColumn(valueRecordOperator));

		initializeTable();
		selectDefaultStateSet();
		refreshSave();

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
		refreshSave();

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

				modified = false;
				refreshSave();
			}
		});
	}

	private void refreshSave() {
		saveChangesButton.setDisable(!modified || !isValid() || getWorkingData().getSavePath() == null);
		saveAsChangesButton.setDisable(!isValid());
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
		stateSetValueRecords.setItems(FXCollections.observableList(data.getOemStateValueRecords()));
		refreshSave();
	}

	private void selectDefaultStateSet() {
		stateSetTableView.getSelectionModel().select(0);
		modified = false;
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
