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
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

public class NumericSensorController implements Initializable {
	@FXML private AnchorPane numericSensorPane;
	@FXML private VBox numericSensorVBox;
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
	@FXML private ComboBox rel;
	@FXML private ImageView physicalAuxIcon;
	@FXML private ComboBox<String> physicalAux;
	@FXML private ImageView physicalAuxRateIcon;
	@FXML private ComboBox<String> physicalAuxRate;
	@FXML private ImageView physicalAuxRateModifierIcon;
	@FXML private TextField physicalAuxUnitModifier;

	private Device device;
	private TreeItem<MainScreenController.TreeData> selectedNode;
	private boolean updated = false;

	public class NumericSensorData{
		public String boundChannel;
		public String physicalSensor;
		public File inputCurve;
		public String inputGearingRatio;
		public String baseUnit;
		public String unitModifier;
		public String rateUnit;
		public String auxUnit;
		public String auxRateRelationship;
		public String auxRate;
		public String auxModifier;
		ArrayList<String> sensorNames = new ArrayList<String>();
		ArrayList<Point2D> inputCurvePoints = new ArrayList<>();
	}

	// choice box choices
	final String[] unitsChoices = {
			"None","Unspecified","Degrees_C", "Degrees_F", "Kelvins", "Volts", "Amps", "Watts", "Joules", "Coulombs",
			"VA", "Nits", "Lumens", "Lux", "Candelas", "kPa", "PSI", "Newtons", "CFM", "RPM", "Hertz",
			"Seconds", "Minutes", "Hours", "Days", "Weeks", "Mils", "Inches", "Feet", "Cubic_Inches",
			"Cubic_Feet", "Meters", "Cubic_Centimeters", "Cubic_Meters", "Liters", "Fluid_Ounces",
			"Radians", "Steradians", "Revolutions", "Cycles", "Gravities", "Ounces", "Pounds",
			"Foot-Pounds", "Ounce-Inches", "Gauss", "Gilberts", "Henries", "Farads", "Ohms", "Siemens",
			"Moles", "Becquerels", "PPM+(parts/million)", "Decibels", "DbA", "DbC", "Grays", "Sieverts",
			"Color_Temperature_Degrees_K", "Bits", "Bytes", "Words_(data)", "DoubleWords", "QuadWords",
			"Percentage", "Pascals", "Counts", "Grams", "Newton-meters", "Hits", "Misses", "Retries",
			"Overruns/Overflows", "Underruns", "Collisions", "Packets", "Messages", "Characters",
			"Errors", "Corrected_Errors", "Uncorrectable_Errors", "Square_Mils", "Square_Inches",
			"Square_Feet", "Square_Centimeters", "Square_Meters"
	};
	final String[] rateChoices = {
			"None","Per_MicroSecond","Per_MilliSecond","Per_Second","Per_Minute","Per_Hour",
			"Per_Day","Per_Week","Per_Month","Per_Year"
	};
	final String[] relChoices = {
			"dividedBy","multipliedBy"
	};

	/**
	 * loadPointsFromCsvFile()
	 * load points from the specified csv file into the data array for this object
	 * @param input - the file to input from
	 * @return - true if successful, otherwise false.
	 */
	public boolean loadPointsFromCsvFile(File input) {
		// clear any existing points
		data.inputCurvePoints.clear();

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
				data.inputCurvePoints.add(p);
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		// valid curves must have at least two points
		if (data.inputCurvePoints.size()<2) return false;
		return true;
	}


	private NumericSensorData data = new NumericSensorData();

