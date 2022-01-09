package ProjSD.src.SI;

import java.net.*;
import java.io.*;


public class SI {
    //static ServerSocket variable
    private static ServerSocket server;

    public static void main(String[] args) throws IOException, ClassNotFoundException{
        //socket server port on which it will listen
        int port = 2002;

        //creating socket and waiting for client connection
        ServerSocket server = new ServerSocket(port);

        Socket socket;
        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {

            socket = server.accept();
            SI_Hander sv = new SI_Hander(socket);

            Thread t1 = new Thread(sv);
            t1.start();
            
        }
    } 

}