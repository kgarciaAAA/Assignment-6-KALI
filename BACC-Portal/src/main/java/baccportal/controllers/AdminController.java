package baccportal.controllers;

import java.io.IOException;

import baccportal.App;
import baccportal.model.services.AuthService;
import baccportal.model.session.Session;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AdminController {

    @FXML private Label welcomeLabel;
    @FXML private VBox contentBox;

    private AdminUser admin;
    private final Session session;
    private final AuthService authService;
    private final UserStorage userStorage;

    public AdminController(Session session, AuthService authService, UserStorage userStorage) {
        this.session = session;
        this.authService = authService;
        this.userStorage = userStorage;
    }

    @FXML
    private void initialize() {
        admin = session.admin();

        if (admin != null) {
            welcomeLabel.setText("Welcome, " + admin.getFullName());
            showOverview();
        } else {
            welcomeLabel.setText("Welcome");
            showError("No admin user is currently logged in.");
        }
    }

    @FXML
    private void showOverview() {
        if (admin == null) return;

        contentBox.getChildren().clear();

        Label heading = createHeading("Admin Overview");

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
        loadAdminPage("adminStudents");
    }

    @FXML
    private void showFaculty() {
        loadAdminPage("adminFaculty");
    }

    @FXML
    private void showSections() {
        loadAdminPage("adminSections");
    }

    @FXML
    private void showChangePassword() {
        loadAdminPage("changePassword");
    }

    @FXML
    private void showCourses() {
        loadAdminPage("adminCourses");
    }



    private Label createHeading(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        return label;
    }

    private void showError(String message) {
        contentBox.getChildren().clear();

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");

        contentBox.getChildren().add(errorLabel);
    }

    private void loadAdminPage(String fxmlName) {
        try {
            Node page = App.loadFXML(fxmlName);

            contentBox.getChildren().clear();
            contentBox.getChildren().add(page);

        } catch (IOException e) {
            e.printStackTrace();
            contentBox.getChildren().clear();
            contentBox.getChildren().add(new Label("Error loading page: " + fxmlName));
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        authService.logout();
        App.setRoot("login");
    }
}
