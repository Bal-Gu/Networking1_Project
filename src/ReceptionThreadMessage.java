import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceptionThreadMessage implements Runnable {
    private final DatagramPacket packet;
    private byte[] buffer;
    private int acknowledgment;
    private String keyword;

    public ReceptionThreadMessage(DatagramPacket packet, byte[] buffer) {
        this.packet = packet;
        this.buffer = buffer;
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
            pack = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());

            String DataString = new String(pack.getData(), 0, pack.getLength());

            if((keyword = DataString.split("\\s+")[0]).equals("END")){
                break;
            }

            byte[] buf = pack.getData();
            byte[] newbufpack = new byte[1024];

            for(int i = 1000; i < 1024; i++){
                newbufpack[i] = buf[i];
            }

            DatagramPacket newpack = new DatagramPacket(newbufpack, newbufpack.length, packet.getAddress(), packet.getPort());

            try {
                socket.send(newpack);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        //TODO while loop with parsing the last byte and sending an acknowleagment (the number in return)

    }
}
//fuck this