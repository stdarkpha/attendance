package project.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Account {
    private int id;
    private final String uid;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatar;
    private String email;
    private String gender;
    private String role;
    private final int divisionId;

    public Account(int id, String uid, String firstName, String lastName, String phone, String avatar, String email, String gender, String role, int idDivision) {
        this.id = id;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatar = avatar;
        this.email = email;
        this.gender = gender;
        this.role = role;
        this.divisionId = idDivision;

        addDivisionOption();
    }

    // Getters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }

    // Example of a getter for the full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    private Map<String, Integer> divisionMap = new HashMap<>();
    private Map<Integer, String> reverseDivisionMap = new HashMap<>();

    public String getDivisionVal() {
        return reverseDivisionMap.get(divisionId);
    }

    public int getDivisionId() { return divisionId; }

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

                divisionMap.put(nameDivision, idDivision);
                reverseDivisionMap.put(idDivision, nameDivision);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}