package baccportal.controllers;

import java.io.IOException;

import baccportal.App;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;


public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox contentBox;

    private StudentUser student;

    @FXML
    private void initialize() {
        User user = App.getCurrentUser();

        if (user instanceof StudentUser) {
            student = (StudentUser) user;
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
    private void showTransactions() {
        loadStudentPage("studentTransactions");
    }



    private void loadStudentPage(String fxmlName) {
        try {
            Node page = FXMLLoader.load(
                    App.class.getResource("/baccportal/fxml/" + fxmlName + ".fxml")
            );

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
        App.setCurrentUser(null);
        App.setRoot("login");
    }
}
