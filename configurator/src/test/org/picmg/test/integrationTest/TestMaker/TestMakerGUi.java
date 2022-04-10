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
    @FXML private Button clearB;
    @FXML private Button addB2;
    @FXML private Button removeB2;
    @FXML private Button clearB2;
    @FXML private Button resetB;

    // Right components
    @FXML private TextField nameField;

    // List views
    @FXML private ListView stepView;
    @FXML private ListView testView;


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
            actionListeners();


        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }


    private void actionListeners()
    {
        // Action listeners
        resetB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reset();
            }
        });
        addB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addStep();
            }
        });

        removeB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeItem();
            }
        });
    }
    public void loadData(Scene scene) throws IOException {


        //TextFields
        stringInputField =(TextField) scene.lookup("#stringInputField");
        idField =(TextField) scene.lookup("#idField");

        // Assign the buttons
        addB = (Button) scene.lookup("#addB");
        removeB = (Button) scene.lookup("#removeB");
        clearB = (Button) scene.lookup("clearB");

        resetB = (Button) scene.lookup("#resetB");

        // Assign radio buttons
        typeB = (RadioButton) scene.lookup("#typeRadio");
        clickB = (RadioButton) scene.lookup("#clickRadio");
        testB = (RadioButton) scene.lookup("#testRadio");

        //
        stepView = (ListView)  scene.lookup("#stepView");
        testView = (ListView)  scene.lookup("#testView");




    }


    private void reset()
    {
        idField.setText("");
        stringInputField.setText("");
        typeB.setSelected(true);
    }

    private void removeItem()
    {
        if(stepView.getSelectionModel().getSelectedIndex() != -1)
        {
            System.out.println("I have something");
        }
        else
        {
            System.out.println("I Nothing");
        }
    }

    private void addStep()
    {
        String output ="";
        if(typeB.isSelected())
        {
            if(stringInputField.getText().equals(""))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Input");
                alert.setContentText("You have entered in an empty string as the input");
                alert.showAndWait();
                return;
            }
            output += "Type ";
            output += stringInputField.getText() + " ";
        }
        else if(clickB.isSelected())
        {
            if(idField.getText().equals(""))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing ID");
                alert.setContentText("An ID is required");
                alert.showAndWait();
                return;
            }
            output += "Click ";
            output += idField.getText() + " ";
        }
        else if(testB.isSelected())
        {
            boolean error = false;
            String content = "";
            if(stringInputField.getText().equals(""))
            {
                System.out.println("GER");

                error = true;
                content += "You have entered in an empty string as the input\n";
            }
            if(idField.getText().equals(""))
            {
                System.out.println("GER");

                error = true;
                content +="An ID is required";
            }
            if(error)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing input");
                alert.setContentText(content);
                alert.showAndWait();
                return;

            }
            output += "Test ";
            output += idField.getText() + " ";
            output += stringInputField.getText() + " ";

        }
        stepView.getItems().add(output);
        stepView.setItems(stepView.getItems());

        testView.getItems().add(output);
        testView.setItems(testView.getItems());

    }
    public static void main(String[] args) {
        launch();
    }
}
