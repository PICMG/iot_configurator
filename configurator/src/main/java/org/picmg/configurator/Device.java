//*******************************************************************
//    Device.java
//
//    More information on the PICMG IoT data model can be found within
//    the PICMG family of IoT specifications.  For more information,
//    please visit the PICMG web site (www.picmg.org)
//
//    Copyright (C) 2020,  PICMG
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
package org.picmg.configurator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.picmg.jsonreader.*;

public class Device {
    // field values
    public final static String[] unitsChoices = {
            "None", "Unspecified", "Degrees_C", "Degrees_F", "Kelvins", "Volts", "Amps", "Watts", "Joules", "Coulombs",
            "VA", "Nits", "Lumens", "Lux", "Candelas", "kPa", "PSI", "Newtons", "CFM", "RPM", "Hertz",
            "Seconds", "Minutes", "Hours", "Days", "Weeks", "Mils", "Inches", "Feet", "Cubic_Inches",
            "Cubic_Feet", "Meters", "Cubic_Centimeters", "Cubic_Meters", "Liters", "Fluid_Ounces",
            "Radians", "Steradians", "Revolutions", "Cycles", "Gravities", "Ounces", "Pounds",
            "Foot-Pounds", "Ounce-Inches", "Gauss", "Gilberts", "Henries", "Farads", "Ohms", "Siemens",
            "Moles", "Becquerels", "PPM+(parts/million)", "Decibels", "DbA", "DbC", "Grays", "Sieverts",
            "Color_Temperature_Degrees_K", "Bits", "Bytes", "Words_(data)", "DoubleWords", "QuadWords",
            "Percentage", "Pascals", "Counts", "Grams", "Newton-meters", "Hits", "Misses", "Retries",
            "Overruns/Overflows", "Underruns", "Collisions", "Packets", "Messages", "Characters",
            "Errors", "Corrected_Errors", "Uncorrectable_Errors", "Square_Mils", "Square_Inches",
            "Square_Feet", "Square_Centimeters", "Square_Meters"
    };
    public final static String[] rateChoices = {
            "None", "Per_MicroSecond", "Per_MilliSecond", "Per_Second", "Per_Minute", "Per_Hour",
            "Per_Day", "Per_Week", "Per_Month", "Per_Year"
    };
    public final static String[] relChoices = {
            "dividedBy", "multipliedBy"
    };

    // json representation of the device
    JsonObject jdev;
    ArrayList<String> allUsedPins;
    ArrayList<String> allUsedChannels;

    /**
     * constructor with Json initializer
     *
     * @param jdev
     */
    public Device(JsonObject jdev) {
        this.jdev = new JsonObject(jdev);

        // create any missing sections

        // -> If config, fail
        JsonObject configuration = new JsonObject();
        configuration.put("stateSets", new JsonArray());
        configuration.put("fruRecords", new JsonArray());
        configuration.put("logicalEntities", new JsonArray());
        this.jdev.put("configuration", configuration);

        allUsedPins = getAllUsedPins();
        allUsedChannels = getAllBoundChannels();


        // add all required entities to the configuration
        JsonArray entities = (JsonArray) ((JsonObject) jdev.get("capabilities")).get("logicalEntities");
        entities.forEach(entity -> {
            String name = entity.getValue("name");
            boolean required = entity.getBoolean("required");
            if (required) {
                addLogicalEntityConfigurationByName(name);
            }
        });

        // add all required fru records to the configuration
        JsonArray fruRecords = (JsonArray) ((JsonObject) jdev.get("capabilities")).get("fruRecords");
        fruRecords.forEach(record -> {
            String name = record.getValue("name");
            boolean required = record.getBoolean("required");
            if (required) {
                addFruRecordConfigurationByName(name);
            }
        });
    }

    public void loadDeviceConfig(JsonObject jdev) {

        this.jdev = new JsonObject(jdev);

        allUsedPins = getAllUsedPins();
        allUsedChannels = getAllBoundChannels();


        // add all required entities to the configuration
        JsonArray entities = (JsonArray) ((JsonObject) jdev.get("capabilities")).get("logicalEntities");
        entities.forEach(entity -> {
            String name = entity.getValue("name");
            boolean required = entity.getBoolean("required");
            if (required) {
                addLogicalEntityConfigurationByName(name);
            }
        });
    }

    public JsonObject getJson() {
        return jdev;
    }

    /*
     * return a list of all channels that have been bound in any logical entities
     */
    private ArrayList<String> getAllBoundChannels() {
        // create the list of channels - initially empty
        ArrayList<String> usedChannels = new ArrayList<>();
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        ((JsonArray) cfg.get("logicalEntities")).forEach(
                entity -> ((JsonArray) jdev.get("ioBindings")).forEach(
                        binding -> usedChannels.add(binding.getValue("boundChannel"))
                )
        );
        return usedChannels;
    }

    /*
     * find which pins have been used so far by any bound I/O on any logical entities
     */
    private ArrayList<String> getAllUsedPins() {
        // create the list of pins - initially empty
        ArrayList<String> usedPins = new ArrayList<>();
        JsonObject capabilities = (JsonObject) jdev.get("capabilities");
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        ((JsonArray) cfg.get("logicalEntities")).forEach(
                entity -> ((JsonArray) jdev.get("ioBindings")).forEach(
                        binding -> (
                                (JsonArray) capabilities.get("channels")).forEach(
                                channel -> {
                                    if (channel.getValue("name").equals(binding.getValue("boundChannel"))) {
                                        ((JsonArray) ((JsonObject) channel).get("pins")).forEach(pin -> usedPins.add(pin.getValue("name")));
                                    }
                                }
                        )
                )
        );
        return usedPins;
    }

