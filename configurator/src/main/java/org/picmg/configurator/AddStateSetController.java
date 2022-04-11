package org.picmg.configurator;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddStateSetController implements Initializable {

    @FXML
    private TextField vendorTextfield;
    @FXML private TextField stateSetTextField;
    @FXML private Button saveChangesButton;

    boolean modified;
    boolean valid;
    AddStateSetController.AddStateSetTableData workingData = new AddStateSetController.AddStateSetTableData();

    public AddStateSetController.AddStateSetTableData getSensorTableData() {
        return workingData;
    }

    /**
     * This inner class describes the data model for the sensor table
     * and sensor data pane
     */
    public class AddStateSetTableData {
        SimpleStringProperty vendor = new SimpleStringProperty();
        SimpleStringProperty stateSet = new SimpleStringProperty();

        public String getVendor() {
            return vendor.get();
        }

        public SimpleStringProperty vendorProperty() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor.set(vendor);
        }

        public String getStateSet() {
            return stateSet.get();
        }

        public SimpleStringProperty stateSetProperty() {
            return stateSet;
        }

        public void setStateSet(String stateSet) {
            this.stateSet.set(stateSet);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void onAddStateSetAction(ActionEvent event) {
        if (vendorTextfield.getText().isBlank()) vendorTextfield.setVisible(true);
        else vendorTextfield.setVisible(false);
        workingData.setVendor(vendorTextfield.getText());
        if (stateSetTextField.getText().isBlank()) stateSetTextField.setVisible(true);
        else stateSetTextField.setVisible(false);
        modified = true;
        //todo: updateVendorAndStateSet();
        saveChangesButton.setDisable(!isValid());
    }

    public boolean isValid() {
        if(vendorTextfield.isVisible()) return  false;
        if(stateSetTextField.isVisible()) return  false;
        return true;
    }


}
