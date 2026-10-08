package muralha.digital.notificacao;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "StatusNotificacao")
@XmlAccessorType (XmlAccessType.FIELD)
public class StatusNotificacao
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(StatusNotificacao.class);
    
    @XmlTransient
	public enum Status {
    	PENDENTE(UUID.fromString("99AF55C6-2446-4B98-BBCD-83663C504C79")),
    	ENVIADO(UUID.fromString("AE081E1E-A323-41A7-8428-55A0B398E545")),
    	NAO_ENVIADO(UUID.fromString("65453CA3-6E31-4740-B082-531306AD7016")),
		None(null);
	
	    private final UUID id;
	    
	    Status(UUID id) {
	    	this.id = id;
	    }
	    
	    public UUID GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(UUID i) {
	    	return id.equals(i);
	    }
	    
        public static Status GetValue(UUID id)
        {
        	Status[] fv = Status.values();
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
	
	public StatusNotificacao() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}
