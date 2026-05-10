package baccportal.controllers;

import baccportal.model.academics.CourseSection;
import baccportal.model.data.AppData;
import baccportal.model.services.RegistrationService;
import baccportal.model.session.Session;
import baccportal.model.storage.CourseStorage;
import baccportal.model.users.StudentUser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class StudentManageCoursesController {

    @FXML private TableView<CourseSection> sectionsTable;
    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, String> instructorColumn;
    @FXML private TableColumn<CourseSection, String> capacityColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;
    @FXML private TableColumn<CourseSection, Number> unitsColumn;

    @FXML private TextField sectionIdField;
    @FXML private TextField accessCodeField;
    @FXML private Label statusLabel;

    private StudentUser student;
    private final Session session;
    private final AppData appData;
    private final CourseStorage courseStorage;
    private final RegistrationService registrationService;

    public StudentManageCoursesController(Session session,
                                          AppData appData,
                                          CourseStorage courseStorage,
                                          RegistrationService registrationService) {
        this.session = session;
        this.appData = appData;
        this.courseStorage = courseStorage;
        this.registrationService = registrationService;
    }

    @FXML
    private void initialize() {
        student = session.student();

        if (student != null) {
            setupTable();
            loadSections();
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

        capacityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCurrentCapacity()
                                + " / "
                                + data.getValue().getTotalCapacity()
                )
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

        sectionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadSections() {
        sectionsTable.getItems().clear();
        sectionsTable.getItems().addAll(
                courseStorage.getAllSections().values()
        );
    }

    @FXML
    private void handleRefresh() {
        appData.reloadSections();
        loadSections();
        statusLabel.setText("Sections refreshed.");
    }

    @FXML
    private void handleEnroll() {
        if (student == null) {
            statusLabel.setText("No student logged in.");
            return;
        }

        String sectionId = sectionIdField.getText().trim();

        if (sectionId.isBlank()) {
            statusLabel.setText("Enter a section ID.");
            return;
        }

        CourseSection section = courseStorage.getSection(sectionId);

        if (section == null) {
            statusLabel.setText("Section not found.");
            return;
        }

        String accessCode = accessCodeField.getText().trim();

        if (accessCode.isBlank()) {
            statusLabel.setText("Enter the access code.");
            return;
        }

        boolean enrolled = registrationService.enroll(student, section, accessCode);

        if (!enrolled) {
            statusLabel.setText("Enrollment failed. Check duplicate enrollment, prerequisites, capacity, or access code.");
            return;
        }

        sectionIdField.clear();
        accessCodeField.clear();
        loadSections();

        statusLabel.setText(
                "Enrolled successfully. New balance: $"
                        + String.format("%.2f", student.getBalanceOwed())
        );
    }
}
