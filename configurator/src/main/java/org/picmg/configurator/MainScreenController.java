package org.picmg.configurator;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;
import org.picmg.jsonreader.JsonValue;

public class MainScreenController implements Initializable {
	JsonObject appConfig;
	JsonObject hardware;
	JsonObject sensorLib;
	JsonObject effecterLib;
	JsonObject stateLib;
	JsonObject deviceLib;
	JsonObject jdev;	
	JsonObject defaultMeta;
	Device     device;
	@FXML private TreeView<TreeData> treeView;
	@FXML private AnchorPane bindingPane;
	private AnchorPane stateSensorPane;

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
		static Image redDotImage;

		// JsonTreeCell
        //
        // Constructor for the tree cell - initialize the control
        public JsonTreeCell() {
			if (redDotImage==null) {
				ClassLoader classLoader = ClassLoader.getSystemClassLoader();
				InputStream is = classLoader.getResourceAsStream("red_dot.png");
				if (is != null) redDotImage = new Image(is);
			}
		}

		void setError(boolean errorValue){
        	if(errorValue){
				ImageView iv = new ImageView(redDotImage);
				iv.setFitWidth(12);
				iv.setFitHeight(12);
				iv.setVisible(true);
				setGraphic(iv);
			}else{
        		setGraphic(null);
			}
		}

