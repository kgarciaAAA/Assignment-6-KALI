package baccportal.controllers;

import baccportal.App;
import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.services.AdminService;
import baccportal.model.storage.CourseStorage;
import baccportal.model.users.FacultyUser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminSectionsController {

    @FXML private TableView<CourseSection> sectionsTable;

    @FXML private TableColumn<CourseSection, String> sectionIdColumn;
    @FXML private TableColumn<CourseSection, String> courseIdColumn;
    @FXML private TableColumn<CourseSection, String> courseNameColumn;
    @FXML private TableColumn<CourseSection, Number> unitsColumn;
    @FXML private TableColumn<CourseSection, String> instructorColumn;
    @FXML private TableColumn<CourseSection, String> accessCodeColumn;
    @FXML private TableColumn<CourseSection, Number> priceColumn;
    @FXML private TableColumn<CourseSection, String> capacityColumn;
    @FXML private TableColumn<CourseSection, Void> deleteColumn;

    @FXML private TextField courseIdField;
    @FXML private ComboBox<FacultyUser> instructorBox;
    @FXML private TextField sectionIdField;
    @FXML private TextField accessCodeField;
    @FXML private TextField priceField;
    @FXML private TextField totalCapacityField;
    @FXML private TextField currentCapacityField;

    @FXML private Label statusLabel;

    private final CourseStorage courseStorage = App.getAppData().getCourseStorage();
    private final AdminService adminService = App.getAppData().getAdminService();

    @FXML
    private void initialize() {
        setupTable();
        setupDeleteColumn();
        setupInstructorBox();
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

        unitsColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCourse().getUnitAmount())
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

        capacityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCurrentCapacity()
                                + " / "
                                + data.getValue().getTotalCapacity()
                )
        );

        sectionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    CourseSection section = getTableView().getItems().get(getIndex());
                    deleteSection(section);
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

    private void setupInstructorBox() {
        instructorBox.getItems().setAll(
                App.getAppData().getUserStorage().getFacultyList()
        );

        instructorBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(FacultyUser faculty, boolean empty) {
                super.updateItem(faculty, empty);

                if (empty || faculty == null) {
                    setText(null);
                } else {
                    setText(faculty.getFullName() + " (" + faculty.getUserId() + ")");
                }
            }
        });

        instructorBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FacultyUser faculty, boolean empty) {
                super.updateItem(faculty, empty);

                if (empty || faculty == null) {
                    setText(null);
                } else {
                    setText(faculty.getFullName() + " (" + faculty.getUserId() + ")");
                }
            }
        });
    }

    private void loadSections() {
        sectionsTable.getItems().clear();
        sectionsTable.getItems().addAll(courseStorage.getAllSections().values());
    }

    @FXML
    private void handleAddSection() {
        String courseId = courseIdField.getText().trim();
        FacultyUser selectedInstructor = instructorBox.getValue();
        String instructor = selectedInstructor == null ? "" : selectedInstructor.getFullName().trim();
        String sectionId = sectionIdField.getText().trim();
        String accessCode = accessCodeField.getText().trim();
        String priceText = priceField.getText().trim();
        String totalCapacityText = totalCapacityField.getText().trim();

        if (courseId.isBlank() || instructor.isBlank() || sectionId.isBlank()
                || accessCode.isBlank() || priceText.isBlank()
                || totalCapacityText.isBlank()) {
            statusLabel.setText("Fill all section fields.");
            return;
        }

        Course course = courseStorage.getCourse(courseId);

        if (course == null) {
            statusLabel.setText("Course ID does not exist.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int totalCapacity = Integer.parseInt(totalCapacityText);

            if (price < 0 || totalCapacity < 0) {
                statusLabel.setText("Price and capacity cannot be negative.");
                return;
            }

            CourseSection section = new CourseSection(
                    course,
                    instructor,
                    sectionId,
                    accessCode,
                    price,
                    totalCapacity,
                    0
            );

            boolean added = adminService.addSection(section, selectedInstructor);

            if (!added) {
                statusLabel.setText("Section ID already exists.");
                return;
            }

            clearAddForm();
            loadSections();
            statusLabel.setText("Section added successfully.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Price must be a number. Capacity must be whole numbers.");
        }
    }

    private void deleteSection(CourseSection section) {
        if (section == null) {
            statusLabel.setText("No section selected.");
            return;
        }

        if (!confirmAction(
                "Delete Section",
                "Are you sure you want to delete section " + section.getSectionId() + "?"
        )) {
            return;
        }

        boolean removed = adminService.deleteSection(section.getSectionId());

        if (!removed) {
            statusLabel.setText("Section not found.");
            return;
        }

        

        loadSections();
        statusLabel.setText("Section deleted successfully.");
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
        instructorBox.setValue(null);
        sectionIdField.clear();
        accessCodeField.clear();
        priceField.clear();
        totalCapacityField.clear();
        currentCapacityField.clear();
    }
}


