package services;

import academics.CourseSection;
import java.util.List;
import users.FacultyUser;

public class FacultyService {
    public CourseSection findSectionById(FacultyUser user, String sectionId) {

        List<CourseSection> taughtList = user.getSectionsTaught();

        for (CourseSection section : taughtList) {
            if (section.getSectionId().equals(sectionId)) {
                return section;
            }
        }
        return null;
    }

    public boolean removeSectionById(FacultyUser user, String sectionId) {
        CourseSection section = findSectionById(user, sectionId);
        if (section == null) {
            return false;
        }
        return user.removeSectionTaught(section);
    }
}
