package network_osrp;


import gui.utils.GUIUtils;
import gui.utils.OsrpCmdUtils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 *      RIP RUNS ON PORTS 2000,2001 AND 2002
 *
 *      OSRP RUNS ON PORTS 3000,3001,3002
 *
 *      DEFAULT RANKS ARE 0
 *
 */



public class OsrpRouter {


    private static ServerSocket serverSocket;
    private static ServerSocket hostServerSocket;
    private static ServerSocket packetSererSocket;
    private static ServerSocket pollerServerSocket;
    private static List<Socket> connectionHistory = new ArrayList<>();
    private static List<Socket> hardwareList = new ArrayList<>();
    private static OsrpTable routerTable;
    private static StringBuilder cmdBuilder;
    private static StringBuilder routingTableBuilder;
    private static StringBuilder hostBuilder;
    
    /**
     * bind 'this' router to listen for other 'routers' on
     * port 2000 and this 'router' to listen for 'hosts' on port 2001
     */
    static {
        try {
            hostServerSocket = new ServerSocket(3001);
            serverSocket = new ServerSocket(3000);
            packetSererSocket = new ServerSocket(3002, 100, InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")));
            pollerServerSocket = new ServerSocket(3003,1000,InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OsrpRouter() {
        routerTable = new OsrpTable();
        cmdBuilder = new StringBuilder();
        routingTableBuilder = new StringBuilder();
        hostBuilder = new StringBuilder();
    }

    public void acceptConnections() {
        try {
            Socket otherRouter = serverSocket.accept();
            Socket _otherRouter = pollerServerSocket.accept();

            if (checkExistingConnection(otherRouter) && checkExistingHardwareConnection(_otherRouter)) {
                System.out.println("connection already exists !!");
                cmdBuilder.append("$- Router Connection Already Exist ..Try Again Later..\n");
                OsrpCmdUtils.getSharedCMDBuilder().append("$- Router Connection Already Exist ..Try Again Later..\n");
            } else {
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");
                cmdBuilder.append("$- A New Connection Has Been Found....\n");
                cmdBuilder.append("$- Adding The New Router Entry To The List And Table...\n");

                OsrpCmdUtils.getSharedCMDBuilder().append("$- A New Connection Has Been Found....\n");
                OsrpCmdUtils.getSharedCMDBuilder().append("$- Adding The New Router Entry To The List And Table...\n");

                connectionHistory.add(otherRouter);
                hardwareList.add(_otherRouter);
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")), otherRouter.getInetAddress(), InetAddress.getByName("0.0.0.0"), 0,1);
                routerTable.displayTable();

                //  routingTableBuilder.append(routerTable.getTableBuilder());

                OsrpCmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());

                //  buildCMD(otherRouter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // a type of client...
    public void requestConnection(String ip) {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), 3000);
            Socket _socket = new Socket(InetAddress.getByName(ip),3003);
            if (checkExistingConnection(socket) && checkExistingHardwareConnection(_socket)) {
                System.out.println("Connection already exists !!");
                cmdBuilder.append("$- Router Connection Already Exist ..Try Again Later..\n");
                OsrpCmdUtils.getSharedCMDBuilder().append("$- Router Connection Already Exist ..Try Again Later..\n");


            } else {
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");

                cmdBuilder.append("$- A New Connection Has Been Found....\n");
                cmdBuilder.append("$- Adding The New Router Entry To The List And Table...\n");


                OsrpCmdUtils.getSharedCMDBuilder().append("$- A New Connection Has Been Found....\n");
                OsrpCmdUtils.getSharedCMDBuilder().append("$- Adding The New Router Entry To The List And Table...\n");

                connectionHistory.add(socket);
                hardwareList.add(_socket);
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")), socket.getInetAddress(), InetAddress.getByName("0.0.0.0"), 0,1);
                routerTable.displayTable();

                routingTableBuilder.append(routerTable.getTableBuilder());
                OsrpCmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());

                //   buildCMD(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    private boolean checkExistingConnection(Socket router) {
        for (Socket socket : connectionHistory) {
            if (socket.getInetAddress().equals(router.getInetAddress())) {
                return true;
            }
        }
        return false;
    }


    public OsrpTable getRouterTable(){
        return routerTable;
    }

    public List<Socket> getConnectionHistory() {
        return connectionHistory;
    }

    public List<Socket> getHardwareList() {
        return hardwareList;
    }

    /*public Table getTable() {
            return network_v2.Router.routerTable;
        }
        */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ServerSocket getHostServerSocket() {
        return hostServerSocket;
    }

    public ServerSocket getPacketSererSocket() {
        return packetSererSocket;
    }
    public ServerSocket getPollerServerSocket() {
    	return pollerServerSocket;
    }

}
