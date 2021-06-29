package configurator;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import jsonreader.JsonObject;

import java.net.URL;
import java.util.*;

public class FruPaneController implements Initializable {
	// TODO Auto-generated method stub
	private JsonObject jsonFru;
	private JsonObject jsonCapabilitiesFru;

	private final int DMTF_IANA = 412;

	@FXML private ChoiceBox choiceBoxFruType;
	@FXML private TextField textFieldOemId;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/* updateControls()
	 * update the field controls based on the current configuration
	 * of the Json representation.
	 */
	private void updateControls()
	{
		// create the choices for the type
		choiceBoxFruType.getItems().removeAll();
		choiceBoxFruType.getItems().add("not selected");
		choiceBoxFruType.getItems().add("standard");
		choiceBoxFruType.getItems().add("oem");

		// select the current choice
		switch (jsonFru.getInteger("vendorIANA")) {
			case 0: // unspecified
				choiceBoxFruType.getSelectionModel().select(0);
				break;
			case DMTF_IANA: // standard
				choiceBoxFruType.getSelectionModel().select(1);
				break;
			default: // oem fru record
				choiceBoxFruType.getSelectionModel().select(2);
				break;
		}

		// if this field is non-null in the capabilities section, disable
		// this control in order to prevent changes.
		choiceBoxFruType.setDisable(false);
		if (!jsonCapabilitiesFru.getValue("vendorIANA").equals("null")) {
			choiceBoxFruType.setDisable(true);
		}

		// update the vendor id field
		textFieldOemId.setDisable(false);
		if (!jsonFru.getValue("vendorIANA").equals("null")) {
			textFieldOemId.setText(jsonFru.getValue("vendorIANA"));

			// if the capabilities value for this field has been set or the
			// fru record type is standard, disable this field
			if ((jsonFru.getInteger("vendorIANA") == DMTF_IANA) ||
					(!jsonCapabilitiesFru.getValue("vendorIANA").equals("null"))) {
				textFieldOemId.setDisable(true);
			}
		} else {
			textFieldOemId.setText("");
		}

		// update the tableview
	}

	public void update(JsonObject fru, JsonObject capabilitiesFru)
	{
		// do any configuration required prior to making the pane visible
		jsonFru = fru;
		jsonCapabilitiesFru = capabilitiesFru;

		updateControls();
	}

}