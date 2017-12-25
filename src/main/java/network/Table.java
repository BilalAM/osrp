package network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Table {
    InetAddress source;
    InetAddress destination;
    InetAddress next;
    int cost;
    StringBuilder builder;
    List<InetAddress> destinations = new ArrayList<>();
    List<InetAddress> nexts = new ArrayList<>();
    List<Integer> costs = new ArrayList<>();

    List<Entry> entries = new ArrayList<>();

    public Table(InetAddress source) {
        this.source = source;
    }

    public static void main(String args[]) throws Exception {

    }

    public void addNewEntry(InetAddress destination, InetAddress next, int cost) {
        this.destination = destination;
        this.next = next;
        this.cost = cost;
        this.destinations.add(destination);
        this.nexts.add(next);
        this.costs.add(cost);

        entries.add(new Entry(destination, next, cost));
    }

    public String displayTable() {
        builder = new StringBuilder();
        System.out.println("\n");
        builder.append("\n");
        builder.append("==============================================");
        builder.append("\n");
        builder.append("  DESTINATION \t          NEXT \t \t    COST ");
        builder.append("\n");
        builder.append("==============================================");
        builder.append("\n");
        System.out.println("==============================================");
        System.out.println("  DESTINATION \t          NEXT \t \t    COST ");
        System.out.println("==============================================");
        for (Entry entry : entries) {
            builder.append(entry.destination + "    " + entry.next + "    " + entry.cost);
            builder.append("\n");
            System.out.println(entry.destination + "    " + entry.next + "    " + entry.cost);
        }
        System.out.println("\n");
        return builder.toString();
    }

    public class Entry {
        InetAddress destination;
        InetAddress next;
        int cost;

        public Entry(InetAddress destination, InetAddress next, int cost) {
            this.destination = destination;
            this.next = next;
            this.cost = cost;
        }
    }

}
