package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class Router {

	public static String name;
	public static Table routerTable;
	private static List<Socket> historyOfRouterConnections = new ArrayList<>();
	private ServerSocket selfServer;
	private int PORT;
	private StringBuilder builder = new StringBuilder();
	private StringBuilder directConnectionBuilder = new StringBuilder();
	private StringBuilder listenConnectionBuilder = new StringBuilder();

	public Router(String name, int port) {
		this.PORT = port;
		try {
			selfServer = new ServerSocket(PORT);
			Router.name = name;
			routerTable = new Table(selfServer.getInetAddress());
			System.out.println("Empty Router created by name.... " + name);
			// builder.append("router created");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("WARNING*** , Router " + name + " cannot be created at Port " + port);
		}
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

				System.out.println("Details  :- ");
				System.out.println("-Address : " + otherRouter.getInetAddress());
				System.out.println("-Port    :" + otherRouter.getPort());
				System.out.println("adding new entry in table....");
				routerTable.displayTable();
			}
			otherRouter.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				otherRouter.close();
			} catch (IOException e) {
				e.printStackTrace();
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
				System.out.println("Connection already exists....");
				checkFlag = false;
				socket.close();
				return checkFlag;
			} else {
				// socket = new Socket(InetAddress.getByName(ip), PORT);
				builder.append("\n");
				builder.append("Successfully connected to a router \n ");
				System.out.println("Connected to a router " + ip);
				// add this to the history
				historyOfRouterConnections.add(socket);
				routerTable.addNewEntry(socket.getInetAddress(), socket.getInetAddress(), 1);
				checkFlag = true;
				return checkFlag;
			}
		} catch (Exception e) {
			System.out.println("cannot connect , Router or Host is currently unreachable or not connected");

			checkFlag = false;
		}
		return checkFlag;
	}

	/**
	 * 
	 * @param the
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

	/**
	 * RouterA routerA = new RouterA("bilal router"); routerA.initialize();
	 * routerA.connectTo(routerB); routerA.connectTo(routerC);
	 * 
	 * RouterB routerB = new RouterB("sara router"); routerB.initialize();
	 * routerB.connectTo(routerA); routerB.connectTo(routerC);
	 * 
	 * RouterC routerC = new RouterC("asad router"); routerC.initialize();
	 * routerC.connectTo(routerA); routerC.connectTo(routerB);
	 * 
	 * 
	 * NetworkGrid gr7idA = new NetworkGrid(routerA); gridA.learnFrom(routerB)
	 * //send B table to A gridA.learnFrom(routerC); // send C table to A
	 * gridA.showTable();
	 * 
	 * NetworkGrid gridB = new Networkrid(routerB); gridB.learnFrom(routerC);
	 * gridB.showTable();
	 * 
	 * Host bahria = new Host(); bahria.connectAndSend(routerA,"my destination
	 * network"); bahria.showPath();
	 * 
	 * 
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

	public static void main(String args[]) {
		// Router router = new Router("bilal", 1999);
		// router.initiateProcess();
	}

}
