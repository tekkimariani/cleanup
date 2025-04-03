package com.tekkimariani;

import org.tinylog.Logger;

import com.tekkimariani.net.Util;

public class Main {


    public static void main(String[] args) {
        try {
//            String mac = "00:11:22:33:44:55"; // MAC-Adresse des Ziel-PCs
            String broadcast = "192.168.203.255"; // Broadcast-Adresse des Netzwerks
            broadcast = "192.168.255.255";
//            Util.sendWakeOnLan(mac, broadcast);
        	String ip = "192.168.203.10";
        	boolean ping = Util.ping(ip);
        	Logger.debug("Erreichbarkeit: " + ping);        	
        	
//        	
//        	String mac = Util.searchMacAdress(ip);
//        	

//        	Logger.debug("Mac Adresse: " + mac);
        	String mac = "90-1b-0e-2c-e0-f3";
        	Util.sendWakeOnLan(mac, broadcast);
        	
//        	Thread.sleep(30000);
        	
        	Util.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}