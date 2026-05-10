package baccportal.model.academics;

public class CourseSection {
    private final Course course;
    private String instructorName;
    private final String sectionId;
    private final String accessCode;
    private final double price;
    private final int totalCapacity;
    private int currentCapacity;

    //constructor
    public CourseSection(Course course, String instructorName, String sectionId,
         String accessCode, double price, int totalCapacity, int currentCapacity) {
        this.course = course;
        this.instructorName = instructorName;
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

    public String getInstructorName() {
        return instructorName;
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
    
    public boolean isAtCapacity() {
        return currentCapacity == totalCapacity;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    //TODO: added exceptions, but could be potentially removed.  
    // added control to prevent section from being over capacity
    public void incrementCurrentCapacity(){
        if (isAtCapacity())
            throw new IllegalStateException("Section is at full capacity.");
        
        currentCapacity++;
    }
    
    //TODO: same as above. Could be potentially removed.
    // free up space in section
    public void decrementCurrentCapacity() {
        if (currentCapacity <= 0)
            throw new IllegalStateException("Section is at minimum capacity.");
        
        currentCapacity--;
    }

    // Used when the assigned faculty member is removed and the section needs to be reassigned.
    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}
