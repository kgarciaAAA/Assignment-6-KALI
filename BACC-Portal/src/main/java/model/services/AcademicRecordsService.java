package services;

import academics.Course;
import academics.CourseSection;
import java.util.List;
import users.StudentUser;

public class AcademicRecordsService {
    private static final int REQUIRED_CREDITS = 120;

    public boolean completeSection(StudentUser student, CourseSection completed) {
        if (!student.removeEnrolledSection(completed)) {
            return false;   
        }
        student.addCompletedSection(completed);
        return true;
    }

    public int calculateCompletedCredits(StudentUser student) {
        int total = 0;
        List<CourseSection> completed = student.getCompletedSections();

        for (CourseSection section : completed){
            Course course = section.getCourse();
            total += course.getUnitAmount();
        }
        return total;
    }

    // A METHOD THAT CHECKS IF STUDENT CAN GRADUATE (would need updates and checks)
    public boolean canGraduate(StudentUser student, int requiredCredits){
        return calculateCompletedCredits(student) >= REQUIRED_CREDITS;
    }

    // A METHOD THAT RETURNS A LIST/DATASTRUCTURE CONTAINING A TRANSCRIPT
    public List<CourseSection> getTranscript(StudentUser student) {
        return student.getCompletedSections();
    }

}



