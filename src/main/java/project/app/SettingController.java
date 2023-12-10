package project.app;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

import java.util.Map;


public class SettingController {
    private final MainApp mainApp;
    private Parent root;
    private Account account;

    @FXML
    private Circle circleAvatar;

    @FXML
    private Text textHeading, textSub;

    @FXML private Pane form;

    @FXML
    private Text texth2, textlabel1, textlabel2, textlabel3, textlabel4;

    @FXML
    private Text textdata1, textdata2, textdata3, textdata4, textLabelForm;
    @FXML
    private VBox containerSetting, formContainer;
    @FXML
    private Button openHome, openMiddle, openSetting, closeTask;

    @FXML
    private ImageView ImgOpen;

    private static String operationType;
    private Map<String, String[]> data;

    Connection conn = DBConnection.getConnection();
    public static void setOperationType(String type) {
        operationType = type;
    }

    public SettingController(MainApp mainApp, Account account) {
        this.mainApp = mainApp;
        this.account = account;
        loadFXML();
    }

    public void showScene() {
        if (root != null) {
            mainApp.setRoot(root);
        } else {
            System.out.println("Root is null");
        }
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("setting-view.fxml"));
            loader.setController(this);
            root = loader.load();

        } catch (IOException e) {
            System.out.println("Failed to load FXML file");
            e.printStackTrace();
        }
    }



    public void monthData() {
        Map<String, Object> result = LogHelper.totalWork(account.getId());
        int UserDayWork = (int) result.get("count");
        int DayWork = LogHelper.DayWork();
        int late = (int) result.get("lateCount");
        textdata4.setText(String.valueOf(late));
        textdata1.setText(UserDayWork +"/" + DayWork);
        int days = UserDayWork - late;
        if (UserDayWork != 0) {
            textdata2.setText(UiHelper.timeFormat(result.get("averageClockIn").toString()));
//            textdata3.setText(UiHelper.timeFormat(result.get("averageClockOut").toString()));
            totalWorkHour();
            showUserSalary(days);
        }
    }

    public void totalWorkHour() {
        try {
            String query =
                    "SELECT user_id, " +
                    "       SUM(TIME_TO_SEC(TIMEDIFF(clock_out, clock_in))) / 3600 AS total_work_hours " +
                    "FROM log_user " +
                    "WHERE user_id = " + account.getId() +
                    "      AND MONTH(date) = MONTH(CURRENT_DATE()) " +
                    "      AND YEAR(date) = YEAR(CURRENT_DATE()) " +
                    "GROUP BY user_id";

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String totalWorkval = rs.getInt("total_work_hours") + "Jam";
                textdata3.setText(totalWorkval);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String formatIDR(int number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setCurrency(Currency.getInstance("IDR"));
        return format.format(number);
    }

    public void showUserSalary(int days) {
        int salary = 0;
        int daily = 0;
        try {
            String query = "SELECT * FROM division WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, account.getDivisionId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                salary = rs.getInt("start_salary");
                daily = rs.getInt("daily_bonus");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        daily *= days;

        VBox salaryBox = new VBox();
        salaryBox.setPrefSize(300, 80);
        salaryBox.setStyle("-fx-alignment: center; -fx-background-color:linear-gradient(to right, #396AFC, #1D22A4); -fx-background-radius: 10; -fx-padding: 20;");

        Label titleLabel = new Label("Perkiraan Gaji Yang Didapat:");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-font-family: 'Roboto';");

        VBox innerContainer = new VBox();
        innerContainer.setStyle("-fx-alignment: center; -fx-padding: 10 0;");

        Label salaryLabel = new Label(formatIDR(salary));
        salaryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Roboto';");

        Label bonusLabel = new Label("+ " + formatIDR(daily));
        bonusLabel.setStyle("-fx-text-fill: #CCFF00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Roboto';");

        innerContainer.getChildren().addAll(salaryLabel, bonusLabel);

        Label descriptionLabel = new Label("Gaji Pokok + Uang Harian Masuk (Tanpa Terlambat)");
        descriptionLabel.setStyle("-fx-text-fill: white; -fx-opacity: 0.75; -fx-font-size: 10px;");

        salaryBox.getChildren().addAll(titleLabel, innerContainer, descriptionLabel);

        containerSetting.getChildren().addFirst(salaryBox);
    }

    @FXML
    private void initialize() {
        if(account.getAvatar() != null) {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/project/app/" + account.getAvatar())).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            circleAvatar.setFill(new ImagePattern(imageView.getImage()));
        }

        textHeading.setText(account.getFullName());
        if(!Objects.equals(account.getRole(), "admin")) {
            textSub.setText("UID: " + account.getUid() + " | Divisi: " + account.getDivisionVal() + " | Telepon: " + account.getPhone() + " | Email: " + account.getEmail());
            texth2.setText("Peforma Bulan Ini");
            textlabel1.setText("Total Masuk");
            textlabel2.setText("Rata'' Masuk");
            textlabel3.setText("Total Kerja");
            textlabel4.setText("Terlambat");
            monthData();

            Text listSetting = new Text("Menu Pengaturan");
            listSetting.setFont(Font.font("Roboto Bold"));
            listSetting.setStyle("-fx-font-size: 14; -fx-text-fill: #3B415A;");
            containerSetting.getChildren().add(listSetting);

            ImgOpen.setImage(new Image(String.valueOf(Objects.requireNonNull(getClass().getResource("/project/app/Activity.png")))));
            openHome.setOnAction(e -> {
                mainApp.navigation(account,"");
            });
            openMiddle.setOnAction(e -> {
                mainApp.navigation(account,"list-task");
            });
        } else {
            ImgOpen.setImage(new Image(String.valueOf(Objects.requireNonNull(getClass().getResource("/project/app/List-Users.png")))));
            textSub.setText("Aplikasi Monitoring Karyawan | Develop by Farouq Mulya");
            texth2.setText("Log Dashboard Admin");
            textlabel1.setText("Tanggal Aktif");
            textlabel2.setText("Terakhir Masuk");
            textlabel3.setText("Terakhir Keluar");
            textlabel4.setText("Durasi Aktif");

            menuNode("Pengaturan Aplikasi", () -> {

            });
            menuNode("Pengaturan Divisi & Salary", () -> {

            });

            openHome.setOnAction(e -> {
                mainApp.navigation(account,"home-admin");
            });
            openMiddle.setOnAction(e -> {
                mainApp.navigation(account,"list-user");
            });
        }
        menuNode("Ubah Avatar", () -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                    "Image Files", "*.jpg", "*.jpeg", "*.png");
            fileChooser.getExtensionFilters().add(imageFilter);
            File selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
            if (selectedFile != null) {
                try {
                    // Save the selected image to the src folder
                    Path srcPath = selectedFile.toPath();
                    URI destUri = Objects.requireNonNull(getClass().getResource("/project/app/")).toURI();
                    Path destPath = Paths.get(destUri).resolve(selectedFile.getName());
                    Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);

                    String query = "UPDATE users SET avatar = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, selectedFile.getName());
                    stmt.setInt(2, account.getId());

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        Image newProfileImage = new Image(selectedFile.toURI().toString());
                        circleAvatar.setFill(new ImagePattern(newProfileImage));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Ganti Avatar Dibatalkan");
            }
        });
        menuNode("Pengaturan Akun", () -> {
            formContainer.getChildren().clear();
            textLabelForm.setText("Ubah Data Akun");
            modalTask();

            data = new LinkedHashMap<>();
            data.put("first_name", new String[]{"Nama Pertama", account.getFirstName(), ""});
            data.put("last_name", new String[]{"Nama Akhiran", account.getLastName(), ""});
            data.put("phone", new String[]{"Nomor Telepon", account.getPhone(), ""});
            data.put("email", new String[]{"Email Kamu", account.getEmail(), ""});

            VBox formLayout = generateForm(data);

            Button submitButton = new Button("Submit");
            submitButton.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(submitButton, Priority.ALWAYS);
            submitButton.setStyle("-fx-background-color: #396AFC; -fx-background-radius: 5;");
            submitButton.setPadding(new Insets(10));
            submitButton.setTextFill(javafx.scene.paint.Color.WHITE);
            submitButton.setOnAction(event -> {
                // Update the data map with the TextField values
                for (Map.Entry<String, String[]> entry : data.entrySet()) {
                    String fieldName = entry.getKey();
                    TextField textField = (TextField) formLayout.lookup("#" + fieldName);
                    String[] fieldData = entry.getValue();
                    fieldData[2] = textField.getText();
                }

                // Customization: Perform actions on form submission
                System.out.println("Form submitted!");
                System.out.println("First Name: " + data.get("first_name")[2]);
                System.out.println("Last Name: " + data.get("last_name")[2]);
                System.out.println("Phone: " + data.get("phone")[2]);
                System.out.println("Email: " + data.get("email")[2]);
            });

            formContainer.getChildren().addAll(formLayout, submitButton);
            formContainer.setSpacing(10);
        });
        menuNode("Ubah Password", () -> {
            formContainer.getChildren().clear();
            textLabelForm.setText("Ubah Password Akun");
            modalTask();

            VBox passwordVBox = new VBox();
            Label passwordLabel = new Label("Enter Password:");
            PasswordField passwordField = new PasswordField();
            passwordVBox.getChildren().addAll(passwordLabel, passwordField);

            VBox confirmVBox = new VBox();
            Label confirmLabel = new Label("Confirm Password:");
            PasswordField confirmField = new PasswordField();
            confirmVBox.getChildren().addAll(confirmLabel, confirmField);

            Button submitButton = new Button("Submit");
            submitButton.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(submitButton, Priority.ALWAYS);
            submitButton.setStyle("-fx-background-color: #396AFC; -fx-background-radius: 5;");
            submitButton.setPadding(new Insets(10));
            submitButton.setTextFill(javafx.scene.paint.Color.WHITE);
            submitButton.setOnAction(e -> {
                String password = passwordField.getText();
                String confirm = confirmField.getText();

                if (password.equals(confirm)) {
                    System.out.println("Password Sama bang :''Vv");
                } else {
                    // Passwords do not match, show a popup to re-enter
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Passwords do not match. Please re-enter.");
                    alert.showAndWait();
                }
            });

            formContainer.getChildren().addAll(passwordVBox, confirmVBox, submitButton);
            formContainer.setSpacing(10);
        });

        menuNode("Logout", () -> {
            mainApp.navigation(account,"logout");
        });

        closeTask.setOnAction(e -> {
            modalTask();
        });
    }

    public void modalTask() {
        double fromY, toY;

        if (form.getTranslateY() == 0.0) {
            fromY = 0.0;
            toY = -700.0;
        } else {
            fromY = -700.0;
            toY = 0.0;

            formContainer.getChildren().clear();
        }

        TranslateTransition transition = new TranslateTransition(Duration.seconds(.4), form);
        transition.setFromY(fromY);
        transition.setToY(toY);

        transition.play();
    }

    private void menuNode(String name, Runnable method) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPrefWidth(200.0);
        hbox.setSpacing(15.0);
        hbox.setPadding(new Insets(6, 14, 6, 14));
        hbox.setStyle("-fx-cursor: hand; -fx-background-color: #FFFFFF; -fx-background-radius: 5; -fx-border-style: solid; -fx-border-width: 4; -fx-border-color: transparent transparent transparent #396AFC;");

        hbox.setOnMouseClicked(e -> {
            // Parse method using the parameter
            method.run();
        });

        Button btn = new Button();
        btn.setStyle("-fx-font-size: 14; -fx-font-family: 'Roboto'; -fx-font-weight: bold; -fx-text-fill: #3B415A");
        btn.setText(name);
        btn.setBackground(Background.EMPTY);

        hbox.getChildren().addAll(btn);
        containerSetting.getChildren().add(hbox);
    }

    private VBox generateForm(Map<String, String[]> data) {
        VBox formLayout = new VBox();
        formLayout.setSpacing(10);

        for (Map.Entry<String, String[]> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            String[] fieldData = entry.getValue();
            String labelText = fieldData[0];
            String fieldValue = fieldData[1];

            Text label = new Text(labelText);
            label.setFill(javafx.scene.paint.Color.web("#9c9c9c"));
            label.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
            label.setStrokeWidth(0);
            label.setFont(Font.font("Roboto Light", 14));

            TextField textField = new TextField();
            textField.setId(fieldName);
            textField.setPadding(new Insets(5));
            textField.setText(fieldValue);

            VBox fieldLayout = new VBox(label, textField);
            VBox.setVgrow(textField, javafx.scene.layout.Priority.ALWAYS);

            formLayout.getChildren().add(fieldLayout);
        }

        return formLayout;
    }
}
