import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

public class WaitingForReadyClient implements Runnable {
    ClientconnectWindow clientconnectWindow;

    Clientinit clientinit;
    boolean stop = false;
    public WaitingForReadyClient(ClientconnectWindow ccw,Clientinit clientinit){
        this.clientconnectWindow = ccw;
        this.clientinit = clientinit;
    }
    public void sendStop(){
        this.stop = true;
    }

    @Override
    public void run() {
        byte[] buffer;
        clientconnectWindow.b.setText("Waiting for connections");
        DatagramPacket data;
        boolean exit = false;
        while (true) {
            buffer = new byte[1024];

            //wait for the ready message otherwise wait until enough persons joined in
            data = new DatagramPacket(buffer, buffer.length);
            if(clientinit.getSocket().isClosed() || stop){
                exit = true;
                clientinit.getSocket().connect(clientinit.getServeraddress(),clientinit.getPort());
                String sending = "stop";
                buffer = sending.getBytes();
                data = new DatagramPacket(buffer, buffer.length, clientinit.getServeraddress(), clientinit.getPort());
                try {
                    clientinit.getSocket().send(data);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                clientinit.getSocket().close();
                return;

            }


            try {
                try{
                clientinit.getSocket().receive(data);
                }
                catch (SocketTimeoutException e){
                    if(stop){
                        String sending = "stop";
                        buffer = sending.getBytes();
                        data = new DatagramPacket(buffer, buffer.length, clientinit.getServeraddress(), clientinit.getPort());
                        try {
                            clientinit.getSocket().send(data);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        clientinit.getSocket().close();
                       return;
                    }
                    continue;
                }
            } catch (IOException e) {
                String sending = "stop";
                buffer = sending.getBytes();
                data = new DatagramPacket(buffer, buffer.length, clientinit.getServeraddress(), clientinit.getPort());
                try {
                    clientinit.getSocket().send(data);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                clientinit.getSocket().close();
                return;

            }
            String res = new String(data.getData(), 0, data.getLength());
            if (res.equals("ready")) {

                String sending = "handshake";
                buffer = sending.getBytes();
                data = new DatagramPacket(buffer, buffer.length, clientinit.getServeraddress(), clientinit.getPort());
                try {
                    clientinit.getSocket().send(data);
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }
                break;
            }
            else if (res.isEmpty()){
                System.out.println("packet was empty");
            }
            else if(res.matches("^[0-9]*[^a-zA-Z]")){
                int result = Integer.parseInt(res);
                clientconnectWindow.readyb.setText("Currently: " + result + " participant");
                clientconnectWindow.repaint();
            }

        }
        //flush the buffer
        buffer = new byte[2024];
        System.out.println("Ready message got");
        //wait for the information about the participants
        data = new DatagramPacket(buffer, buffer.length);
        try {
            clientinit.getSocket().receive(data);
            clientinit.getC().readyparseMessage(new String(data.getData(), 0, data.getLength()));
            for(Clientinfo client:clientinit.getC().getPeers()){
                System.out.println("Recieved "+ client.getUsername());
            }
            clientconnectWindow.readyb.setText("Currently: " + clientinit.getC().getPeers().size() + " participant");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new P2P_Window(clientinit.getC());


    }

}
