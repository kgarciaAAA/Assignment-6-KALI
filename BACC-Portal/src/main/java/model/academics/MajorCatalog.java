package academics;

import java.util.ArrayList;
import java.util.List;

public class MajorCatalog {
    private Major major;
    private List<Course> requiredCourses;
    private List<Course> electiveCourses;

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

    public List<Course> getRequiredCourses() {
        return List.copyOf(requiredCourses);
    }

    public List<Course> getElectiveCourses() {
        return List.copyOf(electiveCourses);
    }

    //controlled updates
    public void addRequiredCourses(Course required) {
        requiredCourses.add(required);
    }

    public void addElectiveCourses(Course elective) {
        electiveCourses.add(elective);
    }
}