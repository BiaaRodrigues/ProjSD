package ProjSD.ST;

import ProjSD.Data.ListService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * O ST é que é o verdadeiro Servidor mas apenas fica à espera de conexoes de clients, quando os clients se conectam
 * cria uma thread da classe Server que funciona como handler do Client
 */
public class ST {

    public static void main(String[] args){
        //socket server port on which it will listen
        int port = 2001;
        ServerSocket ss = null;
        
        Socket socket;
        // o ST tem um base de dados composta  por dois hashmaps (ver fich ListServices)
        ListService bd = new ListService();

        
		try	{ 
			ss = new ServerSocket(port);
		} catch (Exception e) { 
			System.err.println("erro ao criar socket servidor...");
			e.printStackTrace();
			System.exit(-1);
		}

        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            try {
                socket = ss.accept();
                // quando um client se conecta é criada um thread Server que recebe as msgs do Client e depois envia os dados à base de dados
                ST_Handler sv = new ST_Handler(socket, bd);
    
                Thread t1 = new Thread(sv);
                t1.start();
            } catch (IOException e) {
				System.out.println("Erro na execucao do servidor: "+e);
				System.exit(1);
			}
            
        }
    }
}