	private void updateIcons(){
		//update icons
		if (boundChannel.getValue() == null) {
			boundChannelIcon.setVisible(true);
		}else {
			boundChannelIcon.setVisible(false);
		}
		if (physicalSensor.getValue() == null) {
			physicalSensorIcon.setVisible(true);
		}else {
			physicalSensorIcon.setVisible(false);
		}
		if(inputCurveEnabled.isSelected()){
			if(data.inputCurve==null){
				inputCurveIcon.setVisible(true);
			}else{
				inputCurveIcon.setVisible(false);
			}
		}else{
			inputCurveIcon.setVisible(false);
		}
		if (data.inputGearingRatio == null) {
			inputGearingRatioIcon.setVisible(true);
		}else {
			inputGearingRatioIcon.setVisible(false);
		}
		if (physicalBaseUnit.getValue() == null) {
			physicalBaseUnitIcon.setVisible(true);
		}else {
			physicalBaseUnitIcon.setVisible(false);
		}
		if (data.unitModifier == null) {
			physicalUnitModifierIcon.setVisible(true);
		}else {
			physicalUnitModifierIcon.setVisible(false);
		}
		if (physicalRate.getValue() == null) {
			physicalRateIcon.setVisible(true);
		}else {
			physicalRateIcon.setVisible(false);
		}
		if (rel.getValue() == null) {
			relIcon.setVisible(true);
		}else {
			relIcon.setVisible(false);
		}
		if (physicalAux.getValue() == null) {
			physicalAuxIcon.setVisible(true);
		}else {
			physicalAuxIcon.setVisible(false);
		}
		if (physicalAuxRate.getValue() == null) {
			physicalAuxRateIcon.setVisible(true);
		}else {
			physicalAuxRateIcon.setVisible(false);
		}
		if (data.auxModifier == null) {
			physicalAuxRateModifierIcon.setVisible(true);
		}else {
			physicalAuxRateModifierIcon.setVisible(false);
		}
	}

