package edu.seminolestate.goaltrack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateSteps extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField stepNumberInput;
    private JTextField stepDescriptionInput;
    private JCheckBox isCompleteCheckbox;
    private JTextField targetDateInput;
    private JComboBox<String> goalDropdown;
    private JComboBox<String> stepDropdown;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();

    // Constructor to initialize the panel
    public UpdateSteps(CardLayout cardLayout, JPanel contentPane) {
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        setPreferredSize(new Dimension(1000, 600));

        // Back button to return 
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPane, "StepsPage");
            }
        });
        backButton.setForeground(Color.GRAY);
        backButton.setBackground(Color.WHITE);
        backButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        backButton.setBounds(10, 10, 93, 27);
        add(backButton);

        // Header label for the Update Steps panel
        JLabel updateStepsLabel = new JLabel("Update Steps");
        updateStepsLabel.setForeground(Color.LIGHT_GRAY);
        updateStepsLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        updateStepsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateStepsLabel.setBounds(435, 7, 150, 30);
        add(updateStepsLabel);

        // Label and drop down for selecting a goal
        JLabel goalLabel = new JLabel("Select Goal:");
        goalLabel.setForeground(Color.LIGHT_GRAY);
        goalLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        goalLabel.setBounds(357, 73, 300, 25);
        add(goalLabel);

        goalDropdown = new JComboBox<>();
        loadGoals(); // Populate the goal drop down with data from the database
        goalDropdown.setBounds(357, 103, 300, 30);
        add(goalDropdown);

        // Label and drop down for selecting a step
        JLabel stepLabel = new JLabel("Select Step:");
        stepLabel.setForeground(Color.LIGHT_GRAY);
        stepLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stepLabel.setBounds(357, 153, 300, 25);
        add(stepLabel);

        stepDropdown = new JComboBox<>();
        loadSteps(); // Populate the step drop down with data from the database
        stepDropdown.setBounds(357, 183, 300, 30);
        stepDropdown.addActionListener(e -> loadStepDetails()); // Load details when a step is selected
        add(stepDropdown);

        // Label and input field for the step number
        JLabel stepNumberLabel = new JLabel("Step Number:");
        stepNumberLabel.setForeground(Color.LIGHT_GRAY);
        stepNumberLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stepNumberLabel.setBounds(357, 213, 300, 25);
        add(stepNumberLabel);

        stepNumberInput = new JTextField();
        stepNumberInput.setBounds(357, 243, 300, 30);
        add(stepNumberInput);

        // Label and input field for the step description
        JLabel stepDescriptionLabel = new JLabel("Step Description:");
        stepDescriptionLabel.setForeground(Color.LIGHT_GRAY);
        stepDescriptionLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stepDescriptionLabel.setBounds(357, 283, 300, 25);
        add(stepDescriptionLabel);

        stepDescriptionInput = new JTextField();
        stepDescriptionInput.setBounds(357, 313, 300, 30);
        add(stepDescriptionInput);

        // Check box for marking the step as complete
        JLabel isCompleteLabel = new JLabel("Completed:");
        isCompleteLabel.setForeground(Color.LIGHT_GRAY);
        isCompleteLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        isCompleteLabel.setBounds(357, 353, 300, 25);
        add(isCompleteLabel);

        isCompleteCheckbox = new JCheckBox();
        isCompleteCheckbox.setBackground(Color.DARK_GRAY);
        isCompleteCheckbox.setBounds(357, 383, 300, 30);
        add(isCompleteCheckbox);

        // Label and input field for the target date
        JLabel targetDateLabel = new JLabel("Target Date (YYYY-MM-DD):");
        targetDateLabel.setForeground(Color.LIGHT_GRAY);
        targetDateLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        targetDateLabel.setBounds(357, 413, 300, 25);
        add(targetDateLabel);

        targetDateInput = new JTextField();
        targetDateInput.setBounds(357, 443, 300, 30);
        add(targetDateInput);

        // Button to update the step with entered details
        JButton updateStepButton = new JButton("Update Step");
        updateStepButton.setForeground(Color.GRAY);
        updateStepButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        updateStepButton.setBounds(422, 484, 150, 40);
        add(updateStepButton);

        updateStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStep(); // Calls update method to save changes
            }
        });
    }

    // Loads goals from the database drop down
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

    // Loads steps from the database into drop down
    private void loadSteps() {
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Step_ID, Step_Description FROM steps");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String stepEntry = rs.getInt("Step_ID") + " - " + rs.getString("Step_Description");
                stepDropdown.addItem(stepEntry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Loads step details 
    private void loadStepDetails() {
        String selectedStep = (String) stepDropdown.getSelectedItem();
        if (selectedStep != null) {
            int stepId = Integer.parseInt(selectedStep.split(" ")[0]);

            try (Connection conn = goalsDatabase.getConnection()) {
                String query = "SELECT Step_Number, Step_Description, Is_Complete, Target_Date FROM steps WHERE Step_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, stepId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    stepNumberInput.setText(String.valueOf(rs.getInt("Step_Number")));
                    stepDescriptionInput.setText(rs.getString("Step_Description"));
                    isCompleteCheckbox.setSelected(rs.getBoolean("Is_Complete"));
                    targetDateInput.setText(rs.getString("Target_Date"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Updates SQL
    private void updateStep() {
        String selectedStep = (String) stepDropdown.getSelectedItem();
        int stepId = Integer.parseInt(selectedStep.split(" ")[0]);
        int stepNumber = Integer.parseInt(stepNumberInput.getText());
        String description = stepDescriptionInput.getText();
        boolean isComplete = isCompleteCheckbox.isSelected();
        String targetDate = targetDateInput.getText();

        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "UPDATE steps SET Step_Number = ?, Step_Description = ?, Is_Complete = ?, Target_Date = ? WHERE Step_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, stepNumber);
            stmt.setString(2, description);
            stmt.setBoolean(3, isComplete);
            stmt.setDate(4, Date.valueOf(targetDate));
            stmt.setInt(5, stepId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Step updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating step.");
        }
    }
}


