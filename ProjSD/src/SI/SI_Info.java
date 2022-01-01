
import java.io.Serializable;

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
