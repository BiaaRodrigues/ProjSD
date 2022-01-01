package ProjSD.src.ST;

import Data.ListService;
import Data.Service;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
                msg = (String) in.readObject();
                System.out.println(msg);
                parts = msg.split("-");

                switch (parts[0]) {
                    case "1":
                        logIn(parts[1], parts[2]);
                        break;
                    case "2":
                        int porta = Integer.parseInt(parts[4]);
                        if (parts[2].equalsIgnoreCase("rmi")){
                            registerRMI(parts[5], parts[1], parts[2], parts[3], porta, parts[6]);
                        } else if (parts[2].equalsIgnoreCase("socket")){
                            registerSocket(parts[5], parts[1], parts[2], parts[3], porta);
                        } else out.writeObject("RegistoInv");
                        //System.out.println(service.toString());

                        break;
                    case "0":
                        break;
                }
            }
        }
        catch (IOException | NoSuchAlgorithmException | ClassNotFoundException ignored) { }
    }

    /* check if hash of nif is equal to the hash */
    public void logIn(String nif, String hash) throws IOException, NoSuchAlgorithmException {
        /* pegar no nif e calcular um hash */
        String myHash = calculate_md5_hash(nif);

        if (myHash.equals(hash)){
            out.writeObject("TudoCerto");
            this.utilizador = nif;
        }
        else {
            out.writeObject("HASHERRADA");
        }

    }

    public void registerRMI(String key, String desc, String tTecno, String ip, int porto, String name) throws IOException {
        Service service = new Service(key, desc, tTecno, ip, porto, name);

        if(bd.addRMI(service) == 0){
            out.writeObject("RMIExiste");
        } else {
            out.writeObject("RMIRegistado");
        }
        System.out.println(bd.getSvRMI());
    }

    public void registerSocket(String key, String desc, String tTecno, String ip, int porto) throws IOException {
        Service service = new Service(key, desc, tTecno, ip, porto, "");

        if(bd.addSocket(service) == 0){
            out.writeObject("SocketExiste");
        } else {
            out.writeObject("SocketRegistado");
        }
        System.out.println(bd.getSvSockets());
    }

    /* Converter NIF numa hash MD5 */
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