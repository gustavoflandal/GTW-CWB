package muralha.digital.veiculo;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class VeiculoAlerta 
{
	private UUID 			idAlerta;
	private UUID			idVeiculoTempoReal;
	private UUID			idImagemTempoReal;
	
	
	public UUID getIdAlerta() {
		return idAlerta;
	}
	public void setIdAlerta(UUID idAlerta) {
		this.idAlerta = idAlerta;
	}
	
	public UUID getIdVeiculoTempoReal() {
		return idVeiculoTempoReal;
	}
	public void setIdVeiculoTempoReal(UUID idVeiculoTempoReal) {
		this.idVeiculoTempoReal = idVeiculoTempoReal;
	}

	public UUID getIdImagemTempoReal() {
		return idImagemTempoReal;
	}
	public void setIdImagemTempoReal(UUID idImagemTempoReal) {
		this.idImagemTempoReal = idImagemTempoReal;
	}
}
