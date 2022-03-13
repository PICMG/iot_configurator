package org.picmg.test.TestMaker;


import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.io.File;

public class TestReader {

    int index;
    Test test;

    public TestReader()
    {
        test = new Test();
        index = 0;
    }

    public boolean read(String Test)
    {
        String[] testValues = Test.split(":");
        readFileName(testValues);
        readSteps(testValues);
        test.print();
        return true;
    }

    private void readFileName(String[] testValues)
    {
        String[] nameValues = testValues[index].split(",");
        test.setName(nameValues[1]);
        index++;
    }

    private void readSteps(String[] testValues)
    {
        if(testValues[index].contains("Steps,Start"))
        {
            index++;
            while(!testValues[index].contains("Steps,End"))
            {
                if(testValues[index].contains("Click"))
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
                    System.out.println("NOPE");
                }
                index++;
            }
        }
    }

}
