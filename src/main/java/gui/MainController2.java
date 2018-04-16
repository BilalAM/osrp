    package gui;

    import gui.utils.GUIUtils;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.scene.control.Button;
    import javafx.scene.control.TextArea;
    import javafx.scene.control.TextField;

    import javafx.scene.text.TextFlow;
    import network_v2.Responder;
    import network_v2.Router;
    import network_v2.Packet;
    import network_v2.Host;

    import java.net.InetAddress;

    public class MainController2 {

        private static Router self = new Router();
        private Responder responder = new Responder();
        private static Host selfHost = new Host();


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
        private TextArea ripCMD = new TextArea();

        @FXML
        private TextFlow richText;

        @FXML
        private TextArea hostCmd = new TextArea();

        @FXML
        private TextField DEST_IP = new TextField();

        @FXML
        private Button SEND_PCKT = new Button();

        /**
         * A host is connecting to a router , default ip is 192.168.15.120
         */
        @FXML
        private void connectToRouter() {
            // selfHost.connectToRouter(IPsHelper.getPrivateIp("wlo1"));

            // magical packet-nizer loop to create and send packets
            for (int i = 0; i < 5000; i++) {
                Packet packet = new Packet();
                try {
                    packet.setSourceAddress(InetAddress.getByName(GUIUtils.getPrivateIp("wlo1")));
                    packet.setPacketMessage("HELLO THIS IS A PACKET MESSAGE :)");
                    packet.setDestinationAddress(InetAddress.getByName(DEST_IP.getText()));
                    self.requestHostConnection(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hostCmd.setText(selfHost.getCmd());
                System.out.println("host is connected to router");
                onHost();
            }
        }


        /**
         * connecting to a seperate router based on ip
         */
        @FXML
        private void requestConnection() {
            self.requestConnection(REQUEST_IP.getText());
            cmd.setText(self.getCMD());
        }


        /**
         * turns on the router , router is listening to other router's request connections too
         */
        @FXML
        private void on() {
            System.out.println("ROUTER IS ON....WAITING FOR CONNECTIONS....");
            ON_HOST.disableProperty().setValue(true);
            startAcceptTask();
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
         * router is constantly listening for other routers that are sending their tables to it
         */
        @FXML
        public void receiveTable() {
            UPDATE_BUTTON.setDisable(true);
            startTablesTask();
        }

        @FXML
        public void onListenPacket(){
            Thread backGround = new Thread(){
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
            //while (true) {
            self.acceptHostConnection();
            Platform.runLater(() -> {
                cmd.setText("a host is here ! :3 ");
            });
            //}
        }



        private void runAcceptTask2(){
           while (true){
               try {
                   //self.recieveAndforwardPacket();
                   // Thread.sleep(100);
               }catch (Exception e){
                   e.printStackTrace();
               }
               self.receiveAndForwardThePacket();
                Platform.runLater(() -> {
                    cmd.setText("hello love :)");
                });
            }
        }
        private void runAcceptTask() {
            while (true) {
                try {
                    //Thread.sleep(10);

                    self.acceptConnections();
                    Platform.runLater(() -> {
                        cmd.setText(self.getCMD());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        private void runTablesTask() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                responder.recieveTables(self);
                Platform.runLater(() -> {
                    // cmd.setDisable(true);
                });
            }
        }
    }

