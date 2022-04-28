package org.picmg.test.integrationTest.TestMaker;

import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonResultFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

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
            JsonArray testObj = (JsonArray) json.get("Tests");
            // read test file body
            if (testObj == null) {
                System.out.println("Could not find any tests");
                return null;
            }
            for (JsonAbstractValue t : testObj) {
                container.addTest(readTest((JsonObject) t));
            }
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
                case "Test":
                    newTest.addStep(eventType, stepObj.getValue("Location"), stepObj.getValue("Data"));
                    break;
                default:
                    System.out.println("Invalid step event type; aborting.");
                    return null;
            }
        }
        return newTest;
    }

    public static void main(String[] args) {
        try {
            for (String arg : args) {
                Path path = Paths.get(arg);
                if (!path.toFile().exists() || !path.toFile().canRead()) {
                    System.out.println("Cannot find or open " + arg + "; skipping.");
                    continue;
                }
                JsonResultFactory jsonResultFactory = new JsonResultFactory();
                JsonObject json = (JsonObject)jsonResultFactory.buildFromFile(Paths.get(arg));
                if (json == null) {
                    System.out.println("Failed to load json");
                    return;
                }
                TestContainer container = getInstance().read(json);
                if (container == null) {
                    System.out.println("Failed to read tests");
                    return;
                }
                TestWriter.getInstance().createTest(container);
                System.out.println("Successfully generated the following tests:"); container.print();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
