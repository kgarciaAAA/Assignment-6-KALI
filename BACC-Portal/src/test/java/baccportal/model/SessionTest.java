package baccportal.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import baccportal.model.academics.Department;
import baccportal.model.session.Session;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;

class SessionTest {

    @Test
    void newSessionStartsLoggedOut() {
        Session session = new Session();

        assertFalse(session.isLoggedIn());
        assertNull(session.getUser());
        assertNull(session.student());
        assertNull(session.faculty());
        assertNull(session.admin());
    }

    @Test
    void studentReturnsCurrentUserWhenCurrentUserIsStudent() {
        Session session = new Session();

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

        session.setUser(student);

        assertTrue(session.isLoggedIn());
        assertSame(student, session.getUser());
        assertSame(student, session.student());
        assertNull(session.faculty());
        assertNull(session.admin());
    }

    @Test
    void facultyReturnsCurrentUserWhenCurrentUserIsFaculty() {
        Session session = new Session();

        FacultyUser faculty = new FacultyUser(
                "faculty@sjsu.edu",
                "F1001",
                "password123",
                "Test Faculty",
                false,
                Department.COMPUTER_SCIENCE
        );

        session.setUser(faculty);

        assertTrue(session.isLoggedIn());
        assertSame(faculty, session.getUser());
        assertSame(faculty, session.faculty());
        assertNull(session.student());
        assertNull(session.admin());
    }

    @Test
    void adminReturnsCurrentUserWhenCurrentUserIsAdmin() {
        Session session = new Session();

        AdminUser admin = new AdminUser(
                "admin@sjsu.edu",
                "A1001",
                "password123",
                "Test Admin",
                false
        );

        session.setUser(admin);

        assertTrue(session.isLoggedIn());
        assertSame(admin, session.getUser());
        assertSame(admin, session.admin());
        assertNull(session.student());
        assertNull(session.faculty());
    }

    @Test
    void clearRemovesCurrentUser() {
        Session session = new Session();

        AdminUser admin = new AdminUser(
                "admin@sjsu.edu",
                "A1001",
                "password123",
                "Test Admin",
                false
        );

        session.setUser(admin);
        session.clear();

        assertFalse(session.isLoggedIn());
        assertNull(session.getUser());
        assertNull(session.student());
        assertNull(session.faculty());
        assertNull(session.admin());
    }
}