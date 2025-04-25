package edu.seminolestate.goaltrack;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateGoals extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField newTargetDateInputBox;
    private JTextField new_GoalNameInputBox;
    private JComboBox<String> goalComboBox;
    private JComboBox<String> categoryComboBox;
    private GoalsDatabase goalsDatabase = new GoalsDatabase(); // Reference to GoalsDatabase
    private GoalPage goalPage;

    /**
     * Create the panel.
     * @param contentPane 
     * @param cardLayout 
     * @param goalPage 
     */
    public UpdateGoals(CardLayout cardLayout, JPanel contentPane, GoalPage goalPage) {
        this.goalPage = goalPage; // Assign the goalPage
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Set default size
        setPreferredSize(new Dimension(1000, 600));

        // Update Goal Section
        JLabel update_GoalLabel = new JLabel("Update Goal");
        update_GoalLabel.setForeground(Color.LIGHT_GRAY);
        update_GoalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        update_GoalLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        update_GoalLabel.setBounds(380, 11, 194, 39);
        add(update_GoalLabel);

        // Switch to goals page
        JButton back_Button = new JButton("Back");
        back_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPane, "GoalPage");
            }
        });
        back_Button.setForeground(Color.GRAY);
        back_Button.setBackground(Color.WHITE);
        back_Button.setFont(new Font("Rockwell", Font.BOLD, 15));
        back_Button.setBounds(10, 11, 93, 27);
        add(back_Button);

        JLabel selectGoalLabel = new JLabel("Select the Goal:");
        selectGoalLabel.setForeground(Color.LIGHT_GRAY);
        selectGoalLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        selectGoalLabel.setBounds(380, 61, 194, 39);
        add(selectGoalLabel);

        goalComboBox = new JComboBox<>();
        goalComboBox.setBounds(380, 95, 282, 33);
        add(goalComboBox);
        loadGoals(); // Load goals into the JComboBox

        JLabel newGoalNameLabel = new JLabel("Enter new Goal Name:");
        newGoalNameLabel.setForeground(Color.LIGHT_GRAY);
        newGoalNameLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        newGoalNameLabel.setBounds(380, 139, 194, 33);
        add(newGoalNameLabel);

        new_GoalNameInputBox = new JTextField();
        new_GoalNameInputBox.setBounds(380, 178, 282, 33);
        add(new_GoalNameInputBox);

        JCheckBox stillLongTermCheckBox = new JCheckBox(" Yes/No");
        stillLongTermCheckBox.setForeground(Color.LIGHT_GRAY);
        stillLongTermCheckBox.setBackground(Color.DARK_GRAY);
        stillLongTermCheckBox.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stillLongTermCheckBox.setBounds(380, 262, 90, 39);
        add(stillLongTermCheckBox);

        JLabel stillLongTermLabel = new JLabel("Is the goal long-term?");
        stillLongTermLabel.setForeground(Color.LIGHT_GRAY);
        stillLongTermLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        stillLongTermLabel.setBounds(380, 222, 194, 33);
        add(stillLongTermLabel);

        JLabel newTargetDateLabel = new JLabel("Enter new target date:");
        newTargetDateLabel.setForeground(Color.LIGHT_GRAY);
        newTargetDateLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        newTargetDateLabel.setBounds(380, 308, 194, 33);
        add(newTargetDateLabel);

        newTargetDateInputBox = new JTextField();
        newTargetDateInputBox.setBounds(380, 343, 282, 33);
        add(newTargetDateInputBox);

        JLabel categoryLabel = new JLabel("Select Category:");
        categoryLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        categoryLabel.setForeground(Color.LIGHT_GRAY);
        categoryLabel.setBounds(380, 387, 126, 32);
        add(categoryLabel);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setBounds(380, 430, 282, 33);
        add(categoryComboBox);
        loadCategories(); // Load categories into the JComboBox

        JButton updateGoalButton = new JButton("Update Goal");
        updateGoalButton.setForeground(Color.GRAY);
        updateGoalButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        updateGoalButton.setBounds(380, 485, 194, 39);
        add(updateGoalButton);

        updateGoalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedGoal = (String) goalComboBox.getSelectedItem();
                if (selectedGoal == null || selectedGoal.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a valid goal.");
                    return;
                }

                String newGoalName = new_GoalNameInputBox.getText();
                boolean isLongTerm = stillLongTermCheckBox.isSelected();
                String newTargetDate = newTargetDateInputBox.getText();
                int categoryID = getSelectedCategoryID();

                if (categoryID == -1) {
                    // Handle case when no category is selected
                    JOptionPane.showMessageDialog(null, "Please select a valid category.");
                    return;
                }

                try (Connection conn = goalsDatabase.getConnection()) {
                    String query = "UPDATE goals SET Goal_Name = ?, Long_Term = ?, Target_Date = ?, Category_ID = ? WHERE Goal_Name = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, newGoalName);
                    stmt.setBoolean(2, isLongTerm);
                    stmt.setString(3, newTargetDate);
                    stmt.setInt(4, categoryID); // Set the selected category ID
                    stmt.setString(5, selectedGoal); // Use the selected goal name
                    stmt.executeUpdate();

                    // Clear fields after updating
                    new_GoalNameInputBox.setText("");
                    stillLongTermCheckBox.setSelected(false);
                    newTargetDateInputBox.setText("");

                    // Refresh the goals panel
                    if (goalPage != null) {
                        goalPage.refreshGoalsPanel(contentPane);
                    } else {
                        System.err.println("Cannot refresh goals.");
                    }

                    JOptionPane.showMessageDialog(null, "Goal updated successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating goal: " + ex.getMessage());
                }
            }
        });
    }

    // Method to load goals into the JComboBox
    private void loadGoals() {
        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "SELECT Goal_Name FROM goals";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                goalComboBox.addItem(rs.getString("Goal_Name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to load categories into the JComboBox
    private void loadCategories() {
        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "SELECT Category_ID, Category_Name FROM categories";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String categoryItem = rs.getInt("Category_ID") + ": " + rs.getString("Category_Name");
                categoryComboBox.addItem(categoryItem);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to retrieve selected category ID for updating a goal
    private int getSelectedCategoryID() {
        String selectedItem = (String) categoryComboBox.getSelectedItem();
        if (selectedItem != null) {
            return Integer.parseInt(selectedItem.split(":")[0]);
        }
        return -1; // Default case, handle appropriately
    }

    // Method to refresh goals panel
    public void refreshGoalsPanel() {
        goalPage.refreshGoalsPanel(goalPage);
    }
}
