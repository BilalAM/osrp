package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);

    }

    // **********************************************
    // CHANGE THE path of file TO sample.fxml
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SideBarFXML.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("OSRP By Bilal , Neha and Umer");
            primaryStage.setScene(new Scene(root, 1166, 568));
            primaryStage.show();
            PrintStream stream = new PrintStream("logging.txt");
          //  System.setOut(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
