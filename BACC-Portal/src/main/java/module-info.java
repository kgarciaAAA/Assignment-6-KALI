module baccportal {
    requires javafx.controls;
    requires javafx.fxml;

    opens baccportal to javafx.fxml;
    opens baccportal.controllers to javafx.fxml;
    exports baccportal;
}
