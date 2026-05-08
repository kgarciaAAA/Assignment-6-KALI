package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.storage.CourseStorage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminSectionsController {

    @FXML private TableView<CourseSection> sectionsTable;
    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, String> instructorColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;
    @FXML private TableColumn<CourseSection, String> capacityColumn;

    @FXML private TextField courseIdField;
    @FXML private TextField instructorField;
    @FXML private TextField sectionIdField;
    @FXML private TextField accessCodeField;
    @FXML private TextField priceField;
    @FXML private TextField totalCapacityField;
    @FXML private TextField currentCapacityField;

    @FXML private TextField deleteSectionIdField;
    @FXML private Label statusLabel;
    @FXML private TableColumn<CourseSection, String> accessCodeColumn;

    private final CourseStorage courseStorage = App.getAppData().getCourseStorage();

    @FXML
    private void initialize() {
        setupTable();
        loadSections();
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

        instructorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInstructorName())
        );

        accessCodeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAccessCode())
        );

        priceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice())
        );

        capacityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCurrentCapacity()
                                + "/"
                                + data.getValue().getTotalCapacity()
                )
        );

        sectionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadSections() {
        sectionsTable.getItems().setAll(courseStorage.getAllSections().values());
    }

    @FXML
    private void handleAddSection() {
        String courseId = courseIdField.getText().trim();
        String instructor = instructorField.getText().trim();
        String sectionId = sectionIdField.getText().trim();
        String accessCode = accessCodeField.getText().trim();
        String priceText = priceField.getText().trim();
        String totalCapacityText = totalCapacityField.getText().trim();
        String currentCapacityText = currentCapacityField.getText().trim();

        if (courseId.isBlank() || instructor.isBlank() || sectionId.isBlank()
                || accessCode.isBlank() || priceText.isBlank()
                || totalCapacityText.isBlank() || currentCapacityText.isBlank()) {
            statusLabel.setText("Fill all section fields.");
            return;
        }

        Course course = courseStorage.getCourse(courseId);

        if (course == null) {
            statusLabel.setText("Course ID does not exist.");
            return;
        }

        if (courseStorage.getSection(sectionId) != null) {
            statusLabel.setText("Section ID already exists.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int totalCapacity = Integer.parseInt(totalCapacityText);
            int currentCapacity = Integer.parseInt(currentCapacityText);

            if (price < 0 || totalCapacity < 0 || currentCapacity < 0) {
                statusLabel.setText("Price and capacity cannot be negative.");
                return;
            }

            if (currentCapacity > totalCapacity) {
                statusLabel.setText("Current capacity cannot be greater than total capacity.");
                return;
            }

            CourseSection section = new CourseSection(
                    course,
                    instructor,
                    sectionId,
                    accessCode,
                    price,
                    totalCapacity,
                    currentCapacity
            );

            courseStorage.addSection(section);
            App.getAppData().saveSections();

            clearAddForm();
            loadSections();
            statusLabel.setText("Section added successfully.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Price must be a number. Capacity must be whole numbers.");
        }
    }

    @FXML
    private void handleDeleteSection() {
        String sectionId = deleteSectionIdField.getText().trim();

        if (sectionId.isBlank()) {
            statusLabel.setText("Enter a section ID to delete.");
            return;
        }

        boolean removed = courseStorage.removeSectionById(sectionId);

        if (!removed) {
            statusLabel.setText("Section not found.");
            return;
        }

        App.getAppData().saveSections();

        deleteSectionIdField.clear();
        loadSections();
        statusLabel.setText("Section deleted successfully.");
    }

    private void clearAddForm() {
        courseIdField.clear();
        instructorField.clear();
        sectionIdField.clear();
        accessCodeField.clear();
        priceField.clear();
        totalCapacityField.clear();
        currentCapacityField.clear();
    }
}
