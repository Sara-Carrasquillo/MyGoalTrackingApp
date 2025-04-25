package edu.seminolestate.goaltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalPage extends JPanel {

    private static final long serialVersionUID = 1L;
    private GoalsDatabase goalsDatabase = new GoalsDatabase(); // Reference to GoalsDatabase
    private JPanel mainPanel;

    public GoalPage(CardLayout cardLayout, JPanel parentPanel) {
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());

        // Set default size
        setPreferredSize(new Dimension(1000, 600));

        // Panel to hold buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        // Main Menu button
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.setBackground(Color.DARK_GRAY);
        JButton btnBack = new JButton("Main Menu");
        btnBack.setForeground(Color.GRAY);
        btnBack.setBackground(Color.WHITE);
        btnBack.setFont(new Font("Rockwell", Font.BOLD, 15));
        btnBack.setPreferredSize(new Dimension(150, 40));
        leftButtonPanel.add(btnBack);

        btnBack.addActionListener(e -> cardLayout.show(parentPanel, "MainMenu"));

        // Center-aligned label
        JLabel lblNewLabel = new JLabel("Goals", SwingConstants.CENTER);
        lblNewLabel.setForeground(Color.LIGHT_GRAY);
        lblNewLabel.setFont(new Font("Rockwell", Font.BOLD, 20));

        // Add/Update/Delete button panel
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(Color.DARK_GRAY);
        JButton btnAddGoal = new JButton("+ Add");
        btnAddGoal.setPreferredSize(new Dimension(100, 40));
        btnAddGoal.setForeground(Color.GRAY);
        btnAddGoal.setFont(new Font("Rockwell", Font.BOLD, 15));
        btnAddGoal.setBackground(Color.WHITE);
        rightButtonPanel.add(btnAddGoal);

        JButton btnUpdateGoal = new JButton("Update");
        btnUpdateGoal.setPreferredSize(new Dimension(100, 40));
        btnUpdateGoal.setForeground(Color.GRAY);
        btnUpdateGoal.setFont(new Font("Rockwell", Font.BOLD, 15));
        btnUpdateGoal.setBackground(Color.WHITE);
        rightButtonPanel.add(btnUpdateGoal);

        JButton deleteGoalButton = new JButton("Delete");
        deleteGoalButton.setPreferredSize(new Dimension(100, 40));
        deleteGoalButton.setForeground(Color.GRAY);
        deleteGoalButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        deleteGoalButton.setBackground(Color.WHITE);
        rightButtonPanel.add(deleteGoalButton);

        deleteGoalButton.addActionListener(e -> {
            String goalName = JOptionPane.showInputDialog(
                    GoalPage.this, "Enter the Goal Name to delete:", "Delete Goal", JOptionPane.WARNING_MESSAGE);

            if (goalName != null && !goalName.isEmpty()) {
                deleteGoal(goalName);
                refreshGoalsPanel(mainPanel); // Refresh the goal list
            }
        });

        btnAddGoal.addActionListener(e -> cardLayout.show(parentPanel, "AddGoals"));
        btnUpdateGoal.addActionListener(e -> cardLayout.show(parentPanel, "UpdateGoals"));

        // Top panel
        topPanel.add(leftButtonPanel, BorderLayout.WEST);
        topPanel.add(lblNewLabel, BorderLayout.CENTER);
        topPanel.add(rightButtonPanel, BorderLayout.EAST);

        // Panel Added to the main layout
        add(topPanel, BorderLayout.NORTH);

        // Main content panel setup
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        loadGoals(mainPanel);  // Load the goals directly
    }

    // Method to load goals
    private void loadGoals(JPanel mainPanel) {
        mainPanel.removeAll();

        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "SELECT * FROM goals";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JPanel goalPanel = new JPanel();
                goalPanel.setLayout(new BoxLayout(goalPanel, BoxLayout.Y_AXIS));
                goalPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                goalPanel.setBackground(Color.GRAY);

                String goalName = rs.getString("Goal_Name");
                boolean isLongTerm = rs.getBoolean("Long_Term");
                String targetDate = rs.getString("Target_Date");

                JLabel goalLabel = new JLabel("Goal: " + goalName);
                goalLabel.setFont(new Font("Arial", Font.BOLD, 18));
                goalLabel.setForeground(Color.WHITE);

                JLabel detailLabel = new JLabel("Long-Term: " + isLongTerm + ", Target Date: " + targetDate);
                detailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                detailLabel.setForeground(Color.LIGHT_GRAY);

                goalPanel.add(goalLabel);
                goalPanel.add(detailLabel);

                mainPanel.add(goalPanel);
                mainPanel.add(Box.createVerticalStrut(10));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }
    // Reload categories from the database
    public void refreshGoalsPanel(JPanel mainPanel) {
        mainPanel.removeAll();
        loadGoals(mainPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Method to delete a goal by name
    private void deleteGoal(String goalName) {
        String deleteSQL = "DELETE FROM goals WHERE Goal_Name = ?";
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setString(1, goalName);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(GoalPage.this, "Goal deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(GoalPage.this, "Goal not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(GoalPage.this, "Error deleting goal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 // Getter for main panel
 	public JPanel getMainPanel() {
 		return mainPanel;
 	}
}
