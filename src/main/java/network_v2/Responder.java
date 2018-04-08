package network_v2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Responder {

    private static List<Table> tables = new ArrayList<>();

    /**
     * ATTEMPTS to 'broadcast' one by one the outputStreams() of 'other sockets' currently in our list and writeObject() OUR table.
     * <br/>
     * @param router : Our router (current machine)
     */
    public void broadcastTables(Router router){
        try{
            System.out.println("\n attempting to contact other routers to send tables too...");
            if(isEmptyList(router)){

                System.out.println("***  There is no router currently connected ***");
            }
            for(Socket otherRouter : router.getConnectionHistory()) {
                    System.out.println("connected router found...");
                    sendTable(router,otherRouter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * ATTEMPTS to receive one by one the inputStreams() of 'other sockets' currently in our list and readObject() the tables.
     * <br/>
     * The <b><code>( otherRouter.getInputStream().available() > 0 ) </code> </b>is to check if the stream has currently data on it ,
     * if it has then read it, else we suppose the router or socket has not sent any data as of yet
     * @param router : Our router (current machine)
     */
    public void recieveTables(Router router){
        try{
            if(isEmptyList(router)){
                System.out.println("***  There is no router currently connected ***");
            }
            for (Socket otherRouter : router.getConnectionHistory()){
                    System.out.println("connected router found....");
                    //rough estimate to check if there is any data available......very important check !
                    if (otherRouter.getInputStream().available() > 0) {
                       _recieveTables(otherRouter,router);
                    } else {
                        System.out.println("ROUTER HAVING IP " + otherRouter.getInetAddress() + " HAS NOT SENT TABLE AS OF YET...TRY AGAIN LATER..!");
                    }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * helper method of recieveTables logic..
     * @param otherRouter
     * @throws Exception : POSSIBLE EXCEPTIONS : Connection reset , SocketNotFound or StreamCorrupted
     */
    private void _recieveTables(Socket otherRouter , Router selfRouter) throws Exception{
        Updater updater = new Updater();
        ObjectInputStream input = new ObjectInputStream(otherRouter.getInputStream());
        System.out.println("attempting to receive table from stream");
        Table t = (Table) input.readObject();
        tables.add(t);
        System.out.println("a table is received and added to the inner list...");
        t.displayTable();
        System.out.println("ATTEMPTING TO UPDATE OUR TABLE...");
        updater.Update(selfRouter.getTable(),t);
        System.out.println("OUR UPDATED TABLE WITH A NEW ENTRY....");
        selfRouter.getTable().displayTable();
        System.out.println("IS SOCKET CLOSED ? (WITH STREAM) ? " + otherRouter.isClosed());
    }


    /**
     * helper method of sendTables() logic
     * @param fromRouter
     * @param toRouter
     * @throws Exception
     */
    private void sendTable(Router fromRouter , Socket toRouter) throws Exception{
        ObjectOutputStream outputStream = new ObjectOutputStream(toRouter.getOutputStream());
        System.out.println("attempting to write object to stream...");
        outputStream.writeUnshared(fromRouter.getTable());
        outputStream.flush();
        System.out.println("table sent to stream...");
    }

    public static boolean isEmptyList(Router router){
            return router.getConnectionHistory().isEmpty();
    }

    public List<Table> getTables(){
        return tables;
    }

}

