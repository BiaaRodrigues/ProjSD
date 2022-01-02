package ProjSD.src.SI;

import java.security.NoSuchAlgorithmException;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.math.BigInteger;


public class SI {
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 2000;

    // porta e ip do ST, que vamos enviar o client
    private static String port_st = "2001";
    private static String st_ip = "127.0.0.1";

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //creating socket and waiting for client connection
        server = new ServerSocket(port);

        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            Socket socket = server.accept();

            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            /*  convert ObjectInputStream object to Integer,
                vamos receber o que vem do Cliente, que neste caso é o NIF
                agora podemos pegar neste nif e calcular uma hash com ele
             */
            String nif_cliente = (String) ois.readObject();
            System.out.println(nif_cliente);

            /* pegar no nif e calcular um hash */
            String myHash = calculate_md5_hash(nif_cliente);
            /* Criar um objeto SI_Info que tem 3 Strings com informaçao: a hash, e o ip e porta do ST */
            SI_Info info = new SI_Info(myHash, st_ip, port_st);

            /*  create ObjectOutputStream object
                Enviar hash calculada para o cliente */
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write answer(hash) to Socket
            oos.writeObject(info);

            /*terminate the server
            ois.close();
            oos.close();
            socket.close();
            server.close();*/
        }
    }

    /**
     * Converter NIF numa hash MD5
     * Recebe @nif_cliente e retorna a hash MD5
     * */
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