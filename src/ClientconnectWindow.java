import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class ClientconnectWindow extends JFrame {
    public ClientconnectWindow(Clientinit cl) {

        JButton b = new JButton("join group");
        JTextField textArea = new JTextField("Give us your username");
        this.setResizable(false);
        this.setSize(400, 400);
        b.setBounds(this.getWidth() / 2 - this.getWidth() / 8, this.getHeight() / 2 - this.getHeight() / 16, this.getWidth() / 4, this.getHeight() / 8);
        textArea.setBounds(this.getWidth() / 2 - this.getWidth() / 8, this.getHeight() / 2 + this.getHeight() / 16, this.getWidth() / 4, this.getHeight() / 8);
        this.add(b);
        this.add(textArea);
        this.setLayout(null);
        this.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
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
                cl.getSocket().close();

            }
        });
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                cl.getC().setUsername(textArea.getText().replace("\n", " "));
                byte[] buffer = new byte[2048];
                try {
                    String sending = "join " + cl.getC().getUsername();
                    buffer = sending.getBytes();
                    DatagramPacket data = new DatagramPacket(buffer, buffer.length, cl.getServeraddress(), cl.getPort());
                    cl.getSocket().send(data);
                    b.setText("Welcome " + cl.getC().getUsername());
                    //TODO PUTT THIS PART IN A NEW THREAD
                    while (true) {

                        //flush the buffer
                        Arrays.fill(buffer, (byte) 0);
                        //wait for the ready message otherwise wait until enough persons joined in
                        data = new DatagramPacket(buffer, buffer.length,cl.getServeraddress(),cl.getPort());
                        cl.getSocket().receive(data);
                        String res = new String(data.getData(), 0, data.getLength());
                        if (!res.equals("ready")) {
                            continue;
                        }
                        break;
                    }
                    //flush the buffer
                    Arrays.fill(buffer, (byte) 0);
                    //wait for the information about the participants
                    data = new DatagramPacket(buffer, buffer.length);
                    cl.getSocket().receive(data);
                    cl.getC().readyparseMessage(new String(data.getData(), 0, data.getLength()));

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}
