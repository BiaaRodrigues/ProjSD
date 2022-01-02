package ProjSD.src.ST;

import ProjSD.src.Data.ListService;
import ProjSD.src.Data.Service;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Thread que corre quando um cliente se liga ao ST
 * é aqui que sao lidas as mensagens dos clients e que sao chamadas as funçoes para atualizar a base de dados do ST
 */
public class Server implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String utilizador;
    private ListService bd;

    public Server(Socket s, ListService bd) throws IOException {
        this.utilizador = "";
        this.out = new ObjectOutputStream(s.getOutputStream());
        this.in = new ObjectInputStream(s.getInputStream());
        this.bd = bd;
    }


    @Override
    public void run() {
        String msg;
        String[] parts;
        try {
            while(true) {
                // aqui recebe as mensagens do client
                msg = (String) in.readObject();
                System.out.println(msg);
                parts = msg.split("-");

                switch (parts[0]) {
                    case "1":
                        // se a msg começar com "1-etc..." como é nos logins entao faz login
                        logIn(parts[1], parts[2]);
                        break;
                    case "2":
                        // se começar com "2-..." é  porque é o registo de um serviço
                        int porta = Integer.parseInt(parts[4]);
                        if (parts[2].equalsIgnoreCase("rmi")){
                            registerRMI(parts[5], parts[1], parts[2], parts[3], porta, parts[6]);
                        } else if (parts[2].equalsIgnoreCase("socket")){
                            registerSocket(parts[5], parts[1], parts[2], parts[3], porta);
                        } else out.writeObject("RegistoInv");
                        //System.out.println(service.toString());

                        break;
                    // case 3: e hão de ser necessarios mais casos para outras funcionalidades
                    case "0":
                        break;
                }
            }
        }
        catch (IOException | NoSuchAlgorithmException | ClassNotFoundException ignored) { }
    }

    /*
     * Para o CLient ter acesso o @nif e a @hash têm que ser compativeis/iguais
     */
    public void logIn(String nif, String hash) throws IOException, NoSuchAlgorithmException {
        /* pegar no nif e calcular um hash */
        String myHash = calculate_md5_hash(nif);

        // ver se a hash calculada em cima é igual à hash dada pelo client
        if (myHash.equals(hash)){
            // se for entao manda a msg "TudoCerto" para o client
            out.writeObject("TudoCerto");
            this.utilizador = nif;
        }
        else {
            out.writeObject("HASHERRADA");
        }

    }

     /* 
        Registar um serviço RMI, é só criar um objeto Service com as informaçoes todas e chamar a funçao que adiciona o RMI
    */
    public void registerRMI(String key, String desc, String tTecno, String ip, int porto, String name) throws IOException {
        Service service = new Service(key, desc, tTecno, ip, porto, name);

        if(bd.addRMI(service) == 0){
            // se a funçao retornar 0 é porque o serviço RMI já existia e comunicamos isso ao client (ir ver a funçao addRMI no ListServices)
            out.writeObject("RMIExiste");
        } else {
            out.writeObject("RMIRegistado");
        }
        System.out.println(bd.getSvRMI());
    }

    // igual à de cima mas para socket
    public void registerSocket(String key, String desc, String tTecno, String ip, int porto) throws IOException {
        Service service = new Service(key, desc, tTecno, ip, porto, "");

        if(bd.addSocket(service) == 0){
            out.writeObject("SocketExiste");
        } else {
            out.writeObject("SocketRegistado");
        }
        System.out.println(bd.getSvSockets());
    }

    /* Converter NIF numa hash MD5, mesma funçao que ha no SI */
    private static String calculate_md5_hash(String nif_cliente) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(nif_cliente.getBytes());
        byte[] digest = md.digest();

        // Convert byte array into signum representation - https://www.geeksforgeeks.org/md5-hash-in-java/
        BigInteger no = new BigInteger(1, digest);

        String myHash = no.toString(16);
        while (myHash.length() < 32) {
            myHash = "0" + myHash;
        }
        return myHash;
    }
}