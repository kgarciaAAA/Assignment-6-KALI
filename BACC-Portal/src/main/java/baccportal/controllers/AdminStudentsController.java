package baccportal.controllers;

import baccportal.model.academics.Department;
import baccportal.model.data.AppData;
import baccportal.model.services.AdminService;
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
    @FXML private TableColumn<StudentUser, Void> deleteColumn;

    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField majorField;
    @FXML private ComboBox<Department> departmentBox;
    @FXML private TextField balanceField;

    @FXML private Label statusLabel;

    private final AppData appData;
    private final AdminService adminService;

    public AdminStudentsController(AppData appData, AdminService adminService) {
        this.appData = appData;
        this.adminService = adminService;
    }

    @FXML
    private void initialize() {
        setupTable();
        setupDeleteColumn();
        setupDeleteColumnSize();
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

        balanceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.2f", value.doubleValue()));
                }
            }
        });

        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    StudentUser student = getTableView().getItems().get(getIndex());
                    deleteStudent(student);
                });

                deleteButton.setStyle(
                        "-fx-background-color: #dc2626;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6;" +
                                "-fx-padding: 5 12 5 12;"
                );
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void setupDeleteColumnSize() {
        deleteColumn.setPrefWidth(100);
        deleteColumn.setMinWidth(90);
        deleteColumn.setMaxWidth(120);
        deleteColumn.setResizable(false);
    }

    private void setupDepartmentBox() {
        departmentBox.getItems().setAll(
                Department.COMPUTER_SCIENCE,
                Department.DATA_SCIENCE,
                Department.MATHEMATICS
        );
    }

    private void loadStudents() {
        studentTable.getItems().clear();
        studentTable.getItems().addAll(appData.getUserStorage().getStudentsList());
    }

    @FXML
    private void handleRefresh() {
        appData.reloadUsers();
        loadStudents();
        statusLabel.setText("Students refreshed.");
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

            StudentUser student = new StudentUser(
                    email,
                    id,
                    password,
                    fullName,
                    false,
                    majorName,
                    department,
                    balance
            );

            boolean added = adminService.addNewStudent(student);

            if (!added) {
                statusLabel.setText("A user with this ID already exists.");
                return;
            }

            clearAddForm();
            loadStudents();
            statusLabel.setText("Student added successfully.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Balance must be a number.");
        }
    }

    private void deleteStudent(StudentUser student) {
        if (student == null) {
            statusLabel.setText("No student selected.");
            return;
        }

        if (!confirmAction(
                "Delete Student",
                "Are you sure you want to delete student " + student.getFullName()
                        + " (" + student.getUserId() + ")?"
        )) {
            return;
        }

        boolean removed = adminService.deleteStudent(student.getUserId());

        if (!removed) {
            statusLabel.setText("Student not found.");
            return;
        }


        loadStudents();
        statusLabel.setText("Student deleted successfully.");
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
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