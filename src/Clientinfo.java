import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

public class Clientinfo {
    private final InetAddress address;
    private ArrayList<Clientinfo> peers;
    private ArrayList<String> messages;
    private String username = "";
    public Clientinfo(InetAddress a){
        this.address = a;
    }

    public String getUsername() {
        return username;
    }

    public void removeFromPeers(){
        if (peers == null){
            return;
        }
        for(Clientinfo c : peers){
            peers.remove(this);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InetAddress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clientinfo clientinfo = (Clientinfo) o;
        return address.equals(clientinfo.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
