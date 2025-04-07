package com.tekkimariani.cleanup.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tinylog.Logger;

import com.tekkimariani.cleanup.net.Host;

public class Gui {
	
    // Farben für den Dark Mode
    Color bgColor = new Color(45, 45, 45); // Dunkelgrau
    Color fgColor = new Color(200, 200, 200); // Helles Grau
    Color panelColor = new Color(60, 63, 65); // Panels
    Color btnColor = new Color(70, 73, 75);  // Buttons
    Color statusColor = new Color(30, 30, 30); // Statusleiste	
        
    private JFrame frame;
    private JPanel mainPanel; // will use CardLayout
    private CardLayout cardLayout;
	
	private String labelLocalSubnetPre = "Subnet: ";
	private JLabel labelLocalSubnet;

	private String labelLocalIpPre = "IP: ";
	private JLabel labelLocalIp;
	
	private String labelMarkedPcPre = "Marked: ";
	private JLabel labelMarkedPc;
	
	DefaultListModel<String> listModel;
	
	private static App app;
	
	private static JList<String> computerList;
	
	public Gui(App app) {
		Gui.app = app;
		createAndShowGUI();
	}
	
	public void setLocalSubnet(String localSubnet) {
		this.labelLocalSubnet.setText(labelLocalSubnetPre+localSubnet);
	}
	
	public void setLocalIp(String localIp) {
		this.labelLocalIp.setText(labelLocalIpPre+localIp);
	}
	
	public void setHosts(Map<String, Host> hosts) {
		// TODO Auto-generated method stub
		listModel.clear();
		for (Map.Entry<String, Host> entry : hosts.entrySet()) {
			listModel.addElement(entry.getKey());
		}
	}
	
