package network_v2;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {

    Socket hostSocket;
    private static StringBuilder hostCmD = new StringBuilder();
    private static ServerSocket hostServer;

    static {
        try {
          //  hostServer = new ServerSocket(2001);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void connectToRouter(String ip){
        try {
            hostSocket = new Socket(InetAddress.getByName(ip), 2001);
            hostCmD.append("HOST CONNECTED TO ROUTER\n");
            hostCmD.append("ROUTER DETAILS...\n");
            hostCmD.append("");


        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void sendPacket(Packet packet){
        try {
            ObjectOutputStream output = new ObjectOutputStream(hostSocket.getOutputStream());
            output.writeObject(packet);
            System.out.println("a packet has been sent by the host to the router");
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public String getCmd(){
        return hostCmD.toString();
    }

    public void recievePacket(){

    }



}
