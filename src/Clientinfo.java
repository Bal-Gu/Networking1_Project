import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

public class Clientinfo {
    private final InetAddress address;
    private final int order;
    private ArrayList<Clientinfo> peers;
    private ArrayList<String> messages;

    public Clientinfo(InetAddress a , int order){
        this.address = a;
        this.order = order;
    }

    public int getOrder() {
        return order;
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
