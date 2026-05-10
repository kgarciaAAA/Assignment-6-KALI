package baccportal.model.storage;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseStorageTest {
    private CourseStorage storage;
    private Course course;
    private CourseSection section;

    @BeforeEach
    void setUp() {
        storage = new CourseStorage();
        course = new Course("CS151", "Object-Oriented Design", 3.0);
        section = new CourseSection(course, "Dr. Test", "SEC-001", "ABC123", 750.0, 30, 0);
    }

    @Test
    void addAndFindCourseById() {
        storage.addCourse(course);

        assertSame(course, storage.getCourse("CS151"));
        assertEquals(1, storage.getAllCourses().size());
    }

    @Test
    void addAndFindSectionById() {
        storage.addSection(section);

        assertSame(section, storage.getSection("SEC-001"));
        assertEquals(1, storage.getAllSections().size());
    }

    @Test
    void returnedCourseAndSectionMapsCannotBeModifiedFromOutside() {
        storage.addCourse(course);
        storage.addSection(section);

        assertThrows(UnsupportedOperationException.class, () -> storage.getAllCourses().clear());
        assertThrows(UnsupportedOperationException.class, () -> storage.getAllSections().clear());

        assertEquals(1, storage.getAllCourses().size());
        assertEquals(1, storage.getAllSections().size());
    }

    @Test
    void removeCourseByIdReturnsTrueOnlyWhenCourseExists() {
        storage.addCourse(course);

        assertTrue(storage.removeCourseById("CS151"));
        assertFalse(storage.removeCourseById("CS151"));
        assertNull(storage.getCourse("CS151"));
    }

    @Test
    void removeSectionByIdReturnsRemovedSectionOrNull() {
        storage.addSection(section);

        assertSame(section, storage.removeSectionById("SEC-001"));
        assertNull(storage.removeSectionById("SEC-001"));
        assertNull(storage.getSection("SEC-001"));
    }

    @Test
    void getSectionsByCourseReturnsOnlyMatchingSections() {
        Course otherCourse = new Course("MATH30", "Calculus", 3.0);
        CourseSection matchingSection = new CourseSection(course, "Dr. Other", "SEC-002", "DEF456", 750.0, 30, 0);
        CourseSection nonMatchingSection = new CourseSection(otherCourse, "Dr. Math", "SEC-003", "GHI789", 500.0, 30, 0);

        storage.addSection(section);
        storage.addSection(matchingSection);
        storage.addSection(nonMatchingSection);

        assertEquals(2, storage.getSectionsByCourse(course).size());
        assertTrue(storage.getSectionsByCourse(course).contains(section));
        assertTrue(storage.getSectionsByCourse(course).contains(matchingSection));
        assertFalse(storage.getSectionsByCourse(course).contains(nonMatchingSection));
    }

    @Test
    void removeSectionsByCourseIdRemovesAllMatchingSectionsCaseInsensitive() {
        CourseSection secondSection = new CourseSection(course, "Dr. Other", "SEC-002", "DEF456", 750.0, 30, 0);
        Course otherCourse = new Course("MATH30", "Calculus", 3.0);
        CourseSection otherSection = new CourseSection(otherCourse, "Dr. Math", "SEC-003", "GHI789", 500.0, 30, 0);
        storage.addSection(section);
        storage.addSection(secondSection);
        storage.addSection(otherSection);

        assertEquals(2, storage.removeSectionsByCourseId("cs151").size());
        assertNull(storage.getSection("SEC-001"));
        assertNull(storage.getSection("SEC-002"));
        assertSame(otherSection, storage.getSection("SEC-003"));
    }

    @Test
    void clearSectionsRemovesEverySection() {
        storage.addSection(section);

        storage.clearSections();

        assertTrue(storage.getAllSections().isEmpty());
    }
}
