import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceptionThreadMessage implements Runnable {
    private final DatagramPacket packet;
    private final byte[] buffer;

    public ReceptionThreadMessage(DatagramPacket packet, byte[] buffer) {
        this.packet = packet;
        this.buffer = buffer;
    }

    @Override
    public void run() {

        DatagramSocket socket;
        while (true) {
            try (DatagramSocket randSock = new DatagramSocket((int) (Math.random() * 65536))) {
                socket = randSock;
                break;
            } catch (SocketException ignored) {
            }
        }

        DatagramPacket pack = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());

        try {
            socket.send(pack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO while loop with parsing the last byte and sending an acknowleagment (the number in return)

    }
}
//fuck this