package baccportal.controllers;

import java.io.IOException;

import baccportal.App;
import baccportal.model.services.AuthService;
import baccportal.model.session.Session;
import baccportal.model.users.StudentUser;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox contentBox;

    private StudentUser student;
    private final Session session;
    private final AuthService authService;

    public StudentDashboardController(Session session, AuthService authService) {
        this.session = session;
        this.authService = authService;
    }

    @FXML
    private void initialize() {
        student = session.student();

        if (student != null) {
            welcomeLabel.setText("Welcome, " + student.getFullName());
            showOverview();
        } else {
            welcomeLabel.setText("Welcome");
            contentBox.getChildren().clear();

            Label errorLabel = new Label("No student user is currently logged in.");
            errorLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");
            contentBox.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void showOverview() {
        if (student == null) {
            return;
        }

        contentBox.getChildren().clear();

        Label heading = new Label("Student Overview");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(student.getFullName()), 1, 0);

        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(new Label(student.getUserId()), 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(student.getEmail()), 1, 2);

        grid.add(new Label("Major:"), 0, 3);
        grid.add(new Label(student.getMajor() == null ? "Undeclared" : student.getMajor().toString()), 1, 3);

        grid.add(new Label("Balance Owed:"), 0, 4);
        grid.add(new Label("$" + String.format("%.2f", student.getBalanceOwed())), 1, 4);

        contentBox.getChildren().addAll(heading, grid);
    }

    @FXML
    private void showManageCourses() {
        loadStudentPage("studentManageCourses");
    }

    @FXML
    private void showEnrolledCourses() {
        loadStudentPage("studentEnrolledCourses");
    }

    @FXML
    private void showCompletedCourses() {
        loadStudentPage("studentCompletedCourses");
    }

    @FXML
    private void showPayment() {loadStudentPage("studentPayment");}

    @FXML
    private void showTransactions() {
        loadStudentPage("studentTransactions");
    }

    @FXML
    private void showChangePassword() {
        loadStudentPage("changePassword");
    }



    private void loadStudentPage(String fxmlName) {
        try {
            Node page = App.loadFXML(fxmlName);

            contentBox.getChildren().clear();
            contentBox.getChildren().add(page);

        } catch (IOException e) {
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
