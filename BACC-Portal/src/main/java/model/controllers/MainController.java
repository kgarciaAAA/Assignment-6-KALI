package model.controllers;

import academics.CourseSection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.PaymentService;
import services.RegistrationService;
import storage.CourseStorage;
import storage.UserStorage;
import users.StudentUser;

import java.io.IOException;

public class MainController {

    @FXML private TextField studentIdField;
    @FXML private TextField sectionIdField;
    @FXML private ListView<String> sectionListView;
    @FXML private Label statusLabel;

    private final CourseStorage courseStorage = new CourseStorage();
    private final UserStorage userStorage = new UserStorage();

    private final RegistrationService registrationService = new RegistrationService();
    private final PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        loadData(); // For loading data
        loadSections(); // For loading Section
    }

    @FXML
    private void handleEnroll() {
        String studentId = studentIdField.getText();
        String sectionId = sectionIdField.getText();

        StudentUser student = findStudent(studentId);
        if (student == null) {
            statusLabel.setText("Student not found.");
            return;
        }

        CourseSection section = courseStorage.getSection(sectionId);
        if (section == null) {
            statusLabel.setText("Section not found.");
            return;
        }

        boolean success = registrationService.enroll(student, section);

        if (success) {
            paymentService.addCharge(student, section.getPrice());
            statusLabel.setText("Enrolled! Balance: $" + student.getBalanceOwed());
            loadSections();
        } else {
            statusLabel.setText("Enrollment failed.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadSections();
        statusLabel.setText("Refreshed.");
    }

    private void loadSections() {
        sectionListView.getItems().clear();

        for (CourseSection section : courseStorage.getAllSections().values()) {
            String text = section.getSectionId()
                    + " | " + section.getCourse().getCourseId()
                    + " | " + section.getCurrentCapacity()
                    + "/" + section.getTotalCapacity();

            sectionListView.getItems().add(text);
        }
    }

    private StudentUser findStudent(String id) {
        for (StudentUser s : userStorage.getStudentsList()) {
            if (s.getUserId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    // TEMP: so GUI works before UserFileHandler
    private void addTestStudent() {
        StudentUser test = new StudentUser(
                "test@email.com",
                "123",
                "pass",
                "Test User",
                null,
                0
        );
        userStorage.addStudentUser(test);
    }
    private void loadData() {
        try {
            new data.CourseFileHandler().readCoursesFromFile(courseStorage);
            new data.SectionFileHandler().readSectionsFromFile(courseStorage);
            new data.UserFileHandler().readUsersFromFile(userStorage);
        } catch (IOException e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
        }
    }
}
