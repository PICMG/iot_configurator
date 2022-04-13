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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.picmg.configurator.MainScreenController;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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


    // Components menus
    @FXML private MenuItem newOption;
    @FXML private MenuItem openOption;
    @FXML private MenuItem saveOption;
    @FXML private MenuItem quitOption;



    // Middile components
    @FXML private RadioButton typeRadio;
    @FXML private RadioButton clickRadio;
    @FXML private RadioButton testRadio;
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
    @FXML private  Button recallB;

    // Right components
    @FXML private TextField nameField;
    @FXML private TextField nameField1;

    // List views
    @FXML private ListView<Test.Step> stepView;
    @FXML private ListView testView;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage temp = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/testMakerGUI.fxml"));
            loader.setController(this); // You need to set this instance as the controller.
            root = loader.load();
            //root = FXMLLoader.load(getClass().getResource("/testMakerGUI.fxml").toURI().toURL());
            Scene scene = new Scene(root, 1024, 570);
            primaryStage.setScene(scene);
            primaryStage.show();

            actionListeners();

            saveOption.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FileChooser fc = new FileChooser();
                    File file = fc.showSaveDialog(primaryStage);
                    if(file != null)
                    {
                        try {
                            saveTest(file.getAbsolutePath());
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Test File Saved");
                            alert.setContentText(file.getName() + " was saved");
                            alert.showAndWait();                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

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
                    System.out.println("DATA isn't a number");
                    delayField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        recallB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reCallTest();
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


        addB2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addTest();
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



    private void reset()
    {
        idField.setText("");
        stringInputField.setText("");
        typeRadio.setSelected(true);
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

    private void loadTest() throws IOException
    {
        
    }
    private void saveTest(String name) throws IOException {
        JsonObject jsonObject = new JsonObject();
        JsonArray testJson = new JsonArray();
        for(int i = 0; i < testView.getItems().size(); i++)
        {
            testJson.add(((Test)testView.getItems().get(i)).toJson());
        }
        jsonObject.put("Tests", testJson);
        System.out.println(name);

        BufferedWriter bw = new BufferedWriter(new FileWriter(name));
        boolean good = jsonObject.writeToFile(bw);
        bw.close();
        System.out.println(good);
    }

    private void addStep()
    {
        String output ="";
        Test.Step step = null;
        if(typeRadio.isSelected())
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
        else if(clickRadio.isSelected())
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
        else if(testRadio.isSelected())
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
    private void addTest()
    {
        String output ="";
        Test test = new Test();

        if(stepView.getItems().size() < 1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing steps");
            alert.setContentText("Steps are required to make the test");
            alert.showAndWait();
            return;
        }

        if(nameField1.getText().equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing steps");
            alert.setContentText("Steps are required to make the test");
            alert.showAndWait();
            return;
        }

        for(Test.Step s : stepView.getItems())
        {
            test.addStep(s);
        }
        test.setName(nameField1.getText());
        int index = -1;
        for(int i = 0; i < testView.getItems().size(); i++)
        {
            if(((Test)testView.getItems().get(i)).getName().equals(test.getName()))
            {
                index = i;
            }
        }
        if(index != -1)
            testView.getItems().remove(index);
        testView.getItems().add(test);
        testView.setItems(testView.getItems());
        System.out.println(test.toJson());
        clearSteps();
        nameField1.setText("");
    }

    private void reCallTest()
    {
        if(testView.getSelectionModel().getSelectedIndex() != -1)
        {
            stepView.getItems().clear();
            Test t = (Test) testView.getSelectionModel().getSelectedItem();
            for(Test.Step s : t.getSteps())
            {
                stepView.getItems().add(s);
            }
            stepView.setItems(stepView.getItems());
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty test");
            alert.setContentText("Please select a test");
            alert.showAndWait();
            return;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
