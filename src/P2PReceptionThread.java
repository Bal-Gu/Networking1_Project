import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class P2PReceptionThread implements Runnable {
    private Clientinfo client;
    private P2P_Window window;
    public boolean stay = true;

    public P2PReceptionThread(P2P_Window p2P_window, Clientinfo client) {
        this.client = client;
        this.window = p2P_window;
    }


    @Override
    public void run() {
        byte[] buffer = new byte[2048];
        while (stay) {
            //TODO if the socket is closed then it should exit the windows and exit the programm.
            //TODO Make a timeout and catch the exception. In the exception we should check if the client is still connected or not
            //TODO every  10 second - 1 min a handshake should be performed. Time should be random

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                client.getSocket().receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String recieve = new String(packet.getData(), 0, packet.getLength());
            String keyword = recieve.split("\\s*")[0];
            Clientinfo peer;
            switch (keyword) {
                case "/ping":
                    //TODO /ping makes a echo request to the users to so if they are still connected.
                    break;
                case "/username":
                    peer = client.searchpeers(packet.getAddress(), packet.getPort());
                    StringBuilder peerName = new StringBuilder();
                    for (int i = 1; i < recieve.split("\\s*").length; i++) {
                        peerName.append(recieve.split("\\s*")[i]);
                    }
                    peer.setUsername(peerName.toString());
                    break;
                case "/quit":
                    //TODO /quit removes the peer from the peer list
                    //TODO if /quit should go out of the thread
                    break;
                case "STOP":
                    peer = client.searchpeers(packet.getAddress(), packet.getPort());
                    if(peer != null){
                        peer.setConnected(false);
                    }
                    break;
                case "RECONNECTION":
                    peer = client.searchpeers(packet.getAddress(), packet.getPort());
                    if(peer != null){
                        peer.setConnected(true);
                    }
                    break;
                case "MESSAGE":
                    ReceptionThreadMessage message = new ReceptionThreadMessage(packet, buffer);
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
