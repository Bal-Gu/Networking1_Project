import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class Server_thread implements Runnable {


    private final CentralizedServer server;

    public Server_thread(CentralizedServer s) {

        this.server = s;
    }

    @Override
    public void run() {
        //address of packet
        InetAddress address = server.getPacket().getAddress();
        int port = server.getPacket().getPort();
        //get message
        DatagramPacket packet = new DatagramPacket(server.getBuffer(), server.getBuffer().length, address, port);
        String responce = new String(server.getBuffer(), 0, packet.getLength());
        responce = responce.toLowerCase();
        //flush the buffer
        Arrays.fill(server.getBuffer(), (byte) 0);
        //stop will remove the client connections
        if ("stop".equals(responce)) {
            try {
                server.getS().acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int index = 0;
            while (server.getClientList().get(index) != null) {
                if (server.getClientList().get(index).getAddress().equals(address)) {
                    server.getClientList().remove(index);
                    server.getS().release();
                    break;
                }
                index++;
            }
            server.getS().release();
        } else {
            //splits the join out
            if (responce.matches("^join.*")) {
                String[] splitedResponsce = responce.split(" \\s*");
                if (splitedResponsce.length < 1) {
                    return;
                }
                //parse the username
                Clientinfo cl = new Clientinfo(address, port);
                StringBuilder name = new StringBuilder(splitedResponsce[1]);
                for (int i = 2; i < splitedResponsce.length; i++) {
                    name.append(" ");
                    name.append(splitedResponsce[i]);
                }
                //set it to the internal list
                cl.setUsername(name.toString());
                cl.setSocket(server.getSocket());
                server.getClientList().add(cl);
                System.out.println("Server got username " + name);
                System.out.println("Server has " + server.getClientList().size() + " client");
                if (server.getClientList().size() >= 3) {
                    try {
                        cl.sendAllClientsInfoToClient(server.getClientList());
                        System.out.println("Server has sended data.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }
}
