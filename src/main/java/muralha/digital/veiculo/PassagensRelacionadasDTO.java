package muralha.digital.veiculo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PassagensRelacionadasDTO {
    @XmlElement(name = "passagemAtual")
    private PassagemRelacionada passagemAtual;
    
    @XmlElementWrapper(name = "passagensAnteriores")
    @XmlElement(name = "Passagem")
    private List<PassagemRelacionada> passagensAnteriores;
    
    @XmlElementWrapper(name = "passagensPosteriores")
    @XmlElement(name = "Passagem")
    private List<PassagemRelacionada> passagensPosteriores;
    
    public PassagemRelacionada getPassagemAtual() {
        return passagemAtual;
    }
    public void setPassagemAtual(PassagemRelacionada passagemAtual) {
        this.passagemAtual = passagemAtual;
    }
    public List<PassagemRelacionada> getPassagensAnteriores() {
        return passagensAnteriores;
    }
    public void setPassagensAnteriores(List<PassagemRelacionada> passagensAnteriores) {
        this.passagensAnteriores = passagensAnteriores;
    }
    public List<PassagemRelacionada> getPassagensPosteriores() {
        return passagensPosteriores;
    }
    public void setPassagensPosteriores(List<PassagemRelacionada> passagensPosteriores) {
        this.passagensPosteriores = passagensPosteriores;
    }
}