package network_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    private static final Logger logger = LoggerFactory.getLogger(Host.class);

    Socket hostSocket;
    private static StringBuilder hostCmD = new StringBuilder();
    private static ServerSocket hostServer;

    static {
        try {
          //  hostServer = new ServerSocket(2001);
        }catch(Exception e){
            logger.error("Failed to initialize host server socket", e);
        }
    }

    public void connectToRouter(String ip){
        try {
            hostSocket = new Socket(InetAddress.getByName(ip), 2001);
            hostCmD.append("HOST CONNECTED TO ROUTER\n");
            hostCmD.append("ROUTER DETAILS...\n");
            hostCmD.append("");


        }catch(Exception e){
            logger.error("Failed to connect host to router", e);
        }
    }


    public void sendPacket(Packet packet){
        try {
            ObjectOutputStream output = new ObjectOutputStream(hostSocket.getOutputStream());
            output.writeObject(packet);
            logger.debug("A packet has been sent by the host to the router");
        }catch(Exception e){
            logger.error("Failed to send packet from host", e);
        }


    }

    public String getCmd(){
        return hostCmD.toString();
    }

    public void receivePacket(){

    }



}
