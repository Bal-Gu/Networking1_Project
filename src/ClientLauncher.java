import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientLauncher {
    DatagramSocket socket;
    InetAddress serveraddress;

    public static void main(String[] args) throws IOException {
        new Clientinit();
    }


}
