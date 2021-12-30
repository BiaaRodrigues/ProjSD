
import java.util.*;
import java.net.*;
import java.io.*;

public class ST{

	// Lista de Hashes
    List<String> hashes = new ArrayList<>();
	
    public static void main(String[] args) throws IOException{
        //socket server port on which it will listen
        int port = 2001;
        ServerSocket ss = new ServerSocket(port);
        Socket socket;

        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            socket = ss.accept();
            Server sv = new Server(socket);

            Thread t1 = new Thread(sv);
            t1.start();
        }
    }
}
