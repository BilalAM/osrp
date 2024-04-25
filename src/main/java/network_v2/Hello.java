package network_v2;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class Hello {
   static Map<String,String> map = new HashMap<String,String>();
   static ObservableMap<String,String> observableMap = FXCollections.observableMap(map);
   static{
       observableMap.addListener(new MapChangeListener() {
           @Override
           public void onChanged(MapChangeListener.Change change) {
               System.out.println("Detected a change! " + change.getValueAdded());
           }
       });
   }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

//        Map<String,String> map = new HashMap<String,String>();

        // Now add observability by wrapping it with ObservableList.

        // Changes to the observableMap WILL be reported.
        add("a","bb");
        add("a1","aaa");
        //method1();
        add("c","aaa");
    }

    static void method1() {


    }

    static void add(String a , String b){
        observableMap.put(a,a);
    }

    static void method2() {
        try {
            while(true){
                System.out.println("test2");
                Thread.currentThread().sleep(1000);
            }
        } catch (InterruptedException e) {
        }
    }

    static void method3() {
        try {
            while(true){
                System.out.println("test3");
                Thread.currentThread().sleep(1000);
            }
        } catch (InterruptedException e) {
        }
    }

}
