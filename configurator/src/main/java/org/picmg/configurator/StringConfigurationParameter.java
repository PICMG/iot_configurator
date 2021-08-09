//*******************************************************************
//    StringConfigurationParameter.java
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

import java.util.function.UnaryOperator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import org.picmg.jsonreader.JsonArray;

public class StringConfigurationParameter implements ConfigurationParameter {
	private TextField control = new TextField();
	private SimpleStringProperty parameterName = new SimpleStringProperty();
	private int maxLength;
	private Tooltip tooltip = new Tooltip();
	private ComboBox<String> access = new ComboBox<String>();
	
	public StringConfigurationParameter(String name, String value, String tooltipText, int maxLength, ObservableList<String> accessList) {
		parameterName.set(name);
		control.setText(value);
		this.maxLength = maxLength;
		// Limit the length of the text to the length specified
		control.setTextFormatter(new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) c -> {
		    if (c.isContentChange()) {
		        int newLength = c.getControlNewText().length();
		        if (newLength > this.maxLength) {
		        	c.setText("");
		            c.setRange(maxLength, maxLength);
		        }
		    }
		    return c;
		}));
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("full")) { 
			control.setDisable(true);
		}
		// add the tooltip if there is one
		if ((tooltipText!=null)&&(!tooltipText.isEmpty())) {
			tooltip.setText(tooltipText);
			control.setTooltip(tooltip);
		}
		control.setMaxWidth(125);
		control.setBorder(null);
		control.setBackground(null);
		access.setItems(accessList);
		access.getSelectionModel().select(0);
	}
	
	public StringConfigurationParameter(String name, String value, String tooltipText, int maxLength, JsonArray jaccessList) {
		ObservableList<String> accessList = FXCollections.observableArrayList();
		jaccessList.forEach(v -> {
			accessList.add(v.getValue(""));
		});
		parameterName.set(name);
		control.setText(value);
		this.maxLength = maxLength;
		// Limit the length of the text to the length specified
		control.setTextFormatter(new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) c -> {
		    if (c.isContentChange()) {
		        int newLength = c.getControlNewText().length();
		        if (newLength > this.maxLength) {
		        	c.setText("");
		            c.setRange(maxLength, maxLength);
		        }
		    }
		    return c;
		}));
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("full")) { 
			control.setDisable(true);
		}
		// add the tooltip if there is one
		if ((tooltipText!=null)&&(!tooltipText.isEmpty())) {
			tooltip.setText(tooltipText);
			control.setTooltip(tooltip);
		}
		control.setMaxWidth(125);
		control.setBorder(null);
		control.setBackground(null);
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
		control = (TextField)value;
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
