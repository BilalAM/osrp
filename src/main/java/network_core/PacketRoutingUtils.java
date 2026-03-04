package network_core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Unified utility class for packet routing, replacing both
 * PacketForwarderUtils and OsrpPacketForwarderUtils.
 */
public class PacketRoutingUtils {

    /**
     * Returns the entry with the shortest cost/rank for forwarding a packet,
     * filtering out entries where the destination has already been reached.
     *
     * @param table      the routing table to search
     * @param packet     the packet being forwarded
     * @param comparator the comparator to determine shortest (e.g., by cost or rank)
     * @param <E>        the entry type
     * @return the best entry, or null if no matching entries
     */
    public static <E extends AbstractEntry> E getShortestEntry(
            AbstractRoutingTable<E> table, Packet packet, Comparator<E> comparator) {
        List<E> matchingEntries = new ArrayList<>();
        for (E entry : table.getEntries()) {
            if (isDestinationReached(entry, packet)) {
                continue;
            }
            if (entry.destination.equals(packet.getDestinationAddress())) {
                matchingEntries.add(entry);
            }
        }
        return matchingEntries.stream().min(comparator).orElse(null);
    }

    /**
     * Checks if the destination has been reached (i.e., the entry source matches
     * the packet's destination address).
     */
    public static boolean isDestinationReached(AbstractEntry entry, Packet packet) {
        return entry.source.equals(packet.getDestinationAddress());
    }
}
