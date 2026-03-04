package gui.utils;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsrpHardwareUtilities {
    private static final Logger logger = LoggerFactory.getLogger(OsrpHardwareUtilities.class);
    private static volatile Sigar sigar = new Sigar();

    public static double getRamUtilization(){
        Mem mem = null;
        try {
            mem = sigar.getMem();
        }catch(Exception e){
            logger.error("Failed to get RAM utilization", e);
        }
        return mem.getUsedPercent();
    }


    public static double getCpuUtilization(){
        CpuPerc cpuPerc = null;
        try{
            Thread.sleep(10);
            cpuPerc = sigar.getCpuPerc();
        }catch(Exception e){
            logger.error("Failed to get CPU utilization", e);
        }
        return (cpuPerc.getCombined() * 100);
    }
}
