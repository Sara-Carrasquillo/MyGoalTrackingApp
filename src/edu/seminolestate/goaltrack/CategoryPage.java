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

public class CategoryPage extends JPanel {

    private static final long serialVersionUID = 1L;
    private GoalsDatabase goalsDatabase = new GoalsDatabase();
    private JPanel mainPanel;

    public CategoryPage(CardLayout cardLayout, JPanel parentPanel) {
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

        // Center-aligned label
        JLabel lblNewLabel = new JLabel("Categories", SwingConstants.CENTER);
        lblNewLabel.setForeground(Color.LIGHT_GRAY);
        lblNewLabel.setFont(new Font("Rockwell", Font.BOLD, 20));

        // Add/Update button panel
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(Color.DARK_GRAY);
        JButton addCategoryBtn = new JButton("+ Add");
        addCategoryBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		cardLayout.show(parentPanel, "AddCategory");
        	}
        });
        addCategoryBtn.setPreferredSize(new Dimension(100, 40));
        addCategoryBtn.setForeground(Color.GRAY);
        addCategoryBtn.setFont(new Font("Rockwell", Font.BOLD, 15));
        addCategoryBtn.setBackground(Color.WHITE);
        rightButtonPanel.add(addCategoryBtn);

        JButton updateCategoryBtn = new JButton("Update");
        updateCategoryBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		cardLayout.show(parentPanel, "UpdateCategory");
        	}
        });
        
     // Delete button
        JButton deleteCategoryBtn = new JButton("Delete");
        deleteCategoryBtn.setPreferredSize(new Dimension(100, 40));
        deleteCategoryBtn.setForeground(Color.GRAY);
        deleteCategoryBtn.setFont(new Font("Rockwell", Font.BOLD, 15));
        deleteCategoryBtn.setBackground(Color.WHITE);
        rightButtonPanel.add(deleteCategoryBtn);

        deleteCategoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String categoryName = JOptionPane.showInputDialog(
                        CategoryPage.this, "Enter the Category Name to delete:", "Delete Category", JOptionPane.WARNING_MESSAGE);

                if (categoryName != null && !categoryName.isEmpty()) {
                    deleteCategory(categoryName);
                    refreshCategoryPanel(mainPanel);
                }
            }
        });
        
        updateCategoryBtn.setPreferredSize(new Dimension(100, 40));
        updateCategoryBtn.setForeground(Color.GRAY);
        updateCategoryBtn.setFont(new Font("Rockwell", Font.BOLD, 15));
        updateCategoryBtn.setBackground(Color.WHITE);
        rightButtonPanel.add(updateCategoryBtn);

        // Top panel
        topPanel.add(leftButtonPanel, BorderLayout.WEST);
        topPanel.add(lblNewLabel, BorderLayout.CENTER);
        topPanel.add(rightButtonPanel, BorderLayout.EAST);

        // Panel Added to the main layout
        add(topPanel, BorderLayout.NORTH);

        // Main content panel setup
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        loadCategories(mainPanel);
        
    }

    private void loadCategories(JPanel mainPanel) {
        try (Connection conn = goalsDatabase.getConnection()) {
            String query = "SELECT c.Category_ID, c.Category_Name, c.Category_Description, " +
                           "c.Category_Color, c.Category_Value, g.Goal_Name " +
                           "FROM categories c LEFT JOIN goals g ON c.Category_ID = g.Category_ID " +
                           "ORDER BY c.Category_Name";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            String currentCategory = "";
            JPanel categoryPanel = null;

            while (rs.next()) {
                int categoryId = rs.getInt("Category_ID");
                String categoryName = rs.getString("Category_Name");
                String categoryDescription = rs.getString("Category_Description");
                String colorHex = rs.getString("Category_Color");
                int categoryValue = rs.getInt("Category_Value");
                String goalName = rs.getString("Goal_Name");

                if (!categoryName.equals(currentCategory)) {
                    if (categoryPanel != null) {
                        mainPanel.add(categoryPanel);
                        mainPanel.add(Box.createVerticalStrut(20));
                    }

                    categoryPanel = new JPanel();
                    categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
                    categoryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    
                    // Set background color
                    try {
                        categoryPanel.setBackground(Color.decode(colorHex));
                    } catch (NumberFormatException e) {
                        categoryPanel.setBackground(new Color(50, 50, 50)); // default color
                    }

                    JLabel categoryLabel = new JLabel("ID:" + categoryId + " - " + categoryName + ": " + categoryDescription);
                    categoryLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    categoryLabel.setForeground(Color.WHITE);

                    // Value label with color
                    JLabel importanceLabel = new JLabel(String.valueOf(categoryValue));
                    importanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    importanceLabel.setForeground(getImportanceColor(categoryValue));

                    categoryPanel.add(categoryLabel);
                    categoryPanel.add(importanceLabel);

                    currentCategory = categoryName;
                }

                if (goalName != null) {
                    JLabel goalLabel = new JLabel(" • " + goalName);
                    goalLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    goalLabel.setForeground(Color.LIGHT_GRAY);
                    goalLabel.setBorder(new EmptyBorder(5, 20, 5, 5));
                    categoryPanel.add(goalLabel);
                }
            }

            if (categoryPanel != null) {
                mainPanel.add(categoryPanel);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Get color based on importance
    private Color getImportanceColor(int value) {
        if (value <= 3) return Color.GREEN;
        else if (value <= 6) return Color.YELLOW;
        else if (value <= 8) return Color.ORANGE;
        else return Color.RED;
    }
    
    // Reload categories from the database
    public void refreshCategoryPanel(JPanel mainPanel) {
        mainPanel.removeAll();
        loadCategories(mainPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
 // Delete category by name
    private void deleteCategory(String categoryName) {
        String deleteSQL = "DELETE FROM categories WHERE Category_Name = ?";
        try (Connection conn = goalsDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setString(1, categoryName);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(CategoryPage.this, "Category deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(CategoryPage.this, "Category not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(CategoryPage.this, "Error deleting category.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Getter for main panel
	public JPanel getMainPanel() {
		return mainPanel;
	}

}