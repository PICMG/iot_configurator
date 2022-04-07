package org.picmg.test.TestMaker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.picmg.configurator.MainScreenController;

import java.io.IOException;

public class TestMakerGUi extends Application {


    //
    @FXML private AnchorPane idPane;
    @FXML private AnchorPane actionPane;
    @FXML private  AnchorPane outputPane;

    // Components left side
    @FXML private TitledPane deviceTab;
    @FXML private TitledPane sensorsTab;
    @FXML private TitledPane effectersTab;
    @FXML private Button selectIdButton;


    // Middile components
    @FXML private RadioButton typeB;
    @FXML private RadioButton clickB;
    @FXML private RadioButton testB;
    @FXML private TextField idField;
    @FXML private TextField stringInputField;
    @FXML private Button addB;
    @FXML private Button removeB;
    @FXML private Button resetB;

    // Right components
    @FXML private TextField nameField;
    @FXML private ListView outputView;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage temp = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/testMakerGUI.fxml").toURI().toURL());
            Scene scene = new Scene(root, 1024, 570);
            primaryStage.setScene(scene);
            primaryStage.show();

            loadData(scene);




            // Action listeners
            resetB.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    idField.setText("");
                    stringInputField.setText("");
                }
            });
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }


    public void loadData(Scene scene) throws IOException {


        //TextFields
        stringInputField =(TextField) scene.lookup("#stringInputField");
        idField =(TextField) scene.lookup("#idField");
        // Assign the buttons
        addB = (Button) scene.lookup("#addB");
        removeB = (Button) scene.lookup("#removeB");
        resetB = (Button) scene.lookup("#resetB");


    }
    public static void main(String[] args) {
        launch();
    }
}
