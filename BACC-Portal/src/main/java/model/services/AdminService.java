package services;
import storage.UserStorage;
import users.AdminUser;
import users.FacultyUser;
import users.StudentUser;
// import academics.Major;
// import academics.Department;

public class AdminService {
    public boolean addNewStudent(UserStorage userStorage, StudentUser studentUser) {
        boolean doesExist = userStorage.exists(studentUser);
        if (doesExist) return false;
        userStorage.addStudentUser(studentUser);
        return true;
    }

    public boolean addNewFaculty(UserStorage userStorage, FacultyUser facultyUser) { 
        boolean doesExist = userStorage.exists(facultyUser);
        if (doesExist) return false;
        userStorage.addFacultyUser(facultyUser);
        return true;
    }

    public boolean addAdminUser(UserStorage userStorage, AdminUser adminUser) {
        boolean doesExist = userStorage.exists(adminUser);
        if (doesExist) return false;
        userStorage.addAdminUser(adminUser);
        return true;
    }

}
