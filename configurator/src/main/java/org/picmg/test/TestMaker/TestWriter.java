package org.picmg.test.TestMaker;

public class TestWriter {


    private static TestWriter writer;
    private TestContainer currentTestContainer;
    private TestWriter()
    {

    }

    /**
     * @param test
     */

    public void createTest(TestContainer test)
    {
        currentTestContainer = test;
        writeHeader();
        writeImports();
        writeClass();
        currentTestContainer =  null;
    }
    private void writeHeader()
    {
        System.out.print("/**\n\tThis is a generated integration test using the testMaker application\n*/\n");
    }

    private void writeClass()
    {
        System.out.println("public class " + currentTestContainer.getTestContainerName() + " extends Application\n{\n");
        // Write test calls
        writeTests();
        System.out.println("}");
    }

    private void writeTests()
    {
        writeStart();
        for(Test t : currentTestContainer.getTest())
        {
            writeTest(t);
        }
    }

     private void writeStart()
     {
         System.out.print("\t@Override\n\tpublic void start(Stage stage)\n\t{\n\t\tParent root\n\t\ttry \n\t\t{\n\t\t\troot =FXMLLoader.load(getClass().getClassLoader().getResource(\"" +
                  currentTestContainer.getFileToLoad() +"\"));\n");

         System.out.print("\t\t\tScene scene = new Scene(root, 1024, 768);\n\t\t\tstage.setTitle(\"PICMG Configurator\");\n\t\t\tstage.setScene(scene);\n\t\t\tstage.show();\n\t\t\trobotCalls(stage);");
         System.out.println("\t\t\n\t\t}\n\t\tcatch (IOException e) {\n\t\t\tSystem.out.println(e);\n\t\t}\n\t}");

         /*          public void start(Stage stage) {
              Parent root;
              try {
                  root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
                  Scene scene = new Scene(root, 1024, 768);
                  stage.setTitle("PICMG Configurator");
                  stage.setScene(scene);
                 stage.show();
                  clicks(scene);
                  System.out.println("Here is where the test would go");
              } catch (IOException e) {
                  System.out.println(e);
             }
          }*/
     }
    private void writeImports()
    {
        String imports = "javafx.fxml.FXMLLoader," +
                "javafx.scene.Parent," +
                "javafx.scene.Scene," +
                "javafx.stage.Stage," +
                "javafx.application.Application," +
                "org.junit.Test," +
                "static org.junit.Assert.*," +
                "org.picmg.test.unitTest.RobotUtils";

        String[] importsToWrite = imports.split(",");
        for(String s : importsToWrite)
        {
            System.out.print("import " + s + ";\n");
        }
    }

    private void writeTest(Test t)
    {
        System.out.println("\tpublic void " + t.getName().replaceAll(" ", "_") +"\n\t{\n\n\t}\n");

    }
    public static TestWriter getInstance()
    {
        if(writer == null)
        {
            writer = new TestWriter();
        }
        return writer;
    }
}
