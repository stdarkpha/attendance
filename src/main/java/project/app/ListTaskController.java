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
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.*;

public class ListTaskController {
    private final MainApp mainApp;
    private Parent root;

    @FXML
    private AnchorPane ListUserContainer;
    @FXML
    private Button btnLogout, openHome, openSetting;

    @FXML
    private VBox containerListUser;

    @FXML private Pane form;

    @FXML private Circle circleAvatar;

    @FXML
    private Text greetingText, dateToday, TotalTask;

    private Account account;

    public ListTaskController(MainApp mainApp, Account account) {
        this.account = account;
        this.mainApp = mainApp;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("list-task-view.fxml"));
            loader.setController(this);
            root = loader.load();

            getTaskUser();
        } catch (IOException e) {
            System.out.println("Failed to load FXML file: admin-view.fxml");
            e.printStackTrace();
        }
    }

    public void getTaskUser() {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT t.date, COUNT(t.id) AS total_tasks, l.clock_in, l.clock_out\n" +
                    "FROM tasks t\n" +
                    "JOIN log_user l ON t.user_id = l.user_id AND t.date = l.date\n" +
                    "WHERE t.user_id = ?\n" +
                    "GROUP BY t.date, l.clock_in, l.clock_out ORDER BY DATE DESC;";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, account.getId());
            rs = stmt.executeQuery();

            int taskCount = 0;
            
            while (rs.next()) {
                String date = rs.getString("date");
                int total_task = rs.getInt("total_tasks");
                String clockIn = rs.getString("clock_in");
                String clockOut = rs.getString("clock_out");

                taskCount += total_task;

                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefWidth(200.0);
                hbox.setSpacing(15.0);
                hbox.setPadding(new Insets(14, 14, 14, 14));
                hbox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5; -fx-border-style: solid; -fx-border-width: 4; -fx-border-color: transparent transparent transparent #396AFC;");

                hbox.setOnMouseClicked(e -> {
                    System.out.println(date + total_task + clockIn + clockOut);
                });

                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER_LEFT);
                vbox.setSpacing(2.0);
                HBox.setHgrow(vbox, Priority.ALWAYS);

                LocalDate dateString = LocalDate.parse(date);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM - yyyy", new Locale("id", "ID"));
                String formattedDate = dateString.format(formatter);

                Text textName = new Text(formattedDate);
                textName.setFill(Color.web("#3b415a"));
                textName.setStrokeType(StrokeType.OUTSIDE);
                textName.setStrokeWidth(0.0);
                textName.setFont(Font.font("Roboto Bold", 16.0));

                TextFlow textFlow = new TextFlow();
                textFlow.setLineSpacing(5.0);
                textFlow.setOpacity(0.5);

                Text textInfo = new Text("Masuk: " + UiHelper.timeFormat(clockIn) + " - Pulang: " + UiHelper.timeFormat(clockOut));
                textInfo.setFill(Color.web("#3b415a"));
                textInfo.setStrokeType(StrokeType.OUTSIDE);
                textInfo.setStrokeWidth(0.0);
                textInfo.setFont(Font.font("Roboto Medium", 12.0));

                textFlow.getChildren().add(textInfo);

                vbox.getChildren().addAll(textName, textFlow);

                HBox hboxButtons = new HBox();
                hboxButtons.setAlignment(Pos.CENTER_RIGHT);
                hboxButtons.setSpacing(3.0);
                hboxButtons.setStyle("-fx-background-color: #1D22A4; -fx-background-radius: 5;");
                hboxButtons.setPadding(new Insets(4, 8, 4, 8));

                Text textActivity = new Text("Aktifitas");
                textActivity.setFill(Color.web("#FFFFFF"));
                textActivity.setStrokeType(StrokeType.OUTSIDE);
                textActivity.setStrokeWidth(0.0);
                textActivity.setFont(Font.font("Roboto Light", 12.0));

                Text totalActivity = new Text(String.valueOf(total_task));
                totalActivity.setFill(Color.web("#FFFFFF"));
                totalActivity.setStrokeType(StrokeType.OUTSIDE);
                totalActivity.setStrokeWidth(0.0);
                totalActivity.setFont(Font.font("Roboto Bold", 16.0));

                hboxButtons.getChildren().addAll(textActivity, totalActivity);
                hbox.getChildren().addAll(vbox, hboxButtons);

                containerListUser.getChildren().add(hbox);
            }

            TotalTask.setText("Daftar Aktifitas ("+taskCount+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        if (account.getAvatar() != null) {
            String imagePath = "image/" + account.getAvatar();
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                // Handle the case when the image file is not found
                System.err.println("Image file not found1935: " + imagePath);
                return;
            }

            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            circleAvatar.setFill(new ImagePattern(imageView.getImage()));
        }
        String greeting = UiHelper.generateGreeting();
        String todayDate = UiHelper.generateDate();
        greetingText.setText(greeting + account.getFirstName() + "!");
        dateToday.setText(todayDate);

        btnLogout.setOnAction(e -> {
            mainApp.navigation(account,"logout");
        });
        openHome.setOnAction(e -> {
            mainApp.navigation(account, "");
        });
        openSetting.setOnAction(e -> {
            mainApp.navigation(account, "setting");
        });
    }
}
