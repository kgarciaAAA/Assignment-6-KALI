package baccportal.model.session;

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

    public StudentUser student() {
        return currentUser instanceof StudentUser s ? s : null;
    }

    public FacultyUser faculty() {
        return currentUser instanceof FacultyUser f ? f : null;
    }

    public AdminUser admin() {
        return currentUser instanceof AdminUser a ? a : null;
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
