package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SideBarController implements Initializable {


    @FXML
    BorderPane borderPane;

    Parent ripRoot;
     Parent osrpRoot;
     Parent becnhmarkRoot;
    {
        try {
            ripRoot = FXMLLoader.load(getClass().getResource("/RipMainFXML.fxml"));
            osrpRoot = FXMLLoader.load(getClass().getResource("/OsrpFXML.fxml"));
            becnhmarkRoot = FXMLLoader.load(getClass().getResource("/VisualizationFXML.fxml"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void ripUI(MouseEvent mouseEvent) {
            borderPane.setCenter(ripRoot);
    }

    public void osrpUI(MouseEvent mouseEvent) {
        borderPane.setCenter(osrpRoot);
    }

    public void benchmarkingUI(MouseEvent mouseEvent) {
        borderPane.setCenter(becnhmarkRoot);
    }

    private void loadUI(String ui , String controller){

       // borderPane.setCenter(root);
    }
}
