import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Clientinit {
    private final DatagramSocket socket;
    private final Clientinfo c;
    private final int port = 2077;
    private final InetAddress serveraddress;

    public Clientinit() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        serveraddress = InetAddress.getByName("localhost");
        //Create Socket
        while (true) {
            port1 = (int) (Math.random() * 65535);
            try {
                DatagramSocket socket2 = new DatagramSocket(port1);
                socket = socket2;
                break;

            } catch (SocketException ignored) {
                //ignored
            }
        }
        this.c = new Clientinfo(serveraddress, port1);
        c.setSocket(socket);
        socket.setSoTimeout(10000);
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
