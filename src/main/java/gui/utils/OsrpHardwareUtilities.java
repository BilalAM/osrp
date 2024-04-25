package gui.utils;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;

public class OsrpHardwareUtilities {
    private static volatile Sigar sigar = new Sigar();

    public static double getRamUtilization(){
        Mem mem = null;
        try {
            mem = sigar.getMem();
        }catch(Exception e){
            e.printStackTrace();
        }
        return mem.getUsedPercent();
    }


    public static double getCpuUtilization(){
        CpuPerc cpuPerc = null;
        try{
            Thread.sleep(10);
            cpuPerc = sigar.getCpuPerc();
        }catch(Exception e){
            e.printStackTrace();
        }
        return (cpuPerc.getCombined() * 100);
    }
}
