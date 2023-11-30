package project.app;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class UserController {
    SettingHelper setting = new SettingHelper();
    private final LocalTime startTime = setting.getClockIn();
    private final MainApp mainApp;
    private final Account account;
    private Timeline timeline;
    private FadeTransition clockFadeTransition;
    private FadeTransition secondFadeTransition;

    @FXML private AnchorPane userContainer;
    @FXML private Text greetingText, dateToday, clockText, secondText, textLate, timeLate;
    @FXML private Button setClock;
    @FXML private Text clockInText, clockOutText, dayWork, averageClockIn, averageClockOut, totalLateText;

    @FXML private VBox taskContainer;


    public UserController(MainApp mainApp, Account account) { // Receive account object as parameter
        this.mainApp = mainApp;
        this.account = account;
        loadFXML();
    }

    public void showScene() {
        mainApp.getPrimaryStage().setScene(new Scene(userContainer));
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
            loader.setController(this);
            userContainer = loader.load();

            TaskHelper taskHelper = new TaskHelper();
            taskHelper.createTaskVBoxes(this, account.getId());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Tidak dapat memuat scene");
        }
    }

    public void addTaskVBox(VBox vbox) {
        if (taskContainer != null) {
            taskContainer.getChildren().add(vbox);
        } else {
            System.out.println("taskContainer is null. Please ensure it is properly initialized.");
        }
    }

    @FXML
    private void initialize() {
        String greeting = generateGreeting();
        String todayDate = generateDate();
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
    }

    public static String timeFormat(String time) {
        LocalTime localTime = LocalTime.parse(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }

    public void monthData() {
        Map<String, Object> result = LogHelper.totalWork(account.getId());
        int UserDayWork = (int) result.get("count");
        int DayWork = LogHelper.DayWork();
        totalLateText.setText(String.valueOf(result.get("lateCount")));
        dayWork.setText(UserDayWork +"/" + DayWork);
        averageClockIn.setText(timeFormat(result.get("averageClockIn").toString()));
        averageClockOut.setText(timeFormat(result.get("averageClockOut").toString()));
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
                    clockInText.setText(timeFormat(clockInValue));
                    clockInText.setOpacity(1.0);
                    timeLate.setText(timeFormat(clockInValue));
                    setClock.getStyleClass().add("active-button");
                    setClock.setText("Absen Keluar");
                } else {

                    clockInText.setText(timeFormat(clockInValue));
                    clockOutText.setText(timeFormat(clockOut));
                    clockInText.setOpacity(1.0);
                    clockOutText.setOpacity(1.0);

                    clockText.setFill(Color.web("#3B415A"));
                    secondText.setFill(Color.web("#3B415A"));
                    textLate.setText("Kamu Telah Absen Pulang Jam" );
                    timeLate.setText(timeFormat(clockOut));
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

    public static String generateGreeting() {
        LocalTime currentTime = LocalTime.now();

        if (currentTime.isBefore(LocalTime.NOON)) {
            return "Selamat Pagi, ";
        } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
            return "Selamat Siang, ";
        } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
            return "Selamat Sore, ";
        } else {
            return "Selamat Malam, ";
        }
    }

    public static String generateDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM - yyyy", new Locale("id", "ID"));
        return currentDate.format(formatter);
    }
}