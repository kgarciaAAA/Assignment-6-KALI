package users;

import academics.Course;
import academics.Major;
import java.util.ArrayList;
import java.util.List;
import utilities.Receipt;

public class StudentUser extends User{ //implement cloneable?
    private Major major;
    private final List<Course> completedCourses;
    private final List<Course> enrolledCourses;
    // private final List<Course> plannedCourses; 
    private double balanceOwed;
    private final List<Receipt> transactionHistory;
    //Should we add a List<PaymentInfo> savedPaymentMethods to store user payments?


    public StudentUser(String email, String userID, String password, String fullName, Major major, double balanceOwed){
        super(email, userID, password,fullName);
        this.major = major;
        this.completedCourses = new ArrayList<>();
        this.enrolledCourses = new ArrayList<>();
        // this.plannedCourses = new ArrayList<>();
        this.balanceOwed = balanceOwed;
        this.transactionHistory = new ArrayList<>();
        
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

    public double getBalanceOwed(){
        return balanceOwed;
    }

    public List<Receipt> getTransactionHistory() {
        return List.copyOf(transactionHistory);
    }

    // controlled updates
    public void addCompletedCourse(Course course) {
        completedCourses.add(course);
    }

    public void addEnrolledCourse(Course course) {
        enrolledCourses.add(course);
    }

    public void addTransaction(Receipt receipt) {
        transactionHistory.add(receipt);
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

    public void adjustBalanceOwed(double amount) {
        this.balanceOwed += amount;
    }
}
