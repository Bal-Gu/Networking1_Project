import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

public class ReceptionThreadMessage implements Runnable {
    private final DatagramPacket packet;
    private final byte[] buffer = new byte[1024];
    private final ArrayList<Packet> packetsArray = new ArrayList<>();
    private final Clientinfo client;
    private final P2P_Window window;
    private  final  String  username;

    public ReceptionThreadMessage(DatagramPacket packet, P2P_Window window, Clientinfo client,String username) {
        this.packet = packet;
        this.client = client;
        this.window = window;
        this.username = username;
    }

    @Override
    public void run() {

        DatagramSocket socket;
        while (true) {
            try  {
                socket = new DatagramSocket((int) (Math.random() * 65536));
                break;
            } catch (SocketException ignored) {
            }
        }

        DatagramPacket pack = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());

        try {
            socket.send(pack);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            byte[] newbufpack = new byte[1024];
            DatagramPacket newpack = new DatagramPacket(newbufpack, newbufpack.length);

            try {
                socket.receive(newpack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String DataString = new String(newpack.getData(), 0, newpack.getLength());
            DataString = DataString.replace("\0","");
            if((DataString.equals("END"))){
                System.out.println("Recieved END of Messages");
                DataString = "END";
                newbufpack = DataString.getBytes();
                newpack = new DatagramPacket(newbufpack, newbufpack.length, packet.getAddress(), packet.getPort());

                try {
                    socket.send(newpack);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StringBuilder Concatenation = new StringBuilder();
                Collections.sort(packetsArray);
                for(Packet b : packetsArray){
                    newbufpack = b.getPacket();
                    DataString = new String(newbufpack, 0, newbufpack.length);
                    Concatenation.append(DataString);
                }

                String Message = Concatenation.toString();

                client.getMessages().add(new Messages(username,Message));
                window.messagesUpdate();
                break;
            }

            String getNumber = new String(newpack.getData(), 1000, newpack.getLength()-1000);
            byte[] intbuff = getNumber.getBytes();
            int sendnumber =Integer.parseInt(getNumber.split("\0")[0]);
            Packet p = new Packet(sendnumber, new String(newpack.getData(), 0, newpack.getLength()-24).getBytes());
            if(!packetsArray.contains(p)){
                packetsArray.add(p);
            }

            newpack = new DatagramPacket(intbuff, intbuff.length, packet.getAddress(), packet.getPort());

            try {
                socket.send(newpack);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
