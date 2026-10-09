package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SideBarController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SideBarController.class);

    @FXML
    private BorderPane borderPane;

    private Parent ripRoot;
    private Parent osrpRoot;
    private Parent benchmarkRoot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ripRoot = FXMLLoader.load(getClass().getResource("/RipMainFXML.fxml"));
            osrpRoot = FXMLLoader.load(getClass().getResource("/OsrpFXML.fxml"));
            benchmarkRoot = FXMLLoader.load(getClass().getResource("/VisualizationFXML.fxml"));
        } catch (Exception e) {
            logger.error("Failed to load FXML resources", e);
        }

        // Show RIP view by default on startup
        if (ripRoot != null) {
            borderPane.setCenter(ripRoot);
        }
    }

    @FXML
    public void ripUI(MouseEvent mouseEvent) {
        borderPane.setCenter(ripRoot);
    }

    @FXML
    public void osrpUI(MouseEvent mouseEvent) {
        borderPane.setCenter(osrpRoot);
    }

    @FXML
    public void benchmarkingUI(MouseEvent mouseEvent) {
        borderPane.setCenter(benchmarkRoot);
    }
}
