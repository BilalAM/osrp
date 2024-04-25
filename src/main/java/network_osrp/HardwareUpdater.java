package network_osrp;


import java.net.InetAddress;

public class HardwareUpdater {

    public void updateTable(HardwarePollerPacket recievedPoller, OsrpTable toUpdate) throws Exception{
        for (OsrpTable.Entry entry : toUpdate.getEntries()) {
            if(isDirectEntry(entry , recievedPoller)){
                entry.NEXT_RANK = HardwareRanks.getRank(recievedPoller);
                System.out.println("updated entry --> " + entry.toString());
                continue;
            }else if(isIndirectEntry(entry,recievedPoller)){
                System.out.println("updated entry --> " + entry.toString());
                entry.NEXT_RANK = HardwareRanks.getRank(recievedPoller);
                continue;
            }else{
                System.out.println("no changes due to the hardware packet detected...");
                continue;
            }
        }
    }


    private boolean isDirectEntry(OsrpTable.Entry entry , HardwarePollerPacket packet) throws Exception{
        return entry.next.equals(InetAddress.getByName("0.0.0.0")) && entry.destination.equals(packet.getFromAddress());
    }

    private boolean isIndirectEntry(OsrpTable.Entry entry , HardwarePollerPacket packet){
        return entry.next.equals(packet.getFromAddress()) && (!(entry.destination.equals(packet.getFromAddress())));
    }
}
