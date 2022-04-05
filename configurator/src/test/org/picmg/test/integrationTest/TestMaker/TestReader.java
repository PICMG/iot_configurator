package org.picmg.test.integrationTest.TestMaker;

import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestReader {

    static TestReader reader;

    private TestReader() {

    }


    public static TestReader getInstance() {
        if (reader == null)
            reader = new TestReader();
        return reader;
    }

    public TestContainer read(JsonObject json) {
        TestContainer container = new TestContainer();
        try {
            // read test file header
            container.setTestContainerName(json.getValue("Name").replaceAll(" ", ""));
            container.setFileToLoad("topTabScene.fxml"); // TODO replace with findFilesForFXIDS?
            JsonObject testObj = (JsonObject) json.get("Test");
            // read test file body
            Test test = readTest(testObj);
            if (test == null) return null;
            container.addTest(readTest(testObj));
        } catch (Exception e) {
            System.out.println("Failed to read test json.");
            e.printStackTrace();
            return null;
        }
        return container;
    }

    private Test readTest(JsonObject testObj) {
        JsonArray stepsArr = (JsonArray) testObj.get("Steps");
        Test newTest = new Test();
        newTest.setName(testObj.getValue("name").replaceAll(" ", ""));
        for (JsonAbstractValue stepObj : stepsArr) {
            String eventType = stepObj.getValue("Event");
            switch (eventType) {
                case "Click":
                    newTest.addStep(eventType, stepObj.getValue("Location"), "");
                    break;
                case "Type":
                    newTest.addStep(eventType, "", stepObj.getValue("Data"));
                    break;
                case "Check":
                    newTest.addStep(eventType, stepObj.getValue("Location"), stepObj.getValue("Data"));
                    break;
                default:
                    System.out.println("Invalid step event type; aborting.");
                    return null;
            }
        }
        return newTest;
    }

    public static List<Path> findFilesForFXIDS(List<String> fxids) {
        // todo: reflection for each fxid to get paths?
        return List.of();
    }

    public static void main(String[] args) {
        try {
            for (String arg : args) {
                JsonResultFactory jsonResultFactory = new JsonResultFactory();
                JsonObject json = (JsonObject)jsonResultFactory.buildFromFile(Paths.get(arg));
                TestContainer container = getInstance().read(json);
                if (container == null) {
                    System.out.println("Failed to read tests");
                    return;
                }
                TestWriter.getInstance().createTest(container);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
