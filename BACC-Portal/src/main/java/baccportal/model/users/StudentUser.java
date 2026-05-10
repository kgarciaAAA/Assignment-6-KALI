package baccportal.model.users;

import java.util.ArrayList;
import java.util.List;

import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.utilities.Receipt;

public class StudentUser extends User {
    private final String major;
    private final Department department;
    private double balanceOwed;
    private final List<CourseSection> completedSections;
    private final List<CourseSection> enrolledSections;
    private final List<Receipt> transactionHistory;

    public StudentUser(String email, String userId, String password, String fullName,
                       boolean isHashed, String major, Department department, double balanceOwed) {
        super(email, userId, password, fullName, isHashed);
        this.major = major;
        this.department = department;
        this.balanceOwed = balanceOwed;
        this.completedSections = new ArrayList<>();
        this.enrolledSections = new ArrayList<>();
        this.transactionHistory = new ArrayList<>();
    }

    public String getMajor() {
        return major;
    }

    public Department getDepartment() {
        return department;
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
