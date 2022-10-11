import java.io.IOException;
import java.net.ServerSocket;

/**
 * EchoServer is a simple server which accepts a connection and 
 * simply reads input and echos it back to the sender.
 * 
 * Code provided to enable testing of (1) sending/receiving messages from 
 * the server, and (2) updating a sketch based on messages.
 *
 * @author Travis Peters, Dartmouth CS 10, Winter 2015;
 */
public class EchoServer {

    private ServerSocket listen;  // for accepting connections
    
    public EchoServer(ServerSocket listen) {
        this.listen = listen;
    }

    ///////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////
    
    public void getConnections() throws IOException {
        while (true) {
            EchoServerCommunicator comm = new EchoServerCommunicator(listen.accept());
            comm.setDaemon(true);
            comm.start();
        }
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("Starting up the EchoServer...");
        new EchoServer(new ServerSocket(4242)).getConnections();        
    }
        
}
