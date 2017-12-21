package gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextArea;

import javafx.scene.layout.VBox;

public class Main extends Application {
	Button initializeButton;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			String pathToFXML = "/home/bilalam/Documents/new workspace/OSRP/src/gui/osrp2.fxml";
			FileInputStream fileStream = new FileInputStream(pathToFXML);
			VBox box = (VBox) loader.load(fileStream);

			Scene scene = new Scene(box);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		 launch(args);
		
	}

}
