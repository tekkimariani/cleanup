package com.tekkimariani.test;

import javax.swing.*;

import com.tekkimariani.cleanup.net.FastNetworkScanner;
import com.tekkimariani.cleanup.net.Host;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class NetworkScannerUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<String, String> interfaces = listIPv4Addresses();

            if (interfaces.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Keine aktiven Netzwerkadapter gefunden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] options = interfaces.entrySet().stream()
                    .map(entry -> entry.getKey() + " - " + entry.getValue())
                    .toArray(String[]::new);

            String selected = (String) JOptionPane.showInputDialog(
                    null,
                    "Wähle den Netzwerkadapter:",
                    "Netzwerkscanner",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (selected == null) return; // Abbruch durch Nutzer

            // Extrahiere IP aus Auswahl
            String ip = selected.substring(selected.lastIndexOf(" ") + 1);
            String subnet = ip.substring(0, ip.lastIndexOf("."));

            // Scanner starten
            FastNetworkScanner scanner = new FastNetworkScanner(subnet, 100, 50);
            try {
                long start = System.currentTimeMillis();
                List<Host> hosts = scanner.scan();
                long end = System.currentTimeMillis();

                StringBuilder result = new StringBuilder("Gefundene Geräte:\n\n");
                for (Host h : hosts) {
                    result.append(h.getName()).append(" | IP: ").append(h.getIp()).append(" | MAC: ").append(h.getMac()).append("\n");
                }
                result.append("\nScanzeit: ").append(end - start).append("ms");

                JTextArea textArea = new JTextArea(result.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));

                JOptionPane.showMessageDialog(null, scrollPane, "Scan-Ergebnisse", JOptionPane.INFORMATION_MESSAGE);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<String, String> listIPv4Addresses() {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        String name = ni.getDisplayName();
                        String ip = addr.getHostAddress();
                        result.put(name, ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return result;
    }
}