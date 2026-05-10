package baccportal.model;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.utilities.Receipt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void coursePrerequisitesListCannotBeModifiedFromOutside() {
        Course cs46a = new Course("CS46A", "Introduction to Programming", 3.0);
        Course cs151 = new Course("CS151", "Object-Oriented Design", 3.0);
        cs151.addCoursePrerequisites(cs46a);

        assertThrows(UnsupportedOperationException.class,
                () -> cs151.getCoursePrerequisites().add(new Course("CS47", "Another Course", 3.0)));

        assertEquals(1, cs151.getCoursePrerequisites().size());
        assertTrue(cs151.getCoursePrerequisites().contains(cs46a));
    }

    @Test
    void studentListsCannotBeModifiedFromOutside() {
        StudentUser student = new StudentUser(
                "student@sjsu.edu",
                "S1001",
                "password123",
                "Test Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                0.0
        );

        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Dr. Test", "SEC-001", "ABC123", 750.0, 30, 0);
        Receipt receipt = new Receipt(100.0, 200.0);

        student.addCompletedSection(section);
        student.addEnrolledSection(section);
        student.addTransaction(receipt);

        assertThrows(UnsupportedOperationException.class, () -> student.getCompletedSections().clear());
        assertThrows(UnsupportedOperationException.class, () -> student.getEnrolledSections().clear());
        assertThrows(UnsupportedOperationException.class, () -> student.getTransactionHistory().clear());

        assertEquals(1, student.getCompletedSections().size());
        assertEquals(1, student.getEnrolledSections().size());
        assertEquals(1, student.getTransactionHistory().size());
    }

    @Test
    void facultySectionsListCannotBeModifiedFromOutside() {
        FacultyUser faculty = new FacultyUser(
                "faculty@sjsu.edu",
                "F1001",
                "password123",
                "Test Faculty",
                false,
                Department.COMPUTER_SCIENCE
        );

        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Test Faculty", "SEC-001", "ABC123", 750.0, 30, 0);
        faculty.addSectionTaught(section);

        assertThrows(UnsupportedOperationException.class, () -> faculty.getSectionsTaught().clear());
        assertEquals(1, faculty.getSectionsTaught().size());
    }

    @Test
    void studentCanRemoveEnrolledSection() {
        StudentUser student = new StudentUser(
                "student@sjsu.edu",
                "S1001",
                "password123",
                "Test Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                0.0
        );

        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Dr. Test", "SEC-001", "ABC123", 750.0, 30, 0);

        student.addEnrolledSection(section);

        boolean removed = student.removeEnrolledSection(section);

        assertTrue(removed);
        assertFalse(student.getEnrolledSections().contains(section));
    }

    @Test
    void studentCanRemoveCompletedSection() {
        StudentUser student = new StudentUser(
                "student@sjsu.edu",
                "S1001",
                "password123",
                "Test Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                0.0
        );

        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Dr. Test", "SEC-001", "ABC123", 750.0, 30, 0);

        student.addCompletedSection(section);

        boolean removed = student.removeCompletedSection(section);

        assertTrue(removed);
        assertFalse(student.getCompletedSections().contains(section));
    }

    @Test
    void coursesAreEqualWhenCourseIdsMatchIgnoringCase() {
        Course first = new Course("CS151", "Object-Oriented Design", 3.0);
        Course second = new Course("cs151", "Different Name Same ID", 4.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void courseSectionCapacityCannotGoBelowZero() {
        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Dr. Test", "SEC-001", "ABC123", 750.0, 30, 0);

        assertThrows(IllegalStateException.class, section::decrementCurrentCapacity);
        assertEquals(0, section.getCurrentCapacity());
    }

    @Test
    void courseSectionCapacityCannotGoAboveTotalCapacity() {
        Course course = new Course("CS151", "Object-Oriented Design", 3.0);
        CourseSection section = new CourseSection(course, "Dr. Test", "SEC-001", "ABC123", 750.0, 1, 1);

        assertThrows(IllegalStateException.class, section::incrementCurrentCapacity);
        assertEquals(1, section.getCurrentCapacity());
    }
}
