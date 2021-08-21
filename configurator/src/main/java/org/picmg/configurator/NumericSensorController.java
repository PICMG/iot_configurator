//*******************************************************************
//    NumericSensorController.java
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
import java.io.*;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.picmg.jsonreader.*;

public class NumericSensorController implements Initializable {
	@FXML private Label nameText;
	@FXML private Label descriptionText;
	@FXML private ImageView boundChannelIcon;
	@FXML private ComboBox<String> boundChannel;
	@FXML private ImageView physicalSensorIcon;
	@FXML private ComboBox<String> physicalSensor;
	@FXML private ImageView inputCurveIcon;
	@FXML private CheckBox inputCurveEnabled;
	@FXML private Button selectCurve;
	@FXML private Button view;
	@FXML private ImageView inputGearingRatioIcon;
	@FXML private TextField inputGearingRatio;
	@FXML private ImageView physicalBaseUnitIcon;
	@FXML private ComboBox<String> physicalBaseUnit;
	@FXML private ImageView physicalUnitModifierIcon;
	@FXML private TextField physicalUnitModifier;
	@FXML private ImageView physicalRateIcon;
	@FXML private ComboBox<String> physicalRate;
	@FXML private ImageView relIcon;
	@FXML private ComboBox<String> rel;
	@FXML private ImageView physicalAuxIcon;
	@FXML private ComboBox<String> physicalAux;
	@FXML private ImageView physicalAuxRateIcon;
	@FXML private ComboBox<String> physicalAuxRate;
	@FXML private ImageView physicalAuxRateModifierIcon;
	@FXML private TextField physicalAuxUnitModifier;
	@FXML private Button restoreButton;

	private Device device;
	private TreeItem<MainScreenController.TreeData> selectedNode;
	private boolean updated = false;

