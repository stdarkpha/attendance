package project.app;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LogHelper {
    public static Map<String, Object> totalWork(int id) {
        int count = 0;
        int lateCount = 0;
        LocalTime averageClockIn = LocalTime.MIDNIGHT;
        LocalTime averageClockOut = LocalTime.MIDNIGHT;
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        try {
            String clockInSettingQuery = "SELECT value FROM settings WHERE name = 'ClockIn'";
            PreparedStatement clockInStmt = conn.prepareStatement(clockInSettingQuery);
            ResultSet clockInResultSet = clockInStmt.executeQuery();

            if (clockInResultSet.next()) {
                String clockInValue = clockInResultSet.getString("value");

                String query = "SELECT COUNT(*) AS count, " +
                        "SUM(IF(TIME_TO_SEC(clock_in) > TIME_TO_SEC(?), 1, 0)) AS late_count, " +
                        "SEC_TO_TIME(AVG(TIME_TO_SEC(clock_in))) AS avg_clock_in, " +
                        "SEC_TO_TIME(AVG(TIME_TO_SEC(clock_out))) AS avg_clock_out " +
                        "FROM log_user " +
                        "WHERE user_id = ? " +
                        "AND MONTH(date) = MONTH(CURRENT_DATE()) " +
                        "AND YEAR(date) = YEAR(CURRENT_DATE())";

                stmt = conn.prepareStatement(query);

                stmt.setObject(1, LocalTime.parse(clockInValue));
                stmt.setInt(2, id);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    count = resultSet.getInt("count");
                    lateCount = resultSet.getInt("late_count");
                    String avgClockInStr = resultSet.getString("avg_clock_in");
                    String avgClockOutStr = resultSet.getString("avg_clock_out");

                    System.out.println(count != 0 && avgClockInStr != null && avgClockOutStr != null);
                    if(count != 0 && avgClockInStr != null && avgClockOutStr != null) {
                        averageClockIn = LocalTime.parse(avgClockInStr);
                        averageClockOut = LocalTime.parse(avgClockOutStr);
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
        return result;
    }

//    Total Hari Kerja dalam sebulan (5 hari kerja)
    public static int DayWork() {
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        int totalDays = (int) ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth) + 1;
        int excludedDays = 0;

        for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.MONDAY) {
                excludedDays++;
            }
        }

        return totalDays - excludedDays;
    }

}
