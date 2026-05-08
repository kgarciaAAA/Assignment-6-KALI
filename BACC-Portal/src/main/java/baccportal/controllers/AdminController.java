package baccportal.controllers;

import java.io.IOException;

import baccportal.model.academics.Department;
import baccportal.model.academics.Major;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import baccportal.App;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.PasswordField;

public class AdminController {

    @FXML private Label welcomeLabel;
    @FXML private VBox contentBox;

    private AdminUser admin;
    private final UserStorage userStorage = App.getAppData().getUserStorage();

    @FXML
    private void initialize() {
        User user = App.getCurrentUser();

        if (user instanceof AdminUser) {
            admin = (AdminUser) user;
            welcomeLabel.setText("Welcome, " + admin.getFullName());
            showOverview();
        } else {
            welcomeLabel.setText("Welcome");
            contentBox.getChildren().clear();

            Label errorLabel = new Label("No admin user is currently logged in.");
            errorLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");
            contentBox.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void showOverview() {
        if (admin == null) return;

        contentBox.getChildren().clear();

        Label heading = new Label("Admin Overview");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(admin.getFullName()), 1, 0);

        grid.add(new Label("Admin ID:"), 0, 1);
        grid.add(new Label(admin.getUserId()), 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(admin.getEmail()), 1, 2);

        grid.add(new Label("Total Students:"), 0, 3);
        grid.add(new Label(String.valueOf(userStorage.getStudentsList().size())), 1, 3);

        grid.add(new Label("Total Faculty:"), 0, 4);
        grid.add(new Label(String.valueOf(userStorage.getFacultyList().size())), 1, 4);

        grid.add(new Label("Total Admins:"), 0, 5);
        grid.add(new Label(String.valueOf(userStorage.getAdminList().size())), 1, 5);

        contentBox.getChildren().addAll(heading, grid);
    }

    @FXML
    private void showStudents() {
        contentBox.getChildren().clear();

        Label heading = new Label("Manage Students");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();

        for (StudentUser student : userStorage.getStudentsList()) {
            listView.getItems().add(
                    student.getUserId()
                            + " | " + student.getFullName()
                            + " | " + student.getEmail()
                            + " | Balance: $" + String.format("%.2f", student.getBalanceOwed())
            );
        }

        Label statusLabel = new Label();

        // Add Student Section
        Label formHeading = new Label("Add New Student");
        formHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField majorField = new TextField();
        majorField.setPromptText("Major Name");

        ComboBox<Department> departmentBox = new ComboBox<>();
        departmentBox.getItems().addAll(
                Department.COMPUTER_SCIENCE,
                Department.DATA_SCIENCE,
                Department.MATHEMATICS
        );
        departmentBox.setPromptText("Department");

        TextField balanceField = new TextField();
        balanceField.setPromptText("Balance Owed");

        Button addButton = new Button("Add Student");

        addButton.setOnAction(e -> {
            try {
                String email = emailField.getText().trim();
                String id = idField.getText().trim();
                String password = passwordField.getText();
                String fullName = nameField.getText().trim();
                String majorName = majorField.getText().trim();
                Department department = departmentBox.getValue();

                if (email.isBlank() || id.isBlank() || password.isBlank()
                        || fullName.isBlank() || majorName.isBlank() || department == null
                        || balanceField.getText().trim().isBlank()) {
                    statusLabel.setText("Fill all student fields.");
                    return;
                }

                double balance = Double.parseDouble(balanceField.getText().trim());

                if (userStorage.findUserById(id) != null) {
                    statusLabel.setText("A user with this ID already exists.");
                    return;
                }

                Major major = new Major(majorName, department);

                StudentUser newStudent = new StudentUser(
                        email,
                        id,
                        password,
                        fullName,
                        false,
                        major,
                        balance
                );

                userStorage.addStudentUser(newStudent);
                App.getAppData().saveUsers();

                statusLabel.setText("Student added successfully.");
                showStudents();

            } catch (NumberFormatException ex) {
                statusLabel.setText("Balance must be a number.");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Email:"), 0, 0);
        form.add(emailField, 1, 0);

        form.add(new Label("Student ID:"), 0, 1);
        form.add(idField, 1, 1);

        form.add(new Label("Password:"), 0, 2);
        form.add(passwordField, 1, 2);

        form.add(new Label("Full Name:"), 0, 3);
        form.add(nameField, 1, 3);

        form.add(new Label("Major:"), 0, 4);
        form.add(majorField, 1, 4);

        form.add(new Label("Department:"), 0, 5);
        form.add(departmentBox, 1, 5);

        form.add(new Label("Balance:"), 0, 6);
        form.add(balanceField, 1, 6);

        form.add(addButton, 1, 7);

        // Delete Student Section
        Label deleteHeading = new Label("Delete Student");
        deleteHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField deleteIdField = new TextField();
        deleteIdField.setPromptText("Student ID to delete");

        Button deleteButton = new Button("Delete Student");

        deleteButton.setOnAction(e -> {
            String id = deleteIdField.getText().trim();

            if (id.isBlank()) {
                statusLabel.setText("Enter a student ID to delete.");
                return;
            }

            boolean removed = userStorage.removeStudentById(id);

            if (removed) {
                App.getAppData().saveUsers();
                statusLabel.setText("Student deleted successfully.");
                showStudents();
            } else {
                statusLabel.setText("Student not found.");
            }
        });

        GridPane deleteForm = new GridPane();
        deleteForm.setHgap(10);
        deleteForm.setVgap(10);

        deleteForm.add(new Label("Student ID:"), 0, 0);
        deleteForm.add(deleteIdField, 1, 0);
        deleteForm.add(deleteButton, 1, 1);

        contentBox.getChildren().addAll(
                heading,
                listView,
                formHeading,
                form,
                deleteHeading,
                deleteForm,
                statusLabel
        );
    }


    @FXML
    private void showFaculty() {
        contentBox.getChildren().clear();

        Label heading = new Label("Manage Faculty");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();

        for (FacultyUser faculty : userStorage.getFacultyList()) {
            listView.getItems().add(
                    faculty.getUserId()
                            + " | " + faculty.getFullName()
                            + " | " + faculty.getEmail()
            );
        }

        contentBox.getChildren().addAll(heading, listView);
    }
    @FXML
    private void showChangePassword() {
        contentBox.getChildren().clear();

        Label heading = new Label("Change Password");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        Label statusLabel = new Label();

        Button saveButton = new Button("Update Password");

        saveButton.setOnAction(e -> {
            User user = App.getCurrentUser();

            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                statusLabel.setText("Fill all password fields.");
                return;
            }

            if (!user.comparePassword(currentPassword)) {
                statusLabel.setText("Current password is incorrect.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                statusLabel.setText("New passwords do not match.");
                return;
            }

            user.setPassword(newPassword);
            App.getAppData().saveUsers();

            statusLabel.setText("Password updated successfully.");
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Current Password:"), 0, 0);
        form.add(currentPasswordField, 1, 0);

        form.add(new Label("New Password:"), 0, 1);
        form.add(newPasswordField, 1, 1);

        form.add(new Label("Confirm Password:"), 0, 2);
        form.add(confirmPasswordField, 1, 2);

        form.add(saveButton, 1, 3);

        contentBox.getChildren().addAll(heading, form, statusLabel);
    }

    @FXML
    private void showCourses() {
        contentBox.getChildren().clear();

        Label heading = new Label("Manage Courses");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label message = new Label("Course management will connect to CourseStorage next.");

        contentBox.getChildren().addAll(heading, message);
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setCurrentUser(null);
        App.setRoot("login");
    }
}
