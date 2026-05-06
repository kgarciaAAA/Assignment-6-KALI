package academics;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private final String courseId;
    private final String courseName;
    private final double unitAmount;
    private final List<Course> coursePrerequisites;

    // Constructor initializes course with no prerequisites
    public Course(String courseId, String courseName, double unitAmount){
        this.courseId = courseId;
        this.courseName = courseName;
        this.unitAmount = unitAmount;
        this.coursePrerequisites = new ArrayList<>();
    }

    // Getters
    public String getCourseId(){ return courseId; }

    public String getCourseName(){ return courseName; }

    public double getUnitAmount(){ return unitAmount; }

    /**
     * Adds a prerequisite course if it is not null and not already present
     * @param course prerequisite course
     * @return true if added, false otherwise
     */
    public boolean addCoursePrerequisites(Course course) {
        if (course == null || coursePrerequisites.contains(course)) {
            return false;
        }
        return coursePrerequisites.add(course);
    }

    /**
     * Returns an unmodifiable copy of prerequisite courses
     */
    public List<Course> getCoursePrerequisites(){
        return List.copyOf(coursePrerequisites);
    }

    /**
     * Courses are considered equal if they share the same courseId
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        Course other = (Course) o;
        return courseId.equals(other.courseId);
    }

    /**
     * Hash code based on courseId (must match equals logic)
     */
    @Override
    public int hashCode() {
        return courseId == null ? 0 : courseId.hashCode();
    }
}
