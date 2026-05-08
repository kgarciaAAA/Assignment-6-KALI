package baccportal.model.services;

import java.util.List;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.users.StudentUser;

public class RegistrationService {

    public boolean enroll(StudentUser user, CourseSection section) {
        boolean takenOrEnrolled = canEnroll(user, section);
        if (!takenOrEnrolled) {
            return false;
        }
        user.addEnrolledSection(section);
        section.incrementCurrentCapacity();
        return true;
    }

    //enroll helper methods
    private boolean canEnroll(StudentUser user, CourseSection section) {
        List<CourseSection> userCompleted = user.getCompletedSections();
        List<CourseSection> userEnrolled = user.getEnrolledSections();
        Course courseToCheck = section.getCourse();

        for (CourseSection completed : userCompleted) { // checks if user has already taken the course
            if (courseToCheck.getCourseId().equals(completed.getCourse().getCourseId())) { // this shouldn't violate law of demeter, but can be simplified if we override .equals
                return false;
            }
        }

        for (CourseSection currentlyEnrolled : userEnrolled) { // ensures the user isn't already enrolled
            if (courseToCheck.getCourseId().equals(currentlyEnrolled.getCourse().getCourseId())) { // this shouldn't violate law of demeter, but can be simplified if we override .equals
                return false;
            }
        }

        return hasPrerequisites(userCompleted, section);
    }

    private boolean hasPrerequisites(List<CourseSection> userCompleted, CourseSection section) {
        List<Course> coursePreReq = section.getCourse().getCoursePrerequisites();
        for (Course preReq : coursePreReq) {
            boolean found = false; //a boolean used to see if the current preRequisite is found in userCompleted
            for (CourseSection completed : userCompleted) {
                if (completed.getCourse().getCourseId().equals(preReq.getCourseId())) { // this shouldn't violate law of demeter, but can be simplified if we override .equals
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return hasCapacity(section);
    }

    private boolean hasCapacity(CourseSection section) {
        return section.getCurrentCapacity() < section.getTotalCapacity();
    }


}
