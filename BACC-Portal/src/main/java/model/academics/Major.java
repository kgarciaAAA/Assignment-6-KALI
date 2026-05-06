package academics;

import java.util.Objects;

public class Major {
    private final String majorName;
    private final Department department;

    // Constructor
    public Major(String majorName, Department department) {
        if (majorName == null || department == null) {
            throw new IllegalArgumentException("Major name and department cannot be null.");
        }

        this.majorName = majorName;
        this.department = department;
    }

    // Getters
    public String getMajorName() {
        return majorName;
    }

    public Department getDepartment() {
        return department;
    }

    /**
     * Two majors are equal if they have the same name and department
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Major)) return false;

        Major other = (Major) o;
        return majorName.equals(other.majorName) &&
                department == other.department;
    }

    /**
     * Hash code consistent with equals
     */
    @Override
    public int hashCode() {
        return Objects.hash(majorName, department);
    }

    @Override
    public String toString() {
        return majorName + " (" + department + ")";
    }
}
