
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Server implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String utilizador;

    public Server(Socket s) throws IOException {
        this.utilizador = "";
        this.out = new ObjectOutputStream(s.getOutputStream());
        this.in = new ObjectInputStream(s.getInputStream());
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