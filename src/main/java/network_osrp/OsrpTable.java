package network_osrp;

import network_core.AbstractEntry;
import network_core.AbstractRoutingTable;

import java.net.InetAddress;

public class OsrpTable extends AbstractRoutingTable<OsrpTable.Entry> {
    private static final long serialVersionUID = -985473150010261627L;
    public int NEXT_RANK;

    public void addNewEntry(InetAddress source, InetAddress destination, InetAddress next, int NEXT_RANK, int cost) {
        this.source = source;
        this.destination = destination;
        this.next = next;
        this.cost = cost;
        this.NEXT_RANK = NEXT_RANK;
        entries.add(new Entry(source, destination, next, NEXT_RANK, cost));
    }

    @Override
    protected String getTableHeader() {
        return "==============================================================================================\n"
             + "SOURCE \t\t\t DESTINATION \t\t\t NEXT \t\t\t NEXT NODE RANK \t\t\t COST \n"
             + "==============================================================================================\n";
    }

    @Override
    protected String formatEntry(Entry entry) {
        return entry.source + "\t\t\t" + entry.destination + "\t\t\t" + entry.next + "\t\t\t\t"
                + entry.NEXT_RANK + "\t\t\t\t   " + entry.cost;
    }

    public static class Entry extends AbstractEntry {
        private static final long serialVersionUID = 1L;
        public int NEXT_RANK;

        public Entry(InetAddress source, InetAddress destination, InetAddress next, int NEXT_RANK, int cost) {
            super(source, destination, next, cost);
            this.NEXT_RANK = NEXT_RANK;
        }

        @Override
        public String toString() {
            return "[SOURCE : " + source + "]  DESTINATION : " + destination + "]  NEXT : " + next
                    + "]  NEXT_NODE_RANK : " + NEXT_RANK + "]  COST : " + cost + "]";
        }
    }
}
