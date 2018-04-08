    package gui;

    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.scene.control.Button;
    import javafx.scene.control.TextArea;
    import javafx.scene.control.TextField;
    import network_v2.Responder;
    import network_v2.Router;

    import java.lang.management.PlatformManagedObject;

    public class Controller {


        private static Router self = new Router();
        private Responder responder = new Responder();


        @FXML
        private Button CONNECT_BUTTON = new Button();

        @FXML
        private Button ON_BUTTON = new Button();

        @FXML
        private Button UPDATE_BUTTON = new Button();

        @FXML
        private TextField REQUEST_IP = new TextField();

        @FXML
        private TextArea cmd = new TextArea();

        @FXML
        private void requestConnection(){
            self.requestConnection(REQUEST_IP.getText());
        }


        @FXML
        private void on(){
            System.out.println("ROUTER IS ON....WAITING FOR CONNECTIONS....");
            startTask();
           // startTask2();
        }

        @FXML
        public void sendTables() {
           // Responder responder = new Responder();
            responder.broadcastTables(self);
        }

        @FXML
        public void receiveTable(){
            UPDATE_BUTTON.setDisable(true);
            startTask2();
        }





        private void startTask() {

            Runnable task = () -> runTask();

            // Run the task in a background thread
            Thread backgroundThread = new Thread(task);
            // Terminate the running thread if the main.java.gui exits
            backgroundThread.setDaemon(true);
            // Start the thread
            backgroundThread.start();
        }
        private void startTask2() {

            Runnable task = () -> runTask2();

            // Run the task in a background thread
            Thread backgroundThread = new Thread(task);
            // Terminate the running thread if the main.java.gui exits
            backgroundThread.setDaemon(true);
            // Start the thread
            backgroundThread.start();
        }
        private void runTask(){
            while (true){
                self.acceptConnections();
                Platform.runLater(() -> {
                    cmd.setText("beep beep.");
                });
            }
        }


        private void runTask2(){
            while(true){
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                responder.recieveTables(self);
                Platform.runLater(() -> {
                    cmd.setText("beep boop");
                });
            }
        }

    }

