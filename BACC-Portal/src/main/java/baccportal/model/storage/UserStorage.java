package baccportal.model.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import baccportal.model.academics.CourseSection;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;

public class UserStorage {
    private final Map<String, StudentUser> students;
    private final Map<String, FacultyUser> faculty;
    private final Map<String, AdminUser> admins;

    // Ensures that user IDs are always stored in a consistent format.
    private static String consistentUserID(String userId) {
        // Using Locale.ENGLISH to ensure consistent case conversion.
        return userId.toLowerCase(Locale.ENGLISH);
    }

    //constructor
    public UserStorage() {
        students = new LinkedHashMap<>();
        faculty = new LinkedHashMap<>();
        admins = new LinkedHashMap<>();
    }

    //getters
    public List<StudentUser> getStudentsList() {
        return List.copyOf(students.values());
    }

    public List<FacultyUser> getFacultyList() {
        return List.copyOf(faculty.values());
    }

    public List<AdminUser> getAdminList() {
        return List.copyOf(admins.values());
    }

    public boolean exists(User user) {
        String id = user.getUserId();
        if (id == null) {
            return false;
        }
        String key = consistentUserID(id);
        return students.containsKey(key)
                || faculty.containsKey(key)
                || admins.containsKey(key);
    }

    public List<FacultyUser> findFacultyUsersByName(String name) {
        List<FacultyUser> foundUsers = new ArrayList<>();
        for (FacultyUser user : faculty.values()) {
            if (user.getFullName().equalsIgnoreCase(name)) {
                foundUsers.add(user);
            }
        }
        return List.copyOf(foundUsers);
    }

    public User findUserById(String userId) {
        String key = consistentUserID(userId);

        StudentUser student = students.get(key);
        if (student != null) 
            return student;
        
        FacultyUser facultyUser = faculty.get(key);
        if (facultyUser != null) 
            return facultyUser;
        
        return admins.get(key);
    }

    // Guranteed to return a student user with this id, or null if none.
    public StudentUser findStudentUserById(String userId) {
        return students.get(consistentUserID(userId));
    }

    // Guranteed to return a faculty user with this id, or null if none.
    public FacultyUser findFacultyUserById(String userId) {
        return faculty.get(consistentUserID(userId));
    }

    public boolean removeStudentById(String userId) {
        return students.remove(consistentUserID(userId)) != null;
    }

    public boolean removeFacultyById(String userId) {
        return faculty.remove(consistentUserID(userId)) != null;
    }

    // TODO: Moved from AdminService to UserStorage
    public void detachSectionFromAllFaculty(CourseSection section) {
        for (FacultyUser faculty : faculty.values()) {
            faculty.removeSectionTaught(section);
        }
    }
    // TODO: Moved from AdminService to UserStorage
    public void detachSectionFromAllStudents(CourseSection section) {
        for (StudentUser student : students.values()) {
            student.removeEnrolledSection(section);
            student.removeCompletedSection(section);
        }
    }

    public boolean removeAdminById(String userId) {
        return admins.remove(consistentUserID(userId)) != null;
    }

    //controlled updates
    public void addStudentUser(StudentUser user) {
        students.put(consistentUserID(user.getUserId()), user);
    }

    public void addFacultyUser(FacultyUser user) {
        faculty.put(consistentUserID(user.getUserId()), user);
    }

    public void addAdminUser(AdminUser user) {
        admins.put(consistentUserID(user.getUserId()), user);
    }

    public void clearUsers() {
        students.clear();
        faculty.clear();
        admins.clear();
    }
}


