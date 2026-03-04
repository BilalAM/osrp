package network_core;

import gui.utils.CmdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for table responders shared by both RIP and OSRP protocols.
 * Provides the common broadcastTables, receiveTables, and sendTable logic.
 *
 * @param <E> the entry type
 * @param <T> the routing table type
 * @param <R> the router type
 */
public abstract class AbstractResponder<E extends AbstractEntry, T extends AbstractRoutingTable<E>, R extends AbstractRouter<T>> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected List<T> tables = new ArrayList<>();

    /**
     * Gets the routing table from the router (protocol-specific method name).
     */
    protected abstract T getTable(R router);

    /**
     * Creates the appropriate updater for this protocol.
     */
    protected abstract AbstractUpdater<E, T> createUpdater();

    /**
     * Deserializes a table from the input stream.
     * Returns null if the object is not the expected type.
     */
    protected abstract T deserializeTable(ObjectInputStream input) throws Exception;

    /**
     * ATTEMPTS to 'broadcast' one by one the outputStreams() of 'other sockets'
     * currently in our list and writeObject() OUR table.
     *
     * @param router : Our router (current machine)
     */
    public void broadcastTables(R router) {
        try {
            logger.debug("Attempting to contact other routers to send tables to");
            if (isEmptyList(router)) {
                logger.debug("No router currently connected");
            }
            for (Socket otherRouter : router.getConnectionHistory()) {
                logger.debug("Connected router found");
                sendTable(router, otherRouter);
            }
        } catch (Exception e) {
            logger.error("Error broadcasting tables", e);
        }
    }

    /**
     * ATTEMPTS to receive one by one the inputStreams() of 'other sockets'
     * currently in our list and readObject() the tables.
     *
     * The ( otherRouter.getInputStream().available() > 0 ) is to check if the
     * stream has currently data on it, if it has then read it, else we suppose
     * the router or socket has not sent any data as of yet.
     *
     * @param router : Our router (current machine)
     */
    public void receiveTables(R router) {
        try {
            if (isEmptyList(router)) {
                logger.debug("No router currently connected");
            }
            for (Socket otherRouter : router.getConnectionHistory()) {
                logger.debug("Connected router found");
                if (otherRouter.getInputStream().available() > 0) {
                    _receiveTables(otherRouter, router);
                } else {
                    logger.debug("Router at {} has not sent table yet", otherRouter.getInetAddress());
                }
            }
        } catch (Exception e) {
            logger.error("Error receiving tables", e);
        }
    }

    /**
     * Helper method for receiveTables logic.
     *
     * @param otherRouter the socket of the other router
     * @param selfRouter  our router
     * @throws Exception POSSIBLE EXCEPTIONS: Connection reset, SocketNotFound or StreamCorrupted
     */
    private void _receiveTables(Socket otherRouter, R selfRouter) throws Exception {
        AbstractUpdater<E, T> updater = createUpdater();
        ObjectInputStream input = new ObjectInputStream(otherRouter.getInputStream());

        CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To Receive Table From The Stream...\n");
        logger.debug("Attempting to receive table from stream");

        T t = deserializeTable(input);
        if (t != null) {
            tables.add(t);
            CmdUtils.getSharedRIPCMDBuilder().append("$- A table Is Received And Added To The Inner List\n");
            logger.debug("A table is received and added to the inner list");
            t.displayTable();
            CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To UPDATE Our Table..\n");
            logger.debug("Attempting to update our table");
            updater.Update(getTable(selfRouter), t);
            CmdUtils.getSharedRIPCMDBuilder().append("$- Our New Table With A UPDATED Entry\n");
            logger.debug("Our updated table with a new entry");
            getTable(selfRouter).displayTable();
            logger.debug("Is socket closed (with stream)? {}", otherRouter.isClosed());
        }
    }

    /**
     * Helper method of sendTables() logic.
     */
    private void sendTable(R fromRouter, Socket toRouter) throws Exception {
        ObjectOutputStream outputStream = new ObjectOutputStream(toRouter.getOutputStream());
        CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To Write A Table To The Stream..\n");
        logger.debug("Attempting to write object to stream");
        outputStream.writeUnshared(getTable(fromRouter));
        outputStream.flush();
        CmdUtils.getSharedRIPCMDBuilder().append("$- Table Sent To The Stream..\n");
        logger.debug("Table sent to stream");
    }

    public static boolean isEmptyList(AbstractRouter<?> router) {
        return router.getConnectionHistory().isEmpty();
    }

    public List<T> getTables() {
        return tables;
    }
}
