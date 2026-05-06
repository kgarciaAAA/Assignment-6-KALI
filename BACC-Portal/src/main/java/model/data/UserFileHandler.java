package data;
import users.*;
import utilities.Receipt;
import storage.CourseStorage;
import storage.UserStorage;
import academics.Major;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import academics.CourseSection;
import java.util.List;
import utilities.*;

import academics.Department;

public class UserFileHandler {
    public void getStudentUsersFromFile(UserStorage userStorage, CourseStorage courseStorage) throws IOException{
        try (Scanner snr = new Scanner(new File("students.txt"))) {
            while (snr.hasNextLine()) {
                String email = snr.nextLine();
                String userId = snr.nextLine();
                String password = snr.nextLine();
                String fullName = snr.nextLine();
                String majorName = snr.nextLine();
                Department department = Department.stringToDepartment(snr.nextLine());
                Major major = new Major(majorName, department);
                double balanceOwed = Double.parseDouble(snr.nextLine());
                String completedSectionsString = snr.nextLine();
                String[] completedIds = completedSectionsString.split(",");
                String enrolledSectionsString = snr.nextLine();
                String[] enrolledIds = enrolledSectionsString.split(",");
                String receiptInfoString = snr.nextLine();
                String[] receiptInfoArr = receiptInfoString.split(",");
                StudentUser user = new StudentUser(email, userId, password, fullName, major, balanceOwed);
                
                if (!completedIds[0].trim().equalsIgnoreCase("NONE")) {
                    for (String id : completedIds) {
                        CourseSection section = courseStorage.getSection(id.trim());
                        if (section != null) {
                            user.addCompletedSection(section);
                        }
                    }
                }

                if (!enrolledIds[0].trim().equalsIgnoreCase("NONE")) {
                    for (String id : enrolledIds) {
                        CourseSection section = courseStorage.getSection(id.trim());
                        if (section != null) {
                            user.addEnrolledSection(section);
                        }
                    }
                }
                if (!receiptInfoArr[0].trim().equalsIgnoreCase("NONE")) {
                    for (String receiptInfo : receiptInfoArr) {
                        String[] receiptArr = receiptInfo.split("-");
                        int receiptId = Integer.parseInt(receiptArr[0].trim());
                        double totalPaid = Double.parseDouble(receiptArr[1].trim());
                        double remainingBalance = Double.parseDouble(receiptArr[2].trim());
                        user.addTransaction(new Receipt(receiptId, totalPaid, remainingBalance));
                    }
                }
                snr.nextLine();
            }
        } catch (IOException e) {
            throw new IOException("Error reading: \"students.txt\""); 
        }
    }

    public void getFacultyUsersFromFile() throws IOException {

    }

    public void getAdminUsersFromFile() throws IOException {

    }

    public void printStudentUserToFile(UserStorage userStorage, CourseStorage courseStorage) throws IOException{
        try (PrintWriter out = new PrintWriter(new File("students.txt"))) {
            List<StudentUser> studentsList = userStorage.getStudentsList();
            for (StudentUser user : studentsList) {
                out.println(user.getEmail());
                out.println(user.getUserId());
                out.println(user.getPassword());
                out.println(user.getMajor());

                List<CourseSection> completedSections = user.getCompletedSections();
                if (completedSections.isEmpty()) {
                    out.print("NONE");
                } else {
                    for (int i = 0; i < completedSections.size(); ++i) {
                        out.print(completedSections.get(i).getCourse().getCourseName());
                        if (i < completedSections.size() - 1) {
                            out.print(", ");
                        }
                    }
                }
                out.println();

                List<CourseSection> enrolledSections = user.getEnrolledSections();
                if (enrolledSections.isEmpty()) {
                    out.print("NONE");
                } else {
                    for (int i = 0; i < enrolledSections.size(); ++i) {
                        out.print(enrolledSections.get(i).getCourse().getCourseName());
                        if (i < enrolledSections.size() - 1) {
                            out.print(", ");
                        }
                    }
                }
                out.println();

                List<Receipt> transactionHistory = user.getTransactionHistory();
                if (transactionHistory.isEmpty()) {
                    out.print("NONE");
                } else {
                    for (int i = 0; i < transactionHistory.size(); ++i) {
                        out.print(transactionHistory.get(i));
                        if (i < transactionHistory.size() - 1) {
                            out.print(", ");
                        }
                    }
                }
                out.println();
                out.println("----------");
            }
        } catch (IOException e) {
            throw new IOException("Error writing to: \"students.txt\"");
        }
    }

    public void printFacultyUsersToFile() throws IOException{}

    public void printAdminUsersToFile() throws IOException{}
}
