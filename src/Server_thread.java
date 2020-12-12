import java.net.DatagramPacket;
import java.util.ArrayList;

public class Server_thread implements Runnable {



    private DatagramPacket packet;
    private CentralizedServer  server;

    public Server_thread(CentralizedServer s) {
        this.packet = packet;
        this.server = s;
    }

    @Override
    public void run() {
        String responce = new String(server.getBuffer(),0,packet.getLength());
        responce = responce.toLowerCase();
        switch (responce){
            case "group":
                //TODO create a group after pressing a button (internal no message)
                break;
            case "stop":
                //TODO if message is stop break connection and remove client on every peer
                //TODO if application exit then the connection should send a stop and close the connection
                break;
            case "lauch":
                //TODO after 3 people are in a group the user can launch the group (internal no message)
                break;
            default:
                System.out.println("not a valid output");
                break;
        }
    }
}
