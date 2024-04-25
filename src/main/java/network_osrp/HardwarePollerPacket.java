package network_osrp;


import java.io.Serializable;
import java.net.InetAddress;

/**
 *  this is similar to the com.osrp.beans.Node class by MSaifAsif (https://github.com/MSaifAsif)
 *
 */

public class HardwarePollerPacket implements Serializable {


    private double ramPercentage;
    private double cpuPercentage;
    private InetAddress fromAddress;

    public HardwarePollerPacket(double ramPercentage , double cpuPercentage , InetAddress fromAddress){
        this.ramPercentage = ramPercentage;
        this.cpuPercentage = cpuPercentage;
        this.fromAddress = fromAddress;
    }

    public double getRamPercentage(){
        return ramPercentage;
    }

    public double getCpuPercentage(){
        return cpuPercentage;
    }

    public InetAddress getFromAddress() {
        return fromAddress;
    }

    @Override
    public String toString(){
        return "[ RAM % ==> " + ramPercentage + " CPU % ==> " + cpuPercentage + " FROM ==> " + fromAddress.toString() + " ]";
    }
}
