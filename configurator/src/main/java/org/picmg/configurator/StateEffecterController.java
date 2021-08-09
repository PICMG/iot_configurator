//*******************************************************************
//    StateEffecterController.java
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class StateEffecterController implements Initializable {
	@FXML private Label nameText;
	@FXML private Label descriptionText;
	@FXML private ComboBox boundChannelCBox;
	@FXML private TextField selectedState;
	@FXML private ComboBox<String> lowOutputCBox;
	@FXML private ComboBox<String> highOutputCBox;
	@FXML private ComboBox<String> defaultStateCBox;

	private Device device;
	private String stateSet;
	private TreeItem<MainScreenController.TreeData> selectedNode;
	private JsonArray stateSets;



	@Override
	public void initialize(URL location, ResourceBundle resources) {

//		// state set search listener
//		selectedState.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
//			@Override public void handle(MouseEvent e) {
//				try {
//					// load the fxml object for the search pane as modal
//					Stage stage = new Stage();
//					Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("SearchPane.fxml"));
//					stage.setScene(new Scene(root));
//					stage.setTitle("State Set Selection");
//					stage.initModality(Modality.WINDOW_MODAL);
//					stage.initOwner(((Node)e.getSource()).getScene().getWindow() );
//					stage.showAndWait();
//
//					// update the value of the textbox
//					selectedState.setText(StringTransfer.text);
//					String state = StringTransfer.text;
//
//					Iterator<JsonAbstractValue> it1 = stateSets.iterator();
//					while (it1.hasNext()) {
//						JsonObject cdef = (JsonObject)it1.next();
//						if(cdef.getValue("name").equals(selectedState.getText())) {
//							device.setBindingValueFromKey(selectedNode.getValue().name, "stateSetVendor", cdef.getValue("vendorIANA"));
//							device.setBindingValueFromKey(selectedNode.getValue().name, "stateSet", cdef.getValue("stateSetId"));
//						}
//					}
//				} catch (IOException ex) {
//					// TODO Auto-generated catch block
//					ex.printStackTrace();
//				}
//
//				// update state when low and state when high if applicable
//				try{
//					// if stateWhenLow is-non null, set value according to json. Else, allow for population.
//					if(device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenLow")!=null){
//						lowOutputCBox.getItems().clear();
//						Iterator<JsonAbstractValue> it = stateSets.iterator();
//						while (it.hasNext()) {
//							JsonObject cdef = (JsonObject)it.next();
//							if(cdef.getValue("name").equals(stateSet)) {
//								Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
//								while (it2.hasNext()) {
//									JsonObject binding = (JsonObject)it2.next();
//									int bindingInt = Integer.parseInt(binding.getValue("minStateValue"));
//									int jsonInt = Integer.parseInt(device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenLow"));
//									if(bindingInt == jsonInt){
//										String name = binding.getValue("stateName");
//										lowOutputCBox.setValue(name);
//										lowOutputCBox.setDisable(true);
//									}
//								}
//							}
//						}
//					}else{
//						lowOutputCBox.setDisable(false);
//						lowOutputCBox.setValue(null);
//						Iterator<JsonAbstractValue> it = stateSets.iterator();
//						while (it.hasNext()) {
//							JsonObject cdef = (JsonObject)it.next();
//							if(cdef.getValue("name").equals(stateSet)) {
//								Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
//								while (it2.hasNext()) {
//									JsonObject binding = (JsonObject)it2.next();
//									String name = binding.getValue("stateName");
//									lowOutputCBox.getItems().add(name);
//								}
//							}
//						}
//					}
//
//					// if stateWhenHigh is-non null, set value according to json. Else, allow for population.
//					String jsonStr = device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenHigh");
//					jsonStr = jsonStr.replaceAll("\\r","");
//					jsonStr = jsonStr.replaceAll("\\n","");
//					jsonStr = jsonStr.replaceAll("\\t","");
//					jsonStr = jsonStr.replaceAll(" ","");
//
//					if(!jsonStr.equals("null")&&jsonStr!=null){
//						highOutputCBox.getItems().clear();
//						Iterator<JsonAbstractValue> it = stateSets.iterator();
//						while (it.hasNext()) {
//							JsonObject cdef = (JsonObject)it.next();
//							if(cdef.getValue("name").equals(stateSet)) {
//								Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
//								while (it2.hasNext()) {
//									JsonObject binding = (JsonObject)it2.next();
//
//									if(binding.getValue("maxStateValue").equals(jsonStr)){
//										String name = binding.getValue("stateName");
//										highOutputCBox.setValue(name);
//										highOutputCBox.setDisable(true);
//									}
//								}
//							}
//						}
//					}else{
//						highOutputCBox.setDisable(false);
//						highOutputCBox.setValue(null);
//						Iterator<JsonAbstractValue> it = stateSets.iterator();
//						while (it.hasNext()) {
//							JsonObject cdef = (JsonObject)it.next();
//							if(cdef.getValue("name").equals(stateSet)) {
//								Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
//								while (it2.hasNext()) {
//									JsonObject binding = (JsonObject)it2.next();
//									String name = binding.getValue("stateName");
//									highOutputCBox.getItems().add(name);
//								}
//							}
//						}
//					}
//				} catch (NullPointerException ex) {
//					// TODO Auto-generated catch block
//					//ex.printStackTrace();
//				}
//			}
//
//
//		});
//
//		// low input comboBox run on new value
//		lowOutputCBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//			if(stateSets!=null) {
//				Iterator<JsonAbstractValue> it = stateSets.iterator();
//				while (it.hasNext()) {
//					JsonObject cdef = (JsonObject) it.next();
//					if (cdef.getValue("name").equals(selectedState.getText())) {
//						Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
//						while (it2.hasNext()) {
//							JsonObject binding = (JsonObject) it2.next();
//							if (binding.getValue("stateName").equals(newValue)) {
//								//device.setBindingValueFromKey(selectedNode.getValue().name, "stateWhenLow", binding.getValue("minStateValue"));
//							}
//						}
//					}
//				}
//			}
//		});
//
//		// high input comboBox run on new value
//		highOutputCBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//			if(stateSets!=null) {
//				Iterator<JsonAbstractValue> it = stateSets.iterator();
//				while (it.hasNext()) {
//					JsonObject cdef = (JsonObject) it.next();
//					if (cdef.getValue("name").equals(selectedState.getText())) {
//						Iterator<JsonAbstractValue> it2 = ((JsonArray) cdef.get("oemStateValueRecords")).iterator();
//						while (it2.hasNext()) {
//							JsonObject binding = (JsonObject) it2.next();
//							if (binding.getValue("stateName").equals(newValue)) {
//								//device.setBindingValueFromKey(selectedNode.getValue().name, "stateWhenHigh", binding.getValue("maxStateValue"));
//							}
//						}
//					}
//				}
//			}
//		});
//
//		// bound channel comboBox run on new value
//		boundChannelCBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//			if(selectedNode!=null&&newValue!=null) {
//				if (device.getBindingValueFromKey(selectedNode.getValue().name, "isVirtual").equals("false")) {
//					//device.setBindingValueFromKey(selectedNode.getValue().name, "boundChannel", (String) newValue);
//				}
//			}
//		});

	}

	public void update(Device device, TreeItem<MainScreenController.TreeData> selectedNode, JsonArray stateSets){
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
			ArrayList<String> arr = device.getPossibleChannelsForBinding(device.getBindingFromName(selectedNode.getValue().name));
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
		}
		try{
			// if stateWhenLow is-non null, set value according to json. Else, allow for population.
			String jsonStr = device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenLow");

			if(jsonStr!=null){
				lowOutputCBox.getItems().clear();
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject)it.next();
					if(cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject)it2.next();

							if(binding.getValue("minStateValue").equals(jsonStr)){
								String name = binding.getValue("stateName");
								lowOutputCBox.setValue(name);
								lowOutputCBox.setDisable(true);
							}
						}
					}
				}
			}else{
				lowOutputCBox.setDisable(false);
				lowOutputCBox.setValue(null);
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject)it.next();
					if(cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject)it2.next();
							String name = binding.getValue("stateName");
							lowOutputCBox.getItems().add(name);
						}
					}
				}
			}

			// if stateWhenHigh is-non null, set value according to json. Else, allow for population.

			jsonStr = device.getBindingValueFromKey(selectedNode.getValue().name,"stateWhenHigh");
			boolean isStrNull = false;
			if(jsonStr!=null) {
				jsonStr = jsonStr.replaceAll("\\r", "");
				jsonStr = jsonStr.replaceAll("\\n", "");
				jsonStr = jsonStr.replaceAll("\\t", "");
				jsonStr = jsonStr.replaceAll(" ", "");
				if(jsonStr.equals("null")){
					isStrNull=true;
				}
			}
			if(!isStrNull&&jsonStr!=null){
				highOutputCBox.getItems().clear();
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject)it.next();
					if(cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject)it2.next();

							if(binding.getValue("maxStateValue").equals(jsonStr)){
								String name = binding.getValue("stateName");
								highOutputCBox.setValue(name);
								highOutputCBox.setDisable(true);
							}
						}
					}
				}
			}else{
				highOutputCBox.setDisable(false);
				highOutputCBox.setValue(null);
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject)it.next();
					if(cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject)it2.next();
							String name = binding.getValue("stateName");
							highOutputCBox.getItems().add(name);
						}
					}
				}
			}

			// if default state is-non null, set value according to json. Else, allow for population.
			jsonStr = device.getBindingValueFromKey(selectedNode.getValue().name,"defaultState");
			System.out.println(jsonStr);
			if(jsonStr!=null){
				defaultStateCBox.getItems().clear();
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject)it.next();
					if(cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject)it2.next();

							if(binding.getValue("maxStateValue").equals(jsonStr)){
								String name = binding.getValue("stateName");
								defaultStateCBox.setValue(name);
								defaultStateCBox.setDisable(true);
							}
						}
					}
				}
			}else{
				defaultStateCBox.setDisable(false);
				defaultStateCBox.setValue(null);
				Iterator<JsonAbstractValue> it = stateSets.iterator();
				while (it.hasNext()) {
					JsonObject cdef = (JsonObject)it.next();
					if(cdef.getValue("name").equals(stateSet)) {
						Iterator<JsonAbstractValue>it2 = ((JsonArray)cdef.get("oemStateValueRecords")).iterator();
						while (it2.hasNext()) {
							JsonObject binding = (JsonObject)it2.next();
							String name = binding.getValue("stateName");
							defaultStateCBox.getItems().add(name);
						}
					}
				}
			}

		} catch (NullPointerException ex) {
			// TODO Auto-generated catch block
			//ex.printStackTrace();
		}

		this.stateSets = stateSets;
		this.selectedNode = selectedNode;

	}


}