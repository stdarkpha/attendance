package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.nio.charset.StandardCharsets;

public class LoginController {

    private final MainApp mainApp;

    @FXML
    private AnchorPane loginContainer;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    public LoginController(MainApp mainApp) {
        this.mainApp = mainApp;
        loadFXML();
    }

    public void showScene() {
        mainApp.getPrimaryStage().setScene(new Scene(loginContainer));
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            loader.setController(this);
            loginContainer = loader.load();
        } catch (Exception e) {

            System.out.println("Tidak dapat memuat scene");
        }
    }

    public void performLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

//        Account account = login(username, password);
        Account account = login("123", "admin123");

        if (account != null) {
            if (account.getRole().equals("admin")) {
                usernameField.setText("");
                passwordField.setText("");
                mainApp.navigation("home-admin");
            } else {
                usernameField.setText("");
                passwordField.setText("");
                mainApp.navigationUser(account,"");
            }
        } else {
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("ID atau Password Salah!!");
        alert.showAndWait();
    }

//    Encrypt Password ke SHA-256
    static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account login(String uid, String password) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Account account = null;

        try {
            String encryptedPassword = encryptPassword(password);
            String query = "SELECT * FROM users WHERE uid = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, uid);
            stmt.setString(2, encryptedPassword);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phone = rs.getString("phone");
                String avatar = rs.getString("avatar");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                String role = rs.getString("role");
                int division = rs.getInt("division_id");

                account = new Account(id, uid, firstName, lastName, phone, avatar, email, gender, role, division);

//                System.out.println(account.getFullName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return account;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> performLogin());

        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performLogin();
            }
        });
    }
}