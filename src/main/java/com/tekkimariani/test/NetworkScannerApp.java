package com.tekkimariani.test;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkScannerApp {

    private JFrame frame;
    private JPanel mainPanel; // will use CardLayout
    private CardLayout cardLayout;

    // Simulated map of interfaces
    private final Map<String, String> interfaces = new LinkedHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkScannerApp().createAndShowGUI());
    }

    public void createAndShowGUI() {
        interfaces.put("eth0", "192.168.203.5");
        interfaces.put("wlan0", "192.168.0.15");

        frame = new JFrame("Netzwerkscanner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createInterfaceSelectorPanel(), "interfaceSelector");
        mainPanel.add(createScannerUIPanel(), "scannerUI");

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createInterfaceSelectorPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] options = interfaces.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .toArray(String[]::new);

        JComboBox<String> comboBox = new JComboBox<>(options);
        JButton startButton = new JButton("Start Scan");

        startButton.addActionListener(e -> {
            String selected = (String) comboBox.getSelectedItem();
            if (selected != null) {
                // Store selected interface, if needed
                System.out.println("Selected: " + selected);

                // Switch to scanner UI
                cardLayout.show(mainPanel, "scannerUI");
            }
        });

        panel.add(new JLabel("Wähle den Netzwerkadapter:"), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createScannerUIPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Scanner UI kommt hier hin"));
        // Hier kannst du dein Scanner-UI hinzufügen
        return panel;
    }
}