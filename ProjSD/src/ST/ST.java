package ProjSD.src.ST;

import Data.ListService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ST {

    public static void main(String[] args) throws IOException{
        //socket server port on which it will listen
        int port = 2001;
        ServerSocket ss = new ServerSocket(port);
        Socket socket;
        ListService bd = new ListService();

        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            socket = ss.accept();

            Server sv = new Server(socket, bd);

            Thread t1 = new Thread(sv);
            t1.start();
        }
    }
}
