package baccportal.model.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import baccportal.model.academics.Course;
import baccportal.model.academics.CourseSection;

public class CourseStorage {
    private final Map<String, Course> courses;
    private final Map<String, CourseSection> sections;

    public CourseStorage() {
        this.courses = new HashMap<>();
        this.sections = new HashMap<>();
    }

    public Map<String, Course> getAllCourses() {
        return Map.copyOf(courses);
    }

    public Map<String, CourseSection> getAllSections() {
        return Map.copyOf(sections);
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

        return List.copyOf(courseSections);
    }

    public void addCourse(Course course) {
        courses.put(course.getCourseId(), course);
    }

    public boolean removeCourseById(String courseId) {
        return courses.remove(courseId) != null;
    }

    public void addSection(CourseSection courseSection) {
        sections.put(courseSection.getSectionId(), courseSection);
    }

    public CourseSection removeSectionById(String sectionId) {
        return sections.remove(sectionId);
    }

    public List<CourseSection> removeSectionsByCourseId(String courseId) {
        List<CourseSection> removedSections = new ArrayList<>();
        List<String> sectionIdsToRemove = new ArrayList<>();

        for (CourseSection section : sections.values()) {
            if (section.getCourse().getCourseId().equalsIgnoreCase(courseId)) {
                sectionIdsToRemove.add(section.getSectionId());
            }
        }

        for (String sectionId : sectionIdsToRemove) {
            CourseSection removed = sections.remove(sectionId);

            if (removed != null) {
                removedSections.add(removed);
            }
        }

        return removedSections;
    }

    public void clearSections() {
        sections.clear();
    }
}
