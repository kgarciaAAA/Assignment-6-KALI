package baccportal.model.storage;

import java.util.ArrayList;
import java.util.List;

import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;

public class UserStorage {
    private final List<StudentUser> studentsList;
    private final List<FacultyUser> facultyList;
    private final List<AdminUser> adminList;

    //constructor
    public UserStorage() {
        studentsList = new ArrayList<>();
        facultyList = new ArrayList<>();
        adminList = new ArrayList<>();
    }

    //getters
    public List<StudentUser> getStudentsList() {
        return List.copyOf(studentsList);
    }

    public List<FacultyUser> getFacultyList() {
        return List.copyOf(facultyList);
    }

    public List<AdminUser> getAdminList() {
        return List.copyOf(adminList);
    }

    public boolean exists(User user) {
        for (StudentUser student : studentsList)
            if (student.equals(user)) return true;

        for (FacultyUser faculty : facultyList)
            if (faculty.equals(user)) return true;

        for (AdminUser admin : adminList)
            if (admin.equals(user)) return true;

        return false;
    }

    public List<FacultyUser> findFacultyUsersByName(String name) {
        List<FacultyUser> foundUsers = new ArrayList<>();
        for(FacultyUser user : facultyList) {
            if (user.getFullName().equalsIgnoreCase(name)){
                foundUsers.add(user);
            }
        }
        return List.copyOf(foundUsers);
    }

    public User findUserById(String userId) {
        for (StudentUser student : studentsList) {
            if (student.getUserId().equalsIgnoreCase(userId)) {
                return student;
            }
        }

        for (FacultyUser faculty : facultyList) {
            if (faculty.getUserId().equalsIgnoreCase(userId)) {
                return faculty;
            }
        }

        for (AdminUser admin : adminList) {
            if (admin.getUserId().equalsIgnoreCase(userId)) {
                return admin;
            }
        }

        return null;
    }

    public boolean removeStudentById(String userId) {
        return studentsList.removeIf(student -> student.getUserId().equalsIgnoreCase(userId));
    }

    public boolean removeFacultyById(String userId) {
        return facultyList.removeIf(faculty -> faculty.getUserId().equalsIgnoreCase(userId));
    }

    public boolean removeAdminById(String userId) {
        return adminList.removeIf(admin -> admin.getUserId().equalsIgnoreCase(userId));
    }

    //controlled updates
    public void addStudentUser(StudentUser user) {
        studentsList.add(user);
    }

    public void addFacultyUser(FacultyUser user) {
        facultyList.add(user);
    }

    public void addAdminUser(AdminUser user) {
        adminList.add(user);
    }
}



