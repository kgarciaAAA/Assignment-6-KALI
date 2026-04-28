package academics;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private final String courseId; //do we want to change the data type? maybe integer/int or make it an enumerated value?
    private final String courseName;
    private final double unitAmount; 
    private final String accessCode;
    private final double price;
    private final int totalCapacity;
    private final int currentCapacity;
    private final List<Course> coursePrerequisites;

    //default constructor
    public Course(String courseId, String courseName, double unitAmount, String accessCode, double price,
         int totalCapacity, int currentCapacity){
        this.courseId = courseId;
        this.courseName = courseName;
        this.unitAmount = unitAmount;
        this.accessCode = accessCode;
        this.price = price;
        this.totalCapacity = totalCapacity;
        this.currentCapacity = currentCapacity;
        this.coursePrerequisites = new ArrayList<>();
    }

    //getters
    public String getCourseId(){ return courseId; }

    public String getCourseName(){ return courseName; }

    public double getUnitAmount(){ return unitAmount; }

    public String getAccessCode(){ return accessCode; }

    public double getPrice() { return price; }

    public double getTotalCapacity() { return totalCapacity; }

    public double getCurrentCapacity() { return currentCapacity; }

    public List<Course> getCoursePrerequisites(){ return List.copyOf(coursePrerequisites); }
}
