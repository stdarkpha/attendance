package project.app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    private Stage primaryStage;
    private Account account;
//    private MainMenuController mainMenuController;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResource("/project/app/upj.png")).toExternalForm()); // Replace with the path to your icon image
        this.primaryStage.getIcons().add(iconImage);

        // Create the controllers for each menu
        LoginController loginController = new LoginController(this);


        // Show the login screen initially
        loginController.showScene();

        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void switchToMainMenu(Account account, String menu) {
        UserController userController = new UserController(this, account);
        String fullName = account.getFullName();

        switch (menu) {
            case "user":

                primaryStage.setTitle("Dashboard - " + fullName);
                userController.showScene();
                break;

//            case "dashboard":
//                dashboardController.showScene();
//                break;
//            case "report":
//                reportController.showScene();
//                break;
//            case "settings":
//                settingsController.showScene();
//                break;

            default:
                primaryStage.setTitle("Dashboard - " + fullName);
                userController.showScene();
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}