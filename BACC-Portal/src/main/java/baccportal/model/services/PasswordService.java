package baccportal.model.services;

import baccportal.model.data.PersistencePort;
import baccportal.model.users.User;
import baccportal.model.utilities.PasswordUtil;

public class PasswordService {

    private final PersistencePort persistence;

    public PasswordService(PersistencePort persistence) {
        this.persistence = persistence;
    }

    public String setPassword(User user, String password) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        user.setPassword(hashedPassword);

        persistence.saveUsers();
        return hashedPassword;
    }

    public boolean verifyPassword(User user, String password) {
        return PasswordUtil.verifyPassword(password, user.getPassword());
    }
}