	/**
	 * loadPointsFromCsvFile()
	 * load points from the specified csv file into the data array for this object
	 * @param input - the file to input from
	 * @return - true if successful, otherwise false.
	 */
	public boolean loadPointsFromCsvFile(File input) {
		// clear any existing points
		ArrayList<Point2D> points = new ArrayList<>();

		// attempt to load the new points
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			String csvLine;
			// keep reading lines from the file until there are no more
			while ((csvLine = br.readLine()) != null) {
				String[] vals = csvLine.split(",");
				// every line must have two values
				if (vals.length!=2) return false;

				// both values must be numeric
				if (!App.isFloat(vals[0])) return false;
				if (!App.isFloat(vals[1])) return false;

				// this point checks out - make a point object and
				// add it to the response curve.
				Point2D p = new Point2D(Double.parseDouble(vals[0]),Double.parseDouble(vals[1]));
				points.add(p);
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		// valid curves must have at least two points
		if (points.size()<2) return false;

		// here if file has been read - set the points in the device configuration
		JsonArray curve = (JsonArray)((JsonObject)selectedNode.getValue().leaf).get("inputCurve");
		curve.clear();
		for (Point2D point:points) {
			JsonObject jpoint = new JsonObject();
			jpoint.put("in",new JsonValue(Double.toString(point.getX())));
			jpoint.put("out",new JsonValue(Double.toString(point.getY())));
			curve.add(jpoint);
		}
		return true;
	}

	private void updateIcons(){
		boolean isError = false;

		JsonObject binding = (JsonObject)selectedNode.getValue().leaf;
		if (binding==null) return;

		if (!Device.isIoBindingFieldValid(binding,"boundChannel")) {
			boundChannelIcon.setVisible(true);
			isError = true;
		}else {
			boundChannelIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"sensor")) {
			physicalSensorIcon.setVisible(true);
			isError = true;
		}else {
			physicalSensorIcon.setVisible(false);
		}

		if(!Device.isIoBindingFieldValid(binding,"inputCurve")){
			inputCurveIcon.setVisible(true);
			isError = true;
		} else {
			inputCurveIcon.setVisible(false);
		}


		if (!Device.isIoBindingFieldValid(binding,"inputGearingRatio")) {
			inputGearingRatioIcon.setVisible(true);
			isError = true;
		} else {
			inputGearingRatioIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"physicalBaseUnit")) {
			physicalBaseUnitIcon.setVisible(true);
			isError = true;
		}else {
			physicalBaseUnitIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"physicalUnitModifier")) {
			physicalUnitModifierIcon.setVisible(true);
			isError = true;
		}else {
			physicalUnitModifierIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"physicalRateUnit")) {
			physicalRateIcon.setVisible(true);
			isError = true;
		}else {
			physicalRateIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"rel")) {
			relIcon.setVisible(true);
			isError = true;
		}else {
			relIcon.setVisible(false);
		}
		if (!Device.isIoBindingFieldValid(binding,"physicalAuxUnit")) {
			physicalAuxIcon.setVisible(true);
			isError = true;
		}else {
			physicalAuxIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"physicalAuxRateUnit")) {
			physicalAuxRateIcon.setVisible(true);
			isError = true;
		}else {
			physicalAuxRateIcon.setVisible(false);
		}

		if (!Device.isIoBindingFieldValid(binding,"physicalAuxUnitModifier")) {
			physicalAuxRateModifierIcon.setVisible(true);
			isError = true;
		}else {
			physicalAuxRateModifierIcon.setVisible(false);
		}

		// now update the tree icon
		if (isError) {
			selectedNode.getValue().error.setValue(true);
		} else {
			selectedNode.getValue().error.setValue(false);
		}
	}

	@Override
	/**
	 * Initialize the pane.  This is called only once when the pane is created.  This function configures the static
	 * control choices, sets up listeners.  Since nothing is selected on the device tree, there is no need to
	 * configure the values of the individual controls on the pane.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: add logic for ALL listeners to use configured value if one exists rather than capabilities

		// input curve enabled listener. This changes the error icon, and the enabled property of the buttons, nothing else.
		inputCurveEnabled.selectedProperty().addListener((options, oldValue, newValue) -> {
			if(inputCurveEnabled.isSelected()){
				selectCurve.setDisable(false);
				view.setDisable(false);
			}else{
				selectCurve.setDisable(true);
				view.setDisable(true);
			}
			updateIcons();
		});

		// select curve button on click listener
		selectCurve.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File datafile = fileChooser.showOpenDialog(selectCurve.getScene().getWindow());
				loadPointsFromCsvFile(datafile);
				updateIcons();
			}
		});

		// select curve button on click listener
		view.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("responseCurveDialog.fxml"));
				Parent dlg = null;
				try {
					dlg = fxmlLoader.load();
				} catch (IOException e) {
					return;
				}
				responseCurveViewController viewController = fxmlLoader.<responseCurveViewController>getController();

				// set the datapoints from the device model
				JsonArray curve = (JsonArray)((JsonObject)selectedNode.getValue().leaf).get("inputCurve");
				ArrayList<Point2D> points = new ArrayList<>();
				for (JsonAbstractValue val:curve) {
					points.add(new Point2D(val.getDouble("in"),val.getDouble("out")));
				}
				viewController.setDataPoints(points);

				// show the graph in a modal dialog box
				Scene scene = new Scene(dlg, 800, 600);
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.setTitle("Sensor Response Curve");
				stage.setScene(scene);
				stage.showAndWait();
			}
		});


		//TODO: add device save configuration on ALL new value listeners

		// comboboxes run on new value
		boundChannel.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (boundChannel.getValue()!=null)
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"boundChannel",boundChannel.getValue());
			setControlEnables();
			setSensorChoices();
			updateIcons();
		});
		physicalSensor.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (physicalSensor.getValue()!=null)
				device.setSensorFromFile(selectedNode.getValue().name, physicalSensor.getValue());
			setControlEnables();
			updateIcons();
		});
		physicalBaseUnit.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (physicalBaseUnit.getValue()!=null)
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"physicalBaseUnit",physicalBaseUnit.getValue());
			setControlEnables();
			updateIcons();
		});
		physicalRate.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (physicalRate.getValue()!=null)
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"physicalRateUnit",physicalRate.getValue());
			setControlEnables();
			updateIcons();
		});
		physicalAux.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (physicalAux.getValue()!=null)
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit",physicalAux.getValue());
			setControlEnables();
			updateIcons();
		});
		rel.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (rel.getValue()!=null)
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"rel",rel.getValue());
			setControlEnables();
			updateIcons();
		});
		physicalAuxRate.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (physicalAuxRate.getValue()!=null)
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"physicalAuxRateUnit",physicalAuxRate.getValue());
			setControlEnables();
			updateIcons();
		});

		//TODO: add type checking on textboxes

		//textboxes run on new value
		inputGearingRatio.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"inputGearingRatio",inputGearingRatio.getText());
				setControlEnables();
				updateIcons();
			}
		});
		physicalUnitModifier.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"physicalUnitModifier",physicalUnitModifier.getText());
				setControlEnables();
				updateIcons();
			}
		});
		physicalAuxUnitModifier.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				device.setConfiguredBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnitModifier",physicalAuxUnitModifier.getText());
				setControlEnables();
				updateIcons();
			}
		});
		restoreButton.setOnAction( e-> {
			// event handler for button press
			// Restore the values for the configuration section back to
			// the capabilities values from the device
			boundChannel.setValue(null);
			physicalSensor.setValue(null);
			device.restoreBindingToDefaults(selectedNode.getValue().name);
			setPaneValues();
			setControlEnables();
			updateIcons();
		});
	}

	/*******************************************************************************************************************
	 * populate the choices with sensor names found in the library - present only sensor names that match the bound
	 * channel.  This function should be called on update and any time the channel binding is changed.
	 */
	private void setSensorChoices() {
		JsonResultFactory factory = new JsonResultFactory();
		physicalSensor.getItems().clear();

		if (boundChannel.getValue() != null) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(System.getProperty("user.dir") + "/lib/sensors"))) {
				for (Path path : stream) {
					if (!Files.isDirectory(path)) {
						JsonAbstractValue json = factory.buildFromFile(path);
						String name = json.getValue("name");

						// check to see if one of the supported interfaces for this sensor matches
						// the interface of the bound channel.
						String channelType = device.getInterfaceTypeFromName(boundChannel.getValue());
						JsonArray channelTypesSupported = (JsonArray) ((JsonObject) json).get("supportedInterfaces");

						physicalSensor.getItems().add(name);
					}
				}
			} catch (IOException e) {
				// unable to find the directory
			}
		}
	}

	/*******************************************************************************************************************
	 * update the pane contents to the default values from the configuration section of the device object - at this
	 * point, any default values from the capabilities section have already been copied to the configuration section.
	 * This function is called from update().
	 **/
	private void setPaneValues() {
		String bindingName = selectedNode.getValue().name;
		JsonResultFactory factory = new JsonResultFactory();

		// set name and description text at the top of the pane
		nameText.setText("  Name: "+this.device.getConfiguredBindingValueFromKey(bindingName,"name"));
		descriptionText.setText("  Description: "+this.device.getConfiguredBindingValueFromKey(bindingName,"description"));

		//if the binding is virtual, set channel to virtual and gray out the cbox. Else, display available channels.
		if(device.getConfiguredBindingValueFromKey(bindingName,"isVirtual").equals("true")) {
			boundChannel.getItems().clear();
			boundChannel.setValue("virtual");
		}else {
			// here if the channel is not virtual - if the binding has not set by default, allow it to be set
			ArrayList<String> channelChoices = device.getPossibleChannelsForBinding(device.getConfiguredBindingFromName(bindingName));
			boundChannel.getItems().clear();
			boundChannel.setValue(null);

			// if bound channel is non-null, set value according to json.
			for(int i=0; i<channelChoices.size(); i++) {
				boundChannel.getItems().add(channelChoices.get(i));
			}
			if(device.getConfiguredBindingValueFromKey(bindingName,"boundChannel") != null){
				boundChannel.setValue(device.getConfiguredBindingValueFromKey(bindingName,"boundChannel"));
			}else{
				boundChannel.setValue(null);
			}
		}

		// if sensor is non-null, set value according to json. Else, allow for population.
		setSensorChoices();
		if(device.getConfiguredBindingValueFromKey(bindingName,"sensor") != null){
			// here if a value has been set in the configuration - set it in the pane
			String value = device.getConfiguredBindingValueFromKey(bindingName,"sensor.name");
			physicalSensor.setValue(value);
		} else {
			physicalSensor.setValue(null);
		}

		// if input curve is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(bindingName,"inputCurve") != null){
			selectCurve.setDisable(true);
			inputCurveEnabled.setSelected(true);
		}else{
			inputCurveEnabled.setSelected(false);
		}

		// if gearing ratio is non-null, set value according to json. Else, allow for population.
		inputGearingRatio.setText("");
		if(device.getConfiguredBindingValueFromKey(bindingName,"inputGearingRatio") != null){
			inputGearingRatio.setText(device.getConfiguredBindingValueFromKey(bindingName,"inputGearingRatio"));
		} else {
			inputGearingRatio.setText(null);
		}

		// if physical base unit is non-null, set value according to json. Else, allow for population.
		physicalBaseUnit.getItems().clear();
		for (String choice:Device.unitsChoices) physicalBaseUnit.getItems().add(choice);
		if(device.getConfiguredBindingValueFromKey(bindingName,"physicalBaseUnit") != null){
			physicalBaseUnit.setValue(device.getConfiguredBindingValueFromKey(bindingName,"physicalBaseUnit"));
		}

		// if physical unit modifier is non-null, set value according to json. Else, allow for population.
		physicalUnitModifier.setText("");
		if(device.getConfiguredBindingValueFromKey(bindingName,"physicalUnitModifier") != null){
			physicalUnitModifier.setText(device.getConfiguredBindingValueFromKey(bindingName,"physicalUnitModifier"));
		} else {
			physicalUnitModifier.setText(null);
		}

		// if physical rate is non-null, set value according to json. Else, allow for population.
		physicalRate.getItems().clear();
		for (String choice:Device.rateChoices) physicalRate.getItems().add(choice);
		if(device.getConfiguredBindingValueFromKey(bindingName,"physicalRateUnit") != null){
			physicalRate.setValue(device.getConfiguredBindingValueFromKey(bindingName,"physicalRateUnit"));
		}

		// if physical aux is non-null, set value according to json. Else, allow for population.
		physicalAux.getItems().clear();
		for (String choice:Device.unitsChoices) physicalAux.getItems().add(choice);
		if(device.getConfiguredBindingValueFromKey(bindingName,"physicalAuxUnit") != null){
			physicalAux.setValue(device.getConfiguredBindingValueFromKey(bindingName,"physicalAuxUnit"));
		}

		// if rel is non-null, set value according to json. Else, allow for population.
		rel.getItems().clear();
		for (String choice:Device.relChoices) rel.getItems().add(choice);
		if(device.getConfiguredBindingValueFromKey(bindingName,"rel") != null){
			rel.setValue(device.getConfiguredBindingValueFromKey(bindingName,"rel"));
		}

		// if physical aux unit modifier is non-null, set value according to json. Else, allow for population.
		physicalAuxUnitModifier.setText("");
		if(device.getConfiguredBindingValueFromKey(bindingName,"physicalAuxUnitModifier") != null){
			physicalAuxUnitModifier.setText(device.getConfiguredBindingValueFromKey(bindingName,"physicalAuxUnitModifier"));
		} else {
			physicalAuxUnitModifier.setText(null);
		}

		// if physical Aux Rate is non-null, set value according to json. Else, allow for population.
		physicalAuxRate.getItems().clear();
		for (String choice:Device.rateChoices) physicalAuxRate.getItems().add(choice);
		if(device.getConfiguredBindingValueFromKey(bindingName,"physicalAuxRateUnit") != null){
			physicalAuxRate.setValue(device.getConfiguredBindingValueFromKey(bindingName,"physicalAuxRateUnit"));
		}

		updated = true;
	}

	/*******************************************************************************************************************
	 * This function is called on update of the pane and when the binding or sensor selection values are changed.
	 * The function sets the control enables based on the state of the controls.  This pane has 4 distinct states:
	 * 1. The binding has not been set.  The binding control will be enabled and all others will be disabled.
	 * 2. The binding has been set but the sensor has not yet been selected.  The binding control and the
	 *    sensor control will both be enabled. All other controls will be disabled.
	 * 3. The binding and the sensor have both been set but no other controls have been set - in this case, the
	 *    binding control will be disabled.  All other controls will be enabled.
	 * 4. The binding control and the sensor control have been set and at least one other control value is also
	 *    set.  The binding and sensor controls will be disabled and
	 *    all other controls will be enabled.
	 *
	 * A special case exists if the sensor is virtual - in this case the pane will automatically be in state 4.
	 **/
	private void setControlEnables() {
		//TODO; add logic for update such that if there exists a configured value, use it on creation, rather
		// than defaulting to the capabilites

		String bindingName = selectedNode.getValue().name;
		JsonResultFactory factory = new JsonResultFactory();

		boolean disableBinding;
		boolean disableSensor;
		boolean disableOthers;

		//if the binding is virtual, set channel to virtual and gray out the cbox. Else, display available channels.
		if (device.getConfiguredBindingValueFromKey(bindingName,"isVirtual").equals("true")) {
			disableBinding = true;
			disableSensor = true;
			disableOthers = false;
		} else {
			if (device.getConfiguredBindingValueFromKey(bindingName, "boundChannel") != null) {
				// here if the binding has been set - check to see if the sensor has been set
				if (device.getConfiguredBindingValueFromKey(bindingName, "sensor.name") != null) {
					// sensor has been set
					if ((device.getConfiguredBindingValueFromKey(bindingName, "inputGearingRatio") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "physicalBaseUnit") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "physicalUnitModifier") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "physicalRateUnit") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "rel") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "physicalAuxUnit") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "physicalAuxUnitModifier") != null) ||
							(device.getConfiguredBindingValueFromKey(bindingName, "physicalAuxRateUnit") != null)) {
						// the binding, sensor, and at least one other field are set - state 4
						disableBinding = true;
						disableSensor = true;
						disableOthers = false;
					} else {
						// the binding and the sensor are set but no other values have been set - state 3
						disableBinding = true;
						disableSensor = false;
						disableOthers = false;
					}
				} else {
					// binding set, sensor not set - (state 2)
					disableBinding = false;
					disableSensor = false;
					disableOthers = true;
				}
			} else {
				// here if the binding has not been set - (state 1)
				disableBinding = false;
				disableSensor = true;
				disableOthers = true;
			}
		}

		// enable / disable the controls
		boundChannel.setDisable(disableBinding&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"boundChannel"));
		physicalSensor.setDisable(disableSensor&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"sensor"));
		selectCurve.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"inputCurve"));
		inputCurveEnabled.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"inputCurve"));
		selectCurve.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"inputCurve"));
		view.setDisable(disableOthers&&(device.getConfiguredBindingFromName("inputCurve")!=null));
		inputGearingRatio.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"inputGearingRatio"));
		physicalBaseUnit.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"physicalBaseUnit"));
		physicalUnitModifier.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"physicalUnitModifier"));
		physicalRate.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"physicalRate"));
		rel.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"rel"));
		physicalAux.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"physicalAux"));
		physicalAuxUnitModifier.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"physicalAuxUnitModifier"));
		physicalAuxRate.setDisable(disableOthers&&device.isConfigurationBindingFieldEditable(selectedNode.getValue().name,"physicalAuxRate"));
	}

	/*******************************************************************************************************************
	 * This function is called by the main controller any time a new numeric sensor is selected from the tree view.
	 * The purpose of this function is to update the controls from data stored in the device.
	 *
	 * @param device - the device configuration json object that is currently being modified by the user.
	 * @param selectedNode - the tree node that was selected by the user
	 */
	public void update(Device device, TreeItem<MainScreenController.TreeData> selectedNode){
		//TODO; add logic for update such that if there exists a configured value, use it on creation, rather
		// than defaulting to the capabilites

		// do any configuration required prior to making the pane visible

		this.device = device;
		this.selectedNode = selectedNode;
		setPaneValues();
		setControlEnables();
		updateIcons();
	}
}