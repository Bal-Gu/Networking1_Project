import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceptionThreadMessage implements Runnable {
    private final DatagramPacket packet;
    private byte[] buffer;

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
            pack = new DatagramPacket(buffer, buffer.length);

            String DataString = new String(pack.getData(), 0, pack.getLength());


            if((DataString.split("\\s+")[0]).equals("END")){
                break;
            }

            String getNumber = new String(pack.getData(), 1000, pack.getLength()-1000);

            byte[] newbufpack;
            newbufpack = getNumber.getBytes();

            DatagramPacket newpack = new DatagramPacket(newbufpack, newbufpack.length, packet.getAddress(), packet.getPort());

            try {
                socket.send(newpack);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //TODO send end as packet before the break/after the while

        //TODO while loop with parsing the last byte and sending an acknowleagment (the number in return)

    }
}
