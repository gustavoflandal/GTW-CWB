package muralha.digital.consulta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "AlertaOcorrencia")
@XmlAccessorType (XmlAccessType.FIELD)
public class AlertaOcorrencia
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(AlertaOcorrencia.class);
	
	private UUID 	id;
	private UUID 	idTipoAlertaOcorrencia;
	private String 	tipoAlertaOcorrencia;
	private UUID 	idCadVeiculoMonitorado;
	private String 	nomeCadMonitorado;
	private UUID 	idStatus;
	private String 	status;
	private Date 	data;
	private String 	placa;
	private String 	placaLida;
	private UUID 	idMotivoDescarte;
	private String 	motivoDescarte;
	private String 	observacao;
	private Integer idUsuario;
	private String 	usuario;
	private String 	tipoRegistro;
	private UUID	idAlerta; //Se for pesquisa de ocorrência, trazer também o id do alerta
	private Integer idLocal;
	private String 	nomeLocal;
	private String 	equipamento;
	private UUID 	idImgObj1;
	private UUID 	idImgObj2;
	public	String 	dataFormatada = "";
	public	String 	horaFormatada = "";	
	public	Integer	idImagem = 0;
	private boolean	descartado;
	private boolean	ocorrenciaGerada;
	private boolean	alertaVinculado;
	private boolean cadMonitoradoAtivo;
	private int idRegistroFato;
	
	public AlertaOcorrencia() {}

	public UUID getId() 												{ return id; }
	public void setId(UUID id) 											{ this.id = id; }

	public UUID getIdTipoAlertaOcorrencia() 							{ return idTipoAlertaOcorrencia; }
	public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) 	{ this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia; }

	public String getTipoAlertaOcorrencia() 							{ return tipoAlertaOcorrencia; }
	public void setTipoAlertaOcorrencia(String tipoAlertaOcorrencia) 	{ this.tipoAlertaOcorrencia = tipoAlertaOcorrencia; }

	public UUID getIdCadVeiculoMonitorado() 							{ return idCadVeiculoMonitorado; }
	public void setIdCadVeiculoMonitorado(UUID idCadVeiculoMonitorado) 	{ this.idCadVeiculoMonitorado = idCadVeiculoMonitorado; }

	public String getNomeCadMonitorado() 								{ return nomeCadMonitorado; }
	public void setNomeCadMonitorado(String nomeCadMonitorado) 			{ this.nomeCadMonitorado = nomeCadMonitorado; }

	public UUID getIdStatus() 											{ return idStatus; }
	public void setIdStatus(UUID idStatus) 								{ this.idStatus = idStatus; }

	public String getStatus() 											{ return status; }
	public void setStatus(String status) 								{ this.status = status; }

	public Date getData()	 											{ return data; }
	public void setData(Date data) 										{ this.data = data; }
	
	public String getPlaca() 											{ return placa; }
	public void setPlaca(String placa) 									{ this.placa = placa; }
	
	public String getPlacaLida() 										{ return placaLida; }
	public void setPlacaLida(String placaLida) 							{ this.placaLida = placaLida; }

	public UUID getIdMotivoDescarte() 									{ return idMotivoDescarte; }
	public void setIdMotivoDescarte(UUID idMotivoDescarte) 				{ this.idMotivoDescarte = idMotivoDescarte; }

	public String getMotivoDescarte() 									{ return motivoDescarte; }
	public void setMotivoDescarte(String motivoDescarte) 				{ this.motivoDescarte = motivoDescarte; }

	public String getObservacao() 										{ return observacao; }
	public void setObservacao(String observacao) 						{ this.observacao = observacao; }

	public Integer getIdUsuario() 										{ return idUsuario; }
	public void setIdUsuario(Integer idUsuario) 						{ this.idUsuario = idUsuario; }

	public String getUsuario() 											{ return usuario; }
	public void setUsuario(String usuario) 								{ this.usuario = usuario; }

	public String getTipoRegistro() 									{ return tipoRegistro; }
	public void setTipoRegistro(String tipoRegistro) 					{ this.tipoRegistro = tipoRegistro; }

	public UUID getIdAlerta() 											{ return idAlerta; }
	public void setIdAlerta(UUID idAlerta) 								{ this.idAlerta = idAlerta; }

	public Integer getIdLocal() 										{ return idLocal; }
	public void setIdLocal(Integer idLocal)								{ this.idLocal = idLocal; }

	public String getNomeLocal() 										{ return nomeLocal; }
	public void setNomeLocal(String nomeLocal) 							{ this.nomeLocal= nomeLocal; }
	
	public String getEquipamento() 										{ return equipamento; }
	public void setEquipamento(String equipamento) 						{ this.equipamento = equipamento; }

	public UUID getIdImgObj1() 											{ return idImgObj1; }
	public void setIdImgObj1(UUID idImgObj1) 							{ this.idImgObj1 = idImgObj1; }	
	
	public UUID getIdImgObj2() 											{ return idImgObj2; }
	public void setIdImgObj2(UUID idImgObj2) 							{ this.idImgObj2 = idImgObj2; }	
	
	public String getDataFormatada() 									{ return new SimpleDateFormat("dd/MM/yyyy").format(data); }
	public void setDataFormatada(String dataFormatada) 					{ this.dataFormatada = dataFormatada; }
	
	public String getHoraFormatada() 									{ return new SimpleDateFormat("HH:mm:ss").format(data); }
	public void setHoraFormatada(String horaFormatada) 					{ this.horaFormatada = horaFormatada; }
	
	public Integer getIdImagem()										{ return idImagem; }
	public void	setIdImagem(Integer idImagem)							{ this.idImagem = idImagem; }
	
	public boolean isDescartado() 										{ return descartado; }
	public void setDescartado(boolean descartado) 						{ this.descartado = descartado;	}
	
	public boolean isOcorrenciaGerada() 								{ return ocorrenciaGerada; }
	public void setOcorrenciaGerada(boolean ocorrenciaGerada) 			{ this.ocorrenciaGerada = ocorrenciaGerada;	}
	
	public boolean isAlertaVinculado() 									{ return alertaVinculado; }
	public void setAlertaVinculado(boolean alertaVinculado) 			{ this.alertaVinculado = alertaVinculado; }
	
	public boolean isCadMonitoradoAtivo() { return cadMonitoradoAtivo; }
	public void setCadMonitoradoAtivo(boolean ativo) { this.cadMonitoradoAtivo = ativo; }

	public int getIdRegistroFato() 										{ return idRegistroFato; }
	public void setIdRegistroFato(int idRegistroFato) 					{ this.idRegistroFato = idRegistroFato;	}
	
	
}
