package network_osrp;

import gui.utils.CmdUtils;
import network_core.AbstractUpdater;

import java.net.InetAddress;

/**
 * OSRP-specific updater. Uses NEXT_RANK as the metric for route comparison
 * instead of cost (hop count).
 */
public class OsrpUpdater extends AbstractUpdater<OsrpTable.Entry, OsrpTable> {

	@Override
	protected StringBuilder getCmdBuilder() {
		return CmdUtils.getSharedRIPCMDBuilder();
	}

	@Override
	protected OsrpTable.Entry createEntry(InetAddress source, OsrpTable.Entry forEntry, InetAddress via) {
		return new OsrpTable.Entry(source, forEntry.destination, via, forEntry.NEXT_RANK, forEntry.cost + 1);
	}

	@Override
	protected int getMetric(OsrpTable.Entry entry) {
		return entry.NEXT_RANK;
	}
}
