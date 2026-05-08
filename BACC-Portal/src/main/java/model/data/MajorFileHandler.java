package data;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import academics.Course;
import academics.Department;
import academics.Major;
import academics.MajorCatalog;
import storage.CourseStorage;
import storage.MajorStorage;

public class MajorFileHandler {
    public void readMajorsFromFile(MajorStorage majorStorage, CourseStorage courseStorage) throws IOException {
        try (Scanner snr = new Scanner(new File("majors.txt"))) {
            while (snr.hasNextLine()) {
                String majorName = snr.nextLine();
                Department department = Department.stringToDepartment(snr.nextLine());
                Major major = new Major(majorName, department);

                String requiredCoursesString = snr.nextLine();
                String[] requiredCoursesIds = requiredCoursesString.split(",");
                List<Course> requiredCourses = new ArrayList<>();
                if (!requiredCoursesIds[0].trim().equalsIgnoreCase("NONE")) {
                    for (String id : requiredCoursesIds) {
                        Course reqCourse = courseStorage.getCourse(id.trim());
                        if (reqCourse != null) {
                            requiredCourses.add(reqCourse);
                        } else {
                            System.out.println("Could not find Required Course: " + id);
                        }
                    }
                }

                String electiveCoursesString = snr.nextLine();
                String[] electiveCoursesIds = electiveCoursesString.split(",");
                List<Course> electiveCourses = new ArrayList<>();
                if (!electiveCoursesIds[0].trim().equalsIgnoreCase("NONE")) {
                    for (String id : electiveCoursesIds) {
                        Course elective = courseStorage.getCourse(id.trim());
                        if (elective != null) {
                            electiveCourses.add(elective);
                        } else {
                            System.out.println("Could not find Elective Course: " + id);
                        }
                    }
                }
                snr.nextLine();
                majorStorage.addMajorCatalog(new MajorCatalog(major, requiredCourses, electiveCourses));
            }
        } catch (IOException e) {
            throw new IOException("Error reading: \"majors.txt\"");
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error parsing numerical value: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing course prerequisites: " + e.getMessage());
        }
    }

    public void writeMajorsToFile(MajorStorage majorStorage) throws IOException {
        try (PrintWriter out = new PrintWriter(new File("majors.txt"))) {
            for (MajorCatalog majorCatalog : majorStorage.getAllMajorCatalogs().values()){
                Major major = majorCatalog.getMajor();
                out.println(major.getMajorName());
                out.println(major.getDepartment());
                List<Course> requiredCourses = majorCatalog.getRequiredCourses();

                if (requiredCourses.isEmpty()) {
                    out.println("NONE");
                } else {
                    for (int i = 0; i < requiredCourses.size(); ++i) {
                        out.print(requiredCourses.get(i).getCourseId());
                        if (i < requiredCourses.size() - 1) {
                            out.print(", ");
                        }
                    }
                    out.println();
                }

                List<Course> electiveCourses = majorCatalog.getElectiveCourses();
                if (electiveCourses.isEmpty()) {
                    out.println("NONE");
                } else {
                    for (int i = 0; i < electiveCourses.size(); ++i) {
                        out.print(electiveCourses.get(i).getCourseId());
                        if (i < electiveCourses.size() - 1) {
                            out.print(", ");
                        }
                    }
                    out.println();
                }
                out.println("----------");
            }
        } catch (IOException e) {
            throw new IOException("Error writing to: \"majors.txt\"");
        }
    }
}
