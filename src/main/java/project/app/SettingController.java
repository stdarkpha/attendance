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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import java.sql.*;
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

    private void adminData() {
        texth2.setText(SettingHelper.getAppName());
        textlabel1.setText("Total Karyawan");
        textlabel2.setText("Jam Masuk");
        textlabel3.setText("Jam Pulang");
        textlabel4.setText("Total Divisi");
        textdata1.setText(String.valueOf(AdminController.getEmployees()));
        textdata2.setText(String.valueOf(SettingHelper.getClockIn()));
        textdata3.setText(String.valueOf(SettingHelper.getClockOut()));
        textdata4.setText(String.valueOf(AdminController.getDivision()));
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
            textSub.setText(SettingHelper.getAppName() + " | Develop by Farouq Mulya");

            adminData();

            menuNode("Pengaturan Aplikasi", () -> {
                formContainer.getChildren().clear();
                textLabelForm.setText("Ubah Pengaturan Aplikasi");
                modalTask();

                data = new LinkedHashMap<>();
                data.put("AppName", new String[]{"Nama Aplikasi", SettingHelper.getAppName(), ""});
                data.put("ClockIn", new String[]{"Jam Masuk", String.valueOf(SettingHelper.valClockIn()), ""});
                data.put("ClockOut", new String[]{"Jam Pulang", String.valueOf(SettingHelper.valClockOut()), ""});

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
                    System.out.println("Nama Aplikasi: " + data.get("AppName")[2]);
                    System.out.println("Jam Masuk: " + data.get("ClockIn")[2]);
                    System.out.println("Jam Pulang: " + data.get("ClockOut")[2]);

                    try {
                        String updateQuery = "UPDATE settings SET value = ? WHERE name = ?";
                        PreparedStatement stmt = conn.prepareStatement(updateQuery);

                        // Create a list of updates
                        List<Map<String, String>> updates = new ArrayList<>();
                        updates.add(Map.of("name", "AppName", "value", data.get("AppName")[2]));
                        updates.add(Map.of("name", "ClockIn", "value", data.get("ClockIn")[2]));
                        updates.add(Map.of("name", "ClockOut", "value", data.get("ClockOut")[2]));

                        // Iterate over the updates and execute the update query
                        for (Map<String, String> update : updates) {
                            stmt.setString(1, update.get("value"));
                            stmt.setString(2, update.get("name"));
                            stmt.executeUpdate();
                        }
                        modalTask();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Berhasil Ubah Pengaturan");
                        alert.showAndWait();
                        System.out.println("Updates successfully executed.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                formContainer.getChildren().addAll(formLayout, submitButton);
                formContainer.setSpacing(10);
            });
            menuNode("Daftar Divisi & Salary", () -> {
                formContainer.getChildren().clear();
                formContainer.setSpacing(10);
                textLabelForm.setText("Daftar Divisi");
                modalTask();

                getDivisionList();
            });

            menuNode("Tambah Divisi", () -> {
                formContainer.getChildren().clear();
                formContainer.setSpacing(10);
                textLabelForm.setText("Tambah Divisi");
                addDivision();
                modalTask();
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
                    String currentDir = System.getProperty("user.dir");

                    // Create a new image directory in the current working directory
                    File imageDirectory = new File(currentDir, "image");
                    if (!imageDirectory.exists()) {
                        imageDirectory.mkdirs();
                    }

                    // Determine the destination file path
                    String destFilePath = selectedFile.getName();
                    File destFile = new File(imageDirectory, destFilePath);

                    // Copy the selected image to the image directory
                    Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

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

                try {
                    String query = "UPDATE users SET first_name = ?, last_name = ?,  phone = ?, email = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, data.get("first_name")[2]);
                    stmt.setString(2, data.get("last_name")[2]);
                    stmt.setString(3, data.get("phone")[2]);
                    stmt.setString(4, data.get("email")[2]);
                    stmt.setInt(5, account.getId());
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        modalTask();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Berhasil");
                        alert.setHeaderText(null);
                        alert.setContentText("Berhasil Ubah Data");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
            Label confirmLabel = new Label("Konfirmasi Password:");
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
                    String encryptedPassword = LoginController.encryptPassword(confirm);
                    System.out.println(encryptedPassword);
                    try {
                        String query = "UPDATE users SET password = ? WHERE id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, encryptedPassword);
                        stmt.setInt(2, account.getId());
                        int rowsAffected = stmt.executeUpdate();

                        if (rowsAffected > 0) {
                            modalTask();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Berhasil");
                            alert.setHeaderText(null);
                            alert.setContentText("Berhasil Ubah Password!");
                            alert.showAndWait();
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    // Passwords do not match, show a popup to re-enter
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Password Tidak Sama!!");
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

    public void addDivision() {
        data = new LinkedHashMap<>();
        data.put("name", new String[]{"Nama Divisi", "", ""});
        data.put("start_salary", new String[]{"Gaji Utama", "", ""});
        data.put("daily_bonus", new String[]{"Bonus Harian", "", ""});

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

            try {
                String query = "INSERT INTO division (name, start_salary, daily_bonus) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, data.get("name")[2]);
                stmt.setString(2, data.get("start_salary")[2]);
                stmt.setString(3, data.get("daily_bonus")[2]);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    modalTask();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Berhasil");
                    alert.setHeaderText(null);
                    alert.setContentText("Berhasil Tambah Divisi " + data.get("name")[2]);
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        formContainer.getChildren().addAll(formLayout, submitButton);
        formContainer.setSpacing(10);
    }

    public void updateDivision(int Id, String name, int salary, int bonus) {
        data = new LinkedHashMap<>();
        data.put("name", new String[]{"Nama Divisi", name, ""});
        data.put("start_salary", new String[]{"Gaji Utama", String.valueOf(salary), ""});
        data.put("daily_bonus", new String[]{"Bonus Harian", String.valueOf(bonus), ""});

        VBox formLayout = generateForm(data);

        Button cancelButton = new Button("Cancel");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(cancelButton, Priority.ALWAYS);
        cancelButton.setStyle("-fx-background-color: #FBA017; -fx-background-radius: 5;");
        cancelButton.setPadding(new Insets(10));
        cancelButton.setTextFill(javafx.scene.paint.Color.WHITE);
        cancelButton.setOnAction(event -> {
            formContainer.getChildren().clear();
            textLabelForm.setText("Daftar Divisi");
            getDivisionList();

        });

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

            try {
                String query = "UPDATE division SET name = ?, start_salary = ?, daily_bonus = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, data.get("name")[2]);
                stmt.setString(2, data.get("start_salary")[2]);
                stmt.setString(3, data.get("daily_bonus")[2]);
                stmt.setInt(4, Id);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    modalTask();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Berhasil");
                    alert.setHeaderText(null);
                    alert.setContentText("Berhasil Update Divisi " + data.get("name")[2]);
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        formContainer.getChildren().clear();
        formContainer.getChildren().addAll(formLayout, submitButton, cancelButton);
        formContainer.setSpacing(10);
    }

    public void deleteDivision(int id) {
        System.out.println("Delete Divisi ID: " + id);

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Hapus Divisi?");

        ButtonType yesButton = new ButtonType("Ya");
        ButtonType noButton = new ButtonType("Tidak");
        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = null;

                try {
                    String query = "DELETE FROM division WHERE id = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, id);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Divisi Berhasil Dihapus.");
                    } else {
                        System.out.println("Divisi Gagal Dihapus.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                modalTask();
            } else {
                System.out.println("Deletion canceled");
            }
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

    private void getDivisionList() {
        try {
            String query = "SELECT * FROM division";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idDivision = rs.getInt("id");
                String nameDivision = rs.getString("name");
                int startSalary = rs.getInt("start_salary");
                int dailyBonus = rs.getInt("daily_bonus");
                generateListDivision(idDivision, nameDivision, startSalary, dailyBonus);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateListDivision(int idDivision, String nameDivision, int startSalary, int dailyBonus) {
        VBox itemDivision = new VBox();
        itemDivision.setSpacing(5);
        itemDivision.setPadding(new Insets(8, 0, 8, 24));
        itemDivision.setStyle("-fx-border-color: #396AFC; -fx-border-width: 0 0 0 4;");

        HBox hbox1 = new HBox();
        Text text = new Text(nameDivision);
        text.setFont(Font.font("Roboto", FontWeight.BOLD, 16));
        text.setFill(Color.web("#3B415A"));
        HBox hbox2 = new HBox();
        HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(hbox2, javafx.scene.layout.Priority.ALWAYS);
        hbox2.setAlignment(Pos.CENTER_RIGHT);
        hbox2.setSpacing(5);

        Button ubahButton = new Button("Ubah");
        ubahButton.setStyle("-fx-background-color: #FBA017; -fx-cursor: hand; -fx-text-fill: white; -fx-font-family: 'Roboto'; -fx-font-size: 12px; -fx-font-weight: bold;");
        ubahButton.setOnAction(event -> {
            textLabelForm.setText("Ubah Divisi");
            updateDivision(idDivision, nameDivision, startSalary, dailyBonus);
        });
        Button hapusButton = new Button("Hapus");
        hapusButton.setStyle("-fx-background-color: #E10000; -fx-cursor: hand; -fx-text-fill: white; -fx-font-family: 'Roboto'; -fx-font-size: 12px; -fx-font-weight: bold;");
        hapusButton.setOnAction(event -> {
            deleteDivision(idDivision);
        });

        hbox2.getChildren().addAll(ubahButton, hapusButton);
        hbox1.getChildren().addAll(text, hbox2);

        HBox hbox3 = new HBox();
        hbox3.setSpacing(1);

        Text gajiUtamaLabel = new Text("Gaji Utama");
        gajiUtamaLabel.setStyle("-fx-text-fill: #9C9C9C; -fx-font-size: 12px; -fx-font-family: 'Roboto Medium';");
        Text gajiUtamaValue = new Text(": " + formatIDR(startSalary));
        gajiUtamaValue.setStyle("-fx-text-fill: #9C9C9C; -fx-font-size: 12px; -fx-font-family: 'Roboto Light'; ");
        Text bonusHarianLabel = new Text("+ Bonus Harian");
        bonusHarianLabel.setStyle("-fx-text-fill: #9C9C9C; -fx-font-size: 12px; -fx-font-family: 'Roboto Medium';");
        Text bonusHarianValue = new Text(": " + formatIDR(dailyBonus));
        bonusHarianValue.setStyle("-fx-text-fill: #9C9C9C; -fx-font-size: 12px; -fx-font-family: 'Roboto Light';");

        hbox3.getChildren().addAll(gajiUtamaLabel, gajiUtamaValue, bonusHarianLabel, bonusHarianValue);

        itemDivision.getChildren().addAll(hbox1, hbox3);
        formContainer.getChildren().add(itemDivision);
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
