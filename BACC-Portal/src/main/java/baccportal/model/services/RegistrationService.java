package baccportal.model.services;

import java.util.List;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.data.PersistencePort;
import baccportal.model.users.StudentUser;

public class RegistrationService {

    private final PersistencePort persistence;
    private final PaymentService paymentService;
    public RegistrationService(PaymentService paymentService, PersistencePort persistence) {
        this.persistence = persistence;
        this.paymentService = paymentService;
    }

    public boolean enroll(StudentUser user, CourseSection section, String accessCode) {
        if (user == null || section == null)
            return false;
    
        if (!section.getAccessCode().equals(accessCode))
            return false;

        if (!canEnroll(user, section))
            return false;
    

        user.addEnrolledSection(section);
        section.incrementCurrentCapacity();
        
        paymentService.processEnrollmentFee(user, section.getPrice());

        persistence.saveSections();
        return true;
    }

    public boolean drop(StudentUser user, CourseSection section) {
        if (user == null || section == null)
            return false;
        
        boolean removed = user.removeEnrolledSection(section);

        if (removed) {
            section.decrementCurrentCapacity();
            paymentService.processRefund(user, section.getPrice());

            persistence.saveSections();
        }

        return removed;
    }

    //enroll helper methods
    private boolean canEnroll(StudentUser user, CourseSection section) {
        List<CourseSection> userCompleted = user.getCompletedSections();
        List<CourseSection> userEnrolled = user.getEnrolledSections();
        Course courseToCheck = section.getCourse();

        for (CourseSection completed : userCompleted) { // checks if user has already taken the course
            if (courseToCheck.equals(completed.getCourse())) 
                return false;
        }

        for (CourseSection currentlyEnrolled : userEnrolled) { // ensures the user isn't already enrolled
            if (courseToCheck.equals(currentlyEnrolled.getCourse()))
                return false;
        }

        return hasPrerequisites(userCompleted, section);
    }

    private boolean hasPrerequisites(List<CourseSection> userCompleted, CourseSection section) {
        List<Course> coursePreReq = section.getCourse().getCoursePrerequisites();
        for (Course preReq : coursePreReq) {
            boolean found = false; //a boolean used to see if the current preRequisite is found in userCompleted
            for (CourseSection completed : userCompleted) {
                if (completed.getCourse().equals(preReq)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return hasCapacity(section);
    }

    private boolean hasCapacity(CourseSection section) {
        return section.getCurrentCapacity() < section.getTotalCapacity();
    }
}
