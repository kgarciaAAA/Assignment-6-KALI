package baccportal.model.storage;

import java.io.IOException;

import baccportal.model.data.CourseFileHandler;
import baccportal.model.data.SectionFileHandler;
import baccportal.model.data.UserFileHandler;

public class AppData {

    private final UserStorage userStorage;
    private final CourseStorage courseStorage;

    private final UserFileHandler userFileHandler;
    private final CourseFileHandler courseFileHandler;
    private final SectionFileHandler sectionFileHandler;

    public AppData() {
        this.userStorage = new UserStorage();
        this.courseStorage = new CourseStorage();

        this.userFileHandler = new UserFileHandler();
        this.courseFileHandler = new CourseFileHandler();
        this.sectionFileHandler = new SectionFileHandler();
    }

    public void load() {
        try {
            courseFileHandler.readCoursesFromFile(courseStorage);
            System.out.println("Courses file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load courses.txt: " + e.getMessage());
        }

        try {
            sectionFileHandler.readSectionsFromFile(courseStorage);
            System.out.println("Sections file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load sections.txt: " + e.getMessage());
        }

        loadUsersOnly();

        System.out.println("Courses loaded: " + courseStorage.getAllCourses().size());
        System.out.println("Sections loaded: " + courseStorage.getAllSections().size());
        System.out.println("Students loaded: " + userStorage.getStudentsList().size());
        System.out.println("Faculty loaded: " + userStorage.getFacultyList().size());
        System.out.println("Admins loaded: " + userStorage.getAdminList().size());
    }

    private void loadUsersOnly() {
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
    }

    public void reloadSections() {
        try {
            courseStorage.clearSections();
            sectionFileHandler.readSectionsFromFile(courseStorage);
            System.out.println("Sections reloaded.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reloading sections.");
        }
    }

    public void reloadUsers() {
        try {
            userStorage.clearUsers();
            loadUsersOnly();
            System.out.println("Users reloaded.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reloading users.");
        }
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

    public void saveCourses() {
        try {
            courseFileHandler.writeCoursesToFile(courseStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving course data to text files.");
        }
    }

    public void saveSections() {
        try {
            sectionFileHandler.writeSectionsToFile(courseStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving section data to text files.");
        }
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public CourseStorage getCourseStorage() {
        return courseStorage;
    }
}
