package ProjSD.SI;

import java.net.*;
import java.io.*;


public class SI {
    //static ServerSocket variable
    private static ServerSocket server;
    static final String DEFAULT_HOST_SI="26.66.66.146";
    
    public static void main(String[] args){
        //socket server port on which it will listen
        int port = 2002;

        //creating socket and waiting for client connection
        ServerSocket server = null;
        Socket socket;
        //keep listens indefinitely until receives 'exit' call or program terminates

        
		try	{ 
			server = new ServerSocket(port);
		} catch (Exception e) { 
			System.err.println("erro ao criar socket servidor...");
			e.printStackTrace();
			System.exit(-1);
		}
				
        while (true) {
            try {
                
                socket = server.accept();
                SI_Hander sv = new SI_Hander(socket);
    
                Thread t1 = new Thread(sv);
                t1.start();
            } catch (IOException e) {
				System.out.println("Erro na execucao do servidor: "+e);
				System.exit(1);
			}
            
        }
    } 

}