import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Base64;

public class ReceptionOfFileThread implements Runnable {
    private final DatagramPacket packet;
    String[] fileName;
    int countEnd = 0;
    String finalFileData;
    boolean gotFilename = false;
    DatagramSocket mySocket;

    public ReceptionOfFileThread(DatagramPacket packet) {
        //GET THE PACKAGE FROM THE P2PRECHEPTIONTHREAD
        this.packet = packet;
    }

    @Override
    public void run() {
        //FIND A NEW FREE SOCKET/PORT
        int port = 0;
        while (true) {
            port = (int) (Math.random() * 65535);
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close();
                break;
            } catch (SocketException ignored) {
                //ignored
            }
        }

        while (true) {
            //SEND AN OK MESSAGE TO THE SENDER (USE THE PACKAGE IP AND PORT)
            try (DatagramSocket socket = new DatagramSocket(port)) {
                mySocket = socket;
                String message = "ok";
                DatagramPacket datagramPacket = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        packet.getAddress(),
                        packet.getPort()
                );
                socket.send(datagramPacket);

                packet.getData();
                socket.receive(packet);

                //PARSE THE MESSAGE. THE LAST 24 BYTES FROM 1024 ARE THE PACKAGE NUMBER
                String s = new String(packet.getData(), 1000, packet.getLength());

                //SEND THE PACKAGE NUMBER
                DatagramPacket returnPackageNumber = new DatagramPacket(
                        s.getBytes(),
                        s.length(),
                        packet.getAddress(),
                        packet.getPort()
                );
                socket.send(returnPackageNumber);

                //SAVE THE 1000 BYTES OF THE PACKAGE
                String packageDataString = new String(packet.getData(), 0, packet.getLength() - 24);
                finalFileData += packageDataString;

                //THE FIRST WHILE LOOP OF THE RECEPTION HAS TO GET THE PACKAGE AND CHECK FOR A FILENAME (tipp use regex and split with //s* and get the first key)
                if (packageDataString.matches("[\\w]+\\.[A-Za-z]{3,5}")) { //File Name with extension having 3 to 5 chars
                    //AFTER RECEPTION OF FILENAME SAVE IT AND WAIT FOR THE RECEPTION OF END
                    fileName = packageDataString.split("[\\w]+\\.[A-Za-z]{3,5}");
                    gotFilename = true;
                }

                //AFTER SECOND END OR TIMEOUT HAS BEEN RECIEVED CLOSE THE SOCKET CONNECTION
                if (packageDataString.equals("END")) {
                    countEnd++;
                    socket.close();
                }

                if (countEnd == 1) {
                    //AFTER FIRST END HAS REACHED SEND END AS WELL TO CONFIRME THE CONNECTION BEEING CLOSED
                    if (mySocket.isConnected()){
                        mySocket.close();
                    }
                    break;
                }

            } catch (IOException e) { //AFTER X SECONDS AFTER NOT RECIEVING A PACKAGE USE THE TIMEOUT TO DO A PING REQUEST
                e.printStackTrace();
                InetAddress client = null;
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
            String nameFile = Arrays.toString(fileName);
            try (FileOutputStream fos = new FileOutputStream("../files/" + nameFile)) {
                fos.write(Integer.parseInt(finalFileData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //TODO ADD A MESSAGE WITH THE NAME OF THE FILE INTO THE ACTUAL CLIENT
        System.out.println("File has been saved.");
    }
}
