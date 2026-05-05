package storage;

import academics.Course;
import academics.CourseSection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CourseStorage {
    private final Map<String, Course> courses;
    private final Map<String, CourseSection> sections;

    public CourseStorage() {
        this.courses = new HashMap<>();
        this.sections = new HashMap<>();
    }

    public Map<String, Course> getAllCourses(){
        return Map.copyOf(courses);
    }

    public Map<String, CourseSection> getAllSections(){
        return Map.copyOf(sections);
    }

    public void addCourse(Course course) {
        courses.put(course.getCourseId(), course);
    }

    public void addSection(CourseSection courseSection) {
        sections.put(courseSection.getSectionId(), courseSection);
    }

    public Course getCourse(String courseId) {
        return courses.get(courseId);
    }

    public CourseSection getSection(String sectionId) {
        return sections.get(sectionId);
    }

    public List<CourseSection> getSectionsByCourse(Course course) {
        List<CourseSection> courseSections = new ArrayList<>();

        for (CourseSection section : sections.values()) {
            if (section.getCourse().equals(course)) {
                courseSections.add(section);
            }
        }

        return courseSections;
    }
}
