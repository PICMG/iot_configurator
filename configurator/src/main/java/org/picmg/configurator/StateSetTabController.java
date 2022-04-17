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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

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
	@FXML private TextField stateSetVendorNameTextField;
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

	StateSetTabController.StateSetTableData workingData = new StateSetTableData();
	boolean modified;

	public StateSetTableData getWorkingData() {
		return workingData;
	}


	//TODO:change to use state sensor data class
	//SensorTableData workingData = new SensorTableData();

	//	public SensorTableData getSensorTableData() {
	//		return workingData;
	//	}
	public class StateSetTableData{
		SimpleStringProperty stateSetVendorName = new SimpleStringProperty();
		SimpleStringProperty stateSetVendorIANA = new SimpleStringProperty();
		SimpleStringProperty stateSetId = new SimpleStringProperty();
		List<OEMStateValueRecord> oemStateValueRecords = new LinkedList<>();

		public List<OEMStateValueRecord> getOemStateValueRecords() {
			return oemStateValueRecords;
		}

		public void setOemStateValueRecords(List<OEMStateValueRecord> oemStateValueRecords) {
			this.oemStateValueRecords = oemStateValueRecords;
		}

		public String getStateSetId() {
			return stateSetId.get();
		}

		public SimpleStringProperty stateSetIdProperty() {
			return stateSetId;
		}

		public void setStateSetId(String stateSetId) {
			this.stateSetId.set(stateSetId);
		}

		public String getStateSetVendorIANA() {
			return stateSetVendorIANA.get();
		}

		public SimpleStringProperty stateSetVendorIANAProperty() {
			return stateSetVendorIANA;
		}

		public void setStateSetVendorIANA(String stateSetVendorIANA) {
			this.stateSetVendorIANA.set(stateSetVendorIANA);
		}

		public String getStateSetVendorName() {
			return stateSetVendorName.get();
		}

		public SimpleStringProperty stateSetVendorNameProperty() {
			return stateSetVendorName;
		}

		public void setStateSetVendorName(String stateSetVendorName) {
			this.stateSetVendorName.set(stateSetVendorName);
		}
	}
	public class ValueRecords{

	}

	@FXML
	void onStateSetVendorNameAction(ActionEvent event) {
		if (stateSetVendorNameTextField.getText().isBlank()) stateSetVendorNameTextField.setVisible(true);
		else stateSetVendorNameTextField.setVisible(false);
		workingData.setStateSetVendorName(stateSetVendorNameTextField.getText());;
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}
	@FXML
	void onStateSetVendorIANAAction(ActionEvent event) {
		if (stateSetVendorIANA.getText().isBlank() && !App.isInteger(stateSetVendorIANA.getText())) stateSetVendorIANA.setVisible(true);
		else stateSetVendorIANA.setVisible(false);
		workingData.setStateSetVendorIANA(stateSetVendorIANA.getText());;
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}
	@FXML
	void onStateSetIDAction(ActionEvent event) {
		if (stateSetId.getText().isBlank() && !App.isInteger(stateSetId.getText())) stateSetId.setVisible(true);
		else stateSetId.setVisible(false);
		workingData.setStateSetId(stateSetId.getText());;
		modified = true;
		saveChangesButton.setDisable(!isValid());
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
	}

	public boolean isValid() {
		if(stateSetVendorNameTextField.isVisible()) return  false;
		if(stateSetVendorIANA.isVisible()) return  false;
		if(stateSetId.isVisible()) return  false;
		if(this.getWorkingData().getOemStateValueRecords() != null && this.getWorkingData().getOemStateValueRecords().size() == 0) return false;
		return true;
	}

}