package com.tekkimariani.cleanup.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tinylog.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Util {
	
    private static final int PORT = 4000; // WoL-Port
    private static final int timeout = 3000; // Timeout in Millisekunden
    
//    public static void login() {
//        String username = "User";
//        String password = "password";
//        String targetPC = "172.25.164.93"; // Die IP des aufgewachten PCs
//        String command = "cmd.exe"; // Oder "powershell.exe"
//
//        String[] cmd = {
//            "psexec", "\\\\" + targetPC, "-u", username, "-p", password, command
//        };
//        
//        Logger.debug(cmd[0]);
//
//        try {
//            ProcessBuilder pb = new ProcessBuilder(cmd);
//            pb.inheritIO();
//            Process process = pb.start();
//            process.waitFor();
//            
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    
//    public static void shutdown(String ip) {
//        String username = "Administrator";
//        String password = "Windows11"; // Change to the actual password
//        String targetPC = ip;
//
//        // Shutdown command for remote execution
//        String[] cmd = {
//            "psexec", 
//            "\\\\" + targetPC, 
//            "-u", username, 
//            "-p", password, 
//            "shutdown", "-s", "-t", "10", "-f"
//        };
//
//        try {
//            ProcessBuilder pb = new ProcessBuilder(cmd);
//            pb.inheritIO();
//            Process process = pb.start();
//            process.waitFor();
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    
    public static void shutdown(String ip) {
      String username = "Administrator";
      String password = "Windows11"; // Change to the actual password
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, ip, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Shutdown-Befehl ausführen
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setInputStream(null);
            channel.setErrStream(System.err);           
            channel.setCommand("shutdown -s -t 10 -f");

            channel.connect();
            Thread.sleep(2000); // Warte auf Befehlsausführung
            channel.disconnect();
            session.disconnect();

            System.out.println("Shutdown-Befehl gesendet an " + ip);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
    public static void sendWakeOnLan(String macAddress, String broadcastAddress) throws Exception {
        byte[] macBytes = getMacBytes(macAddress.trim());
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

        Logger.debug("Wake-on-LAN Packet gesendet an " + macAddress);
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
                        return parts[1].trim();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return "ERROR";
	}
	
	
	@SuppressWarnings("deprecation")
	public static String getMacAddress(String ip) {
	    try {
	        // Erst anpingen, damit IP in die ARP-Tabelle kommt
	        InetAddress.getByName(ip).isReachable(100);
	        
	        Process p = Runtime.getRuntime().exec("arp -a " + ip);
	        java.util.Scanner s = new java.util.Scanner(p.getInputStream()).useDelimiter("\\A");
	        String output = s.hasNext() ? s.next() : "";
	        s.close();
	        // MAC-Adresse auslesen
	        for (String line : output.split("\n")) {
	            if (line.contains(ip)) {
	                return line.replaceAll(".*(([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}).*", "$1").trim();
	            }
	        }
	        

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
//	@SuppressWarnings("deprecation")
//	public static boolean isWindowsHost(String ip) {
//	    try {
//	        Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
//	        java.util.Scanner s = new java.util.Scanner(p.getInputStream()).useDelimiter("\\A");
//	        String output = s.hasNext() ? s.next() : "";
//	        s.close();
//	        return output.contains("Windows") || output.contains("MAC Address");
//	        
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	    return false;
//	}
	
    public static String getLocalSubnet() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress(); // z. B. 192.168.203.47
                        String subnet = ip.substring(0, ip.lastIndexOf('.')); // ergibt 192.168.203
                        return subnet;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress(); // z. B. 192.168.203.47
//                        String subnet = ip.substring(0, ip.lastIndexOf('.')); // ergibt 192.168.203
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    

    public static Map<String, NetworkInterface> listNetworkInterfaces() {
        Map<String, NetworkInterface> result = new LinkedHashMap<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
//                        String name = ni.getDisplayName();
                        String ip = addr.getHostAddress();
                        result.put(ip, ni);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
}
