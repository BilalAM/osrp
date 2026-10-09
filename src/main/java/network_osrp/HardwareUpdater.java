package network_osrp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class HardwareUpdater {
    private static final Logger logger = LoggerFactory.getLogger(HardwareUpdater.class);

    public void updateTable(HardwarePollerPacket receivedPoller, OsrpTable toUpdate) throws Exception{
        for (OsrpTable.Entry entry : toUpdate.getEntries()) {
            if(isDirectEntry(entry , receivedPoller)){
                entry.NEXT_RANK = HardwareRanks.getRank(receivedPoller);
                logger.debug("Updated direct entry: {}", entry);
                continue;
            }else if(isIndirectEntry(entry,receivedPoller)){
                logger.debug("Updated indirect entry: {}", entry);
                entry.NEXT_RANK = HardwareRanks.getRank(receivedPoller);
                continue;
            }else{
                logger.debug("No changes due to the hardware packet detected");
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
