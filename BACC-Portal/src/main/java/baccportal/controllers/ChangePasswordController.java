package baccportal.controllers;

import baccportal.App;
import baccportal.model.users.User;
import baccportal.model.services.PasswordService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField;
    @FXML private TextField currentPasswordTextField;
    @FXML private Button currentToggleButton;

    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordTextField;
    @FXML private Button newToggleButton;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private Button confirmToggleButton;

    @FXML private Label statusLabel;

    private final PasswordService passwordService = App.getAppData().getPasswordService();

    @FXML
    private void initialize() {
        currentPasswordTextField.textProperty().bindBidirectional(currentPasswordField.textProperty());
        newPasswordTextField.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    private void toggleCurrentPassword() {
        togglePasswordField(currentPasswordField, currentPasswordTextField, currentToggleButton);
    }

    @FXML
    private void toggleNewPassword() {
        togglePasswordField(newPasswordField, newPasswordTextField, newToggleButton);
    }

    @FXML
    private void toggleConfirmPassword() {
        togglePasswordField(confirmPasswordField, confirmPasswordTextField, confirmToggleButton);
    }

    @FXML
    private void handleChangePassword() {
        User user = App.getCurrentUser();

        if (user == null) {
            statusLabel.setText("No user is currently logged in.");
            return;
        }

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            statusLabel.setText("Fill all password fields.");
            return;
        }

        if (!passwordService.verifyPassword(user, currentPassword)) {
            statusLabel.setText("Current password is incorrect.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("New passwords do not match.");
            return;
        }

        passwordService.setPassword(user, newPassword);
        

        clearFields();
        statusLabel.setText("Password updated successfully.");
    }

    private void togglePasswordField(PasswordField hiddenField, TextField visibleField, Button toggleButton) {
        boolean showing = visibleField.isVisible();

        visibleField.setVisible(!showing);
        visibleField.setManaged(!showing);

        hiddenField.setVisible(showing);
        hiddenField.setManaged(showing);

        toggleButton.setText(showing ? "Show" : "Hide");
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}
