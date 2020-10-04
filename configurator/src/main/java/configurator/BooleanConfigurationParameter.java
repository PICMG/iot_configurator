package configurator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class BooleanConfigurationParameter implements ConfigurationParameter {
	private ComboBox<String> control = new ComboBox<String>();
	private SimpleStringProperty parameterName = new SimpleStringProperty();
	private ComboBox<String> access = new ComboBox<String>();
	private Tooltip tooltip = new Tooltip();

	public BooleanConfigurationParameter(String name, boolean value, String tooltipText, ObservableList<String> accessList) {
		parameterName.set(name);
		control.setItems(FXCollections.observableArrayList("true","false"));
		if (value) {
			control.getSelectionModel().select(0);
		} else {
			control.getSelectionModel().select(1);			
		}
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("Full")) { 
			control.setDisable(true);
		}

		// add the tooltip if there is one
		if ((tooltipText!=null)&&(!tooltipText.isEmpty())) {
			tooltip.setText(tooltipText);
			control.setTooltip(tooltip);
		}
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
