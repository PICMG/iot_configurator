package org.picmg.test.integrationTest.TestMaker;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestWriter {


    private static TestWriter writer;
    private BufferedWriter outputWriter;
    private TestContainer currentTestContainer;
    private static Path BASE_PATH = getBasePath();
    private static int STEP_DELAY = 400;

    private static Path getBasePath() {
        Path base = Paths.get(System.getProperty("user.dir"));
        if (base.endsWith("iot_configurator")) {
            base = base.resolve("configurator");
        } else if (!base.endsWith("configurator")) {
            System.out.println("ERROR: Unable to resolve proper directory layout. Test output will resort to run directory until fixe.d");
            return base;
        }
        return Paths.get(base.toString(),"/src/test/org/picmg/test/integrationTest/generated");
    }

    private TestWriter() {
        System.out.println(BASE_PATH);
    }

    /**
     * @param test
     */

    public synchronized void createTest(TestContainer test) throws IOException {
        currentTestContainer = test;
        File file = BASE_PATH.resolve(currentTestContainer.getTestContainerName() + ".java").toFile();
        createWriter(file);
        writeHeader();
        writeImports();
        writeClass();
        currentTestContainer = null;
    }

    private void createWriter(File file) throws IOException {
        outputWriter = new BufferedWriter(new FileWriter(file));
    }

    private void writeHeader() throws IOException {
        outputWriter.write("package org.picmg.test.generated;\n/**\n\tThis is a generated integration test using the testMaker application\n*/\n");
    }

    private void writeClass() throws IOException {
        outputWriter.write("public class " + currentTestContainer.getTestContainerName() + " extends Application\n{\n");
        writeRobotMethods();
        writeTests();
        outputWriter.write("}");
        outputWriter.close();
    }

    private void writeTests() throws IOException {
        writeStart();
        for (Test t : currentTestContainer.getTests()) {
            writeTest(t);
        }
    }

    private void writeStart() throws IOException {
        outputWriter.write("\t@Override\n\tpublic void start(Stage stage)\n\t{\n\t\tParent root;\n\t\ttry \n\t\t{\n\t\t\troot = FXMLLoader.load(getClass().getClassLoader().getResource(\"" +
                currentTestContainer.getFileToLoad() + "\"));\n");

        outputWriter.write("\t\t\tScene scene = new Scene(root, 1024, 768);\n\t\t\tstage.setTitle(\"PICMG Configurator\");\n\t\t\tstage.setScene(scene);\n\t\t\tstage.show();\n\t\t\trobotCalls();\n");
        outputWriter.write("\t\t\n\t\t}\n\t\tcatch (IOException e) {\n\t\t\tSystem.out.println(e);\n\t\t}\n\t}\n");

    }

    private void writeRobotMethods() throws IOException {
        outputWriter.write("\tpublic void robotCalls()\n\t{\n");
        for (Test t : currentTestContainer.getTests()) {
            outputWriter.write("\t\t" + t.getName().replaceAll(" ", "") + "();" + "\n");
        }
        outputWriter.write("\t}\n");
    }

    private void writeImports() throws IOException {
        String[] imports = new String[] {"javafx.fxml.FXMLLoader",
                "javafx.scene.Parent",
                "javafx.scene.Scene",
                "javafx.stage.Stage",
                "javafx.application.Application",
                "org.junit.Test",
                "static org.junit.Assert.*",
                "org.picmg.test.integrationTest.RobotThread",
                "org.picmg.test.integrationTest.RobotUtils",
                "java.io.IOException"};

        for (String im : imports) {
            outputWriter.write("import " + im + ";\n");
        }
    }

    private void writeTest(Test t) throws IOException {
        outputWriter.write("\tpublic void " + t.getName().replaceAll(" ", "") + "()\n\t{\n");
        outputWriter.write("\tSystem.out.println(\"Executing integration test " + t.getName() + "\");\n");
        outputWriter.write("\t\tnew RobotThread()");
        for (Test.Step s : t.getSteps()) {
            switch (s.type) {
                case "Click":
                    outputWriter.write("\t\t\t.then(" + STEP_DELAY + ", ()->RobotUtils.click(" + "\"" + s.id + "\"" + "))\n");
                    break;
                case "Type":
                    outputWriter.write("\t\t\t.then(" + STEP_DELAY +  ", ()->RobotUtils.type(\"" + s.data + "\"" + "))\n");
                    break;
                case "Check":
                    outputWriter.write("\t\t\t.then(" + STEP_DELAY + ", ()->RobotUtils.check(" + "\"" + s.id + "\"" + "," + "\"" + s.data + "\"" + "))\n");
                    break;
            }
        }
        outputWriter.write("\t\t\t.wait(5000)\n");
        outputWriter.write("\t\t\t.run();\n");
        outputWriter.write("\t}\n");
    }

    /**
     * This method returns an instance of the testWriter
     *
     * @return
     */
    public static TestWriter getInstance() {
        if (writer == null) {
            writer = new TestWriter();
        }
        return writer;
    }
}
