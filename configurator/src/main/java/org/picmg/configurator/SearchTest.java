package org.picmg.configurator;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class SearchTest extends Application {
	@Override
    public void start(Stage stage) { 
	    Parent root;
		try {
			// load the fxml object for the main screen
			root = FXMLLoader.load(getClass().getClassLoader().getResource("SearchPane.fxml"));
	        Scene scene = new Scene(root, 600, 400);
		    
	        stage.setTitle("State Set Selection");
	        stage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	       
	            
        stage.show();        
    }

    public static void main(String[] args) {
        launch();
    }
}
	