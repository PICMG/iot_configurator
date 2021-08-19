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

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.picmg.jsonreader.*;

public class Device {
	// json representation of the device
	JsonObject jdev;
	ArrayList<String> allUsedPins;
	ArrayList<String> allUsedChannels;
	
	// constructor with Json initializer
	public Device(JsonObject jdev) {
		this.jdev = new JsonObject(jdev);
		
		// create any missing sections
        JsonObject configuration = new JsonObject();
        configuration.put("stateSets",new JsonArray());
        configuration.put("fruRecords",new JsonArray());
        configuration.put("logicalEntities", new JsonArray());
        this.jdev.put("configuration",configuration);
        
        allUsedPins = getAllUsedPins();
        allUsedChannels = getAllBoundChannels();
        
        // add all required entities to the configuration
        JsonArray entities = (JsonArray)((JsonObject)jdev.get("capabilities")).get("logicalEntities");
        entities.forEach(entity-> {
        	String name = ((JsonObject)entity).getValue("name");
        	boolean required = ((JsonObject)entity).getBoolean("required");
        	if (required) {
        		addLogicalEntityConfigurationByName(name);
        	}
        });
        
        // add all required fru records to the configuration
        JsonArray fruRecords = (JsonArray)((JsonObject)jdev.get("capabilities")).get("fruRecords");
        fruRecords.forEach(record-> {
        	String name = ((JsonObject)record).getValue("name");
        	boolean required = ((JsonObject)record).getBoolean("required");
        	if (required) {
        		addFruRecordConfigurationByName(name);
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
        ArrayList<String> usedChannels = new ArrayList<String>();
        JsonObject cfg = (JsonObject)jdev.get("configuration");
        ((JsonArray)cfg.get("logicalEntities")).forEach( 
        	entity -> ((JsonArray)jdev.get("ioBindings")).forEach(
        		binding ->  {
        			usedChannels.add(((JsonObject)binding).getValue("boundChannel"));
        		} 
        	) 
        );
        return usedChannels;
	}

	/*
     * find which pins have been used so far by any bound I/O on any logical entities
	 */
	private ArrayList<String> getAllUsedPins() {
		// create the list of pins - initially empty
        ArrayList<String> usedPins = new ArrayList<String>();
        JsonObject capabilities = (JsonObject)jdev.get("capabilities"); 
        JsonObject cfg          = (JsonObject)jdev.get("configuration");
        ((JsonArray)cfg.get("logicalEntities")).forEach( 
        	entity -> ((JsonArray)jdev.get("ioBindings")).forEach(
        		binding -> (
        			(JsonArray)capabilities.get("channels")).forEach(
                    channel -> { 
                    	if (((JsonObject)channel).getValue("name").equals(((JsonObject)binding).getValue("boundChannel"))) {
                    		((JsonArray)((JsonObject)channel).get("pins")).forEach( pin -> usedPins.add(pin.getValue("name"))); 
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
        ArrayList<String> result = new ArrayList<String>();
        JsonObject capabilities = (JsonObject)jdev.get("capabilities"); 
		((JsonArray)capabilities.get("channels")).forEach(
			channel -> { 
                if (((JsonObject)channel).getValue("name").equals(channelName)) {
                	((JsonArray)((JsonObject)channel).get("pins")).forEach( pin -> result.add(pin.getValue("name"))); 
                } 
			} 
        );

        return result;
	}
	
	/* return the JsonObject for the named logical entity found within the 
	 * capabilities section of the device's JSON descriptor
	 */
	public JsonObject getLogicalEntityCapabilityByName(String name) {
		JsonObject cap = (JsonObject)jdev.get("capabilities");
		JsonArray logicalEntities = (JsonArray)cap.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
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
		JsonObject cap = (JsonObject)jdev.get("capabilities");
		JsonArray fruRecords = (JsonArray)cap.get("fruRecords");
		Iterator<JsonAbstractValue> it = fruRecords.iterator();
		while (it.hasNext()) {
			JsonObject fruRecord = (JsonObject)it.next();
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
		JsonObject cap = (JsonObject)jdev.get("capabilities");
		JsonArray fruRecords = (JsonArray)cap.get("fruRecords");
		Iterator<JsonAbstractValue> it = fruRecords.iterator();
		while (it.hasNext()) {
			JsonObject frec = (JsonObject)it.next();
			if (frec.getValue("name").equals(name)) {
				// here if the entity has been found - copy it and add it to the 
				// configurations 
				JsonObject cfg = (JsonObject)jdev.get("configuration");
				JsonArray cfgFruRecords = (JsonArray)cfg.get("fruRecords");
				JsonObject newrecord = new JsonObject(frec); 
				cfgFruRecords.add(frec);				
				return frec;
			}
		}
		return null;
	}	

	/* add a named logical entity to the configuration space
	 * NOTE: no checking is done on the validity of the request.  It
	 * is important not to add an entity that cannot be supported.
	 */
	public JsonAbstractValue addLogicalEntityConfigurationByName(String name) {
		JsonObject cap = (JsonObject)jdev.get("capabilities");
		JsonArray logicalEntities = (JsonArray)cap.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			if (edef.getValue("name").equals(name)) {
				// here if the entity has been found - copy it and add it to the 
				// configurations 
				JsonObject cfg = (JsonObject)jdev.get("configuration");
				JsonArray cfgEntities = (JsonArray)cfg.get("logicalEntities");
				JsonObject newEntity = new JsonObject(edef); 
				cfgEntities.add(newEntity);
				
		        // if channel or pins required for already bound iobindings within the 
		        // logical entity are already used, add them to the list
		        Iterator<JsonAbstractValue>it2 = ((JsonArray)newEntity.get("ioBindings")).iterator(); 
		        while (it2.hasNext()) {
		    		JsonObject binding = (JsonObject)it2.next();
		    		String channelName = binding.getValue("boundChannel");
		    		if (channelName!=null) {
		    			allUsedChannels.add(channelName);
		    		
		    			// see if any of the pins used by the channel are already used
		    			ArrayList<String> channelPins = getPinsUsedByChannel(channelName);
		    			allUsedPins.addAll(channelPins);
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
		JsonObject cfg = (JsonObject)jdev.get("configuration");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			if (edef.getValue("name").equals(name)) {
				// here if the entity has been found 
				
		        // if channel or pins required for already bound iobindings within the 
		        // logical entity are already used, remove them to the list
		        Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator(); 
		        while (it2.hasNext()) {
		    		JsonObject binding = (JsonObject)it2.next();
		    		String channelName = binding.getValue("boundChannel");
		    		if (channelName!=null) {
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
		return;
	}

	/*
	 * return the interface type for a specific channel name.  If the type is
	 * not found, null is returned
	 */
	public String getInterfaceTypeFromName(String channelName) {
		JsonObject cfg = (JsonObject)jdev.get("capabilities");
		JsonArray channelDefs = (JsonArray)cfg.get("channels");
		Iterator<JsonAbstractValue> it = channelDefs.iterator();
		while (it.hasNext()) {
			JsonObject cdef = (JsonObject)it.next();
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
		JsonObject cfg = (JsonObject)jdev.get("capabilities");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator(); 
	        while (it2.hasNext()) {
	    		JsonObject binding = (JsonObject)it2.next();
	    		String name = binding.getValue("name");
	    		if(name.equals(bindingName)) {
	    			return binding.getValue(bindingKey);
	    		}
	        }
		}
		return null;
	}

	public String getConfiguredBindingValueFromKey(String bindingName, String bindingKey) {
		JsonObject cfg = (JsonObject)jdev.get("configuration");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator();
			while (it2.hasNext()) {
				JsonObject binding = (JsonObject)it2.next();
				String name = binding.getValue("name");
				if(name.equals(bindingName)) {
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
		JsonObject cfg = (JsonObject)jdev.get("configuration");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator(); 
	        while (it2.hasNext()) {
	    		JsonObject binding = (JsonObject)it2.next();
	    		String name = binding.getValue("name");
	    		if(name.equals(bindingName)) {
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
		JsonObject cfg = (JsonObject)jdev.get("configuration");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator(); 
	        while (it2.hasNext()) {
	    		JsonObject binding = (JsonObject)it2.next();
	    		String name = binding.getValue("name");
	    		if(name.equals(bindingName)) {
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
		JsonObject cfg = (JsonObject)jdev.get("capabilities");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			if (!edef.getValue("name").equals(entityName)) continue;

			// here if the entity name matches - find the matching binding name
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator();
			while (it2.hasNext()) {
				JsonObject binding = (JsonObject)it2.next();
				String name = binding.getValue("name");
				if(name.equals(bindingName)) {
					return binding;
				}
			}
		}
		return null;
	}

	/**
	 * find the named binding and restore all its field values to those found
	 * in the capabilities section of the device structure.
	 **/
	public JsonObject restoreBindingToDefaults(String bindingName) {
		JsonObject cfg = (JsonObject)jdev.get("configuration");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator();
			while (it2.hasNext()) {
				JsonObject binding = (JsonObject)it2.next();
				String name = binding.getValue("name");
				if(name.equals(bindingName)) {
					// attempt to find the matching capabilities entity and binding that
					// matches this one
					JsonObject capBinding = getCapabilitiesBindingFromName(edef.getValue("name"),bindingName);
					if (capBinding!=null) {
						// deep copy the capabilities binding to the configuration binding
						binding.copy(capBinding);
					}
				}
			}
		}
		return null;
	}

	/*******************************************************************************************************************
	 * this function returns true if the named binding field is editable.  Editable fields will have a null value in
	 * for the corresponding field in the capabilities section.
	 *
	 * @param bindingName - the name of the binding
	 * @param fieldName - the name of the field
	 * @return
	 */
	public boolean isConfigurationBindingFieldEditable(String bindingName, String fieldName) {
		JsonObject cfg = (JsonObject)jdev.get("configuration");
		JsonArray logicalEntities = (JsonArray)cfg.get("logicalEntities");
		Iterator<JsonAbstractValue> it = logicalEntities.iterator();
		while (it.hasNext()) {
			JsonObject edef = (JsonObject)it.next();
			Iterator<JsonAbstractValue>it2 = ((JsonArray)edef.get("ioBindings")).iterator();
			while (it2.hasNext()) {
				JsonObject binding = (JsonObject)it2.next();
				String name = binding.getValue("name");
				if(name.equals(bindingName)) {
					// attempt to find the matching capabilities entity and binding that matches this one
					JsonObject capBinding = getCapabilitiesBindingFromName(edef.getValue("name"),bindingName);
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
        ArrayList<String> result = new ArrayList<String>();

        // get the channels
        ArrayList<String> allowedChannels = getPossibleChannelsForBinding(binding);
        
        // now find the types for each channel
        allowedChannels.forEach(channelName -> {
        	result.add(getInterfaceTypeFromName(channelName));
        });
		return result;
	}

	/* 
	 * get a list of possible channels (by name) that support a specific binding
	 * constrained by the remaining pins on the device
	 */
	public ArrayList<String> getPossibleChannelsForBinding(JsonObject binding) {
        ArrayList<String> result = new ArrayList<String>();

        // if the channel is already bound - add the bound channel to the list
		if (binding.getValue("boundChannel")!=null) {
			result.add(binding.getValue("boundChannel"));
		}

		// now find any other options that don't already have pins used
		ArrayList<String> possibleChannelTypes = new ArrayList<String>();
		((JsonArray)binding.get("allowedInterfaceTypes")).forEach(channel -> {
			possibleChannelTypes.add(channel.getValue(""));
		});
		// expand the channel types to channels (without adding used channels)
		ArrayList<String> possibleChannels = new ArrayList<String>();
		JsonObject capabilities = (JsonObject)jdev.get("capabilities");
		JsonArray capChannels = (JsonArray)capabilities.get("channels");
		capChannels.forEach(capChannel -> {
			String channelName = capChannel.getValue("name");
			String channelType = capChannel.getValue("type");
			if ((!allUsedChannels.contains(channelName))&&
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
        		for (int i=0;i<allUsedPins.size(); i++) {
        			if (channelPinsNeeded.contains(allUsedPins.get(i))) {
        				sharesPins = true;
        				break;
        			}
        		};
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
		if (iobinding.getValue("boundChannel")==null) return true;
		
		// remove the pins used by the channel from the used pins list
		ArrayList<String> pinsNeeded = getPinsUsedByChannel(boundChannel);
		pinsNeeded.forEach(pin -> {allUsedPins.remove(pin);});
		
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
		if (iobinding.getValue("boundChannel")!=null) removeChannelBinding(iobinding);
		
		// now, try to bind the channel.
		// first, see if the channel has already been used.
		if (allUsedChannels.contains(channelName)) return false;
		
		// next, see if the pins required by the channel have already been used
		ArrayList<String> pinsNeeded = getPinsUsedByChannel(channelName);
		for (int i=0;i<pinsNeeded.size(); i++) {
			if (allUsedPins.contains(pinsNeeded.get(i))) return false;
		};
		
		// no problems - bind the channel and update the used pins and used channels lists
		iobinding.put("boundChannel",new JsonValue(channelName));
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

		binding.put("sensor",sensor);
	}

	/*
	 * recurse the possible binding configurations to see if a valid solution
	 * exists.
	 * returns true if at least one solution is possible.
	 */
	 private boolean recurseForValidConfiguration(Iterator<JsonAbstractValue> entityIterator, Iterator<JsonAbstractValue> bindingIterator, ArrayList<String> usedChannels, ArrayList<String> usedPins) {
		Iterator<JsonAbstractValue> ei = entityIterator;
		Iterator<JsonAbstractValue> bi = bindingIterator;
		if ((bi==null)||(!bi.hasNext())) {
			// here if there is no additional binding in the current entity - recurse for the
			// next entity
			if (!ei.hasNext()) {
				// here if recursion has traversed all entities and bindings
				// if code has reached this point, a valid combination has been found
				return true;
			}
			
			// step to the next entity 
			JsonObject entity = (JsonObject)ei.next();
			bi = null;
			
			// get the list of bindings for the next entity
			JsonArray bindings = (JsonArray)(entity.get("ioBindings"));
			if (bindings!=null) {
				bi = bindings.iterator();
			}
			return recurseForValidConfiguration(ei, bi, usedChannels, usedPins);
		}
		
		// here if there are additional channels for this entity - try binding the next
		// channel.  This will also automatically advance the binding iterator to the next
		// binding
		JsonObject binding = (JsonObject)bi.next();
		
		// if this binding is virtual, optional, or already bound,  don't add it.
		// instead, just recurse and return the result.
		if ((binding.getBoolean("isVirtual"))||(!binding.getBoolean("required"))||
			(binding.getValue("boundChannel")!=null)) {
			return recurseForValidConfiguration(ei,bi,usedChannels,usedPins);
		}
		
		// Otherwise, check each channel binding to see if there is a solution that exists
		// with that binding.  If a solution is found, return true.
		ArrayList<String> possibleChannelTypes = new ArrayList<String>();
		((JsonArray)binding.get("allowedInterfaceTypes")).forEach(channel -> {
			possibleChannelTypes.add(channel.getValue(""));
		});
		// expand the channel types to channels (without adding used channels)
		ArrayList<String> possibleChannels = new ArrayList<String>();
		JsonObject capabilities = (JsonObject)jdev.get("capabilities");
		JsonArray capChannels = (JsonArray)capabilities.get("channels");
		capChannels.forEach(capChannel -> {
			String channelName = capChannel.getValue("name");
			String channelType = capChannel.getValue("type");
			if ((!usedChannels.contains(channelName))&&
				(possibleChannelTypes.contains(channelType))) {
				possibleChannels.add(channelName);
			}
		});
		for (int i=0; i<possibleChannels.size(); i++) {
			String channelName = possibleChannels.get(i);
			if (setChannelBinding(binding, channelName)) {
				// here if the channel could be bound - recurse
				boolean result = recurseForValidConfiguration(ei, bi, usedChannels, usedPins);
				
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
		JsonObject capabilities = (JsonObject)jdev.get("capabilities");
		JsonObject cfg = (JsonObject)jdev.get("configuration");

		// first, search the device capabilities for the named entity
		// if it does not exist, it can't be added
        Iterator<JsonAbstractValue> it = ((JsonArray)capabilities.get("logicalEntities")).iterator(); 
        boolean found = false;
        JsonObject capEntity = null;
        while (it.hasNext()) {
            JsonObject entity = (JsonObject)it.next();
        	if (entity.getValue("name").equals(entityName)) {
        		found = true;
        		capEntity = entity;
        		break;
        	}
        }
        if (!found) return false;
        
		// next, search the device configuration for the named entity
		// if it already exists, it can't be added.
        it = ((JsonArray)cfg.get("logicalEntities")).iterator(); 
        while (it.hasNext()) {
            JsonObject entity = (JsonObject)it.next();
        	if (entity.getValue("name").equals(entityName)) return false;
        }
        
        // if channel or pins required for already bound iobindings within the 
        // logical entity are already used, this entity cannot be used.
        it = ((JsonArray)capEntity.get("ioBindings")).iterator(); 
        ArrayList<String> newEntityBoundChannels = new ArrayList<String>();
        ArrayList<String> newEntityUsedPins = new ArrayList<String>();
        while (it.hasNext()) {
    		JsonObject binding = (JsonObject)it.next();
    		String channelName = binding.getValue("boundChannel");
    		if (channelName!=null) {
    			// here the channel is bound - see if the channel is already used
    			if (usedChannels.contains(channelName)) return false;
    		
    			// see if any of the pins used by the channel are already used
    			ArrayList<String> channelPins = getPinsUsedByChannel(channelName);
    			
    			Iterator<String> itpin = channelPins.iterator();
    			while (itpin.hasNext()) {
    				String pin = itpin.next();
    				if (usedPins.contains(pin)) return false;
    			}
    			newEntityBoundChannels.add(channelName);
    			newEntityUsedPins.addAll(channelPins);
    		}
        }
        
        // provisionally add the entity
		// first, search the device capabilities for the named entity
		// if it does not exist, it can't be added
        it = ((JsonArray)capabilities.get("logicalEntities")).iterator(); 
        JsonObject newEntity = new JsonObject(capEntity);
        ((JsonArray)cfg.get("logicalEntities")).add(newEntity);
		
        Iterator<JsonAbstractValue> entityIterator = ((JsonArray)cfg.get("logicalEntities")).iterator();
        Iterator<JsonAbstractValue> bindingIterator = ((JsonArray)newEntity.get("ioBindings")).iterator();
        allUsedChannels.addAll(newEntityBoundChannels);
        allUsedPins.addAll(newEntityUsedPins);
        boolean result = recurseForValidConfiguration(entityIterator, bindingIterator, newEntityBoundChannels, newEntityUsedPins); 
        		
		// remove the entity and any channels and pins associated with it
        ((JsonArray)cfg.get("logicalEntities")).remove(newEntity);
		allUsedChannels.removeAll(newEntityBoundChannels);
		allUsedPins.removeAll(newEntityUsedPins);
		
		// return the results
		return result;
	}
	
	// return a list of all entities that can still be added to the
	// configuration and meet the pin/channel constraints
	ArrayList<String> getListOfPossibleEntities() {
		ArrayList<String> result = new ArrayList<String>();
		JsonObject cap = (JsonObject)jdev.get("capabilities");
		JsonArray entities = (JsonArray)cap.get("logicalEntities");
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
	        configuration.put("stateSets",new JsonArray());
	        configuration.put("fruRecords",new JsonArray());
	        configuration.put("logicalEntities", new JsonArray());
	        jdev.put("configuration",configuration);
			FileWriter file = new FileWriter(filename);
			file.write(jdev.toString());
			file.close();
			System.out.println("wrote to file");
	      } catch (IOException e) {
	      }
	}
}
