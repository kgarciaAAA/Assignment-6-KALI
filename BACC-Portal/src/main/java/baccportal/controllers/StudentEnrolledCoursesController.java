package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.CourseSection;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentEnrolledCoursesController {

    @FXML private TableView<CourseSection> enrolledTable;
    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;
    @FXML private Label statusLabel;

    private StudentUser student;

    @FXML
    private void initialize() {
        User user = App.getCurrentUser();

        if (user instanceof StudentUser) {
            student = (StudentUser) user;
            setupTable();
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

        priceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice())
        );

        enrolledTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadEnrolledCourses() {
        enrolledTable.getItems().setAll(student.getEnrolledSections());
    }
}
