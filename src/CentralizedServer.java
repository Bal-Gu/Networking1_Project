import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class CentralizedServer{
    private DatagramSocket socket;
    private final int port = 2077;
    private byte[] buffer = new byte[ 2048 ];
    private DatagramPacket packet;
    private ArrayList<Clientinfo> clientinfoList = new ArrayList<>();
    private Semaphore s = new Semaphore(1);


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



        try{
            socket = new DatagramSocket(port);
        }catch (IOException e){
            throw(e);
        }
        while(true){
            try{
                this.packet =  new DatagramPacket(buffer,buffer.length);
                socket.receive(packet);
                Server_thread s = new Server_thread(this);
                s.run();

            }catch (IOException e){
                throw(e);
            }
        }
    }

    public DatagramPacket getPacket() {
        return packet;
    }
}
