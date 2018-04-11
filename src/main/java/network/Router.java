package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	 * @param toCheck
	 *            new socket to check for prior existance
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
	 * , also 'saves' the connections in a list . <b> ALSO RECEIVES THE TABLE IN OUTPUT
	 * STREAM </b> </br/>
	 * <p>
     * The standard sysouts are for <b>testing purposes only</b>
	 * </p>
	 * <br/>
     * <b>WORKING</b>
     * <p>Waits for incoming connections , if a connection is received , saves the socket in a list and also receives the table with it self</p>
	 */
    public String initialize(){
        ObjectInputStream inputTable;
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

				// receive the table from the input stream and print for test
                inputTable = new ObjectInputStream(otherRouter.getInputStream());
                Table table = (Table) inputTable.readObject();
                System.out.println("table recieved...");
				System.out.println(table.displayTable());


				//tableStream.close();
				listenConnectionBuilder.append(routerTable.displayTable() + "\n");

				logger.trace("----------------");
				logger.trace("Details  :- ");
				logger.trace("-Address : " + otherRouter.getInetAddress());
				logger.trace("-Port    :" + otherRouter.getPort());
				logger.trace("adding new entry in table....");
				logger.trace("----------------");
				//print it on console
				//routerTable.displayTable();
			}
			// otherRouter.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				// otherRouter.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		testHistoryList();
		return listenConnectionBuilder.toString();
	}

	/**
	 * <b>Direct Connection</b> <br/>
	 * tries to connect to a seperate router and returns true if success
	 */
	public boolean connectToRouter(String ip) {
		ObjectOutputStream outputObject = null;
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
				//socket.close();
				return checkFlag;
			} else {
				// socket = new Socket(InetAddress.getByName(ip), PORT);
				builder.append("\n");
				builder.append("Successfully connected to a router \n ");
				logger.info("Connected to a router " + ip);
				// add this to the history
				historyOfRouterConnections.add(socket);
				routerTable.addNewEntry(socket.getInetAddress(), socket.getInetAddress(), 1);
				
				// encapsulate the table and send it to output stream
				outputObject = new ObjectOutputStream(socket.getOutputStream());
				
				outputObject.writeUnshared(routerTable);
				outputObject.flush();
				checkFlag = true;
				
				//test history list
				testHistoryList();
				return checkFlag;
			}
		} catch (Exception e) {

			logger.warn("cannot connect , Router or Host is currently unreachable or not connected");

			checkFlag = false;
		} finally {
			try {
				// socket.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return checkFlag;
	}

	
	private void testHistoryList() {
		for(Socket socket : getHistoryOfConnections()) {
			System.out.println(socket.getInetAddress() + " STATUS ( IS CLOSED? ) " + socket.isClosed());
		}
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
