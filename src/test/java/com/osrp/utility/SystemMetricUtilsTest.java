package com.osrp.utility;

import com.osrp.beans.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author saifasif
 */
public class SystemMetricUtilsTest {

    @Test
    public void testGetMetrics() throws IOException {
        Node systemMetrics = SystemMetricUtils.getSystemMetrics();
        System.out.println(systemMetrics);
    }

}