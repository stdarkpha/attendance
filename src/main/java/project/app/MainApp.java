package project.app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    private Stage primaryStage;
    private Account account;
    LoginController loginController = new LoginController(this);
    AdminController adminController = new AdminController(this);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResource("/project/app/upj.png")).toExternalForm());
        this.primaryStage.getIcons().add(iconImage);

        adminController.showScene();

        // Default & Bypass
//        loginController.showScene();
//        loginController.performLogin();

        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void switchToMainMenu(Account account, String menu) {
        UserController userController = new UserController(this, account);
        String fullName = account.getFullName();

        switch (menu) {
            case "logout":
                this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
                loginController.showScene();
                break;
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