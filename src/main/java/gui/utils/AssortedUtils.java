package gui.utils;

import network_v2.Table;

import java.net.InetAddress;
import java.net.UnknownHostException;

import network_osrp.OsrpTable;
import network_osrp.OsrpTable.Entry;

public class AssortedUtils {

	public static boolean isDirectEntry(Table.Entry entry) throws UnknownHostException {
		return entry.next.equals(InetAddress.getByName("0.0.0.0"));
	}

	public static boolean isOSRPDirectEntry(OsrpTable.Entry entry) throws UnknownHostException {
		return entry.next.equals(InetAddress.getByName("0.0.0.0"));
	}
}
