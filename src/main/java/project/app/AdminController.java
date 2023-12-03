package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


import javafx.scene.chart.XYChart;



public class AdminController {

    private final MainApp mainApp;
    private Parent root;

    @FXML
    private AnchorPane adminContainer;

    @FXML
    private Text textTotalEmployee, textTotalDivision, greetingText, dateToday;
    @FXML
    private Text textCountToday, textInToday, textOutToday, textLateToday;
    @FXML
    private Text textCountMonth, textInMonth, textOutMonth, textLateMonth;
    @FXML
    private Text textCountYear, textInYear, textOutYear, textLateYear;

    @FXML
    private NumberAxis todayX, todayY;

    @FXML
    private Button openUser, openHome, btnLogout;

    @FXML
    private VBox todayChart, monthChart, yearChart;

    UiHelper uiHelper = new UiHelper();

    public AdminController(MainApp mainApp) {
        this.mainApp = mainApp;
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            loader.setController(this);
            root = loader.load();

            String chartName = "yearChart";
            String seriesName = "Grafik Absen Masuk Tahunan";
            String query = "SELECT clock_in, COUNT(*) AS total_count FROM log_user GROUP BY clock_in ORDER BY clock_in ASC";
            createLineChart(chartName, seriesName, query, loader);

            // Month Query
            chartName = "monthChart";
            seriesName = "Grafik Absen Masuk Bulanan";
            query = "SELECT clock_in, COUNT(*) AS total_count \n" +
                    "FROM log_user \n" +
                    "WHERE DATE_FORMAT(date, '%Y-%m') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m') \n" +
                    "GROUP BY clock_in \n" +
                    "ORDER BY clock_in ASC;";
            createLineChart(chartName, seriesName, query, loader);

            // Today Query
            chartName = "todayChart";
            seriesName = "Grafik Absen Masuk Harian";
            query = "SELECT clock_in, COUNT(*) AS total_count \n" +
                    "FROM log_user \n" +
                    "WHERE DATE(date) = CURRENT_DATE() \n" +
                    "GROUP BY clock_in \n" +
                    "ORDER BY clock_in ASC;";
            createLineChart(chartName, seriesName, query, loader);

        } catch (Exception e) {

            System.out.println("Tidak dapat memuat scene");
        }
    }

    public void showScene() {
        if (root != null) {
            mainApp.setRoot(root);
        } else {
            System.out.println("Root is null");
        }
    }

    public void createLineChart(String chartName, String seriesName, String query, FXMLLoader loader) {
        VBox vbox = (VBox) loader.getNamespace().get(chartName);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(seriesName);

        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String clockIn = UiHelper.timeFormat(rs.getString("clock_in"));
                int totalCount = rs.getInt("total_count");
                dataSeries.getData().add(new XYChart.Data<>(clockIn, totalCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lineChart.getData().add(dataSeries);

        dataSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #396AFC;"); // Set line color
        for (XYChart.Data<String, Number> data : dataSeries.getData()) {
            data.getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: #396AFC, white; -fx-background-insets: 0, 2;");
        }
        vbox.getChildren().add(lineChart);
    }


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
                    if(count != 0) {
                        String avgClockInStr = rs.getString("avg_clock_in_month");
                        String avgClockOutStr = rs.getString("avg_clock_out_month");
                        averageClockIn = LocalTime.parse(avgClockInStr);
                        averageClockOut = LocalTime.parse(avgClockOutStr);
                    }

                    // By Today
                    countToday = rs.getInt("count_today");
                    lateCountToday = rs.getInt("late_count_today");
                    if (countToday != 0) {
                        String avgClockInTodayStr = rs.getString("avg_clock_in_today");
                        String avgClockOutTodayStr = rs.getString("avg_clock_out_today");
                        averageClockInToday = LocalTime.parse(avgClockInTodayStr);
                        averageClockOutToday = LocalTime.parse(avgClockOutTodayStr);
                    }

                    // By Year
                    countYear = rs.getInt("count_year");
                    lateCountYear = rs.getInt("late_count_year");
                    if (countYear != 0) {
                        String avgClockInYearStr = rs.getString("avg_clock_in_year");
                        String avgClockOutYearStr = rs.getString("avg_clock_out_year");
                        averageClockInYear = LocalTime.parse(avgClockInYearStr);
                        averageClockOutYear = LocalTime.parse(avgClockOutYearStr);
                    }

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

        textTotalEmployee.setText(String.valueOf(getEmployees()));
        textTotalDivision.setText(String.valueOf(getDivision()));

        Map<String, Object> result = monthDataVal();

        textCountToday.setText(String.valueOf(result.get("countToday")));
        textInToday.setText(UiHelper.timeFormat(String.valueOf(result.get("averageClockInToday"))));
        textOutToday.setText(UiHelper.timeFormat(String.valueOf(result.get("averageClockOutToday"))));
        textLateToday.setText(String.valueOf(result.get("lateCountToday")));

        textCountMonth.setText(String.valueOf(result.get("count")));
        textInMonth.setText(UiHelper.timeFormat(String.valueOf(result.get("averageClockIn"))));
        textOutMonth.setText(UiHelper.timeFormat(String.valueOf(result.get("averageClockOut"))));
        textLateMonth.setText(String.valueOf(result.get("lateCount")));

        textCountYear.setText(String.valueOf(result.get("countYear")));
        textInYear.setText(UiHelper.timeFormat(String.valueOf(result.get("averageClockInYear"))));
        textOutYear.setText(UiHelper.timeFormat(String.valueOf(result.get("averageClockOutYear"))));
        textLateYear.setText(String.valueOf(result.get("lateCountYear")));

//        System.out.println(result);

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
