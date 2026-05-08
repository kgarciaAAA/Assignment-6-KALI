package baccportal.model.storage;

import java.io.IOException;

import baccportal.model.data.UserFileHandler;

public class AppData {

    private final UserStorage userStorage;
    private final CourseStorage courseStorage;
    private final UserFileHandler userFileHandler;

    public AppData() {
        this.userStorage = new UserStorage();
        this.courseStorage = new CourseStorage();
        this.userFileHandler = new UserFileHandler();
    }

    public void load() {
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        System.out.println("admin exists: " + java.nio.file.Files.exists(java.nio.file.Path.of("txtfiles/admin.txt")));
        System.out.println("students exists: " + java.nio.file.Files.exists(java.nio.file.Path.of("txtfiles/students.txt")));
        System.out.println("faculty exists: " + java.nio.file.Files.exists(java.nio.file.Path.of("txtfiles/faculty.txt")));

        try {
            userFileHandler.readAdminUsersFromFile(userStorage);
            System.out.println("Admin file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load admin.txt: " + e.getMessage());
        }

        try {
            userFileHandler.readStudentUsersFromFile(userStorage, courseStorage);
            System.out.println("Students file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load students.txt: " + e.getMessage());
        }

        try {
            userFileHandler.readFacultyUsersFromFile(userStorage, courseStorage);
            System.out.println("Faculty file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load faculty.txt: " + e.getMessage());
        }

        System.out.println("Students loaded: " + userStorage.getStudentsList().size());
        System.out.println("Faculty loaded: " + userStorage.getFacultyList().size());
        System.out.println("Admins loaded: " + userStorage.getAdminList().size());
    }

    public void saveUsers() {
        try {
            userFileHandler.writeAdminUsersToFile(userStorage);
            userFileHandler.writeStudentUserToFile(userStorage);
            userFileHandler.writeFacultyUsersToFile(userStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving user data to text files.");
        }
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public CourseStorage getCourseStorage() {
        return courseStorage;
    }
}
