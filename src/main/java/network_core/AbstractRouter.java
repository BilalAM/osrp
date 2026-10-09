package network_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base class for routers shared by both RIP and OSRP protocols.
 * ServerSockets are instance fields initialized via init() and cleaned up via shutdown().
 */
public abstract class AbstractRouter<T extends AbstractRoutingTable<? extends AbstractEntry>> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ServerSocket serverSocket;
    protected ServerSocket hostServerSocket;
    protected ServerSocket packetServerSocket;
    protected final List<Socket> connectionHistory = new CopyOnWriteArrayList<>();

    protected T routerTable;
    protected StringBuilder cmdBuilder = new StringBuilder();
    protected StringBuilder routingTableBuilder = new StringBuilder();
    protected StringBuilder hostBuilder = new StringBuilder();

    private volatile boolean running = false;

    /**
     * Checks if a connection to the given socket's address already exists.
     */
    protected boolean checkExistingConnection(Socket router) {
        for (Socket socket : connectionHistory) {
            if (socket.getInetAddress().equals(router.getInetAddress())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialize server sockets. Must be called before acceptConnections().
     * Binds to 0.0.0.0 (all interfaces) so localhost connections work.
     */
    public void init(int routerPort, int hostPort, int packetPort) throws IOException {
        if (running) return;
        serverSocket = new ServerSocket(routerPort);
        hostServerSocket = new ServerSocket(hostPort);
        packetServerSocket = new ServerSocket(packetPort, 100, InetAddress.getByName("0.0.0.0"));
        running = true;
        logger.info("Router initialized on ports {}, {}, {}", routerPort, hostPort, packetPort);
    }

    /**
     * Shutdown the router - close all sockets and clear connections.
     */
    public void shutdown() {
        running = false;
        closeQuietly(serverSocket);
        closeQuietly(hostServerSocket);
        closeQuietly(packetServerSocket);
        for (Socket s : connectionHistory) {
            closeQuietly(s);
        }
        connectionHistory.clear();
        if (routerTable != null) {
            routerTable.getEntries().clear();
        }
        serverSocket = null;
        hostServerSocket = null;
        packetServerSocket = null;
        logger.info("Router shut down");
    }

    protected void closeQuietly(java.io.Closeable closeable) {
        if (closeable != null) {
            try { closeable.close(); } catch (IOException ignored) {}
        }
    }

    public boolean isRunning() { return running; }
    public List<Socket> getConnectionHistory() { return connectionHistory; }
    public ServerSocket getServerSocket() { return serverSocket; }
    public ServerSocket getHostServerSocket() { return hostServerSocket; }
    public ServerSocket getPacketServerSocket() { return packetServerSocket; }

    public abstract void acceptConnections();
    public abstract void requestConnection(String ip);
    public abstract T getTable();
}
