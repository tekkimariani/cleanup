package com.tekkimariani.cleanup.net;

import java.net.InetAddress;
import java.util.Objects;

public class Host {
	
    private String ip;
    private String mac;
    private String name;
    private boolean reachable;
    
    public Host() {
    	// Requried for Jackson
    }

    public Host(String ip, String mac, boolean isReachable) {
        this.ip = ip;
        this.mac = mac;
        this.reachable = isReachable;
        this.name = getHostname(ip);
    }

    public String getIp() {
		return ip;
	}

	public String getMac() {
		return mac;
	}
	
	public String getName() {
		return name;
	}

	public boolean isReachable() {
		return reachable;
	}
	
	public void setReachable(boolean b) {
		this.reachable = b;
	}

	@Override
    public String toString() {
        return "Host: '" + ip + "' | Mac: '"+mac+"' | Reachable: '" + reachable+ "'";
    }
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return Objects.equals(ip, host.ip); // Compare by IP
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }
    
    private static String getHostname(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            return addr.getHostName(); // Oft DNS/NetBIOS-Name, sonst = IP
        } catch (Exception e) {
            return null;
        }
    }
    
}

