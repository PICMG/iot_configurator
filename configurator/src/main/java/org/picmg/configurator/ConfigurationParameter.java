package org.picmg.configurator;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;

/*
 * An interface for generic configuration parameters.  Concrete
 * classes derived from this implement the specific behaviors
 */
public interface ConfigurationParameter {
	public String getParameterName();
	public void setParameterName(String name);
	
	public Control getControl();	
	public void setControl(Control value);
	
	public ComboBox<String> getAccess();
	public void setAccess(ComboBox<String> access);
}

