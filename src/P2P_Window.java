import javax.swing.*;

public class P2P_Window extends JFrame {
    private Clientinfo clientinfo;

    public P2P_Window(Clientinfo clientinfo){
        this.clientinfo = clientinfo;
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(1920, 1080);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    //TODO files send should be on another thread such that it doesn't block the GUI
    //TODO long messages should be on another thread such that it doens't block the GUI
    //TODO make a button to connect and disconnet
    //TODO make a message actualiser that will update the scrolling pane
    //TODO make a textarea with a keylistener for enter that will send the message.
    //TODO keylistener should then send the message  to all the peers.
    //TODO text area should also allow for drag and drop
    //TODO methode that actualises the name on the right scrolling pane
    //TODO methode that actualises the clients connection button.
    //TODO add closing listener that will send /quit to each peers
    //TODO add the usernames in a scrolling pane with the color of their respectiv connection

}
