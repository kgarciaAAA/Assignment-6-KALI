package services;

import academics.Course;
import users.*;

public class AcademicRecordsService {
    public boolean completedCourse(StudentUser student, Course completed) {
        boolean removed = student.removeEnrolledCourse(completed);
        if (removed) {
            student.addCompletedCourse(completed);
            return true;
        } else {
            return false;
        }
    }
}
