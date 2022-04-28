package org.picmg.test.integrationTest.TestMaker;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.picmg.configurator.MainScreenController;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;
import org.picmg.jsonreader.JsonValue;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TestMakerGUi extends Application {


    String[] files = {"configuratorScreen.fxml", "effectersTabView.fxml", "FruPane.fxml",
            "MainPanel.fxml", "numericEffecterPane.fxml", "numericSensorPane.fxml", "ParameterPane.fxml",
            "responseCurveDialog.fxml", "SearchPane.fxml", "sensorsTabView.fxml", "stateEffecterPane.fxml",
            "stateSensorPane.fxml", "stateSetsTabView.fxml", "topTabScene.fxml"
    };
    //
    @FXML
    private AnchorPane idPane;
    @FXML
    private AnchorPane actionPane;
    @FXML
    private AnchorPane outputPane;

    // Components left side
    @FXML
    private TitledPane idTab;
    @FXML
    private ListView<String> idList;
    @FXML
    private Button selectIdButton;

    @FXML
    private TextField searchField;

    @FXML
    MenuBar mainMenubar;


    // Components menus
    @FXML
    private MenuItem newOption;
    @FXML
    private MenuItem openOption;
    @FXML
    private MenuItem saveOption;
    @FXML
    private MenuItem exportOption;


    // Middle components
    @FXML
    private RadioButton typeRadio;
    @FXML
    private RadioButton clickRadio;
    @FXML
    private RadioButton testRadio;
    @FXML
    private TextField idField;
    @FXML
    private TextField stringInputField;
    @FXML
    private TextField delayField;

    @FXML
    private Button addB;
    @FXML
    private Button removeB;
    @FXML
    private Button clearB;
    @FXML
    private Button addB2;
    @FXML
    private Button removeB2;
    @FXML
    private Button clearB2;
    @FXML
    private Button resetB;
    @FXML
    private Button recallB;

    // Right components
    @FXML
    private TextField nameField;
    @FXML
    private TextField nameField1;

    // List views
    @FXML
    private ListView<Test.Step> stepView;
    @FXML
    private ListView testView;
    @FXML
    private Button stepUp;
    @FXML
    private Button stepDown;
    @FXML
    private Button testUp;
    @FXML
    private Button testDown;


    ListView<String> tempList = new ListView<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage temp = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/testMakerGUI.fxml"));
            loader.setController(this); // You need to set this instance as the controller.
            root = loader.load();

            loadIds();

            Scene scene = new Scene(root, 1024, 570);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Test Maker");
            primaryStage.show();

            actionListeners(primaryStage);
            clearParameters();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * This method adds all the action listeners
     */
    private void actionListeners(Stage primaryStage) {
        delayField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
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

        saveOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                File file = fc.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        saveTest(file.getAbsolutePath(), "GeneratedTest");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Test File Saved");
                        alert.setContentText(file.getName() + " was saved");
                        alert.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        openOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                File file = fc.showOpenDialog(primaryStage);
                if (file != null) {
                    try {
                        loadTest(file.getAbsolutePath());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Test File was loaded");
                        alert.setContentText(file.getName() + " was loaded");
                        alert.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        exportOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = new File(System.getProperty("user.dir") + "/temp.json");
                try {
                    export(file);
                    TestReader reader = TestReader.getInstance();
                    reader.readFromFile(file);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export completed");
                    alert.setContentText(file.getName() + " was exported as GeneratedTest");
                    alert.showAndWait();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export erroor");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                }
            }
        });

        selectIdButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String id = (String) idList.getSelectionModel().getSelectedItem();
                String[] values = id.split(" from");
                idField.setText("#" + values[0]);
            }
        });


        typeRadio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearParameters();
                stringInputField.setDisable(false);
            }
        });
        clickRadio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearParameters();
                stringInputField.setDisable(true);
            }
        });
        testRadio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearParameters();
                stringInputField.setDisable(false);
            }
        });

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("")) {
                    idList.getItems().clear();
                    for (String s : tempList.getItems()) {
                        idList.getItems().add(s);
                    }
                    idList.setItems(idList.getItems());
                } else {
                    idList.getItems().clear();
                    for (String s : tempList.getItems()) {
                        if (s.toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))) {
                            idList.getItems().add(s);
                        }
                    }
                    idList.setItems(idList.getItems());
                }
            }
        });

        stepUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = stepView.getSelectionModel().getSelectedIndex();
                Test.Step temp;
                if (index > 0) {
                    ObservableList values = stepView.getItems();
                    temp = (Test.Step) values.get(index - 1);
                    values.set(index - 1, values.get(index));
                    values.set(index, temp);
                    stepView.setItems(values);
                    stepView.getSelectionModel().select(index - 1);
                }
            }
        });

        stepDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = stepView.getSelectionModel().getSelectedIndex();
                Test.Step temp;
                if (index + 1 < stepView.getItems().size()) {
                    ObservableList values = stepView.getItems();
                    temp = (Test.Step) values.get(index + 1);
                    values.set(index + 1, values.get(index));
                    values.set(index, temp);
                    stepView.setItems(values);
                    stepView.getSelectionModel().select(index + 1);

                }
            }
        });

        testUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = testView.getSelectionModel().getSelectedIndex();
                Test temp;
                if (index > 0) {
                    ObservableList values = testView.getItems();
                    temp = (Test) values.get(index - 1);
                    values.set(index - 1, values.get(index));
                    values.set(index, temp);
                    testView.setItems(values);
                    testView.getSelectionModel().select(index - 1);

                }
            }
        });

        testDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = testView.getSelectionModel().getSelectedIndex();
                Test temp;
                if (index + 1 < testView.getItems().size()) {
                    ObservableList values = testView.getItems();
                    temp = (Test) values.get(index + 1);
                    values.set(index + 1, values.get(index));
                    values.set(index, temp);
                    testView.setItems(values);
                    testView.getSelectionModel().select(index + 1);

                }
            }
        });
    }

    /**
     * This method clears the text fields
     */
    private void clearParameters(){
        if(typeRadio.isSelected()){
            selectIdButton.setDisable(true);
            idField.setText("");
            stringInputField.setText("");
        }else if(clickRadio.isSelected()){
            selectIdButton.setDisable(false);
            stringInputField.setText("");
        }else if(testRadio.isSelected()){
            selectIdButton.setDisable(false);
        }
    }

    /*
     * export to the specified output file.
     *
     * @param outputFile - the name of the file to output to
     */
    public void export(File outputFile) throws IOException {
        JsonObject jsonObject = new JsonObject();
        JsonArray testJson = new JsonArray();
        for (int i = 0; i < testView.getItems().size(); i++) {
            testJson.add(((Test) testView.getItems().get(i)).toJson());
        }
        jsonObject.put("Name", new JsonValue("GeneratedTest"));
        jsonObject.put("Tests", testJson);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile.getName()));
        jsonObject.writeToFile(bw);
        bw.close();
    }

    /**
     * This method loads all the ids from the resource files
     */
    private void loadIds() {
        Parent root;

        for (String file : files) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + file));
            try {
                root = loader.load();
            } catch (Exception e) {}
            Map<String, Object> ids = loader.getNamespace();

            for (String key : ids.keySet()) {
                idList.getItems().add(key + " from " + file);
            }
        }
        FXCollections.sort(idList.getItems());
        idList.setItems(idList.getItems());
        for (String s : idList.getItems()) {
            tempList.getItems().add(s);
        }
        tempList.setItems(tempList.getItems());
    }

    /**
     * This method clears the main screen of the GUI
     */
    private void reset() {
        idField.setText("");
        stringInputField.setText("");
        typeRadio.setSelected(true);
        delayField.setText("");
        clearParameters();
        stringInputField.setDisable(false);
    }

    /**
     * This method clears the steps of the steps list
     */
    private void clearSteps() {
        stepView.getItems().clear();
    }

    /**
     * This method clears the test from the test list
     */
    private void clearTests() {
        testView.getItems().clear();
    }

    /**
     * This method removes a single step
     */
    private void removeStep() {
        if (stepView.getSelectionModel().getSelectedIndex() != -1) {
            stepView.getItems().remove(stepView.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * This method removes a single test
     */
    private void removeTest() {
        if (testView.getSelectionModel().getSelectedIndex() != -1) {
            testView.getItems().remove(testView.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * This method loads a json test file
     *
     * @param name
     * @throws IOException
     */
    private void loadTest(String name) throws IOException {
        JsonResultFactory temp = new JsonResultFactory();
        JsonArray tests = (JsonArray) ((JsonObject) temp.buildFromFile(Path.of(name))).get("Tests");
        for (int i = 0; i < tests.size(); i++) {
            Test test = new Test((JsonObject) tests.get(i));
            testView.getItems().add(test);
        }
        testView.setItems(testView.getItems());

    }

    /**
     * This method saves test in a json format
     *
     * @param name
     * @param fileName
     * @throws IOException
     */
    private void saveTest(String name, String fileName) throws IOException {
        JsonObject jsonObject = new JsonObject();
        JsonArray testJson = new JsonArray();
        for (int i = 0; i < testView.getItems().size(); i++) {
            testJson.add(((Test) testView.getItems().get(i)).toJson());
        }
        jsonObject.put("Name", new JsonValue(fileName));
        jsonObject.put("Tests", testJson);
        BufferedWriter bw = new BufferedWriter(new FileWriter(name));
        boolean good = jsonObject.writeToFile(bw);
        bw.close();
    }

    /**
     * This method adds a step to the step list
     */
    private void addStep() {
        String output = "";
        Test.Step step = null;
        String method = "";
        if (typeRadio.isSelected()) {
            if (stringInputField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Input");
                alert.setContentText("You have entered in an empty string as the input");
                alert.showAndWait();
                return;
            }
            output += "Type ";
            output += stringInputField.getText() + " ";
            method = "Type";
        } else if (clickRadio.isSelected()) {
            if (idField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing ID");
                alert.setContentText("An ID is required");
                alert.showAndWait();
                return;
            }
            method = "Click";

        } else if (testRadio.isSelected()) {
            boolean error = false;
            String content = "";
            if (stringInputField.getText().equals("")) {
                error = true;
                content += "You have entered in an empty string as the input\n";
            }
            if (idField.getText().equals("")) {
                error = true;
                content += "An ID is required";
            }
            if (error) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing input");
                alert.setContentText(content);
                alert.showAndWait();
                return;
            }
            method = "Test";

        }

        if (!delayField.getText().equals("")) {
            int delay = Integer.parseInt(delayField.getText());
            step = new Test.Step(method, idField.getText(), stringInputField.getText(), delay);

        } else {
            step = new Test.Step(method, idField.getText(), stringInputField.getText());
        }

        if (step != null) {
            stepView.getItems().add(step);
            stepView.setItems(stepView.getItems());
        }
    }

    /**
     * This method collects the steps and adds them as a test
     */
    private void addTest() {
        String output = "";
        Test test = new Test();

        if (stepView.getItems().size() < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing steps");
            alert.setContentText("Steps are required to make the test");
            alert.showAndWait();
            return;
        }

        if (nameField1.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Name");
            alert.setContentText("Test Name field is blank");
            alert.showAndWait();
            return;
        }

        for (Test.Step s : stepView.getItems()) {
            test.addStep(s);
        }
        test.setName(nameField1.getText());
        int index = -1;
        for (int i = 0; i < testView.getItems().size(); i++) {
            if (((Test) testView.getItems().get(i)).getName().equals(test.getName())) {
                index = i;
            }
        }
        if (index != -1)
            testView.getItems().remove(index);
        testView.getItems().add(test);
        testView.setItems(testView.getItems());
        clearSteps();
        nameField1.setText("");
    }

    /**
     * This method recalls a test's steps
     */
    private void reCallTest() {
        if (testView.getSelectionModel().getSelectedIndex() != -1) {
            stepView.getItems().clear();
            Test t = (Test) testView.getSelectionModel().getSelectedItem();
            for (Test.Step s : t.getSteps()) {
                stepView.getItems().add(s);
            }
            stepView.setItems(stepView.getItems());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty test");
            alert.setContentText("Please select a test");
            alert.showAndWait();
            return;
        }
    }

    /**
     * This method will launch the program
     *
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}
