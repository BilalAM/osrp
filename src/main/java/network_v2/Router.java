package network_v2;

import gui.utils.GUIUtils;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Router {


    private static ServerSocket serverSocket;
    private static ServerSocket hostServerSocket;
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
     */
    public void acceptHostConnection() {
        try {
            Socket host = hostServerSocket.accept();
            System.out.println("ROUTER IS CONNECTED TO A HOST");

            if(host.getInputStream().available() > 0) {
                ObjectInputStream inputFromHost = new ObjectInputStream(host.getInputStream());
                Packet packet = (Packet) inputFromHost.readObject();
                System.out.println("PACKET IS RECIEVED BY ROUTER !");
                System.out.println(packet.toString());
            }else{
                System.out.println("host has not sent any packet as of yet , but thats impossbile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestHostConnection(Packet packetToSend){

        try{
            for(int i = 0 ; i < 1 ; i++) {
                Socket socket = new Socket(GUIUtils.getPrivateIp("wlo1"),2001);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                outputStream.writeObject(packetToSend);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkExistingConnection(Socket router){
        for(Socket socket : connectionHistory){
            if(socket.getInetAddress().equals(router.getInetAddress())){
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

