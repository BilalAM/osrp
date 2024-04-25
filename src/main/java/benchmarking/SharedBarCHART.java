package benchmarking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class SharedBarCHART {
    public static int numberOfPacketSentByRouter;
    public static int numberOfPacketReceivedByRouter;
    public static double secondsOfSending;
    public static double secondsOfReceiving;

    public static Map<Double , Integer> Rmap = new HashMap();
    public static Map<Double,Integer> Smap = new HashMap();

    public static ObservableMap<Double,Integer> observableRMap = FXCollections.observableMap(Rmap);
    public static ObservableMap<Double,Integer> observableSMap = FXCollections.observableMap(Smap);


    public static void addRecieveData(int packets , double seconds){
        observableRMap.put(seconds,packets);
        Rmap.put(seconds,packets);
    }

    public static void addSentData(int packets , double seconds){
        observableSMap.put(seconds,packets);
        Smap.put(seconds,packets);

    }

}
