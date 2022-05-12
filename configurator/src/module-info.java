module configurator {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;

    opens org.picmg.configurator to javafx.fxml;
    exports org.picmg.configurator;
    opens org.picmg.test.unitTest to junit;
    exports org.picmg.test.unitTest;
    exports org.picmg.jsonreader;
}