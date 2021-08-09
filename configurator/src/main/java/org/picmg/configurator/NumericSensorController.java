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
import java.io.InputStream;
import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;

public class NumericSensorController implements Initializable {
	@FXML private AnchorPane numericSensorPane;
	@FXML private VBox numericSensorVBox;
	@FXML private Label nameText;
	@FXML private Label descriptionText;
	@FXML private ImageView boundChannelIcon;
	@FXML private ComboBox<String> boundChannel;
	@FXML private ImageView physicalSensorIcon;
	@FXML private TextField physicalSensor;
	@FXML private ImageView inputCurveIcon;
	@FXML private CheckBox inputCurveEnabled;
	@FXML private Button selectCurve;
	@FXML private ImageView inputGearingRatioIcon;
	@FXML private TextField inputGearingRatio;
	@FXML private ImageView physicalBaseUnitIcon;
	@FXML private ComboBox<String> physicalBaseUnit;
	@FXML private ImageView physicalUnitModifierIcon;
	@FXML private TextField physicalUnitModifier;
	@FXML private ImageView physicalRateIcon;
	@FXML private ComboBox<String> physicalRate;
	@FXML private ImageView relIcon;
	@FXML private TextField rel;
	@FXML private ImageView physicalAuxIcon;
	@FXML private ComboBox<String> physicalAux;
	@FXML private ImageView physicalAuxRateIcon;
	@FXML private ComboBox<String> physicalAuxRate;
	@FXML private ImageView physicalAuxRateModifierIcon;
	@FXML private TextField physicalAuxUnitModifier;

	private Device device;
	private TreeItem<MainScreenController.TreeData> selectedNode;
	private boolean updated = false;

	public boolean isError(){
		if(updated) {
			boolean isError = false;
			if (boundChannel.getValue() == null) {
				isError = true;
			}
			if (physicalSensor.getText() == null) {
				isError = true;
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
			if (rel.getText() == null) {
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
			physicalSensor.setText(device.getBindingValueFromKey(selectedNode.getValue().name,"sensor"));
			physicalSensor.setDisable(true);
		}else{
			physicalSensor.setDisable(false);
			//TODO: hook up physical sensor select
		}

		// if input curve is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"inputCurve") != null){
			selectCurve.setDisable(true);
			inputCurveEnabled.setSelected(true);
			inputCurveEnabled.setDisable(true);
		}else{
			selectCurve.setDisable(false);
			inputCurveEnabled.setSelected(false);
			inputCurveEnabled.setDisable(false);
			//TODO: add input curve selection
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
			//TODO: add population for physicalBaseUnit cbox
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
			//TODO: add population for physicalRate cbox
		}

		// if rel is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit") != null){
			rel.setText(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnit"));
			rel.setDisable(true);
		}else{
			rel.setDisable(false);
		}

		// if physical aux unit modifier is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnitModifier") != null){
			physicalAuxUnitModifier.setText(device.getBindingValueFromKey(selectedNode.getValue().name,"physicalAuxUnitModifier"));
			physicalAuxUnitModifier.setDisable(true);
		}else{
			physicalAuxUnitModifier.setDisable(false);
		}

		this.selectedNode = selectedNode;
		updated = true;

		//update icons
		if (boundChannel.getValue() == null) {
			boundChannelIcon.setVisible(true);
		}else {
			boundChannelIcon.setVisible(false);
		}
		if (physicalSensor.getText() == null) {
			physicalSensorIcon.setVisible(true);
		}else {
			physicalSensorIcon.setVisible(false);
		}
		if (inputGearingRatio.getText() == null) {
			inputGearingRatioIcon.setVisible(true);
		}else {
			inputGearingRatioIcon.setVisible(false);
		}
		if (physicalBaseUnit.getValue() == null) {
			physicalBaseUnitIcon.setVisible(true);
		}else {
			physicalBaseUnitIcon.setVisible(false);
		}
		if (physicalUnitModifier.getText() == null) {
			physicalUnitModifierIcon.setVisible(true);
		}else {
			physicalUnitModifierIcon.setVisible(false);
		}
		if (physicalRate.getValue() == null) {
			physicalRateIcon.setVisible(true);
		}else {
			physicalRateIcon.setVisible(false);
		}
		if (rel.getText() == null) {
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
		if (physicalAuxUnitModifier.getText() == null) {
			physicalAuxRateModifierIcon.setVisible(true);
		}else {
			physicalAuxRateModifierIcon.setVisible(false);
		}
	}


}