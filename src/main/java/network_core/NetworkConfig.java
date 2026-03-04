package network_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.InetAddress;
import java.util.Enumeration;

/**
 * Centralized network configuration. Replaces all hardcoded ports and interface names.
 * Interface name can be overridden via system property: -Dosrp.interface=eth0
 */
public final class NetworkConfig {
    private static final Logger logger = LoggerFactory.getLogger(NetworkConfig.class);

    // RIP ports
    public static final int RIP_ROUTER_PORT = 2000;
    public static final int RIP_HOST_PORT = 2001;
    public static final int RIP_PACKET_PORT = 2002;

    // OSRP ports
    public static final int OSRP_ROUTER_PORT = 3000;
    public static final int OSRP_HOST_PORT = 3001;
    public static final int OSRP_PACKET_PORT = 3002;
    public static final int OSRP_HARDWARE_PORT = 3003;

    private static String cachedInterfaceName;

    private NetworkConfig() {}

    /**
     * Returns the network interface name to use.
     * Checks system property first, then auto-detects.
     */
    public static String getInterfaceName() {
        if (cachedInterfaceName != null) {
            return cachedInterfaceName;
        }

        String fromProperty = System.getProperty("osrp.interface");
        if (fromProperty != null && !fromProperty.isEmpty()) {
            cachedInterfaceName = fromProperty;
            logger.info("Using interface from system property: {}", cachedInterfaceName);
            return cachedInterfaceName;
        }

        cachedInterfaceName = detectDefaultInterface();
        logger.info("Auto-detected network interface: {}", cachedInterfaceName);
        return cachedInterfaceName;
    }

    /**
     * Gets the private IP address of the configured network interface.
     */
    public static String getPrivateIp() {
        try {
            NetworkInterface ni = NetworkInterface.getByName(getInterfaceName());
            if (ni != null) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to get private IP for interface {}", getInterfaceName(), e);
        }
        // Fallback to localhost
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private static String detectDefaultInterface() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        return ni.getName();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to auto-detect network interface", e);
        }
        return "lo0"; // fallback
    }
}
