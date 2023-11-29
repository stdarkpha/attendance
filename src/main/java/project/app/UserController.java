package project.app;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.temporal.ChronoUnit;

public class UserController {

    private final MainApp mainApp;
    private final Account account;
    private Timeline timeline;
    private FadeTransition clockFadeTransition;
    private FadeTransition secondFadeTransition;
    private LocalTime startTime = LocalTime.of(8, 0);
    private int passes = 0;

    @FXML
    private AnchorPane userContainer;

    @FXML
    private Text greetingText;
    @FXML
    private Text dateToday;

    @FXML
    private Text clockText;

    @FXML
    private Text secondText;

    @FXML
    private Text textLate;

    @FXML
    private Text timeLate;


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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Tidak dapat memuat scene");
        }
    }

    @FXML
    private void initialize() {
        String greeting = generateGreeting();
        String todayDate = generateDate();
        greetingText.setText(greeting + account.getFirstName() + "!");
        dateToday.setText(todayDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Create a Timeline animation to update the clock every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime currentTime = LocalTime.now();
            String formattedTime = currentTime.format(formatter);
            clockText.setText(formattedTime);

            // Extract the seconds value and update the corresponding node
            int seconds = currentTime.getSecond();
            String formattedSeconds = String.format("%02d", seconds);
            boolean absen = true;
            secondText.setText(formattedSeconds);

            if (absen) {
                clockText.setFill(Color.web("#396AFC"));
                textLate.setText("Kamu Telah Absen Pukul" );
                timeLate.setText("08:00");
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
                    clockText.setFill(Color.web("#3B415A"));
                    secondText.setFill(Color.web("#3B415A"));
                }
            }

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void calculatePasses(LocalTime currentTime) {
        long secondsPassed = ChronoUnit.SECONDS.between(startTime, currentTime);
        LocalTime passesTime = LocalTime.ofSecondOfDay(secondsPassed);
        String formattedPasses = passesTime.format(DateTimeFormatter.ofPattern("'('HH 'jam' mm 'menit)'"));
        textLate.setText("Kamu Terlambat -" );
        timeLate.setText(formattedPasses);
        timeLate.setFill(Color.RED);

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