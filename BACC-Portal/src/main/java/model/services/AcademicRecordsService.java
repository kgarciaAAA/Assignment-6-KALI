package services;

import academics.Course;
import java.util.List;
import users.StudentUser;

public class AcademicRecordsService {
    public boolean completeCourse(StudentUser student, Course completed) {
        if (!student.removeEnrolledCourse(completed)) {
            return false;   
        }
        student.addCompletedCourse(completed);
        return true;
    }

    public int calculateCompletedCredits(StudentUser student) {
        int total = 0;
        List<Course> studentCompletedList = student.getCompletedCourses();
        for (Course course : studentCompletedList){
            total += course.getUnitAmount();
        }

        return total;
    }

    //ADD A METHOD THAT CHECKS IF STUDENT CAN GRADUATE (would need updates and checks)

    //ADD A METHOD THAT RETURNS A LIST/DATASTRUCTURE CONTAINING A TRANSCRIPT?
}
