package com.osrp.utility;

import org.hyperic.jni.ArchLoader;
import org.hyperic.jni.ArchNotSupportedException;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author saifasif
 */
public class SystemBridgeUtils {

    public static void read() throws ArchNotSupportedException, SigarException {
        ArchLoader archLoader = new ArchLoader();
        String archLibName = archLoader.getArchLibName();
        System.out.println(archLibName);

        System.setProperty("java.library.path", ".:/Users/saifasif/projects/osrp/src/main/resources/");


        Sigar sigar = new Sigar();
        sigar.enableLogging(true);

        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.gather(sigar);
        System.out.println(cpuInfo.getModel());

    }
}
