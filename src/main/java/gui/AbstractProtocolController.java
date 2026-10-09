package gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base controller for both RIP and OSRP protocol views.
 * Contains shared FXML fields and common threading/UI utilities.
 *
 * Note: Not all fields have corresponding fx:id in every FXML file.
 * Fields that may be absent are null-safe in all usages.
 */
public abstract class AbstractProtocolController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected volatile boolean killCheck = false;
    protected Timeline clock;

    // Fields present in both FXML files
    @FXML protected Button UPDATE_BUTTON;
    @FXML protected Button PACKET_BTN;
    @FXML protected Button ON_HOST;
    @FXML protected Button KILL_BTN;
    @FXML protected TextField REQUEST_IP;
    @FXML protected TextField DEST_IP;
    @FXML protected TextField packets;
    @FXML protected TextArea cmd;
    @FXML protected TextArea hostCmd;
    @FXML protected ListView listView;
    @FXML protected Label NAME;
    @FXML protected Label STATUS;
    @FXML protected Label IP;
    @FXML protected Label UP_TIME;
    @FXML protected Label MAC;
    @FXML protected Label RAM;

    /**
     * Starts a daemon background thread with the given task.
     */
    protected void startDaemonThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Safely sets text on a label (handles null for fields not in FXML).
     */
    protected void setLabelText(Label label, String text) {
        if (label != null) label.setText(text);
    }

    /**
     * Creates and starts the uptime/memory Timeline clock.
     * Subclasses can override onClockTick() to add protocol-specific logic.
     */
    protected void startClock() {
        if (clock != null) {
            clock.stop();
        }
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            setLabelText(UP_TIME, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            long freeMemMB = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long usedMemMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            setLabelText(RAM, freeMemMB + " MB");
            setLabelText(MAC, usedMemMB + " MB");

            onClockTick();
        }), new KeyFrame(Duration.seconds(1.0)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Stops the clock and cleans up resources.
     */
    protected void stopClock() {
        if (clock != null) {
            clock.stop();
            clock = null;
        }
    }

    /**
     * Hook for subclasses to add logic on each clock tick (e.g., line chart updates).
     */
    protected void onClockTick() {
        // Default no-op
    }
}
