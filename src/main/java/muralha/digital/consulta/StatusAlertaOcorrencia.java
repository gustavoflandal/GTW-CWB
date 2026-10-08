package muralha.digital.consulta;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "StatusAlertaOcorrencia")
@XmlAccessorType (XmlAccessType.FIELD)
public class StatusAlertaOcorrencia
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(StatusAlertaOcorrencia.class);
    
    @XmlTransient
	public enum StatusAlerta {
    	PENDENTE(UUID.fromString("5479C6D9-7381-4492-99BE-442EF2E741B0")),
    	OCORRENCIA(UUID.fromString("15EBBA5F-C805-449E-83CC-227ED3B3AD3C")),
    	DESCARTADO(UUID.fromString("298F5A62-C799-4CA9-8220-6B08F8664534")),
    	VINCULADO(UUID.fromString("CA5E4AE0-501E-48E2-9652-A1CF7AA340BB")),
		None(null);
	
	    private final UUID id;
	    
	    StatusAlerta(UUID id) {
	    	this.id = id;
	    }
	    
	    public UUID GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(UUID i) {
	    	return id.equals(i);
	    }
	    
        public static StatusAlerta GetValue(UUID id)
        {
        	StatusAlerta[] fv = StatusAlerta.values();
            for(int i = 0; i < fv.length; i++)
            {
                if(fv[i].Compare(id))
                    return fv[i];
            }
            return None;
        }
	}
    
    @XmlTransient
	public enum StatusOcorrencia {
    	EM_ABERTO(UUID.fromString("3C0612D6-3950-4861-8CA4-2A261AE787AF")),
    	NOTIFICACAO_ENVIADA(UUID.fromString("6077FC54-FD5B-42C0-9EE1-8FB3612E48BD")),
    	FINALIZADO(UUID.fromString("ACAADE8A-2D4E-4E93-9F1F-0DE9185B576E")),
    	FINALIZADO_VEICULO_RECUPERADO(UUID.fromString("0033D5BC-8E4F-4B10-BC5E-8119B768F566")),
    	FINALIZADO_VEICULO_APREENDIDO(UUID.fromString("81038AD9-2206-4956-A34E-8C844C673A62")),
    	FINALIZADO_SEM_SOLUCAO(UUID.fromString("9CE69C57-59D6-4611-AC1E-3690779FA68F")),
		None(null);
	
	    private final UUID id;
	    
	    StatusOcorrencia(UUID id) {
	    	this.id = id;
	    }
	    
	    public UUID GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(UUID i) {
	    	return id.equals(i);
	    }
	    
        public static StatusOcorrencia GetValue(UUID id)
        {
        	StatusOcorrencia[] fv = StatusOcorrencia.values();
            for(int i = 0; i < fv.length; i++)
            {
                if(fv[i].Compare(id))
                    return fv[i];
            }
            return None;
        }
	}
	
	private UUID id;
	private String descricao;
	private boolean statusPadrao;
	
	public StatusAlertaOcorrencia() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	
	public boolean isStatusPadrao() { return statusPadrao; }
	public void setStatusPadrao(boolean statusPadrao) { this.statusPadrao = statusPadrao; }
}
