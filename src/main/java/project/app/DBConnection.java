package project.app;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String user = "root";
            String pass = "";
            String db = "attendance";
            String url = "jdbc:mysql://127.0.0.1:3306/" + db;
            conn = DriverManager.getConnection(url, user, pass);
//            System.out.println("Connect Sucessful");
        } catch (Exception e) {
            System.out.println("ERROR = " + e);
        }
        return conn;
    }
}