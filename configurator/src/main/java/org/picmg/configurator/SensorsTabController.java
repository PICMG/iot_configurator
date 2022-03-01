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

public class SensorsTabController implements Initializable {
	@FXML private TableView<SensorTableData> SensorTableView;
	@FXML private TableColumn<SensorTableData, String> manufacturerColumn;
	@FXML private TableColumn<SensorTableData, String> modelColumn;
	@FXML private TableColumn<SensorTableData, String> typeColumn;
	@FXML private TextField nameTextField;
	@FXML private TextField manufacturerTextfield;
	@FXML private TextField partNumberTextField;
	@FXML private TextArea descriptionTextArea;
	@FXML private CheckBox analogCheckbox;
	@FXML private CheckBox digitalCheckbox;
	@FXML private CheckBox countCheckbox;
	@FXML private CheckBox rateCheckbox;
	@FXML private CheckBox quadratureCheckbox;
	@FXML private TextField maxSampleRateTextfield;
	@FXML private ChoiceBox<String> baseUnitChoicebox;
	@FXML private ChoiceBox<String> rateUnitChoicebox;
	@FXML private ChoiceBox<String> auxUnitChoicebox;
	@FXML private ChoiceBox<String> relChoicebox;
	@FXML private ChoiceBox<String> auxRateChoicebox;
	@FXML private TextField unitModifierTextField;
	@FXML private TextField auxUnitModifierTextfield;
	@FXML private TextField plusAccuracyTextfield;
	@FXML private TextField minusAccuracyTextfield;
	@FXML private TextField outputUnitsTextfield;
	@FXML private Button selectCurveButton;
	@FXML private Button viewCurveButton;
	@FXML private Button saveChangesButton;
	@FXML private ImageView manufacturerImage;
	@FXML private ImageView unitModifierImage;
	@FXML private ImageView baseUnitImage;
	@FXML private ImageView maxSampleRateImage;
	@FXML private ImageView interfacesImage;
	@FXML private ImageView descriptionImage;
	@FXML private ImageView modelImage;
	@FXML private ImageView rateUnitImage;
	@FXML private ImageView relImage;
	@FXML private ImageView auxUnitModifierImage;
	@FXML private ImageView auxUnitImage;
	@FXML private ImageView minusAccuracyImage;
	@FXML private ImageView outputCurveImage;
	@FXML private ImageView outputUnitsImage;
	@FXML private ImageView plusAccuracyImage;
	@FXML private ImageView auxRateUnitImage;

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

	private final static String NO_AUX = "(No Aux)";
	final String[] relChoices = {
		NO_AUX, "dividedBy","multipliedBy"
	};
	boolean modified;
	boolean valid;
	SensorTableData workingData = new SensorTableData();

	/**
	 * This inner class describes the data model for the sensor table
	 * and sensor data pane
	 */
	public class SensorTableData {
		SimpleStringProperty name = new SimpleStringProperty();
		SimpleStringProperty manufacturer = new SimpleStringProperty();
		SimpleStringProperty model = new SimpleStringProperty();
		SimpleStringProperty description = new SimpleStringProperty();
		SimpleBooleanProperty analog = new SimpleBooleanProperty();
		SimpleBooleanProperty digital = new SimpleBooleanProperty();
		SimpleBooleanProperty count = new SimpleBooleanProperty();
		SimpleBooleanProperty rate = new SimpleBooleanProperty();
		SimpleBooleanProperty quadrature = new SimpleBooleanProperty();
		SimpleStringProperty maxSampleRate = new SimpleStringProperty();
		SimpleStringProperty baseUnit = new SimpleStringProperty();
		SimpleStringProperty unitModifier = new SimpleStringProperty();
		SimpleStringProperty rateUnit = new SimpleStringProperty();
		SimpleStringProperty auxUnit = new SimpleStringProperty();
		SimpleStringProperty auxModifier = new SimpleStringProperty();
		SimpleStringProperty rel = new SimpleStringProperty();
		SimpleStringProperty auxRateUnit =  new SimpleStringProperty();
		SimpleStringProperty plusAccuracy = new SimpleStringProperty();
		SimpleStringProperty minusAccuracy = new SimpleStringProperty();
		SimpleStringProperty outputUnits = new SimpleStringProperty();
		ArrayList<Point2D> outputCurve = new ArrayList<>();
		boolean valid;

