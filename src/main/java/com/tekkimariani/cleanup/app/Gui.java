package com.tekkimariani.cleanup.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.border.EmptyBorder;

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
    
    private JTextArea infoArea;
	
	DefaultListModel<String> listModel;
	
	private static App app;
	
	private static JList<String> computerList;
	
	JLabel statusLabel;
	
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
	
	public void setInfoPanel(String text) {
		infoArea.setText(text+"\n");
	}
	
	public void addInfoPanel(String text) {
		infoArea.setText(infoArea.getText()+text+"\n");
		
	}
	
	public void clearInfoPanel() {
		infoArea.setText("");
	}
	
    private void createAndShowGUI() {

        // Fenster erstellen
        frame = new JFrame("Cerberus - Hack to the Future");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(bgColor);
        frame.setLocationRelativeTo(null);

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
    
    public void setStatus(String status) {
    	statusLabel.setText(status);
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
        BoxLayout mainPanelLayout = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(mainPanelLayout);
        
//        mainPanel.setLayout(new BorderLayout());
        
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder()); // Keine Ränder
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        computerList = createStyledList(panelColor, fgColor);

        JScrollPane scrollPane = new JScrollPane(computerList);
        styleScrollPane(scrollPane, panelColor);
        // Begrenzung der Breite
        int fixedWidth = 200; // Hier die gewünschte Breite einstellen
        scrollPane.setPreferredSize(new Dimension(fixedWidth, Integer.MAX_VALUE));
        scrollPane.setMinimumSize(new Dimension(fixedWidth, Integer.MAX_VALUE));
        scrollPane.setMaximumSize(new Dimension(fixedWidth, Integer.MAX_VALUE));
//        mainPanel.add(scrollPane);
        mainPanel.add(scrollPane);

        // *** Mittlerer Bereich: Buttons ***
        JPanel buttonPanel = createButtonPanel(btnColor, fgColor);
        mainPanel.add(buttonPanel);


        // *** Rechte Seite: Textfeld für Informationen ***
        infoArea = createStyledTextArea(panelColor, fgColor);
        infoArea.setLineWrap(true);      // Aktiviert den Zeilenumbruch
        infoArea.setWrapStyleWord(true); // Bricht den Text nur bei ganzen Wörtern um
        infoArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane infoScrollPane = new JScrollPane(infoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        infoScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        infoScrollPane.setMinimumSize(new Dimension(0, Integer.MAX_VALUE));
        infoScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));    
        
        
        
        styleScrollPane(infoScrollPane, panelColor);
        
        
        
        mainPanel.add(infoScrollPane);
        infoArea.setText("Something");
        panel.add(mainPanel, BorderLayout.CENTER);

        // *** Statusleiste unten ***
        statusLabel = createStyledLabel("Status: Bereit", fgColor);
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

        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; // Linksbündig
        gbc.insets = new Insets(0, 0, 0, 0); // Kleiner vertikaler Abstand
        gbc.anchor = GridBagConstraints.NORTHWEST; // Top-left alignment
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;  // Stretch horizontally

        
        buttonPanel.setBackground(new Color(45, 45, 45)); // Hintergrund für Buttonbereich
//        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Einheitlicher Abstand
        buttonPanel.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
        buttonPanel.setMinimumSize(new Dimension(200, Integer.MAX_VALUE));
        buttonPanel.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN)); // Debug border  
        
        labelLocalIp = createStyledLabel("", Color.WHITE);
        
        labelLocalSubnet = createStyledLabel("", Color.WHITE);
        
        

        maskComboBox = new JComboBox<>(subnetSelection.toArray(new String[0]));
        
        maskComboBox.setPreferredSize(new Dimension(200, 30));
        maskComboBox.setMaximumSize(new Dimension(200, 30)); // Verhindert Wachstum
        maskComboBox.setMinimumSize(new Dimension(200, 30)); // Verhindert Schrumpfen
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
        JPanel comboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboBoxWrapper.setBackground(new Color(45, 45, 45)); // Hintergrund anpassen
        comboBoxWrapper.add(maskComboBox);



        
        labelLocalBroadcast = createStyledLabel("", Color.WHITE);
        this.setLocal("", "", "");
        
     // TODO: Move the buttons to the selected section
        
        JButton btn1 = createStyledButton(App.ACTION_SCAN, btnColor, fgColor);
        btn1.addActionListener(e -> {
        	app.actionScan();
        });
        
        JButton btn2 = createStyledButton(App.ACTION_WAKE, btnColor, fgColor);
        btn2.addActionListener(e -> {
        	app.actionWol();
        });
        
        JButton btn3 = createStyledButton(App.ACTION_RESTART, btnColor, fgColor);
        btn3.addActionListener(e -> {
        	app.actionRestart();
        });
        
        JButton btn4 = createStyledButton(App.ACTION_SHUTDOWN, btnColor, fgColor);
        btn4.addActionListener(e -> {
            app.actionShutdown();
        });
        
        JButton btn5 = createStyledButton(App.ACTION_DELETE, btnColor, fgColor);
        btn5.addActionListener(e -> {
            app.actionDelete();
        });
 
//        
//        JPanel btn1Wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        btn1Wrapper.setBackground(new Color(45, 45, 45)); // Hintergrund anpassen
//        infoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
//        btn1Wrapper.add(btn1);
//        

        gbc.insets = new Insets(2, 10, 2, 10);
        buttonPanel.add(labelLocalIp, gbc); gbc.gridy++;
        buttonPanel.add(labelLocalSubnet, gbc); gbc.gridy++;
        
        gbc.insets = new Insets(2, 2, 2, 2);
        buttonPanel.add(maskComboBox, gbc); gbc.gridy++;
//        buttonPanel.add(comboBoxWrapper); gbc.gridy++;
        
        gbc.insets = new Insets(2, 10, 2, 10);
        buttonPanel.add(labelLocalBroadcast, gbc); gbc.gridy++;
        
        

        gbc.insets = new Insets(2, 10, 2, 10);
//        buttonPanel.add(btn1, gbc); gbc.gridy++;
        buttonPanel.add(btn1, gbc); gbc.gridy++;

        buttonPanel.add(btn2, gbc); gbc.gridy++;
        buttonPanel.add(btn3, gbc); gbc.gridy++;
        buttonPanel.add(btn4, gbc); gbc.gridy++;
        buttonPanel.add(btn5, gbc); gbc.gridy++;
        
   
        
        
        // Row 2: Empty space filler (pushes content up)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.weighty = 1.0; // Takes all vertical space
        buttonPanel.add(Box.createGlue(), gbc);        
        return buttonPanel;
    }
    
    private JPanel wrap(Component c) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setBackground(new Color(45, 45, 45)); // Hintergrund anpassen
        wrap.add(c);
        return wrap;
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
        statusPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 70))); // Dezente Linie oben
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        return statusPanel;
    }


}