package edu.seminolestate.goaltrack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCategory extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField categoryNameInput;
    private JTextField categoryDescriptionInput;
    private JTextField categoryColorInput;
    private JTextField categoryValueInput;

    private GoalsDatabase goalsDatabase = new GoalsDatabase();
	private CategoryPage categoryPage;

    public AddCategory(CardLayout cardLayout, JPanel contentPane, CategoryPage categoryPage) {
        this.setCategoryPage(categoryPage);
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Set default size
        setPreferredSize(new Dimension(1000, 600));

        // Back Button to category page
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPane, "CategoryPage"); 
            }
        });
        backButton.setForeground(Color.GRAY);
        backButton.setBackground(Color.WHITE);
        backButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        backButton.setBounds(10, 10, 93, 27);
        add(backButton);

        // Title Label
        JLabel addCategoryLabel = new JLabel("Add Category");
        addCategoryLabel.setForeground(Color.LIGHT_GRAY);
        addCategoryLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        addCategoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addCategoryLabel.setBounds(435, 7, 150, 30);
        add(addCategoryLabel);

        // Category Name Input
        JLabel nameLabel = new JLabel("Category Name (1-50 characters):");
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        nameLabel.setBounds(357, 73, 300, 25);
        add(nameLabel);

        categoryNameInput = new JTextField();
        categoryNameInput.setBounds(357, 103, 300, 30);
        add(categoryNameInput);

        // Category Description Input
        JLabel descriptionLabel = new JLabel("Category Description:");
        descriptionLabel.setForeground(Color.LIGHT_GRAY);
        descriptionLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        descriptionLabel.setBounds(357, 153, 300, 25);
        add(descriptionLabel);

        categoryDescriptionInput = new JTextField();
        categoryDescriptionInput.setBounds(357, 183, 300, 30);
        add(categoryDescriptionInput);

        // Category Color Input
        JLabel colorLabel = new JLabel("Category Color:");
        colorLabel.setForeground(Color.LIGHT_GRAY);
        colorLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        colorLabel.setBounds(357, 233, 300, 25);
        add(colorLabel);

        categoryColorInput = new JTextField();
        categoryColorInput.setBounds(357, 263, 300, 30);
        add(categoryColorInput);

        // Category Value Input
        JLabel valueLabel = new JLabel("Category Value (0-10):");
        valueLabel.setForeground(Color.LIGHT_GRAY);
        valueLabel.setFont(new Font("Rockwell", Font.PLAIN, 15));
        valueLabel.setBounds(357, 313, 300, 25);
        add(valueLabel);

        categoryValueInput = new JTextField();
        categoryValueInput.setToolTipText("0 by default. 1 not as important 10 very important.");
        categoryValueInput.setBounds(357, 343, 300, 30);
        add(categoryValueInput);

        // Add Category Button
        JButton addCategoryButton = new JButton("+ Add Category");
        addCategoryButton.setForeground(Color.GRAY);
        addCategoryButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        addCategoryButton.setBounds(422, 384, 150, 40);
        add(addCategoryButton);

        // Action to add the category to the database
        addCategoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = categoryNameInput.getText();
                String description = categoryDescriptionInput.getText();
                String color = categoryColorInput.getText();
                int value = Integer.parseInt(categoryValueInput.getText());

                try (Connection conn = goalsDatabase.getConnection()) {
                    String query = "INSERT INTO categories (Category_Name, Category_Description, Category_Color, Category_Value) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setString(2, description);
                    stmt.setString(3, color);
                    stmt.setInt(4, value);
                    stmt.executeUpdate();
                    
                    // Clear fields after adding
                    categoryNameInput.setText("");
                    categoryDescriptionInput.setText("");
                    categoryColorInput.setText("");
                    categoryValueInput.setText("");
                    
                    // Refresh
                    categoryPage.refreshCategoryPanel(categoryPage.getMainPanel());
                    
                    JOptionPane.showMessageDialog(null, "Category added successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding category.");
                }
            }
        });
    }

	public CategoryPage getCategoryPage() {
		return categoryPage;
	}

	public void setCategoryPage(CategoryPage categoryPage) {
		this.categoryPage = categoryPage;
	}
}
