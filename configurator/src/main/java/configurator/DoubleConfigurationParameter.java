package configurator;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import jsonreader.JsonArray;

public class DoubleConfigurationParameter implements ConfigurationParameter {
	private TextField control = new TextField();
	private SimpleStringProperty parameterName = new SimpleStringProperty();
	private double minValue;
	private double maxValue;
	private ComboBox<String> access = new ComboBox<String>();
    private final static Pattern regex = Pattern.compile("-?[0-9]*\\.?[0-9]*");
	private Tooltip tooltip = new Tooltip();

	public DoubleConfigurationParameter(String name, double value, String tooltipText, double minValue, double maxValue, ObservableList<String> accessList) {
		parameterName.set(name);
		control.setText(Double.toString(value));
		this.minValue = minValue;
		this.maxValue = maxValue;
		// Limit to characters for floating point - this could be improved to check to make sure that
		// the -/. characters only appear in the right places.
		control.setTextFormatter(new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) c -> {
		    if (c.isAdded()) {
		    	Matcher m = regex.matcher(c.getControlNewText());
		    	
		    	if (!m.matches()) {
		    		// if resulting text cannot match a floating point,
		    		// don't add the new text
		    		c.setText("");
		    	}
		    }
		    return c;
		}));
		// if the access does not include "Full", don't allow the user to change the value
		if (!accessList.contains("Full")) { 
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

	public DoubleConfigurationParameter(String name, double value, String tooltipText, double minValue, double maxValue, JsonArray jaccessList) {
		ObservableList<String> accessList = FXCollections.observableArrayList();
		jaccessList.forEach(v -> {
			accessList.add(v.getValue(""));
		});
		parameterName.set(name);
		control.setText(Double.toString(value));
		this.minValue = minValue;
		this.maxValue = maxValue;
		// Limit to characters for floating point - this could be improved to check to make sure that
		// the -/. characters only appear in the right places.
		control.setTextFormatter(new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) c -> {
		    if (c.isAdded()) {
		    	Matcher m = regex.matcher(c.getControlNewText());
		    	
		    	if (!m.matches()) {
		    		// if resulting text cannot match a floating point,
		    		// don't add the new text
		    		c.setText("");
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
