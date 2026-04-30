package academics;

import java.util.ArrayList;
import java.util.List;

public class MajorCatalog {
    private final Major major;
    private final List<Course> requiredCourses;
    private final List<Course> electiveCourses;

    //default constructor
    public MajorCatalog (Major major){
        this.major = major;
        this.requiredCourses = new ArrayList<>();
        this.electiveCourses = new ArrayList<>();
    }

    //getters
    public String getMajorName() {
        return major.getMajorName();
    }

    //methods
    public List<Course> getRequiredCourses() {
        return List.copyOf(requiredCourses);
    }

    public List<Course> getElectiveCourses() {
        return List.copyOf(electiveCourses);
    }

    public boolean isRequired(Course course) {
        return requiredCourses.contains(course);
    }

    public boolean isElective(Course course) {
        return electiveCourses.contains(course);
    }

    public Course getCourseByID(String courseID) {
        for (Course reqCourse : requiredCourses) {
            if (reqCourse.getCourseId().equals(courseID)){
                return reqCourse;
            }
        }

        for (Course elective : electiveCourses) {
            if (elective.getCourseId().equals(courseID)){
                return elective;
            }
        }

        return null;
    }
}