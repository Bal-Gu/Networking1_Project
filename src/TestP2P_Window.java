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

            ci.getMessages().add(new Messages(Integer.toString(i),"adwab djhawb djhawbdhjkabwhjdhawbjdhkbahjkw bd awhkbd hwabdk hbwahkbd hwab dkhawbfkhaebfhjkasfbha sbfh bsh bhk beshfbhak sebf hbes fhkb eskhfb hkab fhkae bsfh bahjaefhjkbhk sbfhkasebaes hfb a ehkjfbkhsfbhskjdvn  kjknv senffjksv bnekfefhjkbnehjksb<nkhj<efjk <bn<hj khfn< sejfhefnsjkrg gn hsjykfneshjkfn<sdjhkfesn< jfn<ius jfhnskudfjklsnes<kfjsnfj<s nfje nfljklesnfejk<n<fu<fndjfs<nef ju<sn j<esnfjk n<sjk fn<jsel fnl<sejfnjles<fnlk<jesfnljkes fnljk <esnf jlke<njk ejkl fn je<snfjsegnsj<v<dfulehnen<j<nf<segjhsj<ufdnsm <uesf <sm fiu<senm f<sjfmesoifkjmndgjshenfjeshjfnoiusagjioédsgmiysgjmyriégmryolfgil eis mfeisef msei em fiesfmiasmefi aesfj maismfwioam rdiaofsm ifsmgi sgi"));

            ci2.setUsername(new RandomString().getAlphaNumericString((int) (Math.random() * 25)));
            ci2.setConnected(Math.random() >= 0.5);
            ci.getPeers().add(ci2);
        }
        new P2P_Window(ci);
    }
}
