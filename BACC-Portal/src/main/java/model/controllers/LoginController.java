package model.controllers;

import app.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import storage.UserStorage;
import users.*;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserStorage userStorage = new UserStorage();

    @FXML
    public void initialize() {
        // TEMP: add test users so login works
        userStorage.addStudentUser(new StudentUser(
                "s@test.com", "123", "pass", "Student User", null, 0));

        userStorage.addFacultyUser(new FacultyUser(
                "f@test.com", "456", "pass", "Faculty User", null));

        userStorage.addAdminUser(new AdminUser(
                "a@test.com", "789", "pass", "Admin User"));
    }

    @FXML
    private void handleLogin() {
        String id = userIdField.getText();
        String password = passwordField.getText();

        if (id.isBlank() || password.isBlank()) {
            statusLabel.setText("Fill all fields.");
            return;
        }

        User user = findUser(id);

        if (user == null || !user.comparePassword(password)) {
            statusLabel.setText("Invalid login.");
            return;
        }

        try {
            if (user instanceof StudentUser) {
                App.setRoot("student");
            } else if (user instanceof FacultyUser) {
                App.setRoot("faculty");
            } else if (user instanceof AdminUser) {
                App.setRoot("admin");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading screen.");
        }
    }

    private User findUser(String id) {
        for (StudentUser s : userStorage.getStudentsList()) {
            if (s.getUserId().equalsIgnoreCase(id)) return s;
        }

        for (FacultyUser f : userStorage.getFacultyList()) {
            if (f.getUserId().equalsIgnoreCase(id)) return f;
        }

        for (AdminUser a : userStorage.getAdminList()) {
            if (a.getUserId().equalsIgnoreCase(id)) return a;
        }

        return null;
    }
}
