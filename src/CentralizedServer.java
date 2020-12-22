import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class CentralizedServer {
    private DatagramSocket socket;
    private final byte[] buffer = new byte[2048];
    private DatagramPacket packet;
    private final ArrayList<Clientinfo> clientinfoList = new ArrayList<>();
    private final Semaphore s = new Semaphore(1);


    public DatagramSocket getSocket() {
        return socket;
    }

    public ArrayList<Clientinfo> getClientList() {
        return clientinfoList;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void start() throws IOException {


        int port = 2077;
        socket = new DatagramSocket(port);
        while (true) {
            this.packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(10000000);
            socket.receive(packet);
            Server_thread s = new Server_thread(this);
            s.run();

        }
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    public Semaphore getS() {
        return s;
    }
}
