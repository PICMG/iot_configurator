package org.picmg.test.TestMaker;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestWriter {


    private static TestWriter writer;
    private BufferedWriter outputWriter;
    private TestContainer currentTestContainer;
    private TestWriter()
    {

    }

    /**
     * @param test
     */

    public void createTest(TestContainer test) throws IOException {
        currentTestContainer = test;
        String directory = System.getProperty("user.dir")+"/src/test/org/picmg/test/integrationTest/" + currentTestContainer.getTestContainerName() + ".java";
        createWriter(directory);
        writeHeader();
        writeImports();
        writeClass();
        currentTestContainer =  null;
    }

    private void createWriter(String path) throws IOException {
        outputWriter = new BufferedWriter(new FileWriter(path));
    }
    private void writeHeader() throws IOException {
        outputWriter.write("package org.picmg.test.integrationTest;\n/**\n\tThis is a generated integration test using the testMaker application\n*/\n");
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
        for(Test t : currentTestContainer.getTest())
        {
            writeTest(t);
        }
    }

     private void writeStart() throws IOException {
         outputWriter.write("\t@Override\n\tpublic void start(Stage stage)\n\t{\n\t\tParent root;\n\t\ttry \n\t\t{\n\t\t\troot =FXMLLoader.load(getClass().getClassLoader().getResource(\"" +
                  currentTestContainer.getFileToLoad() +"\"));\n");

         outputWriter.write("\t\t\tScene scene = new Scene(root, 1024, 768);\n\t\t\tstage.setTitle(\"PICMG Configurator\");\n\t\t\tstage.setScene(scene);\n\t\t\tstage.show();\n\t\t\trobotCalls();\n");
         outputWriter.write("\t\t\tRobotUtils.setStage(stage);\n");
         outputWriter.write("\t\t\n\t\t}\n\t\tcatch (IOException e) {\n\t\t\tSystem.out.println(e);\n\t\t}\n\t}\n");

     }
     private void writeRobotMethods() throws IOException {
         outputWriter.write("\tpublic void robotCalls()\n\t{\n");
        for(Test t : currentTestContainer.getTest())
        {
            outputWriter.write("\t\t" + t.getName().replaceAll(" ", "")+"();" + "\n");
        }
         outputWriter.write("\t}\n");
     }
    private void writeImports() throws IOException {
        String imports = "javafx.fxml.FXMLLoader," +
                "javafx.scene.Parent," +
                "javafx.scene.Scene," +
                "javafx.stage.Stage," +
                "javafx.application.Application," +
                "org.junit.Test," +
                "static org.junit.Assert.*," +
                "org.picmg.test.integrationTest.RobotUtils," +
                "java.io.IOException";

        String[] importsToWrite = imports.split(",");
        for(String s : importsToWrite)
        {
            outputWriter.write("import " + s + ";\n");
        }
    }

    private void writeTest(Test t) throws IOException {
        outputWriter.write("\tpublic void " + t.getName().replaceAll(" ", "") +"()\n\t{\n");

        for(Test.Step s : t.getSteps())
        {
            switch(s.type)
            {
                case "Click":
                    outputWriter.write("\t\tRobotUtils.dummyClick(" + "\"" + s.id + "\"" + ");\n");
                    break;
                case "Type":
                    outputWriter.write("\t\tRobotUtils.type(" + "\"" +s.data + "\"" +");\n");
                    break;
                case "Check":
                    outputWriter.write("\t\tRobotUtils.dummyCheck(" + "\"" +s.id + "\"" + "," + "\"" +s.data + "\"" + ");\n");
                    break;
            }
        }
        outputWriter.write("\t}\n");
    }

    /**
     * This method returns an instance of the testWriter
     * @return
     */
    public static TestWriter getInstance()
    {
        if(writer == null)
        {
            writer = new TestWriter();
        }
        return writer;
    }
}
