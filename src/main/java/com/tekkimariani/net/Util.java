package com.tekkimariani.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.tinylog.Logger;

public class Util {
	
    private static final int PORT = 4000; // WoL-Port
    private static final int timeout = 3000; // Timeout in Millisekunden
    
    public static void login() {
        String username = "User";
        String password = "password";
        String targetPC = "172.25.164.93"; // Die IP des aufgewachten PCs
        String command = "cmd.exe"; // Oder "powershell.exe"

        String[] cmd = {
            "psexec", "\\\\" + targetPC, "-u", username, "-p", password, command
        };
        
        Logger.debug(cmd[0]);

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
	
    public static void sendWakeOnLan(String macAddress, String broadcastAddress) throws Exception {
        byte[] macBytes = getMacBytes(macAddress);
        byte[] magicPacket = new byte[6 + 16 * macBytes.length];

        // Magic Packet beginnt mit 6x 0xFF
        for (int i = 0; i < 6; i++) {
            magicPacket[i] = (byte) 0xFF;
        }

        // Danach 16-mal die MAC-Adresse
        for (int i = 6; i < magicPacket.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, magicPacket, i, macBytes.length);
        }

        InetAddress address = InetAddress.getByName(broadcastAddress);
        DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, PORT);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();

        System.out.println("Wake-on-LAN Packet gesendet an " + macAddress);
    }

    private static byte[] getMacBytes(String macAddress) throws IllegalArgumentException {
        String[] hex = macAddress.split("[:-]");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Ungültige MAC-Adresse");
        }

        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(hex[i], 16);
        }
        return bytes;
    }

    public static boolean ping(String ipAddress) {
    	return ping(ipAddress, timeout);
    }
	/**
	 * Ping the ip.
	 * @return true if reachable, false if not.
	 * 
	 */
    public static boolean ping(String ipAddress, int timeout) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    } 
	
	/*
	 * This method requires a ping beforehand.
	 */
	public static String searchMacAdress(String ipAddress) {
        // String ipAddress = "192.168.1.100"; // IP des Zielgeräts
        
        String[] commands = {"arp -a"};
        try {
//        	ping(ipAddress, timeout);
   //     	Process process = Runtime.getRuntime().exec(commands);
            Process process = Runtime.getRuntime().exec("arp -a " + ipAddress);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(ipAddress)) {
                    System.out.println("Gefundene Zeile: " + line);
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 2) {
                        System.out.println("MAC-Adresse: " + parts[1]);
                        return parts[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return "ERROR";
	}
}
