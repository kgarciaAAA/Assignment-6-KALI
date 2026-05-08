package baccportal.controllers;

import java.io.IOException;

import baccportal.model.academics.CourseSection;
import baccportal.model.data.CourseFileHandler;
import baccportal.model.data.SectionFileHandler;
import baccportal.model.data.UserFileHandler;
import baccportal.model.services.PaymentService;
import baccportal.model.services.RegistrationService;
import baccportal.model.storage.CourseStorage;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.StudentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
        StudentUser test = new StudentUser("test@email.com", "123", "pass", "Test User", false, null, 0);
        userStorage.addStudentUser(test);
    }

    private void loadData() {
        try {
            new CourseFileHandler().readCoursesFromFile(courseStorage);
            new SectionFileHandler().readSectionsFromFile(courseStorage);
            new UserFileHandler().readStudentUsersFromFile(userStorage, courseStorage);
        } catch (IOException e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
        }
    }
}
