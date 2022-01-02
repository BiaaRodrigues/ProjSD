//criação da lista de serviços registados

package ProjSD.src.Data;

//import Data.Service;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe ListService que é um objeto que basicamente funciona como base de dados do ST
 * A aplicaçao ST tem um ListService que  é composto por dois hashmaps: um com os serviços rmi e outro com os serviços sockets
 */

public class ListService {

    // a String é a chave (unica) do serviço
    private HashMap<String, Service> rmi_Services;
    private HashMap<String, Service> socket_Services;

    // locks servem para bloquear qualquer acesso ao hashmap. imagina que estao 2 clientes a meter coisas no hashmap, pode dar asneira
    // se tiverem os dois  a tentar a meter o mesmo serviço
    // com locks o primeiro a chegar dá lock e depois de inserir dá unlock. 
    // ver se podemos usar isto
    private ReentrantLock lock_RMI_Serv;
    private ReentrantLock lock_Sckt_Serv;

    public ListService(){
        rmi_Services = new HashMap<>();
        socket_Services = new HashMap<>();

        lock_RMI_Serv = new ReentrantLock();
        lock_Sckt_Serv = new ReentrantLock();
    }

    // adicionar um Serviço RMI ao hashmap dos rmi services
    public int addRMI(Service rmi_service){
        String chave2 = rmi_service.getkey();

        lock_RMI_Serv.lock();
        //vê se já existe alguma entrada no hashmap com a key
        if (rmi_Services.containsKey(chave2)) {
            lock_RMI_Serv.unlock();
            //System.out.println("Este Serviço já está registado");
            return 0;
        }
        // se nao tiver entao mete lá o serviço
        rmi_Services.put(chave2, rmi_service);
        lock_RMI_Serv.unlock();
        return 1; //tudo ok
    }

    // igual ao de cima mas para o hashmap de sockets
    public int addSocket(Service sckt_service){
        String chave2 = sckt_service.getkey();

        lock_Sckt_Serv.lock();
        if (socket_Services.containsKey(chave2)) {
            lock_Sckt_Serv.unlock();
            //System.out.println("Este Serviço já está registado");
            return 0;
        }
        socket_Services.put(chave2, sckt_service);
        lock_Sckt_Serv.unlock();
        return 1; //tudo ok
    }

    // passar os hashmap para string
    public String getSvRMI(){
        return rmi_Services.values().toString();
    }
    public String getSvSockets(){
        return socket_Services.values().toString();
    }
}