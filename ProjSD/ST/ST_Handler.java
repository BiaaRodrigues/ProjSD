package ProjSD.ST;

import ProjSD.Data.ListService;
import ProjSD.Data.Service;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Thread que corre quando um cliente se liga ao ST
 * é aqui que sao lidas as mensagens dos clients e que sao chamadas as funçoes para atualizar a base de dados do ST
 */
public class ST_Handler implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ListService bd;

    public ST_Handler(Socket s, ListService bd) {
        try {
            this.out = new ObjectOutputStream(s.getOutputStream());
            this.in = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("Erro a criar variaveis");
        }
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
                    case "3":
                        if (parts[1].equals("getSocketList")) consultSocket();
                        else if (parts[1].equals("getRMIList")) consultRmi();
                    // case 4: se for necessarios mais casos para outras funcionalidades
                    case "0":
                        break;
                }
            }
        }
        catch (IOException | ClassNotFoundException ignored) { }
    }

    /*
     * Para o CLient ter acesso o @nif e a @hash têm que ser compativeis/iguais
     */
    public void logIn(String nif, String hash) {
        /* pegar no nif e calcular um hash */
        String myHash = calculate_md5_hash(nif);

        // ver se a hash calculada em cima é igual à hash dada pelo client
        if (myHash.equals(hash)){
            // se for entao manda a msg "TudoCerto" para o client
            try {
                out.writeObject("TudoCerto");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            try {
                out.writeObject("HASHERRADA");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

     /* 
        Registar um serviço RMI, é só criar um objeto Service com as informaçoes todas e chamar a funçao que adiciona o RMI
    */
    public void registerRMI(String key, String desc, String tTecno, String ip, int porto, String name) {
        Service service = new Service(key, desc, tTecno, ip, porto, name);

        if(bd.addRMI(service) == 0){
            // se a funçao retornar 0 é porque o serviço RMI já existia e comunicamos isso ao client (ir ver a funçao addRMI no ListServices)
            try {
                out.writeObject("RMIExiste");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                out.writeObject("RMIRegistado");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(bd.getSvRMI());
    }

    // igual à de cima mas para socket
    public void registerSocket(String key, String desc, String tTecno, String ip, int porto) {
        Service service = new Service(key, desc, tTecno, ip, porto, "");

        if(bd.addSocket(service) == 0){
            try {
                out.writeObject("SocketExiste");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                out.writeObject("SocketRegistado");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(bd.getSvSockets());
    }

    public void consultSocket(){
        String table = bd.getSvSockets();
        try {
            out.writeObject(table);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void consultRmi(){
        String table = bd.getSvRMI();
        try {
            out.writeObject(table);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /* Converter NIF numa hash MD5, mesma funçao que ha no SI */
    private static String calculate_md5_hash(String nif_cliente) {
        MessageDigest md;
        String myHash = "";
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(nif_cliente.getBytes());
            byte[] digest = md.digest();

            // Convert byte array into signum representation - https://www.geeksforgeeks.org/md5-hash-in-java/
            BigInteger no = new BigInteger(1, digest);

            myHash = no.toString(16);
            while (myHash.length() < 32) {
                myHash = "0" + myHash;
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println("Este Algoritmo de encriptaçao não existe");
        }
        
        return myHash;
    }
}