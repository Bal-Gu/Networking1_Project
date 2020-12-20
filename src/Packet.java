import java.util.Objects;

public class Packet {
    private int order = 0;
    private byte[] packet = new byte[2000];
    private boolean recieved;
    public Packet(int order, byte[] packet){
        this.order = order;
        this.recieved = false;
        this.packet = packet;
    }

    public int getOrder() {
        return order;
    }

    public void setRecieved(boolean b){
        this.recieved = b;
    }
    public byte[] getPacket() {
        return packet;
    }

    public boolean isRecieved() {
        return recieved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return order == packet.order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order);
    }
}
