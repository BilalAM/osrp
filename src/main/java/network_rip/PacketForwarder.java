package network_rip;

import gui.utils.CmdUtils;
import network_core.NetworkConfig;
import network_core.Packet;
import network_core.PacketRoutingUtils;
import network_v2.Router;
import network_v2.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PacketForwarder {
	private static final Logger logger = LoggerFactory.getLogger(PacketForwarder.class);

	/**
	 * accept host connection and also receive the packet
	 *
	 * - THIS IS THE "INITIAL" STEP TAKEN BY THE ROUTER THAT IS "DIRECTLY" CONNECTED
	 * TO A HOST
	 *
	 * - The Actual Send And Receive Packets By Routers Are Implemented In Different
	 * Functions.
	 *
	 * - THIS ONLY ACCEPTS AND FORWARDS THE PACKET RECEIVED INITIALLY BY THE HOST
	 * THAT IS DIRECTLY CONNECTED TO THIS ROUTER !
	 *
	 */
	public boolean acceptHostConnection(Router router) {
		boolean check = true;
		StringBuilder builder = new StringBuilder("a");
		try {
			Socket host = router.getHostServerSocket().accept();
			logger.info("Router is connected to a host");

			CmdUtils.getSharedCMDBuilder().append("$- A Router Is Connected To A Host !! \n");

			if (host.getInputStream().available() > 0) {
				ObjectInputStream inputFromHost = new ObjectInputStream(host.getInputStream());
				Packet packet = (Packet) inputFromHost.readObject();
				check = true;
				logger.debug("Packet initially received by router");
				logger.debug("Packet: {}", packet);
				logger.debug("Finding the shortest cost entry for this packet to be forwarded to");

				Table.Entry entry = PacketRoutingUtils.getShortestEntry(router.getTable(), packet, java.util.Comparator.comparingInt(e -> e.cost));

				CmdUtils.getSharedCMDBuilder().append("$- Router Has Received A Packet By Host.. \n");
				CmdUtils.getSharedCMDBuilder().append(packet.toString());

				if (entry == null) {
					logger.debug("Destination reached or entry is null");
					CmdUtils.getSharedCMDBuilder().append("$- Destination Has Been Reached ! Entry Is Null Now ! \n");

				} else if (entry.isDirectEntry()) {
					entry.next = entry.destination;

					CmdUtils.getSharedCMDBuilder().append(
							"$- The Shortest Cost Entry For This Packet To Be Forwarded To Is Found To Be : \n");
					CmdUtils.getSharedCMDBuilder().append("$- " + entry.toString() + "\n");
					CmdUtils.getSharedCMDBuilder()
							.append("$- It Is A Direct Entry.. Changing The Next To Destination IP..\n");

					helper(host, entry, builder, packet);
				} else {

					CmdUtils.getSharedCMDBuilder().append(
							"$- The Shortest Cost Entry For This Packet To Be Forwarded To Is Found To Be : \n");
					CmdUtils.getSharedCMDBuilder().append("$- " + entry.toString() + "\n");

					helper(host, entry, builder, packet);
				}
			} else {
				check = false;
				logger.debug("Host has not sent any packet yet");
			}
		} catch (Exception e) {
			logger.error("Error accepting host connection for RIP packet", e);
		}
		return check;
	}

	public void requestHostConnection(Packet packetToSend) {

		try {
			Socket socket = new Socket(NetworkConfig.getPrivateIp(), NetworkConfig.RIP_HOST_PORT);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(packetToSend);

		} catch (Exception e) {
			logger.error("Error requesting host connection for RIP packet", e);
		}
	}

	/***********************************************************************
	 *
	 *
	 *
	 * ENTER AT YOUR OWN RISK !
	 *
	 * MAGIC AT WORK OVER HERE !
	 *
	 *
	 *
	 * Algorithm works as follows :
	 *
	 * -- This router is constantly listening for Sockets on port 2002
	 *
	 * -- Port 2002 is for packets forwarding only
	 *
	 * -- It Checks the route list for existence of that socket on which it needs to
	 * forward , if it exists then dont create a socket and just forward (Same for
	 * receiving side)
	 *
	 * -- When it accepts the connection , it immediatly receives the inputStream
	 * and creates the packet object from the stream.
	 *
	 * -- Then it checks that packets for the destination and whether the
	 * destination address is present in our table.
	 *
	 * -- If it is present , it checks if it is directly connected or is a indirect
	 * connection.
	 *
	 * -- If it is a direct connection , then the entry.next is changed to the
	 * destination (Direct connections have entry 0.0.0.0 as default ,thats why the
	 * entry.next is changed since address 0.0.0.0 is localhost) .
	 *
	 * -- Then after this it creates a socket on port 2002 with the IP Address
	 * received from the Packet object.
	 *
	 * -- Then it forwards that packet onwards..
	 *
	 *
	 *
	 ***********************************************************************/
	public boolean receiveAndForwardThePacket(Router router) {
		StringBuilder builder = new StringBuilder();
		boolean check = true;
		try {

			// Thread.sleep(1000);
			Socket socket = router.getPacketServerSocket().accept();
			// cmdBuilder.append("$- Connection Received For Packet..\n");

			CmdUtils.getSharedCMDBuilder().append("$- Connection Received For Packet..\n");

			// System.out.println("ACCEPTED THE PACKET !! ");
			builder.append("ACCEPTED THE PACKET SOCKET !! \n");
			CmdUtils.getSharedCMDBuilder().append("$- Attempting To Receive The Packet .. \n");

			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			Packet packet = (Packet) input.readObject();

			builder.append("GOT A PACKET .. PRINTING IT... \n");
			builder.append(packet.toString() + "\n");
			builder.append("EXTRACTING SHORTEST ENTRY FROM PACKET BY CHECKING THE TABLE...\n");

			CmdUtils.getSharedCMDBuilder().append(
					"$- Packet Received...Trying To Extract The Shortest Entry From Packet And Checking Our Table For Shortest Entry...\n ");

			Table.Entry entry = PacketRoutingUtils.getShortestEntry(router.getTable(), packet, java.util.Comparator.comparingInt(e -> e.cost));
			CmdUtils.getSharedCMDBuilder().append("$- Entry Extracted...Checking further..\n");

			if (entry == null) {
				CmdUtils.getSharedCMDBuilder().append(
						"$- ****  DESTINATION HAS BEEN REACHED ! OR A NULL ENTRY IS HERE.. GOING BACK TO LISTENING ***\n");

				builder.append("DESTINATION REACHED OR NULL IS HERE..");
			} else if (entry.isDirectEntry()) {
				CmdUtils.getSharedCMDBuilder()
						.append("$- It Is A Direct Entry.. Changing The Next To Destination IP..\n");

				entry.next = entry.destination;
				helper(socket, entry, builder, packet);
			} else {
				CmdUtils.getSharedCMDBuilder().append("$- It Is A Indirect Entry...Forwarding The Packet...");

				helper(socket, entry, builder, packet);
			}

			logger.debug("{}", builder);

		} catch (Exception e) {
			logger.error("Error receiving and forwarding RIP packet", e);

		}
		return check;

	}

	private void helper(Socket socket, Table.Entry entry, StringBuilder builder, Packet packet) throws Exception {
		builder.append("ENTRY OF SHORTEST IS + " + entry.toString() + "\n");

		CmdUtils.getSharedCMDBuilder().append("$- Attempting To Open A New Connection To " + entry.next + "\n");

		Socket _socket = new Socket(entry.next, NetworkConfig.RIP_PACKET_PORT);

		CmdUtils.getSharedCMDBuilder().append("$- Connection Success !! \n");
		CmdUtils.getSharedCMDBuilder().append("$- Forwarding The Packet To This New Router..\n");

		builder.append("FORWARDING PACKET TO " + _socket.getInetAddress() + "\n");

		ObjectOutputStream outputStream = new ObjectOutputStream(_socket.getOutputStream());
		outputStream.writeObject(packet);

		_socket.close();
		socket.close();
		CmdUtils.getSharedCMDBuilder().append("$- The Packet Has Been Sent... \n");

	}

}
