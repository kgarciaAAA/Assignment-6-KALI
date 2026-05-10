package baccportal.controllers;

import baccportal.App;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import baccportal.model.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;
    @FXML private Label statusLabel;

    private boolean passwordVisible = false;
    private final AuthService authService = App.getAppData().getAuthService();
    
    @FXML
    private void initialize() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        visiblePasswordField.setVisible(passwordVisible);
        visiblePasswordField.setManaged(passwordVisible);

        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);

        togglePasswordButton.setText(passwordVisible ? "Hide" : "Show");
    }

    @FXML
    private void handleLogin() {

        String id = userIdField.getText().trim();
        String password = passwordField.getText();

        if (id.isBlank() || password.isBlank()) {
            statusLabel.setText("Fill all fields.");
            return;
        }

        User user = authService.login(id, password);

        if (user == null) {
            statusLabel.setText("Invalid login.");
            return;
        }

        try {
            App.setCurrentUser(user);
            // TODO: Instanceof checks. We should perhaps consider a more flexible approach.
            if (user instanceof StudentUser) {
                App.setRoot("studentDashboard");
            } else if (user instanceof FacultyUser) {
                App.setRoot("faculty");
            } else if (user instanceof AdminUser) {
                App.setRoot("admin");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading screen.");
        }
    }
}