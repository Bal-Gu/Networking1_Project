import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TestP2P_Window {
    public static void main(String[] args) throws SocketException {
        DatagramSocket ds =  new DatagramSocket(1533);
        Clientinfo ci =  new Clientinfo(ds.getInetAddress(),ds.getPort());
        ci.setUsername("WALUIGI");
        P2P_Window win = new P2P_Window(ci);
    }
}