		// getters and setters
		public String getName() { return name.get();}
		public void setName(String name) {this.name.set(name);}
		public String getManufacturer() { return manufacturer.get();}
		public void setManufacturer(String manufacturer) {this.manufacturer.set(manufacturer);}
		public String getModel() {return model.get();}
		public void setModel(String model) {this.model.set(model);}
		public String getDescription() {return description.get();}
		public void setDescription(String description) {this.description.set(description);}
		public boolean isAnalog() {return analog.get();}
		public void setAnalog(boolean analog) {this.analog.set(analog);}
		public boolean isDigital() {return digital.get();}
		public void setDigital(boolean digital) {this.digital.set(digital);}
		public boolean isCount() {return count.get();}
		public void setCount(boolean count) {this.count.set(count);}
		public boolean isRate() {return rate.get();}
		public void setRate(boolean rate) {this.rate.set(rate);}
		public boolean isQuadrature() {return quadrature.get();}
		public void setQuadrature(boolean quadrature) {this.quadrature.set(quadrature);}
		public String getMaxSampleRate() {return maxSampleRate.get();}
		public void setMaxSampleRate(String maxSampleRate) {this.maxSampleRate.set(maxSampleRate);}
		public String getBaseUnit() {return baseUnit.get();}
		public void setBaseUnit(String baseUnit) {this.baseUnit.set(baseUnit);}
		public String getUnitModifier() {return unitModifier.get();}
		public void setUnitModifier(String unitModifier) {this.unitModifier.set(unitModifier);}
		public String getRateUnit() {return rateUnit.get();}
		public void setRateUnit(String rateUnit) {this.rateUnit.set(rateUnit);}
		public String getAuxUnit() {return auxUnit.get();}
		public void setAuxUnit(String auxUnit) {this.auxUnit.set(auxUnit);}
		public String getAuxModifier() {return auxModifier.get();}
		public void setAuxModifier(String auxModifier) {this.auxModifier.set(auxModifier);}
		public String getRel() {return rel.get();}
		public void setRel(String rel) {this.rel.set(rel);}
		public String getAuxRateUnit() {return auxRateUnit.get();}
		public void setAuxRateUnit(String auxRateUnit) {this.auxRateUnit.set(auxRateUnit);}
		public String getPlusAccuracy() {return plusAccuracy.get();}
		public void setPlusAccuracy(String plusAccuracy) {this.plusAccuracy.set(plusAccuracy);}
		public String getMinusAccuracy() {return minusAccuracy.get();}
		public void setMinusAccuracy(String minusAccuracy) {this.minusAccuracy.set(minusAccuracy);}
		public String getOutputUnits() {return outputUnits.get();}
		public void setOutputUnits(String outputUnits) {this.outputUnits.set(outputUnits);}
		public ArrayList<Point2D> getOutputCurve() {return outputCurve;}

		public String getType() {
			StringBuilder sb = new StringBuilder();
			sb.append(baseUnit.get());
			if ((!auxUnit.get().equals("None"))&&(!auxUnit.get().equals("Unspecified"))) {
				if (rel.get().equals("multipliedBy")) {
					sb.append("_");
				} else {
					sb.append("_per_");
				}
				sb.append(auxUnit.get());
			}
			if (!rateUnit.get().equals("None")) {
				sb.append("_");
				sb.append(rateUnit.get());
			}
			if (!auxRateUnit.get().equals("None")) {
				sb.append("_");
				sb.append(auxRateUnit.get());
			}
			// remove any special characters from the string
			String type = sb.toString();
			return type.replaceAll("[^a-z,A-Z,0-9]","_");
		}

