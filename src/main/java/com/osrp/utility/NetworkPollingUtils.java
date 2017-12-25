package com.osrp.utility;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author saifasif
 */
public class NetworkPollingUtils {
    private final static int timeout = 1000;

    public static List<InetAddress> getAliveHosts(String subnet) throws IOException {
        List<InetAddress> addresses = new ArrayList<>();
        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            if (InetAddress.getByName(host).isReachable(timeout)) {
                addresses.add(InetAddress.getByName(host));
                System.out.println(host + " is reachable");
            }
        }
        return addresses;
    }

}
