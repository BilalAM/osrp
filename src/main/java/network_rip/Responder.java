package network_rip;

import gui.utils.CmdUtils;
import network_v2.Router;
import network_v2.Table;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

          //  CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting to contact other routers to send tables too...\n");
            System.out.println("\n attempting to contact other routers to send tables too...");
            if(isEmptyList(router)){

              //  CmdUtils.getSharedRIPCMDBuilder().append("$- *** There Is No Router Currently Connected...\n");
                System.out.println("***  There is no router currently connected ***");
            }
            for(Socket otherRouter : router.getConnectionHistory()) {

            //    CmdUtils.getSharedRIPCMDBuilder().append("$- Connected Router Found !\n");
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
               // CmdUtils.getSharedRIPCMDBuilder().append("$- *** There Is No Router Currently Connected *** !\n");
                System.out.println("***  There is no router currently connected ***");
            }
            for (Socket otherRouter : router.getConnectionHistory()){

               // CmdUtils.getSharedRIPCMDBuilder().append("$- Connected Router Found !\n");
                System.out.println("connected router found....");
                    //rough estimate to check if there is any data available......very important check !
                    if (otherRouter.getInputStream().available() > 0) {
                       _recieveTables(otherRouter,router);
                    } else {
                       // CmdUtils.getSharedRIPCMDBuilder().append("$- ROUTER HAVING IP  "+ otherRouter.getInetAddress() + " HAS NOT SENT TABLE AS OF YET...TRY AGAIN LATER..!\n");
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
    private void _recieveTables(Socket otherRouter , Router selfRouter) throws Exception {
        Updater updater = new Updater();
        ObjectInputStream input = new ObjectInputStream(otherRouter.getInputStream());

        CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To Receive Table From The Stream...\n");
        System.out.println("attempting to receive table from stream");
      //  if (input.readObject() instanceof Table) {
            Table t = (Table) input.readObject();
            tables.add(t);
        CmdUtils.getSharedRIPCMDBuilder().append("$- A table Is Received And Added To The Inner List\n");
        System.out.println("a table is received and added to the inner list...");
            t.displayTable();
        CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To UPDATE Our Table..\n");
        System.out.println("ATTEMPTING TO UPDATE OUR TABLE...");
            updater.Update(selfRouter.getTable(), t);
        CmdUtils.getSharedRIPCMDBuilder().append("$- Our New Table With A UPDATED Entry\n");
        System.out.println("OUR UPDATED TABLE WITH A NEW ENTRY....");
            selfRouter.getTable().displayTable();
            System.out.println("IS SOCKET CLOSED ? (WITH STREAM) ? " + otherRouter.isClosed());
      //  }
    }


    /**
     * helper method of sendTables() logic
     * @param fromRouter
     * @param toRouter
     * @throws Exception
     */
    private void sendTable(Router fromRouter , Socket toRouter) throws Exception{
        ObjectOutputStream outputStream = new ObjectOutputStream(toRouter.getOutputStream());
        CmdUtils.getSharedRIPCMDBuilder().append("$- Attempting To Write A Table To The Stream..\n");
        System.out.println("attempting to write object to stream...");
        outputStream.writeUnshared(fromRouter.getTable());
        outputStream.flush();
        CmdUtils.getSharedRIPCMDBuilder().append("$- Table Sent To The Stream..\n");
        System.out.println("table sent to stream...");
    }

    public static boolean isEmptyList(Router router){
            return router.getConnectionHistory().isEmpty();
    }

    public List<Table> getTables(){
        return tables;
    }

}

