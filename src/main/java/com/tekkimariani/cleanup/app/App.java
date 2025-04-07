package com.tekkimariani.cleanup.app;

import java.net.NetworkInterface;
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
	private String localSubnet;
	
	Map<String, Host> savedHosts = new HashMap<String, Host>();
	List<Host> foundHosts;
	private Host selectedHost;

	Map<String, NetworkInterface> networkInterfaces;
	NetworkInterface networkInterface;

	public App() {
		
		networkInterfaces = Util.listNetworkInterfaces();
		
		if (networkInterfaces.size() == 0) {
			// No activ network adapter
			// Just inform about this behavior.
			// TODO
		} else if (networkInterfaces.size() == 1) {
			// There is only one activ network adapter.
			// We can just choose this one an go on.
			this.setNetworkinterface(networkInterfaces.values().iterator().next());
		} else {
			// There are more than one activ network adapters.
			// Give the user the options to choose from.
			
		}
//		networkInterface = 
		
		
		
		ui = new Gui(this);
		
		localIp = Util.getLocalIp();	
		localSubnet = Util.getLocalSubnet();
		savedHosts = Json.load(FILENAME);
		
		ui.setLocalIp(localIp);
		ui.setLocalSubnet(localSubnet);
		ui.setHosts(savedHosts);

	}
	
	public void setNetworkinterface(NetworkInterface ni) {
		this.networkInterface = ni;
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
	        FastNetworkScanner scanner = new FastNetworkScanner(localSubnet, 100, 50);  // Timeout 100ms, 50 Threads
	        		
			
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
	
}
