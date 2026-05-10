package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.CourseSection;
import baccportal.model.services.RegistrationService;
import baccportal.model.users.StudentUser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class StudentEnrolledCoursesController {

    @FXML private TableView<CourseSection> enrolledTable;

    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, Number> unitsColumn;
    @FXML private TableColumn<CourseSection, String> instructorColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;
    @FXML private TableColumn<CourseSection, Void> dropColumn;

    @FXML private Label statusLabel;

    private StudentUser student;
    private final RegistrationService registrationService = App.getAppData().getRegistrationService();

    @FXML
    private void initialize() {
        var opt = App.getSession().student();

        if (opt.isPresent()) {
            student = opt.get();
            setupTable();
            setupDropColumn();
            loadEnrolledCourses();
        } else {
            statusLabel.setText("No student user is currently logged in.");
        }
    }

    private void setupTable() {
        sectionIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSectionId())
        );

        courseIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourse().getCourseId())
        );

        courseNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourse().getCourseName())
        );

        unitsColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCourse().getUnitAmount())
        );

        instructorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInstructorName())
        );

        priceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice())
        );

        priceColumn.setCellFactory(column -> new TableCell<>() {
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

        enrolledTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupDropColumn() {
        dropColumn.setPrefWidth(90);
        dropColumn.setMinWidth(80);
        dropColumn.setMaxWidth(110);
        dropColumn.setResizable(false);

        dropColumn.setCellFactory(column -> new TableCell<>() {
            private final Button dropButton = new Button("Drop");

            {
                dropButton.setOnAction(e -> {
                    CourseSection section = getTableView().getItems().get(getIndex());
                    dropSection(section);
                });

                dropButton.setStyle(
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
                    setGraphic(dropButton);
                }
            }
        });
    }

    private void loadEnrolledCourses() {
        enrolledTable.getItems().clear();
        enrolledTable.getItems().addAll(student.getEnrolledSections());

        if (student.getEnrolledSections().isEmpty()) {
            statusLabel.setText("No enrolled courses.");
        } else {
            statusLabel.setText("");
        }
    }

    @FXML
    private void handleRefresh() {
        if (student == null) {
            statusLabel.setText("No student user is currently logged in.");
            return;
        }

        String studentId = student.getUserId();

        App.getAppData().reloadUsers();

        StudentUser refreshed = App.getAppData().getUserStorage().findStudentUserById(studentId);

        if (refreshed != null) {
            student = refreshed;
            App.getSession().setUser(refreshed);
            loadEnrolledCourses();
            statusLabel.setText("Enrolled courses refreshed.");
        } else {
            statusLabel.setText("Could not refresh student data.");
        }
    }

    private void dropSection(CourseSection section) {
        if (student == null) {
            statusLabel.setText("No student logged in.");
            return;
        }

        if (section == null) {
            statusLabel.setText("No section selected.");
            return;
        }

        if (!confirmAction(
                "Drop Course",
                "Are you sure you want to drop " + section.getCourse().getCourseName()
                        + " (" + section.getSectionId() + ")?"
        )) {
            return;
        }

        boolean dropped = registrationService.drop(student, section);

        if (!dropped) {
            statusLabel.setText("Could not drop section.");
            return;
        }

        loadEnrolledCourses();

        statusLabel.setText(
                "Dropped successfully. New balance: $"
                        + String.format("%.2f", student.getBalanceOwed())
        );
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
}
