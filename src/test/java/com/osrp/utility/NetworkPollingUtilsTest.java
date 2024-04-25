package com.osrp.utility;

import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * @author saifasif
 */
public class NetworkPollingUtilsTest {

    @Disabled
    public void getAliveHosts() throws IOException {

        List<InetAddress> aliveHosts = NetworkPollingUtils.getAliveHosts("192.168.1.1");
        System.out.println(aliveHosts.size());
    }
}