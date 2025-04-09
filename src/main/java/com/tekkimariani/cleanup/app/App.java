package com.tekkimariani.cleanup.app;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JList;

import org.tinylog.Logger;

import com.tekkimariani.cleanup.net.FastNetworkScanner;
import com.tekkimariani.cleanup.net.BetterNetworkScanner;
import com.tekkimariani.cleanup.net.Host;
import com.tekkimariani.cleanup.net.Util;

public class App {
	
	private static final String USERNAME = "Administrator";
	private static final String PASSWORD = "Windows11";
	
	private static String FILENAME = "hosts.json";
	
	private static Gui ui;
	
	public static final String ACTION_SCAN = "scan";
	public static final String ACTION_SHUTDOWN = "shutdown";
	public static final String ACTION_RESTART = "restart";
	public static final String ACTION_WAKE = "wake over lan";
	public static final String ACTION_DELETE = "delete";
	
	private static JList<String> computerList;
	
	private String localIp;
	private String localSubnetMask;
	private String localBroadcast;
	private String selectedSubnetMask;
	
	Map<String, Host> savedHosts = new HashMap<String, Host>();
	List<Host> foundHosts;
	private Host selectedHost;
	List<Host> selectedHosts = new ArrayList<>();

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
		Logger.debug(ni.getDisplayName());
		this.networkInterface = ni;
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
		savedHosts = Json.load(FILENAME);
		ui.setLocal(localIp, localSubnetMask, localBroadcast);
		ui.setHosts(savedHosts);
		ui.showScanner();
	}
	
//	public void setSelectedHost(String ip) {
//		for (Map.Entry<String,Host> host : savedHosts.entrySet()) {
//			Logger.debug(host);
//		}	
//		this.selectedHost = savedHosts.get(ip);
//		this.selectedHost.setReachable(Util.ping(selectedHost.getIp()));
//		Logger.debug("Marked IP: "+this.selectedHost);
//	}
	
	public void setSelectedHosts(List<String> hosts) {
		this.selectedHosts.clear();
		ui.clearInfoPanel();
		for (String ip : hosts) {
			this.selectedHosts.add(savedHosts.get(ip));
		}
		if (this.selectedHosts.size() == 0) {
			// TODO: Show an info about the need to select a host.
		}
		
		if (this.selectedHosts.size() == 1) {
			// TODO: Show information about this host
			ui.clearInfoPanel();
			Host host = this.selectedHosts.get(0);

			ui.addInfoPanel("IP: " + host.getIp());
			ui.addInfoPanel("Mac: " + host.getMac());
			ui.addInfoPanel("Name: " + host.getName());
			ui.addInfoPanel("Reachable: " + Util.ping(host.getIp(), 100));

		}
		if (this.selectedHosts.size() > 1) {
			// TODO: Show the selected group ??
			for (Host host : this.selectedHosts) {
				ui.addInfoPanel("IP: " + host.getIp());
				ui.addInfoPanel("Mac: " + host.getMac());
				ui.addInfoPanel("Name: " + host.getName());
				ui.addInfoPanel("Reachable: " + Util.ping(host.getIp(), 100));
				ui.addInfoPanel("");
			}
		}
	}
	
	public void setSelectedSubmask(String submask) {
		this.selectedSubnetMask = submask;
	}
	
	public void actionWol() {
		if (selectedHosts == null) {
			Logger.error("selectedHosts is null");
			return;
		}
		if (selectedHosts.size() == 0) {
			Logger.debug("No host was selected");
			return;
		}
		try {
			for (Host host : selectedHosts) {
				Logger.debug("WOL to " + host.getIp());
				Util.sendWakeOnLan(host.getMac(), localBroadcast);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionShutdown() {
		if (selectedHosts == null) {
			Logger.error("selectedHosts is null");
			return;
		}
		if (selectedHosts.size() == 0) {
			Logger.debug("No host was selected");
			return;
		}
		try {
			for (Host host : selectedHosts) {
				Logger.debug("Shutdown " + host);
				Util.shutdown(host.getIp(), USERNAME, PASSWORD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionRestart() {
		if (selectedHosts == null) {
			Logger.error("selectedHosts is null");
			return;
		}
		if (selectedHosts.size() == 0) {
			Logger.debug("No host was selected");
			return;
		}
		try {
			for (Host host : selectedHosts) {
				Logger.debug("Shutdown " + host);
				Util.restart(host.getIp(), USERNAME, PASSWORD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionDelete() {
		Logger.debug(ACTION_DELETE);
		if (selectedHosts == null) {
			Logger.error("selectedHosts is null");
			return;
		}
		if (selectedHosts.size() == 0) {
			Logger.debug("No host was selected");
			return;
		}
        // Iterate over selectedHosts and remove matching entries from savedHosts
        for (Host selectedHost : selectedHosts) {
            Iterator<Map.Entry<String, Host>> iterator = savedHosts.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Host> entry = iterator.next();
                if (entry.getValue().equals(selectedHost)) {
                    iterator.remove();
                }
            }
        }
        ui.setHosts(savedHosts);
        Json.save(FILENAME, savedHosts);
        Logger.debug(ACTION_DELETE+" done");
	}
    
    public void actionScan() {
    	
    	Logger.debug("Scan...");
		try {
			BetterNetworkScanner scanner = new BetterNetworkScanner();
			foundHosts = scanner.scan(this.localIp, this.selectedSubnetMask, 100, 50);

			for (Host host:foundHosts) {
				savedHosts.put(host.getIp(), host);
				Logger.debug(host);
			}
			ui.setHosts(savedHosts);
			Json.save(FILENAME, savedHosts);

	        Logger.debug("Fertig.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

	public Map<String,NetworkInterface> getInterfaces() {
		return networkInterfaces;
	}
	
    /**
     *  Helper method to convert the network prefix length to a subnet mask
     * @param prefixLength
     * @return
     */
    private static String getSubnetMask(short prefixLength) {
        int mask = 0xffffffff << (32 - prefixLength);
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xFF,
                (mask >> 16) & 0xFF,
                (mask >> 8) & 0xFF,
                mask & 0xFF);
    }
	
}
