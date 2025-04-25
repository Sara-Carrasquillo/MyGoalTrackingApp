package edu.seminolestate.goaltrack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateCategory extends JPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> categoryDropdown;
    private JTextField categoryNameInputBox;
    private JTextField categoryDescriptionInputBox;
    private JTextField categoryColorInputBox;
    private JTextField categoryValueInputBox;

    private GoalsDatabase goalsDatabase = new GoalsDatabase(); // Reference to GoalsDatabase
    private CategoryPage categoryPage;

    /**
     * Create the panel.
     * @param contentPane 
     * @param cardLayout 
     */
    public UpdateCategory(CardLayout cardLayout, JPanel contentPane, CategoryPage categoryPage) {
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Set default size
        setPreferredSize(new Dimension(1000, 600));

        JLabel updateCategoryLabel = new JLabel("Update Category");
        updateCategoryLabel.setForeground(Color.LIGHT_GRAY);
        updateCategoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateCategoryLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        updateCategoryLabel.setBounds(380, 11, 194, 39);
        add(updateCategoryLabel);

        JButton back_Button = new JButton("Back");
        back_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPane, "CategoryPage");
            }
        });
        back_Button.setForeground(Color.GRAY);
        back_Button.setBackground(Color.WHITE);
        back_Button.setFont(new Font("Rockwell", Font.BOLD, 15));
        back_Button.setBounds(10, 11, 93, 27);
        add(back_Button);

        JLabel selectCategoryLabel = new JLabel("Select Category:");
        selectCategoryLabel.setForeground(Color.LIGHT_GRAY);
        selectCategoryLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        selectCategoryLabel.setBounds(380, 61, 194, 39);
        add(selectCategoryLabel);

        categoryDropdown = new JComboBox<>();
        categoryDropdown.setBounds(380, 95, 194, 33);
        add(categoryDropdown);

        JLabel newCategoryNameLabel = new JLabel("New Category Name:");
        newCategoryNameLabel.setForeground(Color.LIGHT_GRAY);
        newCategoryNameLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        newCategoryNameLabel.setBounds(380, 139, 194, 33);
        add(newCategoryNameLabel);

        categoryNameInputBox = new JTextField();
        categoryNameInputBox.setBounds(380, 178, 194, 33);
        add(categoryNameInputBox);

        JLabel newCategoryDescriptionLabel = new JLabel("New Category Description:");
        newCategoryDescriptionLabel.setForeground(Color.LIGHT_GRAY);
        newCategoryDescriptionLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        newCategoryDescriptionLabel.setBounds(380, 222, 194, 33);
        add(newCategoryDescriptionLabel);

        categoryDescriptionInputBox = new JTextField();
        categoryDescriptionInputBox.setBounds(380, 261, 194, 33);
        add(categoryDescriptionInputBox);

        JLabel newCategoryColorLabel = new JLabel("New Category Color:");
        newCategoryColorLabel.setForeground(Color.LIGHT_GRAY);
        newCategoryColorLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        newCategoryColorLabel.setBounds(380, 305, 194, 33);
        add(newCategoryColorLabel);

        categoryColorInputBox = new JTextField();
        categoryColorInputBox.setBounds(380, 344, 194, 33);
        add(categoryColorInputBox);

        JLabel newCategoryValueLabel = new JLabel("New Category Value:");
        newCategoryValueLabel.setForeground(Color.LIGHT_GRAY);
        newCategoryValueLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        newCategoryValueLabel.setBounds(380, 387, 194, 33);
        add(newCategoryValueLabel);

        categoryValueInputBox = new JTextField();
        categoryValueInputBox.setToolTipText("0 by default. 1 not as important 10 very important.");
        categoryValueInputBox.setBounds(380, 426, 194, 33);
        add(categoryValueInputBox);

        JButton updateCategoryButton = new JButton("Update Category");
        updateCategoryButton.setForeground(Color.GRAY);
        updateCategoryButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        updateCategoryButton.setBounds(380, 480, 194, 39);
        add(updateCategoryButton);

        updateCategoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryDropdown.getSelectedItem();
                String newCategoryName = categoryNameInputBox.getText();
                String newCategoryDescription = categoryDescriptionInputBox.getText();
                String newCategoryColor = categoryColorInputBox.getText();
                int newCategoryValue = Integer.parseInt(categoryValueInputBox.getText());

                try (Connection conn = goalsDatabase.getConnection()) {
                    String query = "UPDATE categories SET Category_Name = ?, Category_Description = ?, Category_Color = ?, Category_Value = ? WHERE Category_Name = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, newCategoryName);
                    stmt.setString(2, newCategoryDescription);
                    stmt.setString(3, newCategoryColor);
                    stmt.setInt(4, newCategoryValue);
                    stmt.setString(5, selectedCategory);
                    stmt.executeUpdate();
                    
                    // Refresh
                    categoryPage.refreshCategoryPanel(categoryPage.getMainPanel());

                    JOptionPane.showMessageDialog(null, "Category updated successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating category: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number for Category Value.");
                }
            }
        });

        // Populate the drop down with existing categories
        populateCategoryDropdown();
    }

    private void populateCategoryDropdown() {
        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "SELECT Category_Name FROM categories";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categoryDropdown.addItem(rs.getString("Category_Name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading categories: " + ex.getMessage());
        }
    }

    public CategoryPage getCategoryPage() {
        return categoryPage;
    }

    public void setCategoryPage(CategoryPage categoryPage) {
        this.categoryPage = categoryPage;
    }
}
