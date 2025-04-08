package com.tekkimariani.cleanup.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class FastNetworkScanner {

    private final String subnet;
    private final int timeout;
    private final int maxThreads;

    public FastNetworkScanner(String subnet, int timeout, int maxThreads) {
        this.subnet = subnet;
        this.timeout = timeout;
        this.maxThreads = maxThreads;
    }

    public List<Host> scan() throws InterruptedException {
        List<Host> results = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        CountDownLatch latch = new CountDownLatch(254);

        for (int i = 1; i <= 254; i++) {
            final int host = i;
            executor.submit(() -> {
                String ip = subnet + "." + host;
                
                try {
                    InetAddress inet = InetAddress.getByName(ip);
                    boolean reachable = inet.isReachable(timeout);
                    
                    if (reachable) {
                    	String mac = Util.getMacAddress(ip);
                        results.add(new Host(ip, mac, true));
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Warte bis alle fertig sind
        executor.shutdown();
        return results;
    }
}


