//criação da lista de serviços registados

package ProjSD.src.Data;

//import Data.Service;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class ListService {

    private HashMap<String, Service> rmi_Services;
    private HashMap<String, Service> socket_Services;

    private ReentrantLock lock_RMI_Serv;
    private ReentrantLock lock_Sckt_Serv;

    public ListService(){
        rmi_Services = new HashMap<>();
        socket_Services = new HashMap<>();

        lock_RMI_Serv = new ReentrantLock();
        lock_Sckt_Serv = new ReentrantLock();
    }

    public int addRMI(Service rmi_service){
        String chave2 = rmi_service.getkey();

        lock_RMI_Serv.lock();
        if (rmi_Services.containsKey(chave2)) {
            lock_RMI_Serv.unlock();
            //System.out.println("Este Serviço já está registado");
            return 0;
        }
        rmi_Services.put(chave2, rmi_service);
        lock_RMI_Serv.unlock();
        return 1; //tudo ok
    }

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

    public String getSvRMI(){
        return rmi_Services.values().toString();
    }
    public String getSvSockets(){
        return socket_Services.values().toString();
    }
}