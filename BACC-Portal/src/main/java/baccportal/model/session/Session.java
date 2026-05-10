package baccportal.model.session;

import java.util.Optional;

import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;

/**
 * Holds the logged-in user for the desktop session. 
 * Typed accessors avoid repeated instanceof chains.
 */
public final class Session {

    private User currentUser;

    public User getUser() {
        return currentUser;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void clear() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Optional<StudentUser> student() {
        if (currentUser instanceof StudentUser s) {
            return Optional.of(s);
        }
        return Optional.empty();
    }

    public Optional<FacultyUser> faculty() {
        if (currentUser instanceof FacultyUser f) {
            return Optional.of(f);
        }
        return Optional.empty();
    }

    public Optional<AdminUser> admin() {
        if (currentUser instanceof AdminUser a) {
            return Optional.of(a);
        }
        return Optional.empty();
    }

    // Returns the FXML root name for the dashboard that matches this user's role.
    // Contains all of the instanceof checks for the user type.
    public String rootFxmlAfterLogin(User user) {
        if (user instanceof StudentUser) {
            return "studentDashboard";
        }
        if (user instanceof FacultyUser) {
            return "faculty";
        }
        if (user instanceof AdminUser) {
            return "admin";
        }
        throw new IllegalStateException("Unknown user type for login routing.");
    }
}
