package baccportal.model.services;

import baccportal.model.session.Session;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.User;

public class AuthService {

    private final UserStorage userStorage;
    private final PasswordService passwordService;
    private final Session session;

    public AuthService(UserStorage userStorage, PasswordService passwordService, Session session) {
        this.userStorage = userStorage;
        this.passwordService = passwordService;
        this.session = session;
    }

    public User login(String userId, String password) {
        User user = userStorage.findUserById(userId);

        if (user == null)
            return null;

        if (!passwordService.verifyPassword(user, password))
            return null;

        session.setUser(user);
        return user;
    }

    public void logout() {
        session.clear();
    }
}
