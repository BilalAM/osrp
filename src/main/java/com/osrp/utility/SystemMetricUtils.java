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

    public static void getSystemMetrics() throws IOException {
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

        OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
                mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

        long nanoBefore = System.nanoTime();
        long cpuBefore = osMBean.getProcessCpuTime();

// Call an expensive task, or sleep if you are monitoring a remote process

        long cpuAfter = osMBean.getProcessCpuTime();
        long nanoAfter = System.nanoTime();

        long percent;
        if (nanoAfter > nanoBefore)
            percent = ((cpuAfter - cpuBefore) * 100L) /
                    (nanoAfter - nanoBefore);
        else percent = 0;

        System.out.println("Cpu usage: " + percent + "%");
    }

    public static void getSystemMetrics(Node node) {

    }
}
