package users;

public abstract class User {
    protected String email;
    protected String userID;
    protected String password;
    protected String fullName;

    public User(String email, String userID, String password, String fullName){
        this.email = email;
        this.userID = userID;
        this.password = password;
        this.fullName = fullName;
    }

    //getters
    public String getEmail(){return this.email;}
    public String getUserId(){return this.userID;}
    public String getFullName(){return this.fullName;}
}