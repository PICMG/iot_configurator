//*******************************************************************
//    ConfigurationParameter.java
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

