package configurator;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import jsonreader.JsonAbstractValue;
import jsonreader.JsonArray;
import jsonreader.JsonObject;
import jsonreader.JsonResultFactory;
import jsonreader.JsonValue;

public class MainScreenController implements Initializable {
	// TODO Auto-generated method stub
	JsonObject appConfig;
	JsonObject hardware;
	JsonObject sensorLib;
	JsonObject effecterLib;
	JsonObject stateLib;
	JsonObject deviceLib;
	JsonObject jdev;	
	JsonObject defaultMeta;
	Device     device;
	@FXML private TextArea configText;
	@FXML private TreeView<TreeData> treeView;

	// this class associates the relevant data with each node in the tree.
	private class TreeData {
		public JsonAbstractValue parent;
		public JsonAbstractValue leaf;
		public String nodeType;
		
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
        }
 
        void initializeIoBindingCell() {
        	setContextMenu(null);
        	
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
                        		entityItem.getChildren().add(ti);
                        	});                     	
                        	JsonArray parameters = (JsonArray)((JsonObject)ent).get("parameters");
                    		entityItem.getChildren().add(new TreeItem<TreeData>(new TreeData(ent, parameters,"parameters")));
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
            		fruRecord.put("name",new JsonValue("null"));
            		fruRecord.put("required",new JsonValue("false"));
            		fruRecord.put("vendorIANA",new JsonValue("412"));            			
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
            		fruRecord.put("name",new JsonValue("null"));
            		fruRecord.put("required",new JsonValue("false"));
           			fruRecord.put("vendorIANA",new JsonValue("null"));
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
            	setGraphic(null);
            	
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
		String path = classLoader.getResource(folder).getPath();
		File   fileobj = new File(path);
		File[] files = fileobj.listFiles();
		for (int i=0;i<files.length;i++) {
			JsonObject obj = (JsonObject)factory.buildFromResource(folder+"/"+files[i].getName());
			ary.add(obj);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        JsonResultFactory factory = new JsonResultFactory();
        appConfig = (JsonObject)factory.buildFromResource("config.json");
        hardware = (JsonObject)factory.buildFromResource("devices/microsam_new.json");
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
	}
}
