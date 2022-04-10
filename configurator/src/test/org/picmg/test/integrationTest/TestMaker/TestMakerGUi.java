package org.picmg.test.integrationTest.TestMaker;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.w3c.dom.Text;

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
    @FXML private TextField delayField;

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
    @FXML private ListView<Test.Step> stepView;
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


    /**
     * This method adds all the action listeners
     */
    private void actionListeners()
    {
        delayField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*"))
                {
                    System.out.println("DATA isn't a numbe");
                    delayField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        clearB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearSteps();
            }
        });

        clearB2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearTests();
            }
        });

        removeB2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeTest();
            }
        });

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
                removeStep();
            }
        });
    }
    public void loadData(Scene scene) throws IOException {


        //TextFields
        stringInputField =(TextField) scene.lookup("#stringInputField");
        delayField = (TextField) scene.lookup("#delayField");
        idField =(TextField) scene.lookup("#idField");

        // Assign the buttons
        addB = (Button) scene.lookup("#addB");
        removeB = (Button) scene.lookup("#removeB");
        clearB = (Button) scene.lookup("#clearB");

        addB2 = (Button) scene.lookup("#addB2");
        removeB2 = (Button) scene.lookup("#removeB2");
        clearB2 = (Button) scene.lookup("#clearB2");

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
        delayField.setText("");
    }

    private void clearSteps()
    {
        stepView.getItems().clear();
    }

    private void clearTests()
    {
        testView.getItems().clear();
    }

    private void removeStep()
    {
        if(stepView.getSelectionModel().getSelectedIndex() != -1)
        {
            stepView.getItems().remove(stepView.getSelectionModel().getSelectedIndex());
        }
    }

    private void removeTest()
    {
        if(testView.getSelectionModel().getSelectedIndex() != -1)
        {
            testView.getItems().remove(testView.getSelectionModel().getSelectedIndex());
        }
    }

    private void addStep()
    {
        String output ="";
        Test.Step step = null;
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
            step = new Test.Step("Type","",stringInputField.getText());
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
            step = new Test.Step("Click",idField.getText(),"");
        }
        else if(testB.isSelected())
        {
            boolean error = false;
            String content = "";
            if(stringInputField.getText().equals(""))
            {
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
            step = new Test.Step("Test",idField.getText(),stringInputField.getText());

        }

        if(delayField.getText() != "")
        {
            output += "D="+ delayField.getText();
        }
        if(step != null)
        {
            stepView.getItems().add(step);
            stepView.setItems(stepView.getItems());
        }

    }
    public static void main(String[] args) {
        launch();
    }
}
