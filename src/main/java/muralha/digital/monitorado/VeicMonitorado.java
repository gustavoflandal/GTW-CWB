package muralha.digital.monitorado;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "VeicMonitorado")
@XmlAccessorType (XmlAccessType.FIELD)
public class VeicMonitorado
{
    @XmlTransient
	private static final Logger logger = Logger.getLogger(VeicMonitorado.class);
	
	private UUID id;
	private String placa;
	private UUID idTipoIrregularidade;
	private String tipoIrregularidade;
	private transient Date dtDataInicio;
	private transient Date dtDataFim;
	protected String dataInicio = "";
	protected String dataFim = "";
	
	public VeicMonitorado() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	public String getPlaca() { return placa; }
	public void setPlaca(String placa) { this.placa = placa; }

	public UUID getIdTipoIrregularidade() { return idTipoIrregularidade; }
	public void setIdTipoIrregularidade(UUID idTipoIrregularidade) { this.idTipoIrregularidade = idTipoIrregularidade; }

	public String getTipoIrregularidade() { return tipoIrregularidade; }
	public void setTipoIrregularidade(String tipoIrregularidade) { this.tipoIrregularidade = tipoIrregularidade; }

	public Date getDtDataInicio() { return dtDataInicio; }
	public void setDtDataInicio(Date dtDataInicio) { this.dtDataInicio = dtDataInicio; }

	public Date getDtDataFim() { return dtDataFim; }
	public void setDtDataFim(Date dtDataFim) { this.dtDataFim = dtDataFim; }
	
	public String getDataInicio() { return new SimpleDateFormat("yyyy-MM-dd").format(dtDataInicio); }
	public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

	public String getDataFim() { return dtDataFim == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(dtDataFim); }
	public void setDataFim(String dataFim) { this.dataFim = dataFim; }
}
