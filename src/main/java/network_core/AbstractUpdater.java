package network_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for routing table updaters shared by both RIP and OSRP protocols.
 * Implements improved loop detection using visited-set approach for N-hop loops,
 * and only adds routes that are better than existing ones.
 *
 * @param <E> the entry type
 * @param <T> the routing table type
 */
public abstract class AbstractUpdater<E extends AbstractEntry, T extends AbstractRoutingTable<E>> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Maximum allowed hop count to prevent count-to-infinity */
    private static final int MAX_HOP_COUNT = 16;

    private static final InetAddress UNSPECIFIED;
    static {
        try {
            UNSPECIFIED = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to resolve 0.0.0.0", e);
        }
    }

    protected abstract StringBuilder getCmdBuilder();

    /**
     * Creates a new entry for the toUpdate table based on a forEntry from the forUpdate table.
     */
    protected abstract E createEntry(InetAddress source, E forEntry, InetAddress via);

    /**
     * Returns the metric used for route comparison (cost for RIP, rank for OSRP).
     * Default uses cost; OSRP overrides to use NEXT_RANK.
     */
    protected int getMetric(E entry) {
        return entry.cost;
    }

    /**
     * Updates toUpdate's routing table with entries from forUpdate's table.
     * Applies loop detection, duplicate filtering, and best-route selection.
     */
    public void Update(T toUpdate, T forUpdate) {
        List<E> newEntries = new ArrayList<>();
        try {
            logger.debug("Starting table update from {}", forUpdate.source);

            for (E forEntry : forUpdate.getEntries()) {
                // Skip if destination is ourselves (would route to self)
                if (toUpdate.source != null && toUpdate.source.equals(forEntry.destination)) {
                    logger.debug("Skipping entry with destination equal to our source: {}", forEntry);
                    continue;
                }

                // Skip if this would create a routing loop
                if (wouldCreateLoop(toUpdate, forEntry, forUpdate.source)) {
                    getCmdBuilder().append("$- *** LOOP DETECTED - SKIPPING ENTRY **\n");
                    getCmdBuilder().append("$- " + forEntry.toString() + "\n");
                    logger.debug("Loop detected for entry: {}", forEntry);
                    continue;
                }

                // Create the candidate route through the advertising router
                E candidate = createEntry(toUpdate.source, forEntry, forUpdate.source);

                // Enforce max hop count (count-to-infinity prevention)
                if (candidate.cost > MAX_HOP_COUNT) {
                    logger.debug("Entry exceeds max hop count ({}), skipping: {}", MAX_HOP_COUNT, forEntry);
                    getCmdBuilder().append("$- *** MAX HOP COUNT EXCEEDED - SKIPPING **\n");
                    continue;
                }

                // Check if we already have a route to this destination
                E existingRoute = findBestRouteToDestination(toUpdate, forEntry.destination);

                if (existingRoute == null) {
                    // No existing route — add the new one
                    getCmdBuilder().append("$- *** NEW ENTRY ADDED **\n");
                    getCmdBuilder().append("$- " + candidate.toString() + "\n");
                    logger.debug("New route added: {}", candidate);
                    newEntries.add(candidate);
                } else if (getMetric(candidate) < getMetric(existingRoute)) {
                    // New route is better — add it (old one remains but won't be selected)
                    getCmdBuilder().append("$- *** BETTER ROUTE FOUND **\n");
                    getCmdBuilder().append("$- Old: " + existingRoute.toString() + "\n");
                    getCmdBuilder().append("$- New: " + candidate.toString() + "\n");
                    logger.debug("Better route found: {} replaces {}", candidate, existingRoute);
                    newEntries.add(candidate);
                } else {
                    logger.debug("Existing route is equal or better, skipping: {}", forEntry);
                }
            }

            toUpdate.getEntries().addAll(newEntries);
        } catch (Exception e) {
            logger.error("Error during table update", e);
        }
    }

    /**
     * Detects if adding a route via 'via' would create a loop of any length.
     * Traces the next-hop chain: if we encounter ourselves, it's a loop.
     */
    private boolean wouldCreateLoop(T table, E candidate, InetAddress via) {
        Set<InetAddress> visited = new HashSet<>();
        if (table.source != null) {
            visited.add(table.source);
        }

        InetAddress nextHop = via;
        while (nextHop != null && !isUnspecified(nextHop)) {
            if (!visited.add(nextHop)) {
                // Already visited this hop — loop detected
                return true;
            }
            // Find how 'nextHop' would route to the candidate's destination
            E routeFromNext = findRouteVia(table, candidate.destination, nextHop);
            if (routeFromNext != null && !isUnspecified(routeFromNext.next)) {
                nextHop = routeFromNext.next;
            } else {
                break;
            }
        }
        return false;
    }

    /**
     * Finds the best existing route to a destination in the table.
     */
    private E findBestRouteToDestination(T table, InetAddress destination) {
        E best = null;
        for (E entry : table.getEntries()) {
            if (entry.destination.equals(destination)) {
                if (best == null || getMetric(entry) < getMetric(best)) {
                    best = entry;
                }
            }
        }
        return best;
    }

    /**
     * Finds a route in the table where the next hop goes through a specific address.
     */
    private E findRouteVia(T table, InetAddress destination, InetAddress via) {
        for (E entry : table.getEntries()) {
            if (entry.destination.equals(destination) && entry.next.equals(via)) {
                return entry;
            }
        }
        return null;
    }

    private static boolean isUnspecified(InetAddress addr) {
        return UNSPECIFIED.equals(addr);
    }
}
