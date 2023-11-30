package project.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

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

    public void createTaskVBoxes(UserController userController, int id) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
         // Assuming the account ID is stored in a variable called accountId
        String currentDay = LocalDate.now().toString(); // Get the current day as a string

        String query = "SELECT * FROM tasks WHERE user_id = '" + id + "' AND DATE(date) = '" + currentDay + "'";

        try {
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String taskId = null;
                String clockInTime = UserController.timeFormat(rs.getString("task_start"));
                String description = rs.getString("desc");
                taskId = rs.getString("id");

                VBox vbox = new VBox();
                vbox.setPrefWidth(100.0);
                vbox.setSpacing(5.0);
                vbox.setStyle("-fx-background-color: #F6F6F6; -fx-border-style: solid; -fx-border-width: 4; -fx-border-color: transparent transparent transparent #396AFC;");
                vbox.setCursor(Cursor.cursor("hand"));

                Text clockInText = new Text(clockInTime);
                clockInText.setFill(Color.web("#3b415a"));
                clockInText.setStrokeType(StrokeType.OUTSIDE);
                clockInText.setStrokeWidth(0.0);
                clockInText.setFont(Font.font("Roboto Black", 18.0));

                TextFlow textFlow = new TextFlow();
                textFlow.setLineSpacing(5.0);
                textFlow.setOpacity(0.5);
//                textFlow.setPrefHeight(200.0);
//                textFlow.setPrefWidth(200.0);

                Text descriptionText = new Text(description);
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
                    System.out.println("Clock In Time: " + clockInTime);
                    System.out.println("Description: " + description);
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
}