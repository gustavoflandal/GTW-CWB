package muralha.configuracaoequipamento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

@XmlRootElement(name = "ConfiguracaoEquipamento")
@XmlAccessorType (XmlAccessType.FIELD)
public class ConfiguracaoEquipamento 
{
	@XmlTransient
	private static final Logger logger = Logger.getLogger(ConfiguracaoEquipamento.class);
	
	// 											Veiculo clonado
	private int 	clonado_status;
	
	// 											Veiculo roubado
	private int 	roubado_status;
	
	// 											Sequestro relampago
	private int	 	sequestro_status;
	
	// 											Veiculo furtado
	private int 	furtado_status;
	
	// 											Veiculo com atraso de licenciamento
	private int 	licenciamento_status;
	
	// 											Veiculo monitorado
	private int 	monitorado_status;
	
	// 											Comboio
	private int 	comboio_status;
	private String 	comboio_intervalo;
	
	// 											Roubo a banco
	private int 	banco_status;
	private String 	banco_intervalo;
	
	// 											Veiculo clandestino
	private int 	clandestino_status;
	private String 	clandestino_init_manha;
	private String 	clandestino_fim_manha;
	private String 	clandestino_init_tarde;
	private String 	clandestino_fim_tarde;
	private int 	clandestino_passagens;
	private String 	clandestino_tipo;
	private int 	QtdeErrosSemelhanca;
	private int 	qtd_pas_correlacao_baixa;
	private int 	qtd_pas_correlacao_media;
	private int 	qtd_pas_correlacao_alta;
	
	public int 		getStatusClonado()						{return clonado_status;};
	public void 	setStatusClonado(int status)			{this.clonado_status = status;};
	
	public int 		getStatusRoubado()						{return roubado_status;};
	public void 	setStatusRoubado(int status)			{this.roubado_status = status;};
	
	public int 		getStatusSequestro()					{return sequestro_status;};
	public void 	setStatusSequestro(int status)			{this.sequestro_status = status;};
	
	public int 		getStatusFurtado()						{return furtado_status;};
	public void 	setStatusFurtado(int status)			{this.furtado_status = status;};
	
	public int 		getStatusLicenciamento()						{return licenciamento_status;};
	public void 	setStatusLicenciamento(int status)			{this.licenciamento_status = status;};
	
	public int 		getStatusMonitorado()						{return monitorado_status;};
	public void 	setStatusMonitorado(int status)			{this.monitorado_status = status;};
	
	public int 		getStatusComboio()						{return comboio_status;};
	public void 	setStatusComboio(int status)			{this.comboio_status = status;};
	
	public String	getIntervaloComboio()					{return comboio_intervalo;};
	public void		setIntervaloComboio(String intervalo)	{this.comboio_intervalo = intervalo;};
	
	public int 		getStatusBanco()						{return banco_status;};
	public void 	setStatusBanco(int status)				{this.banco_status = status;};
	
	public String	getIntervaloBanco()						{return banco_intervalo;};
	public void		setIntervaloBanco(String intervalo)		{this.banco_intervalo = intervalo;};
	
	public int 		getStatusClandestino()					{return clandestino_status;};
	public void 	setStatusClandestino(int status)		{this.clandestino_status = status;};
	
	public String	getInitManhaClandestino()				{return clandestino_init_manha;};
	public void		setInitManhaClandestino(String init)	{this.clandestino_init_manha = init;};
	
	public String	getFimManhaClandestino()				{return clandestino_fim_manha;};
	public void		setFimManhaClandestino(String fim)		{this.clandestino_fim_manha = fim;};
	
	public String	getInitTardeClandestino()				{return clandestino_init_tarde;};
	public void		setInitTardeClandestino(String init)	{this.clandestino_init_tarde = init;};
	
	public String	getFimTardeClandestino()				{return clandestino_fim_tarde;};
	public void		setFimTardeClandestino(String fim)		{this.clandestino_fim_tarde = fim;};
	
	public int 		getPassagensClandestino()				{return clandestino_passagens;};
	public void 	setPassagensClandestino(int passagens)	{this.clandestino_passagens = passagens;};
	
	public String	getTipoClandestino()					{return clandestino_tipo;};
	public void 	setTipoClandestino(String tipo)			{this.clandestino_tipo = tipo;}
	
	public int getQtdeErrosSemelhanca() {
		return QtdeErrosSemelhanca;
	}
	public void setQtdeErrosSemelhanca(int qtdeErrosSemelhanca) {
		QtdeErrosSemelhanca = qtdeErrosSemelhanca;
	}
	public int getQtdPasCorrelacaoBaixa() {
		return qtd_pas_correlacao_baixa;
	}
	public void setQtdPasCorrelacaoBaixa(int qtdPasCorrelacaoBaixa) {
		this.qtd_pas_correlacao_baixa = qtdPasCorrelacaoBaixa;
	}
	public int getQtdPasCorrelacaoMedia() {
		return qtd_pas_correlacao_media;
	}
	public void setQtdPasCorrelacaoMedia(int qtdPasCorrelacaoMedia) {
		this.qtd_pas_correlacao_media = qtdPasCorrelacaoMedia;
	}
	public int getQtdPasCorrelacaoAlta() {
		return qtd_pas_correlacao_alta;
	}
	public void setQtdPasCorrelacaoAlta(int qtdPasCorrelacaoAlta) {
		this.qtd_pas_correlacao_alta = qtdPasCorrelacaoAlta;
	}
	
}