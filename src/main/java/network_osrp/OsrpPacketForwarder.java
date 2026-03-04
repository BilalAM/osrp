package network_osrp;

import network_core.NetworkConfig;
import network_core.Packet;
import network_core.PacketRoutingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OsrpPacketForwarder {
    private static final Logger logger = LoggerFactory.getLogger(OsrpPacketForwarder.class);



    public boolean acceptHostConnection(OsrpRouter router){
        boolean check = true;
        try{
            Socket _socket = router.getPacketServerSocket().accept();
            if(_socket.getInputStream().available() > 0){
                check = true;
                ObjectInputStream inputStream = new ObjectInputStream(_socket.getInputStream());
                Packet packet = (Packet)inputStream.readObject();
                logger.debug("Packet is initially received by router");
                logger.debug("Packet: {}", packet);
                logger.debug("Finding the shortest cost entry for this packet to be forwarded to");
                // get that entry
                OsrpTable.Entry entry = PacketRoutingUtils.getShortestEntry(router.getRouterTable(), packet, java.util.Comparator.comparingInt(e -> e.NEXT_RANK));
                // helper to send object via entry
                if(entry == null){
                    logger.debug("Destination reached or entry is null");
                }
                else if(entry.isDirectEntry()){
                    entry.next = entry.destination;
                    logger.debug("Direct entry: {}", entry);
                    send(_socket,packet,entry);
                }
                else {
                    logger.debug("Entry: {}", entry);
                    send(_socket, packet, entry);
                }
            }else{
                check = false;
                logger.debug("No packet received yet");
            }
        }catch(Exception e){
            logger.error("Error accepting host connection for OSRP packet", e);
        }
        return check;
    }


    /**
     *@param packetToSend : the packet to send , packet is made in gui.OsrpController
     */
    public void requestHostConnection(Packet packetToSend){
        try{
            Socket _connection = new Socket(NetworkConfig.getPrivateIp(), NetworkConfig.OSRP_PACKET_PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(_connection.getOutputStream());
            outputStream.writeUnshared(packetToSend);
            outputStream.flush();
            outputStream.close();

        }catch(Exception e){
            logger.error("Error requesting host connection for OSRP packet", e);
        }
    }

    public boolean receiveAndForwardThePacket(OsrpRouter selfRouter){
    	boolean check = true;
        try{
        	check = true;
            Socket _socket = selfRouter.getPacketServerSocket().accept();

            ObjectInputStream inputStream = new ObjectInputStream(_socket.getInputStream());
            Packet packet = (Packet)inputStream.readObject();
            OsrpTable.Entry entry = PacketRoutingUtils.getShortestEntry(selfRouter.getRouterTable(), packet, java.util.Comparator.comparingInt(e -> e.NEXT_RANK));
            if(entry == null){
                logger.debug("Destination is reached or entry is null");
            }else if(entry.isDirectEntry()){
                entry.next = entry.destination;
                send(_socket,packet,entry);
            }else{
                send(_socket,packet,entry);
            }

        }catch (Exception e){
            logger.error("Error receiving and forwarding OSRP packet", e);
        }
        return check;
    }


    private void send(Socket socket, Packet packet, OsrpTable.Entry entry) throws Exception{
        logger.debug("Forwarding packet via entry: {}", entry);
        Socket _socket = new Socket(entry.next, NetworkConfig.OSRP_PACKET_PORT);
        ObjectOutputStream outputStream = new ObjectOutputStream(_socket.getOutputStream());
        outputStream.writeUnshared(packet);
        _socket.close();
        socket.close();
    }

}
