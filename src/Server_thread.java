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
        responce = responce.replace("\0", "");
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
                    System.out.println("Removing " + server.getClientList().get(index).getUsername());
                    server.getClientList().remove(index);
                    server.getS().release();
                    break;
                }
                index++;
            }
            server.getS().release();
            String connected = server.getClientList().size() + "";
            for (Clientinfo client : server.getClientList()) {
                byte[] buffer;
                buffer = connected.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, client.getAddress(), client.getPort());
                try {
                    server.getSocket().send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if ("launch".equals(responce)) {
            if (server.getClientList().size() >= 3) {
                try {
                    Clientinfo cl = new Clientinfo(address, port);
                    cl.sendAllClientsInfoToClient(server);
                    System.out.println("Server has sended data.");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                server.getClientList().clear();
            }
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
                if (server.getClientList().contains(cl)) {
                    int counter = 0;
                    while (!server.getClientList().get(counter).equals(cl)) {
                        counter++;
                    }
                    System.out.println("Server changed username " + name);
                    server.getClientList().get(counter).setUsername(cl.getUsername());
                    return;
                }
                server.getClientList().add(cl);
                System.out.println("Server got username " + name);
                System.out.println("Server has " + server.getClientList().size() + " client");
                byte[] buffer;
                String connected = server.getClientList().size() + "";
                for (Clientinfo client : server.getClientList()) {
                    buffer = connected.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, client.getAddress(), client.getPort());
                    try {
                        server.getSocket().send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }
}
