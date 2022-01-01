//Lista de serviços registados

import java.util.ArrayList;
import java.io.Serializable;


public class ListService implements Serializable {
    
    private ArrayList<Service> ListService = new ArrayList<Service>();

    public ArrayList<Service> getListService() {
        return ListService;
    }
    
    public void add(Service list){
        boolean newService = true;
        for (int i = 0; i < ListService.size(); i++){
            Service l = ListService.get(i);
            if (l.getkey().equals(list.getkey()))
            {
                //alteração da descrição, tipo de tecnologia e Ip
                ListService.get(i).setDesc(list.getDesc());
                ListService.get(i).settTecno(list.gettTecno());
                ListService.get(i).setIp(list.getIp());
                newService = false;
            }
        }
        if (newService){
            ListService.add(list);            
        }

    }
}