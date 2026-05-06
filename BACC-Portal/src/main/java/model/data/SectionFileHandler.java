package data;
import academics.Course;
import academics.CourseSection;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import storage.CourseStorage;

public class SectionFileHandler {
    public void readSectionsFromFile(CourseStorage courseStorage) throws IOException {
        try (Scanner snr = new Scanner(new File("sections.txt"))) {
            while (snr.hasNextLine()) {
                Course course = courseStorage.getCourse(snr.nextLine());
                String instructorName = snr.nextLine();
                String sectionId = snr.nextLine();
                String accessCode = snr.nextLine();
                double price = Double.parseDouble(snr.nextLine());
                int totalCapacity = Integer.parseInt(snr.nextLine());
                int currentCapacity = Integer.parseInt(snr.nextLine());
                snr.nextLine();
                if (course == null) {
                    continue;
                }
                courseStorage.addSection(new CourseSection(course, instructorName, sectionId, accessCode, price, totalCapacity, currentCapacity));
            }
        } catch (IOException e) {
            throw new IOException("Error reading: \"sections.txt\"");
        }
    }

    public void writeSectionsToFile(CourseStorage courseStorage) throws IOException{
        try (PrintWriter out = new PrintWriter(new File("sections.txt"))) {
            for (CourseSection section : courseStorage.getAllSections().values()) {
                out.println(section.getCourse().getCourseId());
                out.println(section.getInstructorName());
                out.println(section.getAccessCode());
                out.println(section.getPrice());
                out.println(section.getTotalCapacity());
                out.println(section.getCurrentCapacity());
                out.println("----------");
            }

        } catch (IOException e) {
            throw new IOException("Error writing to: \"sections.txt\"");
        }
    }
}
