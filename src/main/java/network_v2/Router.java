package network_v2;

import helpers.IPsHelper;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Router {

    private static ServerSocket serverSocket;
    private static List<Socket> connectionHistory = new ArrayList<>();
    private static Table routerTable;
    private static HashMap<Socket,Socket> socketHashMap;


    static{
        try {
            serverSocket = new ServerSocket(2000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Router(){
        routerTable = new Table();
        socketHashMap = new HashMap<>();
    }
    public void acceptConnections(){
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        try{
            Socket otherRouter = serverSocket.accept();

            if(checkExistingConnection(otherRouter)){
                System.out.println("connection already exists !!");
            }else{
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");
                connectionHistory.add(otherRouter);
                routerTable.addNewEntry(InetAddress.getByName(IPsHelper.getPrivateIp("wlo1")), otherRouter.getInetAddress(),InetAddress.getByName("0.0.0.0"),1);
                routerTable.displayTable();


                //add it in hashmap
                //socketHashMap.put(otherRouter,)




            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    // a type of client...
    public void requestConnection(String ip){
        ObjectInputStream inputStream;
        try{
            Socket socket = new Socket(InetAddress.getByName(ip),2000);
            if(checkExistingConnection(socket)){
                System.out.println("Connection already exists !!");
            }else{
                System.out.println("A new connection has been found...");
                System.out.println("Adding the new socket to the history list of 'this' router ");
                connectionHistory.add(socket);
                routerTable.addNewEntry(InetAddress.getByName(IPsHelper.getPrivateIp("wlo1")), socket.getInetAddress(),InetAddress.getByName("0.0.0.0"),1);
                routerTable.displayTable();


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


    public List<Socket> getConnectionHistory(){
        return connectionHistory;
    }

    public Table getTable(){
        return Router.routerTable;
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public HashMap<Socket , Socket> getSocketHashMap(){
        return socketHashMap;
    }


}
