package network_v2;

import java.io.Serializable;
import java.net.InetAddress;


// please see Table.java
@Deprecated
public class Entry implements Serializable {


    InetAddress destination;
    InetAddress next;
    int cost;

    public Entry(InetAddress destination, InetAddress next, int cost) {
        this.destination = destination;
        this.next = next;
        this.cost = cost;
    }


}
