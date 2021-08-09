//*******************************************************************
//    EnumConfigurationParameter.java
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

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import org.picmg.jsonreader.JsonArray;

public class EnumConfigurationParameter implements ConfigurationParameter {
	private ComboBox<String> control = new ComboBox<String>();
	private SimpleStringProperty parameterName = new SimpleStringProperty();
	private ComboBox<String> access = new ComboBox<String>();
	private Tooltip tooltip = new Tooltip();
	
	public EnumConfigurationParameter(String name, ObservableList<String> value, String tooltipText, ObservableList<String> accessList) {
		parameterName.set(name);
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("full")) { 
			control.setDisable(true);
		}
		
		// add the tooltip if there is one
		if ((tooltipText!=null)&&(!tooltipText.isEmpty())) {
			tooltip.setText(tooltipText);
			control.setTooltip(tooltip);
		}
		control.setItems(value);
		control.getSelectionModel().select(0);			
		access.setItems(accessList);
		access.getSelectionModel().select(0);
	}
	
	public EnumConfigurationParameter(String name, ObservableList<String> value, String tooltipText, JsonArray jaccessList) {
		ObservableList<String> accessList = FXCollections.observableArrayList();
		jaccessList.forEach(v -> {
			accessList.add(v.getValue(""));
		});
		parameterName.set(name);
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("full")) { 
			control.setDisable(true);
		}
		
		// add the tooltip if there is one
		if ((tooltipText!=null)&&(!tooltipText.isEmpty())) {
			tooltip.setText(tooltipText);
			control.setTooltip(tooltip);
		}
		control.setItems(value);
		control.getSelectionModel().select(0);			
		access.setItems(accessList);
		access.getSelectionModel().select(0);
	}

	@Override
	public String getParameterName() {
		return parameterName.get();
	}

	@Override
	public void setParameterName(String name) {
		parameterName.set(name);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setControl(Control value) {
		control = (ComboBox<String>)value;
	}
	
	@Override
	public ComboBox<String> getAccess() {
		return access;
	};
	
	@Override
	public void setAccess(ComboBox<String> access) {
		this.access = access;
	}
}
