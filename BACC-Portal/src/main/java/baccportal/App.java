package baccportal;

import java.io.IOException;

import baccportal.controllers.AdminController;
import baccportal.controllers.AdminCoursesController;
import baccportal.controllers.AdminFacultyController;
import baccportal.controllers.AdminSectionsController;
import baccportal.controllers.AdminStudentsController;
import baccportal.controllers.ChangePasswordController;
import baccportal.controllers.FacultyDashboardController;
import baccportal.controllers.FacultySectionsController;
import baccportal.controllers.LoginController;
import baccportal.controllers.StudentCompletedCoursesController;
import baccportal.controllers.StudentDashboardController;
import baccportal.controllers.StudentEnrolledCoursesController;
import baccportal.controllers.StudentManageCoursesController;
import baccportal.controllers.StudentPaymentController;
import baccportal.controllers.StudentTransactionsController;
import baccportal.model.data.AppData;
import baccportal.model.session.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static ControllerFactory controllerFactory;

    @Override
    public void start(Stage stage) throws IOException {
        Session session = new Session();
        AppData appData = new AppData(session);
        appData.load();

        controllerFactory = buildControllerFactory(appData, session);

        scene = new Scene(loadFXML("login"), 800, 600);

        stage.setTitle("BAY AREA COMMUNITY COLLEGE PORTAL - KALI");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        stage.show();
    }

    // Single composition root: every controller declares what it needs, and we
    // build it here. Adding or removing a controller dependency only changes
    // this method and the controller itself, nothing else in the codebase.
    private static ControllerFactory buildControllerFactory(AppData appData, Session session) {
        ControllerFactory f = new ControllerFactory();

        f.bind(LoginController.class,
                () -> new LoginController(appData.getAuthService()));

        f.bind(AdminController.class,
                () -> new AdminController(
                        session,
                        appData.getAuthService(),
                        appData.getUserStorage()));

        f.bind(AdminCoursesController.class,
                () -> new AdminCoursesController(
                        appData.getCourseStorage(),
                        appData.getAdminService()));

        f.bind(AdminFacultyController.class,
                () -> new AdminFacultyController(
                        appData.getAdminService(),
                        appData.getUserStorage()));

        f.bind(AdminSectionsController.class,
                () -> new AdminSectionsController(
                        appData.getCourseStorage(),
                        appData.getAdminService(),
                        appData.getUserStorage()));

        f.bind(AdminStudentsController.class,
                () -> new AdminStudentsController(
                        appData,
                        appData.getAdminService()));

        f.bind(ChangePasswordController.class,
                () -> new ChangePasswordController(
                        session,
                        appData.getPasswordService()));

        f.bind(FacultyDashboardController.class,
                () -> new FacultyDashboardController(
                        session,
                        appData.getAuthService()));

        f.bind(FacultySectionsController.class,
                () -> new FacultySectionsController(
                        session,
                        appData,
                        appData.getFacultyService(),
                        appData.getAcademicRecordsService(),
                        appData.getRegistrationService()));

        f.bind(StudentDashboardController.class,
                () -> new StudentDashboardController(
                        session,
                        appData.getAuthService()));

        f.bind(StudentManageCoursesController.class,
                () -> new StudentManageCoursesController(
                        session,
                        appData,
                        appData.getCourseStorage(),
                        appData.getRegistrationService()));

        f.bind(StudentEnrolledCoursesController.class,
                () -> new StudentEnrolledCoursesController(
                        session,
                        appData,
                        appData.getRegistrationService()));

        f.bind(StudentCompletedCoursesController.class,
                () -> new StudentCompletedCoursesController(
                        session,
                        appData));

        f.bind(StudentPaymentController.class,
                () -> new StudentPaymentController( 
                        session, 
                        appData.getPaymentService()));

        f.bind(StudentTransactionsController.class,
                () -> new StudentTransactionsController(session));

        return f;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Public so controllers that swap inner content panes (e.g. dashboards) can
    // still go through the factory instead of calling FXMLLoader.load directly.
    public static Parent loadFXML(String fxml) throws IOException {
        var url = App.class.getResource("/baccportal/fxml/" + fxml + ".fxml");

        if (url == null) {
            throw new IOException("FXML file not found: /baccportal/fxml/" + fxml + ".fxml");
        }

        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(controllerFactory);
        return loader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}