import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class SendingThread implements Runnable {
    private final ArrayList<Packet> packetsToSend = new ArrayList<>();
    private final Semaphore s = new Semaphore(1);
    private DatagramSocket socket;
    private final boolean isMessage;
    private boolean emergencyExit = false;
    private final Clientinfo clientinfo;
    private final String filename;

    public SendingThread(String message, DatagramSocket socket, Clientinfo Tosendclientinfo) {
        this.socket = socket;
        this.clientinfo = Tosendclientinfo;
        this.isMessage = true;
        this.filename = "";
        MessagesToPackets(message);


    }

    public SendingThread(File file, DatagramSocket socket, Clientinfo Tosendclientinfo) throws IOException {
        this.socket = socket;
        this.clientinfo = Tosendclientinfo;
        this.isMessage = false;
        this.filename = file.getName();
        FileToPackets(file);
    }

    public void earlyExit() {
        emergencyExit = true;
    }

    @Override
    public void run() {
        //Create Socket
        while (true) {
            try (DatagramSocket socket2 = new DatagramSocket((int) (Math.random() * 65535))) {
                socket = socket2;
                break;

            } catch (SocketException ignored) {
                //ignored
            }
        }
        //Send new port to the given client
        byte[] buffer;

        String sending = isMessage ? "MESSAGE" : "FILE";
        buffer = sending.getBytes();
        long start = System.currentTimeMillis();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientinfo.getAddress(), clientinfo.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Wait for client package
        buffer = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.setSoTimeout((int) (System.currentTimeMillis() - start + 1000));
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Start new listening thread
        AcknowledgmentThread acknowledgmentThread = new AcknowledgmentThread(this);
        acknowledgmentThread.run();
        //While not all packets are marked as received send each packet.

        while (true) {
            if (emergencyExit) {
                socket.close();
                return;
            }
            try {
                if (!isnull()) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s.release();
            for (Packet p : packetsToSend) {
                try {
                    s.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (p.isRecieved()) {
                    continue;
                } else {
                    DatagramPacket packetToSend = new DatagramPacket(p.getPacket(), p.getPacket().length, packet.getAddress(), packet.getPort());
                    try {
                        socket.send(packetToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                s.release();
            }
        }
        if (!isMessage) {
            sending = "FILENAME " + filename;
            buffer = sending.getBytes();
            DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
            try {
                socket.send(packetToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Send END to the received port
        sending = "END";
        buffer = sending.getBytes();
        DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
        try {
            socket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        acknowledgmentThread.ending = true;

    }

    public boolean isMessage() {
        return isMessage;
    }

    public String getFilename() {
        return filename;
    }

    public void remove(int index) throws InterruptedException {
        s.acquire();
        packetsToSend.get(index).setRecieved(true);
        s.release();
    }

    private boolean isnull() throws InterruptedException {
        s.acquire();
        for (Packet p : packetsToSend) {
            if (!p.isRecieved()) {
                return true;
            }
        }
        return false;
    }

    private void FileToPackets(File file) throws IOException {

        ByteToPackets(Files.readAllBytes(file.toPath()));
    }

    private void MessagesToPackets(String message) {
        ByteToPackets(message.getBytes());
    }

    private void ByteToPackets(byte[] b) {
        for (int i = 0; i <= b.length / 1000; i++) {

            byte[] buf = new byte[1024];
            byte[] max1000buff = Arrays.copyOfRange(b, i * 1000, (int) Math.min((long) (i + 1) * 1000 - 1, b.length - 1));
            System.arraycopy(max1000buff, 0, buf, 0, buf.length);
            byte[] intbyte = Integer.toString(i).getBytes();
            int count = 0;
            for (int j = 23 - intbyte.length; j < 24; j++) {
                buf[i + 1000] = intbyte[count];
                count++;
            }
            packetsToSend.add(new Packet(i, buf));

        }
    }

    public DatagramSocket getSocket() {
        return socket;
    }

}
