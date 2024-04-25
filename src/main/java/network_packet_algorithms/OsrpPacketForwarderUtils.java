package network_packet_algorithms;

import network_osrp.OsrpTable;
import network_v2.Packet;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OsrpPacketForwarderUtils {

    private static InetAddress extractDestination(Packet packet){
        return packet.getDestinationAddress();
    }

    private static List<OsrpTable.Entry> getValidEntries(OsrpTable table , Packet packet){
        List<OsrpTable.Entry> innerList = new ArrayList<>();
        for(OsrpTable.Entry entry : table.getEntries()){
            if(isDestinationReached(entry,packet)){
                continue;
            }
            if(entry.destination.equals(extractDestination(packet))){
                innerList.add(entry);
            }

        }

        innerList.sort(Comparator.comparingInt(e -> e.NEXT_RANK));
        return innerList;

    }
    public static OsrpTable.Entry getShortestRankEntry(OsrpTable table , Packet packet) {
        List<OsrpTable.Entry> entries = getValidEntries(table, packet);
        if (entries.isEmpty()) {
            return null;
        } else {
            return entries.get(0).equals(null) ? null : getValidEntries(table, packet).get(0);
        }
    }
    private static  boolean isDestinationReached(OsrpTable.Entry entry , Packet packet){
        return entry.source.equals(packet.getDestinationAddress());
    }

}
