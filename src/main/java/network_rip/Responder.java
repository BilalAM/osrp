package network_rip;

import network_core.AbstractResponder;
import network_core.AbstractUpdater;
import network_v2.Router;
import network_v2.Table;

import java.io.ObjectInputStream;

/**
 * RIP-specific responder. Implements protocol-specific deserialization and updater creation.
 */
public class Responder extends AbstractResponder<Table.Entry, Table, Router> {

    @Override
    protected Table getTable(Router router) {
        return router.getTable();
    }

    @Override
    protected AbstractUpdater<Table.Entry, Table> createUpdater() {
        return new Updater();
    }

    @Override
    protected Table deserializeTable(ObjectInputStream input) throws Exception {
        return (Table) input.readObject();
    }
}
