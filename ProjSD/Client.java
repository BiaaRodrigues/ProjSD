package ProjSD;
import java.util.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.io.*;

//import Data.Service;
import ProjSD.SI.SI_Info;
import ProjSD.ST.STemp.ServicesInterface;
import java.time.Instant;

/**
 * Classe Client que se liga ao SI e ao ST
 */
public class Client {
    static final int DEFAULT_PORT_SI=2002;
    static final int DEFAULT_PORT_ST=2001;
    static final String DEFAULT_HOST="127.0.0.1";
    private static Scanner scanner = new Scanner(System. in);
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static Socket socket = null;
    //ListService x = new ListSevice();

    public static void main(String[] args) {

        
        boolean running = true;

        while(running){
            int option = 0;

            System.out.println("A que serviço te queres conectar?");
            System.out.println("1 - Serviço de Identificação");
            System.out.println("2 - Serviço de Ticketing");
            System.out.println("3 - Aceder a Serviços que temos Ticket");
            System.out.println("4 - Sair");
            option = scanner.nextInt();

            switch (option){
                case 1:
                    showSIMenu();
                    break;

                case 2:
                    System.out.println("A ligar ao ST ...");
                    
                    try {
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
                        //System.out.println(msg);
                        //write to socket using ObjectOutputStream
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(msg);
                        
                        //read the server response message
                        ois = new ObjectInputStream(socket.getInputStream());
                        String response_st;

                        response_st = (String) ois.readObject();
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

                        // fechar canais de ligaçao
                        ois.close();
                        oos.close();
                        socket.close();

                    } catch (IOException | ClassNotFoundException e2) {
                        // TODO Auto-generated catch block
                        //e2.printStackTrace();
                        System.out.println("Erro na ligação ao SI");
                    }
                    
                    break;

                case 3:
                    scanner.nextLine();
                    System.out.println("A que serviço se quer ligar?");
                    System.out.println("1 - RMI");
                    System.out.println("2 - Sockets");
                    int z = scanner.nextInt();

                    if(z == 1) {
                        scanner.nextLine();
                        System.out.println("Qual o IP do serviço RMI que se quer conectar (Serviço temp está no 127.0.0.1):");
                        String answer_rmi = scanner.nextLine();

                        try {
                            /*ligação ao serviço de temperatura*/
                            ServicesInterface mensagem;
                            mensagem = (ServicesInterface) LocateRegistry.getRegistry(answer_rmi).lookup("/TemperatureService");
                            // É só mandar o timestamp e invocar o getTemp, nao esquecer meter o serviço a correr -> STemp/ServerApp.java
                            Instant timestamp_now = Instant.now();
                            Float response_temp = mensagem.getTemp(timestamp_now);
                            System.out.println("Valor da temperatura: " +response_temp);
                        } catch (NotBoundException | RemoteException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            System.out.println("O serviço RMI com o IP " + answer_rmi + " não existe ou não está a funcionar!");
                        }
                    }

                    if(z == 2){
                        // aqui deviamos imprimir todos os serviços de sockets dos quais temos um ticket, mas para ja está a ir direto para o serviço da humidade
                        // nao esquecer meter o serviço a correr -> SHum/ServiceHumidityServer.java
                        BufferedReader in;
                        PrintWriter out;
                        scanner.nextLine();
                        System.out.println("Qual o IP do serviço Socket que se quer conectar (Serviço Hum está no 127.0.0.1):");
                        String answer_socket = scanner.nextLine();
                        System.out.println("Qual a porta do serviço? (Serviço Hum está na porta 2000):");
                        int answer_porta = scanner.nextInt();


                        try {
                            socket = new Socket(answer_socket, answer_porta);
                            in = new BufferedReader (new InputStreamReader(socket.getInputStream()));
                            out = new PrintWriter(socket.getOutputStream());
                            

                            // o serviço de humidade funciona enviando uma string com "getHumidity + @timestamp", TEM QUE TER UM ESPAÇO ENTRE O GETHUMIDITY E O TIMESTAMP
                            // sacar o timestamp atual:
                            Instant now_instant = Instant.now();
                            // fazer a string e enviar pelo socket
                            String humidity_msg = "getHumidity "+now_instant;
                            System.out.println(humidity_msg);
                            out.println(humidity_msg);
                            out.flush(); //lol isto nao era preciso com o ObjectOutputStream 🥱🤷‍♂️ mas temos q usar BufferedReader/PrintWriter pq sao os usados no Serviço de Hum

                            // ler a resposta e imprimir
                            String response_humidity_status = (String) in.readLine(); //aqui recebes o "200 OK" que quer dizer que foi um pedido efetuado com sucesso
                            String response_humidity_value = (String) in.readLine(); //aqui recebes o valor da humidade
                            System.out.println("Status: "+ response_humidity_status + " | Valor da humidade: "+response_humidity_value);

                            in.close();
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            System.out.println("O serviço Socket no IP" + answer_socket + ":" + answer_porta + " Não está a funcionar");
                        }
                        
                    }
                    break;
                case 4:
                    System.out.println("A sair ...");
                    /* Só temos que colocar a variavel running a false para sair do ciclo while() */
                    running = false;
                    break;
            }
        }
    }

