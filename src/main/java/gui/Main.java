package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;

public class Main extends Application {
    Button initializeButton;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String property = System.getProperties().getProperty("path.to.file", "/home/bilalam/Documents/git/osrp/src/main/java/gui/osrp2.fxml");
            String pathToFXML = property;
            FileInputStream fileStream = new FileInputStream(pathToFXML);
            VBox box = (VBox) loader.load(fileStream);

            Scene scene = new Scene(box);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
