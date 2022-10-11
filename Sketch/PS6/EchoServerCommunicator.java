import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class EchoServerCommunicator extends Thread {
    private Socket sock;
    private BufferedReader in;  // from client
    private PrintWriter out;    // to client

    public EchoServerCommunicator(Socket sock) {
        this.sock = sock;
    }

    public void run() {
        try {
            System.out.println("editor connected for testing...");

            // Communication channel
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);

            // Echo loop
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("received: " + line);
                send(line);
            }

            // Clean up
            out.close();
            in.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        System.out.println("send: " + msg);
        out.println(msg);
    }
}
