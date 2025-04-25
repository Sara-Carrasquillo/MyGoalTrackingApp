package edu.seminolestate.goaltrack;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;

public class MainMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private CardLayout cardLayout; 

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainMenu frame = new MainMenu();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainMenu() {
        setTitle("Goal Tracker");
        setForeground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        contentPane = new JPanel();
        contentPane.setBackground(Color.DARK_GRAY);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new CardLayout());  

        cardLayout = (CardLayout) contentPane.getLayout();  

        // Main Menu Panel
        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setForeground(Color.DARK_GRAY);
        mainMenuPanel.setBackground(Color.DARK_GRAY);
        contentPane.add(mainMenuPanel, "MainMenu");  
        mainMenuPanel.setLayout(null);

        JButton goal_Button = new JButton("Goals");
        goal_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to goals page
                cardLayout.show(contentPane, "GoalPage");
            }
        });
        goal_Button.setForeground(Color.GRAY);
        goal_Button.setBackground(Color.WHITE);
        goal_Button.setFont(new Font("Rockwell", Font.BOLD, 20));
        goal_Button.setBounds(345, 81, 304, 62);
        mainMenuPanel.add(goal_Button);

        // Categories Button
        JButton category_Button = new JButton("Categories");
        category_Button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// Switch to category page
        		cardLayout.show(contentPane, "CategoryPage");
        	}
        });
        category_Button.setForeground(Color.GRAY);
        category_Button.setBackground(Color.WHITE);
        category_Button.setFont(new Font("Rockwell", Font.BOLD, 20));
        category_Button.setBounds(345, 154, 304, 62);
        mainMenuPanel.add(category_Button);

        JButton steps_Button = new JButton("Steps");
        steps_Button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// Switch to steps page
        		cardLayout.show(contentPane, "StepsPage");
        	}
        });
        steps_Button.setForeground(Color.GRAY);
        steps_Button.setBackground(Color.WHITE);
        steps_Button.setFont(new Font("Rockwell", Font.BOLD, 20));
        steps_Button.setBounds(345, 227, 304, 62);
        mainMenuPanel.add(steps_Button);

        JButton progress_Button = new JButton("Progress");
        progress_Button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// Switch to progress page
        		cardLayout.show(contentPane, "ProgressPage");
        	}
        });
        progress_Button.setForeground(Color.GRAY);
        progress_Button.setBackground(Color.WHITE);
        progress_Button.setFont(new Font("Rockwell", Font.BOLD, 20));
        progress_Button.setBounds(345, 300, 304, 62);
        mainMenuPanel.add(progress_Button);

        JButton exit_Button = new JButton("Exit");
        exit_Button.setForeground(Color.GRAY);
        exit_Button.setBackground(Color.WHITE);
        exit_Button.setFont(new Font("Rockwell", Font.BOLD, 20));
        exit_Button.setBounds(345, 373, 304, 62);
        mainMenuPanel.add(exit_Button);

        exit_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show a confirmation before exiting
                int choice = JOptionPane.showConfirmDialog(
                    mainMenuPanel,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    
                    // Close the application safely
                    System.exit(0);
                }
            }
        });


        JLabel main_MenuLabel = new JLabel("Main Menu");
        main_MenuLabel.setForeground(Color.LIGHT_GRAY);
        main_MenuLabel.setBackground(Color.GRAY);
        main_MenuLabel.setFont(new Font("Rockwell", Font.BOLD, 30));
        main_MenuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        main_MenuLabel.setBounds(0, 0, 984, 73);
        mainMenuPanel.add(main_MenuLabel);

        // Goal Panel
        GoalPage goalPagePanel = new GoalPage(cardLayout, contentPane);
        contentPane.add(goalPagePanel, "GoalPage");
        
        AddGoals addGoalsPanel = new AddGoals(cardLayout, contentPane, goalPagePanel);
        contentPane.add(addGoalsPanel, "AddGoals");  

        UpdateGoals updateGoalsPanel = new UpdateGoals(cardLayout, contentPane, goalPagePanel);
        contentPane.add(updateGoalsPanel, "UpdateGoals");  
        
        // Category Panel
        CategoryPage categoryPagePanel = new CategoryPage(cardLayout, contentPane);
        contentPane.add(categoryPagePanel, "CategoryPage");
        
        AddCategory addCategoryPanel = new AddCategory(cardLayout, contentPane, categoryPagePanel);
        contentPane.add(addCategoryPanel, "AddCategory");  

        UpdateCategory updateCategoryPanel = new UpdateCategory(cardLayout, contentPane, categoryPagePanel);
        contentPane.add(updateCategoryPanel, "UpdateCategory");  
        
        // Steps Panel
        StepsPage stepsPagePanel = new StepsPage(cardLayout, contentPane);
        contentPane.add(stepsPagePanel, "StepsPage");
        
        AddSteps addStepsPanel = new AddSteps(cardLayout, contentPane);
        contentPane.add(addStepsPanel, "AddSteps");  

        UpdateSteps updateStepsPanel = new UpdateSteps(cardLayout, contentPane);
        contentPane.add(updateStepsPanel, "UpdateSteps");
        
     // Steps Panel
        ProgressPage progressPagePanel = new ProgressPage(cardLayout, contentPane);
        contentPane.add(progressPagePanel, "ProgressPage");
        
        AddProgress addProgressPanel = new AddProgress(cardLayout, contentPane, progressPagePanel);
        contentPane.add(addProgressPanel, "AddProgress");  

        UpdateProgress updateProgressPanel = new UpdateProgress(cardLayout, contentPane, progressPagePanel);
        contentPane.add(updateProgressPanel, "UpdateProgress");

    }
}

