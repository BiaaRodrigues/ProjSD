package ProjSD.src.ST;

import Data.ListService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * O ST é que é o verdadeiro Servidor mas apenas fica à espera de conexoes de clients, quando os clients se conectam
 * cria uma thread da classe Server que funciona como handler do Client
 */
public class ST {

    public static void main(String[] args) throws IOException{
        //socket server port on which it will listen
        int port = 2001;
        ServerSocket ss = new ServerSocket(port);
        Socket socket;
        // o ST tem um base de dados composta  por dois hashmaps (ver fich ListServices)
        ListService bd = new ListService();

        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            socket = ss.accept();
            // quando um client se conecta é criada um thread Server que recebe as msgs do Client e depois envia os dados à base de dados
            Server sv = new Server(socket, bd);

            Thread t1 = new Thread(sv);
            t1.start();
        }
    }
}
