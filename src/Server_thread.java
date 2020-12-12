import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server_thread implements Runnable {



    private CentralizedServer  server;

    public Server_thread(CentralizedServer s) {

        this.server = s;
    }

    @Override
    public void run() {
        InetAddress address = server.getPacket().getAddress();
        int port = server.getPacket().getPort();
        DatagramPacket packet = new DatagramPacket(server.getBuffer(), server.getBuffer().length, address, port);
        String responce = new String(server.getBuffer(),0,packet.getLength());
        responce = responce.toLowerCase();
        switch (responce){
            case "group":
                //TODO join the unique group after pressing a button (internal no message)
                break;
            case "stop":
                //TODO if message is stop break connection and remove client on every peer
                //TODO if application exit then the connection should send a stop and close the connection
                break;
            case "lauch":
                //TODO after 3 people are in a group the user can launch the group (internal no message)
                break;
            default:
                //splits the join out
                if(responce.matches("^join.*")){
                   String[] splitedResponsce =  responce.split(" \\s*");
                   if( splitedResponsce.length < 1) {
                       return;
                   }
                   //parse the username
                   Clientinfo cl = new Clientinfo(server.getSocket().getInetAddress());
                   StringBuilder name = new StringBuilder(splitedResponsce[1]);
                   for(int i = 2; i < splitedResponsce.length ; i++){
                       name.append(" ");
                       name.append(splitedResponsce[i]);
                   }
                   //set it to the internal list
                   cl.setUsername(name.toString());
                   server.getClientList().add(cl);
                   System.out.println("Server got username " + name);
                   return;
                }
                System.out.println("not a valid output");
                break;
        }
    }
}
