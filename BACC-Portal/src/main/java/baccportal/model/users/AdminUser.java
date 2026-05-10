package baccportal.model.users;

public class AdminUser extends User{

    //constructor
    public AdminUser(String email, String userId, String password, String fullName, boolean isHashed) {
        super(email, userId, password, fullName, isHashed);
    }

    @Override
    public String rootFxmlAfterLogin() {
        return "admin";
    }
}