	public boolean isError(){
		if(updated) {
			boolean isError = false;
			if (boundChannel.getValue() == null) {
				isError = true;
			}
			if (physicalSensor.getValue() == null) {
				isError = true;
			}
			if(inputCurveEnabled.isSelected()){
				if(data.inputCurve==null){
					isError = true;
				}
			}
			if (inputGearingRatio.getText() == null) {
				isError = true;
			}
			if (physicalBaseUnit.getValue() == null) {
				isError = true;
			}
			if (physicalUnitModifier.getText() == null) {
				isError = true;
			}
			if (physicalRate.getValue() == null) {
				isError = true;
			}
			if (rel.getValue() == null) {
				isError = true;
			}
			if (physicalAux.getValue() == null) {
				isError = true;
			}
			if (physicalAuxRate.getValue() == null) {
				isError = true;
			}
			if (physicalAuxUnitModifier.getText() == null) {
				isError = true;
			}
			return  isError;
		}
		return true;

	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
				data.inputCurve = datafile;
				loadPointsFromCsvFile(datafile);
				System.out.println(data.inputCurve.toString());
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

				// set the datapoints
				viewController.setDataPoints(data.inputCurvePoints);

				// show the graph in a modal dialog box
				Scene scene = new Scene(dlg, 800, 600);
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.setTitle("Sensor Response Curve");
				stage.setScene(scene);
				stage.showAndWait();
			}
		});


		//TODO: add device save configuration on new value listners
	}

	public void update(Device device, TreeItem<MainScreenController.TreeData> selectedNode){
		// do any configuration required prior to making the pane visible

		this.device = device;

		// set name and description text at the top of the pane
		nameText.setText("  Name: "+this.device.getBindingValueFromKey(selectedNode.getValue().name,"name"));
		descriptionText.setText("  Description: "+this.device.getBindingValueFromKey(selectedNode.getValue().name,"description"));

		//if the binding is virtual, set channel to virtual and gray out the cbox. Else, display available channels.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"isVirtual").equals("true")) {
			boundChannel.getItems().clear();
			boundChannel.setValue("virtual");
			boundChannel.setDisable(true);
		}else {
			ArrayList<String> arr = device.getPossibleChannelsForBinding(device.getBindingFromName(selectedNode.getValue().name));
			boundChannel.getItems().clear();
			boundChannel.setValue(null);
			boundChannel.setDisable(false);

			// if bound channel is non-null, set value according to json. Else, allow for population.
			if(device.getBindingValueFromKey(selectedNode.getValue().name,"boundChannel") != null){
				boundChannel.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"boundChannel"));
				boundChannel.setDisable(true);
			}else{
				boundChannel.setDisable(false);
				for(int i=0; i<arr.size(); i++) {
					boundChannel.getItems().add(arr.get(i));
				}
			}
		}

		// if sensor is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"sensor") != null){
			physicalSensor.getItems().clear();
			physicalSensor.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"sensor"));
			physicalSensor.setDisable(true);
		}else{
			physicalSensor.setDisable(false);
			physicalSensor.getItems().clear();
			data.sensorNames.clear();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(System.getProperty("user.dir")+"/lib/sensors"))) {
				for (Path path : stream) {
					if (!Files.isDirectory(path)) {
						JsonResultFactory factory = new JsonResultFactory();
						JsonAbstractValue json = factory.buildFromFile(path);
						String name = json.getValue("name");
						data.sensorNames.add(name);
						physicalSensor.getItems().add(name);
						//TODO: handle for physical sensor json is held here
					}
				}
			} catch (IOException e) {
				// unable to find the directory
			}

		}

		// if input curve is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"inputCurve") != null){
			selectCurve.setDisable(true);
			inputCurveEnabled.setSelected(true);
			inputCurveEnabled.setDisable(true);
			selectCurve.setDisable(true);
			view.setDisable(true);
		}else{
			inputCurveEnabled.setSelected(false);
			inputCurveEnabled.setDisable(false);
			selectCurve.setDisable(true);
			view.setDisable(true);
		}

		// if gearing ratio is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"inputGearingRatio") != null){
			inputGearingRatio.setText(device.getBindingValueFromKey(selectedNode.getValue().name,"inputGearingRatio"));
			inputGearingRatio.setDisable(true);
		}else{
			inputGearingRatio.setDisable(false);
		}

		// if physical base unit is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalBaseUnit") != null){
			physicalBaseUnit.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalBaseUnit"));
			physicalBaseUnit.setDisable(true);
		}else{
			physicalBaseUnit.setDisable(false);
			physicalBaseUnit.getItems().clear();
			for (String choice:unitsChoices) physicalBaseUnit.getItems().add(choice);
		}

		// if physical unit modifier is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"phsicalUnitModifier") != null){
			physicalUnitModifier.setText(device.getBindingValueFromKey(selectedNode.getValue().name,"phsicalUnitModifier"));
			physicalUnitModifier.setDisable(true);
		}else{
			physicalUnitModifier.setDisable(false);
		}

		// if physical rate is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalRateUnit") != null){
			physicalRate.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalRateUnit"));
			physicalRate.setDisable(true);
		}else{
			physicalRate.setDisable(false);
			physicalRate.getItems().clear();
			for (String choice:rateChoices) physicalRate.getItems().add(choice);
		}

		// if physical aux is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit") != null){
			physicalAux.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit"));
			physicalAux.setDisable(true);
		}else{
			physicalAux.setDisable(false);
			physicalAux.getItems().clear();
			for (String choice:unitsChoices) physicalAux.getItems().add(choice);
		}

		// if rel is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit") != null){
			rel.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit"));
			rel.setDisable(true);
		}else{
			rel.setDisable(false);
			rel.getItems().clear();
			for (String choice:relChoices) rel.getItems().add(choice);
		}

		// if physical aux unit modifier is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnitModifier") != null){
			physicalAuxUnitModifier.setText(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnitModifier"));
			physicalAuxUnitModifier.setDisable(true);
		}else{
			physicalAuxUnitModifier.setDisable(false);
		}

		// if physical Aux Rate is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxRate") != null){
			physicalAuxRate.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxRate"));
			physicalAuxRate.setDisable(true);
		}else{
			physicalAuxRate.setDisable(false);
			physicalAuxRate.getItems().clear();
			for (String choice:rateChoices) physicalAuxRate.getItems().add(choice);
		}

		this.selectedNode = selectedNode;
		updated = true;

		updateIcons();
	}


}