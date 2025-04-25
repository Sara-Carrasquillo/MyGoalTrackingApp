package edu.seminolestate.goaltrack;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddProgress extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField progressPercentageInput;
    private JTextField progressDescriptionInput;
    private JTextField progressDateInput;
    private JComboBox<String> goalDropdown;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();
    private ProgressPage progressPage; // Reference to ProgressPage

    public AddProgress(CardLayout cardLayout, JPanel contentPane, ProgressPage progressPage) {
        this.progressPage = progressPage; // Initialize ProgressPage reference

        setBackground(Color.DARK_GRAY);
        setLayout(null);
        setPreferredSize(new Dimension(1000, 600));

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(contentPane, "ProgressPage"));
        backButton.setForeground(Color.GRAY);
        backButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        backButton.setBounds(10, 10, 93, 27);
        add(backButton);

        // Title Label
        JLabel titleLabel = new JLabel("Add Progress");
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(435, 7, 150, 30);
        add(titleLabel);

        // Goal Drop down
        JLabel goalLabel = new JLabel("Select Goal:");
        goalLabel.setForeground(Color.LIGHT_GRAY);
        goalLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        goalLabel.setBounds(357, 73, 300, 25);
        add(goalLabel);

        goalDropdown = new JComboBox<>();
        loadGoals();
        goalDropdown.setBounds(357, 103, 300, 30);
        add(goalDropdown);

        // Progress Percentage Input
        JLabel progressPercentageLabel = new JLabel("Progress Percentage:");
        progressPercentageLabel.setForeground(Color.LIGHT_GRAY);
        progressPercentageLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        progressPercentageLabel.setBounds(357, 153, 300, 25);
        add(progressPercentageLabel);

        progressPercentageInput = new JTextField();
        progressPercentageInput.setBounds(357, 183, 300, 30);
        add(progressPercentageInput);

        // Progress Description Input
        JLabel progressDescriptionLabel = new JLabel("Progress Notes:");
        progressDescriptionLabel.setForeground(Color.LIGHT_GRAY);
        progressDescriptionLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        progressDescriptionLabel.setBounds(357, 233, 300, 25);
        add(progressDescriptionLabel);

        progressDescriptionInput = new JTextField();
        progressDescriptionInput.setBounds(357, 263, 300, 30);
        add(progressDescriptionInput);

        // Progress Date Input
        JLabel progressDateLabel = new JLabel("Current Date (YYYY-MM-DD):");
        progressDateLabel.setForeground(Color.LIGHT_GRAY);
        progressDateLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        progressDateLabel.setBounds(357, 313, 300, 25);
        add(progressDateLabel);

        progressDateInput = new JTextField();
        progressDateInput.setBounds(357, 343, 300, 30);
        add(progressDateInput);

        // Add Progress Button
        JButton addProgressButton = new JButton("+ Add Progress");
        addProgressButton.setForeground(Color.GRAY);
        addProgressButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        addProgressButton.setBounds(422, 394, 150, 40);
        add(addProgressButton);

        addProgressButton.addActionListener(e -> addProgress());
    }

    private void loadGoals() {
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Goal_ID, Goal_Name FROM goals");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String goalEntry = rs.getInt("Goal_ID") + " - " + rs.getString("Goal_Name");
                goalDropdown.addItem(goalEntry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProgress() {
        String selectedGoal = (String) goalDropdown.getSelectedItem();
        int goalId = Integer.parseInt(selectedGoal.split(" ")[0]);
        int progressPercentage = Integer.parseInt(progressPercentageInput.getText());
        String description = progressDescriptionInput.getText();
        String date = progressDateInput.getText();

        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "INSERT INTO progress (Goal_ID, Progress_Percentage, Progress_Notes, Progress_Date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, goalId);
            stmt.setInt(2, progressPercentage);
            stmt.setString(3, description);
            stmt.setDate(4, Date.valueOf(date));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Progress added successfully!");

            // Clear input fields
            progressPercentageInput.setText("");
            progressDescriptionInput.setText("");
            progressDateInput.setText("");

            // Refresh ProgressPage
            progressPage.refreshProgressPanel(progressPage.getMainPanel());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding progress.");
        }
    }
}

