package network_core;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Base class for routing table entries shared by both RIP and OSRP protocols.
 */
public abstract class AbstractEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    public InetAddress source;
    public InetAddress destination;
    public InetAddress next;
    public int cost;

    protected AbstractEntry(InetAddress source, InetAddress destination, InetAddress next, int cost) {
        this.source = source;
        this.destination = destination;
        this.next = next;
        this.cost = cost;
    }

    /**
     * Returns true if this is a direct entry (next hop is 0.0.0.0).
     */
    public boolean isDirectEntry() throws UnknownHostException {
        return next.equals(InetAddress.getByName("0.0.0.0"));
    }
}
