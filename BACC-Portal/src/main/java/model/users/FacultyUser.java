package users;

import academics.CourseSection;
import academics.Department;
import java.util.ArrayList;
import java.util.List;


public class FacultyUser extends User{
    private final Department department;
    private final List<CourseSection> sectionsTaught;

    public FacultyUser(String email, String userID, String password, String fullName, Department department, List<CourseSection> sectionsTaught){
        super(email, userID, password,fullName);
        this.department = department;
        this.sectionsTaught = new ArrayList<>(sectionsTaught);
    }

    //getters
    public Department getFacultyDepartment() {
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
