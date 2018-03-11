package network_v2;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Updater {

    public void Update(Table toUpdate, Table forUpdate) throws Exception {
        List<Table.Entry> tempEntries = new ArrayList<>();

        for (Table.Entry forEntry : forUpdate.getEntries()) {
            for (Table.Entry toEntry : toUpdate.getEntries()) {
                if (forEntry.next.equals(InetAddress.getByName("0.0.0.0")) && toEntry.next.equals(InetAddress.getByName("0.0.0.0"))) {
                    continue;
                }
                if (!(forEntry.destination.equals(toEntry.destination))) {

                    tempEntries.add(new Table.Entry(toUpdate.source, forEntry.destination, forUpdate.source, forEntry.cost + 1));
                }
            }
        }
        toUpdate.getEntries().addAll(tempEntries);
    }
}
