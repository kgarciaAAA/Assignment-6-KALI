package baccportal.model.storage;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStorageTest {
    private UserStorage storage;
    private StudentUser student;
    private FacultyUser faculty;
    private AdminUser admin;

    @BeforeEach
    void setUp() {
        storage = new UserStorage();

        student = new StudentUser(
                "student@sjsu.edu",
                "S1001",
                "password123",
                "Test Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                0.0
        );

        faculty = new FacultyUser(
                "faculty@sjsu.edu",
                "F1001",
                "password123",
                "Test Faculty",
                false,
                Department.COMPUTER_SCIENCE
        );

        admin = new AdminUser(
                "admin@sjsu.edu",
                "A1001",
                "password123",
                "Test Admin",
                false
        );
    }

    @Test
    void addAndFindUsersByIdAcrossAllRoles() {
        storage.addStudentUser(student);
        storage.addFacultyUser(faculty);
        storage.addAdminUser(admin);

        assertSame(student, storage.findUserById("S1001"));
        assertSame(faculty, storage.findUserById("F1001"));
        assertSame(admin, storage.findUserById("A1001"));
    }

    @Test
    void findUserByIdIsCaseInsensitive() {
        storage.addStudentUser(student);

        User found = storage.findUserById("s1001");

        assertSame(student, found);
    }

    @Test
    void existsReturnsTrueForUserWithSameId() {
        storage.addStudentUser(student);

        StudentUser sameIdDifferentObject = new StudentUser(
                "other@sjsu.edu",
                "s1001",
                "password123",
                "Other Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                0.0
        );

        assertTrue(storage.exists(sameIdDifferentObject));
    }

    @Test
    void removeUserByIdRemovesCorrectRole() {
        storage.addStudentUser(student);
        storage.addFacultyUser(faculty);
        storage.addAdminUser(admin);

        assertTrue(storage.removeStudentById("S1001"));
        assertTrue(storage.removeFacultyById("F1001"));
        assertTrue(storage.removeAdminById("A1001"));

        assertNull(storage.findUserById("S1001"));
        assertNull(storage.findUserById("F1001"));
        assertNull(storage.findUserById("A1001"));
    }

    @Test
    void returnedUserListsCannotBeModifiedFromOutside() {
        storage.addStudentUser(student);
        storage.addFacultyUser(faculty);
        storage.addAdminUser(admin);

        assertThrows(UnsupportedOperationException.class, () -> storage.getStudentsList().clear());
        assertThrows(UnsupportedOperationException.class, () -> storage.getFacultyList().clear());
        assertThrows(UnsupportedOperationException.class, () -> storage.getAdminList().clear());

        assertEquals(1, storage.getStudentsList().size());
        assertEquals(1, storage.getFacultyList().size());
        assertEquals(1, storage.getAdminList().size());
    }

    @Test
    void clearUsersRemovesAllUsers() {
        storage.addStudentUser(student);
        storage.addFacultyUser(faculty);
        storage.addAdminUser(admin);

        storage.clearUsers();

        assertTrue(storage.getStudentsList().isEmpty());
        assertTrue(storage.getFacultyList().isEmpty());
        assertTrue(storage.getAdminList().isEmpty());
    }

    @Test
    void findFacultyUsersByNameReturnsMatchingFacultyOnly() {
        FacultyUser secondFacultySameName = new FacultyUser(
                "faculty2@sjsu.edu",
                "F1002",
                "password123",
                "Test Faculty",
                false,
                Department.DATA_SCIENCE
        );

        storage.addFacultyUser(faculty);
        storage.addFacultyUser(secondFacultySameName);

        assertEquals(2, storage.findFacultyUsersByName("test faculty").size());
        assertTrue(storage.findFacultyUsersByName("Missing Name").isEmpty());
    }

    @Test
    void findSpecificRoleMethodsReturnCorrectUserOrNull() {
        storage.addStudentUser(student);
        storage.addFacultyUser(faculty);

        assertSame(student, storage.findStudentUserById("s1001"));
        assertSame(faculty, storage.findFacultyUserById("f1001"));
        assertNull(storage.findStudentUserById("missing"));
        assertNull(storage.findFacultyUserById("missing"));
    }

    @Test
    void detachSectionRemovesItFromStudentsAndFaculty() {
        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Test Faculty", "SEC-001", "ABC123", 750.0, 30, 1);

        student.addEnrolledSection(section);
        student.addCompletedSection(section);
        faculty.addSectionTaught(section);

        storage.addStudentUser(student);
        storage.addFacultyUser(faculty);

        storage.detachSectionFromAllStudents(section);
        storage.detachSectionFromAllFaculty(section);

        assertFalse(student.getEnrolledSections().contains(section));
        assertFalse(student.getCompletedSections().contains(section));
        assertFalse(faculty.getSectionsTaught().contains(section));
    }
}
