package network_v2;

import gui.utils.AssortedUtils;
import gui.utils.GUIUtils;
import gui.utils.PacketForwarderUtils;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Router {


    private static ServerSocket serverSocket;
    private static ServerSocket hostServerSocket;
    private static ServerSocket packetSererSocket;
    private static List<Socket> connectionHistory = new ArrayList<>();
    private static Table routerTable;
    private StringBuilder cmdBuilder;

    /**
     * bind 'this' router to listen for other 'routers' on
     * port 2000 and this 'router' to listen for 'hosts' on port 2001
     */
    static{
        try {
            hostServerSocket = new ServerSocket(2001);
            serverSocket = new ServerSocket(2000);
            packetSererSocket = new ServerSocket(2002,100,InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Router(){
        routerTable = new Table();
        cmdBuilder = new StringBuilder();

    }
    public void acceptConnections(){
        try{
            Socket otherRouter = serverSocket.accept();

            if(checkExistingConnection(otherRouter)){
                System.out.println("connection already exists !!");
            }else{
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");
                connectionHistory.add(otherRouter);
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")), otherRouter.getInetAddress(),InetAddress.getByName("0.0.0.0"),1);
                routerTable.displayTable();
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
            } else {
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");
                connectionHistory.add(socket);
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")), socket.getInetAddress(), InetAddress.getByName("0.0.0.0"), 1);
                routerTable.displayTable();
             //   buildCMD(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * accept host connection and also recieve the packet
     *
     *    - THIS IS THE "INITIAL" STEP TAKEN BY THE ROUTER THAT IS "DIRECTLY" CONNECTED TO A HOST
     *
     *    - The Actual Send And Recieve Packets By Routers Are Implemented In Different Functions.
     *
     *    - THIS ONLY ACCEPTS AND FORWARDES THE PACKET RECIEVED INITIALLY BY THE HOST THAT IS DIRECTLY
     *    CONNECTED TO THIS ROUTER !
     *
     */
    public void acceptHostConnection() {
        StringBuilder builder = new StringBuilder("a");
        try {
            Socket host = hostServerSocket.accept();
            System.out.println("ROUTER IS CONNECTED TO A HOST");

            if(host.getInputStream().available() > 0) {
                ObjectInputStream inputFromHost = new ObjectInputStream(host.getInputStream());
                Packet packet = (Packet) inputFromHost.readObject();
                System.out.println("PACKET IS INITIALLY RECIEVED BY ROUTER !");
                System.out.println(packet.toString());
                System.out.println("THE SHORTEST COST ENTRY FOR THIS PACKET TO BE FORWARDED TOO...");
                Table.Entry entry = PacketForwarderUtils.getShortestFirstEntry(routerTable,packet);
                if(entry == null){
                    System.out.println("Destination reached or entry is null");
                }
                else if(AssortedUtils.isDirectEntry(entry)){
                    entry.next = entry.destination;
                    helper(host,entry,builder,packet);
                }
                else{
                    helper(host,entry,builder,packet);
                }
            }else{
                System.out.println("host has not sent any packet as of yet , but thats impossbile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestHostConnection(Packet packetToSend) {

        try {
            Socket socket = new Socket(GUIUtils.getPrivateIp("wlo1"), 2001);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(packetToSend);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /***********************************************************************
     *
     *
     *
     *                       ENTER AT YOUR OWN RISK !
     *
     *                           MAGIC AT WORK OVER HERE !
     *
     *
     *
     *     Algorithm works as follows :
     *
     *    --  This router is constantly listening for Sockets on port 2002
     *    --  Port 2002 is for packets forwarding only
     *    --  When it accepts the connection , it immediatly receives the inputStream and creates the packet object from the stream
     *    --  Then it checks that packets for the destination and whether the destination address is present in our table
     *    --  If it is present , it checks if it is directly connected or is a indirect connection.
     *    --  If it is a direct connection , then the entry.next is changed to the destination (Direct connections have entry 0.0.0.0
     *        as default ,thats why the entry.next is changed since address 0.0.0.0 is localhost) .
     *    --  Then after this it creates a socket on port 2002 with the IP Address received from the Packet object.
     *    --  Then it forwards that packet onwards..
     *
     *
     *
     ***********************************************************************/
    public void receiveAndForwardThePacket() {
        StringBuilder builder = new StringBuilder();

        try {

            //Thread.sleep(1000);
            Socket socket = packetSererSocket.accept();
            // System.out.println("ACCEPTED THE PACKET !! ");
            builder.append("ACCEPTED THE PACKET SOCKET !! \n");
            if (socket.getInputStream().available() > 0) {
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Packet packet = (Packet) input.readObject();
                builder.append("GOT A PACKET .. PRINTING IT... \n");
                builder.append(packet.toString() + "\n");
                builder.append("EXTRACTING SHORTEST ENTRY FROM PACKET BY CHECKING THE TABLE...\n");
                Table.Entry entry = PacketForwarderUtils.getShortestFirstEntry(routerTable, packet);
                if (entry == null) {
                    builder.append("DESTINATION REACHED OR NULL IS HERE..");
                } else if (AssortedUtils.isDirectEntry(entry)) {
                    entry.next = entry.destination;
                    helper(socket, entry, builder, packet);
                } else {
                    helper(socket, entry, builder, packet);
                }
            } else {
                System.out.println("no packet is received...");
            }
            System.out.println(builder.toString());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private void helper(Socket socket, Table.Entry entry, StringBuilder builder, Packet packet) throws Exception {
        builder.append("ENTRY OF SHORTEST IS + " + entry.toString() + "\n");
        Socket _socket = new Socket(entry.next, 2002);
        builder.append("FORWARDING PACKET TO " + _socket.getInetAddress() + "\n");
        ObjectOutputStream outputStream = new ObjectOutputStream(_socket.getOutputStream());
        outputStream.writeObject(packet);
        _socket.close();
        socket.close();
    }

    @Deprecated
     public void recieveAndforwardPacket(){
         StringBuilder builder = new StringBuilder();
         System.out.println("inside our packet method");
         try {
             Socket socket = packetSererSocket.accept();
             System.out.println("PACKETY SOCKET ACCEPTED");
             if(socket.getInputStream().available() > 0){
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                 //Object object = input.read();
                 //if(object instanceof Packet) {
                     Packet packet = (Packet)input.readObject();
                     System.out.println("a packet is recieved....forwarding it too..");


                     Table.Entry entry = PacketForwarderUtils.getShortestFirstEntry(routerTable, packet);


                     if(AssortedUtils.isDirectEntry(entry)){
                         entry.next = entry.destination;
                     }
                     System.out.println("the entry of shortest is : " + entry.toString());

                     // extract the destination from it and forward the packet to port to 2002 with next ip
                     socket = new Socket(entry.next, 2002);
                     ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                     System.out.println("sending packet to another router");
                     outputStream.writeObject(packet);
                     input.close();
                     outputStream.close();


             }
         }catch(Exception e){
             e.printStackTrace();
         }
     }





    /****************************************************************************
     *                             MAGIC ENDS HERE
     *
     ****************************************************************************/


    private boolean checkExistingConnection(Socket router) {
        for (Socket socket : connectionHistory) {
            if (socket.getInetAddress().equals(router.getInetAddress())) {
                return true;
            }
        }
        return false;
    }





   /* private void buildCMD(Socket socket){
        cmdBuilder.append(CMDUtils.greenPrompty() + CMDUtils.whiteTexty("A new connection has been found... \n"));
        cmdBuilder.append(CMDUtils.greenPrompty() +CMDUtils.whiteTexty("-- Adding the new router entry to history list... \n"));
        cmdBuilder.append(CMDUtils.greenPrompty() +CMDUtils.whiteTexty("-- Connection Details : \n"));
        cmdBuilder.append(CMDUtils.greenPrompty() +CMDUtils.whiteTexty("----- ROUTER IP          :  " + socket.getInetAddress() + "\n"));
        cmdBuilder.append(CMDUtils.greenPrompty() +CMDUtils.whiteTexty("----- PORT               :  " + socket.getPort() + "\n"));
        cmdBuilder.append(CMDUtils.greenPrompty() +CMDUtils.whiteTexty("----- TIME OF CONNECTION :  " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+"\n"));
        cmdBuilder.append(CMDUtils.greenPrompty() +CMDUtils.whiteTexty("-- Entry added into table... \n"));
    }*/

    public List<Socket> getConnectionHistory() {
        return connectionHistory;
    }

    public Table getTable(){
        return Router.routerTable;
    }

    public String getCMD(){
        return cmdBuilder.toString();
    }


}

