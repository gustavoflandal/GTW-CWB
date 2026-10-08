package muralha.digital.alerta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "alertasVinculadosAtualizar")
//@XmlAccessorType (XmlAccessType.FIELD)
public class AlertasVinculadosAtualizar 
{
//	@XmlElementWrapper(name="alertasVinculadosAtualizar")
	@XmlElement(name="alertaVinculadoAtualizar")
	private List<AlertaVinculadoAtualizar> alertaVinculadoAtualizar;

	
	public List<AlertaVinculadoAtualizar> getAlertasVinculadosAtualizar() { return alertaVinculadoAtualizar; }
	public void setAlertasVinculadosAtualizar(List<AlertaVinculadoAtualizar> alertaVinculadoAtualizar) { this.alertaVinculadoAtualizar = alertaVinculadoAtualizar; }
}
