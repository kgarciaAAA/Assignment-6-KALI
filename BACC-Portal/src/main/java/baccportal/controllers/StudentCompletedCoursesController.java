package baccportal.controllers;

import baccportal.model.academics.CourseSection;
import baccportal.model.data.AppData;
import baccportal.model.session.Session;
import baccportal.model.users.StudentUser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;

public class StudentCompletedCoursesController {

    @FXML private TableView<CourseSection> completedTable;
    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, Number> unitsColumn;
    @FXML private TableColumn<CourseSection, String> instructorColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;
    @FXML private Label statusLabel;

    private StudentUser student;
    private final Session session;
    private final AppData appData;

    public StudentCompletedCoursesController(Session session, AppData appData) {
        this.session = session;
        this.appData = appData;
    }

    @FXML
    private void initialize() {
        student = session.student();

        if (student != null) {
            setupTable();
            loadCompletedCourses();
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

        completedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadCompletedCourses() {
        completedTable.getItems().clear();
        completedTable.getItems().addAll(student.getCompletedSections());

        if (student.getCompletedSections().isEmpty()) {
            statusLabel.setText("No completed courses.");
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

        appData.reloadUsers();

        StudentUser refreshed = appData.getUserStorage().findStudentUserById(studentId);

        if (refreshed != null) {
            student = refreshed;
            session.setUser(refreshed);
            loadCompletedCourses();
            statusLabel.setText("Completed courses refreshed.");
        } else {
            statusLabel.setText("Could not refresh student data.");
        }
    }
}