package org.picmg.configurator;

import org.picmg.jsonreader.JsonAbstractValue;
import org.picmg.jsonreader.JsonArray;
import org.picmg.jsonreader.JsonObject;
import org.picmg.jsonreader.JsonValue;

import java.util.ArrayList;

public class StateSet {

    private String name;
    private int id;
    private String vendorName;
    private int vendorIANA;
    private ArrayList<OEMStateValueRecord> oemStateValueRecords;

    public StateSet(JsonObject json) {
        this.name = json.getValue("name");
        this.vendorName = json.getValue("vendorName");
        this.vendorIANA = json.getInteger("vendorIANA");
        this.id = json.getInteger("stateSetId");
        this.oemStateValueRecords = new ArrayList<>();

        for (JsonAbstractValue abstractValue : ((JsonArray) json.get("oemStateValueRecords"))) {
            JsonObject oemStateValueRecordJSON = (JsonObject) abstractValue;
            OEMStateValueRecord oemStateValueRecord = new OEMStateValueRecord(oemStateValueRecordJSON);
            oemStateValueRecords.add(oemStateValueRecord);
        }

    }

    public StateSet(String name, int id, String vendorName, int vendorIANA, ArrayList<OEMStateValueRecord> oemStateValueRecords) {
        this.name = name;
        this.vendorName = vendorName;
        this.oemStateValueRecords = oemStateValueRecords;
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

    public int getStateSetID() {
        return id;
    }

    public void setStateSetID(int id) {
        this.id = id;
    }

    public int getVendorIANA() {
        return vendorIANA;
    }

    public void setVendorIANA(int vendorIANA) {
        this.vendorIANA = vendorIANA;
    }

    public JsonObject toJSON() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("name", new JsonValue(name));
        jsonObject.put("vendorName", new JsonValue(vendorName));
        jsonObject.put("vendorIANA", new JsonValue(String.valueOf(vendorIANA)));
        jsonObject.put("stateSetId", new JsonValue(String.valueOf(id)));
        JsonArray oemStateValueRecords = new JsonArray();
        for (StateSetTabController.ValueRecord oemStateValueRecord : this.oemStateValueRecords) {
            if (oemStateValueRecord == null) {
                continue;
            }
            oemStateValueRecords.add(((OEMStateValueRecord) oemStateValueRecord).toJSON());
        }
        jsonObject.put("oemStateValueRecords", oemStateValueRecords);
        return jsonObject;
    }

    public ArrayList<OEMStateValueRecord> getOemStateValueRecords() {
        return oemStateValueRecords;
    }

    public void setOemStateValueRecords(ArrayList<OEMStateValueRecord> oemStateValueRecords) {
        this.oemStateValueRecords = oemStateValueRecords;
    }
}
