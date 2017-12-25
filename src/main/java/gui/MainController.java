package gui;

import gui.utils.GUIUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import network.Host;
import network.Router;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController {

    private Router selfRouter;
    Host selfHost;
    /**
     * Router info Labels , all have default values..
     */
    @FXML
    private final Label NAME = new Label();
    @FXML
    private final Label STATUS = new Label();
    @FXML
    private final Label IP = new Label();
    @FXML
    private final Label UP_TIME = new Label();

    // it is actually the memory consumption of JVM
    @FXML
    private final Label MAC = new Label();

    @FXML
    private final Label RAM = new Label();

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private final TextArea cmd = new TextArea();

    @FXML
    private final Button HostButton = new Button();

    @FXML
    private final TextField ipInput = new TextField();

    @FXML
    private final TextField destinationIP = new TextField();

    @FXML
    private final TextArea ripCMD = new TextArea();

    @FXML
    private final Button button = new Button();

    public MainController() {

    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void hostConnect() {

    }

    @FXML
    private void ripSimulation() {
        ripCMD.appendText(": STARTING RIP SIMULATION ! \n ");
        ripCMD.appendText(": RECIEVING TABLES FROM DIRECT CONNECTION ... ....  \n ");
    }

    @FXML
    private void directConnection() {

        if (ipInput.getText().equals(" ")) {
            cmd.setText("INCORRECT FORMAT");
        }
        if (selfRouter.connectToRouter(ipInput.getText())) {
            cmd.appendText("\n");
            cmd.appendText(Router.routerTable.displayTable());
            cmd.appendText("\n");

        } else {
            cmd.appendText("\n " + selfRouter.getCMD());
        }
        // cmd.setText(Router.routerTable.displayTable());
    }

    /**
     * Set the default values and start listening to active connection also
     */
    @FXML
    private void setDefaulters() {

        STATUS.setText("ON");
        NAME.setText("CISCO XYZ 19-A");
        IP.setText(GUIUtils.getPrivateIp("wlo1"));

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            UP_TIME.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            RAM.setText(String.valueOf((float) (Runtime.getRuntime().freeMemory() / 1024) / 1024));
            MAC.setText(String
                    .valueOf((float) (((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)
                            / 1024)));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        selfRouter = new Router(NAME.getText(), 1999);
        // selfHost = new Host("Bilal", destinationIP.getText());

        cmd.appendText(": initializing ROUTER....");
        cmd.appendText(": setting default parameters..... \n ");
        cmd.appendText(": getting main.java.network stats... \n ");
        cmd.appendText("ROUTER " + NAME.getText() + " CREATED ");

        // start listening to active connections on seperate threads in the background
        startTask();
    }

    private void startTask() {
        Runnable task = this::runTheTask;
        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the main.java.gui exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }

    private void runTheTask() {
        try {
            while (true) {
                final String cmdTextString = selfRouter.initialize();
                Platform.runLater(() -> cmd.setText(cmdTextString + "\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
