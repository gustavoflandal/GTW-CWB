package muralha.digital.monitorado;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "VeiculoMonitorado")
@XmlAccessorType (XmlAccessType.FIELD)
public class VeiculoMonitorado
{
	
	@XmlTransient
	private static final Logger logger = Logger.getLogger(VeiculoMonitorado.class);
	
	private UUID id;
	private String nome;
	private String placa;
	private UUID idTipoAlertaOcorrencia;
	private String tipoAlertaOcorrencia;
	private String descricao;
	private Date dataInicio;
	private Date dataFim;
	private Date dataCadastro;
	private Integer idUsuario;
	private String usuario;
	private String nomeUsuario;
	private Date dataExclusao;
	private Integer idUsuarioExclusao;
	private String usuarioExclusao;
	private String nomeUsuarioExclusao;
	private String motivoExclusao;
	private Date dataInativacao;
	private Integer idUsuarioInativacao;
	private String usuarioInativacao;
	private String nomeUsuarioInativacao;
	private Integer monitorarSomenteEste;
	
	private Date dataAtualizacao;
	private Integer idUsuarioAtualizacao;
	private String usuarioAtualizacao;
	private String nomeUsuarioAtualizacao;
	private Integer id_usuario_responsavel;
	
	public String dataInicioFormatada = "";
	public String dataFimFormatada = "";
	public String dataCadastroFormatada = "";
	public String horaCadastroFormatada = "";
	public String dataExclusaoFormatada = "";
	public String horaExclusaoFormatada = "";
	public String dataInativacaoFormatada = "";
	public String horaInativacaoFormatada = "";
	public String dataAtualizacaoFormatada = "";
	public String horaAtualizacaoFormatada = "";
	
	private boolean valido, editavel, ativo, possuiAlerta;
	private int qtdeRegistros = 10;
	private boolean apenasCadAtivo, apenasPlacaComCoringa;
	
	private boolean supervisionado;
	private boolean privado;
	private Integer errosPermitidosPlaca;
	public String errosPermitidosIni;
	public String errosPermitidosFim;
	private Long idRegistroFato;
	
	private String idClasse;
	private Integer idCor;
	private Integer idMarca;
	private Integer idModelo;
	private String textoAdesivo;
	
	private List<String> gruposPopup;
	private List<Integer> grupos;
	private List<Integer> equipamentosLocais;
	private List<HorarioPermitido> horariosPermitidos;
	private List<VeiculoMonitoradoEquipamentoEntidade> equipamentosEntidade;
	private List<VeiculoMonitoradoPeriodoEntidade> horariosEntidade;
	
	public VeiculoMonitorado() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	public String getNome()	{ return nome; };
	public void setNome(String nome) { this.nome = nome; };

	public String getPlaca() { return placa; }
	public void setPlaca(String placa) { this.placa = placa; }

