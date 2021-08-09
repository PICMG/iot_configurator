//*******************************************************************
//    App.java
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

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class App extends Application {	
	
	private static Scene scene;
	@FXML AnchorPane deviceTabAnchorPane;
	@FXML AnchorPane sensorsTabAnchorPane;
	@FXML AnchorPane effectersTabAnchorPane;
	@FXML AnchorPane stateSetsTabAnchorPane;

/* TODO: Move to device pane controller
    @FXML AnchorPane bindingPane;
    public static AnchorPane stateSensorContent;
    public static AnchorPane stateEffecterContent;
    public static AnchorPane numericSensorContent;
    public static AnchorPane numericEffecterContent;
	public static AnchorPane fruContent;
	public static AnchorPane parameterContent;
	public static StateSensorController stateSensorController;
	public static StateEffecterController stateEffecterController;
	public static NumericEffecterController numericEffecterController;
	public static NumericSensorController numericSensorController;
	public static FruPaneController fruPaneController;
	public static ParameterPaneController parameterPaneController;
*/
	/**
	 * isInteger()
	 * A helper that returns true if the string parameter represents
	 * a valid base-10 signed integer
	 * @param num - the string to check
	 * @return - true if the string is a valid integer
	 */
	static boolean isInteger(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * isUnsignedInteger()
	 * A helper that returns true if the string parameter represents
	 * a valid base-10 unsigned integer
	 * @param num - the string to check
	 * @return - true if the string is a valid integer
	 */
	static boolean isUnsignedInteger(String num) {
		try {
			Integer.parseInt(num);
			if (num.charAt(0) != '-') return true;
		} catch (NumberFormatException e) {}
		return false;
	}

	/**
	 * isFloat()
	 * A helper that returns true if the string parameter represents
	 * a valid double-precision floating-point number.
	 * @param num - the string to check
	 * @return - true if the string is a valid integer
	 */
	static boolean isFloat(String num) {
		try {
			Double.parseDouble(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
    public void start(Stage stage) { 
	    Parent root;
	    try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
			Scene scene = new Scene(root, 1024, 768);

			stage.setTitle("PICMG Configurator");
			stage.setScene(scene);
			stage.show();

			// find the anchor panes for each of the tabs
			deviceTabAnchorPane = (AnchorPane) scene.lookup("#deviceTabAnchorPane");
			sensorsTabAnchorPane = (AnchorPane) scene.lookup("#sensorsTabAnchorPane");
			effectersTabAnchorPane = (AnchorPane) scene.lookup("#effectersTabAnchorPane");
			stateSetsTabAnchorPane = (AnchorPane) scene.lookup("#stateSetsTabAnchorPane");

			// set up the tab panes
			{
				// device configuration
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainPanel.fxml"));
				deviceTabAnchorPane.getChildren().add(loader.load());
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

/*		TODO: move to device configuration controller
  			// load the fxml object for the main screen

			root = FXMLLoader.load(getClass().getClassLoader().getResource("MainPanel.fxml"));
	        Scene scene = new Scene(root, 1024, 768);

	        stage.setTitle("PICMG Configurator");
	        stage.setScene(scene);
	        stage.show();
	        
	        bindingPane = (AnchorPane) scene.lookup("#bindingPane");
	        
	        // setup binding panes
			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("stateSensorPane.fxml"));
				stateSensorContent = (AnchorPane) loader.load();
				stateSensorController = (StateSensorController) loader.getController();
				bindingPane.getChildren().add(stateSensorContent);
				stateSensorContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("stateEffecterPane.fxml"));
				stateEffecterContent = (AnchorPane) loader.load();
				stateEffecterController = (StateEffecterController) loader.getController();
				bindingPane.getChildren().add(stateEffecterContent);
				stateEffecterContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("numericSensorPane.fxml"));
				numericSensorContent = (AnchorPane) loader.load();
				numericSensorController = (NumericSensorController) loader.getController();
				bindingPane.getChildren().add(numericSensorContent);
				numericSensorContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("numericEffecterPane.fxml"));
				numericEffecterContent = (AnchorPane) loader.load();
				numericEffecterController = (NumericEffecterController) loader.getController();
				bindingPane.getChildren().add(numericEffecterContent);
				numericEffecterContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FruPane.fxml"));
				fruContent = (AnchorPane) loader.load();
				fruPaneController = (FruPaneController) loader.getController();
				bindingPane.getChildren().add(fruContent);
				fruContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ParameterPane.fxml"));
				parameterContent = (AnchorPane) loader.load();
				parameterPaneController = (ParameterPaneController) loader.getController();
				bindingPane.getChildren().add(parameterContent);
				parameterContent.setVisible(false);
			}
*/
		} catch (IOException e) {
	    	System.out.println(e);
		}
    }

    public static void main(String[] args) {

		launch();

    }
}