package network_v2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Updater {




    public void Update(Table toUpdate, Table forUpdate) {
        List<Table.Entry> tempEntries = new ArrayList<>();
        try {
            for (Table.Entry forEntry : forUpdate.getEntries()) {

                inner: for (Table.Entry toEntry : toUpdate.getEntries()) {
                    if(isLoopEntry(toEntry,forEntry)){
                        System.out.println("**WE HAVE A LOOP ENTRY !! **");
                        //skip this entry...
                        continue inner;
                    }
                    if (!(forEntry.destination.equals(toEntry.destination))) {

                        tempEntries.add(new Table.Entry(toUpdate.source, forEntry.destination, forUpdate.source, forEntry.cost + 1));
                    }
                }
            }
            toUpdate.getEntries().addAll(tempEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isLoopEntry(Table.Entry e1 , Table.Entry e2) throws UnknownHostException{
        if(e1.source.equals(e2.destination)){
            if(e1.destination.equals((e2.source))){
                return e1.next.equals(InetAddress.getByName("0.0.0.0")) && e2.next.equals(InetAddress.getByName("0.0.0.0"));
            }
        }
        return false;
    }

}
