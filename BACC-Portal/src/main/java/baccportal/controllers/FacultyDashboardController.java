package baccportal.controllers;

import java.io.IOException;

import baccportal.App;
import baccportal.model.services.AuthService;
import baccportal.model.session.Session;
import baccportal.model.users.FacultyUser;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class FacultyDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private VBox contentBox;

    private FacultyUser faculty;
    private final Session session;
    private final AuthService authService;

    public FacultyDashboardController(Session session, AuthService authService) {
        this.session = session;
        this.authService = authService;
    }

    @FXML
    private void initialize() {
        faculty = session.faculty();

        if (faculty != null) {
            welcomeLabel.setText("Welcome, " + faculty.getFullName());
            showOverview();
        } else {
            welcomeLabel.setText("Welcome");
            showError("No faculty user is currently logged in.");
        }
    }

    @FXML
    private void showOverview() {
        if (faculty == null) {
            return;
        }

        contentBox.getChildren().clear();

        Label heading = new Label("Faculty Overview");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(faculty.getFullName()), 1, 0);

        grid.add(new Label("Faculty ID:"), 0, 1);
        grid.add(new Label(faculty.getUserId()), 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(faculty.getEmail()), 1, 2);

        grid.add(new Label("Department:"), 0, 3);
        grid.add(new Label(faculty.getDepartment() == null ? "None" : faculty.getDepartment().toString()), 1, 3);

        grid.add(new Label("Sections Teaching:"), 0, 4);
        grid.add(new Label(String.valueOf(faculty.getSectionsTaught().size())), 1, 4);

        contentBox.getChildren().addAll(heading, grid);
    }

    @FXML
    private void showMySections() {
        loadFacultyPage("facultySections");
    }

    @FXML
    private void showChangePassword() {
        loadFacultyPage("changePassword");
    }

    private void loadFacultyPage(String fxmlName) {
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

    private void showError(String message) {
        contentBox.getChildren().clear();

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");

        contentBox.getChildren().add(errorLabel);
    }

    @FXML
    private void handleLogout() throws IOException {
        authService.logout();
        App.setRoot("login");
    }
}
