package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.CourseSection;
import baccportal.model.services.AcademicRecordsService;
import baccportal.model.services.FacultyService;
import baccportal.model.services.RegistrationService;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
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

public class FacultySectionsController {

    @FXML private TableView<CourseSection> sectionsTable;
    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, String> capacityColumn;
    @FXML private TableColumn<CourseSection, Number> unitsColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;

    @FXML private TextField sectionIdField;

    @FXML private TableView<StudentUser> studentsTable;
    @FXML private TableColumn<StudentUser, String> studentIdColumn;
    @FXML private TableColumn<StudentUser, String> studentNameColumn;
    @FXML private TableColumn<StudentUser, String> studentEmailColumn;
    @FXML private TableColumn<StudentUser, Void> dropColumn;

    @FXML private Label statusLabel;

    private FacultyUser faculty;
    private final FacultyService facultyService = App.getAppData().getFacultyService();
    private final AcademicRecordsService academicRecordsService = App.getAppData().getAcademicRecordsService();
    private final RegistrationService registrationService = App.getAppData().getRegistrationService();

    @FXML
    private void initialize() {
        faculty = App.getSession().faculty();

        if (faculty != null) {
            setupSectionsTable();
            setupStudentsTable();
            setupDropColumn();
            loadSections();
        } else {
            statusLabel.setText("No faculty user is currently logged in.");
        }
    }

    private void setupSectionsTable() {
        sectionIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSectionId())
        );

        courseIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourse().getCourseId())
        );

        courseNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourse().getCourseName())
        );

        capacityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCurrentCapacity()
                                + " / "
                                + data.getValue().getTotalCapacity()
                )
        );

        unitsColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCourse().getUnitAmount())
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

    private void setupStudentsTable() {
        studentIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUserId())
        );

        studentNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName())
        );

        studentEmailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );

        studentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
                    StudentUser student = getTableView().getItems().get(getIndex());
                    dropStudent(student);
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

    private void loadSections() {
        sectionsTable.getItems().setAll(
                facultyService.getSectionsTaughtByFaculty(faculty)
        );

        if (sectionsTable.getItems().isEmpty()) {
            statusLabel.setText("No sections assigned.");
        } else {
            statusLabel.setText("");
        }
    }

    @FXML
    private void handleRefresh() {
        String facultyId = faculty.getUserId();

        App.getAppData().reloadSections();
        App.getAppData().reloadUsers();

        FacultyUser refreshed =
                App.getAppData().getUserStorage().findFacultyUserById(facultyId);

        if (refreshed != null) {
            faculty = refreshed;
            App.getSession().setUser(refreshed);
        }

        loadSections();
        studentsTable.getItems().clear();
        sectionIdField.clear();

        statusLabel.setText("Sections and students refreshed.");
    }

    @FXML
    private void handleViewStudents() {
        if (faculty == null) {
            statusLabel.setText("No faculty user is currently logged in.");
            return;
        }

        App.getAppData().reloadUsers();

        String sectionId = sectionIdField.getText().trim();

        if (sectionId.isBlank()) {
            statusLabel.setText("Enter a section ID.");
            return;
        }

        if (!facultyService.teachesSection(faculty, sectionId)) {
            studentsTable.getItems().clear();
            statusLabel.setText("You are not assigned to this section.");
            return;
        }

        studentsTable.getItems().setAll(
                academicRecordsService.getStudentsInSection(sectionId)
        );

        if (studentsTable.getItems().isEmpty()) {
            statusLabel.setText("No students enrolled in this section.");
        } else {
            statusLabel.setText("Students loaded.");
        }
    }

    private void dropStudent(StudentUser student) {
        if (faculty == null) {
            statusLabel.setText("No faculty user is currently logged in.");
            return;
        }

        if (student == null) {
            statusLabel.setText("No student selected.");
            return;
        }

        String sectionId = sectionIdField.getText().trim();

        if (sectionId.isBlank()) {
            statusLabel.setText("Enter a section ID first.");
            return;
        }

        if (!facultyService.teachesSection(faculty, sectionId)) {
            statusLabel.setText("You are not assigned to this section.");
            return;
        }

        CourseSection section =
                academicRecordsService.findEnrolledSection(student, sectionId);

        if (section == null) {
            statusLabel.setText("Student is not enrolled in this section.");
            return;
        }

        if (!confirmAction(
                "Drop Student",
                "Are you sure you want to drop "
                        + student.getFullName()
                        + " from "
                        + section.getCourse().getCourseName()
                        + " (" + section.getSectionId() + ")?"
        )) {
            return;
        }

        boolean dropped = registrationService.drop(student, section);

        if (!dropped) {
            statusLabel.setText("Could not drop student from section.");
            return;
        }

        loadSections();
        handleViewStudents();

        statusLabel.setText("Student dropped successfully.");
    }

    @FXML
    private void handleMarkSelectedCompleted() {
        if (faculty == null) {
            statusLabel.setText("No faculty user is currently logged in.");
            return;
        }

        String sectionId = sectionIdField.getText().trim();

        if (sectionId.isBlank()) {
            statusLabel.setText("Enter a section ID first.");
            return;
        }

        if (!facultyService.teachesSection(faculty, sectionId)) {
            statusLabel.setText("You are not assigned to this section.");
            return;
        }

        StudentUser selectedStudent =
                studentsTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            statusLabel.setText("Select a student from the table.");
            return;
        }

        CourseSection targetSection =
                academicRecordsService.findEnrolledSection(selectedStudent, sectionId);

        if (targetSection == null) {
            statusLabel.setText("Selected student is not enrolled in this section.");
            return;
        }

        if (!confirmAction(
                "Mark Completed",
                "Mark " + selectedStudent.getFullName()
                        + " as completed for section "
                        + targetSection.getSectionId()
                        + "?"
        )) {
            return;
        }

        boolean completed =
                academicRecordsService.completeSection(selectedStudent, targetSection);

        if (!completed) {
            statusLabel.setText("Could not mark section completed.");
            return;
        }

        loadSections();
        handleViewStudents();

        statusLabel.setText("Student marked completed.");
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