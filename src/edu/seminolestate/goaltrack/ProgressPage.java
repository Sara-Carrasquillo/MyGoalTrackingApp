package edu.seminolestate.goaltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProgressPage extends JPanel {

    private static final long serialVersionUID = 1L;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();
    private JPanel mainPanel; // Declare mainPanel as an instance variable

    public ProgressPage(CardLayout cardLayout, JPanel parentPanel) {
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 600));

        // Top panel for navigation and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        // Main Menu button
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.setBackground(Color.DARK_GRAY);
        JButton btnBack = new JButton("Main Menu");
        styleButton(btnBack);
        leftButtonPanel.add(btnBack);

        btnBack.addActionListener(e -> cardLayout.show(parentPanel, "MainMenu"));

        // Page title
        JLabel lblTitle = new JLabel("Progress", SwingConstants.CENTER);
        lblTitle.setForeground(Color.LIGHT_GRAY);
        lblTitle.setFont(new Font("Rockwell", Font.BOLD, 20));

        // Action buttons
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(Color.DARK_GRAY);

        JButton addProgressBtn = new JButton("+ Add");
        styleButton(addProgressBtn);
        addProgressBtn.addActionListener(e -> cardLayout.show(parentPanel, "AddProgress"));
        rightButtonPanel.add(addProgressBtn);

        JButton updateProgressBtn = new JButton("Update");
        styleButton(updateProgressBtn);
        updateProgressBtn.addActionListener(e -> cardLayout.show(parentPanel, "UpdateProgress"));
        rightButtonPanel.add(updateProgressBtn);

        JButton deleteProgressBtn = new JButton("Delete");
        styleButton(deleteProgressBtn);
        deleteProgressBtn.addActionListener(e -> {
            String goalName = JOptionPane.showInputDialog(
                this, 
                "Enter the goal name to delete progress for:", 
                "Reset Progress", 
                JOptionPane.WARNING_MESSAGE
            );

            if (goalName != null && !goalName.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete progress for \"" + goalName + "\"?",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteProgress(goalName);
                }
            }
        });
        rightButtonPanel.add(deleteProgressBtn);

        // Assemble top panel
        topPanel.add(leftButtonPanel, BorderLayout.WEST);
        topPanel.add(lblTitle, BorderLayout.CENTER);
        topPanel.add(rightButtonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Initialize the mainPanel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        loadProgress(mainPanel);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void styleButton(JButton button) {
        button.setForeground(Color.GRAY);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Rockwell", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(120, 40));
    }

    private void loadProgress(JPanel mainPanel) {
        mainPanel.removeAll();

        try (Connection conn = goalsDatabase.getConnection()) {
            // Query to calculate progress for each goal
            String query = """
                SELECT g.Goal_Name, 
                       COUNT(s.Step_ID) AS TotalSteps, 
                       SUM(CASE WHEN s.Is_Complete = 1 THEN 1 ELSE 0 END) AS CompletedSteps 
                FROM goals g
                LEFT JOIN steps s ON g.Goal_ID = s.Goal_ID
                GROUP BY g.Goal_ID, g.Goal_Name
                """;
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int totalGoals = 0;
            int goalsCompleted = 0;

            while (rs.next()) {
                String goalName = rs.getString("Goal_Name");
                int totalSteps = rs.getInt("TotalSteps");
                int completedSteps = rs.getInt("CompletedSteps");
                int progressPercentage = (totalSteps == 0) ? 0 : (completedSteps * 100 / totalSteps);

                if (progressPercentage == 100) {
                    goalsCompleted++;
                }
                totalGoals++;

                // Create goal panel
                JPanel goalPanel = new JPanel();
                goalPanel.setLayout(new BoxLayout(goalPanel, BoxLayout.Y_AXIS));
                goalPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                goalPanel.setBackground(new Color(60, 63, 65));

                JLabel goalLabel = new JLabel(goalName);
                goalLabel.setFont(new Font("Arial", Font.BOLD, 16));
                goalLabel.setForeground(Color.WHITE);

                // Progress bar
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue(progressPercentage);
                progressBar.setString(progressPercentage + "%");
                progressBar.setStringPainted(true);
                progressBar.setForeground(progressPercentage == 100 ? Color.GREEN : Color.ORANGE);
                progressBar.setBackground(Color.LIGHT_GRAY);
                progressBar.setPreferredSize(new Dimension(200, 20));

                // Information panel
                JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                statsPanel.setBackground(new Color(60, 63, 65));
                JLabel totalStepsLabel = new JLabel("Total Steps: " + totalSteps);
                JLabel completedStepsLabel = new JLabel("Completed: " + completedSteps);
                totalStepsLabel.setForeground(Color.LIGHT_GRAY);
                completedStepsLabel.setForeground(Color.LIGHT_GRAY);

                statsPanel.add(totalStepsLabel);
                statsPanel.add(Box.createHorizontalStrut(10));
                statsPanel.add(completedStepsLabel);

                // Add components to goal panel
                goalPanel.add(goalLabel);
                goalPanel.add(Box.createVerticalStrut(5));
                goalPanel.add(progressBar);
                goalPanel.add(Box.createVerticalStrut(5));
                goalPanel.add(statsPanel);

                mainPanel.add(goalPanel);
                mainPanel.add(Box.createVerticalStrut(10));
            }

            // Add overall progress panel
            JPanel overallProgressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            overallProgressPanel.setBackground(new Color(60, 63, 65));
            JLabel overallProgressLabel = new JLabel(
                "Overall Progress: " + goalsCompleted + " / " + totalGoals + " Goals Completed");
            overallProgressLabel.setFont(new Font("Arial", Font.BOLD, 16));
            overallProgressLabel.setForeground(Color.WHITE);
            overallProgressPanel.add(overallProgressLabel);

            mainPanel.add(overallProgressPanel);

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading progress.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void refreshProgressPanel(JPanel mainPanel) {
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void deleteProgress(String goalName) {
        String resetSQL = """
            UPDATE Progress p
            JOIN Goals g ON p.Goal_ID = g.Goal_ID
            SET p.Progress_Percentage = 0, p.Progress_Notes = NULL, p.Is_Complete = 0
            WHERE g.Goal_Name = ?
            """;
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(resetSQL)) {
            stmt.setString(1, goalName);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Progress has been deleted for \"" + goalName + "\".", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadProgress((JPanel) ((JScrollPane) getComponent(1)).getViewport().getView()); // Reload progress
            } else {
                JOptionPane.showMessageDialog(
                    this, 
                    "No progress found for the specified goal.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Error deleting progress. Please try again.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

