package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import javafx.geometry.Insets;

public class UserController {
    SettingHelper setting = new SettingHelper();
    private final LocalTime startTime = setting.getClockIn();
    private final MainApp mainApp;
    private Parent root;
    private Account account;
    private static String operationType;
    private Timeline timeline;
    private FadeTransition clockFadeTransition;
    private FadeTransition secondFadeTransition;
    @FXML private AnchorPane userContainer;
    @FXML private Circle circleAvatar;
    @FXML private Text greetingText, dateToday, clockText, secondText, textLate, timeLate, textLabelTask;
    @FXML private Text clockInText, clockOutText, dayWork, averageClockIn, averageClockOut, totalLateText;
    @FXML private TextField taskLabel, taskDesc;
    @FXML private Button setClock, addTask, closeTask, taskPushButton, btnLogout;
    @FXML private Pane form;
    @FXML private VBox taskContainer;
    @FXML private HBox btnContainer;
    @FXML private ChoiceBox<String> choiceBox;


    //    Start Scene Controller ---------------------- <<<
    public UserController(MainApp mainApp, Account account) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
            loader.setController(this);
            root = loader.load();

            TaskHelper.createTaskVBoxes(this, account.getId());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Tidak dapat memuat scene");
        }
    }
    //    End Scene Controller ---------------------- <<<


    //    Start Clock ---------------------- <<<
    public boolean CheckAttendance(int userId) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM log_user WHERE user_id = ? AND DATE(date) = CURDATE() AND clock_in IS NOT NULL";
            stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Retrieve the clock_in value from the result set
                String clockInValue = rs.getString("clock_in");
                String clockOut = rs.getString("clock_out");

                if (clockOut == null || clockOut.isEmpty()) {
                    clockText.setFill(Color.web("#396AFC"));
                    secondText.setFill(Color.web("#396AFC"));
                    timeLate.setFill(Color.web("#396AFC"));
                    textLate.setText("Kamu Telah Absen Pukul");
                    clockInText.setText(UiHelper.timeFormat(clockInValue));
                    clockInText.setOpacity(1.0);
                    timeLate.setText(UiHelper.timeFormat(clockInValue));
                    setClock.getStyleClass().add("active-button");
                    setClock.setText("Absen Keluar");
                } else {

                    clockInText.setText(UiHelper.timeFormat(clockInValue));
                    clockOutText.setText(UiHelper.timeFormat(clockOut));
                    clockInText.setOpacity(1.0);
                    clockOutText.setOpacity(1.0);

                    clockText.setFill(Color.web("#3B415A"));
                    secondText.setFill(Color.web("#3B415A"));
                    textLate.setText("Kamu Telah Absen Pulang Jam" );
                    timeLate.setText(UiHelper.timeFormat(clockOut));
                    setClock.getStyleClass().remove("active-button");
                    setClock.setText("Absen Keluar");
                }
                return true;
            } else {
                return false; // No matching row found
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources in a finally block to ensure they are always closed
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false; // Return false if an error occurred
    }

    private void calculatePasses(LocalTime currentTime) {
        long secondsPassed = ChronoUnit.SECONDS.between(startTime, currentTime);
        LocalTime passesTime = LocalTime.ofSecondOfDay(secondsPassed);
        String formattedPasses = passesTime.format(DateTimeFormatter.ofPattern("'('HH 'jam' mm 'menit)'"));
        textLate.setText("Kamu Terlambat " );
        timeLate.setText(formattedPasses);
        timeLate.setFill(Color.RED);
        setClock.getStyleClass().remove("active-button");
        setClock.setText("Absen Masuk");
    }

    public void stop() {
        timeline.stop();
        if (clockFadeTransition != null) {
            clockFadeTransition.stop();
        }
        if (secondFadeTransition != null) {
            secondFadeTransition.stop();
        }
    }

    public void SetClock() {
        int id = account.getId();
        boolean attendance = CheckAttendance(id);

        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String currentDateTime = currentTime.format(formatter);

        if (attendance) {
            try {
                System.out.println("Clock Out");
                String query = "UPDATE log_user SET clock_out = ? WHERE user_id = ? AND date = CURDATE()";
                stmt = conn.prepareStatement(query);

                stmt.setString(1, currentDateTime);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("Clock In");
                // Construct the SQL INSERT statement
                String query = "INSERT INTO log_user (user_id, date, clock_in) VALUES (?, CURDATE(), ?)";
                stmt = conn.prepareStatement(query);

                stmt.setInt(1, id);
                stmt.setString(2, currentDateTime);

                // Execute the SQL statement
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        monthData();
    }
    //    End Clock ---------------------- <<<

    //    Start Dashboard Data ---------------------- <<<
    public void monthData() {
        Map<String, Object> result = LogHelper.totalWork(account.getId());
        int UserDayWork = (int) result.get("count");
        int DayWork = LogHelper.DayWork();
        totalLateText.setText(String.valueOf(result.get("lateCount")));
        dayWork.setText(UserDayWork +"/" + DayWork);
        if (UserDayWork != 0) {
            averageClockIn.setText(UiHelper.timeFormat(result.get("averageClockIn").toString()));
            averageClockOut.setText(UiHelper.timeFormat(result.get("averageClockOut").toString()));
        }
    }
    //    End Dashboard Data ---------------------- <<<

    //    Start Activity Control ---------------------- <<<
    public static void setOperationType(String type) {
        operationType = type;
    }
    public void addTaskVBox(VBox vbox) {
        if (taskContainer != null) {
            taskContainer.getChildren().add(vbox);
        } else {
            System.out.println("taskContainer is null. Please ensure it is properly initialized.");
        }
    }

    public void modalTask() {
        double fromY, toY;

        if (form.getTranslateY() == 0.0) {
            fromY = 0.0;
            toY = -500.0;
            System.out.println("buka");
            if (operationType.equals("add")) {
                textLabelTask.setText("Tambah Tugas Baru");
                taskPushButton.setText("Simpan Tugas");
                taskPushButton.setStyle("-fx-background-color: #3665F0;");
                taskLabel.setText("");
                taskDesc.setText("");
                choiceBox.setValue(null);
            } else if (operationType.equals("update")) {
                taskPushButton.setStyle("-fx-background-color: #f58f0d;");
                taskPushButton.setText("Simpan Perubahan");
                textLabelTask.setText("Form Ubah Tugas");
                taskLabel.setText(TaskHelper.getSelectedLabel());
                taskDesc.setText(TaskHelper.getSelectedDescription());
                choiceBox.setValue(TaskHelper.getSelectedStatus());
                addDeleteButton();
            }
        } else {
            fromY = -500.0;
            toY = 0.0;
            System.out.println("tutup");
        }

        TranslateTransition transition = new TranslateTransition(Duration.seconds(.4), form);
        transition.setFromY(fromY);
        transition.setToY(toY);

        transition.play();
    }

    private void addDeleteButton() {
        Button deleteButton = new Button("Hapus");
        deleteButton.setContentDisplay(ContentDisplay.CENTER);
        deleteButton.setMnemonicParsing(false);
        deleteButton.setPrefHeight(25.0);
        deleteButton.setPrefWidth(376.0);
        deleteButton.setStyle("-fx-background-color: #f03652; -fx-background-radius: 5;");
        deleteButton.setTextAlignment(TextAlignment.CENTER);
        deleteButton.setTextFill(Color.WHITE);
        deleteButton.setId("Hapus");
        HBox.setHgrow(deleteButton, Priority.ALWAYS);
        deleteButton.setFont(Font.font("Roboto Medium", 14.0));
        deleteButton.setPadding(new Insets(8.0, 12.0, 8.0, 12.0));
        deleteButton.setCursor(Cursor.HAND);

        deleteButton.setOnAction(event -> {
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
                    String idTask = TaskHelper.getSelectedTaskId();
                    System.out.println(idTask);
                    try {
                        String query = "DELETE FROM tasks WHERE id = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, idTask);
                        stmt.executeUpdate();

                        taskContainer.getChildren().clear();
                        TaskHelper.createTaskVBoxes(this, account.getId());

                        modalTask();
                        removeBtnDelete();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Deletion canceled");
                }
            });


        });

        btnContainer.getChildren().add(deleteButton);
    }

    private void removeBtnDelete() {
        for (Node node : btnContainer.getChildren()) {
            if (node.getId().equals("Hapus")) {
                btnContainer.getChildren().remove(node);
                break;
            }
        }
    }

    //    End Activity Control ---------------------- <<<

    @FXML
    private void initialize() {
        if(account.getAvatar() != null) {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/project/app/" + account.getAvatar())).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            circleAvatar.setFill(new ImagePattern(imageView.getImage()));
        }

        String greeting = UiHelper.generateGreeting();
        String todayDate = UiHelper.generateDate();
        greetingText.setText(greeting + account.getFirstName() + "!");
        dateToday.setText(todayDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime currentTime = LocalTime.now();
            String formattedTime = currentTime.format(formatter);
            clockText.setText(formattedTime);

            // Extract the seconds value and update the corresponding node
            int seconds = currentTime.getSecond();
            String formattedSeconds = String.format("%02d", seconds);
            boolean absen = CheckAttendance(account.getId());
            secondText.setText(formattedSeconds);

            if (absen) {
                CheckAttendance(account.getId());
            } else {
                if (currentTime.isAfter(startTime)) {
                    clockText.setFill(Color.RED);
                    secondText.setFill(Color.RED);

                    // Create a fade animation for the clock text
                    clockFadeTransition = new FadeTransition(Duration.seconds(1), clockText);
                    clockFadeTransition.setFromValue(0.25);
                    clockFadeTransition.setToValue(1);
                    clockFadeTransition.setAutoReverse(true);
                    clockFadeTransition.play();

                    // Create a fade animation for the second text
                    secondFadeTransition = new FadeTransition(Duration.seconds(1), secondText);
                    secondFadeTransition.setFromValue(0.25);
                    secondFadeTransition.setToValue(1);
                    secondFadeTransition.setAutoReverse(true);
                    secondFadeTransition.play();

                    calculatePasses(currentTime);
                } else {
                    textLate.setText("Silahkan Absen Sebelum Jam Masuk" );
                    timeLate.setText(String.valueOf(startTime));
                    clockText.setFill(Color.web("#3B415A"));
                    secondText.setFill(Color.web("#3B415A"));
                }
            }

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        setClock.setOnAction(e -> SetClock());
        CheckAttendance(account.getId());
        monthData();

        choiceBox.getItems().addAll("Dikerjakan", "Selesai");
        addTask.setOnAction(e -> {
            setOperationType("add");
            modalTask();
        });

        closeTask.setOnAction(e -> {
            modalTask();
            removeBtnDelete();
        });

        btnLogout.setOnAction(e -> {
            mainApp.navigationUser(account, "logout");
        });

        taskPushButton.setOnAction(e -> {
            String label = taskLabel.getText();
            String desc = taskDesc.getText();
            String status = choiceBox.getValue();
            TaskHelper.submitTask(operationType, account.getId(), label, desc, status);
            if(operationType.equals("add")) {
                taskLabel.setText("");
                taskDesc.setText("");
                choiceBox.setValue(null);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Berhasil Menambah");
                alert.setHeaderText(null);
                alert.setContentText("Pekerjaan berhasil ditambah!");
                alert.showAndWait();
            } else if (operationType.equals("update")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Berhasil Update");
                alert.setHeaderText(null);
                alert.setContentText("Pekerjaan berhasil diupdate!");
                alert.showAndWait();
            }
            taskContainer.getChildren().clear();
            TaskHelper.createTaskVBoxes(this, account.getId());
        });
    }
}