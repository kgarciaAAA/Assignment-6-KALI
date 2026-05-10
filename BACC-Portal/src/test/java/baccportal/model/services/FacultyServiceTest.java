package baccportal.model.services;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.users.FacultyUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FacultyServiceTest {
    private FacultyService facultyService;
    private FacultyUser faculty;
    private CourseSection section;

    @BeforeEach
    void setUp() {
        facultyService = new FacultyService();
        faculty = new FacultyUser(
                "faculty@sjsu.edu",
                "F1001",
                "password123",
                "Test Faculty",
                false,
                Department.COMPUTER_SCIENCE
        );
        section = new CourseSection(
                new Course("CS151", "Object-Oriented Design", 3.0),
                "Test Faculty",
                "SEC-001",
                "ABC123",
                750.0,
                30,
                0
        );
    }

    @Test
    void findSectionByIdReturnsMatchingSection() {
        faculty.addSectionTaught(section);

        CourseSection found = facultyService.findSectionById(faculty, "SEC-001");

        assertSame(section, found);
    }

    @Test
    void findSectionByIdReturnsNullWhenMissing() {
        assertNull(facultyService.findSectionById(faculty, "missing"));
    }

    @Test
    void teachesSectionReturnsTrueWhenFacultyTeachesSection() {
        faculty.addSectionTaught(section);

        assertTrue(facultyService.teachesSection(faculty, "SEC-001"));
        assertFalse(facultyService.teachesSection(faculty, "missing"));
    }

    @Test
    void removeSectionByIdRemovesMatchingSection() {
        faculty.addSectionTaught(section);

        boolean removed = facultyService.removeSectionById(faculty, "SEC-001");

        assertTrue(removed);
        assertFalse(faculty.getSectionsTaught().contains(section));
    }

    @Test
    void removeSectionByIdReturnsFalseWhenMissing() {
        boolean removed = facultyService.removeSectionById(faculty, "missing");

        assertFalse(removed);
    }

    @Test
    void getSectionsTaughtByFacultyReturnsFacultySections() {
        faculty.addSectionTaught(section);

        assertEquals(1, facultyService.getSectionsTaughtByFaculty(faculty).size());
        assertTrue(facultyService.getSectionsTaughtByFaculty(faculty).contains(section));
    }
}
