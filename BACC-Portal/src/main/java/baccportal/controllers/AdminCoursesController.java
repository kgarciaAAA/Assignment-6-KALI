package baccportal.controllers;

import baccportal.model.academics.Course;
import baccportal.model.services.AdminService;
import baccportal.model.storage.CourseStorage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdminCoursesController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Number> unitsColumn;
    @FXML private TableColumn<Course, Void> deleteColumn;

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField unitsField;

    @FXML private Label statusLabel;

    private final CourseStorage courseStorage;
    private final AdminService adminService;

    public AdminCoursesController(CourseStorage courseStorage, AdminService adminService) {
        this.courseStorage = courseStorage;
        this.adminService = adminService;
    }

    @FXML
    private void initialize() {
        setupTable();
        setupDeleteColumn();
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

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    Course course = getTableView().getItems().get(getIndex());
                    deleteCourse(course);
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

    private void loadCourses() {
        courseTable.getItems().clear();
        courseTable.getItems().addAll(courseStorage.getAllCourses().values());
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

            Course course = new Course(courseId, courseName, units);
            boolean added = adminService.addCourse(course);

            if (!added) {
                statusLabel.setText("Course ID already exists.");
                return;
            }

            clearAddForm();
            loadCourses();
            statusLabel.setText("Course added successfully.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Units must be a number.");
        }
    }

    private void deleteCourse(Course course) {
        if (course == null) {
            statusLabel.setText("No course selected.");
            return;
        }

        if (!confirmAction(
                "Delete Course",
                "Are you sure you want to delete course " + course.getCourseId()
                        + " and all related sections?"
        )) {
            return;
        }

        boolean removed = adminService.deleteCourse(course.getCourseId());

        if (!removed) {
            statusLabel.setText("Course not found.");
            return;
        }


        loadCourses();
        statusLabel.setText("Course and related sections deleted successfully.");
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
        courseIdField.clear();
        courseNameField.clear();
        unitsField.clear();
    }
}