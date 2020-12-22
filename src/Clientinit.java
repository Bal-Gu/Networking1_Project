import java.net.*;

public class Clientinit {
    private DatagramSocket socket;
    private final Clientinfo c;
    private final InetAddress serveraddress;

    public Clientinit() throws SocketException, UnknownHostException {

        serveraddress = InetAddress.getByName("localhost");
        //Create Socket
        int port1;
        while (true) {
            port1 = (int) (Math.random() * 65535);
            try {
                socket = new DatagramSocket(port1);
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
        return 2077;
    }

    public InetAddress getServeraddress() {
        return serveraddress;
    }
}
