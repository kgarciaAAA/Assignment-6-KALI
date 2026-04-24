package users;

import academics.Course;
import academics.Major;
import java.util.ArrayList;
import java.util.List;

public class StudentUser extends User{ //implement cloneable?
    private Major major;
    private final List<Course> completedCourses;
    private final List<Course> enrolledCourses;
    // private final List<Course> plannedCourses; 
    private double balance;


    public StudentUser(String email, String userID, String password, String fullName, Major major, double balance){
        super(email, userID, password,fullName);
        this.major = major;
        this.completedCourses = new ArrayList<>();
        this.enrolledCourses = new ArrayList<>();
        // this.plannedCourses = new ArrayList<>();
        this.balance = balance;
        
    }
    //getters
    public Major getMajor() { 
        return major; 
    }

    public List<Course> getCompletedCourses() { 
        return List.copyOf(completedCourses); 
    }

    public List<Course> getEnrolledCourses() { 
        return List.copyOf(enrolledCourses); 
    }

    // public List<Course> getPlannedCourses(){
    //     return List.copyOf(plannedCourses);
    // }

    public double getBalance(){
        return balance;
    }

    // controlled updates
    public void addCompletedCourse(Course course) {
        completedCourses.add(course);
    }

    public void addEnrolledCourse(Course course) {
        enrolledCourses.add(course);
    }

    // public void addPlannedCourse(Course course) {
    //     plannedCourses.add(course);
    // }

    public boolean removeEnrolledCourse(Course course) {
        return enrolledCourses.remove(course);
    }

    // public boolean removePlannedCourse(Course course) {
    //     return plannedCourses.remove(course);
    // }

    public void adjustBalance(double amount) {
        this.balance += amount;
    }
}
