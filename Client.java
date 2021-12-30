import java.util.*;
import java.net.*;
import java.io.*;


public class Client {
    static final int DEFAULT_PORT_SI=2000;
    static final String DEFAULT_HOST="127.0.0.1";
   
    

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Socket socket = null;
        boolean running = true;
        Scanner scanner = new Scanner(System. in);

        int stPort = 0; //Variavel para guardar o porto do ST
        InetAddress stIP = null;//Variavel para guardar o IP do ST
        String hash_SI = ""; //Variavel para guardar a hash devolvida pelo SI
        

        while(running){
            int option = 0;

            System.out.println("A que servico te queres conectar?");
            System.out.println("1 - Servico de Identificacao");
            System.out.println("2 - Servico de Ticketing");
            System.out.println("3 - Sair");
            option = scanner.nextInt();

            switch (option){
                case 1:
                    scanner.nextLine();
                    System.out.println("A ligar ao SI ...\nQual o seu NIF?");
                    String nif = scanner.nextLine();
                    //establish socket connection to server
                    socket = new Socket(DEFAULT_HOST, DEFAULT_PORT_SI);

                    //write to socket using ObjectOutputStream
                    /* Vamos enviar o NIF para o SI a partir do qual ele vai calcular uma hash */
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(nif);

                    /* read the server response message
                       Do SI vamos receber a hash que vai servir para nos autenticarmos no ST
                     */
                    ois = new ObjectInputStream(socket.getInputStream());
                    hash_SI = (String) ois.readObject();
                    InetAddress ip = InetAddress.getLocalHost();

                    //Receber o porto do ST
                    stPort = (int) ois.readObject();
                    
                    //Receber IP do ST
                    stIP = InetAddress.getByName((String)ois.readObject());
                    
                    System.out.println("Hash do seu ID: "+hash_SI+"\n"+"IP: "+stIP+"\n"+"Porta do ST: "+stPort);

                    ois.close();
                    oos.close();
                    socket.close();

                    break;

                case 2:
                    System.out.println("A ligar ao ST ...");
                    //establish socket connection to server
                    socket = new Socket(stIP, stPort);
                    scanner.nextLine();
                    String nif_login, hash_login, msg;
                    System.out.println("Introduza o nif");
                    nif_login = scanner.nextLine();
                    System.out.println("Introduza a hash");
                    hash_login = scanner.nextLine();

                    msg = "1-" + nif_login + "-" + hash_login;
                    System.out.println(msg);
                    //write to socket using ObjectOutputStream
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(msg);

                    //read the server response message
                    ois = new ObjectInputStream(socket.getInputStream());
                    String response_st = (String) ois.readObject();

                    switch (response_st) {
                        case "TudoCerto":
                            System.out.println("Correto, ja tem acesso!!!");
                            // showSTMenu();
                            break;
                        case "HASHERRADA":
                            System.out.println("O nif nao corresponde a hash!");
                            break;
                    }

                    ois.close();
                    oos.close();
                    socket.close();
                    break;

                case 3:
                    System.out.println("A sair ...");
                    /* Só temos que colocar a variavel running a false para sair do ciclo while() */
                    running = false;
                    break;
            }
        }
    }
}