    /*
     * return a list of pin names used by a specific named channel
     */
    public ArrayList<String> getPinsUsedByChannel(String channelName) {
        ArrayList<String> result = new ArrayList<>();
        JsonObject capabilities = (JsonObject) jdev.get("capabilities");
        ((JsonArray) capabilities.get("channels")).forEach(
                channel -> {
                    if (channel.getValue("name").equals(channelName)) {
                        ((JsonArray) ((JsonObject) channel).get("pins")).forEach(pin -> result.add(pin.getValue("name")));
                    }
                }
        );

        return result;
    }

    /* return the JsonObject for the named logical entity found within the
     * capabilities section of the device's JSON descriptor
     */
    public JsonObject getLogicalEntityCapabilityByName(String name) {
        JsonObject cap = (JsonObject) jdev.get("capabilities");
        JsonArray logicalEntities = (JsonArray) cap.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            if (edef.getValue("name").equals(name)) {
                return edef;
            }
        }
        return null;
    }

    /* return the fru record object that matches a given name.  If the
     * object is not found, return null
     */
    public JsonObject getCapabilitiesFruRecordByName(String name) {
        JsonObject cap = (JsonObject) jdev.get("capabilities");
        JsonArray fruRecords = (JsonArray) cap.get("fruRecords");
        for (JsonAbstractValue record : fruRecords) {
            JsonObject fruRecord = (JsonObject) record;
            if (fruRecord.getValue("name").equals(name)) {
                return fruRecord;
            }
        }
        return null;
    }

    /* add a named fru record to the configuration space
     *
     */
    public JsonAbstractValue addFruRecordConfigurationByName(String name) {
        JsonObject cap = (JsonObject) jdev.get("capabilities");
        JsonArray fruRecords = (JsonArray) cap.get("fruRecords");
        for (JsonAbstractValue fruRecord : fruRecords) {
            JsonObject frec = (JsonObject) fruRecord;
            if (frec.getValue("name").equals(name)) {
                // here if the entity has been found - copy it and add it to the
                // configurations
                JsonObject cfg = (JsonObject) jdev.get("configuration");
                JsonArray cfgFruRecords = (JsonArray) cfg.get("fruRecords");
                JsonObject newrecord = new JsonObject(frec);
                cfgFruRecords.add(newrecord);
                return newrecord;
            }
        }
        return null;
    }

    /* add a named logical entity to the configuration space
     * NOTE: no checking is done on the validity of the request.  It
     * is important not to add an entity that cannot be supported.
     */
    public JsonAbstractValue addLogicalEntityConfigurationByName(String name) {
        JsonObject cap = (JsonObject) jdev.get("capabilities");
        JsonArray logicalEntities = (JsonArray) cap.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            if (edef.getValue("name").equals(name)) {
                // here if the entity has been found - copy it and add it to the
                // configurations
                JsonObject cfg = (JsonObject) jdev.get("configuration");
                JsonArray cfgEntities = (JsonArray) cfg.get("logicalEntities");
                JsonObject newEntity = new JsonObject(edef);
                cfgEntities.add(newEntity);

                // if channel or pins required for already bound iobindings within the
                // logical entity are already used, add them to the list
                for (JsonAbstractValue jsonAbstractValue : (JsonArray) newEntity.get("ioBindings")) {
                    JsonObject binding = (JsonObject) jsonAbstractValue;
                    String channelName = binding.getValue("boundChannel");
                    if (channelName != null) {
                        allUsedChannels.add(channelName);

                        // see if any of the pins used by the channel are already used
                        ArrayList<String> channelPins = getPinsUsedByChannel(channelName);
                        allUsedPins.addAll(channelPins);
                    }
                }

                // configure any ioBinding default values
                JsonArray bindings = (JsonArray) newEntity.get("ioBindings");
                for (JsonAbstractValue val : bindings) {
                    JsonObject binding = (JsonObject) val;
                    // if the binding has an input curve that is null, set it to a default
                    // linear response.
                    if ((binding.containsKey("inputCurve"))) {
                        if (!binding.get("inputCurve").getClass().isAssignableFrom(JsonArray.class))
                            binding.put("inputCurve", createLinearResponseCurve());
                    }

                    // if the binding has an output curve that is null, set it to a default
                    // linear response.
                    if ((binding.containsKey("outputCurve"))) {
                        if (!binding.get("outputCurve").getClass().isAssignableFrom(JsonArray.class))
                            binding.put("outputCurve", createLinearResponseCurve());
                    }
                }
                return newEntity;
            }
        }
        return null;
    }

    /* remove a named logical entity configuration
     */
    public void removeLogicalEntityConfigurationByName(String name) {
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            if (edef.getValue("name").equals(name)) {
                // here if the entity has been found

                // if channel or pins required for already bound iobindings within the
                // logical entity are already used, remove them to the list
                for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                    JsonObject binding = (JsonObject) jsonAbstractValue;
                    String channelName = binding.getValue("boundChannel");
                    if (channelName != null) {
                        // see if any of the pins used by the channel are already used
                        ArrayList<String> channelPins = getPinsUsedByChannel(channelName);
                        allUsedPins.removeAll(channelPins);
                        allUsedChannels.remove(channelName);
                    }
                }
                logicalEntities.remove(edef);
                return;
            }
        }
    }

    /* retrieve a named logical entity configuration
     */
    public JsonObject getLogicalEntityConfigurationByName(String name) {
        JsonObject configuration = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) configuration.get("logicalEntities");
        for (JsonAbstractValue abstractValue : logicalEntities) {
            JsonObject logicalEntity = (JsonObject) abstractValue;
            if (logicalEntity.getValue("name").equals(name)) {
                return logicalEntity;
            }
        }
        return null;
    }

    /*
     * return the interface type for a specific channel name.  If the type is
     * not found, null is returned
     */
    public String getInterfaceTypeFromName(String channelName) {
        JsonObject cfg = (JsonObject) jdev.get("capabilities");
        JsonArray channelDefs = (JsonArray) cfg.get("channels");
        for (JsonAbstractValue channelDef : channelDefs) {
            JsonObject cdef = (JsonObject) channelDef;
            if (cdef.getValue("name").equals(channelName)) {
                return cdef.getValue("type");
            }
        }
        return null;
    }

    /*
     * return the binding value for a specific key.  If the name of the binding
     * or the key is not found, null is returned
     */
    public String getBindingValueFromKey(String bindingName, String bindingKey) {
        JsonObject cfg = (JsonObject) jdev.get("capabilities");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    return binding.getValue(bindingKey);
                }
            }
        }
        return null;
    }

    public String getConfiguredBindingValueFromKey(String bindingName, String bindingKey) {
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    return binding.getValue(bindingKey);
                }
            }
        }
        return null;
    }

    /*
     * set the binding value for a specific key.
     */
    public void setConfiguredBindingValueFromKey(String bindingName, String bindingKey, String newValue) {
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    binding.replace(bindingKey, new JsonValue(newValue));
                }

            }
        }
    }

    /*
     * return the binding for a specific name.  If the name of the binding
     * is not found, null is returned
     */
    public JsonObject getConfiguredBindingFromName(String bindingName) {
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    return binding;
                }
            }
        }
        return null;
    }

    /**
     * return the capabilities section binding for a specific name.  If the name of the binding
     * is not found, null is returned
     **/
    public JsonObject getCapabilitiesBindingFromName(String entityName, String bindingName) {
        JsonObject cfg = (JsonObject) jdev.get("capabilities");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            if (!edef.getValue("name").equals(entityName)) continue;

            // here if the entity name matches - find the matching binding name
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    return binding;
                }
            }
        }
        return null;
    }

    /**
     * this helper function creates a linear response curve.  This can be used
     * as a default for io bindings that dont have response curves specified.
     *
     * @return result - a two-point linear response curve.
     */
    private JsonArray createLinearResponseCurve() {
        JsonArray result = new JsonArray();
        JsonObject point1 = new JsonObject();
        JsonObject point2 = new JsonObject();
        point1.put("in", new JsonValue("0"));
        point1.put("out", new JsonValue("0"));
        point2.put("in", new JsonValue("1000"));
        point2.put("out", new JsonValue("1000"));
        result.add(0, point1);
        result.add(1, point2);
        return result;
    }

    /**
     * find the named binding and restore all its field values to those found
     * in the capabilities section of the device structure.
     **/
    public JsonObject restoreBindingToDefaults(String bindingName) {
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    // attempt to find the matching capabilities entity and binding that
                    // matches this one
                    JsonObject capBinding = getCapabilitiesBindingFromName(edef.getValue("name"), bindingName);
                    if (capBinding != null) {
                        // deep copy the capabilities binding to the configuration binding
                        binding.copy(capBinding);

                        // if the binding has an input curve that is null, set it to a default
                        // linear response.
                        if ((binding.containsKey("inputCurve"))) {
                            if (!binding.get("inputCurve").getClass().isAssignableFrom(JsonArray.class))
                                binding.put("inputCurve", createLinearResponseCurve());
                        }

                        // if the binding has an output curve that is null, set it to a default
                        // linear response.
                        if ((binding.containsKey("outputCurve"))) {
                            if (!binding.get("outputCurve").getClass().isAssignableFrom(JsonArray.class))
                                binding.put("outputCurve", createLinearResponseCurve());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * isFruRecordValid()
     * check to see if there are any errors in this fru record set.
     */
    static public boolean isFruRecordValid(JsonObject jsonFru) {

        // check the value of the vendor IANA
        String value = jsonFru.getValue("vendorIANA");
        if (value == null) {
            return false;
        }
        if (!App.isInteger(value)) {
            return false;
        }

        boolean required = jsonFru.getBoolean("required");

        // check the fru fields
        JsonArray fruFields = (JsonArray) jsonFru.get("fields");
        for (JsonAbstractValue field : fruFields) {
            JsonObject fruField = (JsonObject) field;
            boolean fieldRequired;

            // don't check optional fields
            fieldRequired = fruField.getBoolean("required");
            if (!fieldRequired) continue;

            value = fruField.getValue("type");
            if (value == null) {
                return false;
            }
            if (!App.isInteger(value)) {
                return false;
            }

            String format = fruField.getValue("format");
            if (value == null) {
                return false;
            }

            // get the value of the field
            value = fruField.getValue("value");
            if (value == null) {
                return false;
            }

            switch (format) {
                case "uint8":
                case "uint16":
                case "uint32":
                    if (!App.isInteger(value)) {
                        return false;
                    }
                    if (Integer.parseInt(value) < 0) {
                        return false;
                    }
                    break;
                case "bool8":
                case "sint8":
                case "sint16":
                case "sint32":
                    if (!App.isInteger(value)) {
                        return false;
                    }
                    break;
                case "real32":
                case "real64":
                    if (!App.isFloat(value)) {
                        return false;
                    }
                    break;
                case "string":
                    // no checking required for strings - any value is okay
                    break;
                case "timestamp 104":
                case "bytes":
                    // the structure of a timestamp 104 or bytes should be an array of bytes
                    JsonAbstractValue ary = fruField.get("value");

                    if (!ary.getClass().isAssignableFrom(JsonArray.class)) {
                        return false;
                    }

                    for (JsonAbstractValue obj : (JsonArray) ary) {
                        // check to make sure the element exists and is an integer
                        if (obj.getValue("") == null) {
                            return false;
                        }
                        if (!App.isInteger(obj.getValue(""))) {
                            return false;
                        }
                    }
                    break;
                default:
                    // this is an unknown type flag an error
                    return false;
            }
        }

        // no errors found
        return true;
    }

    /*******************************************************************************************************************
     * this function returns true if the named binding field is editable.  Editable fields will have a null value in
     * for the corresponding field in the capabilities section.
     *
     * @param bindingName - the name of the binding
     * @param fieldName - the name of the field
     */
    public boolean isConfigurationBindingFieldEditable(String bindingName, String fieldName) {
        JsonObject cfg = (JsonObject) jdev.get("configuration");
        JsonArray logicalEntities = (JsonArray) cfg.get("logicalEntities");
        for (JsonAbstractValue logicalEntity : logicalEntities) {
            JsonObject edef = (JsonObject) logicalEntity;
            for (JsonAbstractValue jsonAbstractValue : (JsonArray) edef.get("ioBindings")) {
                JsonObject binding = (JsonObject) jsonAbstractValue;
                String name = binding.getValue("name");
                if (name.equals(bindingName)) {
                    // attempt to find the matching capabilities entity and binding that matches this one
                    JsonObject capBinding = getCapabilitiesBindingFromName(edef.getValue("name"), bindingName);
                    return (capBinding.getValue(fieldName) == null);
                }
            }
        }
        // here if the field and/or binding could not be found - return false.
        return false;
    }

    /*
     * get a list of possible channel types that support a specific binding
     * constrained by the remaining pins on the device
     */
    public ArrayList<String> getPossibleChannelsTypesForBinding(JsonObject binding, ArrayList<String> usedPins) {
        ArrayList<String> result = new ArrayList<>();

        // get the channels
        ArrayList<String> allowedChannels = getPossibleChannelsForBinding(binding);

        // now find the types for each channel
        allowedChannels.forEach(channelName -> result.add(getInterfaceTypeFromName(channelName)));
        return result;
    }

    /*
     * get a list of possible channels (by name) that support a specific binding
     * constrained by the remaining pins on the device
     */
    public ArrayList<String> getPossibleChannelsForBinding(JsonObject binding) {
        ArrayList<String> result = new ArrayList<>();

        // if the channel is already bound - add the bound channel to the list
        if (binding.getValue("boundChannel") != null) {
            result.add(binding.getValue("boundChannel"));
        }

        // now find any other options that don't already have pins used
        ArrayList<String> possibleChannelTypes = new ArrayList<>();
        ((JsonArray) binding.get("allowedInterfaceTypes")).forEach(channel -> possibleChannelTypes.add(channel.getValue("")));
        // expand the channel types to channels (without adding used channels)
        ArrayList<String> possibleChannels = new ArrayList<>();
        JsonObject capabilities = (JsonObject) jdev.get("capabilities");
        JsonArray capChannels = (JsonArray) capabilities.get("channels");
        capChannels.forEach(capChannel -> {
            String channelName = capChannel.getValue("name");
            String channelType = capChannel.getValue("type");
            if ((!allUsedChannels.contains(channelName)) &&
                    (possibleChannelTypes.contains(channelType))) {
                possibleChannels.add(channelName);
            }
        });
        // now check to see if the channel has pins available
        possibleChannels.forEach(
                channel -> {
                    // get pins that are needed by the channel
                    ArrayList<String> channelPinsNeeded = getPinsUsedByChannel(channel);

                    // if any of this channels pins are already used, the channel cannot be bound
                    boolean sharesPins = false;
                    for (String allUsedPin : allUsedPins) {
                        if (channelPinsNeeded.contains(allUsedPin)) {
                            sharesPins = true;
                            break;
                        }
                    }
                    if (!sharesPins) result.add(channel);
                }
        );

        return result;
    }

    /*
     * remove the channel binding for a specific ioBinding
     * returns true if the binding is successfully removed.  Otherwise, false.
     */
    public boolean removeChannelBinding(JsonObject iobinding) {
        // don't allow unbinding a virtual channel
        if (iobinding.getBoolean("isVirtual")) return false;

        // if the channel is not bound return
        String boundChannel = iobinding.getValue("boundChannel");
        if (iobinding.getValue("boundChannel") == null) return true;

        // remove the pins used by the channel from the used pins list
        ArrayList<String> pinsNeeded = getPinsUsedByChannel(boundChannel);
        pinsNeeded.forEach(pin -> allUsedPins.remove(pin));

        // remove the channel from the used channels list
        allUsedChannels.remove(boundChannel);

        return true;
    }

    /*
     * set the channel binding to a specific channel type
     * returns true if the binding was successful.  Otherwise returns false
     */
    public boolean setChannelBinding(JsonObject iobinding, String channelName) {
        // don't allow binding a virtual channel
        if (iobinding.getBoolean("isVirtual")) return false;

        // if the channel is already bound, unbind it first
        if (iobinding.getValue("boundChannel") != null)
            removeChannelBinding(iobinding);

        // now, try to bind the channel.
        // first, see if the channel has already been used.
        if (allUsedChannels.contains(channelName)) return false;

        // next, see if the pins required by the channel have already been used
        ArrayList<String> pinsNeeded = getPinsUsedByChannel(channelName);
        for (String s : pinsNeeded) {
            if (allUsedPins.contains(s)) return false;
        }

        // no problems - bind the channel and update the used pins and used channels lists
        iobinding.put("boundChannel", new JsonValue(channelName));
        allUsedPins.addAll(pinsNeeded);
        allUsedChannels.add(channelName);
        return true;
    }

    /*
     * set the sensor configuration for the specified binding from the given file
     */
    public void setSensorFromFile(String bindingName, String filename) {
        JsonResultFactory factory = new JsonResultFactory();

        String fullFilename = System.getProperty("user.dir") + "/lib/sensors/" + filename + ".json";

        JsonAbstractValue sensor = factory.buildFromFile(Path.of(fullFilename));
        if (sensor == null) return;

        JsonObject binding = getConfiguredBindingFromName(bindingName);
        if (binding == null) return;

        binding.put("sensor", sensor);
    }

    /*
     * recurse the possible binding configurations to see if a valid solution
     * exists.
     * returns true if at least one solution is possible.
     */
    private boolean recurseForValidConfiguration(Iterator<JsonAbstractValue> entityIterator, Iterator<JsonAbstractValue> bindingIterator, ArrayList<String> usedChannels, ArrayList<String> usedPins) {
        Iterator<JsonAbstractValue> bi = bindingIterator;
        if ((bi == null) || (!bi.hasNext())) {
            // here if there is no additional binding in the current entity - recurse for the
            // next entity
            if (!entityIterator.hasNext()) {
                // here if recursion has traversed all entities and bindings
                // if code has reached this point, a valid combination has been found
                return true;
            }

            // step to the next entity
            JsonObject entity = (JsonObject) entityIterator.next();
            bi = null;

            // get the list of bindings for the next entity
            JsonArray bindings = (JsonArray) (entity.get("ioBindings"));
            if (bindings != null) {
                bi = bindings.iterator();
            }
            return recurseForValidConfiguration(entityIterator, bi, usedChannels, usedPins);
        }

        // here if there are additional channels for this entity - try binding the next
        // channel.  This will also automatically advance the binding iterator to the next
        // binding
        JsonObject binding = (JsonObject) bi.next();

        // if this binding is virtual, optional, or already bound,  don't add it.
        // instead, just recurse and return the result.
        if ((binding.getBoolean("isVirtual")) || (!binding.getBoolean("required")) ||
                (binding.getValue("boundChannel") != null)) {
            return recurseForValidConfiguration(entityIterator, bi, usedChannels, usedPins);
        }

        // Otherwise, check each channel binding to see if there is a solution that exists
        // with that binding.  If a solution is found, return true.
        ArrayList<String> possibleChannelTypes = new ArrayList<>();
        ((JsonArray) binding.get("allowedInterfaceTypes")).forEach(channel -> possibleChannelTypes.add(channel.getValue("")));
        // expand the channel types to channels (without adding used channels)
        ArrayList<String> possibleChannels = new ArrayList<>();
        JsonObject capabilities = (JsonObject) jdev.get("capabilities");
        JsonArray capChannels = (JsonArray) capabilities.get("channels");
        capChannels.forEach(capChannel -> {
            String channelName = capChannel.getValue("name");
            String channelType = capChannel.getValue("type");
            if ((!usedChannels.contains(channelName)) &&
                    (possibleChannelTypes.contains(channelType))) {
                possibleChannels.add(channelName);
            }
        });
        for (String channelName : possibleChannels) {
            if (setChannelBinding(binding, channelName)) {
                // here if the channel could be bound - recurse
                boolean result = recurseForValidConfiguration(entityIterator, bi, usedChannels, usedPins);

                // unbind the channel
                removeChannelBinding(binding);

                if (result) {
                    return true;
                }
            }
            // here if the channel could not be bound or there were no viable solutions
            // with the channel bound.  Loop to try another possible channel (if it exists)
        }

        // here if no viable binding has been found - return false
        return false;
    }

    /*
     * see if an entity with the specified name can be added to the device configuration.
     */
    public boolean canEntityBeAdded(String entityName) {
        ArrayList<String> usedChannels = allUsedChannels;
        ArrayList<String> usedPins = allUsedPins;
        JsonObject capabilities = (JsonObject) jdev.get("capabilities");
        JsonObject cfg = (JsonObject) jdev.get("configuration");

        // first, search the device capabilities for the named entity
        // if it does not exist, it can't be added
        Iterator<JsonAbstractValue> it = ((JsonArray) capabilities.get("logicalEntities")).iterator();
        boolean found = false;
        JsonObject capEntity = null;
        while (it.hasNext()) {
            JsonObject entity = (JsonObject) it.next();
            if (entity.getValue("name").equals(entityName)) {
                found = true;
                capEntity = entity;
                break;
            }
        }
        if (!found) return false;

        // next, search the device configuration for the named entity
        // if it already exists, it can't be added.
        it = ((JsonArray) cfg.get("logicalEntities")).iterator();
        while (it.hasNext()) {
            JsonObject entity = (JsonObject) it.next();
            if (entity.getValue("name").equals(entityName)) return false;
        }

        // if channel or pins required for already bound iobindings within the 
        // logical entity are already used, this entity cannot be used.
        it = ((JsonArray) capEntity.get("ioBindings")).iterator();
        ArrayList<String> newEntityBoundChannels = new ArrayList<>();
        ArrayList<String> newEntityUsedPins = new ArrayList<>();
        while (it.hasNext()) {
            JsonObject binding = (JsonObject) it.next();
            String channelName = binding.getValue("boundChannel");
            if (channelName != null) {
                // here the channel is bound - see if the channel is already used
                if (usedChannels.contains(channelName)) return false;

                // see if any of the pins used by the channel are already used
                ArrayList<String> channelPins = getPinsUsedByChannel(channelName);

                for (String pin : channelPins) {
                    if (usedPins.contains(pin)) return false;
                }
                newEntityBoundChannels.add(channelName);
                newEntityUsedPins.addAll(channelPins);
            }
        }

        // provisionally add the entity
        // first, search the device capabilities for the named entity
        // if it does not exist, it can't be added
        it = ((JsonArray) capabilities.get("logicalEntities")).iterator();
        JsonObject newEntity = new JsonObject(capEntity);
        ((JsonArray) cfg.get("logicalEntities")).add(newEntity);

        Iterator<JsonAbstractValue> entityIterator = ((JsonArray) cfg.get("logicalEntities")).iterator();
        Iterator<JsonAbstractValue> bindingIterator = ((JsonArray) newEntity.get("ioBindings")).iterator();
        allUsedChannels.addAll(newEntityBoundChannels);
        allUsedPins.addAll(newEntityUsedPins);
        boolean result = recurseForValidConfiguration(entityIterator, bindingIterator, newEntityBoundChannels, newEntityUsedPins);

        // remove the entity and any channels and pins associated with it
        ((JsonArray) cfg.get("logicalEntities")).remove(newEntity);
        allUsedChannels.removeAll(newEntityBoundChannels);
        allUsedPins.removeAll(newEntityUsedPins);

        // return the results
        return result;
    }

    // return a list of all entities that can still be added to the
    // configuration and meet the pin/channel constraints
    ArrayList<String> getListOfPossibleEntities() {
        ArrayList<String> result = new ArrayList<>();
        JsonObject cap = (JsonObject) jdev.get("capabilities");
        JsonArray entities = (JsonArray) cap.get("logicalEntities");
        entities.forEach(entity -> {
            if (canEntityBeAdded(entity.getValue("name"))) {
                result.add(entity.getValue("name"));
            }
        });
        return result;
    }

    public void writeToFile(String filename) {
        try {
            JsonObject configuration = new JsonObject();
            configuration.put("stateSets", new JsonArray());
            configuration.put("fruRecords", new JsonArray());
            configuration.put("logicalEntities", new JsonArray());
            jdev.put("configuration", configuration);
            FileWriter file = new FileWriter(filename);
            file.write(jdev.toString());
            file.close();
            System.out.println("wrote to file");
        } catch (IOException e) {
        }
    }

    /**
     * Check the response curve to make sure it has a valid internal structure
     *
     * @param curve - the curve json array to check
     * @return true if valid, otherwise false
     */
    static boolean isResponseCurveValid(JsonArray curve) {
        if (curve == null) return false;
        // TODO: check the array to make sure it is valid
        return true;
    }

    /**
     * Check the sensor to make sure it has a valid internal structure
     *
     * @param sensor - the sensor json object to check
     * @return true if valid, otherwise false
     */
    static boolean isSensorValid(JsonObject sensor) {
        if (sensor == null) return false;
        // TODO: check the sensor fields to make sure they are valid
        return true;
    }

    /**
     * Check the effecter to make sure it has a valid internal structure
     *
     * @param effecter - the sensor json object to check
     * @return true if valid, otherwise false
     */
    static boolean isEffecterValid(JsonObject effecter) {
        if (effecter == null) return false;
        // TODO: check the sensor fields to make sure they are valid
        return true;
    }

    /**
     * Check to see if the specified field within an IoBinding is valid.  This function checks to make sure the
     * field is non null (if required) and of the proper data type.  If there are interdependencies with other fields,
     * these are checked also.
     *
     * @param binding   - the binding to check
     * @param fieldname - the name of the field to check
     */
    static boolean isIoBindingFieldValid(JsonObject binding, String fieldname) {
        // if the binding doesnt contain the key, the field is not valid
        if (!binding.containsKey(fieldname)) return false;

        // get a string representation of the field and whether the field is virtual or not
        boolean virtual = binding.getBoolean("isVirtual");
        boolean inPdr = binding.getBoolean("includeInPdr");
        String value = binding.getValue(fieldname);

        // perform checks based on the type of field
        switch (fieldname) {
            case "boundChannel":
                // TODO: add code to check if the channel is valid
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                // otherwise, value must be non-null
                return (value != null);
            case "sensor":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                JsonAbstractValue sensor = binding.get("sensor");
                if (sensor == null) return false;
                if (!sensor.getClass().isAssignableFrom(JsonObject.class))
                    return false;
                return isSensorValid((JsonObject) sensor);
            case "effecter":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                return isEffecterValid((JsonObject) binding.get("effecter"));
            case "inputCurve":
            case "outputCurve":
                JsonAbstractValue curve = binding.get(fieldname);
                if (curve == null) return false;
                if (!curve.getClass().isAssignableFrom(JsonArray.class))
                    return false;
                return isResponseCurveValid((JsonArray) curve);
            case "inputGearingRatio":
            case "outputGearingRatio":
            case "physicalDefaultValue":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                // the value should be a real
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            case "physicalUnitModifier":
            case "physicalAuxUnitModifier":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                // the value should be an integer
                try {
                    Integer.parseInt(value);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            case "vendorIANA":
            case "stateSetVendorIANA":
            case "stateWhenHigh":
            case "stateWhenLow":
            case "usedStates":
            case "defaultState":
            case "stateSet":
                if (value == null) return false;
                if (!App.isInteger(value)) return false;
                return true;
            case "physicalBaseUnit":
            case "physicalAuxUnit":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                // otherwise,the value should be one of the units choices
                if (value == null) return false;
                for (String choice : unitsChoices)
                    if (value.equals(choice)) return true;
                return false;
            case "physicalRateUnit":
            case "physicalAuxRateUnit":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                // otherwise,the value should be one of the units choices
                if (value == null) return false;
                for (String choice : rateChoices)
                    if (value.equals(choice)) return true;
                return false;
            case "rel":
                if (virtual) {
                    // value must be null
                    return (value == null);
                }
                // otherwise,the value should be one of the units choices
                if (value == null) return false;
                for (String choice : relChoices)
                    if (value.equals(choice)) return true;
                return false;
            case "normalMax":
                if (inPdr) {
                    // value must be null
                    return (value == null);
                }
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // if normalMin is set, normal max must be greater than normal min
                    if (binding.get("normalMin") != null) {
                        return d > binding.getDouble("normalMin");
                    }
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
                break;
            case "normalMin":
                if (inPdr) {
                    // value must be null
                    return (value == null);
                }
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // if normalMax is set, normal min must be less than normal max
                    if (binding.get("normalMax") != null) {
                        return d < binding.getDouble("normalMax");
                    }
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
                break;
            case "upperThresholdWarning":
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // This value must be less than any thresholds above it
                    if (binding.get("upperThresholdCritical") != null) {
                        if (d >= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("upperThresholdFatal") != null) {
                        if (d >= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    // This value must be greater than any thresholds below it
                    if (binding.get("normalMax") != null) {
                        if (d <= binding.getDouble("normalMax")) return false;
                    }
                    if (binding.get("normalMin") != null) {
                        if (d <= binding.getDouble("normalMin")) return false;
                    }
                    if (binding.get("lowerThresholdWarning") != null) {
                        if (d <= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("lowerThresholdCritical") != null) {
                        if (d <= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("lowerThresholdFatal") != null) {
                        if (d <= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    return true;
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
            case "upperThresholdCritical":
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // This value must be less than any thresholds above it
                    if (binding.get("upperThresholdFatal") != null) {
                        if (d >= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }

                    // This value must be greater than any thresholds below it
                    if (binding.get("upperThresholdWarning") != null) {
                        if (d <= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("normalMax") != null) {
                        if (d <= binding.getDouble("normalMax")) return false;
                    }
                    if (binding.get("normalMin") != null) {
                        if (d <= binding.getDouble("normalMin")) return false;
                    }
                    if (binding.get("lowerThresholdWarning") != null) {
                        if (d <= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("lowerThresholdCritical") != null) {
                        if (d <= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("lowerThresholdFatal") != null) {
                        if (d <= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    return true;
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
            case "upperThresholdFatal":
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // This value must be greater than any thresholds below it
                    if (binding.get("upperThresholdCritical") != null) {
                        if (d <= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("upperThresholdWarning") != null) {
                        if (d <= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("normalMax") != null) {
                        if (d <= binding.getDouble("normalMax")) return false;
                    }
                    if (binding.get("normalMin") != null) {
                        if (d <= binding.getDouble("normalMin")) return false;
                    }
                    if (binding.get("lowerThresholdWarning") != null) {
                        if (d <= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("lowerThresholdCritical") != null) {
                        if (d <= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("lowerThresholdFatal") != null) {
                        if (d <= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    return true;
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
            case "lowerThresholdWarning":
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // This value must be less than any thresholds above it
                    if (binding.get("upperThresholdFatal") != null) {
                        if (d >= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    if (binding.get("upperThresholdCritical") != null) {
                        if (d >= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("upperThresholdWarning") != null) {
                        if (d >= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("normalMax") != null) {
                        if (d >= binding.getDouble("normalMax")) return false;
                    }
                    if (binding.get("normalMin") != null) {
                        if (d >= binding.getDouble("normalMin")) return false;
                    }

                    // This value must be greater than any thresholds below it
                    if (binding.get("lowerThresholdCritical") != null) {
                        if (d <= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("lowerThresholdFatal") != null) {
                        if (d <= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    return true;
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
            case "lowerThresholdCritical":
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // This value must be less than any thresholds above it
                    if (binding.get("upperThresholdFatal") != null) {
                        if (d >= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    if (binding.get("upperThresholdCritical") != null) {
                        if (d >= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("upperThresholdWarning") != null) {
                        if (d >= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("normalMax") != null) {
                        if (d >= binding.getDouble("normalMax")) return false;
                    }
                    if (binding.get("normalMin") != null) {
                        if (d >= binding.getDouble("normalMin")) return false;
                    }
                    if (binding.get("lowerThresholdWarning") != null) {
                        if (d >= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("lowerThresholdCritical") != null) {
                        if (d >= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }

                    // This value must be greater than any thresholds below it
                    if (binding.get("lowerThresholdFatal") != null) {
                        if (d <= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    return true;
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
            case "lowerThresholdFatal":
                // the value should be a real
                try {
                    double d = Double.parseDouble(value);

                    // This value must be less than any thresholds above it
                    if (binding.get("upperThresholdFatal") != null) {
                        if (d >= binding.getDouble("upperThresholdFatal"))
                            return false;
                    }
                    if (binding.get("upperThresholdCritical") != null) {
                        if (d >= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    if (binding.get("upperThresholdWarning") != null) {
                        if (d >= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("normalMax") != null) {
                        if (d >= binding.getDouble("normalMax")) return false;
                    }
                    if (binding.get("normalMin") != null) {
                        if (d >= binding.getDouble("normalMin")) return false;
                    }
                    if (binding.get("lowerThresholdWarning") != null) {
                        if (d >= binding.getDouble("upperThresholdWarning"))
                            return false;
                    }
                    if (binding.get("lowerThresholdCritical") != null) {
                        if (d >= binding.getDouble("upperThresholdCritical"))
                            return false;
                    }
                    return true;
                } catch (Exception ex) {
                    // numeric conversion error
                    return false;
                }
            default:
                break;
        }
        return false;
    }

    /**
     * check the given logical entity to make sure all its fields are valid
     *
     * @param entity
     * @return true if no errors, otherwise false
     */
    static boolean isLogicalEntityValid(JsonObject entity) {
        String val = entity.getValue("entityVendorIANA");
        if (val == null) return false;
        if (!App.isInteger(val)) return false;

        val = entity.getValue("name");
        if (val == null) return false;

        JsonAbstractValue bindings = entity.get("ioBindings");
        if (bindings == null) return false;
        if (!bindings.getClass().isAssignableFrom(JsonArray.class))
            return false;

        // if there are any required, but invalid bindings, this entity is not valid
        for (int i = 0; i < ((JsonArray) bindings).size(); i++) {
            JsonObject binding = (JsonObject) ((JsonArray) bindings).get(i);
            boolean required = binding.getBoolean("required");
            if ((required) && (!isBindingValid(binding))) return false;
        }

        // check parameters for any required but null
        JsonArray parameters = (JsonArray) entity.get("parameters");
        for (int i = 0; i < ((JsonArray) parameters).size(); i++) {
            JsonObject parameter = (JsonObject) parameters.get(i);
            if (parameter.get("value") == null) return false;
        }
        return true;
    }

    /**
     * Check to see if the given binding is valid
     *
     * @param binding - the ioBinding to check
     * @return true if the binding is valid, otherwise false
     */
    static boolean isBindingValid(JsonObject binding) {
        // determine the type of the binding in order to determine which checks to run
        boolean result = false;
        switch (binding.getValue("bindingType")) {
            case "numericSensor":
                result = isIoBindingFieldValid(binding, "boundChannel") &&
                        isIoBindingFieldValid(binding, "sensor") &&
                        isIoBindingFieldValid(binding, "inputCurve") &&
                        isIoBindingFieldValid(binding, "inputGearingRatio") &&
                        isIoBindingFieldValid(binding, "physicalBaseUnit") &&
                        isIoBindingFieldValid(binding, "physicalUnitModifier") &&
                        isIoBindingFieldValid(binding, "physicalRateUnit") &&
                        isIoBindingFieldValid(binding, "physicalAuxUnit") &&
                        isIoBindingFieldValid(binding, "rel") &&
                        isIoBindingFieldValid(binding, "physicalAuxUnitModifier") &&
                        isIoBindingFieldValid(binding, "physicalAuxRateUnit");
                // TODO: add threshold checking once the UI is updated
                //	isIoBindingFieldValid(binding,"normalMax") &&
                //	isIoBindingFieldValid(binding,"normalMin") &&
                //	isIoBindingFieldValid(binding,"upperThresholdWarning") &&
                //	isIoBindingFieldValid(binding,"upperThresholdCritical") &&
                //	isIoBindingFieldValid(binding,"upperThresholdFatal") &&
                //	isIoBindingFieldValid(binding,"lowerThresholdWarning") &&
                //	isIoBindingFieldValid(binding,"lowerThresholdCritical") &&
                //	isIoBindingFieldValid(binding,"lowerThresholdFatal");
                break;
            case "numericEffecter":
                result = isIoBindingFieldValid(binding, "boundChannel") &&
                        isIoBindingFieldValid(binding, "effecter") &&
                        isIoBindingFieldValid(binding, "outputCurve") &&
                        isIoBindingFieldValid(binding, "outputGearingRatio") &&
                        isIoBindingFieldValid(binding, "physicalBaseUnit") &&
                        isIoBindingFieldValid(binding, "physicalUnitModifier") &&
                        isIoBindingFieldValid(binding, "physicalRateUnit") &&
                        isIoBindingFieldValid(binding, "physicalAuxUnit") &&
                        isIoBindingFieldValid(binding, "rel") &&
                        isIoBindingFieldValid(binding, "physicalAuxUnitModifier") &&
                        isIoBindingFieldValid(binding, "physicalAuxRateUnit") &&
                        isIoBindingFieldValid(binding, "physicalDefaultValue");
                break;
            case "stateSensor":
                result = isIoBindingFieldValid(binding, "boundChannel") &&
                        isIoBindingFieldValid(binding, "stateSetVendorIANA") &&
                        isIoBindingFieldValid(binding, "stateSet") &&
                        isIoBindingFieldValid(binding, "usedStates") &&
                        isIoBindingFieldValid(binding, "stateWhenHigh") &&
                        isIoBindingFieldValid(binding, "stateWhenLow");
                break;
            case "stateEffecter":
                result = isIoBindingFieldValid(binding, "boundChannel") &&
                        isIoBindingFieldValid(binding, "stateSetVendorIANA") &&
                        isIoBindingFieldValid(binding, "stateSet") &&
                        isIoBindingFieldValid(binding, "usedStates") &&
                        isIoBindingFieldValid(binding, "stateWhenHigh") &&
                        isIoBindingFieldValid(binding, "stateWhenLow") &&
                        isIoBindingFieldValid(binding, "defaultState");
                break;
            default:
                System.out.println("binding type = " + binding.getValue("type"));
        }
        return result;
    }

    public void exportConfiguration(File outputFile) {
        // create a copy of the device structure
        JsonObject cleanDevice = new JsonObject(jdev);

        JsonObject configuration = (JsonObject) cleanDevice.get("configuration");
        JsonArray fruRecords = (JsonArray) configuration.get("fruRecords");
        JsonArray logicalEntities = (JsonArray) configuration.get("logicalEntities");

        // strip out any incomplete FRU records
        for (int i = fruRecords.size() - 1; i >= 0; i--) {
            JsonObject fruRecord = (JsonObject) fruRecords.get(i);
            if (!isFruRecordValid(fruRecord)) fruRecords.remove(fruRecord);
        }

        // strip out any incomplete Logical Entities
        for (int i = logicalEntities.size() - 1; i >= 0; i--) {
            JsonObject entity = (JsonObject) logicalEntities.get(i);
            if (!isLogicalEntityValid(entity)) logicalEntities.remove(entity);
        }

        // strip out any remaining incomplete io bindings
        for (int i = 0; i < logicalEntities.size(); i++) {
            JsonObject entity = (JsonObject) logicalEntities.get(i);
            // find check the IO Bindings
            JsonArray ioBindings = (JsonArray) entity.get("ioBindings");
            for (int j = ioBindings.size() - 1; j >= 0; j--) {
                JsonObject binding = (JsonObject) ioBindings.get(j);
                if (!isBindingValid(binding)) ioBindings.remove(binding);
            }
        }

        // write the result
        try (
                FileWriter writer = new FileWriter(outputFile);
                BufferedWriter bw = new BufferedWriter(writer)) {
            cleanDevice.writeToFile(bw);
            bw.close();
        } catch (IOException e) {
        }
    }
}
