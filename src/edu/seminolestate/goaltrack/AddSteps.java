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

public class AddSteps extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField stepNumberInput;
    private JTextField stepDescriptionInput;
    private JCheckBox isCompleteCheckbox;
    private JTextField targetDateInput;
    private JComboBox<String> goalDropdown;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();

    public AddSteps(CardLayout cardLayout, JPanel contentPane) {
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Set default size
        setPreferredSize(new Dimension(1000, 600));

        // Back Button to steps page
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

        // Title Label
        JLabel addStepsLabel = new JLabel("Add Steps");
        addStepsLabel.setForeground(Color.LIGHT_GRAY);
        addStepsLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        addStepsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addStepsLabel.setBounds(435, 7, 150, 30);
        add(addStepsLabel);

        // Goal Selection
        JLabel goalLabel = new JLabel("Select Goal:");
        goalLabel.setForeground(Color.LIGHT_GRAY);
        goalLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        goalLabel.setBounds(357, 73, 300, 25);
        add(goalLabel);

        goalDropdown = new JComboBox<>();
        loadGoals();
        goalDropdown.setBounds(357, 103, 300, 30);
        add(goalDropdown);

        // Step Number Input
        JLabel stepNumberLabel = new JLabel("Step Number:");
        stepNumberLabel.setForeground(Color.LIGHT_GRAY);
        stepNumberLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stepNumberLabel.setBounds(357, 153, 300, 25);
        add(stepNumberLabel);

        stepNumberInput = new JTextField();
        stepNumberInput.setBounds(357, 183, 300, 30);
        add(stepNumberInput);

        // Step Description Input
        JLabel stepDescriptionLabel = new JLabel("Step Description:");
        stepDescriptionLabel.setForeground(Color.LIGHT_GRAY);
        stepDescriptionLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stepDescriptionLabel.setBounds(357, 233, 300, 25);
        add(stepDescriptionLabel);

        stepDescriptionInput = new JTextField();
        stepDescriptionInput.setBounds(357, 263, 300, 30);
        add(stepDescriptionInput);

        // Is Complete box
        JLabel isCompleteLabel = new JLabel("Completed:");
        isCompleteLabel.setForeground(Color.LIGHT_GRAY);
        isCompleteLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        isCompleteLabel.setBounds(357, 313, 300, 25);
        add(isCompleteLabel);

        isCompleteCheckbox = new JCheckBox();
        isCompleteCheckbox.setBackground(Color.DARK_GRAY);
        isCompleteCheckbox.setBounds(357, 343, 300, 30);
        add(isCompleteCheckbox);

        // Target Date Input
        JLabel targetDateLabel = new JLabel("Target Date (YYYY-MM-DD):");
        targetDateLabel.setForeground(Color.LIGHT_GRAY);
        targetDateLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        targetDateLabel.setBounds(357, 373, 300, 25);
        add(targetDateLabel);

        targetDateInput = new JTextField();
        targetDateInput.setBounds(357, 403, 300, 30);
        add(targetDateInput);

        // Add Step Button
        JButton addStepButton = new JButton("+ Add Step");
        addStepButton.setForeground(Color.GRAY);
        addStepButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        addStepButton.setBounds(422, 454, 150, 40);
        add(addStepButton);

        // Action to add the step to the database
        addStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStep();
            }
        });
    }

    // Method to load goals
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

    // Method to add a step to the database
    private void addStep() {
        String selectedGoal = (String) goalDropdown.getSelectedItem();
        int goalId = Integer.parseInt(selectedGoal.split(" ")[0]);  
        int stepNumber = Integer.parseInt(stepNumberInput.getText());
        String description = stepDescriptionInput.getText();
        boolean isComplete = isCompleteCheckbox.isSelected();
        String targetDate = targetDateInput.getText();

        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "INSERT INTO steps (Goal_ID, Step_Number, Step_Description, Is_Complete, Target_Date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, goalId);
            stmt.setInt(2, stepNumber);
            stmt.setString(3, description);
            stmt.setBoolean(4, isComplete);
            stmt.setDate(5, Date.valueOf(targetDate));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Step added successfully!");

            // Clear input fields after adding
            stepNumberInput.setText("");
            stepDescriptionInput.setText("");
            isCompleteCheckbox.setSelected(false);
            targetDateInput.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding step.");
        }
    }
}
