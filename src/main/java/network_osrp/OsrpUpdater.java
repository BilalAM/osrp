package network_osrp;

import gui.utils.CmdUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * TODO : CONTAINS ERRORS, MAKE THE Table.Entry class FIELDS PUBLIC
 *
 */

public class OsrpUpdater {
	public void Update(OsrpTable toUpdate, OsrpTable forUpdate) {
		List<OsrpTable.Entry> tempEntries = new ArrayList<>();
		try {

			System.out.println("inner loop started..");
			inner: for (OsrpTable.Entry forEntry : forUpdate.getEntries()) {
				if (isLoopEntry(toUpdate, forEntry)) {
					continue inner;
				} else if (isIndirectLoopToLoop(toUpdate, forEntry)) {
					continue inner;
				} else if (isDirectAndIndirectLoopBackEntry(toUpdate, forEntry)) {
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** WE HAVE A REDUNDANT ENTRY !! **\n");
					System.out.println("** WE HAVE A REDUNDANT ENTRY !! **");
					System.out.println(forEntry.toString());
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** SKIPPING THIS ENTRY  **\n");
					System.out.println("** SKIPPING THIS ENTRY ** \n");
					continue inner;
				} else if (isDirectEntry(forEntry)) {
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** NEW DIRECT ENTRY  **\n");
					System.out.println("NEW DIRECT ENTRY !! \n");
					tempEntries.add(new OsrpTable.Entry(toUpdate.source, forEntry.destination, forUpdate.source,
							forEntry.NEXT_RANK, forEntry.cost + 1));
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** NEW ENTRY IS : **\n");
					System.out.println("NEW ENTRY IS ");
					CmdUtils.getSharedRIPCMDBuilder().append("$- " + forEntry.toString() + "\n");
					System.out.println(forEntry.toString() + '\n');
					continue inner;
				} else {
					System.out.println("NEW DIRECT ENTRY !! \n");
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** NEW DIRECT ENTRY: **\n");
					tempEntries.add(new OsrpTable.Entry(toUpdate.source, forEntry.destination, forUpdate.source,
							forEntry.NEXT_RANK, forEntry.cost + 1));
					System.out.println("NEW ENTRY IS ");
					continue inner;
				}
			}

			toUpdate.getEntries().addAll(tempEntries);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isDirectAndIndirectLoopBackEntry(OsrpTable toTable, OsrpTable.Entry e2) {
		for (OsrpTable.Entry e1 : toTable.getEntries()) {
			if (e1.source.equals(e2.source)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isLoopEntry(OsrpTable toTable, OsrpTable.Entry e2) throws UnknownHostException {
		for (OsrpTable.Entry e1 : toTable.getEntries()) {
			if (e1.source.equals(e2.destination)) {
				if (e1.destination.equals((e2.source))) {
					System.out.println("** WE HAVE A LOOP ENTRY !! **");
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** WE HAVE A LOOP ENTRY **\n");
					CmdUtils.getSharedRIPCMDBuilder().append("$- *** LOOP ENTRY IS : **\n");

					System.out.println("** LOOP ENTRY BETWEEN toEntry and forEntry : ");
					CmdUtils.getSharedRIPCMDBuilder().append("$-" + e1.toString() + "-------" + e2.toString() + "\n");
					CmdUtils.getSharedRIPCMDBuilder().append("$- ** SKIPPING THIS ENTRY ** \n");
					System.out.println(e1.toString() + "---------" + e2.toString());
					System.out.println("** SKIPPING THIS ENTRY... ** \n");
					return e1.next.equals(InetAddress.getByName("0.0.0.0"))
							&& e2.next.equals(InetAddress.getByName("0.0.0.0"));
				}
			}
		}
		return false;
	}

	private static boolean isIndirectLoopToLoop(OsrpTable toTable, OsrpTable.Entry e2) throws UnknownHostException {
		for (OsrpTable.Entry e1 : toTable.getEntries()) {
			if (e1.destination.equals(e2.destination) && e1.next.equals(e2.source)) {

				CmdUtils.getSharedRIPCMDBuilder().append("$- ** WE HAVE AN INDIRECT LOOP ENTRY ** SKIPPING** \n");
				CmdUtils.getSharedRIPCMDBuilder().append("$-" + e1.toString() + "-------" + e2.toString() + "\n");

				System.out.println("** WE HAVE AN INDIRECT LOOP ENTRY !! SKIPPING !!");
				System.out.println(e1.toString() + "-----" + e2.toString());
				System.out.println("** SKIPPING THIS ENTRY** \n");
				return true;
			} else if (e1.source.equals(e2.destination) && e1.next.equals(InetAddress.getByName("0.0.0.0"))
					&& e2.next.equals(InetAddress.getByName("0.0.0.0"))) {

				CmdUtils.getSharedRIPCMDBuilder().append("$- ** WE HAVE AN INDIRECT LOOP ENTRY ** SKIPPING** \n");
				CmdUtils.getSharedRIPCMDBuilder().append("$-" + e1.toString() + "-------" + e2.toString() + "\n");

				System.out.println("** WE HAVE AN INDIRECT LOOP ENTRY !! SKIPPING !!");
				System.out.println(e1.toString() + "-----" + e2.toString());
				System.out.println("** SKIPPING THIS ENTRY** \n");
				return true;
			}
		}
		return false;
	}

	private static boolean isDirectEntry(OsrpTable.Entry entry) throws UnknownHostException {
		return entry.next.equals(InetAddress.getByName("0.0.0.0"));
	}

	@Deprecated
	private static boolean isIndirectLoopEntry(OsrpTable.Entry e1, OsrpTable.Entry e2) throws UnknownHostException {
		if (e1.destination.equals(e2.destination) && e1.next.equals(e2.source)) {
			return true;
		} else if (e1.source.equals(e2.destination) && e1.next.equals(InetAddress.getByName("0.0.0.0"))
				&& e2.next.equals("0.0.0.0")) {
			return true;
		}
		return false;
	}

	private static boolean isIndirectEntry(OsrpTable.Entry e1, OsrpTable.Entry e2) {

		return true;
	}

	private static void addDirectEntry(OsrpTable.Entry entry) {

	}

}
