package baccportal;

import java.io.IOException;

import baccportal.model.storage.AppData;
import baccportal.model.users.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static User currentUser;
    private static final AppData appData = new AppData();

    @Override
    public void start(Stage stage) throws IOException {
        appData.load();

        scene = new Scene(loadFXML("login"), 800, 600);

        stage.setTitle("BAY AREA COMMUNITY COLLEGE PORTAL - KALI");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        stage.show();
    }

    public static AppData getAppData() {
        return appData;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        var url = App.class.getResource("/baccportal/fxml/" + fxml + ".fxml");

        if (url == null) {
            throw new IOException("FXML file not found: /baccportal/fxml/" + fxml + ".fxml");
        }

        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}