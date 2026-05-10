package baccportal.model.services;

import java.util.List;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;
import baccportal.model.data.PersistencePort;
import baccportal.model.storage.CourseStorage;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;

public class AdminService {

    private static final String unassigned_faculty = "TBD";

    private final UserStorage userStorage;
    private final CourseStorage courseStorage;
    private final PersistencePort persistence;

    public AdminService(UserStorage userStorage, CourseStorage courseStorage,
                        PersistencePort persistence) {
        this.userStorage = userStorage;
        this.courseStorage = courseStorage;
        this.persistence = persistence;
    }

    public boolean addNewStudent(StudentUser studentUser) {
        if (userStorage.exists(studentUser)) {
            return false;
        }

        userStorage.addStudentUser(studentUser);
        persistence.saveUsers();
        return true;
    }

    public boolean addNewFaculty(FacultyUser facultyUser) {
        if (userStorage.exists(facultyUser)) {
            return false;
        }

        userStorage.addFacultyUser(facultyUser);
        persistence.saveUsers();
        return true;
    }

    public boolean addAdminUser(AdminUser adminUser) {
        if (userStorage.exists(adminUser)) {
            return false;
        }

        userStorage.addAdminUser(adminUser);
        persistence.saveUsers();
        return true;
    }

    public boolean deleteStudent(String userId) {
        StudentUser student = userStorage.findStudentUserById(userId);
        if (student == null)
            return false;

        for (CourseSection section : student.getEnrolledSections())
            section.decrementCurrentCapacity();
    

        boolean removed = userStorage.removeStudentById(userId);

        if (removed) {
            persistence.saveUsers();
            persistence.saveSections();
        }
        return removed;
    }

    public boolean deleteFaculty(String userId) {
        FacultyUser faculty = userStorage.findFacultyUserById(userId);
        if (faculty == null)
            return false;

        unassignSectionsTaughtBy(faculty);

        boolean removed = userStorage.removeFacultyById(userId);

        if (removed) {
            persistence.saveUsers();
            persistence.saveSections();
        }
        return removed;
    }

    public boolean deleteAdmin(String userId) {
        boolean removed = userStorage.removeAdminById(userId);

        if (removed)
            persistence.saveUsers();
        
        return removed;
    }

    public boolean addCourse(Course course) {
        if (courseStorage.getCourse(course.getCourseId()) != null)
            return false;

        courseStorage.addCourse(course);

        persistence.saveCourses();
        return true;
    }

    // Pulls every section that belongs to this course out of storage first.
    // Used to clean up the references the user objects still hold to those sections.
    public boolean deleteCourse(String courseId) {
        if (courseStorage.getCourse(courseId) == null)
            return false;
        
        List<CourseSection> removedSections = courseStorage.removeSectionsByCourseId(courseId);

        for (CourseSection section : removedSections)
            cleanUpSectionReferences(section);
        
        boolean removed = courseStorage.removeCourseById(courseId);

        if (removed || !removedSections.isEmpty()) {
            persistence.saveCourses();
            persistence.saveSections();
            persistence.saveUsers();
        }

        return removed;
    }

    public boolean addSection(CourseSection section, FacultyUser instructor) {
        if (instructor == null)
            return false;
        
        if (courseStorage.getSection(section.getSectionId()) != null) {
            return false;
        }

        courseStorage.addSection(section);
        instructor.addSectionTaught(section);
        persistence.saveSections();
        persistence.saveUsers();
        return true;
    }

    public boolean reassignSection(CourseSection section, FacultyUser instructor) {
        if (instructor == null)
            return false;

        if (section.getInstructorName().equals(instructor.getFullName()))
            return false;

        userStorage.detachSectionFromAllFaculty(section);
        
        section.setInstructorName(instructor.getFullName());
        instructor.addSectionTaught(section);
        persistence.saveSections();
        persistence.saveUsers();
        return true;
    }

    public boolean deleteSection(String sectionId) {
        CourseSection removed = courseStorage.removeSectionById(sectionId);

        if (removed == null)
            return false;

        cleanUpSectionReferences(removed);
        persistence.saveSections();
        persistence.saveUsers();
        return true;
    }

    // Helper method to clean up references to a section when it is deleted.
    // TODO: Wipes from memory, including students and faculty references.
    private void cleanUpSectionReferences(CourseSection section) {
        userStorage.detachSectionFromAllStudents(section);
        userStorage.detachSectionFromAllFaculty(section);
    }

    // Now, when the faculty is removed, we unassign all the sections they taught.
    // TODO: Coould just delete the sections instead. 
    private void unassignSectionsTaughtBy(FacultyUser faculty) {
        for (CourseSection section : faculty.getSectionsTaught()) {
            section.setInstructorName(unassigned_faculty);
        }
    }
}
