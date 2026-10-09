module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires slf4j.api;
    requires sigar;
    requires jdk.management;

    opens gui to javafx.fxml;
    exports gui;
}