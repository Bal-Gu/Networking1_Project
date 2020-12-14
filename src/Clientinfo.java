import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clientinfo {
    private final InetAddress address;
    private final int port;
    private ArrayList<Clientinfo> peers = new ArrayList<>();
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
            if(c.getSocket()  == null){
                continue;
            }
            Arrays.fill(buffer, (byte) 0);
            String sending = "ready";
            buffer = sending.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, c.address, c.port);
            c.getSocket().send(packet);

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
            buffer = message.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, c.address, c.port);
            c.getSocket().send(packet);

        }
    }

    public void readyparseMessage(String message) throws UnknownHostException {
        String[] ClientAmount = message.split("\n");
        for (String s : ClientAmount) {
            String[] params = s.split(";");
            Pattern ipWithRegex = Pattern.compile("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}");
            Matcher m = ipWithRegex.matcher(s);
            if(m.find()) {
                Clientinfo clinfo = new Clientinfo(InetAddress.getByName(m.group(0)), Integer.parseInt(params[2]));

                clinfo.setUsername(params[0]);
                if (!this.equals(clinfo)) {
                    peers.add(clinfo);
                }
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
