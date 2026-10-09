package network_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Base class for routing tables shared by both RIP and OSRP protocols.
 * Uses a template method pattern for displayTable() — subclasses provide header and entry formatting.
 */
public abstract class AbstractRoutingTable<E extends AbstractEntry> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AbstractRoutingTable.class);

    public InetAddress source;
    public InetAddress destination;
    public InetAddress next;
    public int cost;

    protected final List<E> entries = new CopyOnWriteArrayList<>();
    private transient StringBuilder builder;

    public List<E> getEntries() {
        return entries;
    }

    public String getTableBuilder() {
        return builder != null ? builder.toString() : "";
    }

    /**
     * Builds a display string representation of the routing table.
     * Uses stream().distinct() to prevent duplicate entries caused by threading.
     */
    public void displayTable() {
        builder = new StringBuilder();
        builder.append("\n");
        builder.append(getTableHeader());
        logger.debug(getTableHeader());

        for (E entry : entries.stream().distinct().collect(Collectors.toList())) {
            String line = formatEntry(entry);
            builder.append(line).append("\n");
            logger.debug(line);
        }
    }

    protected abstract String getTableHeader();

    protected abstract String formatEntry(E entry);
}
