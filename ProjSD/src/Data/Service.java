//criação do serviço

package ProjSD.src.Data;

import java.io.Serializable;

public class Service implements Serializable{

    private String key;
    private String desc;
    private String tTecno;
    private String ip;
    private int porto;
    private String name;

    public Service(String key, String desc, String tTecno, String ip, int porto, String name) {
        this.key = key;
        this.desc = desc;
        this.tTecno = tTecno;
        this.ip = ip;
        this.porto = porto;
        this.name = name;
    }

    public String getkey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public String gettTecno() {
        return tTecno;
    }

    public String getIp() {
        return ip;
    }

    public int getPorto() {
        return porto;
    }

    public String getname() {
        return name;
    }

    public void setkey(String key) {
        this.key = key;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void settTecno(String tTecno) {
        this.tTecno = tTecno;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPorto(int porto) {
        this.porto = porto;
    }

    public void setname(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WebService{" + "key=" + key + ", desc=" + desc + ", tTecno=" + tTecno + ", ip=" + ip + ", porto=" + porto + ", name=" + name + '}';
    }

}
