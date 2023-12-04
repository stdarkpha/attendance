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

import java.io.IOException;
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
    private Button openHome, openUser;

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
            textdata3.setText(UiHelper.timeFormat(result.get("averageClockOut").toString()));
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
        textlabel3.setText("Rata'' Pulang");
        textlabel4.setText("Terlambat");

        Text listSetting = new Text("Menu Pengaturan");
        listSetting.setFont(Font.font("Roboto Bold"));
        listSetting.setStyle("-fx-font-size: 14; -fx-text-fill: #3B415A;");

        containerSetting.getChildren().add(listSetting);
        menuNode("Ubah Avatar");
        menuNode("Pengaturan Akun");
        menuNode("Logout");

        openHome.setOnAction(e -> {
            mainApp.navigation("home-admin");
        });
        openUser.setOnAction(e -> {
            mainApp.navigation("list-user");
        });
    }

    private void menuNode(String name) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPrefWidth(200.0);
        hbox.setSpacing(15.0);
        hbox.setPadding(new Insets(6, 14, 6, 14));
        hbox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5; -fx-border-style: solid; -fx-border-width: 4; -fx-border-color: transparent transparent transparent #396AFC;");

        hbox.setOnMouseClicked(e -> {
            //Parse method in here
        });

        Button btn = new Button();
        btn.setStyle("-fx-cursor: hand; -fx-font-size: 14; -fx-font-family: 'Roboto'; -fx-font-weight: bold; -fx-text-fill: #3B415A");
        btn.setText(name);
        btn.setBackground(Background.EMPTY);

        hbox.getChildren().addAll(btn);
        containerSetting.getChildren().add(hbox);
    }
}
