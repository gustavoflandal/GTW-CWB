package muralha.digital.alerta;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class MotivoDescarte
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(MotivoDescarte.class);
    
    @XmlTransient
	public enum Motivo {
		ALERTA_INVALIDO(UUID.fromString("67240968-0D40-4137-9C9D-4573A0652971")),
		PLACA_INCORRETA(UUID.fromString("7FABC2E1-87B4-4DD3-B9AD-40C8AF249B8E")),
		None(null);
	
	    private final UUID id;
	    
	    Motivo(UUID id) {
	    	this.id = id;
	    }
	    
	    public UUID GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(UUID i) {
	    	return id.equals(i);
	    }
	    
        public static Motivo GetValue(UUID id)
        {
        	Motivo[] fv = Motivo.values();
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
	
	public MotivoDescarte() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}
