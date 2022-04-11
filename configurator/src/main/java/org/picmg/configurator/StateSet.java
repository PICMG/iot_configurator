package org.picmg.configurator;

import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class StateSet {

    private String name;
    private int id;
    private String vendorName;
    private int vendorIANA;
    private Map<String, String> statesList; // Pairs between names and keys

    public StateSet(String name, String vendorName) {
        this(name, vendorName, new HashMap<>());
    }

    public StateSet(String name, String vendorName, Map<String, String> statesList) {
        this(name, -1, vendorName, -1, statesList);
    }

    public StateSet(String name, int id, String vendorName, int vendorIANA, Map<String, String> statesList) {
        this.name = name;
        this.vendorName = vendorName;
        this.statesList = statesList;
        this.id = id;
        this.vendorIANA = vendorIANA;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Map<String, String> getStatesList() {
        return statesList;
    }

    public void setStatesList(Map<String, String> statesList) {
        this.statesList = statesList;
    }

    public String getStateByKey(String key) {
        return statesList.get(key);
    }

    public void setState(String key, String value) {
        statesList.put(key, value);
    }

    public JsonObject toJSON() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("name", new JsonValue(name));
        jsonObject.put("vendorName", new JsonValue(vendorName));
        jsonObject.put("vendorIANA", new JsonValue(String.valueOf(vendorIANA)));
        jsonObject.put("stateSetID", new JsonValue(String.valueOf(id)));
        JsonArray oemStateValueRecords = new JsonArray();
        jsonObject.put("oemStateValueRecords", oemStateValueRecords);
        return jsonObject;
    }
}
