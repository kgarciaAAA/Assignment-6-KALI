package services;
import storage.UserStorage;
import users.*;
// import academics.Major;
// import academics.Department;

public class AdminService {
    private final UserStorage userStorage;

    public AdminService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addNewStudent(StudentUser studentUser) {
        boolean doesExist = userStorage.exists(studentUser);
        if (doesExist) return false;
        userStorage.addStudentUser(studentUser);
        return true;
    }

    public boolean addNewFaculty(FacultyUser facultyUser) { 
        boolean doesExist = userStorage.exists(facultyUser);
        if (doesExist) return false;
        userStorage.addFacultyUser(facultyUser);
        return true;
    }

    public boolean addAdminUser(AdminUser adminUser) {
        boolean doesExist = userStorage.exists(adminUser);
        if (doesExist) return false;
        userStorage.addAdminUser(adminUser);
        return true;
    }

}
