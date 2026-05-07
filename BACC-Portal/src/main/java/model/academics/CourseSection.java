package academics;

public class CourseSection {
    private final Course course;
    private final String instructorName;
    private final String sectionId;
    private final String accessCode;
    private final double price;
    private final int totalCapacity;
    private int currentCapacity;

    //default constructor
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

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    //controlled updates
    public void incrementCurrentCapacity(){
        currentCapacity++;
    }
}
