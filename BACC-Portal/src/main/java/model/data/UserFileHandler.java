package data;

import academics.Department;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import storage.UserStorage;
import users.AdminUser;
import users.FacultyUser;
import users.StudentUser;

public class UserFileHandler {

    public void readUsersFromFile(UserStorage userStorage) throws IOException {
        try (Scanner snr = new Scanner(new File("users.txt"))) {

            while (snr.hasNextLine()) {
                String type = snr.nextLine();

                String email = snr.nextLine();
                String userId = snr.nextLine();
                String password = snr.nextLine();
                String fullName = snr.nextLine();

                switch (type) {
                    case "STUDENT" -> {
                        StudentUser student = new StudentUser(
                                email, userId, password, fullName,
                                null, // major (can expand later)
                                0
                        );
                        userStorage.addStudentUser(student);
                    }

                    case "FACULTY" -> {
                        Department dept = Department.stringToDepartment(snr.nextLine());

                        FacultyUser faculty = new FacultyUser(
                                email, userId, password, fullName, dept
                        );
                        userStorage.addFacultyUser(faculty);
                    }

                    case "ADMIN" -> {
                        AdminUser admin = new AdminUser(
                                email, userId, password, fullName
                        );
                        userStorage.addAdminUser(admin);
                    }
                }

                if (snr.hasNextLine()) {
                    snr.nextLine(); // skip ----------
                }
            }

        } catch (IOException e) {
            throw new IOException("Error reading users.txt");
        }
    }

    public void writeUsersToFile(UserStorage userStorage) throws IOException {
        try (PrintWriter out = new PrintWriter(new File("users.txt"))) {

            for (StudentUser student : userStorage.getStudentsList()) {
                out.println("STUDENT");
                out.println(student.getEmail());
                out.println(student.getUserId());
                out.println("password"); // you can store real password if needed
                out.println(student.getFullName());
                out.println("----------");
            }

            for (FacultyUser faculty : userStorage.getFacultyList()) {
                out.println("FACULTY");
                out.println(faculty.getEmail());
                out.println(faculty.getUserId());
                out.println("password");
                out.println(faculty.getFullName());
                out.println(faculty.getFacultyDepartment());
                out.println("----------");
            }

            for (AdminUser admin : userStorage.getAdminList()) {
                out.println("ADMIN");
                out.println(admin.getEmail());
                out.println(admin.getUserId());
                out.println("password");
                out.println(admin.getFullName());
                out.println("----------");
            }

        } catch (IOException e) {
            throw new IOException("Error writing users.txt");
        }
    }
}
