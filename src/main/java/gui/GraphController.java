package gui;

import benchmarking.SharedBarCHART;
import benchmarking.SharedLineCHART;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class GraphController implements Initializable {

    @FXML
    private BarChart myChart;

    @FXML
    private BarChart myChart2;


    @FXML
    private LineChart myChart3;

    XYChart.Series series = new XYChart.Series();


    // non static blocks for listening and updating the graphs (event based)
    {

        SharedBarCHART.observableRMap.addListener(new MapChangeListener<Double, Integer>() {
            @Override
            public void onChanged(Change<? extends Double, ? extends Integer> change) {
                XYChart.Series series = new XYChart.Series();
                String a = String.valueOf(SharedBarCHART.secondsOfReceiving);
                Integer b = SharedBarCHART.numberOfPacketReceivedByRouter;
                XYChart.Data<String, Integer> data = new XYChart.Data<>(a, b);
                series.getData().add(data);
                Platform.runLater(() -> {
                    myChart.getData().addAll(series);
                });
            }
        });

        SharedBarCHART.observableSMap.addListener(new MapChangeListener<Double, Integer>() {
            @Override
            public void onChanged(Change<? extends Double, ? extends Integer> change) {
                XYChart.Series series = new XYChart.Series();
                String a = String.valueOf(SharedBarCHART.secondsOfSending);
                Integer b = SharedBarCHART.numberOfPacketSentByRouter;
                XYChart.Data<String, Integer> data = new XYChart.Data<>(a, b);
                series.getData().add(data);
                Platform.runLater(() -> {
                    myChart2.getData().addAll(series);
                });
            }
        });

        SharedLineCHART.OTmap.addListener(new MapChangeListener<Integer, Float>() {
            @Override
            public void onChanged(Change<? extends Integer, ? extends Float> change) {
               // XYChart.Series series = new XYChart.Series();
                String a = String.valueOf(SharedLineCHART.seconds.get());
                Float b = SharedLineCHART.percentage;
                XYChart.Data<String,Float> data = new XYChart.Data<>(a,b);
               series.getData().add(data);
                Platform.runLater(() -> {
                   //series.getData().clear();
//                    myChart3.getData().addAll(series);
                });
            }
        });
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
       myChart.setLegendVisible(false);
       myChart2.setLegendVisible(false);
       myChart3.setLegendVisible(false);
       startGraphTask();
    }

    @FXML
    public void onStart(){
       startGraphTask();
    }

    private void startGraphTask(){
        Runnable runTask = new Runnable() {
            @Override
            public void run() {
                runStartTask();
            }
        };

        Thread backGround = new Thread(runTask);
        backGround.setDaemon(true);
        backGround.start();
    }

    private void runStartTask(){
                Platform.runLater(() -> {
                    myChart3.getData().add(series);
                });
        }
    }