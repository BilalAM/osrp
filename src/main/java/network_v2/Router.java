package network_v2;

import gui.utils.AssortedUtils;
import gui.utils.GUIUtils;
import network_packet_algorithms.PacketForwarderUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import gui.utils.CmdUtils;


/**
 *
 *
 *   IN THE ROUTER PCS , I HAVE REMOVED THE STREAM CHECK CONDITION INSIDE receiveAndForwardThePacket()
 *   IT IS NOT CAUSING THE no packet found from stream ERROR..
 *
 *
 */


public class Router {


    private static ServerSocket serverSocket;
    private static ServerSocket hostServerSocket;
    private static ServerSocket packetSererSocket;
    private static List<Socket> connectionHistory = new ArrayList<>();
    private static Table routerTable;
    private static StringBuilder cmdBuilder;
    private static StringBuilder routingTableBuilder;
    private static StringBuilder hostBuilder;

    /**
     * bind 'this' router to listen for other 'routers' on
     * port 2000 and this 'router' to listen for 'hosts' on port 2001
     */
    static{
        try {
            hostServerSocket = new ServerSocket(2001);
            serverSocket = new ServerSocket(2000);
            packetSererSocket = new ServerSocket(2002,100,InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Router(){
        routerTable = new Table();
        cmdBuilder = new StringBuilder();
        routingTableBuilder = new StringBuilder();
        hostBuilder = new StringBuilder();
    }
    public void acceptConnections(){
        try{
            Socket otherRouter = serverSocket.accept();

            if(checkExistingConnection(otherRouter)){
                System.out.println("connection already exists !!");
                cmdBuilder.append("$- Router Connection Already Exist ..Try Again Later..\n");
                CmdUtils.getSharedCMDBuilder().append("$- Router Connection Already Exist ..Try Again Later..\n");
            }else{
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");
                cmdBuilder.append("$- A New Connection Has Been Found....\n");
                cmdBuilder.append("$- Adding The New Router Entry To The List And Table...\n");

                CmdUtils.getSharedCMDBuilder().append("$- A New Connection Has Been Found....\n");
                CmdUtils.getSharedCMDBuilder().append("$- Adding The New Router Entry To The List And Table...\n");

                connectionHistory.add(otherRouter);
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")), otherRouter.getInetAddress(),InetAddress.getByName("0.0.0.0"),1);
                routerTable.displayTable();

              //  routingTableBuilder.append(routerTable.getTableBuilder());

                CmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());

                //  buildCMD(otherRouter);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // a type of client...
    public void requestConnection(String ip) {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), 2000);
            if (checkExistingConnection(socket)) {
                System.out.println("Connection already exists !!");
                cmdBuilder.append("$- Router Connection Already Exist ..Try Again Later..\n");
                CmdUtils.getSharedCMDBuilder().append("$- Router Connection Already Exist ..Try Again Later..\n");


            } else {
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");

                cmdBuilder.append("$- A New Connection Has Been Found....\n");
                cmdBuilder.append("$- Adding The New Router Entry To The List And Table...\n");


                CmdUtils.getSharedCMDBuilder().append("$- A New Connection Has Been Found....\n");
                CmdUtils.getSharedCMDBuilder().append("$- Adding The New Router Entry To The List And Table...\n");

                connectionHistory.add(socket);
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")), socket.getInetAddress(), InetAddress.getByName("0.0.0.0"), 1);
                routerTable.displayTable();

                routingTableBuilder.append(routerTable.getTableBuilder());
                CmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());

                //   buildCMD(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private boolean checkExistingConnection(Socket router) {
        for (Socket socket : connectionHistory) {
            if (socket.getInetAddress().equals(router.getInetAddress())) {
                return true;
            }
        }
        return false;
    }






    public List<Socket> getConnectionHistory() {
        return connectionHistory;
    }

    public Table getTable(){
        return Router.routerTable;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ServerSocket getHostServerSocket() {
        return hostServerSocket;
    }

    public ServerSocket getPacketSererSocket() {
        return packetSererSocket;
    }

}

