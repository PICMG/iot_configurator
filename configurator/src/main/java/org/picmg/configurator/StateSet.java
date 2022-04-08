package org.picmg.configurator;

import java.util.HashMap;
import java.util.Map;

public class StateSet {

    private String name;
    private String vendorName;
    private Map<String, String> statesList; // Pairs between names and keys

    public StateSet(String name, String vendorName) {
        this.name = name;
        this.vendorName = vendorName;
        this.statesList = new HashMap<>();
    }

    public StateSet(String name, String vendorName, Map<String, String> statesList) {
        this.name = name;
        this.vendorName = vendorName;
        this.statesList = statesList;
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
}
