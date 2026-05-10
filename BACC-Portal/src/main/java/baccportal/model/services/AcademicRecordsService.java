package baccportal.model.services;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import java.util.List;
import java.util.ArrayList;
import baccportal.model.storage.CourseStorage;
import baccportal.model.users.StudentUser;
import baccportal.model.storage.UserStorage;
import baccportal.model.data.PersistencePort;

public class AcademicRecordsService {
    private final UserStorage userStorage;
    private final CourseStorage courseStorage;
    private final PersistencePort persistence;

    public AcademicRecordsService(UserStorage userStorage, CourseStorage courseStorage, PersistencePort persistence) {
        this.userStorage = userStorage;
        this.courseStorage = courseStorage;
        this.persistence = persistence;
    }

    public boolean completeSection(StudentUser student, CourseSection completed) {
        boolean removed = student.removeEnrolledSection(completed);
        
        if (!removed)
            return false;   
        
        student.addCompletedSection(completed);
        // Free a seat now that the student is no longer occupying one in this section.
        completed.decrementCurrentCapacity();

        persistence.saveUsers();
        persistence.saveSections();

        return removed;
    }

    public CourseSection findEnrolledSection(StudentUser student, String sectionId) {
        for (CourseSection section : student.getEnrolledSections()) {
            // TODO: Override .equals? Or just make sectionID consistent?
            if (section.getSectionId().equalsIgnoreCase(sectionId)) 
                return section;
        }

        return null;
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

    // TODO: Uses UserStorage to get students list. Make this more efficient if possible. 
    public List<StudentUser> getStudentsInSection(String sectionId) {
        List<StudentUser> roster = new ArrayList<>();
        
        for (StudentUser student : userStorage.getStudentsList()) {
            for (CourseSection section : student.getEnrolledSections()) {
                if (section.getSectionId().equalsIgnoreCase(sectionId)) {
                    roster.add(student);
                    break;
                }
            }
        }

        return roster;
    }
    
    //ADD A METHOD THAT CHECKS IF STUDENT CAN GRADUATE (would need updates and checks)

    //ADD A METHOD THAT RETURNS A LIST/DATASTRUCTURE CONTAINING A TRANSCRIPT?
}
