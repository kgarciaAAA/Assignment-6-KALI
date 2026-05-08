package users;

import utilities.PasswordUtil;

public abstract class User {
    protected String email;
    protected String userId;
    protected String password;
    protected String fullName;

    //constructor
    public User(String email, String userId, String password, String fullName, boolean isHashed){
        this.email = email;
        this.userId = userId;
        if (isHashed) {
            this.password = password;
        } else {
            this.password = PasswordUtil.hashPassword(password);
        }
        this.fullName = fullName;

    }

    //getters
    public String getEmail(){return this.email;}
    public String getUserId(){return this.userId;}
    public String getPassword() {return this.password;}
    public String getFullName(){return this.fullName;}

    //methods
    public boolean comparePassword(String password) {
        return PasswordUtil.verifyPassword(password, this.password);
    }

    @Override
    public boolean equals(Object o) { 
        if (this == o) {
            return true;
        } else if (!(o instanceof User)) {
            return false;
        } else {
            User u = (User) o;
            return userId != null && userId.equalsIgnoreCase(u.userId);
        }
    }

    @Override
    public int hashCode() {
        return userId == null ? 0 : userId.toLowerCase().hashCode();
    }
}