		// construction
		/**
		 * SensorTableData()
		 * Attempt to initialize the data structure from the specified (fully qualified)
		 * filename.  If there are errors, the object will still be created, but the valid
		 * field will be set (true).
		 * @param path - the fully qualified path to a sensor data file to initialize from.
		 */
		public SensorTableData(Path path) {
			valid = false;

			// attempt to load the json
			JsonResultFactory factory = new JsonResultFactory();
			JsonAbstractValue json = factory.buildFromFile(path);
			if (json==null) return;

			// check to make sure the json is the right type
			if (!json.getClass().isAssignableFrom(JsonObject.class)) return;

			// check to make sure the json has all the right fields
			if (((JsonObject)json).get("name")==null) return;
			if (((JsonObject)json).get("manufacturer")==null) return;
			if (((JsonObject)json).get("partNumber")==null) return;
			if (((JsonObject)json).get("description")==null) return;
			if (((JsonObject)json).get("supportedInterfaces")==null) return;
			if (((JsonObject)json).get("baseUnit")==null) return;
			if (((JsonObject)json).get("maxSampleRate")==null) return;
			if (((JsonObject)json).get("unitModifier")==null) return;
			if (((JsonObject)json).get("rateUnit")==null) return;
			if (((JsonObject)json).get("auxUnit")==null) return;
			if (((JsonObject)json).get("auxUnitModifier")==null) return;
			if (((JsonObject)json).get("rel")==null) return;
			if (((JsonObject)json).get("auxRateUnit")==null) return;
			if (((JsonObject)json).get("plusAccuracy")==null) return;
			if (((JsonObject)json).get("minusAccuracy")==null) return;
			if (((JsonObject)json).get("outputUnits")==null) return;
			if (((JsonObject)json).get("responseCurve")==null) return;

			// check to make sure the json fields are all the right types
			if (!((JsonObject)json).get("name").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("manufacturer").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("partNumber").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("description").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("supportedInterfaces").getClass().isAssignableFrom(JsonArray.class)) return;
			if (!((JsonObject)json).get("maxSampleRate").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("baseUnit").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("unitModifier").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("rateUnit").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("auxUnit").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("auxUnitModifier").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("rel").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("auxRateUnit").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("plusAccuracy").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("minusAccuracy").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("outputUnits").getClass().isAssignableFrom(JsonValue.class)) return;
			if (!((JsonObject)json).get("responseCurve").getClass().isAssignableFrom(JsonArray.class)) return;

			// make sure numeric types are actual numbers
			if (!App.isUnsignedInteger(json.getValue("baseUnit"))) return;
			if (!App.isUnsignedInteger(json.getValue("auxUnit"))) return;
			if (!App.isUnsignedInteger(json.getValue("rateUnit"))) return;
			if (!App.isUnsignedInteger(json.getValue("auxRateUnit"))) return;
			if (!App.isInteger(json.getValue("unitModifier"))) return;
			if (!App.isInteger(json.getValue("auxUnitModifier"))) return;
			if (!App.isFloat(json.getValue("plusAccuracy"))) return;
			if (!App.isFloat(json.getValue("minusAccuracy"))) return;
			if ((json.getValue("maxSampleRate")!=null)&&
				   (!App.isUnsignedInteger(json.getValue("maxSampleRate")))) return;
			
			// check the values of the enumerated fields to make sure they match one of the
			// possible selections
			if ((json.getInteger("baseUnit"))>=unitsChoices.length) return;
			if ((json.getInteger("auxUnit"))>=unitsChoices.length) return;
			if ((json.getInteger("rateUnit"))>=rateChoices.length) return;
			if ((json.getInteger("auxRateUnit"))>=rateChoices.length) return;
			boolean foundMatch = false;
			for (String relChoice : relChoices) {
				if (relChoice.equals(json.getValue("rel"))) {
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch) return;

			//====================
			// Json checks are done.  Now, populate the object from the json data
			name.set(json.getValue("name"));
			manufacturer.set(json.getValue("manufacturer"));
			model.set(json.getValue("partNumber"));
			description.set(json.getValue("description"));
			if (json.getValue("maxSampleRate")!=null) {
				maxSampleRate.set(json.getValue("maxSampleRate"));
			} else {
				maxSampleRate.set("0");
			}
			baseUnit.set(unitsChoices[json.getInteger("baseUnit")]);
			unitModifier.set(json.getValue("unitModifier"));
			rateUnit.set(rateChoices[json.getInteger("rateUnit")]);
			auxUnit.set(unitsChoices[json.getInteger("auxUnit")]);
			auxModifier.set(json.getValue("auxUnitModifier"));
			rel.set(json.getValue("rel"));
			auxRateUnit.set(rateChoices[json.getInteger("auxRateUnit")]);
			plusAccuracy.set(json.getValue("plusAccuracy"));
			minusAccuracy.set(json.getValue("minusAccuracy"));
			outputUnits.set(json.getValue("outputUnits"));
			// set the supported interfaces values
			JsonArray interfaces = (JsonArray)((JsonObject)json).get("supportedInterfaces");
			for (JsonAbstractValue anInterface : interfaces) {
				if (!anInterface.getClass().isAssignableFrom(JsonValue.class)) continue;
				JsonValue interfaceName = (JsonValue) anInterface;
				switch (interfaceName.getValue("")) {
					case "analog_in":
						analog.set(true);
						break;
					case "digital_in":
						digital.set(true);
						break;
					case "count_in":
						count.set(true);
						break;
					case "rate_in":
						rate.set(true);
						break;
					case "quadrature_in":
						quadrature.set(true);
					default:
						break;
				}

			}
			// set the input curve data point values
			JsonArray datapoints = (JsonArray)((JsonObject)json).get("responseCurve");
			int datapointCount = 0;
			for (JsonAbstractValue jsonAbstractValue : datapoints) {
				if (!jsonAbstractValue.getClass().isAssignableFrom(JsonObject.class)) continue;
				JsonObject datapoint = (JsonObject) jsonAbstractValue;
				if (!datapoint.containsKey("in")) continue;
				if (!datapoint.containsKey("out")) continue;
				if (!App.isFloat(datapoint.getValue("in"))) continue;
				if (!App.isFloat(datapoint.getValue("out"))) continue;
				datapointCount++;
				Point2D p = new Point2D(datapoint.getDouble("in"), datapoint.getDouble("out"));
				outputCurve.add(p);
			}
			// there must be at least two data points
			if (datapointCount<2) return;

			// the structure is valid - set the flag
			valid = true;
		}

		/**
		 * SaveToFile()
		 * Save the sensor data to the specified file path
		 */
		public void SaveToFile(String path) {
			// create the Json Object
			JsonObject json = new JsonObject();
			
			// check to make sure the json has all the right fields
			json.put("name", new JsonValue(name.get()));
			json.put("manufacturer", new JsonValue(manufacturer.get()));
			json.put("partNumber", new JsonValue(model.get()));
			json.put("description", new JsonValue(description.get()));
			json.put("supportedInterfaces", new JsonValue());
			{
				String str = "0";
				for (int i=0;i<unitsChoices.length;i++) {
					if (unitsChoices[i].equals(baseUnit.get())) {
						str = Integer.toString(i);
						break;
					}
				}
				json.put("baseUnit", new JsonValue(str));
			}
			if ((maxSampleRate.get().isBlank())||((App.isFloat(maxSampleRate.get())&&(Double.parseDouble(maxSampleRate.get())<=0)))) {
				json.put("maxSampleRate", new JsonValue("null"));
			} else json.put("maxSampleRate", new JsonValue(maxSampleRate.get()));
			json.put("unitModifier", new JsonValue(unitModifier.get()));
			{
				String str = "0";
				for (int i=0;i<rateChoices.length;i++) {
					if (rateChoices[i].equals(rateUnit.get())) {
						str = Integer.toString(i);
						break;
					}
				}
				json.put("rateUnit", new JsonValue(str));
			}
			{
				String str = "0";
				for (int i=0;i<unitsChoices.length;i++) {
					if (unitsChoices[i].equals(auxUnit.get())) {
						str = Integer.toString(i);
						break;
					}
				}
				json.put("auxUnit", new JsonValue(str));
			}
			json.put("auxUnitModifier", new JsonValue(auxModifier.get()));
			json.put("rel", new JsonValue(rel.get()));
			{
				String str = "0";
				for (int i=0;i<rateChoices.length;i++) {
					if (rateChoices[i].equals(auxRateUnit.get())) {
						str = Integer.toString(i);
						break;
					}
				}
				json.put("auxRateUnit", new JsonValue(str));
			}
			json.put("plusAccuracy", new JsonValue(plusAccuracy.get()));
			json.put("minusAccuracy", new JsonValue(minusAccuracy.get()));
			json.put("outputUnits", new JsonValue(outputUnits.get()));
			JsonArray responseData = new JsonArray();
			for (Point2D point:outputCurve) {
				JsonObject obj = new JsonObject();
				obj.put("in", new JsonValue(Double.toString(point.getX())));
				obj.put("out", new JsonValue(Double.toString(point.getY())));
				responseData.add(obj);
			}
			json.put("responseCurve", responseData);

			JsonArray interfaces = new JsonArray();
			if (analog.get()) interfaces.add(new JsonValue("analog_in"));
			if (digital.get()) interfaces.add(new JsonValue("digital_in"));
			if (count.get()) interfaces.add(new JsonValue("count_in"));
			if (rate.get()) interfaces.add(new JsonValue("rate_in"));
			if (quadrature.get()) interfaces.add(new JsonValue("quadrature_in"));
			json.put("supportedInterfaces", interfaces);

			// write the json to the file
			try {
				FileWriter fw;
				fw = new FileWriter(path);
				BufferedWriter br = new BufferedWriter(fw);
				json.writeToFile(br);
				br.close();
			} catch (IOException e) {
				System.out.println("error writing file");
			}
		}

		/**
		 * empty constructor
		 */
		public SensorTableData() {
			valid = false;
		}

		/**
		 * set()
		 * set the values of this object to match those of the given object.
		 * @param data - the object to set from
		 */
		public void set(SensorTableData data) {
			name.set(data.name.get());
			manufacturer.set(data.manufacturer.get());
			model.set(data.model.get());
			description.set(data.description.get());
			analog.set(data.analog.get());
			digital.set(data.digital.get());
			count.set(data.count.get());
			rate.set(data.rate.get());
			quadrature.set(data.quadrature.get());
			maxSampleRate.set(data.maxSampleRate.get());
			baseUnit.set(data.baseUnit.get());
			unitModifier.set(data.unitModifier.get());
			rateUnit.set(data.rateUnit.get());
			auxUnit.set(data.auxUnit.get());
			auxModifier.set(data.auxModifier.get());
			rel.set(data.rel.get());
			auxRateUnit.set(data.auxRateUnit.get()) ;
			plusAccuracy.set(data.plusAccuracy.get());
			minusAccuracy.set(data.minusAccuracy.get());
			outputUnits.set(data.outputUnits.get());

			outputCurve.clear();

			for (Point2D datapoint:data.outputCurve) {
				Point2D newPoint = new Point2D(datapoint.getX(),datapoint.getY());
				outputCurve.add(newPoint);
			}
		}
			
		/**
		 * loadPointsFromCsvFile()
		 * load points from the specified csv file into the data array for this object
		 * @param input - the file to input from
		 * @return - true if successful, otherwise false.
		 */
		public boolean loadPointsFromCsvFile(File input) {
			// clear any existing points
			outputCurve.clear();

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
					outputCurve.add(p);
				}
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			// valid curves must have at least two points
			if (outputCurve.size()<2) return false;
			return true;
		}
	}

