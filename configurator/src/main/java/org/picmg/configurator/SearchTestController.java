//*******************************************************************
//    SearchTestController.java
//
//    This file is a controller class for the State Set Search fxml.
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
import java.util.Scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonResultFactory;

public class SearchTestController implements Initializable {	
	
	// handles to fxml elements
	@FXML private TableView<TableData> stateSetTable;
	@FXML private TableColumn<TableData,String> vendor;
	@FXML private TableColumn<TableData,String> name;
	@FXML private Button searchButton;
	@FXML private TextField searchField;
	@FXML private Button cancelButton;
	
	//*******************************************************************
	// TableData (Class)
	//
	// Data storage class for state set.
	//
	// Member Data:
	//	vendorData - the name of the vendor
	//	nameData   - the name of the state set
	//	fileData   - the name of the file the state set is in
	//
	public static class TableData{
		private final SimpleStringProperty vendorData;
		private final SimpleStringProperty nameData;
		private final SimpleStringProperty fileData;
		
		private TableData(String vendor, String name, String file) {
			vendorData = new SimpleStringProperty(vendor);
			nameData = new SimpleStringProperty(name);
			fileData = new SimpleStringProperty(file);
		}
		
		public String getVendorData() {
			return vendorData.get();
		}
		
		public String getNameData() {
			return nameData.get();
		}
		
		public String getFileData() {
			return fileData.get();
		}
		
		public void setVendorData(String vendor) {
			vendorData.set(vendor);
		}
		
		public void setNameData(String name) {
			nameData.set(name);
		}
		
		public void setFileData(String file) {
			nameData.set(file);
		}
	}
	
	//*******************************************************************
	// returnLine()
	//
	// Helper function for controller, used in parsing
	// the state set json files
	//
	// parameters:
	//	str - characters to skip at the beginning of the line
	//	scn - a scanner used to read the line in
	// returns:
	//	    the value of the json line
	//
	private static String returnLine(String str, Scanner scn) {
		String line = scn.nextLine();
		return (String) line.subSequence(str.length(), line.length()-2);
	}
	
	//*******************************************************************
	// initialize()
	//
	// Key javafx function called by main on startup. Contains
	// all code that is needed to run the GUI
	//
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//create vendor and name arraylists
		File[] stateSets;
		ArrayList<File> list = new ArrayList<File>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(System.getProperty("user.dir")+"/lib/state_sets"))) {
			int i = 0;
			for (Path path : stream) {
				if (!Files.isDirectory(path)) {
					list.add(new File(String.valueOf(path)));
				}
			}
		} catch (IOException e) {
			// unable to find the directory
		}
        stateSets = list.toArray(File[]::new);

        ArrayList<String> vendorList = new ArrayList<String>();
        ArrayList<String> nameList   = new ArrayList<String>();
        ArrayList<String> fileList   = new ArrayList<String>();
        
        // for each file, read in values for name and vendor and store them in their respective arrayLists
        for(File f: stateSets){
        	try {
				Scanner lineReader = new Scanner(f);
				
				fileList.add(f.getName());
				
				lineReader.nextLine();
				nameList.add(returnLine("	\"name\":\"",lineReader));
				lineReader.nextLine();
				lineReader.nextLine();
				vendorList.add(returnLine("	\"vendorName\":\"",lineReader));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        //observable list for data by row
        ObservableList<TableData> data = FXCollections.observableArrayList();
        
        //add arrayList data to observableList
        for(int i =0; i<vendorList.size(); i++) {
        	data.add(new TableData(vendorList.get(i),nameList.get(i),fileList.get(i)));
        }
        
        vendor.setCellValueFactory(new PropertyValueFactory<TableData,String>("vendorData"));
        name.setCellValueFactory(new PropertyValueFactory<TableData,String>("nameData"));
        
        //store data in table
        stateSetTable.setItems(data);
        
        //search button run when clicked
        searchButton.setOnAction(new EventHandler<ActionEvent>(){
        	@Override public void handle(ActionEvent e) {
        		String searchTerm = searchField.getText();
        		stateSetTable.getItems().clear();
        		ObservableList<TableData> searchedData = FXCollections.observableArrayList();
                
        		for(int i=0; i<vendorList.size(); i++) {
        			if(vendorList.get(i).toLowerCase().contains(searchTerm.toLowerCase())||nameList.get(i).toLowerCase().contains(searchTerm.toLowerCase())) {
        				searchedData.add(new TableData(vendorList.get(i),nameList.get(i),fileList.get(i)));
        			}
        		}
        		
                stateSetTable.setItems(searchedData);
                
        	}
        });
        
        //cancel button run when clicked
        cancelButton.setOnAction(new EventHandler<ActionEvent>(){
        	@Override public void handle(ActionEvent e) {
        		Stage activeStage = (Stage) cancelButton.getScene().getWindow();
                activeStage.close();
        	}
        });
        
        //data selection run when clicked
        stateSetTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
            	if (stateSetTable.getSelectionModel().getSelectedItem() != null) {
                    TableData selectedRow = stateSetTable.getSelectionModel().getSelectedItem();
                                        
                    StringTransfer.text=selectedRow.getNameData();
                    
                    Stage activeStage = (Stage) stateSetTable.getScene().getWindow();
                    activeStage.close();
                }
            }
        });

   	}
}
