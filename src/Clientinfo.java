import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clientinfo {
    private final InetAddress address;
    private final int port;
    private ArrayList<Clientinfo> peers = new ArrayList<>();
    private ArrayList<String> messages;
    private String username = "";
    private DatagramSocket socket;
    private boolean connected = true;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

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

    public void sendAllClientsInfoToClient(CentralizedServer cs) throws IOException, InterruptedException {
        if (cs.getClientList() == null) {
            return;
        }
        byte[] buffer = new byte[2048];

        StringBuilder message = new StringBuilder("");
        ArrayList<Clientinfo> cl = new ArrayList<>();
        for (Clientinfo c : cs.getClientList()) {
            Thread.sleep(100);
            if (c.getSocket() == null) {
                continue;
            }
            Arrays.fill(buffer, (byte) 0);
            String sending = "ready";
            buffer = sending.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, c.address, c.port);
            buffer = new byte[10];
            c.getSocket().send(packet);
            packet = new DatagramPacket(buffer, buffer.length);
            c.getSocket().setSoTimeout(2000);
            try {
                System.out.println("Server waiting for handshake from "+c.getUsername());
                c.getSocket().receive(packet);
                String s = new String(packet.getData(), 0, packet.getLength());

                s = s.replace("\0", "");
                if (!s.equals("handshake")) {
                    cl.add(c);
                    System.out.println("Server got corrupted hadshake");
                    continue;
                }
                System.out.println("Server recieved hadshake");
            } catch (SocketTimeoutException e) {
                System.out.println("Handshake  Timeout for "+c.getUsername());
                cl.add(c);
            }


        }
        ArrayList<Clientinfo> dupcheck = new ArrayList<>();
        for (Clientinfo c : cs.getClientList()) {
            if(dupcheck.contains(c)){
                cl.add(c);
            }else{
                dupcheck.add(c);
            }
        }

        cs.getClientList().removeAll(cl);
        for (Clientinfo c1 : cs.getClientList()) {
            message.append(c1.username);
            message.append(";");
            message.append(c1.address.toString());
            message.append(";");
            message.append(c1.port);
            message.append("\n");

        }
        for (Clientinfo c : cs.getClientList()) {
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
            if (m.find()) {
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
        return address.equals(clientinfo.address) && this.port == clientinfo.port;
    }


}
