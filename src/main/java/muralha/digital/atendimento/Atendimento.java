package muralha.digital.atendimento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Atendimento {

	private String tipoAlerta;
	private Date dataAlerta;
	private String dtAlertaStr;
	private String placaVeiculo;
	private String observacao;
	private UUID idStatusOcorrencia;
	private String statusOcorrencia;
	private String endereco_alerta;
	private String endereco_local_evento;
	private String descricao;
	private String id_ocorrencia;
	private int prioridade;
	private int origemRegistro;
	private Date dataAtendimentoCriacao;
	private int idAtendimento;
	private Date dataEncerramento;
	private int idSituacaoEnvio;
	private String protocolo;
	private int idGuarnicao;
	private UUID idAlerta;
	private int idRegistroFato;
	private int temBoletim;
	private int id_local;

	// Strings formatadas
	private String dataAlertaFormatada = "";
	private String horaAlertaFormatada = "";
	private String dataAtendimentoFormatada = "";
	private String horaAtendimentoFormatada = "";
	private String dataEncerramentoFormatada = "";

	// Getters e Setters

	public String getTipoAlerta() { return tipoAlerta; }
	public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

	public String getPlacaVeiculo() { return placaVeiculo; }
	public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }

	public String getObservacao() { return observacao; }
	public void setObservacao(String observacao) { this.observacao = observacao; }

	public String getDtAlertaStr() { return dtAlertaStr; }
	public void setDtAlertaStr(String dtAlertaStr) { this.dtAlertaStr = dtAlertaStr; }

	public UUID getIdStatusOcorrencia() { return idStatusOcorrencia; }
	public void setIdStatusOcorrencia(UUID idStatusOcorrencia) { this.idStatusOcorrencia = idStatusOcorrencia; }

	public Date getDataAlerta() { return dataAlerta; }
	public void setDataAlerta(Date dataAlerta) { this.dataAlerta = dataAlerta; }

	public Date getDataAtendimentoCriacao() { return dataAtendimentoCriacao; }
	public void setDataAtendimentoCriacao(Date dataAtendimentoCriacao) { this.dataAtendimentoCriacao = dataAtendimentoCriacao; }

	public Date getDataEncerramento() { return dataEncerramento; }
	public void setDataEncerramento(Date dataEncerramento) { this.dataEncerramento = dataEncerramento; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public String getId_ocorrencia() { return id_ocorrencia; }
	public void setId_ocorrencia(String id_ocorrencia) { this.id_ocorrencia = id_ocorrencia; }

	public int getPrioridade() { return prioridade; }
	public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

	public int getOrigemRegistro() { return origemRegistro; }
	public void setOrigemRegistro(int origemRegistro) { this.origemRegistro = origemRegistro; }

	public String getStatusOcorrencia() { return statusOcorrencia; }
	public void setStatusOcorrencia(String statusOcorrencia) { this.statusOcorrencia = statusOcorrencia; }

	public int getIdAtendimento() { return idAtendimento; }
	public void setIdAtendimento(int idAtendimento) { this.idAtendimento = idAtendimento; }

	public int getIdSituacaoEnvio() { return idSituacaoEnvio; }
	public void setIdSituacaoEnvio(int idSituacaoEnvio) { this.idSituacaoEnvio = idSituacaoEnvio; }

	public int getIdGuarnicao() { return idGuarnicao; }
	public void setIdGuarnicao(int idGuarnicao) { this.idGuarnicao = idGuarnicao; }

	public String getProtocolo() { return protocolo; }
	public void setProtocolo(String protocolo) { this.protocolo = protocolo; }

	public UUID getIdAlerta() { return idAlerta; }
	public void setIdAlerta(UUID idAlerta) { this.idAlerta = idAlerta; }

	public int getIdRegistroFato() { return idRegistroFato; }
	public void setIdRegistroFato(int idRegistroFato) { this.idRegistroFato = idRegistroFato; }

	public int getTemBoletim() { return temBoletim; }
	public void setTemBoletim(int temBoletim) { this.temBoletim = temBoletim; }

	public String getEndereco_alerta() { return endereco_alerta; }
	public void setEndereco_alerta(String endereco_alerta) { this.endereco_alerta = endereco_alerta; }

	public String getEndereco_local_evento() { return endereco_local_evento; }
	public void setEndereco_local_evento(String endereco_local_evento) { this.endereco_local_evento = endereco_local_evento; }

	public int getId_local() { return id_local; }
	public void setId_local(int id_local) { this.id_local = id_local; }

	// Métodos formatados (com validação de null)
	public String getDataAlertaFormatada() {
		if (dataAlerta == null) return "";
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataAlerta);
	}
	public void setDataAlertaFormatada(String dataAlertaFormatada) { this.dataAlertaFormatada = dataAlertaFormatada; }

	public String getHoraAlertaFormatada() {
		if (dataAlerta == null) return "";
		return new SimpleDateFormat("HH:mm:ss").format(dataAlerta);
	}
	public void setHoraAlertaFormatada(String horaAlertaFormatada) { this.horaAlertaFormatada = horaAlertaFormatada; }

	public String getDataAtendimentoFormatada() {
		if (dataAtendimentoCriacao == null) return "";
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataAtendimentoCriacao);
	}
	public void setDataAtendimentoFormatada(String dataAtendimentoFormatada) { this.dataAtendimentoFormatada = dataAtendimentoFormatada; }

	public String getHoraAtendimentoFormatada() {
		if (dataAtendimentoCriacao == null) return "";
		return new SimpleDateFormat("HH:mm:ss").format(dataAtendimentoCriacao);
	}
	public void setHoraAtendimentoFormatada(String horaAtendimentoFormatada) { this.horaAtendimentoFormatada = horaAtendimentoFormatada; }

	public String getDataEncerramentoFormatada() {
		if (dataEncerramento == null) return "";
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataEncerramento);
	}
	public void setDataEncerramentoFormatada(String dataEncerramentoFormatada) { this.dataEncerramentoFormatada = dataEncerramentoFormatada; }

}