package muralha.digital.pontointeresse;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "PontoInteresse")
@XmlAccessorType (XmlAccessType.FIELD)
public class PontoInteresse
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(PontoInteresse.class);
	
	private UUID 		id;
    private String		nome;
    private String  	descricao;
    private int			tipo;
    private float		latitude;
    private float		longitude;
    private String		equipamentos;
    private String		codigosEquipamentos;
    
    public UUID			getId() 											{ return id; };
    public void 		setId(UUID id)										{ this.id = id; };
    
    public String		getNome() 											{ return nome; };
    public void 		setNome(String nome)								{ this.nome = nome; };
    
    public String		getDescricao() 										{ return descricao; };
    public void 		setDescricao(String descricao)						{ this.descricao = descricao; };
    
    public int			getTipo() 											{ return tipo; };
    public void 		setTipo(int tipo)									{ this.tipo = tipo; };
    
    public float		getLatitude() 										{ return latitude; };
    public void 		setLatitude(float latitude)							{ this.latitude = latitude; };
    
    public float		getLongitude() 										{ return longitude; };
    public void 		setLongitude(float longitude)						{ this.longitude = longitude; };
    
    public String		getEquipamentos() 									{ return equipamentos; };
    public void 		setEquipamentos(String equipamentos)				{ this.equipamentos = equipamentos; }

    public String 		getCodigosEquipamentos() 							{ return codigosEquipamentos; }
	public void 		setCodigosEquipamentos(String codigosEquipamentos) 	{ this.codigosEquipamentos = codigosEquipamentos; }
}
