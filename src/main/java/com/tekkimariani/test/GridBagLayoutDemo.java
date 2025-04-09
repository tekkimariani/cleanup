package com.tekkimariani.test;

import javax.swing.*;
import java.awt.*;

public class GridBagLayoutDemo {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridBagLayout Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        // Main panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Common settings for all components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Top-left alignment
        gbc.insets = new Insets(5, 5, 5, 5); // Component margins
        
        // Row 0: Labels
        gbc.gridy = 0;
        gbc.weightx = 0; // Don't expand labels horizontally
        
        gbc.gridx = 0;
        panel.add(new JLabel("Label 1"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Label 2"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Label 3"), gbc);
        
        // Row 1: Buttons
        gbc.gridy++;
        gbc.weightx = 1.0; // Buttons expand to fill space
        
        gbc.gridy++;
        panel.add(new JButton("Button 1"), gbc);
        
        gbc.gridy++;
        panel.add(new JButton("Button 2"), gbc);
        
        gbc.gridy++;
        panel.add(new JButton("Button 3"), gbc);
        
        // Row 2: Empty space filler (pushes content up)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.weighty = 1.0; // Takes all vertical space
        panel.add(Box.createGlue(), gbc);
        
        frame.add(panel);
        frame.setVisible(true);
    }
}
