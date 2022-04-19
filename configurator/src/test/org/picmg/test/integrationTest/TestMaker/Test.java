package org.picmg.test.integrationTest.TestMaker;

import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.util.ArrayList;

public class Test {

    private String name;
    private ArrayList<Step> steps;

    public static class Step
    {
        public String id;
        public String data;
        public String type;
        public int delay;
        public String name;
        private int delay = 1000;

        public Step(String type, String id, String data)
        {
            this.id = id;
            this.data = data;
            this.type = type;
            delay = 0;
        }

        public Step()
        {

        }
        public Step(String type, String id, String data, int delay)
        {
            this.id = id;
            this.data = data;
            this.type = type;
            this.delay = delay;
        }

        public Step(String type, String id, String data, int delay) {
            this(type, id, data);
            this.delay = delay;
        }

        public int getDelay() {
            return delay;
        }

        public void print()
        {
            System.out.println(delay);
            if(delay == 0) {
                System.out.println("Method = " + type + " Id = " + id + " Data =" + data);
            }
            else
            {
                System.out.println("Method = " + type + " Id = " + id + " Data =" + data + " Delay = " + delay);
            }


        }

        @Override
        public String toString() {

            if (delay == 0) {
                return "Method = " + type + " Id = " + id + " Data = " + data;
            } else {
                return "Method = " + type + " Id = " + id + " Data = " + data + " Delay = " + delay;
            }
        }

        public JsonObject toJson()
        {
            JsonObject stepObject = new JsonObject();
            stepObject.put("Event", new JsonValue(type));
            stepObject.put("Location", new JsonValue(id));
            if(!data.equals("") && data != null)
                stepObject.put("Data", new JsonValue(data));
            return stepObject;
        }

        public void fromJson(JsonObject jsonObject)
        {
            this.type = jsonObject.getValue("Event");
            this.id = jsonObject.getValue("Location");
            this.data = jsonObject.getValue("Data");

        }
    }

    public Test()
    {
        steps = new ArrayList<Step>();
    }

    /**
     * This sets the test name
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * This method gets the test name
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * This method adds a step to the test
     * @param type
     * @param id
     * @param data
     */
    public void addStep(String type, String id, String data)
    {
        Step newStep = new Step(type,id,data);
        steps.add(newStep);
    }

    public void addStep(Step step)
    {
        steps.add(step);
    }
    /**
     * This method returns the steps for the test
     * @return
     */
    public ArrayList<Step> getSteps()
    {
        return steps;
    }

    /**
     * This method is for debugging
     */
    public void print()
    {
        System.out.println("Test Name is " + name);
        System.out.println("Test Steps \n");
        for(Step s : steps)
        {
            s.print();
        }
    }

    @Override
    public String toString()
    {
        return getName()+ " Steps " + getSteps().size();
    }

    public JsonObject toJson()
    {
        JsonObject testObject = new JsonObject();
        testObject.put("name", new JsonValue(this.getName()));

        JsonArray steps = new JsonArray();
        for(Step s : getSteps())
        {

            steps.add(s.toJson());

        }
        testObject.put("Steps", steps);
        return testObject;
    }

    public void fromJson(JsonObject jsonValue)
    {
        this.name = jsonValue.getValue("name");
        steps.clear();
        JsonArray jsonSteps = (JsonArray) jsonValue.get("Steps");
        for(int i = 0; i < jsonSteps.size(); i++)
        {
            Step step = new Step();
            step.fromJson((JsonObject) jsonSteps.get(i));
            System.out.println(step);
            steps.add(step);
        }
    }

}
