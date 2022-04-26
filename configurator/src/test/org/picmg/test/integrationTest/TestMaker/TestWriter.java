package org.picmg.test.integrationTest.TestMaker;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestWriter {


    private static TestWriter writer;
    private BufferedWriter outputWriter;
    private TestContainer currentTestContainer;
    private static Path BASE_PATH = getBasePath();
    private static int TEST_DELAY = 8000;

    private static Path getBasePath() {
        Path base = Paths.get(System.getProperty("user.dir"));
        if (base.endsWith("iot_configurator")) {
            base = base.resolve("configurator");
        }
        if (!base.endsWith("configurator")) {
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
        outputWriter.write("package org.picmg.test.integrationTest.generated;\n/**\n\tThis is a generated integration test using the testMaker application\n*/\n");
    }

    private void writeClass() throws IOException {
        outputWriter.write("public class " + currentTestContainer.getTestContainerName() + " extends Application\n{\n");
        writeLine(1, "volatile boolean hasRun = false;");
        writeExecutor();
        writeLaunch();
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

    private void writeLaunch() throws IOException {
        writeLine(1, "@BeforeClass");
        writeLine(1, "public static void setup() {");
        writeLine(2, "launch();");
        writeLine(1, "}\n");
    }

    private void writeStart() throws IOException {
        outputWriter.write("\t@Override\n\tpublic void start(Stage stage) {\n\t\tParent root;\n\t\ttry \n\t\t{\n\t\t\troot = FXMLLoader.load(getClass().getClassLoader().getResource(\"" +
                currentTestContainer.getFileToLoad() + "\"));\n");
        outputWriter.write("\t\t\tScene scene = new Scene(root, 1024, 870);\n\t\t\tstage.setTitle(\"PICMG Configurator\");\n\t\t\tstage.setScene(scene);\n\t\t\tstage.show();\n\t\t\t\n");
        outputWriter.write("\t\t\n\t\t}\n\t\tcatch (IOException e) {\n\t\t\tSystem.out.println(e);\n\t\t}\n\t}\n");

    }

    private void writeExecutor() throws IOException {
        writeLine(1, "@Test");
        writeLine(1, "public void robotCalls() {");
        writeLine(2, "if (hasRun) return;");
        writeRobotMethods();
        writeLine(2, "hasRun = true;");
        writeLine(1, "}");
    }

    private void writeRobotMethods() throws IOException {
        ArrayList<Test> tests = currentTestContainer.getTests();
        outputWriter.write("\t\t");
        for (Test t : tests) {
            int index = tests.indexOf(t);
            outputWriter.write(t.getName().replaceAll(" ", "") + "()");
            if (index < tests.size() - 1)
                outputWriter.write(".then(()->");
        }
        if (tests.size() != 0) {
            for (int i = 0; i < tests.size() - 1; i++)
                outputWriter.write(")");
            outputWriter.write(".run();\n");
        }
    }

    private void writeLine(int indentNum, String ... messages) throws IOException {
        String out = "";
        for (int i = 0; i < indentNum; i++) out += "\t";
        System.out.println(Arrays.toString(messages));
        for (String message : messages) out += message;
        outputWriter.write(out + "\n");
    }

    private void writeImports() throws IOException {
        String[] imports = new String[] {"javafx.fxml.FXMLLoader",
                "javafx.scene.Parent",
                "javafx.scene.Scene",
                "javafx.stage.Stage",
                "javafx.application.Application",
                "org.junit.BeforeClass",
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
        outputWriter.write("\tpublic RobotThread " + t.getName().replaceAll(" ", "") + "()\n\t{\n");
        outputWriter.write("\t\tSystem.out.println(\"Executing integration test " + t.getName() + "\");\n");
        outputWriter.write("\t\treturn new RobotThread()\n");
        for (Test.Step s : t.getSteps()) {
            switch (s.type) {
                case "Click":
                    outputWriter.write("\t\t\t\t.then(" + s.getDelay() + ", ()->RobotUtils.click(" + "\"" + s.id + "\"" + "))\n");
                    break;
                case "Type":
                    outputWriter.write("\t\t\t\t.then(" + s.getDelay() +  ", ()->RobotUtils.type(\"" + s.data + "\"" + "))\n");
                    break;
                case "Test":
                    outputWriter.write("\t\t\t\t.then(" + s.getDelay() + ", ()->RobotUtils.check(" + "\"" + s.id + "\"" + "," + "\"" + s.data + "\"" + "))\n");
                    break;
            }
        }
        outputWriter.write("\t\t\t\t.wait(" + TEST_DELAY + ");\n");
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
