package academics;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseID; //do we want to change the data type? maybe integer/int or make it an enumerated value?
    private String courseName;
    private double unitAmount; 
    private String accessCode;
    private double price;
    private int totalCapacity;
    private int currentCapacity;
    private List<Course> coursePrerequisites;

    //default constructor
    public Course(String courseID, String courseName, double unitAmount, String accessCode, double price,
         int totalCapacity, int currentCapacity){
        this.courseID = courseID;
        this.courseName = courseName;
        this.unitAmount = unitAmount;
        this.accessCode = accessCode;
        this.price = price;
        this.totalCapacity = totalCapacity;
        this.currentCapacity = currentCapacity;
        this.coursePrerequisites = new ArrayList<>();
    }

    //getters
    public String getCourseID(){ return courseID; }

    public String getCourseName(){ return courseName; }

    public double getUnitAmount(){ return unitAmount; }

    public String getAccessCode(){ return accessCode; }

    public double getPrice() { return price; }

    public double getTotalCapacity() { return totalCapacity; }

    public double getCurrentCapacity() { return currentCapacity; }

    public List<Course> getCoursePrerequisites(){ return List.copyOf(coursePrerequisites); }
}
