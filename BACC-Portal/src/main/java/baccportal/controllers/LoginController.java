package baccportal.controllers;

import baccportal.App;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
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

    private final UserStorage userStorage = App.getAppData().getUserStorage();

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

        User user = userStorage.findUserById(id);

        System.out.println("Entered ID: " + id);
        System.out.println("Entered password: " + password);
        System.out.println("User found: " + user);
        System.out.println("Students loaded: " + userStorage.getStudentsList().size());
        System.out.println("Faculty loaded: " + userStorage.getFacultyList().size());
        System.out.println("Admins loaded: " + userStorage.getAdminList().size());

        if (user != null) {
            System.out.println("Stored user ID: " + user.getUserId());
            System.out.println("Stored full name: " + user.getFullName());
            System.out.println("Password matches: " + user.comparePassword(password));
        }

        if (user == null || !user.comparePassword(password)) {
            statusLabel.setText("Invalid login.");
            return;
        }

        try {
            App.setCurrentUser(user);

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