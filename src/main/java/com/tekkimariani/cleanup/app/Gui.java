package com.tekkimariani.cleanup.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.tekkimariani.cleanup.net.Util;

public class Gui {

    private static final String GUI_INFO = "info";
    private static final String GUI_SELECTION = "selection";
    private static final String GUI_SCANNER = "scanner";
	
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
	
	private String labelLocalBroadcastPre = "Broadcast: ";
	private JLabel labelLocalBroadcast;
	
    private JLabel info;
	
	DefaultListModel<String> listModel;
	
	private static App app;
	
	private static JList<String> computerList;
	
	private List<String> subnetSelection = List.of("255.255.255.255");
	JComboBox<String> maskComboBox;
	
	public Gui(App app) {
		Gui.app = app;
		createAndShowGUI();
	}
	

	
	public void setLocal(String ip, String subnetMask, String broadcast) {
		this.labelLocalIp.setText(labelLocalIpPre+ip);
		this.labelLocalSubnet.setText(labelLocalSubnetPre+subnetMask);
		this.labelLocalBroadcast.setText(labelLocalBroadcastPre+broadcast);
		this.subnetSelection = Util.getRelevantSubnetMasks(subnetMask);
		this.maskComboBox.removeAllItems();
		
		for (String mask : this.subnetSelection) {
			this.maskComboBox.addItem(mask);
		}
	}
	
	public void setHosts(Map<String, Host> hosts) {
		// TODO Auto-generated method stub
		listModel.clear();
		for (Map.Entry<String, Host> entry : hosts.entrySet()) {
			listModel.addElement(entry.getKey());
		}
	}
	
	public void setInfoPanel() {
	
	}
	
    private void createAndShowGUI() {

        // Fenster erstellen
        frame = new JFrame("Netzwerksteuerung");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(bgColor);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createInterfaceSelectorPanel(), GUI_SELECTION);
        mainPanel.add(createScannerPanel(), GUI_SCANNER);
        mainPanel.add(createInfoJPanel(), GUI_INFO);



        // Fenster sichtbar machen
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private void show(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void showScanner() {
        this.show(GUI_SCANNER);
    }

    public void showSelection() {
        this.show(GUI_SELECTION);
    }

    public void showInfo(String infoText) {
        info.setText(infoText);
        this.show(GUI_INFO);
    }


    private JPanel createInfoJPanel() {
        // Create the informational JPanel
        JPanel panel = new JPanel();
        info = new JLabel("This is an informational message for the user.");

        // Create a JButton
        JButton closeButton = new JButton("Close");

        // Add an ActionListener to the button
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the program
            }
        });


        panel.add(info);
        panel.add(closeButton);
        return panel;
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
                app.setNetworkInterface(ni);
                this.show("scannerUI");
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
        infoArea.setText("");
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
        	List<String> selection = new ArrayList<>();
            if (!e.getValueIsAdjusting()) {  // Verhindert mehrfaches Auslösen während der Auswahländerung
                int[] selectedIndices = list.getSelectedIndices();
//                System.out.println("Ausgewählte Indizes: " + Arrays.toString(selectedIndices));
                
                if (selectedIndices.length > 0) {
//                    System.out.println("Ausgewählte Items: ");
                    for (int index : selectedIndices) {
//                        System.out.println(list.getModel().getElementAt(index));
//                        app.setSelectedHost(list.getSelectedValue());
                        selection.add(list.getModel().getElementAt(index));
                        
                    }
                }
                app.setSelectedHosts(selection);
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
        
        labelLocalSubnet = createStyledLabel("", Color.WHITE);
        
        maskComboBox = new JComboBox<>(subnetSelection.toArray(new String[0]));
        maskComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (maskComboBox.getSelectedItem() == null || maskComboBox.getSelectedItem().equals("")) return;
                String selectedMask = (String) maskComboBox.getSelectedItem();
//                System.out.println("Selected: " + selectedMask);
                // Do something with the selection...
                app.setSelectedSubmask(selectedMask);
            }
        });
        
        labelLocalBroadcast = createStyledLabel("", Color.WHITE);
        this.setLocal("", "", "");
        
     // TODO: Move the buttons to the selected section
        
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
        	Logger.debug("restart :o");
        	app.actionRestart();
        	// TODO: Implement the restart action
        });
        
        JButton btn4 = createStyledButton(App.ACTION_SHUTDOWN, btnColor, fgColor);
        btn4.addActionListener(e -> {
            app.actionShutdown();
        });
        
        // TODO: Do a delete button to delete host from the list.

        
        buttonPanel.add(labelLocalIp);
        buttonPanel.add(labelLocalSubnet);
        buttonPanel.add(maskComboBox);
        buttonPanel.add(labelLocalBroadcast);
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