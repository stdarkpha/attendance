package project.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class SettingHelper {
    public String getAppName() {
        return getValueByName("AppName");
    }

    public LocalTime getClockIn() {
        String clockInString = getValueByName("ClockIn");
        String[] components = clockInString.split(":");
        int hour = Integer.parseInt(components[0]);
        int minute = Integer.parseInt(components[1]);
        int second = Integer.parseInt(components[2]);
        return LocalTime.of(hour, minute, second);
    }

    public String getClockOut() {
        return getValueByName("ClockOut");
    }

    public String getLogoApp() {
        return getValueByName("LogoApp");
    }

    private String getValueByName(String name) {
        Connection conn = DBConnection.getConnection();
        try {
            String query = "SELECT value FROM settings WHERE name = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // or throw an exception if desired
    }
}