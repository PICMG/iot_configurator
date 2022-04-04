package org.picmg.test.TestMaker;


import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.io.File;

public class TestReader {

    int index;
    TestContainer container;
    static TestReader reader;
    private TestReader()
    {
        reset();
    }

    private void reset()
    {
        container = new TestContainer();
        index = 0;
    }
    public static TestReader getInstance()
    {
        if(reader == null)
            reader = new TestReader();
        return reader;
    }

    public boolean read(String Test) throws Exception {
        String[] testValues = Test.split(":");
        readFileName(testValues);
        readStage(testValues);
        for(int i = index; i < testValues.length; i++) {
            if(testValues[i].contains("Steps,Start"))
                readSteps(testValues);
        }
        container.print();
        TestWriter.getInstance().createTest(container);
        reset();
        return true;
    }


    private void readStage(String[] testValues)
    {
        String[] nameValues = testValues[index].split(",");
        container.setFileToLoad(nameValues[1]);
        index++;
    }
    private void readFileName(String[] testValues)
    {
        String[] nameValues = testValues[index].split(",");
        container.setTestContainerName(nameValues[1]);
        index++;
    }

    private void readSteps(String[] testValues) throws Exception {

            index++;
            Test test = new Test();
            System.out.println("HERE in steps");
            while(!testValues[index].contains("Steps,End"))
            {
                if(testValues[index].contains("Name"))
                {
                    String[] nameValues = testValues[index].split(",");
                    test.setName(nameValues[1]);
                }
                else if(testValues[index].contains("Click"))
                {
                    String[] clickValues = testValues[index].split(",");
                    test.addStep(clickValues[0],clickValues[1], "");

                }
                else if(testValues[index].contains("Type"))
                {
                    String[] typeValues = testValues[index].split(",");
                    test.addStep(typeValues[0],"", typeValues[1]);
                }
                else if(testValues[index].contains("Check"))
                {
                    String[] checkValues = testValues[index].split(",");
                    test.addStep(checkValues[0],checkValues[1], checkValues[2]);
                }
                else
                {
                    System.out.println(testValues[index]);
                    throw new Exception("Invalid option");
                }
                index++;
            }
            index++;
            container.addTest(test);
        }
}
