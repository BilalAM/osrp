package network_osrp;

import gui.utils.CmdUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class OsrpResponder {
	private static List<OsrpTable> tables = new ArrayList<>();

	/**
	 * ATTEMPTS to 'broadcast' one by one the outputStreams() of 'other sockets'
	 * currently in our list and writeObject() OUR table. <br/>
	 * 
	 * @param router
	 *            : Our router (current machine)
	 */
	public void broadcastTables(OsrpRouter router) {
		try {

			// CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting to contact other
			// routers to send tables too...\n");
			System.out.println("\n attempting to contact other routers to send tables too...");
			if (isEmptyList(router)) {

				// CmdUtils.getSharedRIPCMDBuilder().append("$- *** There Is No Router Currently
				// Connected...\n");
				System.out.println("***  There is no router currently connected ***");
			}
			for (Socket otherRouter : router.getConnectionHistory()) {

				// CmdUtils.getSharedRIPCMDBuilder().append("$- Connected Router Found !\n");
				System.out.println("connected router found...");
				sendTable(router, otherRouter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ATTEMPTS to receive one by one the inputStreams() of 'other sockets'
	 * currently in our list and readObject() the tables. <br/>
	 * The <b><code>( otherRouter.getInputStream().available() > 0 ) </code> </b>is
	 * to check if the stream has currently data on it , if it has then read it,
	 * else we suppose the router or socket has not sent any data as of yet
	 * 
	 * @param router
	 *            : Our router (current machine)
	 */
	public void recieveTables(OsrpRouter router) {
		try {
			if (isEmptyList(router)) {
				// CmdUtils.getSharedRIPCMDBuilder().append("$- *** There Is No Router Currently
				// Connected *** !\n");
				System.out.println("***  There is no router currently connected ***");
			}
			for (Socket otherRouter : router.getConnectionHistory()) {

				// CmdUtils.getSharedRIPCMDBuilder().append("$- Connected Router Found !\n");
				System.out.println("connected router found....");
				// rough estimate to check if there is any data available......very important
				// check !
				if (otherRouter.getInputStream().available() > 0) {
					_recieveTables(otherRouter, router);
				} /*
				else {

					System.out.println("ROUTER HAVING IP " + otherRouter.getInetAddress()
							+ " HAS NOT SENT TABLE AS OF YET...TRY AGAIN LATER..!");
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * helper method of recieveTables logic..
	 * 
	 * @param otherRouter
	 * @throws Exception
	 *             : POSSIBLE EXCEPTIONS : Connection reset , SocketNotFound or
	 *             StreamCorrupted
	 */
	private void _recieveTables(Socket otherRouter, OsrpRouter selfRouter) throws Exception {
		OsrpUpdater updater = new OsrpUpdater();
		ObjectInputStream input = new ObjectInputStream(otherRouter.getInputStream());

		CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To Receive Table From The Stream...\n");
		System.out.println("attempting to receive table from stream");
		// if (input.readObject() instanceof Table) {
		Object ob = input.readObject();
		if (ob instanceof OsrpTable) {
			OsrpTable t = (OsrpTable) ob;
			tables.add(t);
			CmdUtils.getSharedRIPCMDBuilder().append("$- A table Is Received And Added To The Inner List\n");
			System.out.println("a table is received and added to the inner list...");
			t.displayTable();
			CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To UPDATE Our Table..\n");
			System.out.println("ATTEMPTING TO UPDATE OUR TABLE...");
			updater.Update(selfRouter.getRouterTable(), t);
			CmdUtils.getSharedRIPCMDBuilder().append("$- Our New Table With A UPDATED Entry\n");
			System.out.println("OUR UPDATED TABLE WITH A NEW ENTRY....");
			selfRouter.getRouterTable().displayTable();
			System.out.println("IS SOCKET CLOSED ? (WITH STREAM) ? " + otherRouter.isClosed());
			// }
		} else {

		}
	}

	/**
	 * helper method of sendTables() logic
	 * 
	 * @param fromRouter
	 * @param toRouter
	 * @throws Exception
	 */
	private void sendTable(OsrpRouter fromRouter, Socket toRouter) throws Exception {
		ObjectOutputStream outputStream = new ObjectOutputStream(toRouter.getOutputStream());
		CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To Write A Table To The Stream..\n");
		System.out.println("attempting to write object to stream...");
		outputStream.writeUnshared(fromRouter.getRouterTable());
		outputStream.flush();
		CmdUtils.getSharedRIPCMDBuilder().append("$- Table Sent To The Stream..\n");
		System.out.println("table sent to stream...");
	}

	public static boolean isEmptyList(OsrpRouter router) {
		return router.getConnectionHistory().isEmpty();
	}

	public List<OsrpTable> getTables() {
		return tables;
	}

}
