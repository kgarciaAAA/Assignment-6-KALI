package services;

import academics.Course;
import java.util.List;
import users.StudentUser;

public class RegistrationService {

    public boolean enroll(StudentUser user, Course course) { //SHOULD add schedule conflicts and maybe Valid for Major checks
        boolean takenOrEnrolled = canEnroll(user, course);
        if (!takenOrEnrolled) {
            return false;
        }
        user.addEnrolledCourse(course);
        return true;
    }

    //enroll helper methods
    private boolean canEnroll(StudentUser user, Course course) {
        List<Course> userCompleted = user.getCompletedCourses();
        List<Course> userEnrolled = user.getEnrolledCourses();
        if (userCompleted.contains(course) || userEnrolled.contains(course)){
            return false;
        } 
        return hasPrerequisites(userCompleted, course);
    }

    private boolean hasPrerequisites(List<Course> userCompleted, Course course) {
        List<Course> coursePreReq = course.getCoursePrerequisites();
        for (Course preReq : coursePreReq) {
            if (!userCompleted.contains(preReq)){
                return false;
            }
        }
        return hasCapacity(course);
    }

    private boolean hasCapacity(Course course){
        return course.getCurrentCapacity() < course.getTotalCapacity();
    }


}
