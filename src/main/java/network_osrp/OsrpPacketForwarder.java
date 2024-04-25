package network_osrp;

import gui.utils.AssortedUtils;
import gui.utils.GUIUtils;
import network_packet_algorithms.OsrpPacketForwarderUtils;
import network_v2.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OsrpPacketForwarder {



    public boolean acceptHostConnection(OsrpRouter router){
        boolean check = true;
        try{
            Socket _socket = router.getPacketSererSocket().accept();
            if(_socket.getInputStream().available() > 0){
                check = true;
                ObjectInputStream inputStream = new ObjectInputStream(_socket.getInputStream());
                Packet packet = (Packet)inputStream.readObject();
                System.out.println("PACKET IS INITIALLY RECIEVED BY ROUTER !");
                System.out.println(packet.toString());
                System.out.println("THE SHORTEST COST ENTRY FOR THIS PACKET TO BE FORWARDED TOO...");
                // get that entry
                OsrpTable.Entry entry = OsrpPacketForwarderUtils.getShortestRankEntry(router.getRouterTable(),packet);
                // helper to send object via entry
                if(entry == null){
                    System.out.println("Destination reached or entry is null");
                }
                else if(AssortedUtils.isOSRPDirectEntry(entry)){
                    entry.next = entry.destination;
                    System.out.println(entry.toString());
                    send(_socket,packet,entry);
                }
                else {
                    System.out.println(entry.toString());
                    send(_socket, packet, entry);
                }
            }else{
                check = false;
                System.out.println("not gotten any packet :( ");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return check;
    }


    /**
     *@param packetToSend : the packet to send , packet is made in gui.OsrpController
     */
    public void requestHostConnection(Packet packetToSend){
        try{
            Socket _connection = new Socket(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a"),3002);
            ObjectOutputStream outputStream = new ObjectOutputStream(_connection.getOutputStream());
            outputStream.writeUnshared(packetToSend);
            outputStream.flush();
            outputStream.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean recieveAndForwardThePacket(OsrpRouter selfRouter){
    	boolean check = true;
        try{
        	check = true;
            Socket _socket = selfRouter.getPacketSererSocket().accept();

            ObjectInputStream inputStream = new ObjectInputStream(_socket.getInputStream());
            Packet packet = (Packet)inputStream.readObject();
            OsrpTable.Entry entry = OsrpPacketForwarderUtils.getShortestRankEntry(selfRouter.getRouterTable(),packet);
            if(entry == null){
                System.out.println("destination is reached or is null");
            }else if(AssortedUtils.isOSRPDirectEntry(entry)){
                entry.next = entry.destination;
                send(_socket,packet,entry);
            }else{
                send(_socket,packet,entry);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return check;
    }


    private void send(Socket socket, Packet packet, OsrpTable.Entry entry) throws Exception{
        System.out.println(entry.toString());
        Socket _socket = new Socket(entry.next,3002);
        ObjectOutputStream outputStream = new ObjectOutputStream(_socket.getOutputStream());
        outputStream.writeUnshared(packet);
        _socket.close();
        socket.close();
    }

}
