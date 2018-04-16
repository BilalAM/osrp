package gui.utils;

import network_v2.Table;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AssortedUtils {

    public static boolean isDirectEntry(Table.Entry entry) throws UnknownHostException {
        return entry.next.equals(InetAddress.getByName("0.0.0.0"));
    }
}
