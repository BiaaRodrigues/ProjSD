package ProjSD;
import java.util.*;
import java.net.*;
import java.rmi.NotBoundException;
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
    //ListService x = new ListSevice();

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, NotBoundException {

        Socket socket = null;
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
                    //System.out.println(msg);
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
                    scanner.nextLine();
                    System.out.println("A que serviço se quer ligar?");
                    System.out.println("1 - RMI");
                    System.out.println("2 - Sockets");
                    int z = scanner.nextInt();

                    if(z == 1) {
                        // aqui deviamos imprimir todos os serviços de RMI dos quais temos um ticket, mas para ja está a ir direto para o serviço da temp
                        /*ligação ao serviço de temperatura*/
                        ServicesInterface mensagem= (ServicesInterface) LocateRegistry.getRegistry("127.0.0.1").lookup("/TemperatureService");
                        // É só mandar o timestamp e invocar o getTemp
                        // nao esquecer meter o serviço a correr -> STemp/ServerApp.java
                        Instant timestamp_now = Instant.now();
                        Float response_temp = mensagem.getTemp(timestamp_now);
                        System.out.println(response_temp);
                    }
                    if(z == 2){
                        // aqui deviamos imprimir todos os serviços de sockets dos quais temos um ticket, mas para ja está a ir direto para o serviço da humidade
                        // nao esquecer meter o serviço a correr -> SHum/ServiceHumidityServer.java
                        BufferedReader in;
                        PrintWriter out;
                        // a porta do serviço de humidade é a 2000, foi o prof que definiu no codigo do SHum
                        socket = new Socket(DEFAULT_HOST, 2000);
                        in = new BufferedReader (new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream());

                        // o serviço de humidade funciona enviando uma string com "getHumidity + @timestamp", TEM QUE TER UM ESPAÇO ENTRE O GETHUMIDITY E O TIMESTAMP
                        // sacar o timestamp atual:
                        Instant now_instant = Instant.now();
                        // fazer a string e enviar pelo socket
                        String humidity_msg = "getHumidity "+now_instant;
                        System.out.println(humidity_msg);
                        out.println(humidity_msg);
                        out.flush(); //lol merdas que nao eram preciso com o ObjectOutputStream 🥱🤷‍♂️ mas temos q usar BufferedReader/PrintWriter pq sao os usados no Serviço de Hum

                        // ler a resposta e imprimir
                        String response_humidity_status = (String) in.readLine(); //aqui recebes o "200 OK" que quer dizer que foi um pedido efetuado com sucesso
                        String response_humidity_value = (String) in.readLine(); //aqui recebes o valor da humidade
                        System.out.println("Valor da humidade: "+response_humidity_value);

                        in.close();
                        out.close();
                        socket.close();
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

    /**
     * Menu do ST, podemos registar e ver/pedir os serviços (só a parte do registar é que ta feita)
     * @throws NotBoundException
     */
    public static void showSTMenu() throws IOException, InterruptedException, ClassNotFoundException, NotBoundException {
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
                        oos.writeObject("3-getRMIList");
                        String response_st = (String) ois.readObject();
                        System.out.println(response_st);
                        //tenho de conectar ao ST e lá é que faço um metodo-> um for e um println dos valores
                        // do cliente, como não sabemos o tamanho, fazemos in.readnextline, vamos ter fe zer um try 
                        //e um catch
                        /*
                        try{
                            while(true){
                                System.out.println(in.readnextLine());
                            }
                        }catch(Exception e){}*/
                    }
                    else if (y == 2){
                        oos.writeObject("3-getSocketList");
                        String response_st = (String) ois.readObject();
                        System.out.println(response_st);
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
            chave = iP+":"+porta+"/"+nome;
            msg = "2-" + descricao + "-" + comunicacao + "-" + iP + "-" + porta + "-" + chave + "-" + nome;
        } else {
            chave = iP+":"+porta;
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