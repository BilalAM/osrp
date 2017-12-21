package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {

	public static String name;
	private Socket selfClient;
	private Packet packetToSend;
	private static int PORT = 1999;
	private String destination;

	public Host(String name, String destination) {
		try {
			this.destination = destination;
			packetToSend = new Packet();
			initializePacket();
		} catch (Exception e) {
			System.out.println("cannot create Host at PORT " + PORT);
			e.printStackTrace();
		}
	}

	private void initializePacket() throws Exception {
		//packetToSend.setSourceAddress(selfClient.getInetAddress());
		packetToSend.setDestinationAddress(InetAddress.getByName(destination));
		packetToSend.setPacketMessage("Hello :) this is a sample packet message , it can be an email or a big file");
	}

	public void connectToRouter(String ip) {
		try {
			selfClient = new Socket(ip, PORT);
			packetToSend.setSourceAddress(selfClient.getInetAddress());
			try (ObjectOutputStream packetStream = new ObjectOutputStream(selfClient.getOutputStream())) {
				packetStream.writeObject(packetToSend);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				selfClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
