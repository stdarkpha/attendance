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

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResource("/project/app/upj.png")).toExternalForm());
        this.primaryStage.getIcons().add(iconImage);
        Scene scene = new Scene(new AnchorPane());
        this.primaryStage.setScene(scene);
        primaryStage.setResizable(false);


        loginController.showScene();
        // Bypass Login
         loginController.performLogin();

        // navigationUser(this.account, "list-task");
        primaryStage.show();
    }

    public void setRoot(Parent root) {
        primaryStage.getScene().setRoot(root);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void navigation(Account account, String menu) {
        String fullName = account.getFullName();

        switch (menu) {
            case "logout":
                this.primaryStage.setTitle("Aplikasi Kehadiran | Build v.1.0");
                loginController.showScene();
                this.account = null;
                break;
            case "list-task":
                ListTaskController listTaskController = new ListTaskController(this, account);
                primaryStage.setTitle("Dashboard - " + fullName);
                listTaskController.showScene();
                break;
            case "setting":
                SettingController settingController = new SettingController(this, account);
                settingController.showScene();
                break;
            case "home-admin":
                AdminController adminController = new AdminController(this, account);
                adminController.showScene();
                break;
            case "list-user":
                AdminListUserController adminListUserController = new AdminListUserController(this, account);
                adminListUserController.showScene();
                break;
            default:
                UserController userController = new UserController(this, account);
                primaryStage.setTitle("Dashboard - " + fullName);
                userController.showScene();
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}