package muralha.digital.consulta;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "AlertaOcorrencia")
@XmlAccessorType (XmlAccessType.FIELD)
public class TipoRegistro
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(TipoRegistro.class);
    
    @XmlTransient
	public enum Tipo {
		ALERTA(UUID.fromString("E7D115B9-E6B3-4E86-9083-F347A1917045"), "ALERTAS", "muralha.fcn_ObterAlertas()", "muralha.fcn_ObterAlertasAlt()"),
		IRREGULARIDADES(UUID.fromString("5511CEF5-C1A0-450B-99B3-6FCA8668D243"), "IRREGULARIDADES", "muralha.fcn_ObterOcorrencias()", "muralha.fcn_ObterOcorrenciasAlt()"),
		None(null, null, null, null);
	
	    private final UUID id;
	    private final String descricao;
	    private final String funcaoSQL;
	    private final String funcaoSqlAlt;
	    
	    Tipo(UUID id, String descricao, String funcaoSQL, String funcaoSqlAlt) {
	    	this.id = id;
	    	this.descricao = descricao;
	    	this.funcaoSQL = funcaoSQL;
	    	this.funcaoSqlAlt = funcaoSqlAlt;
	    }
	    
	    public UUID GetID() {
	    	return id;
	    }
	    
	    public String GetDescricao() {
	    	return descricao;
	    }
	    
	    public String GetFuncaoSQL() {
	    	return funcaoSQL;
	    }
	    
	    public String GetFuncaoSqlAlt() {
	    	return funcaoSqlAlt;
	    }
	    
	    public boolean Compare(UUID i) {
	    	return id.equals(i);
	    }
	    
	    public boolean CompareByDesc(String desc) {
	    	return descricao.equals(desc);
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
        
        public static Tipo GetValueByDesc(String desc)
        {
        	Tipo[] fv = Tipo.values();
            for(int i = 0; i < fv.length; i++)
            {
                if(fv[i].CompareByDesc(desc))
                    return fv[i];
            }
            return None;
        }
	}
	
	private UUID id;
	private String descricao;
	
	public TipoRegistro() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}
