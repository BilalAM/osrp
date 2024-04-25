package benchmarking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedLineCHART {

    public static float percentage;
    public static AtomicInteger seconds;


    public static Map<Integer, Float > Tmap = new HashMap();
    public static ObservableMap<Integer,Float> OTmap = FXCollections.observableMap(Tmap);


    public static void addEntry(int second , float consumption){
        OTmap.put(second,consumption);
        Tmap.put(second,consumption);
    }


}
