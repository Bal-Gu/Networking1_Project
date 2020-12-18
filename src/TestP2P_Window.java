import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TestP2P_Window {
    public static void main(String[] args) throws SocketException {
        DatagramSocket ds = new DatagramSocket(1533);
        Clientinfo ci = new Clientinfo(ds.getInetAddress(), ds.getPort());
        ci.setUsername("WALUIGI");

        for (int i = 0; i < 200; i++) {
            Clientinfo ci2 = new Clientinfo(ds.getInetAddress(), ds.getPort());
            ci2.setUsername(new RandomString().getAlphaNumericString((int) (Math.random() * 25)));
            ci2.setConnected(Math.random() >= 0.5);
            ci.getPeers().add(ci2);
        }
        P2P_Window win = new P2P_Window(ci);
    }
}
