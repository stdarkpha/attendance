package project.app;

import javafx.scene.Parent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    private Stage primaryStage;
    private Account account;
    LoginController loginController = new LoginController(this);
    AdminController adminController = new AdminController(this);
    AdminListUserController adminListUserController = new AdminListUserController(this);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResource("/project/app/upj.png")).toExternalForm());
        this.primaryStage.getIcons().add(iconImage);
//        adminListUserController.showScene();
        Scene scene = new Scene(new AnchorPane());
        this.primaryStage.setScene(scene);
//        adminListUserController.showScene();

        // Default & Bypass
        loginController.showScene();
//        loginController.performLogin();

        primaryStage.show();
    }

    public void setRoot(Parent root) {
        primaryStage.getScene().setRoot(root);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void navigation(String menu) {
        switch (menu) {
            case "home-admin":
                adminController.showScene();
                break;
            case "list-user":
                adminListUserController.showScene();
                break;
            default:
                break;
        }
    }

    public void navigationUser(Account account, String menu) {
        UserController userController = new UserController(this, account);
        String fullName = account.getFullName();

        // Route Setting
        switch (menu) {
            case "logout":
                this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
                loginController.showScene();
                this.account = null;
                break;
            case "user":
                primaryStage.setTitle("Dashboard - " + fullName);
                userController.showScene();
                break;

            case "list-user":
//                adminListUserController.showScene();
                break;
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