package baccportal.controllers;

import baccportal.model.academics.Department;
import baccportal.model.services.AdminService;
import baccportal.model.users.FacultyUser;
import baccportal.model.storage.UserStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminFacultyController {

    @FXML private TableView<FacultyUser> facultyTable;
    @FXML private TableColumn<FacultyUser, String> idColumn;
    @FXML private TableColumn<FacultyUser, String> nameColumn;
    @FXML private TableColumn<FacultyUser, String> emailColumn;
    @FXML private TableColumn<FacultyUser, String> departmentColumn;
    @FXML private TableColumn<FacultyUser, Void> deleteColumn;

    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private ComboBox<Department> departmentBox;

    @FXML private Label statusLabel;

    private final AdminService adminService;
    private final UserStorage userStorage;

    public AdminFacultyController(AdminService adminService, UserStorage userStorage) {
        this.adminService = adminService;
        this.userStorage = userStorage;
    }

    @FXML
    private void initialize() {
        setupTable();
        setupDeleteColumn();
        setupDepartmentBox();
        loadFaculty();
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

        departmentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDepartment() == null
                                ? "None"
                                : data.getValue().getDepartment().toString()
                )
        );

        facultyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    FacultyUser faculty = getTableView().getItems().get(getIndex());
                    deleteFaculty(faculty);
                });

                deleteButton.setStyle(
                        "-fx-background-color: #dc2626;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6;"
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

    private void setupDepartmentBox() {
        departmentBox.getItems().setAll(
                Department.COMPUTER_SCIENCE,
                Department.DATA_SCIENCE,
                Department.MATHEMATICS
        );
    }

    private void loadFaculty() {
        facultyTable.getItems().clear();
        facultyTable.getItems().addAll(userStorage.getFacultyList());
    }

    @FXML
    private void handleAddFaculty() {
        String email = emailField.getText().trim();
        String id = idField.getText().trim();
        String password = passwordField.getText();
        String fullName = nameField.getText().trim();
        Department department = departmentBox.getValue();

        if (email.isBlank() || id.isBlank() || password.isBlank()
                || fullName.isBlank() || department == null) {
            statusLabel.setText("Fill all faculty fields.");
            return;
        }

        FacultyUser faculty = new FacultyUser(
                email,
                id,
                password,
                fullName,
                false,
                department
        );

        boolean added = adminService.addNewFaculty(faculty);

        if (!added) {
            statusLabel.setText("A user with this ID already exists.");
            return;
        }


        clearAddForm();
        loadFaculty();
        statusLabel.setText("Faculty added successfully.");
    }

    private void deleteFaculty(FacultyUser faculty) {
        if (faculty == null) {
            statusLabel.setText("No faculty selected.");
            return;
        }

        if (!confirmAction(
                "Delete Faculty",
                "Are you sure you want to delete faculty " + faculty.getFullName()
                        + " (" + faculty.getUserId() + ")?"
        )) {
            return;
        }

        boolean removed = adminService.deleteFaculty(faculty.getUserId());

        if (!removed) {
            statusLabel.setText("Faculty not found.");
            return;
        }

        loadFaculty();
        statusLabel.setText("Faculty deleted successfully.");
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
        departmentBox.setValue(null);
    }
}