        void initializeIoBindingCell() {
        	setContextMenu(null);
			TreeItem selectedNode = treeView.getSelectionModel().getSelectedItem();
        	TreeItem<TreeData> it = getTreeItem();
			errorCheck(it.getParent());
        	boolean err = getItem().error.getValue();
        	setError(err);
			treeView.getSelectionModel().select(selectedNode);

			getItem().error.addListener((observable, oldValue, newValue) -> {
				boolean errorVal = getItem().error.getValue();
				setError(errorVal);
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
                		((JsonArray)data.parent).remove(data.leaf);
                		device.addFruRecordConfigurationByName(data.leaf.getValue("name"));
           			} else {                	
           				ti.getParent().getChildren().remove(ti);
           				((JsonArray)data.parent).remove(data.leaf);
           			}
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
                    		MenuItem obj = (MenuItem)t.getSource();
                    		String name = obj.getText().substring(12);
                    		
                    		// copy the possible Logical Entity into the configuration
                    		TreeData data = getTreeItem().getValue();
                    		JsonAbstractValue ent = device.addLogicalEntityConfigurationByName(name);
                        	TreeItem<TreeData> entityItem =
                                new TreeItem<TreeData>(new TreeData(data.leaf, ent, "logicalEntity"));
                        	entityItem.setExpanded(true);
                        	getTreeItem().getChildren().add(entityItem);
                        	
                        	// now add bindings and parameters for the entity
                        	JsonArray bindings = (JsonArray)((JsonObject)ent).get("ioBindings");
                        	bindings.forEach(binding -> {
                        		TreeItem<TreeData> ti = new TreeItem<TreeData>(new TreeData(bindings, binding,"ioBinding"));
                        		errorCheck(ti);
                        		entityItem.getChildren().add(ti);
                        	});
                        	JsonArray parameters = (JsonArray)((JsonObject)ent).get("parameters");
                    		entityItem.getChildren().add(new TreeItem<TreeData>(new TreeData(ent, parameters,"parameters")));
							errorCheck(treeView.getRoot());
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
            		JsonArray fruSet = (JsonArray)data.leaf;
            		
            		// create the new fru record
            		JsonObject fruRecord = new JsonObject();
            	
            		// insert the required fields
            		fruRecord.put("name",new JsonValue(getRandomString(25)));
            		fruRecord.put("required",new JsonValue("false"));
            		fruRecord.put("vendorIANA",new JsonValue("412"));            			
            		fruRecord.put("description",new JsonValue("null"));            			
            		fruRecord.put("fields",new JsonArray());
            		
            		// add the fru record to the record set
            		fruSet.add(fruRecord);
            		
            		// create the new menu item for the fru record
                	TreeItem<TreeData> fruRecordItem =
                        new TreeItem<TreeData>(new TreeData(data.leaf, fruRecord, "fruRecord"));
                	getTreeItem().getChildren().add(fruRecordItem);
                }
            });        	            
            mi2.setOnAction(new EventHandler<ActionEvent>() {
            	@Override
            	public void handle(ActionEvent t) {
            		// create a new fru record entry
            		TreeData data = getTreeItem().getValue();
            		JsonArray fruSet = (JsonArray)data.leaf;
            		
            		// create the new fru record
            		JsonObject fruRecord = new JsonObject();
            	
            		// insert the required fields
            		fruRecord.put("name",new JsonValue(getRandomString(25)));
            		fruRecord.put("required",new JsonValue("false"));
           			fruRecord.put("vendorIANA",new JsonValue("null"));
            		fruRecord.put("description",new JsonValue("null"));            			
            		fruRecord.put("fields",new JsonArray());
            		
            		// add the fru record to the record set
            		fruSet.add(fruRecord);
            		
            		// create the new menu item for the fru record
                	TreeItem<TreeData> fruRecordItem =
                        new TreeItem<TreeData>(new TreeData(data.leaf, fruRecord, "fruRecord"));
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
            		//TODO: add code to set up the context pane for the fru collection
            		setText(getItem().nodeType);
            		initializeFruCell();
                	break;
            	case "parameters":
            		//TODO: add code to set up the context pane for the parameters collection
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
    
	void writeFile() {
        try (FileWriter writer = new FileWriter("output.json");
                BufferedWriter bw = new BufferedWriter(writer)) {

               jdev.writeToFile(bw);
               bw.close();
           } catch (IOException e) {
               System.err.format("IOException: %s%n", e);
           }
	}

	
	// populate the tree with a simplified structure
	void populateTree(TreeItem<TreeData> localRoot) {
		// add branch for FRU
		JsonObject configuration = (JsonObject)device.getJson().get("configuration");
	    JsonArray fruRecords = (JsonArray)(configuration).get("fruRecords");	    
		TreeItem<TreeData> fruRecordsItem = new TreeItem<TreeData>(new TreeData(configuration,fruRecords,"fruRecords"));
	    fruRecordsItem.setExpanded(true);
		localRoot.getChildren().add(fruRecordsItem);

	    // add leaf nodes for any FRU records that already exist in the configuration
	    fruRecords.forEach(record -> {
            // iterate to populate the rest of the tree
			TreeItem<TreeData> fruLeafItem = new TreeItem<TreeData>(new TreeData(fruRecords, record, "fruRecord"));
            fruRecordsItem.getChildren().add(fruLeafItem);	    
            fruLeafItem.setExpanded(true);
	    });

		// add branch for Logical Entities
	    JsonArray logicalEntities = (JsonArray)(configuration).get("logicalEntities");	    
	    TreeItem<TreeData> logicalEntitiesItem = new TreeItem<TreeData>(new TreeData(configuration,logicalEntities,"logicalEntities"));
	    logicalEntitiesItem.setExpanded(true);
	    
	    // add leaf nodes for any logical entities records that already exist in the configuration
	    logicalEntities.forEach(entity -> {
            // iterate to populate the rest of the tree
			TreeItem<TreeData> entityItem = new TreeItem<TreeData>(new TreeData(logicalEntities,entity,"logicalEntity"));
            entityItem.setExpanded(true);
            logicalEntitiesItem.getChildren().add(entityItem);

        	// now add bindings and parameters for the entity
        	JsonArray bindings = (JsonArray)((JsonObject)entity).get("ioBindings");
        	bindings.forEach(binding -> {
        		TreeItem<TreeData> ti = new TreeItem<TreeData>(new TreeData(bindings, binding,"ioBinding"));
				entityItem.getChildren().add(ti);
        	});                     	
        	JsonArray parameters = (JsonArray)((JsonObject)entity).get("parameters");
    		entityItem.getChildren().add(new TreeItem<TreeData>(new TreeData(entity, parameters,"parameters")));
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
		String path = System.getProperty("user.dir")+"/lib/"+folder+"/";
		File   fileobj = new File(path);
		File[] files = fileobj.listFiles();
		for (int i=0;i<files.length;i++) {
			String file = path+files[i].getName();
			JsonObject obj = (JsonObject)factory.buildFromFile(Paths.get(file));
			ary.add(obj);
		}
	}
	
	// helper function that clears all panes
	private void clearPanes() {
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
		} catch (IOException e) {
		}
	}

	// Checks for errors in all logical entites. displays error icon if found.
	public void errorCheck(TreeItem<TreeData> treeNode) {

		if (treeNode.getChildren().isEmpty()) {
			// Do nothing if the node is empty.
		} else {

			// Otherwise, loop through every child
			for (TreeItem<TreeData> node : treeNode.getChildren()) {
				if (node.getValue().nodeType=="ioBinding") {
					node.setExpanded(true);
					treeView.getSelectionModel().select(node);
					TreeItem<TreeData> selectedNode = treeView.getSelectionModel().getSelectedItem();
					try {
						String name = selectedNode.getValue().leaf.getValue("name");
						String bindingType = device.getBindingValueFromKey(selectedNode.getValue().name, "bindingType");
						switch (bindingType) {
							case "stateEffecter":
								//clearPanes();
								break;
							case "stateSensor":
								//clearPanes();
								stateSensorController.update(device, treeView.getSelectionModel().getSelectedItem(), (JsonArray) stateLib.get("stateSets"));
								if (stateSensorController.isError()) {
									selectedNode.getValue().error.setValue(true);
									ClassLoader classLoader = ClassLoader.getSystemClassLoader();
									InputStream is = classLoader.getResourceAsStream("red_dot.png");
									ImageView iv = new ImageView(new Image(is));
									iv.setFitWidth(12);
									iv.setFitHeight(12);
									iv.setVisible(true);
									selectedNode.setGraphic(iv);

								} else {
									selectedNode.getValue().error.setValue(false);
									selectedNode.setGraphic(null);
								}
								break;
							case "numericEffecter":
								//clearPanes();
								break;
							case "numericSensor":
								//clearPanes();
								numericSensorController.update(device, treeView.getSelectionModel().getSelectedItem());
								if (numericSensorController.isError()) {
									selectedNode.getValue().error.setValue(true);
									ClassLoader classLoader = ClassLoader.getSystemClassLoader();
									InputStream is = classLoader.getResourceAsStream("red_dot.png");
									ImageView iv = new ImageView(new Image(is));
									iv.setFitWidth(12);
									iv.setFitHeight(12);
									iv.setVisible(true);
									selectedNode.setGraphic(iv);

								} else {
									selectedNode.getValue().error.setValue(false);
									selectedNode.setGraphic(null);
								}
								break;

							default:
								clearPanes();
						}

					}catch(NullPointerException ex){
						//catch block
					}
				} else {
					node.setExpanded(true);
				}



				// If the current node has children then check
				if (!treeNode.getChildren().isEmpty()) {
					errorCheck(node);
				}

			}

		}

	}

	// Clears error value in all treeItems by setting the error value to false.
	public void errorClear(TreeItem<TreeData> treeNode) {
		if (treeNode.getChildren().isEmpty()) {
			// Do nothing if the node is empty.
		} else {
			// Otherwise, loop through every child
			for (TreeItem<TreeData> node : treeNode.getChildren()) {
				if (node.getValue().nodeType=="ioBinding") {
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
					errorCheck(node);
				}
			}
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setPaneBindings();

        JsonResultFactory factory = new JsonResultFactory();
        appConfig = (JsonObject)factory.buildFromResource("config.json");
        hardware = (JsonObject)factory.buildFromResource("microsam_new2.json");
        defaultMeta = (JsonObject)factory.buildFromResource("default_meta.json");

        // Load the libraries from the resource folders - these are the default
        // picmg libraries and sensors
        sensorLib = new JsonObject();
        addLibrariesFromResourceFolder(sensorLib,"sensors","sensors");
        effecterLib = new JsonObject();
        addLibrariesFromResourceFolder(effecterLib,"effecters","effecters");
        stateLib = new JsonObject();
        addLibrariesFromResourceFolder(stateLib,"stateSets","state_sets");
        deviceLib = new JsonObject();
        addLibrariesFromResourceFolder(deviceLib,"devices","devices");

        device = new Device(hardware);

        device.canEntityBeAdded("servo1");

        // create the tree based on the device
        TreeItem<TreeData> rootNode = treeView.getRoot();
        rootNode = new TreeItem<TreeData>(new TreeData(null,null,"device"));
        treeView.setRoot(rootNode);
        rootNode.setExpanded(true);
        populateTree(rootNode);

        treeView.setEditable(true);
        treeView.setCellFactory(new Callback<TreeView<TreeData>,TreeCell<TreeData>>(){
            @Override
            public TreeCell<TreeData> call(TreeView<TreeData> p) {
                return new JsonTreeCell();
            }
        });

		//treeview logic is contained in this event listener
        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
            	Node treeNode = event.getPickResult().getIntersectedNode();

				if (treeNode instanceof Text || (treeNode instanceof TreeCell && ((TreeCell) treeNode).getText() != null)) {
                    TreeItem<TreeData> selectedNode = treeView.getSelectionModel().getSelectedItem();
					errorClear(treeView.getRoot());
					errorCheck(treeView.getRoot());
					treeView.getSelectionModel().select(selectedNode);
					clearPanes();

					//checking if click is on ioBinding
                    if(selectedNode.getValue().nodeType=="ioBinding") {

						// switch on type of binding
						String name = selectedNode.getValue().leaf.getValue("name");
      					String bindingType = device.getBindingValueFromKey(selectedNode.getValue().name,"bindingType");
                    	switch(bindingType) {
	                    	case "stateEffecter":
	                    		clearPanes();
	                    		stateEffecterContent.setVisible(true);
								break;
	                    	case "stateSensor":
	                    		clearPanes();
	                    		stateSensorContent.setVisible(true);
	                    		stateSensorController.update(device, treeView.getSelectionModel().getSelectedItem(), (JsonArray)stateLib.get("stateSets"));
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

            		}
                    else if(selectedNode.getValue().nodeType=="parameters") {
						clearPanes();
						JsonObject capabilitiesEntity = (JsonObject)selectedNode.getValue().parent;
						JsonArray capabilitiesParameters = (JsonArray)capabilitiesEntity.get("parameters");
						parameterPaneController.update((JsonArray) selectedNode.getValue().leaf, capabilitiesParameters);
						parameterContent.setVisible(true);
					}
					else if(selectedNode.getValue().nodeType=="fruRecord") {
						clearPanes();
						fruPaneController.update((JsonObject) selectedNode.getValue().leaf,
								(JsonObject) device.getCapabilitiesFruRecordByName(selectedNode.getValue().leaf.getValue("name")));
						fruContent.setVisible(true);
					}
				}
			}
        });
	}
}