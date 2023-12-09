package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

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
import java.util.Map;
import java.util.Objects;


public class SettingController {
    private final MainApp mainApp;
    private Parent root;
    private Account account;

    @FXML
    private Circle circleAvatar;

    @FXML
    private Text textHeading, textSub;

    @FXML
    private Text texth2, textlabel1, textlabel2, textlabel3, textlabel4;

    @FXML
    private Text textdata1, textdata2, textdata3, textdata4;
    @FXML
    private VBox containerSetting;
    @FXML
    private Button openHome, openTask, openSetting;

    Connection conn = DBConnection.getConnection();

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
        textdata4.setText(String.valueOf(result.get("lateCount")));
        textdata1.setText(UserDayWork +"/" + DayWork);
        if (UserDayWork != 0) {
            textdata2.setText(UiHelper.timeFormat(result.get("averageClockIn").toString()));
//            textdata3.setText(UiHelper.timeFormat(result.get("averageClockOut").toString()));
            totalWorkHour();
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

    @FXML
    private void initialize() {
        if(account.getAvatar() != null) {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/project/app/" + account.getAvatar())).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            circleAvatar.setFill(new ImagePattern(imageView.getImage()));
        }

        monthData();

        textHeading.setText(account.getFullName());
        textSub.setText("UID: " + account.getUid() + " | Divisi: " + account.getDivisionVal() + " | Telepon: " + account.getPhone() + " | Email: " + account.getEmail());
        texth2.setText("Peforma Bulan Ini");
        textlabel1.setText("Total Masuk");
        textlabel2.setText("Rata'' Masuk");
        textlabel3.setText("Total Kerja");
        textlabel4.setText("Terlambat");

        Text listSetting = new Text("Menu Pengaturan");
        listSetting.setFont(Font.font("Roboto Bold"));
        listSetting.setStyle("-fx-font-size: 14; -fx-text-fill: #3B415A;");

        containerSetting.getChildren().add(listSetting);
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
        menuNode("Pengaturan Akun", () -> {});
        menuNode("Logout", () -> {
            mainApp.navigationUser(account,"logout");
        });

        openHome.setOnAction(e -> {
            mainApp.navigationUser(account,"");
        });
        openTask.setOnAction(e -> {
            mainApp.navigationUser(account,"list-task");
        });
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
}
