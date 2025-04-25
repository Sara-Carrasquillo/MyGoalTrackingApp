package edu.seminolestate.goaltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StepsPage extends JPanel {

    private static final long serialVersionUID = 1L;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();

    public StepsPage(CardLayout cardLayout, JPanel parentPanel) {
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

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(parentPanel, "MainMenu");
            }
        });

        // Main label
        JLabel lblNewLabel = new JLabel("Steps", SwingConstants.CENTER);
        lblNewLabel.setForeground(Color.LIGHT_GRAY);
        lblNewLabel.setFont(new Font("Rockwell", Font.BOLD, 20));

        // Add/Update/Delete button panel
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(Color.DARK_GRAY);

        // Add Step button
        JButton addStepBtn = new JButton("+ Add");
        addStepBtn.setForeground(Color.GRAY);
        addStepBtn.setBackground(Color.WHITE);
        addStepBtn.setFont(new Font("Rockwell", Font.BOLD, 15));
        addStepBtn.setPreferredSize(new Dimension(100, 40));
        rightButtonPanel.add(addStepBtn);

        addStepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(parentPanel, "AddSteps");
            }
        });

        // Update Step button
        JButton updateStepBtn = new JButton("Update");
        updateStepBtn.setForeground(Color.GRAY);
        updateStepBtn.setBackground(Color.WHITE);
        updateStepBtn.setFont(new Font("Rockwell", Font.BOLD, 15));
        updateStepBtn.setPreferredSize(new Dimension(100, 40));
        rightButtonPanel.add(updateStepBtn);

        updateStepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(parentPanel, "UpdateSteps");
            }
        });
        
        // Delete Step button
        JButton deleteStepBtn = new JButton("Delete");
        deleteStepBtn.setForeground(Color.GRAY);
        deleteStepBtn.setBackground(Color.WHITE);
        deleteStepBtn.setFont(new Font("Rockwell", Font.BOLD, 15));
        deleteStepBtn.setPreferredSize(new Dimension(100, 40));
        rightButtonPanel.add(deleteStepBtn);

        deleteStepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String goalName = JOptionPane.showInputDialog(
                        StepsPage.this, "Enter the Goal Name:", "Delete Step", JOptionPane.WARNING_MESSAGE);
                
                if (goalName != null && !goalName.isEmpty()) {
                    String stepNumberStr = JOptionPane.showInputDialog(
                            StepsPage.this, "Enter the Step Number:", "Delete Step", JOptionPane.WARNING_MESSAGE);

                    if (stepNumberStr != null && !stepNumberStr.isEmpty()) {
                        try {
                            int stepNumber = Integer.parseInt(stepNumberStr);
                            deleteStep(goalName, stepNumber);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(StepsPage.this, "Invalid step number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Top panel
        topPanel.add(leftButtonPanel, BorderLayout.WEST);
        topPanel.add(lblNewLabel, BorderLayout.CENTER);
        topPanel.add(rightButtonPanel, BorderLayout.EAST);

        // Top panel to the main layout
        add(topPanel, BorderLayout.NORTH);

        // Main content panel setup for displaying steps
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        loadSteps(mainPanel);
    }

    private void loadSteps(JPanel mainPanel) {
        mainPanel.removeAll(); 

        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "SELECT g.Goal_Name, s.Step_Number, s.Step_Description, s.Is_Complete, s.Target_Date " +
                           "FROM steps s JOIN goals g ON s.Goal_ID = g.Goal_ID " +
                           "ORDER BY g.Goal_Name, s.Step_Number";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            String currentGoal = "";
            JPanel goalPanel = null;

            while (rs.next()) {
                String goalName = rs.getString("Goal_Name");
                int stepNumber = rs.getInt("Step_Number");
                String stepDescription = rs.getString("Step_Description");
                String stepStatus = rs.getString("Is_Complete").equals("1") ? "Complete" : "In Progress";
                String stepDeadline = rs.getString("Target_Date");

             // New section for each new goal
                if (!goalName.equals(currentGoal)) {
                    if (goalPanel != null) {                        
                        JScrollPane goalScrollPane = new JScrollPane(goalPanel);
                        goalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                        goalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        goalScrollPane.setPreferredSize(new Dimension(900, 200)); 

                        mainPanel.add(goalScrollPane); 
                        mainPanel.add(Box.createVerticalStrut(15)); // Space between goals
                    }

                    // New panel for each goal section
                    goalPanel = new JPanel();
                    goalPanel.setLayout(new BoxLayout(goalPanel, BoxLayout.Y_AXIS));
                    goalPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    goalPanel.setBackground(new Color(60, 63, 65));

                    // Goal name header
                    JLabel goalLabel = new JLabel(goalName);
                    goalLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    goalLabel.setForeground(Color.WHITE);
                    goalPanel.add(goalLabel);

                    currentGoal = goalName;
                }

                // Step item panel
                JPanel stepPanel = new JPanel();
                stepPanel.setLayout(new GridLayout(1, 4, 10, 0)); // Adds spacing between columns
                stepPanel.setBackground(new Color(75, 75, 75));
                stepPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));

                // Step details
                JLabel stepNumberLabel = new JLabel("Step " + stepNumber);
                stepNumberLabel.setForeground(Color.LIGHT_GRAY);

                JLabel stepDescriptionLabel = new JLabel(stepDescription);
                stepDescriptionLabel.setForeground(Color.LIGHT_GRAY);

                JLabel stepStatusLabel = new JLabel(stepStatus);
                stepStatusLabel.setForeground(stepStatus.equals("Complete") ? new Color(34, 139, 34) : Color.ORANGE);

                JLabel stepDeadlineLabel = new JLabel(stepDeadline);
                stepDeadlineLabel.setForeground(Color.LIGHT_GRAY);

                // Add to step panel
                stepPanel.add(stepNumberLabel);
                stepPanel.add(stepDescriptionLabel);
                stepPanel.add(stepStatusLabel);
                stepPanel.add(stepDeadlineLabel);

                // Add the step panel to the goal panel
                goalPanel.add(Box.createVerticalStrut(5)); // Space between steps
                goalPanel.add(stepPanel);
            }

            // Add the last goal panel to the main panel if it exists
            if (goalPanel != null) {
                mainPanel.add(goalPanel);
            }

            // Refresh the main panel to show changes
            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // Method to delete a step using goal name and step number
    public void deleteStep(String goalName, int stepNumber) {
        String deleteSQL = "DELETE FROM steps WHERE Goal_ID = (SELECT Goal_ID FROM goals WHERE Goal_Name = ?) AND Step_Number = ?";
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setString(1, goalName);
            stmt.setInt(2, stepNumber);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(StepsPage.this, "Step deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(StepsPage.this, "Step not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(StepsPage.this, "Error deleting step.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}

