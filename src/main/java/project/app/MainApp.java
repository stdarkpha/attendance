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
        this.primaryStage.setTitle(SettingHelper.getAppName() + " | Build v.1.4");
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResource("/project/app/upj.png")).toExternalForm());
        this.primaryStage.getIcons().add(iconImage);
        Scene scene = new Scene(new AnchorPane());
        this.primaryStage.setScene(scene);
        primaryStage.setResizable(false);


        loginController.showScene();
        // Bypass Login
//        loginController.performLogin();

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
        // Program Router
        switch (menu) {
            case "logout":
                this.primaryStage.setTitle(SettingHelper.getAppName() + " | Build v.1.4");
                loginController.showScene();
                this.account = null;
                break;
            case "list-task":
                ListTaskController listTaskController = new ListTaskController(this, account);
                primaryStage.setTitle("Daftar Aktifitas - " + fullName);
                listTaskController.showScene();
                break;
            case "setting":
                SettingController settingController = new SettingController(this, account);
                primaryStage.setTitle("Pengaturan - " + SettingHelper.getAppName());
                settingController.showScene();
                break;
            case "home-admin":
                AdminController adminController = new AdminController(this, account);
                primaryStage.setTitle("Analitik - " + SettingHelper.getAppName());
                adminController.showScene();
                break;
            case "list-user":
                AdminListUserController adminListUserController = new AdminListUserController(this, account);
                primaryStage.setTitle("Daftar Karyawan - " + SettingHelper.getAppName());
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