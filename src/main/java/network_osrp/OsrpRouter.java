package network_osrp;

import gui.utils.CmdUtils;
import network_core.AbstractRouter;
import network_core.NetworkConfig;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * OSRP Router with additional hardware polling socket.
 * OSRP uses ports 3000-3003.
 */
public class OsrpRouter extends AbstractRouter<OsrpTable> {

    private ServerSocket pollerServerSocket;
    private final List<Socket> hardwareList = new CopyOnWriteArrayList<>();

    public OsrpRouter() {
        routerTable = new OsrpTable();
    }

    /**
     * Initialize OSRP router with the hardware poller socket.
     */
    public void initOsrp() throws IOException {
        init(NetworkConfig.OSRP_ROUTER_PORT, NetworkConfig.OSRP_HOST_PORT, NetworkConfig.OSRP_PACKET_PORT);
        pollerServerSocket = new ServerSocket(NetworkConfig.OSRP_HARDWARE_PORT, 1000, InetAddress.getByName("0.0.0.0"));
        logger.info("OSRP hardware poller initialized on port {}", NetworkConfig.OSRP_HARDWARE_PORT);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        closeQuietly(pollerServerSocket);
        for (Socket s : hardwareList) {
            closeQuietly(s);
        }
        hardwareList.clear();
        pollerServerSocket = null;
    }

    @Override
    public void acceptConnections() {
        try {
            Socket otherRouter = serverSocket.accept();
            Socket hardwareSocket = pollerServerSocket.accept();

            if (checkExistingConnection(otherRouter) && checkExistingHardwareConnection(hardwareSocket)) {
                logger.debug("Connection already exists");
                CmdUtils.osrpInstance().getCMDBuilder().append("$- Connection already exists, try again later\n");
            } else {
                logger.info("New OSRP connection from {}", otherRouter.getInetAddress());
                CmdUtils.osrpInstance().getCMDBuilder().append("$- New connection from " + otherRouter.getInetAddress() + "\n");

                connectionHistory.add(otherRouter);
                hardwareList.add(hardwareSocket);
                routerTable.addNewEntry(
                    InetAddress.getByName(NetworkConfig.getPrivateIp()),
                    otherRouter.getInetAddress(),
                    InetAddress.getByName("0.0.0.0"), 0, 1);
                routerTable.displayTable();
                CmdUtils.osrpInstance().getRoutingCMDBuilder().append(routerTable.getTableBuilder());
            }
        } catch (java.net.SocketException e) {
            if (isRunning()) {
                logger.error("Socket error accepting OSRP connections", e);
            }
        } catch (Exception e) {
            logger.error("Error accepting OSRP connections", e);
        }
    }

    @Override
    public void requestConnection(String ip) {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), NetworkConfig.OSRP_ROUTER_PORT);
            Socket hardwareSocket = new Socket(InetAddress.getByName(ip), NetworkConfig.OSRP_HARDWARE_PORT);

            if (checkExistingConnection(socket) && checkExistingHardwareConnection(hardwareSocket)) {
                logger.debug("Connection already exists");
                CmdUtils.osrpInstance().getCMDBuilder().append("$- Connection to " + ip + " already exists\n");
            } else {
                logger.info("Connected to OSRP router at {}", ip);
                CmdUtils.osrpInstance().getCMDBuilder().append("$- Connected to " + ip + "\n");

                connectionHistory.add(socket);
                hardwareList.add(hardwareSocket);
                routerTable.addNewEntry(
                    InetAddress.getByName(NetworkConfig.getPrivateIp()),
                    socket.getInetAddress(),
                    InetAddress.getByName("0.0.0.0"), 0, 1);
                routerTable.displayTable();
                CmdUtils.osrpInstance().getRoutingCMDBuilder().append(routerTable.getTableBuilder());
            }
        } catch (ConnectException e) {
            logger.warn("Connection refused to {} - is the remote OSRP router running?", ip);
            CmdUtils.osrpInstance().getCMDBuilder().append("$- CONNECTION REFUSED to " + ip + " - Is the remote router ON?\n");
        } catch (Exception e) {
            logger.error("Error connecting to OSRP router at {}", ip, e);
            CmdUtils.osrpInstance().getCMDBuilder().append("$- ERROR connecting to " + ip + ": " + e.getMessage() + "\n");
        }
    }

    private boolean checkExistingHardwareConnection(Socket router) {
        for (Socket socket : hardwareList) {
            if (socket.getInetAddress().equals(router.getInetAddress())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OsrpTable getTable() { return routerTable; }
    public OsrpTable getRouterTable() { return routerTable; }
    public List<Socket> getHardwareList() { return hardwareList; }
    public ServerSocket getPollerServerSocket() { return pollerServerSocket; }
}
