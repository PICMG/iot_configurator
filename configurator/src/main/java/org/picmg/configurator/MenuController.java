//*******************************************************************
//    MainScreenController.java
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
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
	@FXML Menu resetMenu;
	@FXML Menu exportMenu;
	@FXML Menu newDeviceMenu;
	@FXML Menu loadDeviceMenu;

	@FXML MenuBar mainMenubar;
	@FXML AnchorPane deviceTabAnchorPane;
	@FXML AnchorPane sensorsTabAnchorPane;
	@FXML AnchorPane effectersTabAnchorPane;
	@FXML AnchorPane stateSetsTabAnchorPane;

	MainScreenController mainController; // the controller for the main pane

	/**
	 * Update whether or not the Export menu item is visible on the menu bar
	 * @param configError if false, display the export menu option
	 */
	void updateMenuChoices(Boolean configError) {
		exportMenu.setDisable(configError);
	}

	@FXML void notifyExport(Event event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(App.getBasePath()));
		File selectedFile = fileChooser.showOpenDialog(mainMenubar.getScene().getWindow());
	}

	/**
	 * This function is invoked by the main menu "New" selection.  The function attempts
	 * to load an existing device capabilities file.
	 */
	void importNewDevice()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(App.getBasePath()));
		File selectedFile = fileChooser.showOpenDialog(mainMenubar.getScene().getWindow());
		mainController.loadDevice(selectedFile);
	}

	/**
	 * This function is invoked by the main menu "Edit" selection.  The function attempts
	 * to load an existing configuration file.
	 */
	void importConfig()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(App.getBasePath()));
		File selectedFile = fileChooser.showOpenDialog(mainMenubar.getScene().getWindow());
		mainController.loadConfig(selectedFile);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// find the anchor panes for each of the tabs

		try {
			// set up the tab panes
			{
				// device configuration
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainPanel.fxml"));
				deviceTabAnchorPane.getChildren().add(loader.load());

				// set up a listener to change the state of the main menu when the configuration error
				// condition changes
				mainController = loader.getController();
				mainController.getErrorProperty().addListener( new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable,
										Boolean oldValue, Boolean newValue) {
						updateMenuChoices(newValue);
					};
				});
			}

			{
				// sensors
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sensorsTabView.fxml"));
				sensorsTabAnchorPane.getChildren().add(loader.load());
			}

			{
				// effecters
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("effectersTabView.fxml"));
				effectersTabAnchorPane.getChildren().add(loader.load());
			}

			{
				// state sets
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("stateSetsTabView.fxml"));
				stateSetsTabAnchorPane.getChildren().add(loader.load());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Label resetLabel = new Label("Reset");
		resetLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Warning");
				alert.setHeaderText("Reset Configuration:");
				alert.setContentText("If you proceed, all current work will be lost.  Continue?");
				Button temp = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
				temp.setId("resetOk");
				Optional<ButtonType> result = alert.showAndWait();
				if (result == null) return;
				if (result.get() == null) return;
				if (result.get() == ButtonType.OK){
					mainController.resetDevice();
				}
			}
		});
		resetMenu.setGraphic(resetLabel);


		// this code is a work-around to get a menu-level, clickable control
		Label exportLabel = new Label("Export");
		exportLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(new File(App.getBasePath()));
				File selectedFile = fileChooser.showSaveDialog(mainMenubar.getScene().getWindow());
				if (selectedFile != null) mainController.exportConfiguration(selectedFile);
			}
		});
		exportMenu.setGraphic(exportLabel);
		updateMenuChoices(true);

		Label newDeviceLabel = new Label("New");
		newDeviceLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				importNewDevice();
			}
		});

		newDeviceMenu.setGraphic(newDeviceLabel);

		Label loadDeviceLabel = new Label("Edit");
		loadDeviceLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				importConfig();
			}
		});
		loadDeviceMenu.setGraphic(loadDeviceLabel);
	}
}