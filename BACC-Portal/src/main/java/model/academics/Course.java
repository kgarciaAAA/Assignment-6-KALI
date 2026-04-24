package academics;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseID;
    private String courseName;
    private double unitAmount; 
    private String accessCode;
    private double price;
    private List<Course> coursePrerequisites;

    //default constructor
    public Course(String courseID, String courseName, double unitAmount, String accessCode, double price){
        this.courseID = courseID;
        this.courseName = courseName;
        this.unitAmount = unitAmount;
        this.accessCode = accessCode;
        this.price = price;
        this.coursePrerequisites = new ArrayList<Course>();
    }

    //getters
    public String getCourseID(){ return courseID; }

    public String getCourseName(){ return courseName; }

    public double getUnitAmount(){ return unitAmount; }

    public String getAccessCode(){ return accessCode; }

    public double getPrice() { return price; }

    public List<Course> getCoursePrerequisites(){ return coursePrerequisites; }
}
