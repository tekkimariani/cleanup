package com.tekkimariani.cleanup.app;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JList;

import org.tinylog.Logger;

import com.tekkimariani.cleanup.net.FastNetworkScanner;
import com.tekkimariani.cleanup.net.Host;
import com.tekkimariani.cleanup.net.Util;

public class App {
	
	private static String FILENAME = "hosts.json";
	
	private static Gui ui;
	
	public static final String ACTION_SHUTDOWN = "shutdown";
	
	private static JList<String> computerList;
	
	private String localIp;
	private String localSubnetMask;
	private String localBroadcast;
	
	Map<String, Host> savedHosts = new HashMap<String, Host>();
	List<Host> foundHosts;
	private Host selectedHost;

	Map<String, NetworkInterface> networkInterfaces;
	NetworkInterface networkInterface;

	public App() {

		Logger.info("Cleanup v0.1");

		

		networkInterfaces = Util.listNetworkInterfaces();

		ui = new Gui(this);

		if (networkInterfaces.size() == 0) {
			// No activ network adapter
			// Just inform about this behavior.
			ui.showInfo("There are no activ network adapters to work with.");
		} else if (networkInterfaces.size() == 1) {
			// There is only one activ network adapter.
			// We can just choose this one an go on.
			this.setNetworkInterface(networkInterfaces.values().iterator().next());
		} else {
			// There are more than one activ network adapters.
			// Give the user the options to choose from.
			ui.showSelection();
		}
		
		

		


	}
	
	public void setNetworkInterface(NetworkInterface ni) {
		this.networkInterface = ni;

		

		try {
			Logger.debug(ni.getDisplayName());
			Logger.debug(ni.getName());			
			
			Logger.debug(ni.getHardwareAddress());
			
			Logger.debug("getInetAdresses:");
			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                Logger.debug("InetAddress: " + inetAddress.getHostAddress());
            }
//			Logger.debug(ni.getInetAddresses());
//			Logger.debug(ni.getDisplayName());	
            
            // Iterate over all InterfaceAddresses associated with the NetworkInterface
            List<InterfaceAddress> interfaceAddresses = ni.getInterfaceAddresses();

            for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                InetAddress inetAddress = interfaceAddress.getAddress();

                // Check if the InetAddress is an IPv4 address
                if (inetAddress instanceof java.net.Inet4Address) {
                    System.out.println("IP Address: " + inetAddress.getHostAddress());
                    localIp = inetAddress.getHostAddress();

                    // Get the subnet mask (network prefix length)
                    short networkPrefixLength = interfaceAddress.getNetworkPrefixLength();
                    System.out.println("Subnet Mask: " + getSubnetMask(networkPrefixLength));
                    localSubnetMask = getSubnetMask(networkPrefixLength);
                    
                    // Get the broadcast address
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast != null) {
                        System.out.println("Broadcast Address: " + broadcast.getHostAddress());
                        localBroadcast = broadcast.getHostAddress();
                    } else {
                    	localBroadcast = "null";
                    }
                }
            }
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		savedHosts = Json.load(FILENAME);
		
		ui.setLocal(localIp, localSubnetMask, localBroadcast);
		ui.setHosts(savedHosts);

		ui.showScanner();
	}
	
	public void setMarkedHost(String ip) {
		for (Map.Entry<String,Host> host : savedHosts.entrySet()) {
			Logger.debug(host);
		}
		
		this.selectedHost = savedHosts.get(ip);
		this.selectedHost.setReachable(Util.ping(selectedHost.getIp()));
		Logger.debug("Marked IP: "+this.selectedHost);
	}
	
	public void actionWol() {
		if (selectedHost == null) {
			Logger.debug("fail");
			return;
		}
		try {
			Logger.debug("WOL to " + this.selectedHost);
			Util.sendWakeOnLan(this.selectedHost.getMac(), "192.168.255.255");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void actionShutdown() {
		if (selectedHost == null) {
			Logger.debug("fail");
			return;
		}
		try {
			Logger.debug("Shutdown " + this.selectedHost);
			Util.shutdown(this.selectedHost.getIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void actionScan() {
    	
    	Logger.debug("Scan...");
		try {
	        FastNetworkScanner scanner = new FastNetworkScanner(localSubnetMask, 100, 50);  // Timeout 100ms, 50 Threads
	        		
			
			foundHosts = scanner.scan();

			for (Host host:foundHosts) {
				savedHosts.put(host.getIp(), host);
				Logger.debug(host);
			}
			ui.setHosts(savedHosts);
			Json.save(FILENAME, savedHosts);

	        Logger.debug("Fertig.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public Map<String,NetworkInterface> getInterfaces() {
		// TODO Auto-generated method stub
		return networkInterfaces;
	}
	
    // Helper method to convert the network prefix length to a subnet mask
    private static String getSubnetMask(short prefixLength) {
        int mask = 0xffffffff << (32 - prefixLength);
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xFF,
                (mask >> 16) & 0xFF,
                (mask >> 8) & 0xFF,
                mask & 0xFF);
    }
	
}
