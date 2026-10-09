package network_rip;

import gui.utils.CmdUtils;
import network_core.AbstractUpdater;
import network_v2.Table;

import java.net.InetAddress;

/**
 * RIP-specific updater. Only needs to implement createEntry() and getCmdBuilder().
 */
public class Updater extends AbstractUpdater<Table.Entry, Table> {

    @Override
    protected StringBuilder getCmdBuilder() {
        return CmdUtils.getSharedRIPCMDBuilder();
    }

    @Override
    protected Table.Entry createEntry(InetAddress source, Table.Entry forEntry, InetAddress via) {
        return new Table.Entry(source, forEntry.destination, via, forEntry.cost + 1);
    }
}
