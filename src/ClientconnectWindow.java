import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;

public class ClientconnectWindow extends JFrame {
    JButton b;
    JButton readyb;

    public ClientconnectWindow(Clientinit cl) {

        b = new JButton("join group");
        readyb = new JButton("Currently: 0 participant");
        JTextField textArea = new JTextField("Give us your username");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(400, 400);
        readyb.setBounds(0, 0, 400, 100);//TODO FIX THIS
        b.setBounds(this.getWidth() / 2 - this.getWidth() / 8, this.getHeight() / 2 - this.getHeight() / 16, this.getWidth() / 4, this.getHeight() / 8);//TODO make it bigger
        textArea.setBounds(this.getWidth() / 2 - this.getWidth() / 8, this.getHeight() / 2 + this.getHeight() / 16, this.getWidth() / 4, this.getHeight() / 8);
        this.add(b);
        this.add(readyb);
        this.add(textArea);
        this.setLayout(null);
        this.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        WaitingForReadyClient wfrc = new WaitingForReadyClient(ClientconnectWindow.this, cl);
        readyb.addActionListener(e -> {
            String sending = "launch";
            byte[] buffer;
            buffer = sending.getBytes();
            DatagramPacket data = new DatagramPacket(buffer, buffer.length, cl.getServeraddress(), cl.getPort());
            try {
                cl.getSocket().send(data);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                byte[] buffer;
                String sending = "stop";
                buffer = sending.getBytes();
                DatagramPacket data = new DatagramPacket(buffer, buffer.length, cl.getServeraddress(), cl.getPort());
                try {
                    cl.getSocket().send(data);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                wfrc.sendStop();

            }
        });

        b.addActionListener(e -> {

            if(textArea.getText().length() <= 25){
                cl.getC().setUsername(textArea.getText().replace("\n", " "));
                byte[] buffer;
                try {
                    String sending = "join " + cl.getC().getUsername();
                    buffer = sending.getBytes();
                    DatagramPacket data = new DatagramPacket(buffer, buffer.length, cl.getServeraddress(), cl.getPort());
                    cl.getSocket().send(data);
                    b.setText("Welcome " + cl.getC().getUsername());
                    WaitingForReadyClient wfrc1 = new WaitingForReadyClient(ClientconnectWindow.this, cl);
                    new Thread(wfrc1).start();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else{
                textArea.setText("name <= 25 char");
            }
        });
    }
}
