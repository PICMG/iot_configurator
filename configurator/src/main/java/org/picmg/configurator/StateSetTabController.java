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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class StateSetTabController implements Initializable {
	@FXML private TableView<StateSetTableData> stateSetTableView;
	@FXML private TableColumn<StateSetTableData, String> vendorName;
	@FXML private TableColumn<StateSetTableData, String> vendorIANA;
	@FXML private TableColumn<StateSetTableData, String> stateSetID;

	@FXML private TableView<ValueRecords> stateSetValueRecords;
	@FXML private TableColumn<ValueRecords, String> stateName;

	@FXML private TextField stateSetId;
	@FXML private TextField stateSetVendorNameTextField;
	@FXML private TextField stateSetVendorIANA;

	@FXML private Button saveChangesButton;
	@FXML private Button saveAsChangesButton;

	@FXML private ImageView vendorNameImage;
	@FXML private ImageView oemStateSetValueRecordImage;
	@FXML private ImageView stateSetIDImage;
	@FXML private ImageView vendorIANAImage;

	//TODO:change to use state sensor data class
	//SensorTableData workingData = new SensorTableData();

	//	public SensorTableData getSensorTableData() {
	//		return workingData;
	//	}
	public class StateSetTableData{

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
	}
}