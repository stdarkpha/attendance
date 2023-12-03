package project.app;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UiHelper {
    //    Start Navbar Methode ---------------------- <<<
    public static String generateGreeting() {
        LocalTime currentTime = LocalTime.now();

        if (currentTime.isBefore(LocalTime.of(10, 0))) {
            return "Selamat Pagi, ";
        } else if (currentTime.isAfter(LocalTime.of(10, 0))) {
            return "Selamat Siang, ";
        } else if (currentTime.isAfter(LocalTime.of(15, 0))) {
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
    //    End Navbar Methode ---------------------- <<<

    public static String timeFormat(String time) {
        LocalTime localTime = LocalTime.parse(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }
}
