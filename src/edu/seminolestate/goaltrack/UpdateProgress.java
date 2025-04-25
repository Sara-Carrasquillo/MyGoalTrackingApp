package edu.seminolestate.goaltrack;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateProgress extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField progressPercentageInput;
    private JTextField progressDescriptionInput;
    private JTextField progressDateInput;
    private JComboBox<String> progressDropdown;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();
    private ProgressPage progressPage; // Reference to ProgressPage

    public UpdateProgress(CardLayout cardLayout, JPanel contentPane, ProgressPage progressPage) {
        this.setProgressPage(progressPage); // Initialize ProgressPage reference

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
        JLabel titleLabel = new JLabel("Update Progress");
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(435, 7, 150, 30);
        add(titleLabel);

        // Progress Drop down
        JLabel progressLabel = new JLabel("Select Goal:");
        progressLabel.setForeground(Color.LIGHT_GRAY);
        progressLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        progressLabel.setBounds(357, 73, 300, 25);
        add(progressLabel);

        progressDropdown = new JComboBox<>();
        loadProgressEntries();
        progressDropdown.setBounds(357, 103, 300, 30);
        add(progressDropdown);

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
        JLabel progressDateLabel = new JLabel("Update Date (YYYY-MM-DD):");
        progressDateLabel.setForeground(Color.LIGHT_GRAY);
        progressDateLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        progressDateLabel.setBounds(357, 313, 300, 25);
        add(progressDateLabel);

        progressDateInput = new JTextField();
        progressDateInput.setBounds(357, 343, 300, 30);
        add(progressDateInput);

        // Update Progress Button
        JButton updateProgressButton = new JButton("Update Progress");
        updateProgressButton.setForeground(Color.GRAY);
        updateProgressButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        updateProgressButton.setBounds(422, 394, 150, 40);
        add(updateProgressButton);

        updateProgressButton.addActionListener(e -> updateProgress());
    }

    private void loadProgressEntries() {
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Progress_ID, Goal_Name FROM progress INNER JOIN goals ON progress.Goal_ID = goals.Goal_ID");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String progressEntry = rs.getInt("Progress_ID") + " - " + rs.getString("Goal_Name");
                progressDropdown.addItem(progressEntry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProgress() {
        String selectedProgress = (String) progressDropdown.getSelectedItem();
        if (selectedProgress == null || selectedProgress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid progress entry.");
            return;
        }

        int progressId = Integer.parseInt(selectedProgress.split(" ")[0]);

        // Fetch and update only relevant data
        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "UPDATE progress SET Progress_Percentage = ?, Progress_Notes = ?, Progress_Date = ? WHERE Progress_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            // Optionally fields can be left empty
            stmt.setInt(1, !progressPercentageInput.getText().isEmpty() ? Integer.parseInt(progressPercentageInput.getText()) : 0);
            stmt.setString(2, progressDescriptionInput.getText());
            stmt.setDate(3, !progressDateInput.getText().isEmpty() ? Date.valueOf(progressDateInput.getText()) : null);
            stmt.setInt(4, progressId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Progress updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating progress.");
        }
    }

	public ProgressPage getProgressPage() {
		return progressPage;
	}

	public void setProgressPage(ProgressPage progressPage) {
		this.progressPage = progressPage;
	}
}
