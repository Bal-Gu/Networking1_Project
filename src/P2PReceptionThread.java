import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class P2PReceptionThread implements Runnable {
    private final Clientinfo client;
    private final P2P_Window window;
    public boolean stay = true;

    public P2PReceptionThread(P2P_Window p2P_window, Clientinfo client) {
        this.client = client;
        this.window = p2P_window;
    }


    @Override
    public void run() {
        byte[] buffer = new byte[2048];
        while (stay) {
            //if the socket is closed then it should exit the windows and exit the programm.
            if(client.getSocket().isClosed()){
                System.exit(0);
            }
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                client.getSocket().receive(packet);
            } catch (IOException e) {
                //TODO Make a timeout and catch the exception. In the exception we should check if the client is still connected or not
                //TODO every  10 second - 1 min a handshake should be performed. Time should be random
                continue;
            }
            String recieve = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Recieved " + recieve);
            String keyword = recieve.split("\\s+")[0];
            Clientinfo peer;
            switch (keyword) {
                case "/username":
                    peer = client.searchpeers(packet.getAddress(), packet.getPort());
                    if (peer != null) {
                        StringBuilder peerName = new StringBuilder();
                        for (int i = 1; i < recieve.split("\\s+").length; i++) {
                            peerName.append(recieve.split("\\s+")[i]).append(" ");
                        }
                        peer.setUsername(peerName.toString());
                        window.updateUsername();
                    }
                    break;
                case "/quit":
                    //quit removes the peer from the peer list
                    Clientinfo tempClient = new Clientinfo(packet.getAddress(), packet.getPort());
                    client.getPeers().remove(tempClient);
                    window.updateUsername();
                    break;
                case "STOP":
                    peer = client.searchpeers(packet.getAddress(), packet.getPort());
                    if (peer != null) {
                        peer.setConnected(false);
                        window.updateUsername();
                    }
                    break;
                case "RECONNECTION":
                    peer = client.searchpeers(packet.getAddress(), packet.getPort());
                    if (peer != null) {
                        peer.setConnected(true);
                        window.updateUsername();
                    }
                    break;
                case "MESSAGE":
                    ReceptionThreadMessage message = new ReceptionThreadMessage(packet, buffer);
                    message.run();
                    //TODO MESSAGE add message to the messages list and actualise the P2P_Window
                    break;
                case "FILE":
                    //TODO FILE open another thread for this such that it doesn't stop the client
                default:
                    break;

            }


        }
    }
}