    public static void showSIMenu(){
        scanner.nextLine();
        System.out.println("A ligar ao SI ...\nQual o seu NIF?");
        String nif = scanner.nextLine();
        //establish socket connection to server
        try {
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
            SI_Info info_SI;
            
            info_SI = (SI_Info) ois.readObject();
            System.out.println("Hash do seu ID: "+info_SI.getHash());
            System.out.println("IP do ST: "+info_SI.getIp_st());
            System.out.println("Porta do ST: "+info_SI.getPorta_st());

            
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
            System.out.println("Servidor de Identificaçao não está disponivel, tente mais tarde");
        }

        

    }

    /**
     * Menu do ST, podemos registar e ver os serviços registados
     */
    public static void showSTMenu() throws IOException {
        boolean x = true;

        while (x) {
            System.out.println("O que deseja fazer?");
            System.out.println("1 - Registar Serviço");
            System.out.println("2 - Consultar Serviços");
            System.out.println("3 - Voltar");
            int n = scanner.nextInt();
            switch (n) {
                case 1:
                    registerSv();
                    //x = false;
                    break;
                case 2:
                    System.out.println("Que Serviços quer consultar?");
                    System.out.println("1 - RMI");
                    System.out.println("2 - Sockets");
                    int y = scanner.nextInt();
                    

                    if(y == 1) {
                        //tenho de conectar ao ST e lá é que faço um metodo-> um for e um println dos valores
                        // do cliente, como não sabemos o tamanho, fazemos in.readnextline, vamos ter fe zer um try 
                        //e um catch
                        /*
                        try{while(true){System.out.println(in.readnextLine());} }catch(Exception e){}*/
                        oos.writeObject("3-getRMIList");
                        String response_st;
                        try {
                            response_st = (String) ois.readObject();
                            System.out.println(response_st);
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            System.out.println("O servidor nao está disponivel");
                        }
                        
                    }
                    else if (y == 2){
                        oos.writeObject("3-getSocketList");
                        String response_st;
                        try {
                            response_st = (String) ois.readObject();
                            System.out.println(response_st);
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            System.out.println("O servidor nao está disponivel");
                        }
                    }
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
    public static void registerSv() throws IOException {
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
            chave = iP+":"+porta+"/"+nome;
            msg = "2-" + descricao + "-" + comunicacao + "-" + iP + "-" + porta + "-" + chave + "-" + nome;
        } else {
            chave = iP+":"+porta;
            msg = "2-" + descricao + "-" + comunicacao + "-" + iP + "-" + porta + "-" + chave;
        }

        // enviamos uma string com a info toda para o ST, como a string começa por "2-", o ST sabe que é info para registar um serviço
        // a maneira como a chave unica do serviço é calculada está explicada no enunciado -> ip+porta para os sockets ...

        System.out.println("Chave de Registo: " + chave);


        //System.out.println(msg);

        //enviar a string para o ST
        oos.writeObject(msg);

        //oos.newLine();

        try {
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
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println("Servidor está com problemas, tente mais tarde");
        }
        
    }
}
