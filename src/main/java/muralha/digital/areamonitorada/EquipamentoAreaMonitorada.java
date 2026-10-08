package muralha.digital.areamonitorada;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

@XmlRootElement(name = "EquipamentoAreaMonitorada")
@XmlAccessorType (XmlAccessType.FIELD)
public class EquipamentoAreaMonitorada {
	
	@XmlTransient
	private static final Logger logger = Logger.getLogger(EquipamentoAreaMonitorada.class);
	
	public int id;
	public int id_area_monitorada;
	public int id_equipamento;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId_area_monitorada() {
		return id_area_monitorada;
	}
	public void setId_area_monitorada(int id_area_monitorada) {
		this.id_area_monitorada = id_area_monitorada;
	}
	public int getId_equipamento() {
		return id_equipamento;
	}
	public void setId_equipamento(int id_equipamento) {
		this.id_equipamento = id_equipamento;
	}	
}
