package muralha.digital.painelInformacoes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Dispositivos")
public class DispositivoSimplesResponse {

    private List<DispositivoSimples> dispositivos;
    private boolean sucesso;

    public DispositivoSimplesResponse() {}

    public DispositivoSimplesResponse(List<DispositivoSimples> dispositivos) {
        this.dispositivos = dispositivos;
    }

    @XmlElement(name = "Dispositivo")
    public List<DispositivoSimples> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<DispositivoSimples> dispositivos) {
        this.dispositivos = dispositivos;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }
}
