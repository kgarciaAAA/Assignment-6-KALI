package baccportal.model.data;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import baccportal.model.academics.CourseSection;
import baccportal.model.academics.Department;
import baccportal.model.academics.Major;
import baccportal.model.storage.CourseStorage;
import baccportal.model.storage.UserStorage;
import baccportal.model.users.AdminUser;
import baccportal.model.users.FacultyUser;
import baccportal.model.users.StudentUser;
import baccportal.model.utilities.Receipt;

public class UserFileHandler {
    public void readStudentUsersFromFile(UserStorage userStorage, CourseStorage courseStorage) throws IOException{
        try (Scanner snr = new Scanner(new File("txtfiles/students.txt"))) {
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
                StudentUser user = new StudentUser(email, userId, password, fullName, true, major, balanceOwed);
                
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
                userStorage.addStudentUser(user);
                snr.nextLine();
            }
        } catch (IOException e) {
            throw new IOException("Error reading: \"txtfiles/students.txt\""); 
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error parsing numerical value: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing course prerequisites: " + e.getMessage());
        }
    }

    public void readFacultyUsersFromFile(UserStorage userStorage, CourseStorage courseStorage) throws IOException {
        try (Scanner snr = new Scanner(new File("txtfiles/faculty.txt"))){
            while (snr.hasNextLine()) {
                String email = snr.nextLine();
                String userId = snr.nextLine();
                String password = snr.nextLine();
                String fullName = snr.nextLine();
                Department department = Department.stringToDepartment(snr.nextLine());
                FacultyUser user = new FacultyUser(email, userId, password, fullName, true, department);

                String sectionsTaughtString = snr.nextLine();
                String[] sectionsId = sectionsTaughtString.split(",");
                if (!sectionsId[0].trim().equalsIgnoreCase("NONE")) {
                    for (String id : sectionsId) {
                        CourseSection section = courseStorage.getSection(id.trim());
                        if (section != null) {
                            user.addSectionTaught(section);
                        }
                    }
                }
                userStorage.addFacultyUser(user);
                snr.nextLine();
            }
        } catch (IOException e) {
            throw new IOException("Error reading: \"txtfiles/faculty.txt\"");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("File data: " + e.getMessage());
        }
    }

    public void readAdminUsersFromFile(UserStorage userStorage) throws IOException {
        try (Scanner snr = new Scanner(new File("txtfiles/admin.txt"))) {
            while (snr.hasNextLine()) {
                String email = snr.nextLine();
                String userId = snr.nextLine();
                String password = snr.nextLine();
                String fullName = snr.nextLine();
                snr.nextLine();
                AdminUser user = new AdminUser(email, userId, password, fullName, true);
                userStorage.addAdminUser(user);
            }
        } catch (IOException e) {
            throw new IOException("Error reading: \"txtfiles/admin.txt\"");
        }
    }

    public void writeStudentUserToFile(UserStorage userStorage) throws IOException {
        try (PrintWriter out = new PrintWriter(new File("txtfiles/students.txt"))) {
            List<StudentUser> studentsList = userStorage.getStudentsList();

            for (StudentUser user : studentsList) {
                out.println(user.getEmail());
                out.println(user.getUserId());
                out.println(user.getPassword());
                out.println(user.getFullName());

                if (user.getMajor() == null) {
                    out.println("Undeclared");
                    out.println("NONE");
                } else {
                    out.println(user.getMajor().getMajorName());
                    out.println(user.getMajor().getDepartment());
                }

                out.println(user.getBalanceOwed());

                List<CourseSection> completedSections = user.getCompletedSections();
                if (completedSections.isEmpty()) {
                    out.print("NONE");
                } else {
                    for (int i = 0; i < completedSections.size(); i++) {
                        out.print(completedSections.get(i).getSectionId());
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
                    for (int i = 0; i < enrolledSections.size(); i++) {
                        out.print(enrolledSections.get(i).getSectionId());
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
                    for (int i = 0; i < transactionHistory.size(); i++) {
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
            throw new IOException("Error writing to: \"txtfiles/students.txt\"");
        }
    }

    public void writeFacultyUsersToFile(UserStorage userStorage) throws IOException {
        try (PrintWriter out = new PrintWriter(new File("txtfiles/faculty.txt"))){
            List<FacultyUser> facultyList = userStorage.getFacultyList();
            for (FacultyUser user : facultyList) {
                out.println(user.getEmail());
                out.println(user.getUserId());
                out.println(user.getPassword());
                out.println(user.getFullName());
                out.println(user.getDepartment());
                List<CourseSection> sectionsTaught = user.getSectionsTaught();
                if (!sectionsTaught.isEmpty()) {
                    for (int i = 0; i < sectionsTaught.size(); ++i) {
                        out.print(sectionsTaught.get(i).getSectionId());
                        if (i < sectionsTaught.size() - 1) {
                            out.print(", ");
                        }
                    }
                } else {
                    out.print("NONE");
                }
                out.println();
                out.println("----------");
            }

        } catch (IOException e) {
            throw new IOException("Error writing to: \"txtfiles/faculty.txt\"");
        }
    }

    public void writeAdminUsersToFile(UserStorage userStorage) throws IOException{
        try (PrintWriter out = new PrintWriter(new File("txtfiles/admin.txt"))) {
            List<AdminUser> adminList = userStorage.getAdminList();
            for (AdminUser user : adminList) {
                out.println(user.getEmail());
                out.println(user.getUserId());
                out.println(user.getPassword());
                out.println(user.getFullName());
                out.println("----------");
            }
        } catch (IOException e) {
            throw new IOException("Error writing to: \"txtfiles/admin.txt\"");
        }
    }
}
