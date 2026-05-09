package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.CourseSection;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    @FXML private Label statusLabel;

    private FacultyUser faculty;
    private final UserStorage userStorage = App.getAppData().getUserStorage();

    @FXML
    private void initialize() {
        User user = App.getCurrentUser();

        if (user instanceof FacultyUser) {
            faculty = (FacultyUser) user;
            setupSectionsTable();
            setupStudentsTable();
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

        priceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice())
        );

        unitsColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCourse().getUnitAmount())
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

    private void loadSections() {
        sectionsTable.getItems().clear();

        String facultyName = faculty.getFullName().trim();

        for (CourseSection section : App.getAppData().getCourseStorage().getAllSections().values()) {
            String instructorName = section.getInstructorName().trim();

            if (instructorName.equalsIgnoreCase(facultyName)) {
                sectionsTable.getItems().add(section);
            }
        }

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

        User refreshedUser = App.getAppData().getUserStorage().findUserById(facultyId);

        if (refreshedUser instanceof FacultyUser) {
            faculty = (FacultyUser) refreshedUser;
        }

        loadSections();
        studentsTable.getItems().clear();
        sectionIdField.clear();

        statusLabel.setText("Sections and students refreshed.");
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

        if (!facultyTeachesSection(sectionId)) {
            statusLabel.setText("You are not assigned to this section.");
            return;
        }

        StudentUser selectedStudent = studentsTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            statusLabel.setText("Select a student from the table.");
            return;
        }

        CourseSection targetSection = null;

        for (CourseSection section : selectedStudent.getEnrolledSections()) {
            if (section.getSectionId().equalsIgnoreCase(sectionId)) {
                targetSection = section;
                break;
            }
        }

        if (targetSection == null) {
            statusLabel.setText("Selected student is not enrolled in this section.");
            return;
        }

        if (!confirmAction(
                "Mark Completed",
                "Mark " + selectedStudent.getFullName()
                        + " as completed for section " + targetSection.getSectionId() + "?"
        )) {
            return;
        }

        boolean completed = selectedStudent.completeSection(targetSection);

        if (!completed) {
            statusLabel.setText("Could not mark section completed.");
            return;
        }

        // This is the important line.
        targetSection.decrementCurrentCapacity();

        App.getAppData().saveUsers();
        App.getAppData().saveSections();

        loadSections();
        handleViewStudents();

        statusLabel.setText("Student marked completed.");
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

        if (!facultyTeachesSection(sectionId)) {
            studentsTable.getItems().clear();
            statusLabel.setText("You are not assigned to this section.");
            return;
        }

        studentsTable.getItems().clear();

        for (StudentUser student : App.getAppData().getUserStorage().getStudentsList()) {
            for (CourseSection section : student.getEnrolledSections()) {
                if (section.getSectionId().equalsIgnoreCase(sectionId)) {
                    studentsTable.getItems().add(student);
                    break;
                }
            }
        }

        if (studentsTable.getItems().isEmpty()) {
            statusLabel.setText("No students enrolled in this section.");
        } else {
            statusLabel.setText("Students loaded.");
        }
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



    private boolean facultyTeachesSection(String sectionId) {
        String facultyName = faculty.getFullName().trim();

        for (CourseSection section : App.getAppData().getCourseStorage().getAllSections().values()) {
            String instructorName = section.getInstructorName().trim();

            boolean sameInstructor = instructorName.equalsIgnoreCase(facultyName);
            boolean sameSection = section.getSectionId().trim().equalsIgnoreCase(sectionId.trim());

            if (sameInstructor && sameSection) {
                return true;
            }
        }

        return false;
    }
}