package users;

public class AdminUser extends User{
    public AdminUser(String email, String userId, String password, String fullName, boolean isHashed) {
        super(email, userId, password, fullName, isHashed);
    }
}
