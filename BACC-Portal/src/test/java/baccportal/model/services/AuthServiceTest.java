package baccportal.model.services;

import baccportal.model.academics.Department;
import baccportal.model.data.PersistencePort;
import baccportal.model.session.Session;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private UserStorage userStorage;
    private Session session;
    private AuthService authService;
    private StudentUser student;

    @BeforeEach
    void setUp() {
        userStorage = new UserStorage();
        session = new Session();
        authService = new AuthService(userStorage, new PasswordService(new FakePersistence()), session);

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
    }

    @Test
    void loginReturnsUserAndSetsSessionForValidCredentials() {
        userStorage.addStudentUser(student);

        User loggedIn = authService.login("S1001", "password123");

        assertSame(student, loggedIn);
        assertSame(student, session.getUser());
        assertTrue(session.isLoggedIn());
        assertSame(student, session.student());
    }

    @Test
    void loginIsCaseInsensitiveForUserId() {
        userStorage.addStudentUser(student);

        User loggedIn = authService.login("s1001", "password123");

        assertSame(student, loggedIn);
        assertSame(student, session.getUser());
    }

    @Test
    void loginReturnsNullWhenUserDoesNotExist() {
        User loggedIn = authService.login("missing", "password123");

        assertNull(loggedIn);
        assertFalse(session.isLoggedIn());
    }

    @Test
    void loginReturnsNullWhenPasswordIsWrong() {
        userStorage.addStudentUser(student);

        User loggedIn = authService.login("S1001", "wrong-password");

        assertNull(loggedIn);
        assertFalse(session.isLoggedIn());
        assertNull(session.getUser());
    }

    @Test
    void logoutClearsCurrentSession() {
        userStorage.addStudentUser(student);
        assertNotNull(authService.login("S1001", "password123"));

        authService.logout();

        assertNull(session.getUser());
        assertFalse(session.isLoggedIn());
    }

    private static class FakePersistence implements PersistencePort {
        @Override
        public void saveUsers() {
        }

        @Override
        public void saveCourses() {
        }

        @Override
        public void saveSections() {
        }
    }
}
