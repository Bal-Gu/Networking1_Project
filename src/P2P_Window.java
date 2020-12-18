import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class P2P_Window extends JFrame {
    private Clientinfo clientinfo;
    private JLabel username;
    private JPanel leftPanel, MiddlePanel, RightPanel;
    private JButton connectedButton;
    private JScrollPane MessagePane, UsernamePane;

    public P2P_Window(Clientinfo clientinfo) {
        this.clientinfo = clientinfo;

        this.setResizable(false);
        this.setSize(1920, 1080);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Sets the Panels
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        leftPanel = new JPanel();
        GridBagLayout gridleft = new GridBagLayout();
        MiddlePanel = new JPanel();
        updateUsername(MiddlePanel);
        MessagePane = new JScrollPane(MiddlePanel);
        MessagePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        MessagePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        RightPanel = new JPanel();
        updateUsername(RightPanel);
        UsernamePane = new JScrollPane(RightPanel);
        UsernamePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        UsernamePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.setLayout(gb);

        //ADDING them to the main frame
        c.weightx = 0.2;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 3;
        c.fill = GridBagConstraints.BOTH;
        this.add(leftPanel, c);
        c.weightx = 1;
        c.gridx = 1;
        this.add(MessagePane, c);
        c.weightx = 0.5;
        c.gridx = 2;
        this.add(UsernamePane, c);


        //Set's the username
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.gridwidth = 3;
        c.gridheight = 1;
        leftPanel.setLayout(gridleft);
        this.username = new JLabel(clientinfo.getUsername());
        this.username.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 50));
        leftPanel.add(username, c);
        username.setVisible(true);

        //finish the leftPanel
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.weightx = 1;
        connectedButton = new JButton("Connected");
        leftPanel.add(connectedButton, c);
        connectedButton.setVisible(true);
        connected();



        //sets the ButtonListener
        connectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connectedButton.getText().equals("Connected")) {
                    disconnect();
                } else {
                    reconnected();
                }
            }
        });

        //In case the user press the exit button
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //TODO send /quit to every peers
                super.windowClosed(e);
            }
        });

        for (int i = 0; i < 200; i++) {

            RightPanel.add(new JLabel("Fuck"));
        }
        this.setVisible(true);
    }

    public void changeUsername(String s) {
        this.username.setText(s);
        //TODO send the messages to the peers
    }

    public void actualisesUsernameList() {
        //TODO add all usernames to the Pane. (Clear it bevor)
    }

    public void connected() {
        connectedButton.setBackground(new Color(11, 102, 35));
        connectedButton.setText("Connected");
        connectedButton.setForeground(new Color(255, 255, 255));
        connectedButton.setFont(new Font(Font.SERIF, Font.BOLD, 60));

    }

    public void disconnect() {
        connectedButton.setBackground(new Color(128, 0, 0));
        connectedButton.setText("Disconnected");
    }

    public void reconnected() {
        //TODO send RECONNECTION
        connected();
    }

    public void updateUsername(JPanel p){
        p.setVisible(true);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.CENTER;
        for (int i = 0; i < 100; i++)
        {
            c.gridx = i;
            JLabel label = new JLabel("Label " + i);
            p.add(label,c);
        }
    }
    //TODO files send should be on another thread such that it doesn't block the GUI
    //TODO long messages should be on another thread such that it doens't block the GUI
    //TODO make a message actualiser that will update the scrolling pane
    //TODO make a textarea with a keylistener for enter that will send the message.
    //TODO keylistener should then send the message  to all the peers.
    //TODO text area should also allow for drag and drop
    //TODO methode that actualises the name on the right scrolling pane
    //TODO methode that actualises the clients connection button.
    //TODO add closing listener that will send /quit to each peers
    //TODO add the usernames in a scrolling pane with the color of their respectiv connection

}
