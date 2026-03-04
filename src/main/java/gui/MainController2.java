package gui;

import benchmarking.SharedBarCHART;
import benchmarking.SharedLineCHART;
import gui.utils.CmdUtils;
import javafx.application.Platform;
import network_core.NetworkConfig;
import javafx.fxml.FXML;
import network_rip.PacketForwarder;
import network_rip.Responder;
import network_core.Host;
import network_core.Packet;
import network_v2.Router;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController2 extends AbstractProtocolController {

	private Router self = new Router();
	private Responder responder = new Responder();
	private Host selfHost = new Host();
	private PacketForwarder packetForwarder = new PacketForwarder();

	@FXML
	private void connectToRouter() {
		startDaemonThread(this::runHostToRouterAccept);
	}

	@FXML
	private void requestConnection() {
		String targetIp = REQUEST_IP.getText();
		if (targetIp == null || targetIp.trim().isEmpty()) {
			cmd.setText("$- Please enter an IP address\n");
			return;
		}
		if (!self.isRunning()) {
			cmd.setText("$- Router is not running. Click 'On' first.\n");
			return;
		}
		cmd.setText("$- Connecting to " + targetIp + "...\n");
		startDaemonThread(() -> {
			self.requestConnection(targetIp.trim());
			Platform.runLater(() -> {
				String routing = CmdUtils.getSharedRoutingCMDBuilder().toString();
				String cmdText = CmdUtils.getSharedCMDBuilder().toString();
				cmd.setText(cmdText + "\n" + routing);
			});
		});
	}

	@FXML
	private void on() {
		if (self.isRunning()) {
			cmd.setText("$- Router is already running\n");
			return;
		}
		killCheck = false;

		try {
			self.init(NetworkConfig.RIP_ROUTER_PORT, NetworkConfig.RIP_HOST_PORT, NetworkConfig.RIP_PACKET_PORT);
		} catch (Exception e) {
			logger.error("Failed to start router", e);
			cmd.setText("$- FAILED to start router: " + e.getMessage() + "\n");
			return;
		}

		SharedLineCHART.percentage = 0;
		logger.info("RIP Router is on, waiting for connections");
		setLabelText(STATUS, "ON");
		setLabelText(NAME, "RIP Router");
		setLabelText(IP, NetworkConfig.getPrivateIp());

		AtomicInteger _seconds = new AtomicInteger(0);
		SharedLineCHART.seconds = _seconds;
		startClock();
		startDaemonThread(this::runAcceptTask);

		cmd.setText("$- RIP Router started on ports " + NetworkConfig.RIP_ROUTER_PORT
				+ ", " + NetworkConfig.RIP_HOST_PORT + ", " + NetworkConfig.RIP_PACKET_PORT + "\n"
				+ "$- Waiting for connections...\n");
	}

	@Override
	protected void onClockTick() {
		SharedLineCHART.percentage = ((((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
				/ 1024)) / 1024);
		if (SharedLineCHART.seconds != null) {
			SharedLineCHART.addEntry(SharedLineCHART.seconds.getAndIncrement(), SharedLineCHART.percentage);
		}
	}

	@FXML
	private void onHost() {
		if (!self.isRunning()) {
			cmd.setText("$- Router is not running. Click 'On' first.\n");
			return;
		}
		logger.info("Router is waiting for host connections");
		startDaemonThread(this::runHostAcceptTask);
	}

	@FXML
	public void sendTables() {
		if (!self.isRunning()) return;
		startDaemonThread(() -> {
			responder.broadcastTables(self);
			Platform.runLater(() -> cmd.setText(CmdUtils.getSharedCMDBuilder().toString()));
		});
	}

	@FXML
	public void receiveTable() {
		if (!self.isRunning()) return;
		if (UPDATE_BUTTON != null) UPDATE_BUTTON.setDisable(true);
		startDaemonThread(this::runTablesTask);
	}

	@FXML
	public void onListenPacket() {
		if (!self.isRunning()) {
			cmd.setText("$- Router is not running. Click 'On' first.\n");
			return;
		}
		startDaemonThread(this::runListenPacketTask);
	}

	@FXML
	public void onKill() {
		killCheck = true;
		stopClock();
		self.shutdown();

		// Clear CmdUtils buffers
		CmdUtils.getSharedCMDBuilder().setLength(0);
		CmdUtils.getSharedRoutingCMDBuilder().setLength(0);
		CmdUtils.getSharedRIPCMDBuilder().setLength(0);

		if (ON_HOST != null) ON_HOST.setDisable(false);
		if (UPDATE_BUTTON != null) UPDATE_BUTTON.setDisable(false);
		if (listView != null) listView.getItems().clear();
		if (cmd != null) cmd.setText("$- Router is OFF\n");

		setLabelText(STATUS, "OFF");
		setLabelText(NAME, "--");
		setLabelText(IP, "--");
		setLabelText(UP_TIME, "--");
		setLabelText(RAM, "--");
		setLabelText(MAC, "--");

		logger.info("RIP Router shut down");
	}

	// --- Background tasks ---

	private void runHostToRouterAccept() {
		if (killCheck || !self.isRunning()) return;

		SharedBarCHART.numberOfPacketReceivedByRouter = 0;
		SharedBarCHART.numberOfPacketSentByRouter = 0;
		long startTime = System.nanoTime();

		for (int i = 0; i < 10 && !killCheck; i++) {
			Packet packet = new Packet();
			try {
				packet.setSourceAddress(InetAddress.getByName(NetworkConfig.getPrivateIp()));
				packet.setPacketMessage("HELLO THIS IS A PACKET MESSAGE :)");
				packet.setDestinationAddress(InetAddress.getByName(DEST_IP.getText()));
				packetForwarder.requestHostConnection(packet);
			} catch (Exception e) {
				logger.error("Error creating or sending packet", e);
			}

			if (packetForwarder.acceptHostConnection(self)) {
				SharedBarCHART.numberOfPacketReceivedByRouter++;
				SharedBarCHART.numberOfPacketSentByRouter++;
			}
		}

		long endTime = System.nanoTime();
		double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
		SharedBarCHART.secondsOfReceiving = elapsedSeconds;
		SharedBarCHART.secondsOfSending = elapsedSeconds;
		SharedBarCHART.addReceiveData(SharedBarCHART.numberOfPacketReceivedByRouter, elapsedSeconds);
		SharedBarCHART.addSentData(SharedBarCHART.numberOfPacketSentByRouter, elapsedSeconds);
	}

	private void runHostAcceptTask() {
		if (!self.isRunning()) return;
		packetForwarder.acceptHostConnection(self);
		Platform.runLater(() -> cmd.setText(CmdUtils.getSharedCMDBuilder().toString()));
	}

	private void runAcceptTask() {
		while (!killCheck && self.isRunning()) {
			try {
				self.acceptConnections();
				Platform.runLater(() -> {
					String routing = CmdUtils.getSharedRoutingCMDBuilder().toString();
					String cmdText = CmdUtils.getSharedCMDBuilder().toString();
					cmd.setText(cmdText + "\n" + routing);
				});
			} catch (Exception e) {
				if (self.isRunning()) {
					logger.error("Error in accept connections loop", e);
				}
			}
		}
	}

	private void runListenPacketTask() {
		SharedBarCHART.numberOfPacketReceivedByRouter = 0;
		SharedBarCHART.numberOfPacketSentByRouter = 0;

		while (!killCheck && self.isRunning()) {
			if (packetForwarder.receiveAndForwardThePacket(self)) {
				SharedBarCHART.numberOfPacketReceivedByRouter++;
				SharedBarCHART.numberOfPacketSentByRouter++;
			}
		}
	}

	private void runTablesTask() {
		while (!killCheck && self.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			responder.receiveTables(self);
			Platform.runLater(() -> {
				if (listView != null) {
					listView.getItems().clear();
					listView.getItems().add(CmdUtils.getSharedRIPCMDBuilder().toString());
				}
			});
		}
	}
}
