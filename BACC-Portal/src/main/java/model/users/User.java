package users;

public abstract class User {
    protected String email;
    protected String userId;
    protected String password;
    protected String fullName;

    public User(String email, String userId, String password, String fullName){
        this.email = email;
        this.userId = userId;
        this.password = password;
        this.fullName = fullName;
    }

    //getters
    public String getEmail(){return this.email;}
    public String getUserId(){return this.userId;}
    public String getFullName(){return this.fullName;}

    //methods
    public boolean comparePassword(String password) {
        return this.password.equals(password);
    }
}