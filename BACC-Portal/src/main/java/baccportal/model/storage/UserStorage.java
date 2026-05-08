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
