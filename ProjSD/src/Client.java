package ProjSD.src;
import java.util.*;
import java.net.*;
import java.io.*;

import ProjSD.src.Data.ListService;
//import Data.Service;
import ProjSD.src.SI.SI_Info;

/**
 * Classe Client que se liga ao SI e ao ST
 */
public class Client {
    static final int DEFAULT_PORT_SI=2000;
    static final int DEFAULT_PORT_ST=2001;
    static final String DEFAULT_HOST="127.0.0.1";
    private static Scanner scanner = new Scanner(System. in);
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Socket socket = null;
        boolean running = true;

        while(running){
            int option = 0;

            System.out.println("A que serviço te queres conectar?");
            System.out.println("1 - Serviço de Identificação");
            System.out.println("2 - Serviço de Ticketing");
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

                    /* read the server response message:
                       Do SI vamos receber um objeto SI_Info que contem a hash que vai servir para nos autenticarmos no ST
                       e contem tambem um o ip e porta do ST
                     */
                    ois = new ObjectInputStream(socket.getInputStream());
                    SI_Info info_SI = (SI_Info) ois.readObject();
                    System.out.println("Hash do seu ID: "+info_SI.getHash());
                    System.out.println("IP do ST: "+info_SI.getIp_st());
                    System.out.println("Porta do ST: "+info_SI.getPorta_st());

                    ois.close();
                    oos.close();
                    socket.close();

                    break;

                case 2:
                    System.out.println("A ligar ao ST ...");
                    //establish socket connection to server
                    socket = new Socket(DEFAULT_HOST, DEFAULT_PORT_ST);
                    scanner.nextLine();
                    // se nos ligarmos ao ST temos que pedir nif e hash ao cliente de modo a se autenticar:
                    String nif_login, hash_login, msg;
                    System.out.println("Introduza o nif");
                    nif_login = scanner.nextLine();
                    System.out.println("Introduza a hash");
                    hash_login = scanner.nextLine();
                    
                    // enviar uma string do tipo "1-NIF-HASH"
                    msg = "1-" + nif_login + "-" + hash_login;
                    System.out.println(msg);
                    //write to socket using ObjectOutputStream
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(msg);

                    //read the server response message
                    ois = new ObjectInputStream(socket.getInputStream());
                    String response_st = (String) ois.readObject();

                    // se o ST responder com "TudoCerto" entao está tudo ok e podemos mostrar o menu do ST -> showSTMenu()
                    switch (response_st) {
                        case "TudoCerto":
                            System.out.println("Tudo Certo, ganhou acesso!");
                            showSTMenu();
                            break;
                        case "HASHERRADA":
                            System.out.println("O nif nao corresponde à hash!");
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

    /**
     * Menu do ST, podemos registar e ver/pedir os serviços (só a parte do registar é que ta feita)
     */
    public static void showSTMenu() throws IOException, InterruptedException, ClassNotFoundException {
        boolean x = true;

        while (x) {
            System.out.println("O que deseja fazer?");
            System.out.println("1 - Registar Serviço");
            System.out.println("2 - Ver Serviços");
            System.out.println("3 - Voltar");
            int n = scanner.nextInt();
            switch (n) {
                case 1:
                    registerSv();
                    //x = false;
                    break;
                case 2:
                    System.out.println("Que Serviços quer ver?");
                    System.out.println("1 - RMI");
                    System.out.println("2 - Sockets");
                    int y = scanner.nextInt();
                    if(y == 1) {
                        //System.out.println("Lista RMI");
                       String listRMI = getSvRMI();
                       System.out.println(listRMI);

                    }
                    else {
                        //System.out.println("Lista Sockets");
                        String listSocket = getSvRMI();
                        System.out.println(listSocket);
                    }
                    //x = false;
                    break;
                case 3:
                    x = false;
                    break;
            }
        }
    }

    /**
     * Funçao que pede a informaçao ao cliente para depois ser usada no ST para registar um serviço novo
     */
    public static void registerSv() throws IOException, ClassNotFoundException {
        scanner.nextLine();
        String nome, descricao, comunicacao, iP, chave, msg, porta;


        System.out.println("Introduza a descricao");
        descricao = scanner.nextLine();
        System.out.println("Introduza o tipo de comunicaçao (Socket ou RMI)");
        comunicacao = scanner.nextLine();
        System.out.println("Introduza o IP");
        iP = scanner.nextLine();
        System.out.println("Introduza a porta");
        porta = scanner.nextLine();

        if (comunicacao.equals("rmi")){
            // só os serviços rmi é que têm um nome, ver no enunciado
            System.out.println("Introduza o nome do RMI");
            nome = scanner.nextLine();
            chave = iP+porta+nome;
            msg = "2-" + descricao + "-" + comunicacao + "-" + iP + "-" + porta + "-" + chave + "-" + nome;
        } else {
            chave = iP+porta;
            msg = "2-" + descricao + "-" + comunicacao + "-" + iP + "-" + porta + "-" + chave;
        }

        // enviamos uma string com a info toda para o ST, como a string começa por "2-", o ST sabe que é info para registar um serviço
        // a maneira como a chave unica do serviço é calculada está explicada no enunciado -> ip+porta para os sockets ...

        System.out.println("Chave de Registo: " + chave);


        System.out.println(msg);

        //enviar a string para o ST
        oos.writeObject(msg);

        //oos.newLine();

        msg = (String) ois.readObject();
        // conforme o que o ST responde, damos a informçao ao cliente
        switch (msg) {
            case "RMIExiste" : 
                System.out.println("Este RMI já existe!");
                break;
            
            case "RMIRegistado" : 
                System.out.println("Serviço RMI Criado com Sucesso!");
                break;
            
            case "SocketExiste" : 
                System.out.println("Este Socket já existe!");
                break;
            
            case "SocketRegistado" : 
                System.out.println("Serviço Socket Criado com Sucesso!");
                break;
            
            case "RegistoInv" : 
                System.out.println("Registo Invalido, no tipo de comun tem que ser 'rmi' ou 'socket'");
                break;
        }
    }
}
