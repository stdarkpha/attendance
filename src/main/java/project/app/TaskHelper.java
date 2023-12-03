package project.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TaskHelper {

    private UserController userController;
    private static String selectedTaskId;
    private static String selectedLabel;
    private static String selectedDescription;

    private static String selectedStatus;

    public static String getSelectedTaskId() {
        return selectedTaskId;
    }

    public static String getSelectedLabel() {
        return selectedLabel;
    }

    public static String getSelectedDescription() {
        return selectedDescription;
    }

    public static String getSelectedStatus() {
        return selectedStatus;
    }

    public static void setSelectedTask(String taskId, String label, String description, String status) {
        selectedTaskId = taskId;
        selectedLabel = label;
        selectedDescription = description;
        selectedStatus = status;
    }

    public static void createTaskVBoxes(UserController userController, int id) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
         // Assuming the account ID is stored in a variable called accountId
        String currentDay = LocalDate.now().toString(); // Get the current day as a string

        String query = "SELECT * FROM tasks WHERE user_id = '" + id + "' AND DATE(date) = '" + currentDay + "' ORDER BY task_start DESC";

        try {
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String taskId;
                String clockInTime = UiHelper.timeFormat(rs.getString("task_start"));
                String description = rs.getString("desc");
                taskId = rs.getString("id");
                String label = rs.getString("label");
                String status = rs.getString("status");

                VBox vbox = new VBox();
                vbox.setPrefWidth(100.0);
                vbox.setSpacing(5.0);
                vbox.setStyle("-fx-background-color: #F6F6F6; -fx-border-style: solid; -fx-border-width: 4; -fx-border-color: transparent transparent transparent #396AFC;");
                vbox.setCursor(Cursor.cursor("hand"));

                Text clockInText = new Text(clockInTime + " | " + label);
                clockInText.setFill(Color.web("#3b415a"));
                clockInText.setStrokeType(StrokeType.OUTSIDE);
                clockInText.setStrokeWidth(0.0);
                clockInText.setFont(Font.font("Roboto Medium", 18.0));

                TextFlow textFlow = new TextFlow();
                textFlow.setLineSpacing(5.0);
                textFlow.setOpacity(0.5);

                Text descriptionText = new Text("(Status: " + status + ") - " + description);
                descriptionText.setFill(Color.web("#3b415a"));
                descriptionText.setStrokeType(StrokeType.OUTSIDE);
                descriptionText.setStrokeWidth(0.0);
                descriptionText.setFont(Font.font("Roboto Medium", 12.0));

                textFlow.getChildren().add(descriptionText);

                vbox.getChildren().addAll(clockInText, textFlow);

                Insets padding = new Insets(8.0, 16.0, 8.0, 16.0);
                vbox.setPadding(padding);

                String finalTaskId = taskId;
                vbox.setOnMouseClicked(event -> {
                    System.out.println("Task ID: " + finalTaskId);
                    UserController.setOperationType("update");
                    setSelectedTask(taskId, label, description, status);
                    userController.modalTask();
                });

                userController.addTaskVBox(vbox);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    public static void submitTask(String type, int id, String label, String desc, String status) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String currentDateTime = currentTime.format(formatter);

        try {
            if(type.equals("add")){
                String query = "INSERT INTO tasks (user_id, task_start, date, status, label, `desc`) VALUES (?, ?, CURDATE(), ?, ?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.setString(2, currentDateTime);
                stmt.setString(3, status);
                stmt.setString(4, label);
                stmt.setString(5, desc);
                stmt.executeUpdate();
            } else if (type.equals("update")) {
                String taskId = getSelectedTaskId();
                String query = "UPDATE tasks SET status = ?, label = ?, `desc` = ? WHERE id = ? AND user_id = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, status);
                stmt.setString(2, label);
                stmt.setString(3, desc);
                stmt.setString(4, taskId);
                stmt.setInt(5, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }
}