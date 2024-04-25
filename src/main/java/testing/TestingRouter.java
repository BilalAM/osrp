package testing;

import network_osrp.HardwarePollerPacket;
import org.hyperic.sigar.*;

public class TestingRouter {

    private static Sigar sigar = new Sigar();
    public static void main(String[] args) {
      //  HardwarePollerPacket pollerPacket = new HardwarePollerPacket();
        try {
            while(true) {
           //     System.out.println(pollerPacket.getRamUtilization());
            // //   System.out.println(pollerPacket.getCpuUtilization());
            //    System.out.println("\n");
                Thread.sleep(1000);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void getSystemStatistics(){
        Mem mem = null;
        CpuPerc cpuperc = null;
        FileSystemUsage filesystemusage = null;
        try {
            mem = sigar.getMem();
            cpuperc = sigar.getCpuPerc();
           // filesystemusage = sigar.getFileSystemUsage("C:");
        } catch (SigarException se) {
            se.printStackTrace();
        }


        System.out.println(mem.getUsedPercent()+"\t");
        System.out.println((cpuperc.getCombined()*100)+"\t");
        //System.out.print(filesystemusage.getUsePercent()+"\n");
    }
}
