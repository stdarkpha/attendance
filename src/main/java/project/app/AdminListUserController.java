package project.app;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.control.ChoiceBox;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdminListUserController {
    Connection conn = DBConnection.getConnection();
    private final MainApp mainApp;
    private Parent root;
    private Account account;
    private static String operationType;
    private Map<String, Integer> divisionMap = new HashMap<>();
    private Map<Integer, String> reverseDivisionMap = new HashMap<>();

    @FXML
    private AnchorPane ListUserContainer;

    @FXML private Circle circleAvatar;
    @FXML
    private Button openHome, openSetting, buttonAddUser, closeTask, PushButton, btnLogout;

    @FXML
    private VBox containerListUser;

    @FXML private Pane form;

    @FXML
    private Text greetingText, dateToday, textTotalEmployee, textLabelForm;

    @FXML
    private ChoiceBox<String> choiceDivision, choiceGender;

    @FXML
    private Label labelNameDivision;

    @FXML
    private DatePicker birthDate;

    @FXML
    private TextField firstName, lastName, phone, mail, password;

    public AdminListUserController(MainApp mainApp, Account account) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("list-user-view.fxml"));
            loader.setController(this);
            root = loader.load();

            getDataEmployees();
            addDivisionOption();
        } catch (IOException e) {
            System.out.println("Failed to load FXML file: admin-view.fxml");
            e.printStackTrace();
        }
    }

    private static int selectedUserId, selectedDivisionId;
    private static String selectedfirst_name;
    private static String selectedlast_name;
    private static String selecteddate;
    private static String selectedphone;
    private static String selectedemail;
    private static String selectedgender;
    private static Date selectedDate;

    public static void setSelectedUser(int id, String first_name, String last_name, String phone, String email, String gender) {
        selectedUserId = id;
        selectedfirst_name = first_name;
        selectedlast_name = last_name;
        selectedphone = phone;
        selectedemail = email;
        selectedgender = gender;
    }

    public static int getSelectedUserId() {
        return selectedUserId;
    }

    public static void setOperationType(String type) {
        operationType = type;
    }

    public void modalTask() {
        double fromY, toY;

        if (form.getTranslateY() == 0.0) {
            fromY = 0.0;
            toY = -700.0;
            if (operationType.equals("add")) {
                textLabelForm.setText("Tambah Karyawan Baru");
                PushButton.setText("Simpan Data");
                PushButton.setStyle("-fx-background-color: #3665F0;");
            } else if (operationType.equals("update")) {
                PushButton.setStyle("-fx-background-color: #f58f0d;");
                PushButton.setText("Simpan Perubahan");
                textLabelForm.setText("Form Ubah Data Karyawan");
            }
        } else {
            fromY = -700.0;
            toY = 0.0;

            firstName.setText("");
            lastName.setText("");
            birthDate.setValue(null);
            phone.setText("");
            mail.setText("");
            choiceGender.setValue(null);
            choiceDivision.setValue(null);
            password.setText("");
        }

        TranslateTransition transition = new TranslateTransition(Duration.seconds(.4), form);
        transition.setFromY(fromY);
        transition.setToY(toY);

        transition.play();
    }

    public int getTotalWorkHours(int id) {
        int totalWorkHours = 0;
        try {
            String query = "SELECT user_id, " +
                    "       SUM(TIME_TO_SEC(TIMEDIFF(clock_out, clock_in))) / 3600 AS total_work_hours " +
                    "FROM log_user " +
                    "WHERE user_id = " + id +
                    "      AND MONTH(date) = MONTH(CURRENT_DATE()) " +
                    "      AND YEAR(date) = YEAR(CURRENT_DATE()) " +
                    "GROUP BY user_id";

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                totalWorkHours = rs.getInt("total_work_hours");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalWorkHours;
    }

    public void getDataEmployees() {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT users.*, division.name AS division_name, division.start_salary, division.daily_bonus\n" +
                    "FROM users\n" +
                    "JOIN division ON users.division_id = division.id\n" +
                    "WHERE role = 'user'";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                int uid = rs.getInt("uid");
                int divisionId = rs.getInt("division_id");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String currentPhone = rs.getString("phone");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                Date birth = rs.getDate("birth");

                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.rgb(29, 35, 165, 0.1));
                dropShadow.setBlurType(BlurType.GAUSSIAN);
                dropShadow.setRadius(12);
                dropShadow.setOffsetY(5);

                VBox vboxContainer = new VBox();
                vboxContainer.setEffect(dropShadow);

                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefWidth(200.0);
                hbox.setSpacing(15.0);
                hbox.setPadding(new Insets(14, 14, 14, 14));
                hbox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5;");


                Circle circleAvatar = new Circle(20.0, Color.web("#ebebeb"));
                circleAvatar.setStroke(Color.BLACK);
                circleAvatar.setStrokeType(StrokeType.INSIDE);
                circleAvatar.setStrokeWidth(0.0);

                if (rs.getString("avatar") != null) {

                    String imagePath = "image/" + rs.getString("avatar");
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

                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER_LEFT);
                vbox.setSpacing(2.0);
                vbox.setCursor(Cursor.cursor("hand"));
                HBox.setHgrow(vbox, Priority.ALWAYS);

                Text textName = new Text(first_name + " " + last_name);
                textName.setFill(Color.web("#3b415a"));
                textName.setStrokeType(StrokeType.OUTSIDE);
                textName.setStrokeWidth(0.0);
                textName.setFont(Font.font("Roboto Medium", 14.0));

                Text textInfo = new Text("Id: " + rs.getString("uid") + " | Divisi: " + rs.getString("division_name"));
                textInfo.setFill(Color.web("#a9a9b3"));
                textInfo.setStrokeType(StrokeType.OUTSIDE);
                textInfo.setStrokeWidth(0.0);
                textInfo.setFont(Font.font("Montserrat Regular", 12.0));

                vbox.getChildren().addAll(textName, textInfo);

                HBox hboxButtons = new HBox();
                hboxButtons.setAlignment(Pos.CENTER_RIGHT);
                hboxButtons.setSpacing(5.0);

                Button editButton = new Button();
                editButton.setStyle("-fx-cursor: hand;");
                editButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/project/app/Edit.png")).toExternalForm())));
                editButton.setText("");
                editButton.setBackground(Background.EMPTY);
                editButton.setOnAction(event -> {
                    setSelectedUser(userId, first_name, last_name, currentPhone, email, gender);
                    selectedDate = birth;
                    selectedDivisionId = divisionId;
                    String setDivision = String.valueOf(reverseDivisionMap.get(selectedDivisionId));
                    System.out.println(setDivision);
                    firstName.setText(first_name);
                    lastName.setText(last_name);
                    birthDate.setValue(selectedDate.toLocalDate());
                    phone.setText(currentPhone);
                    mail.setText(email);
                    choiceGender.setValue(gender);
                    choiceDivision.setValue(setDivision);
                    selecteddate = String.valueOf(birthDate.getValue());
                    birthDate.setValue(LocalDate.parse(selecteddate));
                    setOperationType("update");
                    modalTask();
                });

                Button trashButton = new Button();
                trashButton.setStyle("-fx-cursor: hand;");
                trashButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/project/app/Trash.png")).toExternalForm())));
                trashButton.setText("");
                trashButton.setBackground(Background.EMPTY);
                trashButton.setOnAction(event -> {
                    System.out.println("delete" + userId);
                    selectedUserId = userId;
                    deleteUser();
                    AdminController.getEmployees();
                });

                hboxButtons.getChildren().addAll(editButton, trashButton);
                hbox.getChildren().addAll(circleAvatar, vbox, hboxButtons);

                VBox grade = new VBox();
                grade.setAlignment(Pos.CENTER_LEFT);
                grade.setSpacing(2.0);
                grade.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10 20; -fx-border-color: #396AFC; -fx-border-width: 0 0 0 5;");

                Text gradeText = new Text("Data Absensi dan Grade Karyawan");
                gradeText.setStrokeType(StrokeType.OUTSIDE);
                gradeText.setStrokeWidth(0.0);
                gradeText.setFont(Font.font("Roboto Medium", 14.0));
                Map<String, Object> result = LogHelper.totalWork(userId);
                int UserDayWork = (int) result.get("count");
                int DayWork = LogHelper.DayWork();
                int late = (int) result.get("lateCount");
                int workHourTotal = getTotalWorkHours(userId);
                int minWorkHour = DayWork*8;

                Text totalDayWork = new Text("Total Masuk = "+ UserDayWork +"/" + DayWork + " Hari");
                Text totalWorkHours = new Text("Total Jam Kerja = " + workHourTotal + "Jam (Minimum: " +(DayWork*8)+ "Jam)");
                Text totalPresent = new Text("Kehadiran = " + (DayWork - late) + "/" + DayWork +" Hari (Tidak Terlambat)");

                double workPercent = (double) UserDayWork / DayWork * 100;
                double limitedWorkPoint = Math.min(workPercent, 100);
                int workPoint = (int) (limitedWorkPoint * 0.4);

                double hourPercent = (double) workHourTotal / minWorkHour * 100;
                double limitedHourPoint = Math.min(hourPercent, 100);
                int hourPoint = (int) (limitedHourPoint * 0.4);

                double presentPercent = (double) (DayWork - late) / DayWork * 100;
                double limitedPresentPoint = Math.min(presentPercent, 100);
                System.out.println("present"+limitedPresentPoint);
                int presentPoint = (int) (limitedPresentPoint * 0.2);

                int score = workPoint + hourPoint + presentPoint;

                if(UserDayWork != 0) {
                    String gradeUser = "";
                    if(score >= 95) {
                        gradeUser = "A("+score+"%), Karyawan Berkomitmen";
                    } else if (score >= 90) {
                        gradeUser = "B("+score+"%), Harap Ditingkatkan";
                    } else {
                        gradeUser = "C("+score+"%), Karyawan Tidak Komitmen";
                    }
                    Text gradeFinal = new Text("Grade " + gradeUser);
                    gradeFinal.setFill(Color.web("#396AFC"));

                    grade.getChildren().addAll(gradeText, totalDayWork, totalWorkHours, totalPresent, gradeFinal);
                    System.out.println((workPoint + hourPoint + presentPoint) + "%");
                } else {
                    Text other = new Text("Penilaian dapat dilihat setelah satu bulan kerja");
                    grade.getChildren().addAll(gradeText, other);
                }

                grade.setVisible(false);
                grade.setManaged(false);

                final boolean[] isGradeVisible = {false};
                hbox.setOnMouseClicked(e -> {
                    System.out.println(userId + uid + first_name + last_name);
                    isGradeVisible[0] = !isGradeVisible[0]; // Toggle the visibility state
                    grade.setVisible(isGradeVisible[0]);
                    grade.setManaged(grade.isVisible());

                });

                vboxContainer.getChildren().addAll(hbox, grade);

                containerListUser.getChildren().add(vboxContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDivisionOption() {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM division";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int idDivision = rs.getInt("id");
                String nameDivision = rs.getString("name");
                double startSalary = rs.getDouble("start_salary");
                double dailyBonus = rs.getDouble("daily_bonus");

                choiceDivision.getItems().add(nameDivision);
                divisionMap.put(nameDivision, idDivision);
                reverseDivisionMap.put(idDivision, nameDivision);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitUser() {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (Objects.equals(operationType, "add")){
            if (choiceDivision.getValue() != null && !firstName.getText().isEmpty() && !lastName.getText().isEmpty() && birthDate.getValue() != null && !password.getText().isEmpty()) {
                String selectedDivision = choiceDivision.getValue();
                int divisionId = divisionMap.get(selectedDivision);
                Date birth = Date.valueOf(birthDate.getValue());
                Random random = new Random();
                int randomNumber = random.nextInt(900) + 100;
                int UID = Integer.parseInt(String.valueOf(divisionId) +
                        String.valueOf(birthDate.getValue().getMonthValue()) +
                        String.valueOf(birthDate.getValue().getDayOfMonth()) +
                        String.valueOf(randomNumber));

                String encryptPass = LoginController.encryptPassword(password.getText());
                System.out.println(UID + firstName.getText() + lastName.getText()
                        + birthDate.getValue() + mail.getText() + choiceGender.getValue()
                        + choiceDivision.getValue() + LoginController.encryptPassword(password.getText())
                );
                try {
                    String query = "INSERT INTO users (uid, first_name, last_name, birth, phone, email, gender, division_id, password, avatar) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'avatar.png')";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, UID);
                    stmt.setString(2, firstName.getText());
                    stmt.setString(3, lastName.getText());
                    stmt.setDate(4, birth);
                    stmt.setString(5, phone.getText());
                    stmt.setString(6, mail.getText());
                    stmt.setString(7, choiceGender.getValue());
                    stmt.setInt(8, divisionId);
                    stmt.setString(9, encryptPass);

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        containerListUser.getChildren().clear();
                        getDataEmployees();
                        textTotalEmployee.setText("Daftar Karyawan (" + AdminController.getEmployees() + ")");

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Berhasil Menambah");
                        alert.setHeaderText(null);
                        alert.setContentText("Karyawan berhasil ditambah!");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Gagal Menyimpan");
                alert.setHeaderText(null);
                alert.setContentText("Harap isi formulir dengan lengkap");
                alert.showAndWait();
            }
        } else if (Objects.equals(operationType, "update")){
            String selectedDivision = choiceDivision.getValue();
            int divisionId = divisionMap.get(selectedDivision);
            Date birth = Date.valueOf(birthDate.getValue());
            try {
                String query = "UPDATE users SET first_name = ?, last_name = ?, birth = ?, phone = ?, email = ?, gender = ?, division_id = ?";
                if (!password.getText().isEmpty()) {
                    query += ", password = ?";
                }
                query += " WHERE id = ?";

                stmt = conn.prepareStatement(query);
                stmt.setString(1, firstName.getText());
                stmt.setString(2, lastName.getText());
                stmt.setDate(3, birth);
                stmt.setString(4, phone.getText());
                stmt.setString(5, mail.getText());
                stmt.setString(6, choiceGender.getValue());
                stmt.setInt(7, divisionId);

                int parameterIndex = 8;
                if (!password.getText().isEmpty()) {
                    String encryptPass = LoginController.encryptPassword(password.getText());
                    stmt.setString(parameterIndex, encryptPass);
                    parameterIndex++;
                }

                stmt.setInt(parameterIndex, getSelectedUserId());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    containerListUser.getChildren().clear();
                    getDataEmployees();
                    textTotalEmployee.setText("Daftar Karyawan (" + AdminController.getEmployees() + ")");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Berhasil Memperbarui");
                    alert.setHeaderText(null);
                    alert.setContentText("Data karyawan berhasil diperbarui!");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUser() {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to delete?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {

                PreparedStatement stmt = null;

                try {
                    String query = "DELETE FROM users WHERE id = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, getSelectedUserId());
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("User deleted successfully.");
                    } else {
                        System.out.println("User not found or deletion unsuccessful.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                containerListUser.getChildren().clear();
                getDataEmployees();
            } else {
                System.out.println("Deletion canceled");
            }
        });

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

        choiceGender.getItems().add("Laki-Laki");
        choiceGender.getItems().add("Perempuan");

        textTotalEmployee.setText("Daftar Karyawan (" + AdminController.getEmployees() + ")");

        buttonAddUser.setOnAction(e -> {
            setOperationType("add");
            modalTask();
        });
        closeTask.setOnAction(e -> {
            modalTask();
        });
        PushButton.setOnAction(e -> {
            submitUser();
        });
        btnLogout.setOnAction(e -> {
            mainApp.navigation(account, "logout");
        });
        openHome.setOnAction(e -> {
            mainApp.navigation(account,"home-admin");
        });
        openSetting.setOnAction(e -> {
            mainApp.navigation(account, "setting");
        });
    }
}
