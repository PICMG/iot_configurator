package configurator;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class App extends Application {	
	
	private static Scene scene;
    @FXML AnchorPane bindingPane;
    public static AnchorPane stateSensorContent;
    public static AnchorPane stateEffecterContent;
    public static AnchorPane numericSensorContent;
    public static AnchorPane numericEffecterContent;
  
	@Override
    public void start(Stage stage) { 
	    Parent root;
	    try {
			// load the fxml object for the main screen
			root = FXMLLoader.load(getClass().getClassLoader().getResource("MainPanel.fxml"));
	        Scene scene = new Scene(root, 1024, 768);
		  
	        stage.setTitle("PICMG Configurator");
	        stage.setScene(scene);
	        stage.show();
	        
	        bindingPane = (AnchorPane) scene.lookup("#bindingPane");
	        
	        // setup binding panes
	        stateSensorContent = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("stateSensorPane.fxml"));
	        bindingPane.getChildren().add(stateSensorContent);
	        stateSensorContent.setVisible(false);
	        
	        stateEffecterContent = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("stateEffecterPane.fxml"));
	        bindingPane.getChildren().add(stateEffecterContent);
	        stateEffecterContent.setVisible(false);
	        
	        numericSensorContent = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("numericSensorPane.fxml"));
	        bindingPane.getChildren().add(numericSensorContent);
	        numericSensorContent.setVisible(false);
	        
	        numericEffecterContent = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("numericEffecterPane.fxml"));
	        bindingPane.getChildren().add(numericEffecterContent);
	        numericEffecterContent.setVisible(false);
	        
	        System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	       
    }

    public static void main(String[] args) {
        launch();
    }
}