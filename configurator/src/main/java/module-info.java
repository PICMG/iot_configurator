module configurator {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;

    opens org.picmg.configurator to javafx.fxml;
    exports org.picmg.configurator;
    exports org.picmg.jsonreader;
}