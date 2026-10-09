package network_osrp;

import gui.utils.OsrpHardwareUtilities;
import network_core.NetworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HardwareBroadcaster2 {
	private static final Logger logger = LoggerFactory.getLogger(HardwareBroadcaster2.class);

	public void broadCastInfo(OsrpRouter selfRouter) {
		try {
			if (selfRouter.getHardwareList().isEmpty()) {
				logger.debug("Hardware list to send pollers to is empty currently");
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

	public void receiveInfo(OsrpRouter router) {
		try {
			if(router.getHardwareList().isEmpty()){

			}
			for(Socket _socket : router.getHardwareList()){
				if(_socket.getInputStream().available() > 0){
					HardwareUpdater updater = new HardwareUpdater();
				//	Socket socket = router.getPollerServerSocket().accept();
					logger.debug("Received a poller connection");
					ObjectInputStream inputStream = new ObjectInputStream(_socket.getInputStream());
					HardwarePollerPacket pollerPacket = (HardwarePollerPacket) inputStream.readObject();
					updater.updateTable(pollerPacket, router.getRouterTable());
						logger.debug("Updating table with new entry if applicable");
						router.getRouterTable().displayTable();
				//	socket.close();
				}else {

					/*System.out.println("ROUTER HAVING IP " + _socket.getInetAddress()
							+ " HAS NOT SENT POLLER PACKET AS OF YET...TRY AGAIN LATER..!");*/
				}
			}



		} catch (Exception e) {
			logger.error("Error receiving hardware info", e);
		}
	}

	private void broadcastTo(Socket socket) {
		try {
			HardwarePollerPacket hardwarePollerPacket = new HardwarePollerPacket(
					OsrpHardwareUtilities.getRamUtilization(), OsrpHardwareUtilities.getCpuUtilization(),
					InetAddress.getByName(NetworkConfig.getPrivateIp()));
			logger.debug("Sending poller packet: {}", hardwarePollerPacket);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeUnshared(hardwarePollerPacket);
			outputStream.flush();
			//outputStream.close();
		} catch (Exception e) {
			logger.error("Error broadcasting hardware info", e);
		}
	}

}
