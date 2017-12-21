package network;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

public class Packet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String packetMessage;
	private InetAddress sourceAddress;
	private InetAddress destinationAddress;
	
	
	
	public String getPacketMessage() {
		return packetMessage;
	}
	public void setPacketMessage(String packetMessage) {
		this.packetMessage = packetMessage;
	}
	public InetAddress getSourceAddress() {
		return sourceAddress;
	}
	public void setSourceAddress(InetAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	public InetAddress getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(InetAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
	
	@Override
	public String toString() {
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append("==================================");
		builder.append("\n");
		builder.append("	       PACKET");
		builder.append("\n");
		builder.append("==================================");
		builder.append("\n");
		builder.append("FROM : " + this.getSourceAddress().getHostAddress());
		builder.append("\n");
		builder.append("TO   : " + this.getDestinationAddress().getHostAddress());
		builder.append("\n");
		builder.append("SENT   : " + date.toString());
		builder.append("\n");
		builder.append("==================================");
		builder.append("\n");
		builder.append("	       PAYLOAD");
		builder.append("\n");
		builder.append("==================================");
		builder.append("\n");
		builder.append(this.getPacketMessage());
		builder.append("\n");
		builder.append("------------ END OF PACKET -----------");
		builder.append("\n");
		return builder.toString();
	}
}
