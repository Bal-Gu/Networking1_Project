import java.io.IOException;

public class ServerLauncher {
    public static void main(String[] args) throws IOException {
        CentralizedServer c = new CentralizedServer();
        c.start();
    }
}
