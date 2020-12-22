import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class ReceptionThreadMessage implements Runnable {
    private final DatagramPacket packet;
    private byte[] buffer;
    private ArrayList<Packet> packetsArray = new ArrayList<>();
    private Clientinfo client;


    public ReceptionThreadMessage(DatagramPacket packet, byte[] buffer, Clientinfo client) {
        this.packet = packet;
        this.buffer = buffer;
        this.client = client;
    }

    @Override
    public void run() {

        DatagramSocket socket;
        while (true) {
            try (DatagramSocket randSock = new DatagramSocket((int) (Math.random() * 65536))) {
                socket = randSock;
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
            pack = new DatagramPacket(buffer, buffer.length);
            DatagramPacket newpack;
            byte[] newbufpack;
            String DataString = new String(pack.getData(), 0, pack.getLength());


            if((DataString.split("\\s+")[0]).equals("END")){
                DataString = "END";
                newbufpack = DataString.getBytes();
                newpack = new DatagramPacket(newbufpack, newbufpack.length, packet.getAddress(), packet.getPort());

                try {
                    socket.send(newpack);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StringBuffer Concatenation = new StringBuffer();

                for(Packet b : packetsArray){
                    newbufpack = b.getPacket();
                    DataString = new String(newbufpack, 0, newbufpack.length);
                    Concatenation.append(DataString);
                }

                String Message = Concatenation.toString();

                client.addMessage(Message);

                break;
            }

            String getNumber = new String(pack.getData(), 1000, pack.getLength()-1000);
            newbufpack = getNumber.getBytes();

            packetsArray.add(new Packet(Integer.parseInt(getNumber), new String(pack.getData(), 0, pack.getLength()-24).getBytes()));

            newpack = new DatagramPacket(newbufpack, newbufpack.length, packet.getAddress(), packet.getPort());

            try {
                socket.send(newpack);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
