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
import javafx.util.Callback;
import org.picmg.jsonreader.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
	static Image redDotImage;
	static Image yellowDotImage;
	JsonObject hardware;
	JsonObject sensorLib;
	JsonObject effecterLib;
	JsonObject stateLib;
	JsonObject deviceLib;
	Device     device;
	SimpleBooleanProperty configurationError;

	@FXML private TreeView<TreeData> treeView;
	@FXML private AnchorPane bindingPane;

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

	// this class associates the relevant data with each node in the tree.
	protected class TreeData {
		public JsonAbstractValue parent;
		public JsonAbstractValue leaf;
		public String nodeType;
		public String name;
		public boolean required;
		public SimpleBooleanProperty error = new SimpleBooleanProperty(false);

		public TreeData() {
			nodeType = "unknown";
		}


		public TreeData(JsonAbstractValue parent, JsonAbstractValue leaf, String nodeType) {
			this.parent = parent;
			this.leaf = leaf;
			this.nodeType = nodeType;
		}
	}

	private final class JsonTreeCell extends TreeCell<TreeData> {
		private ContextMenu contextMenu;

		// JsonTreeCell
		//
		// Constructor for the tree cell - initialize the control
		public JsonTreeCell() {
			if (redDotImage==null) {
				ClassLoader classLoader = ClassLoader.getSystemClassLoader();
				InputStream is = classLoader.getResourceAsStream("red_dot.png");
				if (is != null) redDotImage = new Image(is);
			}
			if (yellowDotImage==null) {
				ClassLoader classLoader = ClassLoader.getSystemClassLoader();
				InputStream is = classLoader.getResourceAsStream("yellow_dot.png");
				if (is != null) yellowDotImage = new Image(is);
			}
		}


		void setError(boolean errorValue,String req){
			if(errorValue){
				boolean required = true;
				if(req!=null){
					if(req.equals("false")){
						required = false;
					}
				}else{
					required = false;
				}
				if(required){
					ImageView iv = new ImageView(redDotImage);
					iv.setFitWidth(12);
					iv.setFitHeight(12);
					iv.setVisible(true);
					setGraphic(iv);
				}else{
					ImageView iv = new ImageView(yellowDotImage);
					iv.setFitWidth(12);
					iv.setFitHeight(12);
					iv.setVisible(true);
					setGraphic(iv);
				}

			}else{
				setGraphic(null);
			}
		}

		void initializeIoBindingCell() {
			setContextMenu(null);
			TreeItem selectedNode = treeView.getSelectionModel().getSelectedItem();
			TreeItem<TreeData> it = getTreeItem();

			errorCheck();

			boolean err = getItem().error.getValue();
			setError(err,getItem().leaf.getValue("required"));
			treeView.getSelectionModel().select(selectedNode);

			getItem().error.addListener((observable, oldValue, newValue) -> {
				boolean errorVal = getItem().error.getValue();
				setError(errorVal,getItem().leaf.getValue("required"));
				errorCheck();
			});

			// set the behavior when the cell is clicked by the mouse
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent t) {
					//TODO: add code to set up the context pane for the io binding

				}
			});

		}

		void initializeFruRecordCell() {
			// set the behavior when the cell is clicked by the mouse
			TreeData data = getTreeItem().getValue();
			contextMenu = new ContextMenu();
			MenuItem mi = new MenuItem("Delete Record");
			if (data.leaf.getBoolean("required")) {
				mi.setText("Restore Defaults");
			}
			contextMenu.getItems().add(mi);
			setContextMenu(contextMenu);

			errorCheck();
			setError(getItem().error.getValue(),getItem().leaf.getValue("required"));

			getItem().error.addListener((observable, oldValue, newValue) -> {
				if (getItem()==null) return;
				boolean errorVal = getItem().error.getValue();
				setError(errorVal,getItem().leaf.getValue("required"));
				errorCheck();
			});

			// set the handler for the menu item
			mi.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					// get the name of the entity that was selected
					TreeItem<TreeData> ti = getTreeItem();
					TreeData data = ti.getValue();

					// remove the Logical Entity from the configuration
					if (data.leaf.getBoolean("required")) {
						// restore defaults by removing and adding the logical entity
						((JsonArray) data.parent).remove(data.leaf);
						device.addFruRecordConfigurationByName(data.leaf.getValue("name"));
					} else {
						ti.getParent().getChildren().remove(ti);
						((JsonArray) data.parent).remove(data.leaf);
					}
					setPaneContent();
				}
			});
		}

		void initializeParametersCell() {
			setContextMenu(null);

			// set the behavior when the cell is clicked by the mouse
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent t) {
					//TODO: add code to set up the context pane for the parameters
				}
			});
		}

		void initializeLogicalEntityCell() {
			// initialize the menu
			TreeData data = getTreeItem().getValue();
			setText(data.leaf.getValue("name"));
			contextMenu = new ContextMenu();
			MenuItem mi = new MenuItem("Delete Entity");
			if (data.leaf.getBoolean("required")) {
				mi.setText("Restore Defaults");
			}
			contextMenu.getItems().add(mi);
			// set the handler for the menu item
			mi.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					// get the name of the entity that was selected
					TreeItem<TreeData> ti = getTreeItem();
					TreeData data = ti.getValue();

					// remove the Logical Entity from the configuration
					if (data.leaf.getBoolean("required")) {
						// restore defaults by removing and adding the logical entity
						device.removeLogicalEntityConfigurationByName(data.leaf.getValue("name"));
						device.addLogicalEntityConfigurationByName(data.leaf.getValue("name"));
					} else {
						errorClear(ti);
						ti.getParent().getChildren().remove(ti);
						device.removeLogicalEntityConfigurationByName(data.leaf.getValue("name"));
					}
				}
			});
			setContextMenu(contextMenu);
		}

		void initializeLogicalEntitiesCell() {
			// initialize the menu
			if (contextMenu!=null) {
				contextMenu.getItems().clear();
			} else {
				contextMenu = new ContextMenu();
			}

			// add entities that can be added to the menu
			ArrayList<String> possibles = device.getListOfPossibleEntities();
			if (possibles.size()>0) {
				possibles.forEach(possible->{
					// add the menu item
					MenuItem mi = new MenuItem("add entity: " + possible);
					contextMenu.getItems().add(mi);

					// set the handler for the menu item
					mi.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent t) {
							// get the name of the entity that was selected
							MenuItem obj = (MenuItem) t.getSource();
							String name = obj.getText().substring(12);

							// copy the possible Logical Entity into the configuration
							TreeData data = getTreeItem().getValue();
							JsonAbstractValue ent = device.addLogicalEntityConfigurationByName(name);
							TreeItem<TreeData> entityItem =
									new TreeItem<>(new TreeData(data.leaf, ent, "logicalEntity"));
							entityItem.setExpanded(true);
							Node temp = entityItem.getGraphic();
							entityItem.setGraphic(temp);
							getTreeItem().getChildren().add(entityItem);

							// now add bindings and parameters for the entity
							JsonArray bindings = (JsonArray) ((JsonObject) ent).get("ioBindings");
							bindings.forEach(binding -> {
								TreeItem<TreeData> ti = new TreeItem<>(new TreeData(bindings, binding, "ioBinding"));
								//errorCheck(ti);
								entityItem.getChildren().add(ti);
							});
							JsonArray parameters = (JsonArray) ((JsonObject) ent).get("parameters");
							entityItem.getChildren().add(new TreeItem<>(new TreeData(ent, parameters, "parameters")));
							errorCheck();
						}
					});
				});
			} else {
				MenuItem mi = new MenuItem("none");
				mi.setDisable(true);
				contextMenu.getItems().add(mi);
			}

			setContextMenu(contextMenu);
		}

		// this function returns a random string of alphanumeric characters with a
		// length that matches the given length.
		String getRandomString(int len)
		{
			StringBuilder result = new StringBuilder();
			Random rand = new Random(System.currentTimeMillis());

			for (int i=0;i<len;i++) {
				int charCode = rand.nextInt(10+26+26);
				if (charCode<10) {
					// number
					result.append(((char)(48+charCode)));
				}
				else if ((charCode>=10)&&(charCode<10+26)) {
					// upper case letter
					result.append(((char)(65+charCode - 10)));
				}
				else {
					// lower case letter
					result.append(((char)(97+charCode-10-26)));
				}

			}
			return result.toString();
		}

		void initializeFruCell() {
			// initialize the menu
			if (contextMenu!=null) {
				contextMenu.getItems().clear();
			} else {
				contextMenu = new ContextMenu();
			}

			// add entities that can be added to the menu
			MenuItem mi  = new MenuItem("add standard fru record");
			MenuItem mi2 = new MenuItem("add OEM fru record");
			contextMenu.getItems().add(mi);
			contextMenu.getItems().add(mi2);
			setContextMenu(contextMenu);

			// set the handler for the menu item
			mi.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					// create a new fru record entry
					TreeData data = getTreeItem().getValue();
					JsonArray fruSet = (JsonArray) data.leaf;

					// create the new fru record
					JsonObject fruRecord = new JsonObject();

					// insert the required fields
					fruRecord.put("name", new JsonValue(getRandomString(25)));
					fruRecord.put("required", new JsonValue("false"));
					fruRecord.put("vendorIANA", new JsonValue("412"));
					fruRecord.put("description", new JsonValue("null"));
					fruRecord.put("fields", new JsonArray());

					// add the fru record to the record set
					fruSet.add(fruRecord);

					// create the new menu item for the fru record
					TreeItem<TreeData> fruRecordItem =
							new TreeItem<>(new TreeData(data.leaf, fruRecord, "fruRecord"));
					getTreeItem().getChildren().add(fruRecordItem);
				}
			});
			mi2.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					// create a new fru record entry
					TreeData data = getTreeItem().getValue();
					JsonArray fruSet = (JsonArray) data.leaf;

					// create the new fru record
					JsonObject fruRecord = new JsonObject();

					// insert the required fields
					fruRecord.put("name", new JsonValue(getRandomString(25)));
					fruRecord.put("required", new JsonValue("false"));
					fruRecord.put("vendorIANA", new JsonValue("null"));
					fruRecord.put("description", new JsonValue("null"));
					fruRecord.put("fields", new JsonArray());

					// add the fru record to the record set
					fruSet.add(fruRecord);

					// create the new menu item for the fru record
					TreeItem<TreeData> fruRecordItem =
							new TreeItem<>(new TreeData(data.leaf, fruRecord, "fruRecord"));
					getTreeItem().getChildren().add(fruRecordItem);
				}
			});
		}

		@Override
		public void updateItem(TreeData item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				switch (item.nodeType) {
					case "logicalEntities":
						setText(getItem().nodeType);
						initializeLogicalEntitiesCell();
						break;
					case "logicalEntity":
						initializeLogicalEntityCell();
						break;
					case "ioBinding":
						setText(getItem().leaf.getValue("name"));
						String name = getItem().leaf.getValue("name");
						item.name = getItem().leaf.getValue("name");
						initializeIoBindingCell();
						break;
					case "fruRecord":
						setText(getItem().nodeType);
						initializeFruRecordCell();
						break;
					case "fruRecords":
						setText(getItem().nodeType);
						initializeFruCell();
						break;
					case "parameters":
						setText(getItem().nodeType);
						initializeParametersCell();
						break;
					default:
						// do nothing
						setText(getItem().nodeType);
						break;
				}

			}
		}
	}

	/**
	 * export the configuration to the specified output file.
	 * @param outputFile - the name of the file to output to
	 */
	public void exportConfiguration(File outputFile) {
		device.exportConfiguration(outputFile);
	}


	// populate the tree with a simplified structure
	void populateTree(TreeItem<TreeData> localRoot) {
		// add branch for FRU
		JsonObject configuration = (JsonObject)device.getJson().get("configuration");
		JsonArray fruRecords = (JsonArray)(configuration).get("fruRecords");
		TreeItem<TreeData> fruRecordsItem = new TreeItem<>(new TreeData(configuration, fruRecords, "fruRecords"));
		fruRecordsItem.setExpanded(true);
		localRoot.getChildren().add(fruRecordsItem);

		// add leaf nodes for any FRU records that already exist in the configuration
		fruRecords.forEach(record -> {
			// iterate to populate the rest of the tree
			TreeItem<TreeData> fruLeafItem = new TreeItem<>(new TreeData(fruRecords, record, "fruRecord"));
			fruRecordsItem.getChildren().add(fruLeafItem);
			fruLeafItem.setExpanded(true);
		});

		// add branch for Logical Entities
		JsonArray logicalEntities = (JsonArray)(configuration).get("logicalEntities");
		TreeItem<TreeData> logicalEntitiesItem = new TreeItem<>(new TreeData(configuration, logicalEntities, "logicalEntities"));
		logicalEntitiesItem.setExpanded(true);

		// add leaf nodes for any logical entities records that already exist in the configuration
		logicalEntities.forEach(entity -> {
			// iterate to populate the rest of the tree
			TreeItem<TreeData> entityItem = new TreeItem<>(new TreeData(logicalEntities, entity, "logicalEntity"));
			entityItem.setExpanded(true);
			logicalEntitiesItem.getChildren().add(entityItem);

			// now add bindings and parameters for the entity
			JsonArray bindings = (JsonArray)((JsonObject)entity).get("ioBindings");
			bindings.forEach(binding -> {
				TreeItem<TreeData> ti = new TreeItem<>(new TreeData(bindings, binding, "ioBinding"));
				entityItem.getChildren().add(ti);
			});
			JsonArray parameters = (JsonArray)((JsonObject)entity).get("parameters");
			entityItem.getChildren().add(new TreeItem<>(new TreeData(entity, parameters, "parameters")));
		});
		localRoot.getChildren().add(logicalEntitiesItem);
	}

	// addLibrariesFromResourceFolder()
	//
	// add json objects into a specified array within a library json library object.
	//
	// lib - the library object to add the library entries to.
	// key - the key for the array within the libary object to add the library elements to
	// folder - the resource folder to load library elements from
	void addLibrariesFromResourceFolder(JsonObject lib, String key, String folder) {
		JsonResultFactory factory = new JsonResultFactory();

		// get the array within the library to add the library entries to
		if (lib.get(key)==null) {
			// here if the key does not yet exist - add the array object
			JsonArray ary = new JsonArray();
			lib.put(key,ary);
		}
		JsonArray ary = (JsonArray)lib.get(key);

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		String path = App.getBasePath()+"/lib/"+folder+"/";
		File   fileobj = new File(path);
		File[] files = fileobj.listFiles();
		for (File value : files) {
			String file = path + value.getName();
			JsonObject obj = (JsonObject) factory.buildFromFile(Paths.get(file));
			ary.add(obj);
		}
	}

	// helper function that clears all panes
	public void clearPanes() {
		stateSensorContent.setVisible(false);
		stateEffecterContent.setVisible(false);
		numericSensorContent.setVisible(false);
		numericEffecterContent.setVisible(false);
		fruContent.setVisible(false);
		parameterContent.setVisible(false);
	}

	/**
	 * setPaneBindings()
	 * This helper function binds each FXML pane to the conext-sensitive
	 * right pane in this view.
	 */
	void setPaneBindings() {
		try {
			// setup binding panes
			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("stateSensorPane.fxml"));
				stateSensorContent = loader.load();
				stateSensorController = loader.getController();
				bindingPane.getChildren().add(stateSensorContent);
				stateSensorContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("stateEffecterPane.fxml"));
				stateEffecterContent = loader.load();
				stateEffecterController = loader.getController();
				bindingPane.getChildren().add(stateEffecterContent);
				stateEffecterContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("numericSensorPane.fxml"));
				numericSensorContent = loader.load();
				numericSensorController = loader.getController();
				bindingPane.getChildren().add(numericSensorContent);
				numericSensorContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("numericEffecterPane.fxml"));
				numericEffecterContent = loader.load();
				numericEffecterController = loader.getController();
				bindingPane.getChildren().add(numericEffecterContent);
				numericEffecterContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FruPane.fxml"));
				fruContent = loader.load();
				fruPaneController = loader.getController();
				fruPaneController.setMainController(this);
				bindingPane.getChildren().add(fruContent);
				fruContent.setVisible(false);
			}

			{
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ParameterPane.fxml"));
				parameterContent = loader.load();
				parameterPaneController = loader.getController();
				bindingPane.getChildren().add(parameterContent);
				parameterContent.setVisible(false);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * walk the tree searching for errors.  set error indicators as found.
	 * @param treeNode - the current tree node to recurse
	 * @returns true if error found, otherwise, false
	 */
	private boolean errorChecker(TreeItem<TreeData> treeNode, boolean error) {
		boolean result = error;
		if (treeNode.getChildren().isEmpty()) {
			// Do nothing if the node is empty.
		} else {
			// Otherwise, loop through every child
			for (TreeItem<TreeData> node : treeNode.getChildren()) {
				result |= errorChecker(node,error);
			}
		}
		try {
			String type = treeNode.getValue().nodeType;
			if (treeNode.getValue().nodeType.equals("ioBinding")) {
				String name = treeNode.getValue().leaf.getValue("name");
				String bindingType = device.getBindingValueFromKey(treeNode.getValue().name, "bindingType");
				switch (bindingType) {
					case "stateEffecter":
						break;
					case "stateSensor":
						if (stateSensorController.isError()) {
							treeNode.getValue().error.setValue(true);
							ClassLoader classLoader = ClassLoader.getSystemClassLoader();
							InputStream is = classLoader.getResourceAsStream("red_dot.png");
							ImageView iv = new ImageView(new Image(is));
							iv.setFitWidth(12);
							iv.setFitHeight(12);
							iv.setVisible(true);
							treeNode.setGraphic(iv);
							result |= true;
						} else {
							treeNode.getValue().error.setValue(false);
							treeNode.setGraphic(null);
						}
						break;
					case "numericEffecter":
						//clearPanes();
						break;
					case "numericSensor":
						if (!Device.isBindingValid((JsonObject)treeNode.getValue().leaf)) {
							treeNode.getValue().error.setValue(true);
							ClassLoader classLoader = ClassLoader.getSystemClassLoader();
							InputStream is = classLoader.getResourceAsStream("red_dot.png");
							ImageView iv = new ImageView(new Image(is));
							iv.setFitWidth(12);
							iv.setFitHeight(12);
							iv.setVisible(true);
							treeNode.setGraphic(iv);
							result |= true;
						} else {
							treeNode.getValue().error.setValue(false);
							treeNode.setGraphic(null);
						}
						break;

					default:
						clearPanes();
				}
			}
			else {
				if (treeNode.getValue().nodeType.equals("fruRecord")) {
					if (!Device.isFruRecordValid((JsonObject)treeNode.getValue().leaf)) {
						treeNode.getValue().error.setValue(true);
						ClassLoader classLoader = ClassLoader.getSystemClassLoader();
						InputStream is = classLoader.getResourceAsStream("red_dot.png");
						ImageView iv = new ImageView(new Image(is));
						iv.setFitWidth(12);
						iv.setFitHeight(12);
						iv.setVisible(true);
						treeNode.setGraphic(iv);
						result |= true;
					} else {
						treeNode.getValue().error.setValue(false);
						treeNode.setGraphic(null);
					}
				}
			}
		} catch(NullPointerException ex){
			//catch block
		}
		return result;
	}

	// Checks for errors in all tree nodes. displays error icon if found.
	public void errorCheck() {
		configurationError.set(true);
		if ((hardware != null) && (device != null)) {
			configurationError.set(errorChecker(treeView.getRoot(), false));
		}
	}

	// Clears error value in all treeItems by setting the error value to false.
	public void errorClear(TreeItem<TreeData> treeNode) {
		if (treeNode.getChildren().isEmpty()) {
			// Do nothing if the node is empty.
		} else {
			// Otherwise, loop through every child
			for (TreeItem<TreeData> node : treeNode.getChildren()) {
				if (node.getValue().nodeType.equals("ioBinding")) {
					node.setExpanded(true);
					treeView.getSelectionModel().select(node);
					TreeItem<TreeData> selectedNode = treeView.getSelectionModel().getSelectedItem();
					try {
						selectedNode.getValue().error.setValue(false);
					}catch(NullPointerException ex){
						//catch block
					}
				} else {
					node.setExpanded(true);
				}
				// If the current node has children then check
				if (!treeNode.getChildren().isEmpty()) {
					errorClear(node);
				}
			}
		}
	}

	/**
	 * Get the error property for the current device.  The error property is used to signal that an error condition
	 * exists.  As long as an error exists, exporting the device is not allowed.
	 * @return the error property
	 */
	public SimpleBooleanProperty getErrorProperty() {
		return configurationError;
	}

	/**
	 * Attempt to reset the device to its default values
	 */
	public void resetDevice() {
		configurationError.set(true);
		// create a copy of the hardware file to be configured
		newDevice();
		clearPanes();
		showTree();
	}

	/**
	 * Attempt to load a device capabilities file from the specified file path
	 * @param filePath The path of the capabilities file to load
	 */
	public void loadDevice(File filePath)
	{
		if(filePath != null)
		{
			try {
				JsonResultFactory factory = new JsonResultFactory();
				hardware = (JsonObject)factory.buildFromFile(filePath.toPath());
			} catch (Exception e) {
				App.showErrorDlg("Error Reading Input File");
				return;
			}
			clearPanes();
			try {
				newDevice();
			} catch (Exception e) {
				App.showErrorDlg("Input file error.  It appears the requested file is not a device file.");
				return;
			}
			showTree();
		}
	}

	public void showTree()
	{
		treeView.setVisible(true);
	}

	/**
	 * Attempt to load an existing configuration file from the specified file path.
	 * @param filePath The path of the configuration file to load
	 */
	public void loadConfig(File filePath)
	{
		if (filePath != null) {
			try {
				JsonResultFactory factory = new JsonResultFactory();
				hardware = (JsonObject) factory.buildFromFile(filePath.toPath());
			} catch (Exception e) {
				App.showErrorDlg("Error Reading Input File");
				return;
			}
			clearPanes();
			try {
				loadDevice();
			} catch (Exception e) {
				App.showErrorDlg("Input file error.  It appears the requested file is not a configuration file.");
				return;
			}
			showTree();
		}
	}

	/**
	 * This method loads the tree with a device node as the root.
	 */
	public void newDevice()
	{
		if (hardware == null) return;

		// create the tree based on the device
		treeView.setVisible(false);
		configurationError.set(true);
		device = new Device(hardware);
		treeView.setId("device");
		TreeItem<TreeData> rootNode = treeView.getRoot();
		rootNode = new TreeItem<>(new TreeData(null, null, "device"));
		treeView.setRoot(rootNode);
		rootNode.setExpanded(true);
		populateTree(rootNode);
	}


	public void loadDevice()
	{
		// create the tree based on the device
		treeView.setVisible(false);
		configurationError.set(true);
		device = new Device(hardware);
		device.loadDeviceConfig(hardware);
		treeView.setId("device");
		TreeItem<TreeData> rootNode = treeView.getRoot();
		rootNode = new TreeItem<>(new TreeData(null, null, "device"));
		treeView.setRoot(rootNode);
		rootNode.setExpanded(true);
		populateTree(rootNode);
	}

	private void setPaneContent() {
		clearPanes();
		TreeItem<TreeData> selectedNode = treeView.getSelectionModel().getSelectedItem();
		if (selectedNode == null) return;
		treeView.getSelectionModel().select(selectedNode);

		//checking if click is on ioBinding
		if (selectedNode.getValue().nodeType.equals("ioBinding")) {

			// switch on type of binding
			String name = selectedNode.getValue().leaf.getValue("name");
			String bindingType = device.getBindingValueFromKey(selectedNode.getValue().name, "bindingType");
			switch (bindingType) {
				case "stateEffecter":
					clearPanes();
					stateEffecterContent.setVisible(true);
					break;
				case "stateSensor":
					clearPanes();
					stateSensorContent.setVisible(true);
					stateSensorController.update(device, treeView.getSelectionModel().getSelectedItem(), (JsonArray) stateLib.get("stateSets"));
					break;
				case "numericEffecter":
					clearPanes();
					numericEffecterContent.setVisible(true);
					break;
				case "numericSensor":
					clearPanes();
					numericSensorContent.setVisible(true);
					numericSensorController.update(device, treeView.getSelectionModel().getSelectedItem());
					break;
				default:
					clearPanes();
			}

		} else if (selectedNode.getValue().nodeType.equals("parameters")) {
			clearPanes();
			JsonObject capabilitiesEntity = (JsonObject) selectedNode.getValue().parent;
			JsonArray capabilitiesParameters = (JsonArray) capabilitiesEntity.get("parameters");
			parameterPaneController.update((JsonArray) selectedNode.getValue().leaf, capabilitiesParameters);
			parameterContent.setVisible(true);
		} else if (selectedNode.getValue().nodeType.equals("fruRecord")) {
			clearPanes();
			fruPaneController.update(treeView.getSelectionModel().getSelectedItem(), (JsonObject) selectedNode.getValue().leaf,
					device.getCapabilitiesFruRecordByName(selectedNode.getValue().leaf.getValue("name")));
			fruContent.setVisible(true);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setPaneBindings();

		configurationError = new SimpleBooleanProperty();
		configurationError.set(true);

		// load the default hardware profile
		JsonResultFactory factory = new JsonResultFactory();
		//hardware = (JsonObject)factory.buildFromResource("microsam_new2.json");

		// Load the libraries from the resource folders - these are the default
		// picmg libraries and sensors
		sensorLib = new JsonObject();
		try{
			addLibrariesFromResourceFolder(sensorLib,"sensors","sensors");
			effecterLib = new JsonObject();
			addLibrariesFromResourceFolder(effecterLib,"effecters","effecters");
			stateLib = new JsonObject();
			addLibrariesFromResourceFolder(stateLib,"stateSets","state_sets");
			deviceLib = new JsonObject();
			addLibrariesFromResourceFolder(deviceLib,"devices","devices");
			treeView.setEditable(true);
		}catch (Exception e){}
		newDevice();
		treeView.setCellFactory(new Callback<TreeView<TreeData>, TreeCell<TreeData>>() {
			@Override
			public TreeCell<TreeData> call(TreeView<TreeData> p) {
				return new JsonTreeCell();
			}
		});

		//treeview logic is contained in this event listener
		treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Node treeNode = event.getPickResult().getIntersectedNode();

				if (treeNode instanceof Text || (treeNode instanceof TreeCell && ((TreeCell) treeNode).getText() != null)) {
					setPaneContent();
				}
			}
		});
	}
}