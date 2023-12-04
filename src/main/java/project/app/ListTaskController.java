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
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ListTaskController {
    private final MainApp mainApp;
    private Parent root;
    private static String operationType;
    private Map<String, Integer> divisionMap = new HashMap<>();
    private Map<Integer, String> reverseDivisionMap = new HashMap<>();

    @FXML
    private AnchorPane ListUserContainer;
    @FXML
    private Button openUser, openHome, buttonAddUser, closeTask, taskPushButton, btnLogout;

    @FXML
    private VBox containerListUser;

    @FXML private Pane form;

    @FXML
    private Text greetingText, dateToday, textTotalEmployee;

    @FXML
    private ChoiceBox<String> choiceDivision, choiceGender;

    @FXML
    private Label labelNameDivision;

    @FXML
    private DatePicker birthDate;

    @FXML
    private TextField firstName, lastName, phone, mail, password;

    public ListTaskController(MainApp mainApp) {
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
            System.out.println("buka");
//            if (operationType.equals("add")) {
//                textLabelTask.setText("Tambah Tugas Baru");
//                taskPushButton.setText("Simpan Tugas");
//                taskPushButton.setStyle("-fx-background-color: #3665F0;");
//                taskLabel.setText("");
//                taskDesc.setText("");
//                choiceBox.setValue(null);
//            } else if (operationType.equals("update")) {
//                taskPushButton.setStyle("-fx-background-color: #f58f0d;");
//                taskPushButton.setText("Simpan Perubahan");
//                textLabelTask.setText("Form Ubah Tugas");
//                taskLabel.setText(TaskHelper.getSelectedLabel());
//                taskDesc.setText(TaskHelper.getSelectedDescription());
//                choiceBox.setValue(TaskHelper.getSelectedStatus());
//                addDeleteButton();
//            }
        } else {
            fromY = -700.0;
            toY = 0.0;
            System.out.println("tutup");
        }

        TranslateTransition transition = new TranslateTransition(Duration.seconds(.4), form);
        transition.setFromY(fromY);
        transition.setToY(toY);

        transition.play();
    }

    public void getDataEmployees() {
        Connection conn = DBConnection.getConnection();
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

                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefWidth(200.0);
                hbox.setSpacing(15.0);
                hbox.setPadding(new Insets(14, 14, 14, 14));
                hbox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5;");

                hbox.setOnMouseClicked(e -> {
                    System.out.println(userId + uid + first_name + last_name);
                });

                Circle circleAvatar = new Circle(20.0, Color.web("#ebebeb"));
                circleAvatar.setStroke(Color.BLACK);
                circleAvatar.setStrokeType(StrokeType.INSIDE);
                circleAvatar.setStrokeWidth(0.0);

                if (rs.getString("avatar") != null) {
                    Image image = new Image(Objects.requireNonNull(getClass().getResource("/project/app/" + rs.getString("avatar"))).toExternalForm());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(40);
                    imageView.setFitHeight(40);
                    circleAvatar.setFill(new ImagePattern(imageView.getImage()));
                }

                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER_LEFT);
                vbox.setSpacing(2.0);
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

                containerListUser.getChildren().add(hbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDivisionOption() {
        Connection conn = DBConnection.getConnection();
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

            choiceDivision.setOnAction(event -> {
                String selectedDivision = choiceDivision.getValue();
                int divisionId = divisionMap.get(selectedDivision);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitUser() {
        Connection conn = DBConnection.getConnection();
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
                    String query = "INSERT INTO users (uid, first_name, last_name, birth, phone, email, gender, division_id, password) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                Connection conn = DBConnection.getConnection();
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
        String greeting = UiHelper.generateGreeting();
        String todayDate = UiHelper.generateDate();
        // greetingText.setText(greeting + account.getFirstName() + "!");
        greetingText.setText(greeting + "Administrator!");
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

        taskPushButton.setOnAction(e -> {
            submitUser();
        });

        btnLogout.setOnAction(e -> {
            mainApp.navigation("logout");
        });
        openHome.setOnAction(e -> {
            mainApp.navigation("home-admin");
        });
        openUser.setOnAction(e -> {
            mainApp.navigation("list-user");
        });
    }
}