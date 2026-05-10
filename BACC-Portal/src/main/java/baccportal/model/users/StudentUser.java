package baccportal.model.users;

import java.util.ArrayList;
import java.util.List;

import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Major;
import baccportal.model.utilities.Receipt;

public class StudentUser extends User {
    private final Major major;
    private double balanceOwed;
    private final List<CourseSection> completedSections;
    private final List<CourseSection> enrolledSections;
    private final List<Receipt> transactionHistory;

    public StudentUser(String email, String userId, String password, String fullName,
                       boolean isHashed, Major major, double balanceOwed) {
        super(email, userId, password, fullName, isHashed);
        this.major = major;
        this.balanceOwed = balanceOwed;
        this.completedSections = new ArrayList<>();
        this.enrolledSections = new ArrayList<>();
        this.transactionHistory = new ArrayList<>();
    }

    public Major getMajor() {
        return major;
    }

    public double getBalanceOwed() {
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

    public void addCompletedSection(CourseSection section) {
        completedSections.add(section);
    }

    // TODO: added exception, but could be potentially removed.
    public void addEnrolledSection(CourseSection section) {
        if (enrolledSections.contains(section))
            throw new IllegalStateException("Student is already enrolled in this section.");
        
        enrolledSections.add(section);
    }

    public void addTransaction(Receipt receipt) {
        transactionHistory.add(receipt);
    }

    public boolean removeEnrolledSection(CourseSection section) {
        return enrolledSections.remove(section);
    }

    public boolean removeCompletedSection(CourseSection section) {
        return completedSections.remove(section);
    }

    // TODO: added exceptions, but could be potentially removed.
    public void incrementBalanceOwed(double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount must be non-negative.");
        
        balanceOwed += amount;
    }

    // TODO: added exceptions, but could be potentially removed.
    public void decrementBalanceOwed(double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount must be non-negative.");
        
        balanceOwed -= amount;
    }

    @Override
    public String rootFxmlAfterLogin() {
        return "studentDashboard";
    }
}
