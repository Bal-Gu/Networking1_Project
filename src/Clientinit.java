import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Clientinit {
    private final DatagramSocket socket;
    private Clientinfo c;
    private int port = 2077;
    private InetAddress serveraddress;
    public Clientinit() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        serveraddress = InetAddress.getByName("localhost");
        this.c = new Clientinfo(socket.getLocalAddress()) ;
        new ClientconnectWindow(this);

    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public Clientinfo getC() {
        return c;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getServeraddress() {
        return serveraddress;
    }
}
