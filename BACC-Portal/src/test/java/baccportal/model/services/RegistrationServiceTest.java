package baccportal.model.services;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.data.PersistencePort;
import baccportal.model.users.StudentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {
    private FakePersistence persistence;
    private RegistrationService registrationService;
    private StudentUser student;
    private Course introCourse;
    private Course advancedCourse;
    private CourseSection introSection;
    private CourseSection advancedSection;

    @BeforeEach
    void setUp() {
        persistence = new FakePersistence();
        PaymentService paymentService = new PaymentService(persistence);
        registrationService = new RegistrationService(paymentService, persistence);

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

        introCourse = new Course("CS46A", "Introduction to Programming", 3.0);
        advancedCourse = new Course("CS151", "Object-Oriented Design", 3.0);
        advancedCourse.addCoursePrerequisites(introCourse);

        introSection = new CourseSection(introCourse, "Dr. Intro", "SEC-001", "ABC123", 500.0, 30, 0);
        advancedSection = new CourseSection(advancedCourse, "Dr. Advanced", "SEC-002", "XYZ789", 750.0, 30, 0);
    }

    @Test
    void enrollReturnsTrueWhenStudentMeetsPrerequisitesSectionHasCapacityAndAccessCodeMatches() {
        student.addCompletedSection(introSection);

        boolean enrolled = registrationService.enroll(student, advancedSection, "XYZ789");

        assertTrue(enrolled);
        assertEquals(1, student.getEnrolledSections().size());
        assertTrue(student.getEnrolledSections().contains(advancedSection));
        assertEquals(1, advancedSection.getCurrentCapacity());
        assertEquals(750.0, student.getBalanceOwed(), 0.001);
        assertEquals(1, persistence.saveUsersCalls);
        assertEquals(1, persistence.saveSectionsCalls);
    }

    @Test
    void enrollReturnsFalseWhenAccessCodeDoesNotMatch() {
        student.addCompletedSection(introSection);

        boolean enrolled = registrationService.enroll(student, advancedSection, "WRONG");

        assertFalse(enrolled);
        assertTrue(student.getEnrolledSections().isEmpty());
        assertEquals(0, advancedSection.getCurrentCapacity());
        assertEquals(0.0, student.getBalanceOwed(), 0.001);
    }

    @Test
    void enrollReturnsFalseWhenStudentIsMissingPrerequisite() {
        boolean enrolled = registrationService.enroll(student, advancedSection, "XYZ789");

        assertFalse(enrolled);
        assertTrue(student.getEnrolledSections().isEmpty());
        assertEquals(0, advancedSection.getCurrentCapacity());
    }

    @Test
    void enrollReturnsFalseWhenStudentAlreadyCompletedSameCourse() {
        CourseSection completedAdvancedSection = new CourseSection(
                advancedCourse,
                "Dr. Other",
                "SEC-003",
                "DONE151",
                750.0,
                30,
                0
        );

        student.addCompletedSection(introSection);
        student.addCompletedSection(completedAdvancedSection);

        boolean enrolled = registrationService.enroll(student, advancedSection, "XYZ789");

        assertFalse(enrolled);
        assertFalse(student.getEnrolledSections().contains(advancedSection));
        assertEquals(0, advancedSection.getCurrentCapacity());
    }

    @Test
    void enrollReturnsFalseWhenStudentAlreadyEnrolledInSameCourse() {
        CourseSection existingAdvancedSection = new CourseSection(
                advancedCourse,
                "Dr. Existing",
                "SEC-004",
                "EXIST151",
                750.0,
                30,
                0
        );

        student.addCompletedSection(introSection);
        student.addEnrolledSection(existingAdvancedSection);

        boolean enrolled = registrationService.enroll(student, advancedSection, "XYZ789");

        assertFalse(enrolled);
        assertFalse(student.getEnrolledSections().contains(advancedSection));
        assertEquals(0, advancedSection.getCurrentCapacity());
    }

    @Test
    void enrollReturnsFalseWhenSectionIsFull() {
        CourseSection fullSection = new CourseSection(
                advancedCourse,
                "Dr. Full",
                "SEC-005",
                "FULL151",
                750.0,
                1,
                1
        );

        student.addCompletedSection(introSection);

        boolean enrolled = registrationService.enroll(student, fullSection, "FULL151");

        assertFalse(enrolled);
        assertFalse(student.getEnrolledSections().contains(fullSection));
        assertEquals(1, fullSection.getCurrentCapacity());
    }

    @Test
    void dropReturnsTrueWhenStudentIsEnrolledAndDecrementsCapacity() {
        student.addCompletedSection(introSection);
        assertTrue(registrationService.enroll(student, advancedSection, "XYZ789"));
        assertEquals(1, advancedSection.getCurrentCapacity());

        boolean dropped = registrationService.drop(student, advancedSection);

        assertTrue(dropped);
        assertFalse(student.getEnrolledSections().contains(advancedSection));
        assertEquals(0, advancedSection.getCurrentCapacity());
        assertEquals(0.0, student.getBalanceOwed(), 0.001);
    }

    @Test
    void dropReturnsFalseForNullArguments() {
        assertFalse(registrationService.drop(null, advancedSection));
        assertFalse(registrationService.drop(student, null));
    }

    @Test
    void dropReturnsFalseWhenStudentIsNotEnrolledInSection() {
        boolean dropped = registrationService.drop(student, advancedSection);

        assertFalse(dropped);
        assertEquals(0, advancedSection.getCurrentCapacity());
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
