package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class AdminController {

    private final MainApp mainApp;

    @FXML
    private AnchorPane AdminContainer;

    public AdminController(MainApp mainApp) {
        this.mainApp = mainApp;
        loadFXML();
    }

    public void showScene() {
        mainApp.getPrimaryStage().setScene(new Scene(AdminContainer));
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            loader.setController(this);
            AdminContainer = loader.load();
        } catch (Exception e) {

            System.out.println("Tidak dapat memuat scene");
        }
    }

    @FXML
    private Text textTotalEmployee, textTotalDivision, greetingText, dateToday;
    @FXML
    private Text textCountToday, textInToday, textOutToday, textLateToday;
    @FXML
    private Text textCountMonth, textInMonth, textOutMonth, textLateMonth;
    @FXML
    private Text textCountYear, textInYear, textOutYear, textLateYear;

    public static int getEmployees() {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int totalCount = 0;

        try {
            String query = "SELECT COUNT(*) AS total_users FROM users WHERE role = 'user'";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalCount = rs.getInt("total_users");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalCount;
    }

    public static int getDivision() {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int totalCount = 0;

        try {
            String query = "SELECT COUNT(*) AS total_division FROM division";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalCount = rs.getInt("total_division");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalCount;
    }

    public static Map<String, Object> monthDataVal() {
        int count = 0;
        int lateCount = 0;
        LocalTime averageClockIn = LocalTime.MIDNIGHT;
        LocalTime averageClockOut = LocalTime.MIDNIGHT;

        int countToday = 0;
        int lateCountToday = 0;
        LocalTime averageClockInToday = LocalTime.MIDNIGHT;
        LocalTime averageClockOutToday = LocalTime.MIDNIGHT;

        int countYear = 0;
        int lateCountYear = 0;
        LocalTime averageClockInYear = LocalTime.MIDNIGHT;
        LocalTime averageClockOutYear = LocalTime.MIDNIGHT;

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String clockInSettingQuery = "SELECT value FROM settings WHERE name = 'ClockIn'";
            PreparedStatement clockInStmt = conn.prepareStatement(clockInSettingQuery);
            ResultSet clockInResultSet = clockInStmt.executeQuery();

            if (clockInResultSet.next()) {
                String clockInValue = clockInResultSet.getString("value");

                String query = "SELECT " +
                        "SUM(IF(MONTH(date) = MONTH(CURRENT_DATE()) AND YEAR(date) = YEAR(CURRENT_DATE()), 1, 0)) AS count_month, "
                        +
                        "SUM(IF(TIME_TO_SEC(clock_in) > TIME_TO_SEC(?) AND MONTH(date) = MONTH(CURRENT_DATE()) AND YEAR(date) = YEAR(CURRENT_DATE()), 1, 0)) AS late_count_month, "
                        +
                        "SEC_TO_TIME(AVG(IF(MONTH(date) = MONTH(CURRENT_DATE()) AND YEAR(date) = YEAR(CURRENT_DATE()), TIME_TO_SEC(clock_in), NULL))) AS avg_clock_in_month, "
                        +
                        "SEC_TO_TIME(AVG(IF(MONTH(date) = MONTH(CURRENT_DATE()) AND YEAR(date) = YEAR(CURRENT_DATE()), TIME_TO_SEC(clock_out), NULL))) AS avg_clock_out_month, "
                        +
                        "SUM(IF(DATE(date) = CURRENT_DATE(), 1, 0)) AS count_today, " +
                        "SUM(IF(TIME_TO_SEC(clock_in) > TIME_TO_SEC(?) AND DATE(date) = CURRENT_DATE(), 1, 0)) AS late_count_today, "
                        +
                        "SEC_TO_TIME(AVG(IF(DATE(date) = CURRENT_DATE(), TIME_TO_SEC(clock_in), NULL))) AS avg_clock_in_today, "
                        +
                        "SEC_TO_TIME(AVG(IF(DATE(date) = CURRENT_DATE(), TIME_TO_SEC(clock_out), NULL))) AS avg_clock_out_today, "
                        +
                        "SUM(IF(YEAR(date) = YEAR(CURRENT_DATE()), 1, 0)) AS count_year, " +
                        "SUM(IF(TIME_TO_SEC(clock_in) > TIME_TO_SEC(?) AND YEAR(date) = YEAR(CURRENT_DATE()), 1, 0)) AS late_count_year, "
                        +
                        "SEC_TO_TIME(AVG(IF(YEAR(date) = YEAR(CURRENT_DATE()), TIME_TO_SEC(clock_in), NULL))) AS avg_clock_in_year, "
                        +
                        "SEC_TO_TIME(AVG(IF(YEAR(date) = YEAR(CURRENT_DATE()), TIME_TO_SEC(clock_out), NULL))) AS avg_clock_out_year "
                        +
                        "FROM log_user";

                stmt = conn.prepareStatement(query);
                stmt.setObject(1, LocalTime.parse(clockInValue));
                stmt.setObject(2, LocalTime.parse(clockInValue));
                stmt.setObject(3, LocalTime.parse(clockInValue));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // By Month
                    count = rs.getInt("count_month");
                    lateCount = rs.getInt("late_count_month");
                    String avgClockInStr = rs.getString("avg_clock_in_month");
                    String avgClockOutStr = rs.getString("avg_clock_out_month");
                    averageClockIn = LocalTime.parse(avgClockInStr);
                    averageClockOut = LocalTime.parse(avgClockOutStr);

                    // By Today
                    countToday = rs.getInt("count_today");
                    lateCountToday = rs.getInt("late_count_today");
                    String avgClockInTodayStr = rs.getString("avg_clock_in_today");
                    String avgClockOutTodayStr = rs.getString("avg_clock_out_today");
                    averageClockInToday = LocalTime.parse(avgClockInTodayStr);
                    averageClockOutToday = LocalTime.parse(avgClockOutTodayStr);

                    // By Year
                    countYear = rs.getInt("count_year");
                    lateCountYear = rs.getInt("late_count_year");
                    String avgClockInYearStr = rs.getString("avg_clock_in_year");
                    String avgClockOutYearStr = rs.getString("avg_clock_out_year");
                    averageClockInYear = LocalTime.parse(avgClockInYearStr);
                    averageClockOutYear = LocalTime.parse(avgClockOutYearStr);

                }

                clockInResultSet.close();
                clockInStmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("lateCount", lateCount);
        result.put("averageClockIn", averageClockIn);
        result.put("averageClockOut", averageClockOut);

        result.put("countToday", countToday);
        result.put("lateCountToday", lateCountToday);
        result.put("averageClockInToday", averageClockInToday);
        result.put("averageClockOutToday", averageClockOutToday);

        result.put("countYear", countYear);
        result.put("lateCountYear", lateCountYear);
        result.put("averageClockInYear", averageClockInYear);
        result.put("averageClockOutYear", averageClockOutYear);

        return result;
    }

    @FXML
    private void initialize() {
        String greeting = UiHelper.generateGreeting();
        String todayDate = UiHelper.generateDate();
        // greetingText.setText(greeting + account.getFirstName() + "!");
        greetingText.setText(greeting + "Administrator!");
        dateToday.setText(todayDate);

        System.out.println("total divisi" + getDivision() + "total Employees" + getEmployees());
        textTotalEmployee.setText(String.valueOf(getEmployees()));
        textTotalDivision.setText(String.valueOf(getDivision()));

//        Map<String, Object> result = monthDataVal();

//        textCountToday.setText(String.valueOf(result.get("countToday")));
//        textInToday.setText(UiHelper.timeFormat(result.get("averageClockInToday").toString()));
//        textOutToday.setText(UiHelper.timeFormat(result.get("averageClockOutToday").toString()));
//        textLateToday.setText(String.valueOf(result.get("lateCountToday")));
//
//        textCountMonth.setText(String.valueOf(result.get("count")));
//        textInMonth.setText(UiHelper.timeFormat(result.get("averageClockIn").toString()));
//        textOutMonth.setText(UiHelper.timeFormat(result.get("averageClockOut").toString()));
//        textLateMonth.setText(String.valueOf(result.get("lateCount")));
//
//        textCountYear.setText(String.valueOf(result.get("countYear")));
//        textInYear.setText(UiHelper.timeFormat(result.get("averageClockInYear").toString()));
//        textOutYear.setText(UiHelper.timeFormat(result.get("averageClockOutYear").toString()));
//        textLateYear.setText(String.valueOf(result.get("lateCountYear")));

//        System.out.println(result);
    }
}
