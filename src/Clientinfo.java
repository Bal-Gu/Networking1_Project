import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Clientinfo {
    private final InetAddress address;
    private final int port;
    private ArrayList<Clientinfo> peers;
    private ArrayList<String> messages;
    private String username = "";
    private DatagramSocket socket;

    public int getPort() {
        return port;
    }

    public ArrayList<Clientinfo> getPeers() {
        return peers;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public Clientinfo(InetAddress a, int port) {
        this.address = a;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void removeFromPeers() {
        if (peers == null) {
            return;
        }
        for (Clientinfo c : peers) {
            peers.remove(this);
        }
    }

    public void sendAllClientsInfoToClient(ArrayList<Clientinfo> clientinfoList) throws IOException {
        if (clientinfoList == null) {
            return;
        }
        byte[] buffer = new byte[2048];

        StringBuilder message = new StringBuilder("");
        for (Clientinfo c : clientinfoList) {
            if(socket  == null){
                return;
            }
            Arrays.fill(buffer, (byte) 0);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, c.address, c.port);
            String sending = "ready";
            buffer = sending.getBytes();
            socket.send(packet);

        }
        for (Clientinfo c1 : clientinfoList) {
            if (c1.equals(this)) {
                continue;
            }
            message.append(c1.username);
            message.append(";");
            message.append(c1.address.toString());
            message.append(";");
            message.append(c1.port);
            message.append("\n");

        }
        for (Clientinfo c : clientinfoList) {
            Arrays.fill(buffer, (byte) 0);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, c.address, c.port);
            buffer = message.toString().getBytes();
            socket.send(packet);

        }
    }

    public void readyparseMessage(String message) throws UnknownHostException {
        String[] ClientAmount = message.split("\n");
        peers.clear();
        for (String s : ClientAmount) {
            String[] params = s.split(";");
            Clientinfo clinfo = new Clientinfo(InetAddress.getByAddress(params[1].getBytes()), Integer.parseInt(params[2]));
            clinfo.setUsername(params[0]);
            if(!this.equals(clinfo)){
                peers.add(clinfo);
            }
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
        return address.equals(clientinfo.address) && this.port == clientinfo.port ;
    }


}
