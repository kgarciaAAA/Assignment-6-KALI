package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.CourseSection;
import baccportal.model.services.PaymentService;
import baccportal.model.services.RegistrationService;
import baccportal.model.storage.CourseStorage;
import baccportal.model.users.StudentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class StudentController {

    @FXML private ListView<String> sectionListView;
    @FXML private ListView<String> enrolledListView;
    @FXML private TextField sectionIdField;
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;

    private final RegistrationService registrationService = new RegistrationService();
    private final PaymentService paymentService = new PaymentService();

    private CourseStorage courseStorage;
    private StudentUser student;

    @FXML
    public void initialize() {
        // get shared data from App
        courseStorage = new CourseStorage();
        student = (StudentUser) App.getCurrentUser();

        loadSections();
        loadEnrolled();
        updateBalance();
    }

    @FXML
    private void handleEnroll() {
        String sectionId = sectionIdField.getText();

        if (sectionId == null || sectionId.isBlank()) {
            statusLabel.setText("Enter section ID.");
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
            statusLabel.setText("Enrolled successfully.");
            loadSections();
            loadEnrolled();
            updateBalance();
        } else {
            statusLabel.setText("Enrollment failed.");
        }
    }

    private void loadSections() {
        sectionListView.getItems().clear();

        for (CourseSection s : courseStorage.getAllSections().values()) {
            String text = s.getSectionId()
                    + " | " + s.getCourse().getCourseId()
                    + " | " + s.getCurrentCapacity()
                    + "/" + s.getTotalCapacity()
                    + " | $" + s.getPrice();

            sectionListView.getItems().add(text);
        }
    }

    private void loadEnrolled() {
        enrolledListView.getItems().clear();

        for (CourseSection s : student.getEnrolledSections()) {
            String text = s.getSectionId()
                    + " | " + s.getCourse().getCourseId();

            enrolledListView.getItems().add(text);
        }
    }

    private void updateBalance() {
        balanceLabel.setText("$" + student.getBalanceOwed());
    }
}
