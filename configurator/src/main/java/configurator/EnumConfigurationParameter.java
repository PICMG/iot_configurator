package configurator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

public class EnumConfigurationParameter implements ConfigurationParameter {
	private ComboBox<String> control = new ComboBox<String>();
	private SimpleStringProperty parameterName = new SimpleStringProperty();
	private ComboBox<String> access = new ComboBox<String>();
	private Tooltip tooltip = new Tooltip();
	
	public EnumConfigurationParameter(String name, ObservableList<String> value, String tooltipText, ObservableList<String> accessList) {
		parameterName.set(name);
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("Full")) { 
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
