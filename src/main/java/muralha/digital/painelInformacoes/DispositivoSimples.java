package muralha.digital.painelInformacoes;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Date;
@XmlRootElement(name = "Dispositivo")
public class DispositivoSimples {

    private int idDispositivo;
    private boolean conectado;
    private String descDispositivo;
    private boolean seComunicou;
    private Date ultimaDataRegistrada;
    public DispositivoSimples() {}

    public DispositivoSimples(int idDispositivo, boolean conectado, String descDispositivo, boolean seComunicou, Date ultimaDataRegistrada) {
        this.idDispositivo = idDispositivo;
        this.conectado = conectado;
        this.descDispositivo = descDispositivo;
        this.seComunicou = seComunicou;
        this.ultimaDataRegistrada = ultimaDataRegistrada;
    }

    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public String getDescDispositivo() {
        return descDispositivo;
    }

    public void setDescDispositivo(String descDispositivo) {
        this.descDispositivo = descDispositivo;
    }

    public boolean isSeComunicou() {
        return seComunicou;
    }

    public void setSeComunicou(boolean seComunicou) {
        this.seComunicou = seComunicou;
    }

    public Date getUltimaDataRegistrada() {
        return ultimaDataRegistrada;
    }

    public void setUltimaDataRegistrada(Date ultimaDataRegistrada) {
        this.ultimaDataRegistrada = ultimaDataRegistrada;
    }
}
