package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Router {

    public static String name;
    public static Table routerTable;
    private static List<Socket> historyOfRouterConnections = new ArrayList<>();
    private ServerSocket selfServer;
    private int PORT;
    private StringBuilder builder = new StringBuilder();
    private StringBuilder directConnectionBuilder = new StringBuilder();
    private StringBuilder listenConnectionBuilder = new StringBuilder();
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    public Router(String name, int port) {
        this.PORT = port;
        try {
            selfServer = new ServerSocket(PORT);
            Router.name = name;
            routerTable = new Table(selfServer.getInetAddress());
            logger.info("Empty Router created by name.... " + name);
            // builder.append("router created");
        } catch (Exception e) {
            logger.warn("WARNING*** , Router " + name + " cannot be created at Port " + port);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param toCheck new socket to check for prior existance
     * @return whether the socket already exists or not
     */
    private static boolean checkExistingConnection(Socket toCheck) {
        for (Socket socket : historyOfRouterConnections) {
            if (socket.getInetAddress().equals(toCheck.getInetAddress())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]) {
        Router router = new Router("bilal", 1999);
        router.initialize();
    }

    /**
     * <b>Listen Connection</b> <br/>
     * listens for active connection from other router and add that entry into table
     * , also 'saves' the connections in a list
     * </br/>
     * <p>The standard sysouts are for <b>testing purposes only</b></p>
     */
    public String initialize() {

        Socket otherRouter = null;
        try {
            listenConnectionBuilder.append("\n");

            // System.out.println("waiting...");

            otherRouter = selfServer.accept();
            listenConnectionBuilder.append("======================================== \n ");
            listenConnectionBuilder.append("*** ROUTER CONNECTION FOUND ! *** \n");

            if (checkExistingConnection(otherRouter)) {
                System.out.println("Connection already exists....");

                listenConnectionBuilder.append("*** CONNECTION ALREADY EXISTS ! , RESUMING LISTENING ! *** \n");
                listenConnectionBuilder.append("======================================== \n ");
            } else {

                System.out.println("Router connection found...");
                listenConnectionBuilder.append("* DETAILS OF CONNECTION * \n");
                listenConnectionBuilder.append("-ADDRESS 			: " + otherRouter.getInetAddress() + "\n");
                listenConnectionBuilder.append("-PORT 	 			: " + otherRouter.getPort() + "\n");
                listenConnectionBuilder.append("-TIME OF CONNECTION : "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                listenConnectionBuilder.append("* ADDING ENTRY IN TABLE * \n");
                listenConnectionBuilder.append("======================================== \n ");

                // populate the history lists
                routerTable.addNewEntry(otherRouter.getInetAddress(), otherRouter.getInetAddress(), 1);
                historyOfRouterConnections.add(otherRouter);

                listenConnectionBuilder.append(routerTable.displayTable() + "\n");

                logger.trace("----------------");
                logger.trace("Details  :- ");
                logger.trace("-Address : " + otherRouter.getInetAddress());
                logger.trace("-Port    :" + otherRouter.getPort());
                logger.trace("adding new entry in table....");
                logger.trace("----------------");
                routerTable.displayTable();
            }
            otherRouter.close();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                otherRouter.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return listenConnectionBuilder.toString();
    }

    /**
     * <b>Direct Connection</b> <br/>
     * tries to connect to a seperate router and returns true if success
     */
    public boolean connectToRouter(String ip) {
        boolean checkFlag;
        // System.out.println("Enter IP Address Of Router To Connect");
        // String ip = scanner.nextLine();
        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName(ip), PORT);
            if (checkExistingConnection(socket)) {
                builder.append("\n");
                builder.append("connection already exists \n ");
                logger.debug("Connection already exists....");
                checkFlag = false;
                socket.close();
                return checkFlag;
            } else {
                // socket = new Socket(InetAddress.getByName(ip), PORT);
                builder.append("\n");
                builder.append("Successfully connected to a router \n ");
                logger.info("Connected to a router " + ip);
                // add this to the history
                historyOfRouterConnections.add(socket);
                routerTable.addNewEntry(socket.getInetAddress(), socket.getInetAddress(), 1);
                checkFlag = true;
                return checkFlag;
            }
        } catch (Exception e) {

            logger.warn("cannot connect , Router or Host is currently unreachable or not connected");

            checkFlag = false;
        }
        return checkFlag;
    }

    /**
     * RouterA routerA = new RouterA("bilal router"); routerA.initialize();
     * routerA.connectTo(routerB); routerA.connectTo(routerC);
     * <p>
     * RouterB routerB = new RouterB("sara router"); routerB.initialize();
     * routerB.connectTo(routerA); routerB.connectTo(routerC);
     * <p>
     * RouterC routerC = new RouterC("asad router"); routerC.initialize();
     * routerC.connectTo(routerA); routerC.connectTo(routerB);
     * <p>
     * <p>
     * NetworkGrid gr7idA = new NetworkGrid(routerA); gridA.learnFrom(routerB)
     * //send B table to A gridA.learnFrom(routerC); // send C table to A
     * gridA.showTable();
     * <p>
     * NetworkGrid gridB = new Networkrid(routerB); gridB.learnFrom(routerC);
     * gridB.showTable();
     * <p>
     * Host bahria = new Host(); bahria.connectAndSend(routerA,"my destination
     * main.java.network"); bahria.showPath();
     */

    private void constructListenConnectionBuilder(Router selfRouter) {

    }

    // GETTERS
    public String getCMD() {
        return builder.toString();
    }

    public String getBuilder() {
        return builder.toString();
    }

    public String getDirectConnectionBuilder() {
        return directConnectionBuilder.toString();
    }

    public String getListenConnectionBuilder() {
        return listenConnectionBuilder.toString();
    }

    public List<Socket> getHistoryOfConnections() {
        return Router.historyOfRouterConnections;
    }

}
