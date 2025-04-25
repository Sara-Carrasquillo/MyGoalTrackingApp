package edu.seminolestate.goaltrack;

import java.awt.CardLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
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

public class AddGoals extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField goal_NameInputBox;
    private JTextField targetDateInputBox;
    private JComboBox<String> categoryComboBox;

    private GoalsDatabase goalsDatabase = new GoalsDatabase(); // Reference to GoalsDatabase
    private GoalPage goalPage; // Reference to GoalPage for refreshing

    /**
     * Create the panel.
     * @param contentPane 
     * @param cardLayout 
     * @param goalPage 
     */
    public AddGoals(CardLayout cardLayout, JPanel contentPane, GoalPage goalPage) {
        this.goalPage = goalPage; // Correctly assign the goalPage variable
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Set default size
        setPreferredSize(new Dimension(1000, 600));
        
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

        // Add Goal Section
        JLabel add_GoalLabel = new JLabel("Add Goal");
        add_GoalLabel.setForeground(Color.LIGHT_GRAY);
        add_GoalLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add_GoalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add_GoalLabel.setBounds(409, 11, 112, 39);
        add(add_GoalLabel);

        goal_NameInputBox = new JTextField();
        goal_NameInputBox.setFont(new Font("Rockwell", Font.PLAIN, 12));
        goal_NameInputBox.setBounds(327, 110, 282, 33);
        add(goal_NameInputBox);
        goal_NameInputBox.setColumns(10);

        JLabel enterGoalNameLabel = new JLabel("Enter the Goal Name (1-50 characters):");
        enterGoalNameLabel.setForeground(Color.LIGHT_GRAY);
        enterGoalNameLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        enterGoalNameLabel.setBounds(327, 61, 282, 45);
        add(enterGoalNameLabel);

        JCheckBox long_TermCheckbox = new JCheckBox("Yes/No");
        long_TermCheckbox.setBackground(Color.DARK_GRAY);
        long_TermCheckbox.setForeground(Color.LIGHT_GRAY);
        long_TermCheckbox.setFont(new Font("Rockwell", Font.PLAIN, 15));
        long_TermCheckbox.setBounds(327, 215, 93, 39);
        add(long_TermCheckbox);

        JLabel goalLongTermLabel = new JLabel("Is the goal long-term?");
        goalLongTermLabel.setForeground(Color.LIGHT_GRAY);
        goalLongTermLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        goalLongTermLabel.setBounds(327, 169, 194, 39);
        add(goalLongTermLabel);

        JLabel lblNewLabel_4 = new JLabel("Enter target date (YYYY-MM-DD):");
        lblNewLabel_4.setForeground(Color.LIGHT_GRAY);
        lblNewLabel_4.setFont(new Font("Rockwell", Font.PLAIN, 15));
        lblNewLabel_4.setBounds(327, 261, 247, 33);
        add(lblNewLabel_4);

        targetDateInputBox = new JTextField();
        targetDateInputBox.setBounds(327, 305, 282, 33);
        add(targetDateInputBox);
        targetDateInputBox.setColumns(10);

        JLabel categoryLabel = new JLabel("Select Category:");
        categoryLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        categoryLabel.setForeground(Color.LIGHT_GRAY);
        categoryLabel.setBounds(327, 349, 126, 32);
        add(categoryLabel);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setBounds(327, 393, 282, 33);
        add(categoryComboBox);
        
        loadCategories();

        JButton addGoalButton = new JButton("+ Add Goal");
        addGoalButton.setForeground(Color.GRAY);
        addGoalButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        addGoalButton.setBounds(381, 437, 176, 39);
        add(addGoalButton);

        // Add Goal Action
        addGoalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String goalName = goal_NameInputBox.getText();
                boolean isLongTerm = long_TermCheckbox.isSelected();
                String targetDate = targetDateInputBox.getText();
                int categoryID = getSelectedCategoryID();

                try (Connection conn = goalsDatabase.getConnection()) {
                    String query = "INSERT INTO goals (Goal_Name, Long_Term, Target_Date, Category_ID) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, goalName);
                    stmt.setBoolean(2, isLongTerm);
                    stmt.setString(3, targetDate);
                    stmt.setInt(4, categoryID);
                    stmt.executeUpdate();
                    
                    // Clear fields after adding
                    goal_NameInputBox.setText("");
                    long_TermCheckbox.setSelected(false);
                    targetDateInputBox.setText("");

                    // Refresh the goals panel after adding a goal
                    if (goalPage != null) {
                        goalPage.refreshGoalsPanel(contentPane);
                    } else {
                        System.err.println("Cannot refresh goals.");
                    }

                    JOptionPane.showMessageDialog(null, "Goal added successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding goal: " + ex.getMessage());
                }
            }
        });
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

    // Method to retrieve selected category ID for saving a new goal
    private int getSelectedCategoryID() {
        String selectedItem = (String) categoryComboBox.getSelectedItem();
        if (selectedItem != null) {
            return Integer.parseInt(selectedItem.split(":")[0]);
        }
        return -1;  // Default case, handle appropriately
    }

    public GoalPage getGoalPage() {
        return goalPage;
    }

    public void setGoalPage(GoalPage goalPage) {
        this.goalPage = goalPage;
    }
}

