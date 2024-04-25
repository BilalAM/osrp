package com.osrp.utility;

import com.osrp.beans.Node;
import com.sun.management.OperatingSystemMXBean;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * @author saifasif
 */
public class SystemMetricUtils {

    public static Node getSystemMetrics() throws IOException {
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

        OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
                mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

        long nanoBefore = System.nanoTime();
        double cpuUsage = osMBean.getSystemCpuLoad();
        long freePhysicalMemorySize = osMBean.getFreePhysicalMemorySize();
        Node node = new Node();
        node.setMemLoad(freePhysicalMemorySize);
        node.setCpuLoad(cpuUsage);
        return node;
    }
}