	/**
	 * updateName()
	 * update the name field based on data within the current working set.
	 */
	public void updateName() {
		String name = workingData.getManufacturer() +
				'-' +
				workingData.getModel() +
				'_' +
				workingData.getType();
		nameTextField.setText(name.replaceAll("[^a-z,A-Z,0-9]","_"));
		workingData.setName(name.replaceAll("[^a-z,A-Z,0-9]","_"));
	}

	/**
	 * isValid()
	 * return true if the current working data set is valid.  This check is performed by
	 * evaluating each "indicator light" to see if any are visible.
	 * @return true if valid, otherwise, false
	 */
	public boolean isValid() {
		if (manufacturerImage.isVisible()) return false;
		if (unitModifierImage.isVisible()) return false;
		if (baseUnitImage.isVisible()) return false;
		if (maxSampleRateImage.isVisible()) return false;
		if (interfacesImage.isVisible()) return false;
		if (descriptionImage.isVisible()) return false;
		if (modelImage.isVisible()) return false;
		if (rateUnitImage.isVisible()) return false;
		if (relImage.isVisible()) return false;
		if (auxUnitModifierImage.isVisible()) return false;
		if (auxUnitImage.isVisible()) return false;
		if (minusAccuracyImage.isVisible()) return false;
		if (outputCurveImage.isVisible()) return false;
		if (outputUnitsImage.isVisible()) return false;
		if (plusAccuracyImage.isVisible()) return false;
		if (auxRateUnitImage.isVisible()) return false;
		return true;
	}
	
