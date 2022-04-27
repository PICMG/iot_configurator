package org.picmg.configurator;

import javafx.beans.property.SimpleStringProperty;
import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.util.ArrayList;

public class OEMStateValueRecord {

    private int minStateValue;
    private int maxStateValue;
    // TODO: these should redefined as lists if many languages are ever expected
    private SimpleStringProperty languageTags = new SimpleStringProperty();
    private SimpleStringProperty stateName = new SimpleStringProperty();

    public OEMStateValueRecord(JsonObject json) {
        this.minStateValue = json.getInteger("minStateValue");
        this.maxStateValue = json.getInteger("maxStateValue");

        JsonArray abstractValue = ((JsonArray) json.get("languageTags"));
        languageTags.set(abstractValue.getValue("0."));

        abstractValue = ((JsonArray) json.get("stateName"));
        stateName.set(abstractValue.getValue("0."));
    }

    public OEMStateValueRecord(int minStateValue, int maxStateValue, String languageTags, String stateName) {
        this.minStateValue = minStateValue;
        this.maxStateValue = maxStateValue;
        this.languageTags.set(languageTags);
        this.stateName.set(stateName);
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

    public String getLanguageTags() {
        return languageTags.get();
    }

    public void setLanguageTags(String languageTags) {
        this.languageTags.set(languageTags);
    }

    public String getStateName() {
        return stateName.get();
    }

    public void setStateName(String stateName) {
        this.stateName.set(stateName);
    }

    public JsonObject toJSON() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("minStateValue", new JsonValue(String.valueOf(minStateValue)));
        jsonObject.put("maxStateValue", new JsonValue(String.valueOf(maxStateValue)));

        JsonArray languageTagsJSON = new JsonArray();
        languageTagsJSON.add(new JsonValue(languageTags.get()));
        jsonObject.put("languageTags", languageTagsJSON);

        JsonArray stateNameJSON = new JsonArray();
        stateNameJSON.add(new JsonValue(stateName.get()));
        jsonObject.put("stateName", stateNameJSON);

        return jsonObject;
    }
}
