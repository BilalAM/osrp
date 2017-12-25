package com.osrp.utility;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * @author saifasif
 */
class NetworkPollingUtilsTest {

    @Test
    void getAliveHosts() throws IOException {

        List<InetAddress> aliveHosts = NetworkPollingUtils.getAliveHosts("192.168.1.1");
        org.junit.jupiter.api.Assertions.assertTrue(!aliveHosts.isEmpty());
    }
}