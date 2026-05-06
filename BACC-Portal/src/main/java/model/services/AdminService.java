package services;

import storage.UserStorage;
import users.AdminUser;
import users.FacultyUser;
import users.StudentUser;
import users.User;

public class AdminService {
    private final UserStorage userStorage;

    public AdminService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addNewStudent(StudentUser studentUser) {
        return addUser(studentUser, () -> userStorage.addStudentUser(studentUser));
    }

    public boolean addNewFaculty(FacultyUser facultyUser) {
        return addUser(facultyUser, () -> userStorage.addFacultyUser(facultyUser));
    }

    public boolean addAdminUser(AdminUser adminUser) {
        return addUser(adminUser, () -> userStorage.addAdminUser(adminUser));
    }

    // helper to remove duplication
    private boolean addUser(User user, Runnable addAction) {
        if (userStorage.exists(user)) {
            return false;
        }
        addAction.run();
        return true;
    }
}
