package baccportal.model.services;

import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;

public class AdminService {

    private final UserStorage userStorage;

    public AdminService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addNewStudent(StudentUser studentUser) {
        if (userStorage.exists(studentUser)) {
            return false;
        }

        userStorage.addStudentUser(studentUser);
        return true;
    }

    public boolean addNewFaculty(FacultyUser facultyUser) {
        if (userStorage.exists(facultyUser)) {
            return false;
        }

        userStorage.addFacultyUser(facultyUser);
        return true;
    }

    public boolean addAdminUser(AdminUser adminUser) {
        if (userStorage.exists(adminUser)) {
            return false;
        }

        userStorage.addAdminUser(adminUser);
        return true;
    }

    public boolean deleteStudent(String userId) {
        return userStorage.removeStudentById(userId);
    }

    public boolean deleteFaculty(String userId) {
        return userStorage.removeFacultyById(userId);
    }

    public boolean deleteAdmin(String userId) {
        return userStorage.removeAdminById(userId);
    }
}