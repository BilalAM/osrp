package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class Main extends Application {
    Button initializeButton;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String pathToFXML = "/Users/saifasif/projects/osrp/src/main/java/gui/osrp2.fxml";
            FileInputStream fileStream = new FileInputStream(pathToFXML);
            VBox box = (VBox) loader.load(fileStream);

            Scene scene = new Scene(box);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
