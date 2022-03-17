package org.picmg.test.TestMaker;

import java.util.ArrayList;

public class Test {

    private String name;
    private ArrayList<Step> steps;

    private class Step
    {
        public String id;
        public String data;
        public String type;
        public String name;

        public Step(String type, String id, String data)
        {
            this.id = id;
            this.data = data;
            this.type = type;
        }

        public void print()
        {
            System.out.println("Method = " + type + " Id = " + id + " Data =" + data);
        }
    }

    public Test()
    {
        steps = new ArrayList<Step>();
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }
    public void addStep(String type, String id, String data)
    {
        Step newStep = new Step(type,id,data);
        steps.add(newStep);
    }

    public void print()
    {
        System.out.println("Test Name is " + name);
        System.out.println("Test Steps \n");
        for(Step s : steps)
        {
            s.print();
        }
    }

}
