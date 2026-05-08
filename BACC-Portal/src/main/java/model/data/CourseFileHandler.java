package data;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import academics.Course;
import storage.CourseStorage;

public class CourseFileHandler {
    
    public void readCoursesFromFile(CourseStorage storage) throws IOException {
        try (Scanner snr = new Scanner(new File("courses.txt"))){
            Map<String, String[]> allCoursesPrereqs = new HashMap<>();

            while (snr.hasNextLine()) {
                String courseId = snr.nextLine();
                String courseName = snr.nextLine();
                double unitAmount = Double.parseDouble(snr.nextLine());
                String coursePrereqs = snr.nextLine();
                snr.nextLine();
                if (!coursePrereqs.equalsIgnoreCase("NONE")) {
                    String[] preReq = coursePrereqs.split(",");
                    allCoursesPrereqs.put(courseId, preReq);
                }

                storage.addCourse(new Course(courseId, courseName, unitAmount));
            }
            renderCoursePrereqs(storage, allCoursesPrereqs);
        } catch (IOException e) {
            throw new IOException("Error reading: \"courses.txt\"");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing unit amount: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing course prerequisites: " + e.getMessage());
        }
    }

    public void renderCoursePrereqs(CourseStorage storage, Map<String, String[]> allCoursesPrereqs){
        for (String courseId : allCoursesPrereqs.keySet()) {
            Course course = storage.getCourse(courseId);
            String[] coursePrereqStrings = allCoursesPrereqs.get(courseId);

            for (String prereqId : coursePrereqStrings) {
                Course preRequisite = storage.getCourse(prereqId.trim());
                if (preRequisite != null) {
                    course.addCoursePrerequisites(preRequisite);
                } else {
                    System.out.println("Could not find Course Prerequisite with courseId: " + courseId); //remove later!
                }
            }
        }
    }
 
    public void writeCoursesToFile(CourseStorage storage) throws IOException {
        try (PrintWriter out = new PrintWriter(new File("courses.txt"))) {
            for (Course course : storage.getAllCourses().values()) {
                out.println(course.getCourseId());
                out.println(course.getCourseName());
                out.println(course.getUnitAmount());
                List<Course> preReqList = course.getCoursePrerequisites();
                if (!preReqList.isEmpty()) {
                    for (int i = 0; i < preReqList.size(); ++i) {
                        Course preReq = preReqList.get(i);
                        out.print(preReq.getCourseId());
                        if (i < preReqList.size() - 1) {
                            out.print(", ");
                        }
                    }
                    out.println();
                } else {
                    out.println("NONE");
                }
                out.println("----------");
            }
        } catch (IOException e) {
            throw new IOException("Error writing to: \"courses.txt\"");
        }
    }
    
}
