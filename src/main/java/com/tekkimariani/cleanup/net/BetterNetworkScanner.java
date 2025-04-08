
package com.tekkimariani.cleanup.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import org.tinylog.Logger;

public class BetterNetworkScanner {
	    
    public BetterNetworkScanner() {
    	//
    }

    public List<Host> scan(String ipAddress, String subnetMask, int timeout, int maxThreads) {
    	
    	try {

	        // Calculate IP range
	        byte[] ipBytes = InetAddress.getByName(ipAddress).getAddress();
	        byte[] maskBytes = InetAddress.getByName(subnetMask).getAddress();

	        // Get network address (first IP)
	        byte[] networkBytes = new byte[4];
	        for (int i = 0; i < 4; i++) {
	            networkBytes[i] = (byte) (ipBytes[i] & maskBytes[i]);
	        }
	        String firstIP = InetAddress.getByAddress(networkBytes).getHostAddress();

	        // Get broadcast address (last IP)
	        byte[] broadcastBytes = new byte[4];
	        for (int i = 0; i < 4; i++) {
	            broadcastBytes[i] = (byte) (ipBytes[i] | ~maskBytes[i]);
	        }
	        String lastIP = InetAddress.getByAddress(broadcastBytes).getHostAddress();

	        // Parse first & last IP into integers for iteration
	        long firstIPLong = ipToLong(firstIP);
	        long lastIPLong = ipToLong(lastIP);

	        // Skip network & broadcast addresses (usable range: firstIP+1 to lastIP-1)
	        long startHost = firstIPLong + 1;
	        long endHost = lastIPLong - 1;

	        // Thread-safe results list
	        List<Host> results = Collections.synchronizedList(new ArrayList<>());
	        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
	        CountDownLatch latch = new CountDownLatch((int) (endHost - startHost + 1));

	        // Scan all IPs in the range
	        for (long i = startHost; i <= endHost; i++) {
	            final String ip = longToIp(i);
	            executor.submit(() -> {
	                try {
	                    InetAddress inet = InetAddress.getByName(ip);
	                    boolean reachable = inet.isReachable(timeout);
	                    if (reachable) {
	                        String mac = Util.getMacAddress(ip);
	                        results.add(new Host(ip, mac, true));
	                        Logger.debug("Ip: " + ip + " | Mac: " + mac);
	                    }
	                } catch (Exception ignored) {
	                } finally {
	                    latch.countDown();
	                }
	            });
	        }

	        latch.await(); // Wait for all threads
	        executor.shutdown();
	        return results; 
            
            
        } catch (UnknownHostException e) {
            Logger.error("Invalid IP or subnet mask");
            e.printStackTrace();
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
    	
    	return new ArrayList<>();

    }
    
    // Helper: Convert IP (String) to long (for iteration)
    private static long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) +
               (Long.parseLong(octets[1]) << 16) +
               (Long.parseLong(octets[2]) << 8) +
               Long.parseLong(octets[3]);
    }

    // Helper: Convert long back to IP (String)
    private static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }

}
