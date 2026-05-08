package baccportal.model.services;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import java.util.List;
import baccportal.model.users.StudentUser;

public class AcademicRecordsService {
    public boolean completeSection(StudentUser student, CourseSection completed) {
        if (!student.removeEnrolledSection(completed)) {
            return false;   
        }
        student.addCompletedSection(completed);
        return true;
    }

    public double calculateCompletedCredits(StudentUser student) {
        double total = 0;
        List<CourseSection> completed = student.getCompletedSections();
        for (CourseSection section : completed){
            Course course = section.getCourse();
            total += course.getUnitAmount(); 
        }
        return total;
    }

    //ADD A METHOD THAT CHECKS IF STUDENT CAN GRADUATE (would need updates and checks)

    //ADD A METHOD THAT RETURNS A LIST/DATASTRUCTURE CONTAINING A TRANSCRIPT?
}
