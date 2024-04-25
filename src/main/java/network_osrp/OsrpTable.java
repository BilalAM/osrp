package network_osrp;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OsrpTable implements Serializable {
    private static final long serialVersionUID = -985473150010261627L;
    public InetAddress destination;
    public InetAddress next;
    public InetAddress source;
    public  int cost;
    public int NEXT_RANK;




    private List<OsrpTable.Entry> entries = new ArrayList<>();
    private StringBuilder builder;

    public void addNewEntry(InetAddress source , InetAddress destination , InetAddress next , int NEXT_RANK , int cost){
        this.source = source;
        this.destination = destination;
        this.next = next;
        this.cost = cost;
        this.NEXT_RANK = NEXT_RANK;
        entries.add(new OsrpTable.Entry(source,destination,next,NEXT_RANK,cost));
    }

    public String getTableBuilder(){
        return builder.toString();
    }
    /**
     * Sometimes the 'connect to' button runs two times (due to thread-y stuff) , so in order to remove duplicate values i did stream().distinct() values..
     *
     */
    public void displayTable(){
        builder = new StringBuilder();
        System.out.println("\n");
        builder.append("\n");
        builder.append("==============================================================================================\"");
        builder.append("\n");
        builder.append("SOURCE \t\t\t DESTINATION \t\t\t NEXT \t\t\t NEXT NODE RANK \t\t\t COST ");
        builder.append("\n");
        builder.append("==============================================================================================\"");
        builder.append("\n");
        System.out.println("=====================================================================================================");
        System.out.println("SOURCE \t\t\tDESTINATION \t\t        NEXT \t \t NEXT NODE RANK     \tCOST ");
        System.out.println("=====================================================================================================");
        for (OsrpTable.Entry entry : entries.stream().distinct().collect(Collectors.toList())) {
            builder.append(entry.source + "\t\t\t" + entry.destination + "\t\t\t" + entry.next + "\t\t\t\t"
                    +entry.NEXT_RANK + "\t\t\t\t   " + entry.cost);
            builder.append("\n");


            System.out.println(entry.source + "          " + entry.destination + "              " + entry.next + "               "
                    +entry.NEXT_RANK + "               " + entry.cost);
           // System.out.println(builder.toString());
        }
        System.out.println("\n");
    }

    public List<OsrpTable.Entry> getEntries() {
        return entries;
    }


    public static class Entry implements Serializable {


        public InetAddress destination;
        public InetAddress next;
        public InetAddress source;
        public int cost;
        public int NEXT_RANK;

        public Entry(InetAddress source , InetAddress destination, InetAddress next, int NEXT_RANK, int cost) {
            this.source = source;
            this.destination = destination;
            this.next = next;
            this.cost = cost;
            this.NEXT_RANK = NEXT_RANK;

        }

        @Override
        public String toString(){
            return "[SOURCE : " + source + "]  DESTINATION : " + destination + "]  NEXT : " + next + "]  NEXT_NODE_RANK : " + NEXT_RANK +  "]  COST : " + cost + "]";

        }




    }
}
