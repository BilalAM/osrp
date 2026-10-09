package network_osrp;

import network_core.AbstractResponder;
import network_core.AbstractUpdater;

import java.io.ObjectInputStream;

/**
 * OSRP-specific responder. Implements protocol-specific deserialization and updater creation.
 */
public class OsrpResponder extends AbstractResponder<OsrpTable.Entry, OsrpTable, OsrpRouter> {

	@Override
	protected OsrpTable getTable(OsrpRouter router) {
		return router.getRouterTable();
	}

	@Override
	protected AbstractUpdater<OsrpTable.Entry, OsrpTable> createUpdater() {
		return new OsrpUpdater();
	}

	@Override
	protected OsrpTable deserializeTable(ObjectInputStream input) throws Exception {
		Object ob = input.readObject();
		if (ob instanceof OsrpTable) {
			return (OsrpTable) ob;
		}
		return null;
	}
}
