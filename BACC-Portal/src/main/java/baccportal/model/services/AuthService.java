package baccportal.model.services;

import baccportal.App;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.User;

public class AuthService {

    private final UserStorage userStorage;
    private final PasswordService passwordService;

    public AuthService(UserStorage userStorage, PasswordService passwordService) {
        this.userStorage = userStorage;
        this.passwordService = passwordService;
    }

    public User login(String userId, String password) {
        User user = userStorage.findUserById(userId);

        if (user == null)
            return null;
        
        if (!passwordService.verifyPassword(user, password))
            return null;
        
        return user;
    }

    public void logout() {
        App.getSession().clear();
    }
}
