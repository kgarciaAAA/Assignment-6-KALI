package academics;

public class Major {
    private final String majorName;
    private final Department department;

    //default constructor
    public Major(String majorName, Department department) {
        this.majorName = majorName; 
        this.department = department;
    }

    //getters
    public String getMajorName(){ return majorName; }

    public Department getDepartment(){ return department; }

    @Override
    public String toString() {
        return this.majorName + "\n" + this.department;
    }
}
