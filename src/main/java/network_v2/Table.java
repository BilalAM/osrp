package network_v2;

import network_core.AbstractEntry;
import network_core.AbstractRoutingTable;

import java.net.InetAddress;

public class Table extends AbstractRoutingTable<Table.Entry> {
    private static final long serialVersionUID = -985473150010261626L;

    public void addNewEntry(InetAddress source, InetAddress destination, InetAddress next, int cost) {
        this.source = source;
        this.destination = destination;
        this.next = next;
        this.cost = cost;
        entries.add(new Entry(source, destination, next, cost));
    }

    @Override
    protected String getTableHeader() {
        return "=====================================================\n"
             + "SOURCE \t\t  DESTINATION \t\t          NEXT \t \t    COST \n"
             + "=====================================================\n";
    }

    @Override
    protected String formatEntry(Entry entry) {
        return entry.source + "     " + entry.destination + "\t\t\t" + entry.next + "\t\t\t" + entry.cost;
    }

    public static class Entry extends AbstractEntry {
        private static final long serialVersionUID = 1L;

        public Entry(InetAddress source, InetAddress destination, InetAddress next, int cost) {
            super(source, destination, next, cost);
        }

        @Override
        public String toString() {
            return "[SOURCE : " + source + "]  DESTINATION : " + destination + "]  NEXT : " + next + "]  COST : " + cost + "]";
        }
    }
}
