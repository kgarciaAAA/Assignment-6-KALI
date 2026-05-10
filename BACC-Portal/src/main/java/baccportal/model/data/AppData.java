package baccportal.model.data;

import java.io.IOException;

import baccportal.model.services.AcademicRecordsService;
import baccportal.model.services.AdminService;
import baccportal.model.services.AuthService;
import baccportal.model.services.FacultyService;
import baccportal.model.services.PasswordService;
import baccportal.model.services.PaymentService;
import baccportal.model.services.RegistrationService;
import baccportal.model.session.Session;
import baccportal.model.storage.CourseStorage;
import baccportal.model.storage.MajorStorage;
import baccportal.model.storage.UserStorage;

public class AppData implements PersistencePort {

    private final UserStorage userStorage;
    private final CourseStorage courseStorage;
    private final MajorStorage majorStorage;

    private final UserFileHandler userFileHandler;
    private final CourseFileHandler courseFileHandler;
    private final SectionFileHandler sectionFileHandler;
    private final MajorFileHandler majorFileHandler;

    private final AdminService adminService;
    private final AcademicRecordsService academicRecordsService;
    private final FacultyService facultyService;
    private final PasswordService passwordService;
    private final AuthService authService;
    private final PaymentService paymentService;
    private final RegistrationService registrationService;

    public AppData(Session session) {
        this.userStorage = new UserStorage();
        this.courseStorage = new CourseStorage();
        this.majorStorage = new MajorStorage();

        this.userFileHandler = new UserFileHandler();
        this.courseFileHandler = new CourseFileHandler();
        this.sectionFileHandler = new SectionFileHandler();
        this.majorFileHandler = new MajorFileHandler();
        
        // Services that impact persisted state, get this as a dependency.
        this.paymentService = new PaymentService(this);
        this.registrationService = new RegistrationService(paymentService, this);
        this.academicRecordsService = new AcademicRecordsService(userStorage, this);
        this.facultyService = new FacultyService();
        this.passwordService = new PasswordService(this);
        this.authService = new AuthService(userStorage, passwordService, session);
        this.adminService = new AdminService(userStorage, courseStorage, this);
    }

    public void load() {
        try {
            courseFileHandler.readCoursesFromFile(courseStorage);
            System.out.println("Courses file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load courses.txt: " + e.getMessage());
        }

        try {
            sectionFileHandler.readSectionsFromFile(courseStorage);
            System.out.println("Sections file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load sections.txt: " + e.getMessage());
        }

        loadUsersOnly();

        System.out.println("Courses loaded: " + courseStorage.getAllCourses().size());
        System.out.println("Sections loaded: " + courseStorage.getAllSections().size());
        System.out.println("Students loaded: " + userStorage.getStudentsList().size());
        System.out.println("Faculty loaded: " + userStorage.getFacultyList().size());
        System.out.println("Admins loaded: " + userStorage.getAdminList().size());
    }

    private void loadUsersOnly() {
        try {
            userFileHandler.readAdminUsersFromFile(userStorage);
            System.out.println("Admin file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load admin.txt: " + e.getMessage());
        }

        try {
            userFileHandler.readStudentUsersFromFile(userStorage, courseStorage);
            System.out.println("Students file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load students.txt: " + e.getMessage());
        }

        try {
            userFileHandler.readFacultyUsersFromFile(userStorage, courseStorage);
            System.out.println("Faculty file loaded.");
        } catch (Exception e) {
            System.out.println("Could not load faculty.txt: " + e.getMessage());
        }
    }

    public void reloadSections() {
        try {
            courseStorage.clearSections();
            sectionFileHandler.readSectionsFromFile(courseStorage);
            System.out.println("Sections reloaded.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reloading sections.");
        }
    }

    public void reloadUsers() {
        try {
            userStorage.clearUsers();
            loadUsersOnly();
            System.out.println("Users reloaded.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reloading users.");
        }
    }

    @Override
    public void saveUsers() {
        try {
            userFileHandler.writeAdminUsersToFile(userStorage);
            userFileHandler.writeStudentUserToFile(userStorage);
            userFileHandler.writeFacultyUsersToFile(userStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving user data to text files.");
        }
    }

    @Override
    public void saveCourses() {
        try {
            courseFileHandler.writeCoursesToFile(courseStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving course data to text files.");
        }
    }

    @Override
    public void saveSections() {
        try {
            sectionFileHandler.writeSectionsToFile(courseStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving section data to text files.");
        }
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public CourseStorage getCourseStorage() {
        return courseStorage;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public AcademicRecordsService getAcademicRecordsService() {
        return academicRecordsService;
    }

    public FacultyService getFacultyService() {
        return facultyService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public PasswordService getPasswordService() {
        return passwordService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }

    public MajorStorage getMajorStorage() {
        return majorStorage;
    }

    public MajorFileHandler getMajorFileHandler() {
        return majorFileHandler;
    }
}
