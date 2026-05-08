package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.Course;
import baccportal.model.storage.CourseStorage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdminCoursesController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Number> unitsColumn;

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField unitsField;

    @FXML private TextField deleteCourseIdField;
    @FXML private Label statusLabel;

    private final CourseStorage courseStorage = App.getAppData().getCourseStorage();

    @FXML
    private void initialize() {
        setupTable();
        loadCourses();
    }

    private void setupTable() {
        courseIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourseId())
        );

        courseNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourseName())
        );

        unitsColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getUnitAmount())
        );

        courseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadCourses() {
        courseTable.getItems().setAll(courseStorage.getAllCourses().values());
    }

    @FXML
    private void handleAddCourse() {
        String courseId = courseIdField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String unitsText = unitsField.getText().trim();

        if (courseId.isBlank() || courseName.isBlank() || unitsText.isBlank()) {
            statusLabel.setText("Fill all course fields.");
            return;
        }

        try {
            double units = Double.parseDouble(unitsText);

            if (courseStorage.getCourse(courseId) != null) {
                statusLabel.setText("Course ID already exists.");
                return;
            }

            Course course = new Course(courseId, courseName, units);
            courseStorage.addCourse(course);

            App.getAppData().saveCourses();

            clearAddForm();
            loadCourses();
            statusLabel.setText("Course added successfully.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Units must be a number.");
        }
    }

    @FXML
    private void handleDeleteCourse() {
        String courseId = deleteCourseIdField.getText().trim();

        if (courseId.isBlank()) {
            statusLabel.setText("Enter a course ID to delete.");
            return;
        }

        boolean removed = courseStorage.removeCourseById(courseId);

        if (!removed) {
            statusLabel.setText("Course not found.");
            return;
        }

        App.getAppData().saveCourses();

        deleteCourseIdField.clear();
        loadCourses();
        statusLabel.setText("Course deleted successfully.");
    }

    private void clearAddForm() {
        courseIdField.clear();
        courseNameField.clear();
        unitsField.clear();
    }
}