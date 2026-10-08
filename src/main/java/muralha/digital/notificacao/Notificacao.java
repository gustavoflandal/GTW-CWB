package muralha.digital.notificacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import muralha.digital.veiculo.imagem.VeiculoImagem;


@XmlRootElement(name = "Notificacao")
@XmlAccessorType (XmlAccessType.FIELD)
public class Notificacao
{
	private UUID id;
	private Date dataCadastro;
	private UUID idOcorrencia;
	private UUID idAlerta;
	private String equipamento;
	private String equipamentoSMS;
	private Date dataVeiculo;
	private Date dataAlerta;
	private Date dataOcorrencia;
	private String nomeUsuarioOcorrencia;
	private String placaMonitorada;
	private String placaLida;
	private Integer idGrupo;
	private String grupo;
	private UUID idTipoAlertaOcorrencia;
	private String tipoAlertaOcorrencia;
	private String tipoAlertaOcorrenciaSMS;
	private UUID idTipoNotificacao;
	private String tipoNotificacao;
	private UUID idTipoRegistro;
	private String tipoRegistro;
	private UUID idStatusNotificacao;
	private String statusNotificacao;
	private String remetente;
	
	
	private List<InternetAddress> destinatarios = new ArrayList<InternetAddress>();
	private List<VeiculoImagem> imagens = new ArrayList<VeiculoImagem>();
	
	private List<String> destinatariosSMS = new ArrayList<String>();
	

	public Notificacao() {}


	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public UUID getIdOcorrencia() {
		return idOcorrencia;
	}
	public void setIdOcorrencia(UUID idOcorrencia) {
		this.idOcorrencia = idOcorrencia;
	}

	public UUID getIdAlerta() {
		return idAlerta;
	}
	public void setIdAlerta(UUID idAlerta) {
		this.idAlerta = idAlerta;
	}

	public String getEquipamento() {
		return equipamento;
	}
	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	public String getEquipamentoSMS() {
		return equipamentoSMS;
	}
	public void setEquipamentoSMS(String equipamento_sms) {
		this.equipamentoSMS = equipamento_sms;
	}

	public Date getDataVeiculo() {
		return dataVeiculo;
	}
	public void setDataVeiculo(Date dataVeiculo) {
		this.dataVeiculo = dataVeiculo;
	}

	public Date getDataAlerta() {
		return dataAlerta;
	}
	public void setDataAlerta(Date dataAlerta) {
		this.dataAlerta = dataAlerta;
	}

	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public String getNomeUsuarioOcorrencia() {
		return nomeUsuarioOcorrencia;
	}
	public void setNomeUsuarioOcorrencia(String nomeUsuarioOcorrencia) {
		this.nomeUsuarioOcorrencia = nomeUsuarioOcorrencia;
	}

	public String getPlacaMonitorada() {
		return placaMonitorada;
	}
	public void setPlacaMonitorada(String placaMonitorada) {
		this.placaMonitorada = placaMonitorada;
	}

	public String getPlacaLida() {
		return placaLida;
	}
	public void setPlacaLida(String placaLida) {
		this.placaLida = placaLida;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public UUID getIdTipoAlertaOcorrencia() {
		return idTipoAlertaOcorrencia;
	}
	public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) {
		this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia;
	}

	public String getTipoAlertaOcorrenciaSMS() {
		return tipoAlertaOcorrenciaSMS;
	}
	public void setTipoAlertaOcorrenciaSMS(String tipoAlertaOcorrenciaSMS) {
		this.tipoAlertaOcorrenciaSMS = tipoAlertaOcorrenciaSMS;
	}

	public String getTipoAlertaOcorrencia() {
		return tipoAlertaOcorrencia;
	}
	public void setTipoAlertaOcorrencia(String tipoAlertaOcorrencia) {
		this.tipoAlertaOcorrencia = tipoAlertaOcorrencia;
	}

	public UUID getIdTipoNotificacao() {
		return idTipoNotificacao;
	}
	public void setIdTipoNotificacao(UUID idTipoNotificacao) {
		this.idTipoNotificacao = idTipoNotificacao;
	}

	public String getTipoNotificacao() {
		return tipoNotificacao;
	}
	public void setTipoNotificacao(String tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}

	public UUID getIdTipoRegistro() {
		return idTipoRegistro;
	}
	public void setIdTipoRegistro(UUID idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}
	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public UUID getIdStatusNotificacao() {
		return idStatusNotificacao;
	}
	public void setIdStatusNotificacao(UUID idStatusNotificacao) {
		this.idStatusNotificacao = idStatusNotificacao;
	}

	public String getStatusNotificacao() {
		return statusNotificacao;
	}
	public void setStatusNotificacao(String statusNotificacao) {
		this.statusNotificacao = statusNotificacao;
	}
	
	public String getRemetente() {
		return remetente;
	}
	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	public List<InternetAddress> getDestinatarios() {
		return destinatarios;
	}
	public void setDestinatarios(List<InternetAddress> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public List<VeiculoImagem> getImagens() {
		return imagens;
	}
	public void setImagens(List<VeiculoImagem> imagens) {
		this.imagens = imagens;
	}

	public List<String> getDestinatariosSMS() {
		return destinatariosSMS;
	}
	public void setDestinatariosSMS(List<String> destinatariosSMS) {
		this.destinatariosSMS = destinatariosSMS;
	}
}