	@FXML
	void onManufacturerAction(ActionEvent event) {
		if (manufacturerTextfield.getText().isBlank()) manufacturerImage.setVisible(true);
		else manufacturerImage.setVisible(false);
		workingData.setManufacturer(manufacturerTextfield.getText());
		modified = true;
		updateName();
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onPartNumberAction(ActionEvent event) {
		if (partNumberTextField.getText().isBlank()) modelImage.setVisible(true);
		else modelImage.setVisible(false);
		workingData.setModel(partNumberTextField.getText());
		modified = true;
		updateName();
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onAnalogAction(ActionEvent event) {
		if ((!digitalCheckbox.isSelected())&&(!analogCheckbox.isSelected())
				&&(!countCheckbox.isSelected())&&(!rateCheckbox.isSelected())
				&&(!quadratureCheckbox.isSelected())) {
			interfacesImage.setVisible(true);
		} else {
			interfacesImage.setVisible(false);
		}
		workingData.setAnalog(analogCheckbox.isSelected());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onDigitalAction(ActionEvent event) {
		if ((!digitalCheckbox.isSelected())&&(!analogCheckbox.isSelected())
			&&(!countCheckbox.isSelected())&&(!rateCheckbox.isSelected())
			&&(!quadratureCheckbox.isSelected())) {
			interfacesImage.setVisible(true);
		} else {
			interfacesImage.setVisible(false);
		}
		workingData.setDigital(digitalCheckbox.isSelected());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onCountAction(ActionEvent event) {
		if ((!digitalCheckbox.isSelected())&&(!analogCheckbox.isSelected())
				&&(!countCheckbox.isSelected())&&(!rateCheckbox.isSelected())
				&&(!quadratureCheckbox.isSelected())) {
			interfacesImage.setVisible(true);
		} else {
			interfacesImage.setVisible(false);
		}
		workingData.setCount(countCheckbox.isSelected());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onRateAction(ActionEvent event) {
		if ((!digitalCheckbox.isSelected())&&(!analogCheckbox.isSelected())
				&&(!countCheckbox.isSelected())&&(!rateCheckbox.isSelected())
				&&(!quadratureCheckbox.isSelected())) {
			interfacesImage.setVisible(true);
		} else {
			interfacesImage.setVisible(false);
		}
		workingData.setRate(rateCheckbox.isSelected());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onQuadratureAction(ActionEvent event) {
		if ((!digitalCheckbox.isSelected())&&(!analogCheckbox.isSelected())
				&&(!countCheckbox.isSelected())&&(!rateCheckbox.isSelected())
				&&(!quadratureCheckbox.isSelected())) {
			interfacesImage.setVisible(true);
		} else {
			interfacesImage.setVisible(false);
		}
		workingData.setQuadrature(quadratureCheckbox.isSelected());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onMaxSampleRateAction(ActionEvent event) {
		if ((!maxSampleRateTextfield.getText().isBlank())&&
		   (!App.isUnsignedInteger(maxSampleRateTextfield.getText()))) maxSampleRateImage.setVisible(true);
		else maxSampleRateImage.setVisible(false);
		workingData.setMaxSampleRate(maxSampleRateTextfield.getText());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onUnitModifierAction(ActionEvent event) {
		if (!App.isUnsignedInteger(unitModifierTextField.getText())) unitModifierImage.setVisible(true);
		else unitModifierImage.setVisible(false);
		workingData.setUnitModifier(unitModifierTextField.getText());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onAuxUnitModifierAction(ActionEvent event) {
		if (!App.isUnsignedInteger(auxUnitModifierTextfield.getText())) auxUnitModifierImage.setVisible(true);
		else auxUnitModifierImage.setVisible(false);
		workingData.setAuxModifier(auxUnitModifierTextfield.getText());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onPlusAccuractyAction(ActionEvent event) {
		if (!App.isFloat(plusAccuracyTextfield.getText())) plusAccuracyImage.setVisible(true);
		else plusAccuracyImage.setVisible(false);
		workingData.setPlusAccuracy(plusAccuracyTextfield.getText());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onMinusAccuracyAction(ActionEvent event) {
		if (!App.isFloat(minusAccuracyTextfield.getText())) minusAccuracyImage.setVisible(true);
		else minusAccuracyImage.setVisible(false);
		workingData.setMinusAccuracy(minusAccuracyTextfield.getText());
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onOutputUnitsAction(ActionEvent event) {
		workingData.setOutputUnits(outputUnitsTextfield.getText());
		outputUnitsImage.setVisible(false);
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onOutputCurveSelectAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File datafile = fileChooser.showOpenDialog(nameTextField.getScene().getWindow());
		boolean result = workingData.loadPointsFromCsvFile(datafile);
		outputCurveImage.setVisible(!result);
		modified = true;
		saveChangesButton.setDisable(!isValid());
	}

	@FXML
	void onOutputCurveViewAction(ActionEvent event) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("responseCurveDialog.fxml"));
		Parent dlg = null;
		try {
			dlg = fxmlLoader.load();
		} catch (IOException e) {
			return;
		}
		responseCurveViewController viewController = fxmlLoader.<responseCurveViewController>getController();

		// set the datapoints
		viewController.setDataPoints(workingData.getOutputCurve());

		// show the graph in a modal dialog box
		Scene scene = new Scene(dlg, 800, 600);
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Sensor Response Curve");
		stage.setScene(scene);
		stage.showAndWait();
	}

	@FXML
	void onSaveChangesAction(ActionEvent event) {
		String path = System.getProperty("user.dir")+"/lib/sensors/" + workingData.getName()+".json";
		workingData.SaveToFile(path);
		modified = false;
		saveChangesButton.setDisable(true);
		initializeTable();
		selectDefaultSensor();
	}

	Tooltip createTooltip(String tip) {
		Tooltip tt = new Tooltip(tip);
		tt.setWrapText(true);
		return tt;
	}
	/**
	 * setTooltips()
	 * set the tooltip for each control
	 */
	private void setTooltips() {
		nameTextField.setTooltip(createTooltip("The name of the sensor data file (not editable)"));
		manufacturerTextfield.setTooltip(createTooltip("The name of the manufacturer of the sensor"));
		partNumberTextField.setTooltip(createTooltip("The manufacturer's part number or model number for the sensor"));
		descriptionTextArea.setTooltip(createTooltip("A brief description of the sensor."));
		analogCheckbox.setTooltip(createTooltip("The electrical interface type for the sensor"));
		digitalCheckbox.setTooltip(createTooltip("The electrical interface type for the sensor"));
		countCheckbox.setTooltip(createTooltip("The electrical interface type for the sensor"));
		rateCheckbox.setTooltip(createTooltip("The electrical interface type for the sensor"));
		quadratureCheckbox.setTooltip(createTooltip("The electrical interface type for the sensor"));
		maxSampleRateTextfield.setTooltip(createTooltip("The maximum sample rate for the sensor (in Hertz).  Leave blank or set to 0 for no maximum"));
		baseUnitChoicebox.setTooltip(createTooltip("The base units for the sensor"));
		rateUnitChoicebox.setTooltip(createTooltip("The rate units for the sensor (if any)"));
		auxUnitChoicebox.setTooltip(createTooltip("The auxiliary units for the sensor (if any)"));
		relChoicebox.setTooltip(createTooltip("The relationship between the base units and the auxiliary units of the sensor"));
		auxRateChoicebox.setTooltip(createTooltip("The auxiliary rate units for the sensor (if any)"));
		unitModifierTextField.setTooltip(createTooltip("The power of 10 modifier for the base units"));
		auxUnitModifierTextfield.setTooltip(createTooltip("The power of 10 modifier for the auxiliary units"));
		plusAccuracyTextfield.setTooltip(createTooltip("The absolute value of the positive accuracy of the sensor"));
		minusAccuracyTextfield.setTooltip(createTooltip("The absolute value of the negative accuracy of the sensor"));
		outputUnitsTextfield.setTooltip(createTooltip("The output electrical units of the sensor"));
	}
	
	/**
	 * initializeTable()
	 * initialize the contents of the table with contents from the library folder
	 */
	private void initializeTable() {
		SensorTableView.getItems().clear();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(System.getProperty("user.dir")+"/lib/sensors"))) {
			for (Path path : stream) {
				if (!Files.isDirectory(path)) {
					SensorTableData data = new SensorTableData(path);
					if (data.valid) {
						// place the data in the table
						SensorTableView.getItems().add(data);
					}
				}
			}
		} catch (IOException e) {
			// unable to find the directory
		}
	}

	private void setSensorData(SensorTableData data) {
		nameTextField.setText(data.getName());
		manufacturerTextfield.setText(data.getManufacturer());
		partNumberTextField.setText(data.getModel());
		descriptionTextArea.setText(data.getDescription());
		analogCheckbox.setSelected(data.isAnalog());
		digitalCheckbox.setSelected(data.isDigital());
		countCheckbox.setSelected(data.isCount());
		rateCheckbox.setSelected(data.isRate());
		quadratureCheckbox.setSelected(data.isQuadrature());
		maxSampleRateTextfield.setText(data.getMaxSampleRate());
		baseUnitChoicebox.setValue(data.getBaseUnit());
		unitModifierTextField.setText(data.getUnitModifier());
		rateUnitChoicebox.setValue(data.getRateUnit());
		auxUnitChoicebox.setValue(data.getAuxUnit());
		auxUnitModifierTextfield.setText(data.getAuxModifier());
		relChoicebox.setValue(data.getRel());
		auxRateChoicebox.setValue(data.getAuxRateUnit());
		plusAccuracyTextfield.setText(data.getPlusAccuracy());
		minusAccuracyTextfield.setText(data.getMinusAccuracy());
		outputUnitsTextfield.setText(data.getOutputUnits());
	}

	public void clearIndicators() {
		manufacturerImage.setVisible(false);
		unitModifierImage.setVisible(false);
		baseUnitImage.setVisible(false);
		maxSampleRateImage.setVisible(false);
		interfacesImage.setVisible(false);
		descriptionImage.setVisible(false);
		modelImage.setVisible(false);
		rateUnitImage.setVisible(false);
		relImage.setVisible(false);
		auxUnitModifierImage.setVisible(false);
		auxUnitImage.setVisible(false);
		minusAccuracyImage.setVisible(false);
		outputCurveImage.setVisible(false);
		outputUnitsImage.setVisible(false);
		plusAccuracyImage.setVisible(false);
		auxRateUnitImage.setVisible(false);
	}

	private void selectDefaultSensor() {
		SensorTableView.getSelectionModel().select(0);
		SensorTableData selecteddata = SensorTableView.getSelectionModel().getSelectedItem();
		workingData.set(selecteddata);
		setSensorData(selecteddata);
		modified = false;
		saveChangesButton.setDisable(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
		modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

		// set up the table view with sensors from the library
		initializeTable();

		// set the values for each choicebox
		for (String choice:unitsChoices) baseUnitChoicebox.getItems().add(choice);
		for (String choice:unitsChoices) auxUnitChoicebox.getItems().add(choice);
		for (String choice:rateChoices) rateUnitChoicebox.getItems().add(choice);
		for (String choice:rateChoices) auxRateChoicebox.getItems().add(choice);
		for (String choice:relChoices) relChoicebox.getItems().add(choice);

		// set up parameters for other controls
		descriptionTextArea.setWrapText(true);

		// set up the sensor configuration with default data
		selectDefaultSensor();

		// clear error indicators
		clearIndicators();

		// set tooltips for each control
		setTooltips();

		//====================================
		// set any additional listeners
		//====================================
		// add an event filter to the tableview to prevent changing selections without permission
		SensorTableView.addEventFilter(
				MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					public void handle(final MouseEvent mouseEvent) {
						if (modified) {
							Alert alert = new Alert(Alert.AlertType.WARNING,
									"If you select a new sensor now, unsaved work on the existing sensor will be lost.",
									ButtonType.OK, ButtonType.CANCEL);
							alert.setTitle("Loss of Data Warning");
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() != ButtonType.OK) {
								mouseEvent.consume();
							}
						}
					}
				});
		// select new sensor from table view
		ObservableList<SensorTableData> tableSelection = SensorTableView.getSelectionModel().getSelectedItems();
		tableSelection.addListener(new ListChangeListener<SensorTableData>() {
			@Override
			public void onChanged(Change<? extends SensorTableData> c) {
				// here if a new selection has been made from the table - populate the
				// controls with the data
				SensorTableData data = SensorTableView.getSelectionModel().getSelectedItem();
				if (data==null) return;
				workingData.set(data);
				setSensorData(workingData);
				clearIndicators();
				modified = false;
				saveChangesButton.setDisable(true);
			}
		});

		// selection choice changes for each choice box
		baseUnitChoicebox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
				workingData.setBaseUnit(newString);
				updateName();
				modified = true;
				saveChangesButton.setDisable(!isValid());
			}
		});
		auxUnitChoicebox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
				workingData.setAuxUnit(newString);
				updateName();
				modified = true;
				saveChangesButton.setDisable(!isValid());
			}
		});
		rateUnitChoicebox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
				workingData.setRateUnit(newString);
				updateName();
				modified = true;
				saveChangesButton.setDisable(!isValid());
			}
		});
		auxRateChoicebox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
				workingData.setAuxRateUnit(newString);
				updateName();
				modified = true;
				saveChangesButton.setDisable(!isValid());
			}
		});
		relChoicebox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
				workingData.setRel(newString);
				updateName();
				modified = true;
				saveChangesButton.setDisable(!isValid());
			}
		});

		// fire action events if focus is lost on our text fields - this allows the normal action handler
		// to update and check values.
		manufacturerTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { manufacturerTextfield.fireEvent(new ActionEvent()); }}});
		partNumberTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { partNumberTextField.fireEvent(new ActionEvent()); }}});
		maxSampleRateTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { maxSampleRateTextfield.fireEvent(new ActionEvent()); }}});
		unitModifierTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { unitModifierTextField.fireEvent(new ActionEvent()); }}});
		auxUnitModifierTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { auxUnitModifierTextfield.fireEvent(new ActionEvent()); }}});
		plusAccuracyTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { plusAccuracyTextfield.fireEvent(new ActionEvent()); }}});
		minusAccuracyTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { minusAccuracyTextfield.fireEvent(new ActionEvent()); }}});
		outputUnitsTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) { outputUnitsTextfield.fireEvent(new ActionEvent()); }}});
		descriptionTextArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					if (descriptionTextArea.getText().isBlank()) descriptionImage.setVisible(true);
					else descriptionImage.setVisible(false);
					workingData.setDescription(descriptionTextArea.getText());
					saveChangesButton.setDisable(!isValid());
					modified = true;
				}
			}
		});
		modified = false;

	}
}