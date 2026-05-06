package users;

import academics.CourseSection;
import academics.Major;
import java.util.ArrayList;
import java.util.List;
import utilities.Receipt;

public class StudentUser extends User{ //implement cloneable?
    private final Major major;
    private double balanceOwed;
    private final List<CourseSection> completedSections;
    private final List<CourseSection> enrolledSections;
    private final List<Receipt> transactionHistory;
    //Should we add a List<PaymentInfo> savedPaymentMethods to store user payments?

    public StudentUser(String email, String userId, String password, String fullName, Major major, double balanceOwed){
        super(email, userId, password,fullName);
        this.major = major;
        this.balanceOwed = balanceOwed;
        this.completedSections = new ArrayList<>(); //dont know if we want to make a reference outside of user and inject it or make a new ArrayList in user then update it using methods.
        this.enrolledSections = new ArrayList<>();
        this.transactionHistory = new ArrayList<>();
    }

    //getters
    public Major getMajor() { 
        return major; 
    }

    public double getBalanceOwed(){
        return balanceOwed;
    }

    public List<CourseSection> getCompletedSections() { 
        return List.copyOf(completedSections); 
    }

    public List<CourseSection> getEnrolledSections() { 
        return List.copyOf(enrolledSections); 
    }

    public List<Receipt> getTransactionHistory() {
        return List.copyOf(transactionHistory);
    }

    // controlled updates
    public void addCompletedSection(CourseSection section) {
        completedSections.add(section);
    }

    public void addEnrolledSection(CourseSection section) {
        enrolledSections.add(section);
    }

    public void addTransaction(Receipt receipt) {
        transactionHistory.add(receipt);
    }

    public boolean removeEnrolledSection(CourseSection course) {
        return enrolledSections.remove(course);
    }

    public void adjustBalanceOwed(double amount) {
        this.balanceOwed += amount;
    }
}
