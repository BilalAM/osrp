package gui;

import benchmarking.SharedBarCHART;
import benchmarking.SharedLineCHART;
import gui.utils.CmdUtils;
import gui.utils.GUIUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import network_rip.PacketForwarder;
import network_rip.Responder;
import network_v2.Host;
import network_v2.Packet;
import network_v2.Router;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * implement the timer logics here directly over the methods...
 */

public class MainController2 {

	private static Router self = new Router();
	private Responder responder = new Responder();
	private static Host selfHost = new Host();
	private PacketForwarder packetForwarder = new PacketForwarder();
	private boolean killCheck = false;

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

	/**
	 * A host is connecting to a router , default ip is 192.168.15.120
	 */
	@FXML
	private void connectToRouter() {
		// magical packet-nizer loop to create and send packets
		startHostToRouterAccept();
	}

	private void startHostToRouterAccept() {
		Runnable runn = new Runnable() {
			@Override
			public void run() {
				runHostToRouterAccept();
			}
		};

		Thread thread = new Thread(runn);
		thread.setDaemon(true);
		thread.start();
	}

	private void runHostToRouterAccept() {

		// reset the shared variables

		if (killCheck == false) {

			SharedBarCHART.numberOfPacketReceivedByRouter = 0;
			SharedBarCHART.numberOfPacketSentByRouter = 0;
			long tempReceiveSecond = System.nanoTime();
			long tempSentSeconds = System.nanoTime();

			for (int i = 0; i < 10; i++) {
				Packet packet = new Packet();
				try {
					packet.setSourceAddress(
							InetAddress.getByName(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a")));
					packet.setPacketMessage("HELLO THIS IS A PACKET MESSAGE :)");
					packet.setDestinationAddress(InetAddress.getByName(DEST_IP.getText()));
					packetForwarder.requestHostConnection(packet);
					Platform.runLater(() -> {
						listView.getItems().clear();
						// listView.getItems().add(CmdUtils.getSharedCMDBuilder().toString());
						// listView.getItems().clear();
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("host is connected to router");
				if (packetForwarder.acceptHostConnection(self)) {
					// increment the packets received and sent variable
					SharedBarCHART.numberOfPacketReceivedByRouter++;
					SharedBarCHART.numberOfPacketSentByRouter++;
				}
				// onHost();
			}
			long tempReceiveSecond2 = System.nanoTime();
			long tempSentSecond2 = System.nanoTime();

			double Rseconds = ((double) (tempReceiveSecond - tempReceiveSecond2)) / 1000000000.0;
			double Sseconds = ((double) (tempSentSeconds - tempSentSecond2)) / 1000000000.0;

			SharedBarCHART.secondsOfReceiving = Rseconds;
			SharedBarCHART.secondsOfSending = Sseconds;

			SharedBarCHART.addRecieveData(SharedBarCHART.numberOfPacketReceivedByRouter,
					SharedBarCHART.secondsOfReceiving);
			SharedBarCHART.addSentData(SharedBarCHART.numberOfPacketSentByRouter, SharedBarCHART.secondsOfSending);

		}
	}

	/**
	 * connecting to a seperate router based on ip
	 */
	@FXML
	private void requestConnection() {
		self.requestConnection(REQUEST_IP.getText());
		// routingTableListView.getItems().add(self.getRoutingTableCMD());
		cmd.setText(CmdUtils.getSharedRoutingCMDBuilder().toString());
	}

	/**
	 * turns on the router , router is listening to other router's request
	 * connections too
	 */
	@FXML
	private void on() {
		killCheck = false;
		if (killCheck == false) {
			// variables for the dynamic line chart memory consumption
			double totalMemory = ((Runtime.getRuntime().freeMemory() / 1024) / 1024);
			double usageMemory = ((((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024))
					/ 1024);
			SharedLineCHART.percentage = 0;

			System.out.println("ROUTER IS ON....WAITING FOR CONNECTIONS....");
			// ON_HOST.disableProperty().setValue(true);
			STATUS.setText("ON");
			NAME.setText("CISCO XYZ 19-A");
			IP.setText(GUIUtils.getPrivateIp("wlxa0f3c12c7d2a"));

			AtomicInteger _seconds = new AtomicInteger(0);
			// Unsafe.getUnsafe().addressSize();

			Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
				UP_TIME.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
				RAM.setText(String.valueOf((float) (Runtime.getRuntime().freeMemory() / 1024) / 1024));
				MAC.setText(String.valueOf(
						(float) (((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)
								/ 1024)));

				// for line chart
				// final float usagePercentage = (float) ((totalMemory / usageMemory) / 100);
				SharedLineCHART.percentage = ((((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
						/ 1024)) / 1024);
				SharedLineCHART.seconds = _seconds;
				SharedLineCHART.addEntry(SharedLineCHART.seconds.getAndIncrement(), SharedLineCHART.percentage);
			}), new KeyFrame(Duration.seconds(1.2)));
			clock.setCycleCount(Animation.INDEFINITE);
			clock.play();

			startAcceptTask();
		}
	}

	/**
	 * router is listening for other HOSTS connections too
	 */
	@FXML
	private void onHost() {
		System.out.println("ROUTER IS WAITING FOR HOST ALSO....");
		startHostAcceptTask();
	}

	/**
	 * router sends the table to all of the router in its list
	 */
	@FXML
	public void sendTables() {
		// Responder responder = new Responder();
		responder.broadcastTables(self);
	}

	/**
	 * router is constantly listening for other routers that are sending their
	 * tables to it
	 */
	@FXML
	public void receiveTable() {
		UPDATE_BUTTON.setDisable(true);
		startTablesTask();
	}

	@FXML
	public void onListenPacket() {
		Thread backGround = new Thread() {
			@Override
			public void run() {
				runAcceptTask2();
			}
		};
		backGround.setDaemon(true);
		backGround.start();
	}

	// MULTITHREADED OPERATIONS FUNCTIONALITIES

	private void startHostAcceptTask() {
		Runnable task = () -> runHostAcceptTask();

		Thread backGround = new Thread(task);
		backGround.setDaemon(true);
		backGround.start();
	}

	private void startAcceptTask() {
		// Run the task in a background thread
		Thread backgroundThread = new Thread() {
			@Override
			public void run() {
				runAcceptTask();
			}
		};
		// Terminate the running thread if the main.java.gui exits
		backgroundThread.setDaemon(true);
		backgroundThread.start();

	}

	private void startTablesTask() {

		Runnable task = () -> runTablesTask();

		// Run the task in a background thread
		Thread backgroundThread = new Thread(task);
		// Terminate the running thread if the main.java.gui exits
		backgroundThread.setDaemon(true);
		// Start the thread
		backgroundThread.start();
	}

	private void runHostAcceptTask() {
		// while (true) {
		packetForwarder.acceptHostConnection(self);
		Platform.runLater(() -> {
			cmd.setText(CmdUtils.getSharedCMDBuilder().toString());
		});
		// }
	}

	private void runAcceptTask2() {

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
				if (packetForwarder.receiveAndForwardThePacket(self)) {
					packetCheck = true;
					SharedBarCHART.numberOfPacketReceivedByRouter++;
					SharedBarCHART.numberOfPacketSentByRouter++;
					
				}
				Platform.runLater(() -> {
					//cmd.setText("hello ");
				});
			} else {
				break;
			}
		}

	}

	private void runAcceptTask() {

		while (true) {
			if (killCheck == false) {
				try {
					// Thread.sleep(10);

					self.acceptConnections();
					Platform.runLater(() -> {
						// listView.getItems().add(CmdUtils.getSharedCMDBuilder().toString());
						// listView.getItems().clear();
						cmd.setText(CmdUtils.getSharedRoutingCMDBuilder().toString());
						// cmd.setText(self.getCMD());
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
	}

	private void runTablesTask() {

		while (true) {
			if (killCheck == false) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				responder.recieveTables(self);
				Platform.runLater(() -> {
					 listView.getItems().clear();
					 listView.getItems().add(CmdUtils.getSharedRIPCMDBuilder().toString());
				});
			} else {
				break;
			}
		}
	}

	// kill all the processes , kill all connections , clear all routing tables and
	// stop all processes and stuff
	@FXML
	public void onKill() {
		killCheck = true;

		// first enable the buttons
		ON_HOST.setDisable(false);
		UPDATE_BUTTON.setDisable(false);

		// clear the Router
		self.getTable().getEntries().clear();

		// clear the connection history
		self.getConnectionHistory().clear();

		// clear the cmd displayes
		ripCMD.clear();
		listView.getItems().clear();
		cmd.clear();
		cmd.setText("*** ROUTER IS OFF CURRENTLY *** ");

		// display the off status
		System.out.println("** ROUTER IS OFF ** ");

	}
}
