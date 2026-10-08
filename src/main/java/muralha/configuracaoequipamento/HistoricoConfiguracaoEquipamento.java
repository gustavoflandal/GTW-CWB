package muralha.configuracaoequipamento;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;
import org.apache.poi.hpsf.GUID;

@XmlRootElement(name = "HistoricoConfiguracaoEquipamento")
@XmlAccessorType (XmlAccessType.FIELD)
public class HistoricoConfiguracaoEquipamento {

	@XmlTransient
	private static final Logger logger = Logger.getLogger(HistoricoConfiguracaoEquipamento.class);
	
	private int id;
	
	private GUID idTipoAlertaOcorrencia;
	
	private int idGrupo;
	
	private int idUsuarioResponsavel;
	
	private Date dataAcao;
	
	private String DadosJson;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GUID getIdTipoAlertaOcorrencia() {
		return idTipoAlertaOcorrencia;
	}

	public void setIdTipoAlertaOcorrencia(GUID idTipoAlertaOcorrencia) {
		this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia;
	}

	public int getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	public int getIdUsuarioResponsavel() {
		return idUsuarioResponsavel;
	}

	public void setIdUsuarioResponsavel(int idUsuarioResponsavel) {
		this.idUsuarioResponsavel = idUsuarioResponsavel;
	}

	public Date getDataAcao() {
		return dataAcao;
	}

	public void setDataAcao(Date dataAcao) {
		this.dataAcao = dataAcao;
	}

	public String getDadosJson() {
		return DadosJson;
	}

	public void setDadosJson(String dadosJson) {
		DadosJson = dadosJson;
	}
}
