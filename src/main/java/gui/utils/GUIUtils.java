package gui.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Short com.osrp.utility methods for the User Interface
 *
 * @author bilalam
 */
public class GUIUtils {

    // testing
    public static void main(String[] args) {
        GUIUtils.getPrivateIp("wlo1");
    }

    public static String getPrivateIp(String interfaceName) {
        String ip = "";
        try {
            Enumeration<NetworkInterface> netInter = NetworkInterface.getNetworkInterfaces();
            while (netInter.hasMoreElements()) {
                NetworkInterface ni = netInter.nextElement();
                if (ni.getDisplayName().equals(interfaceName)) {
                    Enumeration<InetAddress> iterator = ni.getInetAddresses();
                    while (iterator.hasMoreElements()) {
                        InetAddress address = iterator.nextElement();
                        // to ignore ipv6 addresses , is this a good approach ?
                        if (address.getHostAddress().contains(":")) {
                            continue;
                        } else {
                            ip = address.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static String getSelfIp() {
        String selfIp = null;
        try {
            InetAddress local = InetAddress.getLocalHost();
            selfIp = local.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selfIp;
    }

    public static String getMAC() {
        String mac = "";
        try {
            NetworkInterface ni = NetworkInterface.getByName("wlo1");
            byte[] macc = ni.getHardwareAddress();

            for (int i = 0; i < macc.length; i++) {
                mac += String.format("%02X%s", macc[i], (i < macc.length - 1) ? "-" : "");
            }
            return mac;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

}
