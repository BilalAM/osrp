package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
	private static Scanner scanner = new Scanner(System.in);
	public static StringBuilder builder = new StringBuilder();


	public Router(String name, int port) {
		this.PORT = port;
		try {
			selfServer = new ServerSocket(PORT);
			Router.name = name;
			routerTable = new Table(selfServer.getInetAddress());
			System.out.println("Empty Router created by name.... " + name);
			//builder.append("router created");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("WARNING*** , Router " + name + " cannot be created at Port " + port);
		}
	}

	/**
	 * listens for active connection from other router and add that entry into table
	 * , also 'saves' the connections in a list
	 */
	public String initialize() {

		Socket otherRouter = null;
		try {

			builder.append("\n");
			// System.out.println("waiting...");
				
				
				otherRouter = selfServer.accept();
				builder.append("Router Connection Found");
				builder.append("\n");
				if (checkExistingConnection(otherRouter)) {
					System.out.println("Connection already exists....");

					builder.append("connection exists");
					builder.append("\n");
				} else {
					
					// if(checkTypeOfConnection().equals("R"){
					//		processRouter();
					//      }else{
					//		processPacket();
					//	}
					/*
					try(ObjectInputStream inputPacket = new ObjectInputStream(otherRouter.getInputStream())){
						Packet packet = (Packet)inputPacket.readObject();
						
					}
					*/
					builder.append("\n");
					System.out.println("Router connection found...");
					builder.append("\n");
					System.out.println("Details  :- ");
					builder.append("Details :- ");
					builder.append("\n");
					builder.append("-Address : " + otherRouter.getInetAddress());
					System.out.println("-Address : " + otherRouter.getInetAddress());
					builder.append("\n");
					builder.append("-Port : " + otherRouter.getPort());
					System.out.println("-Port    :" + otherRouter.getPort());
					builder.append("\n");
					System.out.println("adding new entry in table....");
					builder.append("Adding new Entry in the table..");
					builder.append("\n");
					routerTable.addNewEntry(otherRouter.getInetAddress(), otherRouter.getInetAddress(), 1);
					historyOfRouterConnections.add(otherRouter);
					routerTable.displayTable();
					builder.append(routerTable.builder.toString());
					builder.append("\n");
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
		return builder.toString();
	}

	public String getCMD() {
		return builder.toString();
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
	 * tries to connect to a seperate router
	 */
	@Deprecated
	public void connectToRouter() {
		System.out.println("Enter IP Address Of Router To Connect");
		String ip = scanner.nextLine();
		Socket socket = null;
		try {
			socket = new Socket(InetAddress.getByName(ip),PORT);
			if (checkExistingConnection(socket)) {
				builder.append("\n");
				builder.append("connection already exists");
				System.out.println("Connection already exists....");
				socket.close();
			} else {
				socket = new Socket(InetAddress.getByName(ip), PORT);
				builder.append("\n");
				builder.append("Successfully connected to a router");
				System.out.println("Connected to a router " + ip);
				// add this to the history
				historyOfRouterConnections.add(socket);
				routerTable.addNewEntry(socket.getInetAddress(), socket.getInetAddress(), 1);
			}
		} catch (Exception e) {
			System.out.println("cannot connect ");
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * tries to connect to a seperate router and returns true if success
	 */
	public boolean connectToRouter(String ip) {
		boolean checkFlag;
		//System.out.println("Enter IP Address Of Router To Connect");
		//String ip = scanner.nextLine();
		Socket socket = null;
		try {
			socket = new  Socket(InetAddress.getByName(ip), PORT);
			if (checkExistingConnection(socket)) {
				builder.append("\n");
				builder.append("connection already exists \n ");
				System.out.println("Connection already exists....");
				checkFlag = false;
				socket.close();
				return checkFlag;
			} else {
				//socket = new Socket(InetAddress.getByName(ip), PORT);
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
	public void initiateProcess() {
		Callable<Void> process1 = (() -> {
			initialize();
			return null;
		});
		Callable<Void> process2 = (() -> {
			connectToRouter();
			return null;
		});
		List<Callable<Void>> list = new ArrayList<>();
		list.add(process1);
		list.add(process2);

		try {
			while (true) {
				connectToRouter();
				initialize();
				// connectToRouter();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static void main(String args[]) {
		// Router router = new Router("bilal", 1999);
		// router.initiateProcess();
	}

}
