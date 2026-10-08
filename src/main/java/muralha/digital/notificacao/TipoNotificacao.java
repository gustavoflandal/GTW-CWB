package muralha.digital.notificacao;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "TipoNotificacao")
@XmlAccessorType (XmlAccessType.FIELD)
public class TipoNotificacao
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(TipoNotificacao.class);
    
    @XmlTransient
	public enum Tipo {
    	EMAIL(UUID.fromString("3A3F1F17-6EC3-4FCA-9195-5B160A091779")),
    	SMS(UUID.fromString("2C434CFD-F581-4FA0-B3E3-45A7367BF05E")),
    	POPUP(UUID.fromString("778F443E-9514-44E0-BCD1-F953D91042BF")),
		None(null);
	
	    private final UUID id;
	    
	    Tipo(UUID id) {
	    	this.id = id;
	    }
	    
	    public UUID GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(UUID i) {
	    	return id.equals(i);
	    }
	    
        public static Tipo GetValue(UUID id)
        {
        	Tipo[] fv = Tipo.values();
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
	
	public TipoNotificacao() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}
