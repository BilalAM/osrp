package gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Short com.osrp.utility methods for the User Interface
 *
 * @author bilalam
 */
public class GUIUtils {
    private static final Logger logger = LoggerFactory.getLogger(GUIUtils.class);

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
            logger.error("Failed to get private IP for interface {}", interfaceName, e);
        }
        return ip;
    }

    public static String getSelfIp() {
        String selfIp = null;
        try {
            InetAddress local = InetAddress.getLocalHost();
            selfIp = local.getHostAddress();
        } catch (Exception e) {
            logger.error("Failed to get self IP", e);
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
            logger.error("Failed to get MAC address", e);
        }
        return mac;
    }

}
