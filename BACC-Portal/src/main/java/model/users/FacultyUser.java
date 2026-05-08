package users;

import java.util.ArrayList;
import java.util.List;

import academics.CourseSection;
import academics.Department;


public class FacultyUser extends User{
    private final Department department;
    private final List<CourseSection> sectionsTaught;

    public FacultyUser(String email, String userID, String password, String fullName, boolean isHashed, Department department){
        super(email, userID, password, fullName, isHashed);
        this.department = department;
        this.sectionsTaught = new ArrayList<>();
    }

    //getters
    public Department getDepartment() {
        return department;
    }

    public List<CourseSection> getSectionsTaught() {
        return List.copyOf(sectionsTaught);
    }

    //controlled updates
    public void addSectionTaught(CourseSection section) {
        sectionsTaught.add(section);
    }

    public boolean removeSectionTaught(CourseSection section) {
        return sectionsTaught.remove(section);
    }

    public int getNumSectionsTaught() {
        return sectionsTaught.size();
    }

}
