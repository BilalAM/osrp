package network_osrp;

import gui.utils.GUIUtils;
import gui.utils.OsrpHardwareUtilities;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HardwareBroadcaster2 {

	public void broadCastInfo(OsrpRouter selfRouter) {
		try {
			if (selfRouter.getHardwareList().isEmpty()) {
				System.out.println("list to send pollers to is empty currently/...");
			} else {
				for (Socket socket : selfRouter.getHardwareList()) {
					//Socket _socket = new Socket(socket.getInetAddress(), 3003);
					broadcastTo(socket);
					//socket.close();
				}
			}
		} catch (Exception e) {

		}
	}

	public void recieveInfo(OsrpRouter router) {
		try {
			if(router.getHardwareList().isEmpty()){

			}
			for(Socket _socket : router.getHardwareList()){
				if(_socket.getInputStream().available() > 0){
					HardwareUpdater updater = new HardwareUpdater();
				//	Socket socket = router.getPollerServerSocket().accept();
					System.out.println("recieved a poller connection");
					ObjectInputStream inputStream = new ObjectInputStream(_socket.getInputStream());
					HardwarePollerPacket pollerPacket = (HardwarePollerPacket) inputStream.readObject();
					updater.updateTable(pollerPacket, router.getRouterTable());
						System.out.println("OUR UPDATING TABLE WITH NEW ENTRY..IF IT IS");
						router.getRouterTable().displayTable();
				//	socket.close();
				}else {

					/*System.out.println("ROUTER HAVING IP " + _socket.getInetAddress()
							+ " HAS NOT SENT POLLER PACKET AS OF YET...TRY AGAIN LATER..!");*/
				}
			}



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void broadcastTo(Socket socket) {
		try {
			HardwarePollerPacket hardwarePollerPacket = new HardwarePollerPacket(
					OsrpHardwareUtilities.getRamUtilization(), OsrpHardwareUtilities.getCpuUtilization(),
					InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")));
			System.out.println("SENDING POLLER PACKET AS : ");
			System.out.println(hardwarePollerPacket.toString());
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeUnshared(hardwarePollerPacket);
			outputStream.flush();
			//outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
