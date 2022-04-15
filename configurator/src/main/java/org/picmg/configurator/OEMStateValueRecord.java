package org.picmg.configurator;

import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.util.ArrayList;

public class OEMStateValueRecord {

    private int minStateValue;
    private int maxStateValue;
    private ArrayList<String> languageTags;
    private ArrayList<String> stateName;

    public OEMStateValueRecord(JsonObject json) {
        this.minStateValue = json.getInteger("minStateValue");
        this.maxStateValue = json.getInteger("maxStateValue");
        this.languageTags = new ArrayList<>();
        this.stateName = new ArrayList<>();

        for (JsonAbstractValue abstractValue : ((JsonArray) json.get("languageTags"))) {
            JsonValue languageTagJSON = (JsonValue) abstractValue;
            languageTags.add(languageTagJSON.getValue(""));
        }

        for (JsonAbstractValue abstractValue : ((JsonArray) json.get("stateName"))) {
            JsonValue stateNameJSON = (JsonValue) abstractValue;
            stateName.add(stateNameJSON.getValue(""));
        }
    }

    public OEMStateValueRecord(int minStateValue, int maxStateValue, ArrayList<String> languageTags, ArrayList<String> stateName) {
        this.minStateValue = minStateValue;
        this.maxStateValue = maxStateValue;
        this.languageTags = languageTags;
        this.stateName = stateName;
    }

    public int getMinStateValue() {
        return minStateValue;
    }

    public void setMinStateValue(int minStateValue) {
        this.minStateValue = minStateValue;
    }

    public int getMaxStateValue() {
        return maxStateValue;
    }

    public void setMaxStateValue(int maxStateValue) {
        this.maxStateValue = maxStateValue;
    }

    public ArrayList<String> getLanguageTags() {
        return languageTags;
    }

    public void setLanguageTags(ArrayList<String> languageTags) {
        this.languageTags = languageTags;
    }

    public ArrayList<String> getStateName() {
        return stateName;
    }

    public void setStateName(ArrayList<String> stateName) {
        this.stateName = stateName;
    }

    public JsonObject toJSON() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("minStateValue", new JsonValue(String.valueOf(minStateValue)));
        jsonObject.put("maxStateValue", new JsonValue(String.valueOf(maxStateValue)));

        JsonArray languageTagsJSON = new JsonArray();
        for (String tag : languageTags) {
            languageTagsJSON.add(new JsonValue(tag));
        }
        jsonObject.put("languageTags", languageTagsJSON);

        JsonArray stateNameJSON = new JsonArray();
        for (String state : stateName) {
            stateNameJSON.add(new JsonValue(state));
        }
        jsonObject.put("stateName", stateNameJSON);

        return jsonObject;
    }
}
