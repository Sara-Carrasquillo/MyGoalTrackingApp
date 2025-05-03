package edu.seminolestate.goaltrack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GoalsDatabase {
    // Database connection details
    private static final String URL = "jdbc:mysql:***********/goal_tracker";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
    	// Connection confirmation
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if (connection != null) {
                System.out.println("Connected to the database.");
            }
        } catch (SQLException e) {
            // Print SQL exception details
            e.printStackTrace();
        }
    }

    // Get connection for goals
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; 
        }
    }
}
