package baccportal.model.services;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.data.PersistencePort;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.StudentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcademicRecordsServiceTest {
    private FakePersistence persistence;
    private UserStorage userStorage;
    private AcademicRecordsService recordsService;
    private StudentUser student;
    private CourseSection section;

    @BeforeEach
    void setUp() {
        persistence = new FakePersistence();
        userStorage = new UserStorage();
        recordsService = new AcademicRecordsService(userStorage, persistence);

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

        section = new CourseSection(
                new Course("CS151", "Object-Oriented Design", 3.0),
                "Dr. Test",
                "SEC-001",
                "ABC123",
                750.0,
                30,
                1
        );
    }

    @Test
    void completeSectionMovesEnrolledSectionToCompletedAndSaves() {
        student.addEnrolledSection(section);

        boolean completed = recordsService.completeSection(student, section);

        assertTrue(completed);
        assertFalse(student.getEnrolledSections().contains(section));
        assertTrue(student.getCompletedSections().contains(section));
        assertEquals(0, section.getCurrentCapacity());
        assertEquals(1, persistence.saveUsersCalls);
        assertEquals(1, persistence.saveSectionsCalls);
    }

    @Test
    void completeSectionReturnsFalseWhenStudentWasNotEnrolled() {
        boolean completed = recordsService.completeSection(student, section);

        assertFalse(completed);
        assertTrue(student.getCompletedSections().isEmpty());
        assertEquals(1, section.getCurrentCapacity());
        assertEquals(0, persistence.saveUsersCalls);
        assertEquals(0, persistence.saveSectionsCalls);
    }

    @Test
    void findEnrolledSectionReturnsMatchingSectionIgnoringCase() {
        student.addEnrolledSection(section);

        assertSame(section, recordsService.findEnrolledSection(student, "sec-001"));
        assertNull(recordsService.findEnrolledSection(student, "missing"));
    }

    @Test
    void calculateCompletedCreditsAddsUnitsForCompletedSections() {
        Course mathCourse = new Course("MATH30", "Calculus", 4.0);
        CourseSection mathSection = new CourseSection(
                mathCourse,
                "Dr. Math",
                "SEC-002",
                "DEF456",
                500.0,
                30,
                0
        );

        student.addCompletedSection(section);
        student.addCompletedSection(mathSection);

        double credits = recordsService.calculateCompletedCredits(student);

        assertEquals(7.0, credits, 0.001);
    }

    @Test
    void getStudentsInSectionReturnsStudentsCurrentlyEnrolledInSection() {
        StudentUser otherStudent = new StudentUser(
                "other@sjsu.edu",
                "S1002",
                "password123",
                "Other Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                0.0
        );

        CourseSection otherSection = new CourseSection(
                new Course("MATH30", "Calculus", 4.0),
                "Dr. Math",
                "SEC-002",
                "DEF456",
                500.0,
                30,
                1
        );

        student.addEnrolledSection(section);
        otherStudent.addEnrolledSection(otherSection);

        userStorage.addStudentUser(student);
        userStorage.addStudentUser(otherStudent);

        assertEquals(1, recordsService.getStudentsInSection("sec-001").size());
        assertTrue(recordsService.getStudentsInSection("sec-001").contains(student));
        assertFalse(recordsService.getStudentsInSection("sec-001").contains(otherStudent));
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
