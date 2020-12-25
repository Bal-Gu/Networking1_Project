import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReceptionOfFileThread implements Runnable {
    private final DatagramPacket packet;
    private final String username;
    String fileName;
    int countEnd = 0;
    boolean gotFilename = false;
    DatagramSocket mySocket;
    List<Packet> packetArray = new ArrayList<>();
    private final Clientinfo client;

    public ReceptionOfFileThread(DatagramPacket packet, Clientinfo client,String username) {
        //GET THE PACKAGE FROM THE P2PRECHEPTIONTHREAD
        this.packet = packet;
        this.client = client;
        this.username = username;
    }

    @Override
    public void run() {
        //FIND A NEW FREE SOCKET/PORT
        int port;
        while (true) {
            port = (int) (Math.random() * 65535);
            try {
                mySocket = new DatagramSocket(port);
                break;
            } catch (SocketException ignored) {
                //ignored
            }
        }

        //send ok after creation of socket
        String message = "ok";
        DatagramPacket datagramPacket = new DatagramPacket(
                message.getBytes(),
                message.length(),
                packet.getAddress(),
                packet.getPort()
        );
        try {
            mySocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            //SEND AN OK MESSAGE TO THE SENDER (USE THE PACKAGE IP AND PORT)
            try {
                byte[] buffer = new byte[1024];
                datagramPacket = new DatagramPacket(
                        buffer,
                        buffer.length
                );

                try {
                    mySocket.receive(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String s = "";
                String packageDataString = "";
                //PARSE THE MESSAGE. THE LAST 24 BYTES FROM 1024 ARE THE PACKAGE NUMBER
                if (datagramPacket.getLength() < 1000) {
                    packageDataString = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                } else {
                    s = new String(datagramPacket.getData(), 1000, datagramPacket.getLength() - 1000);

                    int sendingint = Integer.parseInt(s.replace("\0", ""));
                    s = sendingint + "";
                    //SEND THE PACKAGE NUMBER
                    DatagramPacket returnPackageNumber = new DatagramPacket(
                            s.getBytes(),
                            s.length(),
                            packet.getAddress(),
                            packet.getPort()
                    );
                    mySocket.send(returnPackageNumber);
                    packageDataString = new String(datagramPacket.getData(), 0, datagramPacket.getLength() - 24);
                    byte[] dataFile = datagramPacket.getData();
                    dataFile = Arrays.copyOfRange(dataFile, 0, 1000);
                    System.out.println(sendingint);
                    Packet p = new Packet(sendingint, dataFile);
                    if (!packetArray.contains(p)) {
                        packetArray.add(p);
                    }
                }
                //SAVE THE 1000 BYTES OF THE PACKAGE


                //THE FIRST WHILE LOOP OF THE RECEPTION HAS TO GET THE PACKAGE AND CHECK FOR A FILENAME (tipp use regex and split with //s* and get the first key)
                if (packageDataString.split("\\s+")[0].equals("FILENAME")) { //File Name with extension having 3 to 5 chars
                    //AFTER RECEPTION OF FILENAME SAVE IT AND WAIT FOR THE RECEPTION OF END
                    fileName = packageDataString.split("\\s+")[1];
                    //put this string in the client message. May have to find the right peer from the peer list. and get the client from the constructur.
                    client.getMessages().add(new Messages(username , fileName));
                    gotFilename = true;
                }

                //AFTER SECOND END OR TIMEOUT HAS BEEN RECIEVED CLOSE THE SOCKET CONNECTION
                if (packageDataString.equals("END")) {
                    countEnd++;
                }

                if (countEnd == 1) {
                    //AFTER FIRST END HAS REACHED SEND END AS WELL TO CONFIRME THE CONNECTION BEEING CLOSED

                    mySocket.close();

                    break;
                }

            } catch (IOException e) { //AFTER X SECONDS AFTER NOT RECIEVING A PACKAGE USE THE TIMEOUT TO DO A PING REQUEST
                e.printStackTrace();
                InetAddress client;
                client = packet.getAddress();
                try {
                    if (!client.isReachable(5000)) {
                        //IF PING HASN'T RESEND ANYTHING BRAKE THE CONNECTION AND DROP THE PACKAGES
                        mySocket.close();
                        break;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        //SAVE THE FILE USING THE SAME NAME AS PROVIDED (DO THIS OUT OF THE WHILE) THEN CLOSE THE THREAD IF GOT AT LEAST ONE END OR AT LEAST THE FILENAME BUT DIDN'T PROPERLY CLOSE THE CONNECTION START THE FILE COMPOSITION PROCESS
        if (countEnd <= 1 || gotFilename) {
            File directory = new File(System.getProperty("user.dir")+"/files");
            if (! directory.exists()){
                if(!directory.mkdir()){
                    return;
                }
            }
            System.out.println(fileName);
            //filebuffer creation
            ArrayList<Byte> bytes = new ArrayList<>();
            //sorting the collection in the right order
            Collections.sort(packetArray);
            int totallength = 0;
            //getting the total length of the packets
            for(Packet p : packetArray){
                totallength += p.getPacket().length;
            }
            //appending each packet
            ByteBuffer buffer = ByteBuffer.wrap(new byte[totallength]);
            for(Packet p : packetArray){
                buffer.put(p.getPacket());
            }
            //checking if the file exist
            File f = new File(System.getProperty("user.dir")+"/files/" + fileName);
            if(f.exists()){
                return;
            }
            try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"/files/" + fileName)) {
                fos.write(buffer.array());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("File has been saved.");
    }
}
