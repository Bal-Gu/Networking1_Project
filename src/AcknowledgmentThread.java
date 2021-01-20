import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class AcknowledgmentThread implements Runnable {
    private final SendingThread sendingThread;
    public boolean ending = false;
    private  final InetAddress address;
    public AcknowledgmentThread(SendingThread sendingThread, InetAddress address) {
        this.sendingThread = sendingThread;
        this.address = address;
    }

    @Override
    public void run() {
        DatagramPacket packet = null;
        if (ending) {
            return;
        }
        while (!ending) {
            if(sendingThread.getSocket().isClosed()){
                return;
            }
            //set the received package to true
            byte[] buffer = new byte[1024];

            packet = new DatagramPacket(buffer, buffer.length);
            try {
                try {
                    //receives package
                    sendingThread.getSocket().receive(packet);
                } catch (SocketTimeoutException e) {
                    if (address.isReachable(sendingThread.getSocket().getSoTimeout())) {
                        //client isn't dead
                        continue;
                    } else {
                        sendingThread.earlyExit();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //parses revived message
            String receive = new String(packet.getData(), 0, packet.getLength());
            String keyword = receive.replace("\0","");
            //END is received
            if ("END".equals(keyword)) {
                //End result in a break an sending the rest to properly exit
                break;
            } else if (isNumeric(keyword)) {
                //received a integer to mark a package as received
                int parsed_int = Integer.parseInt(keyword);
                try {
                    sendingThread.remove(parsed_int);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //making sure the client gets the Filename
        if (!sendingThread.isMessage()) {
            for (int i = 0; i < 5; i++) {
                String sending = "FILENAME " + sendingThread.getFilename();
                byte[] buffer = sending.getBytes();

                try {
                    DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    sendingThread.getSocket().send(packetToSend);
                } catch (IOException | IllegalArgumentException ignore ) {

                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //making sure to properly disconnect from the client
        for (int i = 0; i < 5; i++) {
            String sending = "END";
            byte[] buffer = sending.getBytes();

            try {
                DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                sendingThread.getSocket().send(packetToSend);
            } catch (IOException | IllegalArgumentException ignore) {
                //ignores potential Sockets being already closed
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //no connections should pass thus closing the Socket
        sendingThread.getSocket().close();
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
