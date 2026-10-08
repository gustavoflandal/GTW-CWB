package muralha.digital.registroDeFato;

import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe wrapper para a resposta da API, contendo uma lista de históricos.
 * O nome do elemento raiz no XML será <RegistroDeFatoHistoricoResponse>.
 */
@XmlRootElement(name = "RegistroDeFatoHistoricoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoHistoricoResponse {

    @XmlElement(name = "RegistroDeFatoHistorico") 
    private List<RegistroDeFatoHistoricoDTO> historicos;

    public RegistroDeFatoHistoricoResponse() {
        this.historicos = new ArrayList<>();
    }
    
    public RegistroDeFatoHistoricoResponse(List<RegistroDeFatoHistoricoDTO> historicos) {
        this.historicos = historicos;
    }

    // Getters e Setters
    public List<RegistroDeFatoHistoricoDTO> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<RegistroDeFatoHistoricoDTO> historicos) {
        this.historicos = historicos;
    }
}