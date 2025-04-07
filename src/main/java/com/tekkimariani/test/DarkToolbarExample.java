package com.tekkimariani.test;

import javax.swing.*;
import java.awt.*;

public class DarkToolbarExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DarkToolbarExample::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Dark Toolbar Beispiel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // Farben definieren
        Color bgColor = new Color(30, 30, 30);  // Dunkelgrauer Hintergrund
        Color fgColor = new Color(200, 200, 200); // Helle Schriftfarbe
        Color hoverColor = new Color(50, 50, 50); // Hover-Farbe für Buttons

        // Toolbar erstellen
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(bgColor);

        // Buttons für Toolbar
        JButton btn1 = createToolbarButton("Option 1", bgColor, fgColor, hoverColor);
        JButton btn2 = createToolbarButton("Option 2", bgColor, fgColor, hoverColor);
        JButton btn3 = createToolbarButton("Beenden", bgColor, fgColor, hoverColor);

        // Aktion für Beenden-Button
        btn3.addActionListener(e -> System.exit(0));

        // Buttons zur Toolbar hinzufügen
        toolBar.add(btn1);
        toolBar.add(btn2);
        toolBar.add(btn3);

        // Layout setzen
        frame.setLayout(new BorderLayout());
        frame.add(toolBar, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    // Methode zum Erstellen von Toolbar-Buttons mit Hover-Effekt
    private static JButton createToolbarButton(String text, Color bgColor, Color fgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);

        // Hover-Effekt
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}