    private void createAndShowGUI() {

        // Fenster erstellen
        frame = new JFrame("Netzwerksteuerung (Dark Mode)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(bgColor);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createInterfaceSelectorPanel(), "interfaceSelector");
        mainPanel.add(createScannerPanel(), "scannerUI");



        // Fenster sichtbar machen
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    
    
    private JPanel createInterfaceSelectorPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        Map<String, NetworkInterface> selectionMap = new HashMap<>();
        String[] options = app.getInterfaces().entrySet().stream()
            .map(entry -> {
                String label = entry.getKey() + " - " + entry.getValue();
                selectionMap.put(label, entry.getValue()); // später verwendbar
                return label;
            })
            .toArray(String[]::new);

        JComboBox<String> comboBox = new JComboBox<>(options);
        JButton startButton = new JButton("Adapter auswählen");

        startButton.addActionListener(e -> {
            String selected = (String) comboBox.getSelectedItem();
            if (selected != null) {
            	NetworkInterface ni = selectionMap.get(selected);
                // Store selected interface, if needed
                System.out.println("Selected: " + selected);

                // Switch to scanner UI
//                app.setNetworkInterface(ni);
                cardLayout.show(mainPanel, "scannerUI");
            }
        });

        panel.add(new JLabel("Wähle den Netzwerkadapter:"), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createScannerPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BorderLayout());
    	
        // Haupt-Panel für den dreigeteilten Bereich (Abstände entfernt)
        JPanel mainPanel = new JPanel(); // 0 Abstand#
        BoxLayout mainPanelLayout = new BoxLayout(mainPanel, 0);
        mainPanel.setLayout(mainPanelLayout);
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder()); // Keine Ränder

//        // *** Linke Seite: Scrollbare Liste für Netzwerk-Computer ***
//        JList<String> computerList = createStyledList(panelColor, fgColor);
//        JScrollPane scrollPane = new JScrollPane(computerList);
//        styleScrollPane(scrollPane, panelColor);
//        mainPanel.add(scrollPane);
     // *** Linke Seite: Scrollbare Liste für Netzwerk-Computer **
        
        computerList = createStyledList(panelColor, fgColor);

        JScrollPane scrollPane = new JScrollPane(computerList);
        styleScrollPane(scrollPane, panelColor);
        // Begrenzung der Breite
        int fixedWidth = 100; // Hier die gewünschte Breite einstellen
        scrollPane.setPreferredSize(new Dimension(fixedWidth, Integer.MAX_VALUE));
        mainPanel.add(scrollPane);

        // *** Mittlerer Bereich: Buttons ***
        JPanel buttonPanel = createButtonPanel(btnColor, fgColor);
        mainPanel.add(buttonPanel);

        // *** Rechte Seite: Textfeld für Informationen ***
        JTextArea infoArea = createStyledTextArea(panelColor, fgColor);
        infoArea.setLineWrap(true);      // Aktiviert den Zeilenumbruch
        infoArea.setWrapStyleWord(true); // Bricht den Text nur bei ganzen Wörtern um


        JScrollPane infoScrollPane = new JScrollPane(infoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollPane(infoScrollPane, panelColor);
        mainPanel.add(infoScrollPane);
        infoArea.setText("wtf wtf wtf wtf wtf wtf wtf wtf \nwtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf wtf ");

        panel.add(mainPanel, BorderLayout.CENTER);

        // *** Statusleiste unten ***
        JLabel statusLabel = createStyledLabel("Status: Bereit", fgColor);
        JPanel statusPanel = createStatusPanel(statusColor, statusLabel);
        panel.add(statusPanel, BorderLayout.SOUTH);

//        // Testdaten zur Liste hinzufügen
        listModel = (DefaultListModel<String>) computerList.getModel();
//        listModel.addElement("Computer-1");
//        listModel.addElement("Computer-2");
//        listModel.addElement("Computer-3");    
        
        return panel;
    }
    
    
    
    
    
    
    
    
    
    

//    // Methode zum Erstellen einer Menüleiste
//    private JMenuBar createMenuBar(Color bgColor, Color fgColor) {
//        JMenuBar menuBar = new JMenuBar();
//        menuBar.setBackground(bgColor);
//        menuBar.setForeground(fgColor);
//        menuBar.setBorder(BorderFactory.createEmptyBorder()); // Keine Ränder
//
//        JMenu menu = new JMenu("Optionen");
//        menu.setForeground(fgColor);
//        JMenuItem exitItem = new JMenuItem("Beenden");
//        exitItem.addActionListener(e -> System.exit(0));
//        menu.add(exitItem);
//        menuBar.add(menu);
//        
//        return menuBar;
//    }

    // Methode zum Erstellen einer Liste
    private JList<String> createStyledList(Color bgColor, Color fgColor) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setBackground(bgColor);
        list.setForeground(fgColor);
        list.setBorder(BorderFactory.createEmptyBorder()); // Keine Ränder
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {  // Verhindert mehrfaches Auslösen während der Auswahländerung
                int[] selectedIndices = list.getSelectedIndices();
//                System.out.println("Ausgewählte Indizes: " + Arrays.toString(selectedIndices));
                
                if (selectedIndices.length > 0) {
                    System.out.println("Ausgewählte Items: ");
                    for (int index : selectedIndices) {
//                        System.out.println(list.getModel().getElementAt(index));
                        app.setMarkedHost(list.getSelectedValue());
                    }
                }
            }
        });
        return list; 
    }

    // Methode zum Erstellen eines Button-Panels mit mehreren Buttons
    private JPanel createButtonPanel(Color btnColor, Color fgColor) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Minimaler Abstand
        buttonPanel.setBackground(new Color(45, 45, 45)); // Hintergrund für Buttonbereich
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Einheitlicher Abstand

        labelLocalIp = createStyledLabel("", Color.WHITE);
        this.setLocalIp("");
        labelLocalSubnet = createStyledLabel("", Color.WHITE);
        this.setLocalSubnet("");
        
        JButton btn1 = createStyledButton("Scan", btnColor, fgColor);
        btn1.addActionListener(e -> {
        	Logger.debug("Scanne das Netzwerk nach Computern.");
        	app.actionScan();
        });
        
        JButton btn2 = createStyledButton("WOL", btnColor, fgColor);
        btn2.addActionListener(e -> {
        	Logger.debug("Send Wake-on-LAN Packet.");
        	app.actionWol();
        });
        
        JButton btn3 = createStyledButton("Restart", btnColor, fgColor);
        btn3.addActionListener(e -> {
        	Logger.debug("Scanne das Netzwerk nach Computern.");
//        	app.restart();
        });
        
        JButton btn4 = createStyledButton(App.ACTION_SHUTDOWN, btnColor, fgColor);
        btn4.addActionListener(e -> {
//            Logger.debug("Sh");
            app.actionShutdown();
        });

        buttonPanel.add(labelLocalIp);
        buttonPanel.add(labelLocalSubnet);
        buttonPanel.add(btn1);
        buttonPanel.add(btn2);
        buttonPanel.add(btn3);
        buttonPanel.add(btn4);
        
        return buttonPanel;
    }

    // Methode zum Erstellen eines einheitlichen Buttons
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90))); // Weiche Trennlinie
        return button;
    }

    // Methode zum Erstellen eines Textbereichs
    private JTextArea createStyledTextArea(Color bgColor, Color fgColor) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(bgColor);
        textArea.setForeground(fgColor);
        textArea.setBorder(BorderFactory.createEmptyBorder()); // Keine Ränder
        return textArea;
    }

    // Methode zum Stylen von ScrollPanes (keine weißen Ränder)
    private void styleScrollPane(JScrollPane scrollPane, Color bgColor) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Keine Rahmen
        scrollPane.getViewport().setBackground(bgColor);
    }

    // Methode zum Erstellen eines Labels
    private JLabel createStyledLabel(String text, Color fgColor) {
        JLabel label = new JLabel(text);
        label.setForeground(fgColor);
        return label;
    }
    
    // Methode zum Erstellen eines Labels
    private JLabel createStyledLabel(String text, Color fgColor, Color bgColor) {
        JLabel label = new JLabel(text);
        label.setForeground(fgColor);
        label.setBackground(bgColor);
        return label;
    }

    // Methode zum Erstellen einer Statusleiste
    private JPanel createStatusPanel(Color bgColor, JLabel statusLabel) {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(bgColor);
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 70))); // Dezente Linie oben
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        return statusPanel;
    }


}