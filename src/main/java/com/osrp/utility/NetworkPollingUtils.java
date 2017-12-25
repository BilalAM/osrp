package com.osrp.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author saifasif
 */
public class NetworkPollingUtils {
    private final static int timeout = 1000;
    private static final Logger logger = LoggerFactory.getLogger(NetworkPollingUtils.class);

    public static List<InetAddress> getAliveHosts(String subnet) throws IOException {
        List<InetAddress> addresses = new ArrayList<>();
        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            if (InetAddress.getByName(host).isReachable(timeout)) {
                addresses.add(InetAddress.getByName(host));
                logger.info(host + " is reachable");
            }
        }
        return addresses;
    }

}
