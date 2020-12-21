import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Base64;

public class ReceptionOfFileThread implements Runnable {
    private final String clientIp;
    private final int clientPort;
    byte[] receiveData;
    String[] fileName;
    int countEnd = 0;
    byte[] finalFileData;
    boolean gotFilename = false;

    public ReceptionOfFileThread(String clientIp, int clientPort, byte[] data) {
        //GET THE PACKAGE FROM THE P2PRECHEPTIONTHREAD
        this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.receiveData = data;
    }

    @Override
    public void run() {
        while (true) {
            //FIND A NEW FREE SOCKET/PORT
            int port = 0;
            try {
                ServerSocket server = new ServerSocket(0);
                port = server.getLocalPort();
                server.close();
            } catch (IOException e) {
                throw new RuntimeException("No port found", e);
            }

            //SEND AN OK MESSAGE TO THE SENDER (USE THE PACKAGE IP AND PORT)
            try (DatagramSocket socket = new DatagramSocket(port)) {
                String message = "ok";
                DatagramPacket datagramPacket = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        InetAddress.getLocalHost(),
                        clientPort
                );
                socket.send(datagramPacket);

                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(packet);

                //PARSE THE MESSAGE. THE LAST 24 BYTES FROM 1024 ARE THE PACKAGE NUMBER
                byte[] lastBytes = Arrays.copyOfRange(receiveData, receiveData.length - 1000, receiveData.length); //recheck if it is 1000 or 999
                String s = Base64.getEncoder().encodeToString(lastBytes);

                //SEND THE PACKAGE NUMBER
                DatagramPacket returnPackageNumber = new DatagramPacket(
                        s.getBytes(),
                        s.length(),
                        InetAddress.getLocalHost(),
                        clientPort
                );
                socket.send(returnPackageNumber);

                //SAVE THE 1000 BYTES OF THE PACKAGE
                byte[] packageData = Arrays.copyOfRange(receiveData, 0, receiveData.length - 24); //recheck if it is 24 or 23 or 25
                byte[] data = new byte[finalFileData.length + packageData.length];
                finalFileData = data;
                String packageDataString = new String(packageData);

                //THE FIRST WHILE LOOP OF THE RECEPTION HAS TO GET THE PACKAGE AND CHECK FOR A FILENAME (tipp use regex and split with //s* and get the first key)
                if (packageDataString.matches("^[\\w,\\s-]+\\.[A-Za-z]{3}")) { //File Name with extension having 3 chars
                    //AFTER RECEPTION OF FILENAME SAVE IT AND WAIT FOR THE RECEPTION OF END
                    fileName = packageDataString.split("^[\\w,\\s-]+\\.[A-Za-z]{3}");
                    gotFilename = true;
                }

                //AFTER SECOND END OR TIMEOUT HAS BEEN RECIEVED CLOSE THE SOCKET CONNECTION
                if (packageDataString.equals("END")) {
                    countEnd++;
                    socket.close();
                }

                if (countEnd == 1) {
                    //TODO AFTER FIRST END HAS REACHED SEND END AS WELL TO CONFIRME THE CONNECTION BEEING CLOSED
                    break;
                }

                //AFTER X SECONDS AFTER NOT RECIEVING A PACKAGE USE THE TIMEOUT TO DO A PING REQUEST
                InetAddress client = InetAddress.getByName(clientIp);
                if (!client.isReachable(5000)) {
                    //IF PING HASN'T RESEND ANYTHING BRAKE THE CONNECTION AND DROP THE PACKAGES
                    socket.close();
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //SAVE THE FILE USING THE SAME NAME AS PROVIDED (DO THIS OUT OF THE WHILE) THEN CLOSE THE THREAD IF GOT AT LEAST ONE END OR AT LEAST THE FILENAME BUT DIDN'T PROPERLY CLOSE THE CONNECTION START THE FILE COMPOSITION PROCESS
        if (countEnd <= 1 || gotFilename) {
            String nameFile = Arrays.toString(fileName);
            try (FileOutputStream fos = new FileOutputStream("/path/" + nameFile)) {
                fos.write(finalFileData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //TODO ADD A MESSAGE WITH THE NAME OF THE FILE INTO THE ACTUAL CLIENT
        System.out.println("File has been saved.");
    }
}
