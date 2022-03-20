package org.picmg.test.TestMaker;

public class TestWriter {


    private static TestWriter writer;
    private TestContainer currentTestContainer;
    private TestWriter()
    {

    }

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
        System.out.println("public class " + currentTestContainer.getTestContainerName() + "\n{\n");
        // Write test calls
        writeTests();
        System.out.println("}");
    }

    private void writeTests()
    {
        for(Test t : currentTestContainer.getTest())
        {
            writeTest(t);
        }
    }
    private void writeImports()
    {
        String imports = "javafx.fxml.FXMLLoader," +
                "javafx.scene.Parent," +
                "javafx.scene.Scene," +
                "javafx.stage.Stage," +
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
        System.out.println("\t@Test\n\tpublic void " + t.getName() +"\n\t{\n\n\t}\n");

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
