package muralha.digital.alerta;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="alertaVinculadoAtualizar")
@XmlAccessorType (XmlAccessType.FIELD)
public class AlertaVinculadoAtualizar 
{
	@XmlElement(name="idAlerta")
	private UUID idAlerta;
	
	@XmlElement(name="idTipoAlerta")
	private UUID idTipoAlerta;
	
	@XmlElement(name="idStatusAlerta")
	private UUID idStatusAlerta;


	public UUID getIdAlerta() { return idAlerta; }
	public void setIdAlerta(UUID idAlerta) { this.idAlerta = idAlerta; }
	
	public UUID getIdTipoAlerta() { return idTipoAlerta; }
	public void setIdTipoAlerta(UUID idTipoAlerta) { this.idTipoAlerta = idTipoAlerta; }
	
	public UUID getIdStatusAlerta() { return idStatusAlerta; }
	public void setIdStatusAlerta(UUID idStatusAlerta) { this.idStatusAlerta = idStatusAlerta; }
}
