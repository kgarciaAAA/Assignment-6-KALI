package users;

import academics.CourseSection;
import academics.Department;
import java.util.ArrayList;
import java.util.List;


public class FacultyUser extends User{
    private final Department department;
    private final List<CourseSection> coursesTaught;

    public FacultyUser(String email, String userID, String password, String fullName, Department department){
        super(email, userID, password,fullName);
        this.department = department;
        this.coursesTaught = new ArrayList<>();
    }

    //getters
    public Department getFacultyDepartment() {
        return department;
    }

    public List<CourseSection> getCoursesTaught() {
        return List.copyOf(coursesTaught);
    }

    //controlled updates
    public void addCoursesTaught(CourseSection course) {
        coursesTaught.add(course);
    }

    public boolean removeCourseTaught(CourseSection course) {
        return coursesTaught.remove(course);
    }
}
