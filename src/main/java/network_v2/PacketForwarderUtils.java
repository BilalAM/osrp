package network_v2;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PacketForwarderUtils {

   static Table table;
    static Packet packet;

    private static InetAddress extractDestination(Packet packet){
        return packet.getDestinationAddress();
    }

    private static InetAddress extractSource(Packet packet){
        return packet.getSourceAddress();
    }

    private static List<Table.Entry> getMatchingEntries(Table table , Packet packet){

        List<Table.Entry> innerList = new ArrayList<>();
        for(Table.Entry e : table.getEntries()){
            if(e.destination.equals(extractDestination(packet))){
                innerList.add(e);
            }
        }
        return innerList;
    }

    public static Table.Entry getShortestFirstEntry(Table table ,  Packet packet){
        return getMatchingEntries(table,packet).stream().min(Comparator.comparingInt(entry -> entry.cost)).orElse(null);
    }



}
