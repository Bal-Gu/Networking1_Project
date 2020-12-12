import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;

public class ClientconnectWindow extends JFrame{
    public ClientconnectWindow(Clientinit cl){

        JButton b=new JButton("join group");
        JTextField textArea = new JTextField("Give us your username");
        this.setResizable(false);
        this.setSize(400,400);
        b.setBounds(this.getWidth()/2 - this.getWidth()/8 ,this.getHeight()/2 -this.getHeight()/16,this.getWidth()/4, this.getHeight()/8);
        textArea.setBounds(this.getWidth()/2 - this.getWidth()/8 ,this.getHeight()/2 +this.getHeight()/16,this.getWidth()/4, this.getHeight()/8);
        this.add(b);
        this.add(textArea);
        this.setLayout(null);
        this.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                cl.getSocket().close();
                cl.getC().removeFromPeers();
            }
        });
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(textArea.getText());
                cl.getC().setUsername(textArea.getText().replace("\n"," "));
                byte[] buffer = new byte[ 2048 ];
                try {
                    String sending = "join " + cl.getC().getUsername();
                    buffer = sending.getBytes();
                    DatagramPacket data = new DatagramPacket(buffer, buffer.length, cl.getServeraddress(), cl.getPort());
                    cl.getSocket().send(data);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}
