module configurator {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;

    opens org.picmg.configurator to javafx.fxml;
    exports org.picmg.configurator;
    exports org.picmg.jsonreader;
    exports org.picmg.test.unitTest;
    opens org.picmg.test.unitTest to javafx.fxml;
    exports org.picmg.test.integrationTest;
    opens org.picmg.test.integrationTest to javafx.fxml;
}