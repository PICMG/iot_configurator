package org.picmg.test.TestMaker;

public class TestWriter {


    private static TestWriter writer;
    private Test currentTest;
    private TestWriter()
    {

    }

    public void createTest(Test test)
    {
        currentTest = test;
        writeHeader();
        writeImports();
        writeClass();
        currentTest =  null;
    }
    private void writeHeader()
    {
        System.out.print("/**\n\tThis is a generated integration test using the testMaker application\n*/\n");
    }

    private void writeClass()
    {
        System.out.println("public class " + currentTest.getName() + "\n{\n");
        // Write test calls
        wrtieTest();
        System.out.println("}");
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

    public void wrtieTest()
    {
        System.out.println("\t@Test\n\tpublic void test\n\t{\n\n\t}\n");
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
