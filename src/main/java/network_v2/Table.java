package network_v2;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table implements Serializable {

    private static final long serialVersionUID = -985473150010261626L;
    InetAddress destination;
    InetAddress next;
    InetAddress source;
    int cost;




    private List<Entry> entries = new ArrayList<>();
    StringBuilder builder;

    public void addNewEntry(InetAddress source , InetAddress destination , InetAddress next , int cost){
        this.source = source;
        this.destination = destination;
        this.next = next;
        this.cost = cost;
        entries.add(new Entry(source,destination,next,cost));
    }

    /**
     * Sometimes the 'connect to' button runs two times (due to thread-y stuff) , so in order to remove duplicate values i did stream().distinct() values..
     *
     */
    public void displayTable(){
        builder = new StringBuilder();
        System.out.println("\n");
        builder.append("\n");
        builder.append("==============================================");
        builder.append("\n");
        builder.append("SOURCE \t\t  DESTINATION \t\t          NEXT \t \t    COST ");
        builder.append("\n");
        builder.append("==============================================");
        builder.append("\n");
        System.out.println("==============================================");
        System.out.println("SOURCE \t\t DESTINATION \t\t          NEXT \t \t    COST ");
        System.out.println("==============================================");
        for (Entry entry : entries.stream().distinct().collect(Collectors.toList())) {
            builder.append(entry.source + "     " + entry.destination + "    " + entry.next + "    " + entry.cost);
            builder.append("\n");
            System.out.println(entry.source + "                " + entry.destination + "              " + entry.next + "               " + entry.cost);
        }
        System.out.println("\n");
    }
    public List<Entry> getEntries() {
        return entries;
    }
    public static class Entry implements Serializable {


        InetAddress destination;
        InetAddress next;
        InetAddress source;
        int cost;

        public Entry(InetAddress source , InetAddress destination, InetAddress next, int cost) {
            this.source = source;
            this.destination = destination;
            this.next = next;
            this.cost = cost;
        }


    }


}
