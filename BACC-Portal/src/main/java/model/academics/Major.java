package academics;

public class Major {
    private final String majorName;
    private final String department;

    //default constructor
    public Major(String majorName, String department) {
        this.majorName = majorName; 
        this.department = department;
    }

    //getters
    public String getMajorName(){ return majorName; }

    public String getDepartment(){ return department; }
}