	public UUID getIdTipoAlertaOcorrencia() { return idTipoAlertaOcorrencia; }
	public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) { this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia; }

	public String getTipoAlertaOcorrencia() { return tipoAlertaOcorrencia; }
	public void setTipoAlertaOcorrencia(String tipoAlertaOcorrencia) { this.tipoAlertaOcorrencia = tipoAlertaOcorrencia; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public Date getDataInicio() { return dataInicio; }
	public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

	public Date getDataFim() { return dataFim; }
	public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

	public Date getDataCadastro() { return dataCadastro; }
	public void setDataCadastro(Date dataCadastro) { this.dataCadastro = dataCadastro; }

	public Integer getIdUsuario() { return idUsuario; }
	public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
	
	public String getNomeUsuario() { return nomeUsuario; }
	public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

	public String getUsuario() { return usuario; }
	public void setUsuario(String usuario) { this.usuario = usuario; }

	public Date getDataExclusao() { return dataExclusao; }
	public void setDataExclusao(Date dataExclusao) { this.dataExclusao = dataExclusao; }

	public Integer getIdUsuarioExclusao() { return idUsuarioExclusao; }
	public void setIdUsuarioExclusao(Integer idUsuarioExclusao) { this.idUsuarioExclusao = idUsuarioExclusao; }

	public String getUsuarioExclusao() { return usuarioExclusao; }
	public void setUsuarioExclusao(String usuarioExclusao) { this.usuarioExclusao = usuarioExclusao; }

	public String getNomeUsuarioExclusao() { return nomeUsuarioExclusao; }
	public void setNomeUsuarioExclusao(String nomeUsuarioExclusao) { this.nomeUsuarioExclusao = nomeUsuarioExclusao; }

	public String getMotivoExclusao() { return motivoExclusao; }
	public void setMotivoExclusao(String motivoExclusao) { this.motivoExclusao = motivoExclusao; }
	
	public Date getDataInativacao() { return dataInativacao; }
	public void setDataInativacao(Date dataInativacao) { this.dataInativacao = dataInativacao; }

	public Integer getIdUsuarioInativacao() { return idUsuarioInativacao; }
	public void setIdUsuarioInativacao(Integer idUsuarioInativacao) { this.idUsuarioInativacao = idUsuarioInativacao; }

	public String getUsuarioInativacao() { return usuarioInativacao; }
	public void setUsuarioInativacao(String usuarioInativacao) { this.usuarioInativacao = usuarioInativacao; }

	public String getNomeUsuarioInativacao() { return nomeUsuarioInativacao; }
	public void setNomeUsuarioInativacao(String nomeUsuarioInativacao) { this.nomeUsuarioInativacao = nomeUsuarioInativacao; }

	public String getDataInicioFormatada() { return new SimpleDateFormat("dd/MM/yyyy").format(dataInicio); }
	public void setDataInicioFormatada(String dataInicioFormatada) { this.dataInicioFormatada = dataInicioFormatada; }

	public String getDataFimFormatada() { return dataFim == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(dataFim); }
	public void setDataFimFormatada(String dataFimFormatada) { this.dataFimFormatada = dataFimFormatada; }

	public String getDataCadastroFormatada() { return new SimpleDateFormat("dd/MM/yyyy").format(dataCadastro); }
	public void setDataCadastroFormatada(String dataCadastroFormatada) { this.dataCadastroFormatada = dataCadastroFormatada; }

	public String getHoraCadastroFormatada() { return new SimpleDateFormat("HH:mm:ss").format(dataCadastro); }
	public void setHoraCadastroFormatada(String horaCadastroFormatada) { this.horaCadastroFormatada = horaCadastroFormatada; }

	public String getDataExclusaoFormatada() { return dataExclusao == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(dataExclusao); }
	public void setDataExclusaoFormatada(String dataExclusaoFormatada) { this.dataExclusaoFormatada = dataExclusaoFormatada; }

	public String getHoraExclusaoFormatada() { return dataExclusao == null ? null : new SimpleDateFormat("HH:mm:ss").format(dataExclusao); }
	public void setHoraExclusaoFormatada(String horaExclusaoFormatada) { this.horaExclusaoFormatada = horaExclusaoFormatada; }
	
	public String getDataInativacaoFormatada() { return dataInativacao == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(dataInativacao); }
	public void setDataInativacaoFormatada(String dataInativacaoFormatada) { this.dataInativacaoFormatada = dataInativacaoFormatada; }

	public String getHoraInativacaoFormatada() { return dataInativacao == null ? null : new SimpleDateFormat("HH:mm:ss").format(dataInativacao); }
	public void setHoraInativacaoFormatada(String horaInativacaoFormatada) { this.horaInativacaoFormatada = horaInativacaoFormatada; }
	
	public Date getDataAtualizacao() { return dataAtualizacao; }
	public void setDataAtualizacao(Date dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

	public Integer getIdUsuarioAtualizacao() { return idUsuarioAtualizacao; }
	public void setIdUsuarioAtualizacao(Integer idUsuarioAtualizacao) { this.idUsuarioAtualizacao = idUsuarioAtualizacao; }

	public String getUsuarioAtualizacao() { return usuarioAtualizacao; }
	public void setUsuarioAtualizacao(String usuarioAtualizacao) { this.usuarioAtualizacao = usuarioAtualizacao; }

	public String getNomeUsuarioAtualizacao() { return nomeUsuarioAtualizacao; }
	public void setNomeUsuarioAtualizacao(String nomeUsuarioAtualizacao) { this.nomeUsuarioAtualizacao = nomeUsuarioAtualizacao; }

	public String getDataAtualizacaoFormatada() { return dataAtualizacao == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(dataAtualizacao); }
	public void setDataAtualizacaoFormatada(String dataAtualizacaoFormatada) { this.dataAtualizacaoFormatada = dataAtualizacaoFormatada; }

	public String getHoraAtualizacaoFormatada() { return dataAtualizacao == null ? null : new SimpleDateFormat("HH:mm:ss").format(dataAtualizacao); }
	public void setHoraAtualizacaoFormatada(String horaAtualizacaoFormatada) { this.horaAtualizacaoFormatada = horaAtualizacaoFormatada; }

	public boolean isValido() { return valido; }
	public void setValido(boolean valido) { this.valido = valido; }
	
	public boolean isEditavel() { return editavel; }
	public void setEditavel(boolean editavel) { this.editavel = editavel; }
	
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }
	
	public boolean PossuiAlerta() { return possuiAlerta; }
	public void setPossuiAlerta(boolean possuiAlerta) { this.possuiAlerta = possuiAlerta; }

	public int getQtdeRegistros() { return qtdeRegistros; }
	public void setQtdeRegistros(int qtdeRegistros) { this.qtdeRegistros = qtdeRegistros; }

	public boolean isApenasCadAtivo() { return apenasCadAtivo; }
	public void setApenasCadAtivo(boolean apenasCadAtivo) { this.apenasCadAtivo = apenasCadAtivo; }
	
	public boolean isApenasPlacaComCoringa() { return apenasPlacaComCoringa; }
	public void setApenasPlacaComCoringa(boolean apenasPlacaComCoringa) { this.apenasPlacaComCoringa = apenasPlacaComCoringa; }
	
	public boolean isSupervisionado() {
		return supervisionado;
	}
	public void setSupervisionado(boolean supervisionado) {
		this.supervisionado = supervisionado;
	}

	public boolean isPrivado() {
		return privado;
	}
	public void setPrivado(boolean privado) {
		this.privado = privado;
	}

	public Integer getErrosPermitidosPlaca() {
		return errosPermitidosPlaca;
	}
	public void setErrosPermitidosPlaca(Integer errosPermitidosPlaca) {
		this.errosPermitidosPlaca = errosPermitidosPlaca;
	}
	public List<String> getGruposPopup() {
		return gruposPopup;
	}

	public String getErrosPermitidosIni() {
		return errosPermitidosIni;
	}
	public void setErrosPermitidosIni(String errosPermitidosIni) {
		this.errosPermitidosIni = errosPermitidosIni;
	}

	public String getErrosPermitidosFim() {
		return errosPermitidosFim;
	}
	public void setErrosPermitidosFim(String errosPermitidosFim) {
		this.errosPermitidosFim = errosPermitidosFim;
	}

	public void setGruposPopup(List<String> gruposEmail) {
		this.gruposPopup = gruposEmail;
	}
	
	public List<Integer> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Integer> grupos) {
		this.grupos = grupos;
	}
	
	public List<Integer> getEquipamentosLocais() {
		return equipamentosLocais;
	}

	public void setEquipamentosLocais(List<Integer> equipamentosLocais) {
		this.equipamentosLocais = equipamentosLocais;
	}
	
	public List<HorarioPermitido> getHorariosPermitidos() {
		return horariosPermitidos;
	}

	public void setHorariosPermitidos(List<HorarioPermitido> horariosPermitidos) {
		this.horariosPermitidos = horariosPermitidos;
	}
	
	public List<VeiculoMonitoradoEquipamentoEntidade> getEquipamentosEntidade() {
		return equipamentosEntidade;
	}

	public void setEquipamentosEntidade(List<VeiculoMonitoradoEquipamentoEntidade> equipamentosEntidade) {
		this.equipamentosEntidade = equipamentosEntidade;
	}

	public List<VeiculoMonitoradoPeriodoEntidade> getHorariosEntidade() {
		return horariosEntidade;
	}

	public void setHorariosEntidade(List<VeiculoMonitoradoPeriodoEntidade> horariosEntidade) {
		this.horariosEntidade = horariosEntidade;
	}

	public Long getIdRegistroFato() {
		return idRegistroFato;
	}

	public void setIdRegistroFato(Long idRegistroFato) {
		this.idRegistroFato = idRegistroFato;
	}

	public Integer getId_usuario_responsavel() {
		return id_usuario_responsavel;
	}

	public void setId_usuario_responsavel(Integer id_usuario_responsavel) {
		this.id_usuario_responsavel = id_usuario_responsavel;
	}	
	
	public Integer getMonitorarSomenteEste() {
		return monitorarSomenteEste;
	}

	public void setMonitorarSomenteEste(Integer monitorarSomenteEste) {
		this.monitorarSomenteEste = monitorarSomenteEste;
	}
	
	public String getIdClasse() {
	    return idClasse;
	}

	public void setIdClasse(String idClasse) {
	    this.idClasse = idClasse;
	}

	public Integer getIdCor() {
	    return idCor;
	}

	public void setIdCor(Integer idCor) {
	    this.idCor = idCor;
	}

	public Integer getIdMarca() {
	    return idMarca;
	}

	public void setIdMarca(Integer idMarca) {
	    this.idMarca = idMarca;
	}

	public Integer getIdModelo() {
	    return idModelo;
	}

	public void setIdModelo(Integer idModelo) {
	    this.idModelo = idModelo;
	}

	public String getTextoAdesivo() {
	    return textoAdesivo;
	}

	public void setTextoAdesivo(String textoAdesivo) {
	    this.textoAdesivo = textoAdesivo;
	}
}
