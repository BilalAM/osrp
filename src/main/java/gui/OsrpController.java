package gui;

import gui.utils.CmdUtils;
import javafx.application.Platform;
import network_core.NetworkConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import network_osrp.HardwareBroadcaster2;
import network_osrp.OsrpPacketForwarder;
import network_osrp.OsrpResponder;
import network_osrp.OsrpRouter;
import network_core.Host;

import benchmarking.SharedBarCHART;

public class OsrpController extends AbstractProtocolController {

	private OsrpRouter self = new OsrpRouter();
	private OsrpResponder responder = new OsrpResponder();
	private Host selfHost = new Host();
	private OsrpPacketForwarder packetForwarder = new OsrpPacketForwarder();
	private HardwareBroadcaster2 broad2 = new HardwareBroadcaster2();

	@FXML private Button SEND_HARDWARE;
	@FXML private Button UPDATE_HARDWARE;

	@FXML
	public void onRouter() {
		if (self.isRunning()) {
			cmd.setText("$- OSRP Router is already running\n");
			return;
		}
		killCheck = false;

		try {
			self.initOsrp();
		} catch (Exception e) {
			logger.error("Failed to start OSRP router", e);
			cmd.setText("$- FAILED to start OSRP router: " + e.getMessage() + "\n");
			return;
		}

		logger.info("OSRP router is on, waiting for connections");
		setLabelText(STATUS, "ON");
		setLabelText(NAME, "OSRP Router");
		setLabelText(IP, NetworkConfig.getPrivateIp());
		startClock();
		startDaemonThread(this::runAcceptConnectionTask);

		cmd.setText("$- OSRP Router started on ports " + NetworkConfig.OSRP_ROUTER_PORT
				+ ", " + NetworkConfig.OSRP_HOST_PORT + ", " + NetworkConfig.OSRP_PACKET_PORT
				+ ", " + NetworkConfig.OSRP_HARDWARE_PORT + "\n"
				+ "$- Waiting for connections...\n");
	}

	@FXML
	public void requestRouterConnection() {
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
				String routing = CmdUtils.osrpInstance().getRoutingCMDBuilder().toString();
				String cmdText = CmdUtils.osrpInstance().getCMDBuilder().toString();
				cmd.setText(cmdText + "\n" + routing);
			});
		});
	}

	@FXML
	public void receiveTheTables() {
		if (!self.isRunning()) return;
		startDaemonThread(this::runReceiveTablesTask);
	}

	@FXML
	public void sendTheTables() {
		if (!self.isRunning()) return;
		startDaemonThread(() -> {
			responder.broadcastTables(self);
			Platform.runLater(() -> cmd.setText(CmdUtils.osrpInstance().getCMDBuilder().toString()));
		});
	}

	@FXML
	private void sendHardwareInfo() {
		if (!self.isRunning()) return;
		startDaemonThread(() -> broad2.broadCastInfo(self));
	}

	@FXML
	public void listenPackets() {
		if (!self.isRunning()) {
			cmd.setText("$- Router is not running. Click 'On' first.\n");
			return;
		}
		startDaemonThread(this::runListenPacketTask);
	}

	@FXML
	private void updateHardwareInfo() {
		if (!self.isRunning()) return;
		startDaemonThread(this::runUpdatingHardwareTask);
	}

	@FXML
	public void onKill() {
		killCheck = true;
		stopClock();
		self.shutdown();

		CmdUtils.osrpInstance().getCMDBuilder().setLength(0);
		CmdUtils.osrpInstance().getRoutingCMDBuilder().setLength(0);
		CmdUtils.osrpInstance().getRIPCMDBuilder().setLength(0);

		if (ON_HOST != null) ON_HOST.setDisable(false);
		if (UPDATE_BUTTON != null) UPDATE_BUTTON.setDisable(false);
		if (listView != null) listView.getItems().clear();
		if (cmd != null) cmd.setText("$- OSRP Router is OFF\n");

		setLabelText(STATUS, "OFF");
		setLabelText(NAME, "--");
		setLabelText(IP, "--");
		setLabelText(UP_TIME, "--");
		setLabelText(RAM, "--");
		setLabelText(MAC, "--");

		logger.info("OSRP Router shut down");
	}

	// --- Background tasks ---

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

	private void runUpdatingHardwareTask() {
		while (!killCheck && self.isRunning()) {
			try {
				Thread.sleep(1000);
				broad2.receiveInfo(self);
			} catch (Exception e) {
				if (self.isRunning()) {
					logger.error("Error updating hardware info", e);
				}
			}
		}
	}

	private void runReceiveTablesTask() {
		while (!killCheck && self.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			responder.receiveTables(self);
		}
	}

	private void runAcceptConnectionTask() {
		while (!killCheck && self.isRunning()) {
			try {
				self.acceptConnections();
				Platform.runLater(() -> {
					String routing = CmdUtils.osrpInstance().getRoutingCMDBuilder().toString();
					String cmdText = CmdUtils.osrpInstance().getCMDBuilder().toString();
					cmd.setText(cmdText + "\n" + routing);
				});
			} catch (Exception e) {
				if (self.isRunning()) {
					logger.error("Error accepting OSRP connection", e);
				}
			}
		}
	}
}
