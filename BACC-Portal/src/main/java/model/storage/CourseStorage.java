package storage;

import academics.Course;
import academics.CourseSection;
import java.util.HashMap;
import java.util.Map;


public class CourseStorage {
    private final Map<String, Course> courses;
    private final Map<String, CourseSection> sections;

    public CourseStorage() {
        this.courses = new HashMap<>();
        this.sections = new HashMap<>();
    }

    public void addCourses(Course course) {
        courses.put(course.getCourseId(), course);
    }

    public void addSections(CourseSection courseSection) {
        sections.put(courseSection.getSectionId(), courseSection);
    }

    public Course getCourses(String courseId) {
        return courses.get(courseId);
    }

    public CourseSection getSections(String sectionId) {
        return sections.get(sectionId);
    }
}
