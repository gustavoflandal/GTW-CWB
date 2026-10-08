package muralha.digital.consulta;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "TipoAlertaOcorrencia")
@XmlAccessorType (XmlAccessType.FIELD)
public class TipoAlertaOcorrencia
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(TipoAlertaOcorrencia.class);
    
    @XmlTransient
	public enum Tipo {
		VEICULO_ROUBADO(UUID.fromString("95631582-96B2-4220-9612-12131BE4923C")),
		VEICULO_FURTADO(UUID.fromString("0349F722-DFDE-4080-9E3B-D65F1C058EDC")),
		TRANSPORTE_CLANDESTINO(UUID.fromString("AE93F81A-DF6D-41B5-AFC6-99B3438C291D")),
		VEICULO_SUSPEITO_ROUBO_BANCO(UUID.fromString("FEEF9500-83C0-4942-A8AA-AED77E20BA5B")),
		VEICULO_SUSPEITO_SEQUESTRO(UUID.fromString("CB8D5C4B-1822-4868-A2F9-0153B50212DA")),
		VEICULO_CLONADO(UUID.fromString("CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3")),
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
	private String tipo;
	private String descricao;
	
	public TipoAlertaOcorrencia() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}
