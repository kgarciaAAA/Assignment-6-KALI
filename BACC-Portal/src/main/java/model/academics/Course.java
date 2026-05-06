package academics;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private final String courseId; //do we want to change the data type? maybe integer/int or make it an enumerated value?
    private final String courseName;
    private final double unitAmount; 
    private final List<Course> coursePrerequisites;

    //default constructor
    public Course(String courseId, String courseName, double unitAmount){
        this.courseId = courseId;
        this.courseName = courseName;
        this.unitAmount = unitAmount;
        this.coursePrerequisites = new ArrayList<>();
    }

    //getters
    public String getCourseId(){ return courseId; }

    public String getCourseName(){ return courseName; }

    public double getUnitAmount(){ return unitAmount; }

    public void addCoursePrerequisites(Course course) {
        coursePrerequisites.add(course);
    }

    public List<Course> getCoursePrerequisites(){ return List.copyOf(coursePrerequisites); }

    @Override
    public boolean equals(Object o) {
        if (o == this){
          return true; 
        } else if (!(o instanceof Course)) {
            return false;
        } else {
            Course other = (Course) o;
            return this.courseId.equals(other.courseId);
        }
    }

    @Override
    public int hashCode() {
        return courseId == null ? 0 : courseId.toLowerCase().hashCode();
    }
}
