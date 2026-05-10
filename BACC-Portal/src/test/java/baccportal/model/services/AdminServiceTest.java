package baccportal.model.services;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.data.PersistencePort;
import baccportal.model.storage.CourseStorage;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {
    private UserStorage userStorage;
    private CourseStorage courseStorage;
    private FakePersistence persistence;
    private AdminService adminService;
    private StudentUser student;
    private FacultyUser faculty;
    private Course course;
    private CourseSection section;

    @BeforeEach
    void setUp() {
        userStorage = new UserStorage();
        courseStorage = new CourseStorage();
        persistence = new FakePersistence();
        adminService = new AdminService(userStorage, courseStorage, persistence);

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

        course = new Course("CS151", "Object-Oriented Design", 3.0);
        section = new CourseSection(course, "Test Faculty", "SEC-001", "ABC123", 750.0, 30, 0);
    }

    @Test
    void addNewStudentAddsStudentAndSavesUsers() {
        boolean added = adminService.addNewStudent(student);

        assertTrue(added);
        assertSame(student, userStorage.findStudentUserById("S1001"));
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void addNewStudentReturnsFalseWhenDuplicateUserIdExists() {
        assertTrue(adminService.addNewStudent(student));

        boolean addedAgain = adminService.addNewStudent(student);

        assertFalse(addedAgain);
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void addNewFacultyAndAdminAddUsersAndSave() {
        AdminUser admin = new AdminUser("admin@sjsu.edu", "A1001", "password123", "Test Admin", false);

        assertTrue(adminService.addNewFaculty(faculty));
        assertTrue(adminService.addAdminUser(admin));

        assertSame(faculty, userStorage.findFacultyUserById("F1001"));
        assertSame(admin, userStorage.findUserById("A1001"));
        assertEquals(2, persistence.saveUsersCalls);
    }

    @Test
    void deleteStudentRemovesStudentDecrementsEnrolledSectionCapacityAndSaves() {
        section.incrementCurrentCapacity();
        student.addEnrolledSection(section);
        userStorage.addStudentUser(student);

        boolean deleted = adminService.deleteStudent("S1001");

        assertTrue(deleted);
        assertNull(userStorage.findStudentUserById("S1001"));
        assertEquals(0, section.getCurrentCapacity());
        assertEquals(1, persistence.saveUsersCalls);
        assertEquals(1, persistence.saveSectionsCalls);
    }

    @Test
    void deleteStudentReturnsFalseWhenStudentIsMissing() {
        boolean deleted = adminService.deleteStudent("missing");

        assertFalse(deleted);
        assertEquals(0, persistence.saveUsersCalls);
        assertEquals(0, persistence.saveSectionsCalls);
    }

    @Test
    void deleteFacultyUnassignsSectionsAndSaves() {
        faculty.addSectionTaught(section);
        userStorage.addFacultyUser(faculty);

        boolean deleted = adminService.deleteFaculty("F1001");

        assertTrue(deleted);
        assertNull(userStorage.findFacultyUserById("F1001"));
        assertEquals("TBD", section.getInstructorName());
        assertEquals(1, persistence.saveUsersCalls);
        assertEquals(1, persistence.saveSectionsCalls);
    }

    @Test
    void deleteAdminRemovesAdminAndSavesUsers() {
        AdminUser admin = new AdminUser("admin@sjsu.edu", "A1001", "password123", "Test Admin", false);
        userStorage.addAdminUser(admin);

        boolean deleted = adminService.deleteAdmin("A1001");

        assertTrue(deleted);
        assertNull(userStorage.findUserById("A1001"));
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void addCourseAddsCourseAndSavesCourses() {
        boolean added = adminService.addCourse(course);

        assertTrue(added);
        assertSame(course, courseStorage.getCourse("CS151"));
        assertEquals(1, persistence.saveCoursesCalls);
    }

    @Test
    void addCourseReturnsFalseWhenCourseAlreadyExists() {
        assertTrue(adminService.addCourse(course));

        boolean addedAgain = adminService.addCourse(course);

        assertFalse(addedAgain);
        assertEquals(1, persistence.saveCoursesCalls);
    }

    @Test
    void deleteCourseRemovesCourseSectionsAndUserReferences() {
        courseStorage.addCourse(course);
        courseStorage.addSection(section);
        student.addEnrolledSection(section);
        faculty.addSectionTaught(section);
        userStorage.addStudentUser(student);
        userStorage.addFacultyUser(faculty);

        boolean deleted = adminService.deleteCourse("CS151");

        assertTrue(deleted);
        assertNull(courseStorage.getCourse("CS151"));
        assertNull(courseStorage.getSection("SEC-001"));
        assertFalse(student.getEnrolledSections().contains(section));
        assertFalse(faculty.getSectionsTaught().contains(section));
        assertEquals(1, persistence.saveCoursesCalls);
        assertEquals(1, persistence.saveSectionsCalls);
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void addSectionAddsSectionToStorageAndFacultyAndSaves() {
        boolean added = adminService.addSection(section, faculty);

        assertTrue(added);
        assertSame(section, courseStorage.getSection("SEC-001"));
        assertTrue(faculty.getSectionsTaught().contains(section));
        assertEquals(1, persistence.saveSectionsCalls);
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void addSectionReturnsFalseForNullInstructorOrDuplicateSection() {
        assertFalse(adminService.addSection(section, null));
        assertTrue(adminService.addSection(section, faculty));
        assertFalse(adminService.addSection(section, faculty));
    }

    @Test
    void reassignSectionMovesSectionToNewFacultyAndUpdatesInstructorName() {
        FacultyUser newFaculty = new FacultyUser(
                "newfaculty@sjsu.edu",
                "F1002",
                "password123",
                "New Faculty",
                false,
                Department.DATA_SCIENCE
        );

        userStorage.addFacultyUser(faculty);
        userStorage.addFacultyUser(newFaculty);
        courseStorage.addSection(section);
        faculty.addSectionTaught(section);

        boolean reassigned = adminService.reassignSection("SEC-001", newFaculty);

        assertTrue(reassigned);
        assertEquals("New Faculty", section.getInstructorName());
        assertFalse(faculty.getSectionsTaught().contains(section));
        assertTrue(newFaculty.getSectionsTaught().contains(section));
        assertEquals(1, persistence.saveSectionsCalls);
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void deleteSectionRemovesSectionAndCleansUserReferences() {
        courseStorage.addSection(section);
        student.addCompletedSection(section);
        faculty.addSectionTaught(section);
        userStorage.addStudentUser(student);
        userStorage.addFacultyUser(faculty);

        boolean deleted = adminService.deleteSection("SEC-001");

        assertTrue(deleted);
        assertNull(courseStorage.getSection("SEC-001"));
        assertFalse(student.getCompletedSections().contains(section));
        assertFalse(faculty.getSectionsTaught().contains(section));
        assertEquals(1, persistence.saveSectionsCalls);
        assertEquals(1, persistence.saveUsersCalls);
    }

    private static class FakePersistence implements PersistencePort {
        int saveUsersCalls;
        int saveCoursesCalls;
        int saveSectionsCalls;

        @Override
        public void saveUsers() {
            saveUsersCalls++;
        }

        @Override
        public void saveCourses() {
            saveCoursesCalls++;
        }

        @Override
        public void saveSections() {
            saveSectionsCalls++;
        }
    }
}
