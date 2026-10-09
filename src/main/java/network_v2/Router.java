package network_v2;

import gui.utils.CmdUtils;
import network_core.AbstractRouter;
import network_core.NetworkConfig;

import java.net.*;

public class Router extends AbstractRouter<Table> {

    public Router() {
        routerTable = new Table();
    }

    @Override
    public void acceptConnections() {
        try {
            Socket otherRouter = serverSocket.accept();

            if (checkExistingConnection(otherRouter)) {
                logger.debug("Connection already exists");
                CmdUtils.getSharedCMDBuilder().append("$- Connection already exists, try again later\n");
            } else {
                logger.info("New connection from {}", otherRouter.getInetAddress());
                CmdUtils.getSharedCMDBuilder().append("$- New connection from " + otherRouter.getInetAddress() + "\n");

                connectionHistory.add(otherRouter);
                routerTable.addNewEntry(
                    InetAddress.getByName(NetworkConfig.getPrivateIp()),
                    otherRouter.getInetAddress(),
                    InetAddress.getByName("0.0.0.0"), 1);
                routerTable.displayTable();
                CmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());
            }
        } catch (java.net.SocketException e) {
            if (isRunning()) {
                logger.error("Socket error accepting connections", e);
            }
            // else: shutdown in progress, expected
        } catch (Exception e) {
            logger.error("Error accepting connections", e);
        }
    }

    @Override
    public void requestConnection(String ip) {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), NetworkConfig.RIP_ROUTER_PORT);
            if (checkExistingConnection(socket)) {
                logger.debug("Connection already exists");
                CmdUtils.getSharedCMDBuilder().append("$- Connection to " + ip + " already exists\n");
            } else {
                logger.info("Connected to {}", ip);
                CmdUtils.getSharedCMDBuilder().append("$- Connected to " + ip + "\n");

                connectionHistory.add(socket);
                routerTable.addNewEntry(
                    InetAddress.getByName(NetworkConfig.getPrivateIp()),
                    socket.getInetAddress(),
                    InetAddress.getByName("0.0.0.0"), 1);
                routerTable.displayTable();
                CmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());
            }
        } catch (ConnectException e) {
            logger.warn("Connection refused to {} - is the remote router running?", ip);
            CmdUtils.getSharedCMDBuilder().append("$- CONNECTION REFUSED to " + ip + " - Is the remote router ON?\n");
        } catch (Exception e) {
            logger.error("Error connecting to {}", ip, e);
            CmdUtils.getSharedCMDBuilder().append("$- ERROR connecting to " + ip + ": " + e.getMessage() + "\n");
        }
    }

    @Override
    public Table getTable() {
        return routerTable;
    }
}
