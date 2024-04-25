package gui;

import gui.utils.GUIUtils;
import gui.utils.OsrpCmdUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import network_osrp.HardwareBroadcaster;
import network_osrp.HardwareBroadcaster2;
import network_osrp.OsrpPacketForwarder;
import network_osrp.OsrpResponder;
import network_osrp.OsrpRouter;
import network_rip.PacketForwarder;
import network_v2.Host;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import benchmarking.SharedBarCHART;

public class OsrpController {

	private static OsrpRouter self = new OsrpRouter();
	private OsrpResponder responder = new OsrpResponder();
	private static Host selfHost = new Host();
	private OsrpPacketForwarder packetForwarder = new OsrpPacketForwarder();
	private HardwareBroadcaster hardwareBroadcaster = new HardwareBroadcaster();
	private boolean killCheck = false;

	private HardwareBroadcaster2 broad2 = new HardwareBroadcaster2();

	@FXML
	private Button CONNECT_BUTTON = new Button();

	@FXML
	private Button ON_BUTTON = new Button();

	@FXML
	private Button UPDATE_BUTTON = new Button();

	@FXML
	private Button PACKET_BTN = new Button();

	@FXML
	private Button ON_HOST = new Button();

	@FXML
	private TextField REQUEST_IP = new TextField();

	@FXML
	private TextArea cmd = new TextArea();

	@FXML
	private ListView listView = new ListView();

	@FXML
	private TextArea ripCMD = new TextArea();

	@FXML
	private TextFlow richText;

	@FXML
	private TextArea hostCmd = new TextArea();

	@FXML
	private TextField DEST_IP = new TextField();

	@FXML
	private Button SEND_PCKT = new Button();

	@FXML
	private Label NAME = new Label();

	@FXML
	private Label STATUS = new Label();

	@FXML
	private Label IP = new Label();

	@FXML
	private Label UP_TIME = new Label();

	// it is actually the memory consumption of JVM
	@FXML
	private Label MAC = new Label();

	@FXML
	private Label RAM = new Label();

	@FXML
	private TextField packets = new TextField();

	@FXML
	private Button KILL_BTN = new Button();

	@FXML
	private Button SEND_HARDWARE = new Button();

	@FXML
	private Button UPDATE_HARDWARE = new Button();

	/**
	 * A host is connecting to a router , default ip is 192.168.15.120
	 */

	// THE @FXML METHODS
	@FXML
	public void onRouter() {
		System.out.println("OSRP ROUTER IS ON....WAITING FOR CONNECTIONS....");
		// ON_HOST.disableProperty().setValue(true);
		STATUS.setText("ON");
		NAME.setText("CISCO XYZ 19-A");
		IP.setText(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a"));

		AtomicInteger _seconds = new AtomicInteger(0);
		// Unsafe.getUnsafe().addressSize();

		Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			UP_TIME.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			RAM.setText(String.valueOf((float) (Runtime.getRuntime().freeMemory() / 1024) / 1024));
			MAC.setText(String
					.valueOf((float) (((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)
							/ 1024)));

			// for line chart
			// final float usagePercentage = (float) ((totalMemory / usageMemory) / 100);
			/*
			 * SharedRIPLineCHART.percentage = ((((Runtime.getRuntime().totalMemory() -
			 * Runtime.getRuntime().freeMemory()) / 1024)) / 1024);
			 * SharedRIPLineCHART.seconds = _seconds;
			 * SharedRIPLineCHART.addEntry(SharedRIPLineCHART.seconds.getAndIncrement(),
			 * SharedRIPLineCHART.percentage);
			 */
		}), new KeyFrame(Duration.seconds(1.2)));
		clock.setCycleCount(Animation.INDEFINITE);
		clock.play();

		startAcceptConnectionTask();
	}

	@FXML
	public void requestRouterConnection() {
		self.requestConnection(REQUEST_IP.getText());
		cmd.setText(OsrpCmdUtils.getSharedRoutingCMDBuilder().toString());

	}

	@FXML
	public void recieveTheTables() {
		startRecieveTablesTask();
	}

	@FXML
	public void sendTheTables() {
		// Responder responder = new Responder();
		responder.broadcastTables(self);
	}

	@FXML
	private void sendHardwareInfo() {
			//startSendingHardwareTask();
				broad2.broadCastInfo(self);

	}

	@FXML
	public void listenPackets() {
		startListenPacketTask();
	}

	@FXML
	private void updateHardwareInfo() {
		startUpdatingHardwareTask();
	}

	// IMPLEMENTATION OF THE MULTITHREADED METHODS

	public void startListenPacketTask() {
		Runnable runn = () -> runListenPacketTask();
		Thread thread = new Thread(runn);
		thread.setDaemon(true);
		thread.start();
	}

	public void runListenPacketTask() {

		SharedBarCHART.numberOfPacketReceivedByRouter = 0;
		SharedBarCHART.numberOfPacketSentByRouter = 0;
		long tempReceiveSecond = System.nanoTime();
		long tempSentSeconds = System.nanoTime();
		boolean packetCheck = true;

		while (true) {
			if (killCheck == false) {
				try {
					// Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (packetForwarder.recieveAndForwardThePacket(self)) {
					packetCheck = true;
					SharedBarCHART.numberOfPacketReceivedByRouter++;
					SharedBarCHART.numberOfPacketSentByRouter++;

				}
				Platform.runLater(() -> {
					cmd.setText("hello love :)");
				});
			} else {
				break;
			}
		}

	}

	private void startUpdatingHardwareTask() {
		Runnable runnable = () -> runUpdatingHardwareTask();
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
	}

	private void runUpdatingHardwareTask() {
		while (true) {
			try {
				Thread.sleep(1000);
				broad2.recieveInfo(self);
				Platform.runLater(() -> {
					// gui here
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startSendingHardwareTask() {
		Runnable runTask = () -> runSendingHardwareTask();
		Thread thread = new Thread(runTask);
		thread.setDaemon(true);
		thread.start();
	}

	private void runSendingHardwareTask() {
		//for (int i = 0; i < 2; i++) {
			try {
				//Thread.sleep(1000);
				// hardwareBroadcaster.broadcastHardwareInformation(self);
				broad2.broadCastInfo(self);
				Platform.runLater(() -> {
					// gui here
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}


	private void startRecieveTablesTask() {
		Runnable runTask = () -> runRecieveTablesTask();
		Thread thread = new Thread(runTask);
		thread.setDaemon(true);
		thread.start();
	}

	private void runRecieveTablesTask() {
		while (true) {
			if (killCheck == false) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				responder.recieveTables(self);
				Platform.runLater(() -> {
					// listView.getItems().clear();
					// listView.getItems().add(CmdUtils.getSharedRIPCMDBuilder().toString());
				});
			} else {
				break;
			}
		}
	}

	private void startAcceptConnectionTask() {
		Runnable runTask = () -> runAcceptConnectionTask();
		Thread acceptThread = new Thread(runTask);
		acceptThread.setDaemon(true);
		acceptThread.start();
	}

	private void runAcceptConnectionTask() {
		while (true) {
			try {
				self.acceptConnections();
				Platform.runLater(() -> {
					cmd.setText(OsrpCmdUtils.getSharedRoutingCMDBuilder().toString());

				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
