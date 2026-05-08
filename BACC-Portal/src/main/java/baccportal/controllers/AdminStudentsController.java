package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.Department;
import baccportal.model.academics.Major;
import baccportal.model.services.AdminService;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.StudentUser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminStudentsController {

    @FXML private TableView<StudentUser> studentTable;
    @FXML private TableColumn<StudentUser, String> idColumn;
    @FXML private TableColumn<StudentUser, String> nameColumn;
    @FXML private TableColumn<StudentUser, String> emailColumn;
    @FXML private TableColumn<StudentUser, Number> balanceColumn;

    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField majorField;
    @FXML private ComboBox<Department> departmentBox;
    @FXML private TextField balanceField;

    @FXML private TextField deleteIdField;
    @FXML private Label statusLabel;

    private final UserStorage userStorage = App.getAppData().getUserStorage();
    private final AdminService adminService = new AdminService(userStorage);

    @FXML
    private void initialize() {
        setupTable();
        setupDepartmentBox();
        loadStudents();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUserId())
        );

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName())
        );

        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );

        balanceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getBalanceOwed())
        );

        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupDepartmentBox() {
        departmentBox.getItems().setAll(
                Department.COMPUTER_SCIENCE,
                Department.DATA_SCIENCE,
                Department.MATHEMATICS
        );
    }

    private void loadStudents() {
        studentTable.getItems().setAll(userStorage.getStudentsList());
    }

    @FXML
    private void handleAddStudent() {
        try {
            String email = emailField.getText().trim();
            String id = idField.getText().trim();
            String password = passwordField.getText();
            String fullName = nameField.getText().trim();
            String majorName = majorField.getText().trim();
            Department department = departmentBox.getValue();
            String balanceText = balanceField.getText().trim();

            if (email.isBlank() || id.isBlank() || password.isBlank()
                    || fullName.isBlank() || majorName.isBlank()
                    || department == null || balanceText.isBlank()) {
                statusLabel.setText("Fill all student fields.");
                return;
            }

            double balance = Double.parseDouble(balanceText);
            Major major = new Major(majorName, department);

            StudentUser student = new StudentUser(
                    email,
                    id,
                    password,
                    fullName,
                    false,
                    major,
                    balance
            );

            boolean added = adminService.addNewStudent(student);

            if (!added) {
                statusLabel.setText("A user with this ID already exists.");
                return;
            }

            App.getAppData().saveUsers();
            clearAddForm();
            loadStudents();
            statusLabel.setText("Student added successfully.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Balance must be a number.");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        String id = deleteIdField.getText().trim();

        if (id.isBlank()) {
            statusLabel.setText("Enter a student ID to delete.");
            return;
        }

        boolean removed = adminService.deleteStudent(id);

        if (!removed) {
            statusLabel.setText("Student not found.");
            return;
        }

        App.getAppData().saveUsers();
        deleteIdField.clear();
        loadStudents();
        statusLabel.setText("Student deleted successfully.");
    }

    private void clearAddForm() {
        emailField.clear();
        idField.clear();
        passwordField.clear();
        nameField.clear();
        majorField.clear();
        departmentBox.setValue(null);
        balanceField.clear();
    }
}