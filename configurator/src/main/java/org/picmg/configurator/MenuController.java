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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.picmg.jsonreader.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
	@FXML MenuItem fileOpenMenuItem;
	@FXML Menu exportMenu;
	@FXML MenuBar mainMenubar;
	@FXML AnchorPane deviceTabAnchorPane;
	@FXML AnchorPane sensorsTabAnchorPane;
	@FXML AnchorPane effectersTabAnchorPane;
	@FXML AnchorPane stateSetsTabAnchorPane;

	void updateMenuChoices(Boolean configError) {
		exportMenu.setDisable(configError);
	}

	@FXML void notifyExport(Event event) {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(mainMenubar.getScene().getWindow());
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
				MainScreenController controller = loader.getController();
				controller.getErrorProperty().addListener( new ChangeListener<Boolean>() {
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
			}

			{
				// state sets
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileOpenMenuItem.setOnAction( e -> {
			FileChooser fileChooser = new FileChooser();
			File selectedFile = fileChooser.showOpenDialog(mainMenubar.getScene().getWindow());
		});

		// this code is a work-around to get a menu-level, clickable control
		Label exportLabel = new Label("Export");
		exportLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				File selectedFile = fileChooser.showOpenDialog(mainMenubar.getScene().getWindow());
			}
		});
		exportMenu.setGraphic(exportLabel);
	}
}