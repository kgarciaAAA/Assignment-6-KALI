package academics;

import users.FacultyUser;

public class CourseSection {
    private final Course course;
    private final FacultyUser instructor;
    private final String sectionId;
    private final String accessCode;
    private final double price;
    private final int totalCapacity;
    private int currentCapacity;

    //default constructor
    public CourseSection(Course course, FacultyUser instructor, String sectionId,
         String accessCode, double price, int totalCapacity, int currentCapacity) {
        this.course = course;
        this.instructor = instructor;
        this.sectionId = sectionId;
        this.accessCode = accessCode;
        this.price = price;
        this.totalCapacity = totalCapacity;
        this.currentCapacity = currentCapacity;
    }

    //getters
    public Course getCourse() {
        return course;
    }

    public FacultyUser getInstructor() {
        return instructor;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalCapacity() {
        return totalCapacity;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    //controlled updates
    public void incrementCurrentCapacity(){
        currentCapacity++;
    }
}
