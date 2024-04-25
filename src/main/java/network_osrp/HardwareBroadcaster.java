package network_osrp;

import gui.utils.GUIUtils;
import gui.utils.OsrpHardwareUtilities;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * DONE : Updatey Cases
 *
 * TODO : NEEDS TESTING , TESTING , TESTING AND TESTING !!
 *
 */
public class HardwareBroadcaster {

	public void broadcastHardwareInformation(OsrpRouter router) {
		try {
			if (isEmpty(router.getRouterTable())) {
				System.out.println("** LIST TO SEND POLLER IS CURRENTLY EMPTY ** ");
			}
			for (Socket otherRouter : router.getConnectionHistory()) {
				broadcastTo(otherRouter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recieveHardwareInformation(OsrpRouter router) {
		try {
			if (isEmpty(router.getRouterTable())) {
				System.out.println("** LIST IS CURRENTLY EMPTY ** ");
			}
			for (Socket otherRouter : router.getConnectionHistory()) {
				if (otherRouter.getInputStream().available() > 0) {
					recieveInfo(otherRouter, router);
				} else {
					System.out.println("no hardware packet sent by " + otherRouter.getInetAddress().toString()
							+ "..thats impossible !");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the hardware info of itself and sends it to a different router
	 * 
	 * @param socket
	 *            : the router to broadcast the info to.
	 */
	private void broadcastTo(Socket socket) {
		try {
			HardwarePollerPacket hardwarePollerPacket = new HardwarePollerPacket(
					OsrpHardwareUtilities.getRamUtilization(), OsrpHardwareUtilities.getCpuUtilization(),
					InetAddress.getByName(GUIUtils.getPrivateIp("Intel(R) PRO/1000 MT Desktop Adapter")));
			System.out.println("SENDING POLLER PACKET AS : ");
			System.out.println(hardwarePollerPacket.toString());
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeUnshared(hardwarePollerPacket);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recieveInfo(Socket otherRouter, OsrpRouter selfRouter) {
		try {
			HardwareUpdater updater = new HardwareUpdater();
			ObjectInputStream inputStream = new ObjectInputStream(otherRouter.getInputStream());
			Object ob = inputStream.readObject();
			if (ob instanceof HardwarePollerPacket) {
				HardwarePollerPacket hardwarePollerPacket = (HardwarePollerPacket)ob;
				System.out.println("the gotten packet : ");
				System.out.println(hardwarePollerPacket.toString());
				System.out.println(" a poller packet is received..attempting to update our table now");
				System.out.println("our previous table..");
				selfRouter.getRouterTable().displayTable();
				updater.updateTable(hardwarePollerPacket, selfRouter.getRouterTable());
				System.out.println("OUR UPDATING TABLE WITH NEW ENTRY..IF IT IS");
				selfRouter.getRouterTable().displayTable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isEmpty(OsrpTable table) {
		return table.getEntries().isEmpty();
	}

}
