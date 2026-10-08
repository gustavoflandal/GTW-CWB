package muralha.digital.alerta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import muralha.digital.veiculo.VeiculoAlerta;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Alerta 
{
	private UUID 			id;
	private UUID			idTipoAlerta;
	private String			tipoAlerta;
	private	String			descAlerta;
	private UUID			idVeiculoMonitorado;
	private UUID			idCadVeicMonitorado;
	private String			placaCadastro;
	private Date			dataCadVeicMonitorado;
	private UUID			idStatusAlerta;
	private String			statusAlertaDesc;
	private	Date			dataAlerta;
	private	Date			dataPassagem;
	private String			dtAlertaStr;
	private Integer			enviadoAoCliente;
	private	Date			dataEnviadoCliente;
	private String			placaVeiculo;
	private Date			dataVeiculo;
	private String			equipamento;
	private float			latitude;
	private float			longitude;
	private UUID			idMotivoDescarte;
	private String			motivoDescarte;
	private String			observacao;
	private Integer			idUsuario;
	private String			usuario;
	private String			nomeUsuario;
	private boolean			descartado;
	private UUID 			idOcorrencia;
	private boolean			ocorrenciaGerada;
	private boolean			ocorrenciaComNotificacao;
	private UUID			idTipoRegistro;
	private String			tipoRegistro;
	private int 			lembrete;
	private UUID			idStatusOcorrencia;
	private String			statusOcorrencia;
	private boolean			ocorrenciaFinalizada;
	private String			obsFinalizarOcorrencia;
	private UUID			idPontoInteresse;
	private String			nomePontoInteresse;
	private boolean			alertaVinculado;
	private UUID			idAlertaVinculado;
	private int				idLocal;
	private int				serieEquipamento;
	private int				idPista;
	private int				faixa;
	private int				velocidade;
	private int				totalRegistros;
	private String          emAtendimentoPor;
	private int				atendido;
	private boolean			permiteAtendimento;
	private boolean			permiteAlterarAtendimento;
	private int				supervisionado;
	private boolean			assinado;
	@XmlElementWrapper		(name="listaVeiculos")
	@XmlElement				(name="veiculo")
	private List<VeiculoAlerta> veiculosAlerta;
	
	
	public String 			dataCadVeicMonitoradoFormatada = "";
	public String 			horaCadVeicMonitoradoFormatada = "";
	public String 			dataAlertaFormatada = "";
	public String 			dataPassagemFormatada = "";
	public String 			horaAlertaFormatada = "";
	public String 			horaPassagemFormatada = "";
	public String 			dataVeiculoFormatada = "";
	public String 			horaVeiculoFormatada = "";
	
	private int				com_semelhanca;
	private int				com_semelhanca_erros;
	private String			com_semelhanca_desc;
	private String			som;

	private boolean			possui_fato;
	public int				id_registro_fato;
	private int				idUsuarioResponsavel;
	private String			possui_bo_alerta;
	private int				nao_assinados;
	
	
	
	public UUID getId() 														{	return id;	}
	public void setId(UUID id) 													{	this.id = id;	}
	
	public UUID getIdTipoAlerta() 												{	return idTipoAlerta;	}
	public void setIdTipoAlerta(UUID idTipoAlerta) 								{	this.idTipoAlerta = idTipoAlerta;	}
	
	public UUID getIdVeiculoMonitorado() 										{	return idVeiculoMonitorado;	}
	public void setIdVeiculoMonitorado(UUID idVeiculoMonitorado)				{	this.idVeiculoMonitorado = idVeiculoMonitorado;	}
	
	public UUID getIdStatusAlerta() 											{	return idStatusAlerta;	}
	public void setIdStatusAlerta(UUID idStatusAlerta) 							{	this.idStatusAlerta = idStatusAlerta;	}
	
	public String getTipoAlerta() 												{	return tipoAlerta;	}
	public void setTipoAlerta(String tipoAlerta)								{	this.tipoAlerta = tipoAlerta;	}
	
	public String getDescAlerta()												{	return descAlerta;	}
	public void setDescAlerta(String descAlerta)								{	this.descAlerta = descAlerta;	}
	
	public UUID getIdCadVeicMonitorado() 										{	return idCadVeicMonitorado;	}
	public void setIdCadVeicMonitorado(UUID idCadVeicMonitorado)				{	this.idCadVeicMonitorado = idCadVeicMonitorado;	}
	
	public String getPlacaCadastro() 											{	return placaCadastro;	}
	public void setPlacaCadastro(String placaCadastro) 							{	this.placaCadastro = placaCadastro;	}
	
	public Date getDataCadVeicMonitorado() 										{	return dataCadVeicMonitorado;	}
	public void setDataCadVeicMonitorado(Date dataCadVeicMonitorado) 			{	this.dataCadVeicMonitorado = dataCadVeicMonitorado;	}
	
	public String getStatusAlertaDesc() 										{	return statusAlertaDesc;	}
	public void setStatusAlertaDesc(String statusAlertaDesc) 					{	this.statusAlertaDesc = statusAlertaDesc;	}
	
	public Date getDataAlerta() 												{	return dataAlerta;	}
	public void setDataAlerta(Date dataAlerta)									{	this.dataAlerta = dataAlerta;	}
	
	public Date getDataPassagem() 												{	return dataPassagem;	}
	public void setDataPassagem(Date dataPassagem)								{	this.dataPassagem = dataPassagem;	}
	
	public Integer getEnviadoAoCliente() 										{	return enviadoAoCliente;	}
	public void setEnviadoAoCliente(Integer enviadoAoCliente) 					{	this.enviadoAoCliente = enviadoAoCliente;	}
	
	public Date getDataEnviadoCliente() 										{	return dataEnviadoCliente;	}
	public void setDataEnviadoCliente(Date dataEnviadoCliente) 					{	this.dataEnviadoCliente = dataEnviadoCliente;	}
	
	public String getEquipamento() 												{	return equipamento;	}
	public void setEquipamento(String equipamento) 								{	this.equipamento = equipamento;	}
	
	public float getLatitude()													{	return latitude;	};
	public void setLatitude(float latitude)										{	this.latitude = latitude;	};
	
	public float getLongitude()													{	return longitude;	};
	public void setLongitude(float longitude)									{	this.longitude = longitude;	};
	
	public String getPlacaVeiculo() 											{	return placaVeiculo;	}
	public void setPlacaVeiculo(String placaVeiculo) 							{	this.placaVeiculo = placaVeiculo;	}
	
	public Date getDataVeiculo() 												{	return dataVeiculo;	}
	public void setDataVeiculo(Date dataVeiculo) 								{	this.dataVeiculo = dataVeiculo;	}
	
	public UUID getIdMotivoDescarte() 											{	return idMotivoDescarte;	}
	public void setIdMotivoDescarte(UUID idMotivoDescarte) 						{	this.idMotivoDescarte = idMotivoDescarte;	}
	
	public String getMotivoDescarte() 											{	return motivoDescarte;	}
	public void setMotivoDescarte(String motivoDescarte) 						{	this.motivoDescarte = motivoDescarte;	}
	
	public String getObservacao() 												{	return observacao;	}
	public void setObservacao(String observacao) 								{	this.observacao = observacao;	}
	
	public Integer getIdUsuario() 												{	return idUsuario;	}
	public void setIdUsuario(Integer idUsuario) 								{	this.idUsuario = idUsuario;	}
	
	public String getUsuario() 													{	return usuario;	}
	public void setUsuario(String usuario) 										{	this.usuario = usuario;	}
	
	public String getNomeUsuario() 												{	return nomeUsuario;	}
	public void setNomeUsuario(String nomeUsuario) 								{	this.nomeUsuario = nomeUsuario;	}
	
	public boolean isDescartado() 												{	return descartado;	}
	public void setDescartado(boolean descartado) 								{	this.descartado = descartado;	}
	
	public UUID getIdOcorrencia() 												{	return idOcorrencia;	}
	public void setIdOcorrencia(UUID idOcorrencia) 								{	this.idOcorrencia = idOcorrencia;	}

	public boolean isOcorrenciaGerada() 										{	return ocorrenciaGerada;	}
	public void setOcorrenciaGerada(boolean ocorrenciaGerada) 					{	this.ocorrenciaGerada = ocorrenciaGerada;	}

	public boolean isOcorrenciaComNotificacao() 								{	return ocorrenciaComNotificacao;	}
	public void setOcorrenciaComNotificacao(boolean ocorrenciaComNotificacao) 	{	this.ocorrenciaComNotificacao = ocorrenciaComNotificacao;	}

	public List<VeiculoAlerta> getVeiculosAlerta() 								{	return veiculosAlerta;	}
	public void setVeiculosAlerta(List<VeiculoAlerta> veiculosAlerta) 			{	this.veiculosAlerta = veiculosAlerta;	}
	
	public UUID getIdTipoRegistro() 											{	return idTipoRegistro;	}
	public void setIdTipoRegistro(UUID idTipoRegistro) 							{	this.idTipoRegistro = idTipoRegistro;	}

	public String getTipoRegistro() 											{	return tipoRegistro;	}
	public void setTipoRegistro(String tipoRegistro) 							{	this.tipoRegistro = tipoRegistro;	}
	
	public String getDtAlertaStr() 												{	return dtAlertaStr;	}
	public void setDtAlertaStr(String dtAlertaStr) 								{	this.dtAlertaStr = dtAlertaStr;	}	
	
	public int getLembrete() 													{	return lembrete;	}
	public void setLembrete(int lembrete) 										{	this.lembrete = lembrete;	}
	
	public UUID getIdStatusOcorrencia() 										{	return idStatusOcorrencia;	}
	public void setIdStatusOcorrencia(UUID idStatusOcorrencia) 					{	this.idStatusOcorrencia = idStatusOcorrencia;	}
	
	public String getStatusOcorrencia() 										{	return statusOcorrencia;	}
	public void setStatusOcorrencia(String statusOcorrencia) 					{	this.statusOcorrencia = statusOcorrencia;	}

	public boolean isOcorrenciaFinalizada() 									{	return ocorrenciaFinalizada;	}
	public void setOcorrenciaFinalizada(boolean ocorrenciaFinalizada) 			{	this.ocorrenciaFinalizada = ocorrenciaFinalizada;	}
	
	public String getObsFinalizarOcorrencia() 									{	return obsFinalizarOcorrencia;	}
	public void setObsFinalizarOcorrencia(String obsFinalizarOcorrencia) 		{	this.obsFinalizarOcorrencia = obsFinalizarOcorrencia;	}
	
	public UUID getIdPontoInteresse() 											{	return idPontoInteresse;	}
	public void setIdPontoInteresse(UUID idPontoInteresse) 						{	this.idPontoInteresse = idPontoInteresse;	}
	
	public String getNomePontoInteresse() 										{	return nomePontoInteresse;	}
	public void setNomePontoInteresse(String nomePontoInteresse) 				{	this.nomePontoInteresse = nomePontoInteresse;	}
	
	public boolean isAlertaVinculado() 											{	return alertaVinculado;	}
	public void setAlertaVinculado(boolean alertaVinculado) 					{	this.alertaVinculado = alertaVinculado;	}
	
	public UUID getIdAlertaVinculado() 											{	return idAlertaVinculado;	}
	public void setIdAlertaVinculado(UUID idAlertaVinculado) 					{	this.idAlertaVinculado = idAlertaVinculado;	}
	
	public int getIdLocal() 													{	return idLocal; }
	public void setIdLocal(int idLocal) 										{	this.idLocal = idLocal; }
	
	public int getSerieEquipamento() 											{	return serieEquipamento; }
	public void setSerieEquipamento(int serieEquipamento) 						{	this.serieEquipamento = serieEquipamento; }
	
	public int getIdPista() 													{ 	return idPista; }
	public void setIdPista(int idPista) 										{	this.idPista = idPista; }
	
	public int getFaixa() 													{ 	return faixa; }
	public void setFaixa(int faixa) 										{	this.faixa = faixa; }
	
	public int getVelocidade() 													{	return velocidade; }
	public void setVelocidade(int velocidade) 									{	this.velocidade = velocidade; }
	
	public int getTotalRegistros() 													{	return totalRegistros; }
	public void setTotalRegistros(int totalRegistros) 									{	this.totalRegistros = totalRegistros; }
	
	public String getDataCadVeicMonitoradoFormatada() 										{	return new SimpleDateFormat("dd/MM/yyyy").format(dataCadVeicMonitorado);	}
	public void setDataCadVeicMonitoradoFormatada(String dataCadVeicMonitoradoFormatada) 	{	this.dataCadVeicMonitoradoFormatada = dataCadVeicMonitoradoFormatada;	}
	
	public String getHoraCadVeicMonitoradoFormatada() 										{	return new SimpleDateFormat("HH:mm:ss").format(dataCadVeicMonitorado);	}
	public void setHoraCadVeicMonitoradoFormatada(String horaCadVeicMonitoradoFormatada) 	{	this.horaCadVeicMonitoradoFormatada = horaCadVeicMonitoradoFormatada;	}
	
	public String getDataAlertaFormatada() 													{	return new SimpleDateFormat("dd/MM/yyyy").format(dataAlerta);	}
	public void setDataAlertaFormatada(String dataAlertaFormatada) 							{	this.dataAlertaFormatada = dataAlertaFormatada;	}
	
	public String getDataPassagemFormatada() 												{	return new SimpleDateFormat("dd/MM/yyyy").format(dataPassagem);	}
	public void setDataPassagemFormatada(String dataPassagemFormatada) 						{	this.dataPassagemFormatada = dataPassagemFormatada;	}
	
	public String getHoraAlertaFormatada() 													{	return new SimpleDateFormat("HH:mm:ss").format(dataAlerta);	}
	public void setHoraAlertaFormatada(String horaAlertaFormatada) 							{	this.horaAlertaFormatada = horaAlertaFormatada;	}
	
	public String getHoraPassagemFormatada() 												{	return new SimpleDateFormat("HH:mm:ss").format(dataPassagem);	}
	public void setHoraPassagemaFormatada(String horaPassagemFormatada) 					{	this.horaPassagemFormatada = horaPassagemFormatada;	}
	
	public String getDataVeiculoFormatada() 												{	return new SimpleDateFormat("dd/MM/yyyy").format(dataVeiculo);	}
	public void setDataVeiculoFormatada(String dataVeiculoFormatada) 						{	this.dataVeiculoFormatada = dataVeiculoFormatada;	}
	
	public String getHoraVeiculoFormatada() 												{	return new SimpleDateFormat("HH:mm:ss").format(dataVeiculo);	}
	public void setHoraVeiculoFormatada(String horaVeiculoFormatada) 						{	this.horaVeiculoFormatada = horaVeiculoFormatada;	}
	
	public String getEmAtendimentoPor()                                                     {   return emAtendimentoPor; }
	public void setEmAtendimentoPor(String emAtendimentoPor)                                {  this.emAtendimentoPor = emAtendimentoPor;  }
	
	public int getAtendido()                                                     			{   return atendido; }
	public void setAtendido(int atendido)                                					{  this.atendido = atendido;  }
	
	public boolean isPermiteAtendimento() 													{ return permiteAtendimento; }
	public void setPermiteAtendimento(boolean permiteAtendimento) 							{ this.permiteAtendimento = permiteAtendimento; }
	
	public boolean isPermiteAlterarAtendimento() 											{ return permiteAlterarAtendimento; }
	public void setPermiteAlterarAtendimento(boolean permiteAlterarAtendimento) 			{ this.permiteAlterarAtendimento = permiteAlterarAtendimento; }
	
	public int isSupervisionado() 															{ return supervisionado; }
	public void setSupervisionado(int supervisionado) 										{ this.supervisionado = supervisionado; }
	
	public boolean isAssinado() 															{ return assinado; }
	public void setAssinado(boolean assinado) 												{ this.assinado = assinado; }
	
	public int getCom_semelhanca() 															{ return com_semelhanca;	}
	public void setCom_semelhanca(int com_semelhanca) 										{ this.com_semelhanca = com_semelhanca;	}
	
	public int getCom_semelhanca_erros() 													{ return com_semelhanca_erros;	}
	public void setCom_semelhanca_erros(int com_semelhanca_erros) 							{ this.com_semelhanca_erros = com_semelhanca_erros;	}
	
	public String getCom_semelhanca_desc() 													{ return com_semelhanca_desc;	}
	public void setCom_semelhanca_desc(String com_semelhanca_desc) 							{ this.com_semelhanca_desc = com_semelhanca_desc; }
	
	public String getSom() 																	{ return som; }
	public void setSom(String som) 														    { this.som = som; }
	
	public int getSupervisionado() 															{ return supervisionado; }

	public boolean isPossuiFato() 															{ return possui_fato; }
	public void setPossuiFato(boolean possui_fato) 											{ this.possui_fato = possui_fato; }
	
	public int getId_registro_fato() 														{ return id_registro_fato; }
	public void setId_registro_fato(int id_registro_fato) 									{ this.id_registro_fato = id_registro_fato; }
	
	public int getIdUsuarioResponsavel() 													{ return idUsuarioResponsavel; }
	public void setIdUsuarioResponsavel(int idUsuarioResponsavel) 							{ this.idUsuarioResponsavel = idUsuarioResponsavel;	}

	public String getPossui_bo_alerta()														{ return possui_bo_alerta; }
	public void setPossui_bo_alerta(String possui_bo_alerta)								{ this.possui_bo_alerta = possui_bo_alerta; }
	
	public int getNao_assinados() {
		return nao_assinados;
	}
	public void setNao_assinados(int nao_assinados) {
		this.nao_assinados = nao_assinados;
	}
	
	
}
