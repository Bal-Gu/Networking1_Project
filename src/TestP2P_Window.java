import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;


public class TestP2P_Window {
    public static void main(String[] args) throws SocketException {

        ArrayList<Packet> p = new ArrayList<>();
        p.add(new Packet(4,new byte[20]));
        p.add(new Packet(1,new byte[20]));
        p.add(new Packet(3,new byte[20]));
        Collections.sort(p );
        for(Packet p1 : p){
            System.out.println(p1.getOrder());
        }

        DatagramSocket ds = new DatagramSocket(1533);
        Clientinfo ci = new Clientinfo(ds.getInetAddress(), ds.getPort());
        ci.setUsername("WALUIGI");

        for (int i = 0; i < 200; i++) {
            Clientinfo ci2 = new Clientinfo(ds.getInetAddress(), ds.getPort());
            ci2.setUsername(new RandomString().getAlphaNumericString((int) (Math.random() * 25)));
            ci2.setConnected(Math.random() >= 0.5);
            ci.getPeers().add(ci2);
        }
        new P2P_Window(ci);
    }
}
