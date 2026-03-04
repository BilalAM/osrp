package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SideBarFXML.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("OSRP - Routing Protocol Simulator");
            Scene scene = new Scene(root, 1366, 700);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Failed to start application", e);
        }
    }
}
