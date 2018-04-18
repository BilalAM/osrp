package network_v2;

import gui.utils.AssortedUtils;
import gui.utils.GUIUtils;
import gui.utils.PacketForwarderUtils;


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
            packetSererSocket = new ServerSocket(2002,100,InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")));
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
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")), otherRouter.getInetAddress(),InetAddress.getByName("0.0.0.0"),1);
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
                routerTable.addNewEntry(InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")), socket.getInetAddress(), InetAddress.getByName("0.0.0.0"), 1);
                routerTable.displayTable();

                routingTableBuilder.append(routerTable.getTableBuilder());
                CmdUtils.getSharedRoutingCMDBuilder().append(routerTable.getTableBuilder());

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

            cmdBuilder.append("$- A Router Is Connected To A Host !! \n");
            hostBuilder.append("$- A Host Is Connected To A Router !! \n " );

            CmdUtils.getSharedCMDBuilder().append("$- A Router Is Connected To A Host !! \n");


            if(host.getInputStream().available() > 0) {
                ObjectInputStream inputFromHost = new ObjectInputStream(host.getInputStream());
                Packet packet = (Packet) inputFromHost.readObject();
                System.out.println("PACKET IS INITIALLY RECIEVED BY ROUTER !");
                System.out.println(packet.toString());
                System.out.println("THE SHORTEST COST ENTRY FOR THIS PACKET TO BE FORWARDED TOO...");

                Table.Entry entry = PacketForwarderUtils.getShortestFirstEntry(routerTable,packet);

                cmdBuilder.append("$- Router Has Received A Packet By Host.. \n");
                cmdBuilder.append(packet.toString()) ;


                CmdUtils.getSharedCMDBuilder().append("$- Router Has Received A Packet By Host.. \n");
                CmdUtils.getSharedCMDBuilder().append(packet.toString()) ;



                if(entry == null){
                    System.out.println("Destination reached or entry is null");
                    cmdBuilder.append("$- Destination Has Been Reached ! Entry Is Null Now ! \n");
                   CmdUtils.getSharedCMDBuilder().append("$- Destination Has Been Reached ! Entry Is Null Now ! \n");

                }
                else if(AssortedUtils.isDirectEntry(entry)){
                    entry.next = entry.destination;
                    cmdBuilder.append("$- The Shortest Cost Entry For This Packet To Be Forwarded To Is Found To Be : \n");
                    cmdBuilder.append("$- " + entry.toString() + "\n");
                    cmdBuilder.append("$- It Is A Direct Entry.. Changing The Next To Destination IP..\n");

                    CmdUtils.getSharedCMDBuilder().append("$- The Shortest Cost Entry For This Packet To Be Forwarded To Is Found To Be : \n");
                    CmdUtils.getSharedCMDBuilder().append("$- " + entry.toString() + "\n");
                    CmdUtils.getSharedCMDBuilder().append("$- It Is A Direct Entry.. Changing The Next To Destination IP..\n");


                    helper(host,entry,builder,packet);
                }
                else{
                    cmdBuilder.append("$- The Shortest Cost Entry For This Packet To Be Forwarded To Is Found To Be : \n");
                    cmdBuilder.append("$- " + entry.toString() + "\n");

                    CmdUtils.getSharedCMDBuilder().append("$- The Shortest Cost Entry For This Packet To Be Forwarded To Is Found To Be : \n");
                    CmdUtils.getSharedCMDBuilder().append("$- " + entry.toString() + "\n");

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
            cmdBuilder.append("$- Connection Received For Packet..\n");

            CmdUtils.getSharedCMDBuilder().append("$- Connection Received For Packet..\n");


            // System.out.println("ACCEPTED THE PACKET !! ");
            builder.append("ACCEPTED THE PACKET SOCKET !! \n");
                cmdBuilder.append("$- Attempting To Receive The Packet .. \n");
               CmdUtils.getSharedCMDBuilder().append("$- Attempting To Receive The Packet .. \n");

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Packet packet = (Packet) input.readObject();
                builder.append("GOT A PACKET .. PRINTING IT... \n");
                builder.append(packet.toString() + "\n");
                builder.append("EXTRACTING SHORTEST ENTRY FROM PACKET BY CHECKING THE TABLE...\n");

                cmdBuilder.append("$- Packet Received...Trying To Extract The Shortest Entry From Packet And Checking Our Table For Shortest Entry...\n ");

                CmdUtils.getSharedCMDBuilder().append("$- Packet Received...Trying To Extract The Shortest Entry From Packet And Checking Our Table For Shortest Entry...\n ");


                Table.Entry entry = PacketForwarderUtils.getShortestFirstEntry(routerTable, packet);
                cmdBuilder.append("$- Entry Extracted...Checking further..\n");
                CmdUtils.getSharedCMDBuilder().append("$- Entry Extracted...Checking further..\n");

                if (entry == null) {
                    cmdBuilder.append("$- ****  DESTINATION HAS BEEN REACHED ! OR A NULL ENTRY IS HERE.. GOING BACK TO LISTENING ***\n");
                    CmdUtils.getSharedCMDBuilder().append("$- ****  DESTINATION HAS BEEN REACHED ! OR A NULL ENTRY IS HERE.. GOING BACK TO LISTENING ***\n");

                    builder.append("DESTINATION REACHED OR NULL IS HERE..");
                } else if (AssortedUtils.isDirectEntry(entry)) {
                    cmdBuilder.append("$- It Is A Direct Entry.. Changing The Next To Destination IP..\n");
                   CmdUtils.getSharedCMDBuilder().append("$- It Is A Direct Entry.. Changing The Next To Destination IP..\n");

                    entry.next = entry.destination;
                    helper(socket, entry, builder, packet);
                } else {
                    cmdBuilder.append("$- It Is A Indirect Entry...Forwarding The Packet...");
                    CmdUtils.getSharedCMDBuilder().append("$- It Is A Indirect Entry...Forwarding The Packet...");

                    helper(socket, entry, builder, packet);
                }

            System.out.println(builder.toString());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private void helper(Socket socket, Table.Entry entry, StringBuilder builder, Packet packet) throws Exception {
        builder.append("ENTRY OF SHORTEST IS + " + entry.toString() + "\n");

        cmdBuilder.append("$- Attempting To Open A New Connection To " + entry.next + "\n");
        CmdUtils.getSharedCMDBuilder().append("$- Attempting To Open A New Connection To " + entry.next + "\n");

        Socket _socket = new Socket(entry.next, 2002);
        cmdBuilder.append("$- Connection Success !! \n");
        cmdBuilder.append("$- Forwarding The Packet To This New Router..\n");

        CmdUtils.getSharedCMDBuilder().append("$- Connection Success !! \n");
        CmdUtils.getSharedCMDBuilder().append("$- Forwarding The Packet To This New Router..\n");

        builder.append("FORWARDING PACKET TO " + _socket.getInetAddress() + "\n");

        ObjectOutputStream outputStream = new ObjectOutputStream(_socket.getOutputStream());
        outputStream.writeObject(packet);
        _socket.close();
        socket.close();
        cmdBuilder.append("$- The Packet Has Been Sent... \n");
        CmdUtils.getSharedCMDBuilder().append("$- The Packet Has Been Sent... \n");

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

    public String getRoutingTableCMD(){
        return routingTableBuilder.toString();
    }

    public String getHostBuilder(){
        return hostBuilder.toString();
    }
}

