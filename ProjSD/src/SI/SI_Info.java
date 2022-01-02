package ProjSD.src.SI;
import java.io.Serializable;

/**
 * Classe SI_Info que representa o objeto que o SI comunica aos Clients.
 * O SI em vez de enviar uma string, envia um objeto SI_Info que tem a informaçao
 * da hash calculada, e da porta e do ip do ST
 */
public class SI_Info implements Serializable {

    private String hash;
    private String ip_st;
    private String porta_st;

    public SI_Info(String hash, String ip_st, String porta_st){
        this.hash = hash;
        this.ip_st = ip_st;
        this.porta_st = porta_st;
    }

    public String getHash() {
        return hash;
    }

    public String getPorta_st() {
        return porta_st;
    }

    public String getIp_st() {
        return ip_st;
    }
}
