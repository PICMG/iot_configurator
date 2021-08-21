//*******************************************************************
//    StateSensorController.java
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
public class StateSensorController implements Initializable {
	@FXML private Label nameText;
	@FXML private Label descriptionText;
	@FXML private ComboBox boundChannelCBox;
	@FXML private TextField selectedState;
	@FXML private ComboBox<String> lowInputCBox;
	@FXML private ComboBox<String> highInputCBox;
	@FXML private ImageView channelIndicator;
	@FXML private ImageView setIndicator;
	@FXML private ImageView lowIndicator;
	@FXML private ImageView highIndicator;
	private Device device;
	private String stateSet;
	private TreeItem<MainScreenController.TreeData> selectedNode;
	private JsonArray stateSets;
	private boolean updated = false;

	private void lowStatePopulate(){
		// if stateWhenLow is-non null, set value according to json. Else, allow for population.
		String jsonStr = device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenLow");
		boolean isNull = false;
		if(jsonStr!=null) {
			jsonStr = jsonStr.replaceAll("\\r", "");
			jsonStr = jsonStr.replaceAll("\\n", "");
			jsonStr = jsonStr.replaceAll("\\t", "");
			jsonStr = jsonStr.replaceAll(" ", "");
			if (!jsonStr.equals("null")) {
				lowInputCBox.getItems().clear();
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject) it.next();
					if (cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject) it2.next();
							if (binding.getValue("minStateValue").equals(jsonStr)) {
								String name = binding.getValue("stateName");
								lowInputCBox.setValue(name);
								lowInputCBox.setDisable(true);
							}
						}
					}
				}
			} else {
				isNull = true;
			}
		}else{
			isNull = true;
		}
		if(isNull){
			lowInputCBox.setDisable(false);
			lowInputCBox.setValue(null);
			lowInputCBox.getItems().clear();
			Iterator<JsonAbstractValue> it = stateSets.iterator();
			while (it.hasNext()) {
				JsonObject cdef = (JsonObject) it.next();
				if (cdef.getValue("name").equals(stateSet)) {
					Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
					while (it2.hasNext()) {
						JsonObject binding = (JsonObject) it2.next();
						String name = binding.getValue("stateName");
						lowInputCBox.getItems().add(name);
					}
				}
			}
		}
	}

	private void highStatePopulate(){
		// if stateWhenHigh is-non null, set value according to json. Else, allow for population.
		String jsonStr = device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenHigh");
		boolean isNull = false;
		if(jsonStr!=null){
			jsonStr = jsonStr.replaceAll("\\r","");
			jsonStr = jsonStr.replaceAll("\\n","");
			jsonStr = jsonStr.replaceAll("\\t","");
			jsonStr = jsonStr.replaceAll(" ","");
			if(!jsonStr.equals("null")) {
				highInputCBox.getItems().clear();
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject) it.next();
					if (cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject) it2.next();
							if (binding.getValue("maxStateValue").equals(jsonStr)) {
								String name = binding.getValue("stateName");
								highInputCBox.setValue(name);
								highInputCBox.setDisable(true);
							}
						}
					}
				}
			}else{
				isNull= true;
			}
		}else{
			isNull= true;
		}
		if(isNull){
			highInputCBox.setDisable(false);
			highInputCBox.setValue(null);
			highInputCBox.getItems().clear();
			Iterator<JsonAbstractValue> it = stateSets.iterator();
			while (it.hasNext()) {
				JsonObject cdef = (JsonObject)it.next();
				if(cdef.getValue("name").equals(stateSet)) {
					Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
					while (it2.hasNext()) {
						JsonObject binding = (JsonObject)it2.next();
						String name = binding.getValue("stateName");
						highInputCBox.getItems().add(name);
					}
				}
			}
		}
	}

	private void updateIcons(){
		//TODO: add call to mainScreenController to update error icons in tree
		if(boundChannelCBox.getValue()!=null){
			channelIndicator.setVisible(false);
		}else{
			channelIndicator.setVisible(true);
		}
		if(stateSet!=null){
			setIndicator.setVisible(false);
		}else{
			setIndicator.setVisible(true);
		}
		if(lowInputCBox.getValue()!=null){
			lowIndicator.setVisible(false);
		}else{
			lowIndicator.setVisible(true);
		}
		if(highInputCBox.getValue()!=null){
			highIndicator.setVisible(false);
		}else{
			highIndicator.setVisible(true);
		}
	}

	public boolean isError(){
		// TODO: update to match method used in Numeric Sensor controller
		// for now, just return false;
		return false;
//		if(updated){
//			boolean isError = false;
//			if(boundChannelCBox.getValue()==null){
//				isError=true;
//			}
//			if(stateSet==null){
//				isError=true;
//			}
//			if(lowInputCBox.getValue()==null){
//				isError=true;
//			}
//			if(highInputCBox.getValue()==null){
//				isError=true;
//			}
//			return isError;
//		}
//		return true;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: add logic for ALL listeners to use configured value if one exists rather than capabilities

		// state set search listener
		selectedState.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			@Override public void handle(MouseEvent e) {
				if(updated){
					try {
						// load the fxml object for the search pane as modal
						Stage stage = new Stage();
						Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("SearchPane.fxml"));
						stage.setScene(new Scene(root));
						stage.setTitle("State Set Selection");
						stage.initModality(Modality.WINDOW_MODAL);
						stage.initOwner(((Node)e.getSource()).getScene().getWindow() );
						stage.showAndWait();
						// update the value of the textbox
						selectedState.setText(StringTransfer.text);
						String state = StringTransfer.text;
						stateSet = state;
						//TODO: add device save functionality
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					lowStatePopulate();
					highStatePopulate();
					if(stateSet!=null){
						setIndicator.setVisible(false);
					}else{
						setIndicator.setVisible(true);
					}
				}
			}
		});
		// low input comboBox run on new value
		lowInputCBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if(stateSets!=null&&updated) {
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject) it.next();
					if (cdef.getValue("name").equals(selectedState.getText())) {
						Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject) it2.next();
							if (binding.getValue("stateName").equals(newValue)) {
								//TODO: add device save functionality
								//device.setBindingValueFromKey(selectedNode.getValue().name, "stateWhenLow", binding.getValue("minStateValue"));
							}
						}
					}
				}
				if(lowInputCBox.getValue()!=null){
					lowIndicator.setVisible(false);
				}else{
					lowIndicator.setVisible(true);
				}
			}
		});
		// high input comboBox run on new value
		highInputCBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if(stateSets!=null&&updated) {
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject) it.next();
					if (cdef.getValue("name").equals(selectedState.getText())) {
						Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject) it2.next();
							if (binding.getValue("stateName").equals(newValue)) {
								//TODO: add device save functionality
								//device.setBindingValueFromKey(selectedNode.getValue().name, "stateWhenHigh", binding.getValue("maxStateValue"));
							}
						}
					}
				}
				if(highInputCBox.getValue()!=null){
					highIndicator.setVisible(false);
				}else{
					highIndicator.setVisible(true);
				}
			}
		});
		// bound channel comboBox run on new value
		boundChannelCBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if(selectedNode!=null&&newValue!=null&&updated) {
				if (device.getBindingValueFromKey(selectedNode.getValue().name, "isVirtual").equals("false")) {
					//TODO: add device save functionality
					//device.setBindingValueFromKey(selectedNode.getValue().name, "boundChannel", (String) newValue);
				}
				if(boundChannelCBox.getValue()!=null){
					channelIndicator.setVisible(false);
				}else{
					channelIndicator.setVisible(true);
				}
			}
		});
	}
	public void update(Device device, TreeItem<MainScreenController.TreeData> selectedNode, JsonArray stateSets){
		//TODO; add logic for update such that if there exists a configured value, use it on creation, rather
		// than defaulting to the capabilites

		// do any configuration required prior to making the pane visible
		this.device = device;
		// set name and description text at the top of the pane
		nameText.setText("  Name: "+this.device.getBindingValueFromKey(selectedNode.getValue().name,"name"));
		descriptionText.setText("  Description: "+this.device.getBindingValueFromKey(selectedNode.getValue().name,"description"));
		//if the binding is virtual, set channel to virtual and gray out the cbox. Else, display available channels.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"isVirtual").equals("true")) {
			boundChannelCBox.getItems().clear();
			boundChannelCBox.setValue("virtual");
			boundChannelCBox.setDisable(true);
		}else {
			ArrayList<String> arr = device.getPossibleChannelsForBinding(device.getConfiguredBindingFromName(selectedNode.getValue().name));
			boundChannelCBox.getItems().clear();
			boundChannelCBox.setValue(null);
			boundChannelCBox.setDisable(false);
			// if bound channel is non-null, set value according to json. Else, allow for population.
			if(device.getBindingValueFromKey(selectedNode.getValue().name,"boundChannel") != null){
				boundChannelCBox.setValue(device.getBindingValueFromKey(selectedNode.getValue().name,"boundChannel"));
				boundChannelCBox.setDisable(true);
			}else{
				boundChannelCBox.setDisable(false);
				for(int i=0; i<arr.size(); i++) {
					boundChannelCBox.getItems().add(arr.get(i));
				}
			}
		}
		// if state set is non-null, set value according to json. Else, allow for population.
		if(device.getBindingValueFromKey(selectedNode.getValue().name,"stateSet")!=null){
			Iterator<JsonAbstractValue> it1 = stateSets.iterator();
			while (it1.hasNext()) {
				JsonObject cdef = (JsonObject)it1.next();
				if(cdef.getValue("stateSetId").equals(device.getBindingValueFromKey(selectedNode.getValue().name,"stateSet"))) {
					selectedState.clear();
					stateSet = cdef.getValue("name");
					selectedState.setText(stateSet);
					selectedState.setDisable(true);
				}
			}
		}else{
			selectedState.clear();
			selectedState.setDisable(false);
			stateSet = null;
		}
		this.stateSets = stateSets;
		this.selectedNode = selectedNode;
		lowStatePopulate();
		highStatePopulate();
		updated = true;
		updateIcons();
	}
}