/**
 * 
 */
package com.consilux.model;

import java.sql.SQLException;
import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.consilux.infra.exception.ConexaoException;

/**
 * Classe que representa um item (uma linha do TXT) de uma remessa. 
 * @author fos
 */
public abstract class ItemExportaRemessa {

	protected Integer idInfracao;
	protected Integer numeroAuto;
	
	protected Integer idEnquadramento;
	protected Integer codLocal;
	protected String nomeLocal;
	protected Integer codEquipamento;
	protected Date dataInfracao;
	protected Integer velocidade;
	protected Integer codAgente;
	protected String serieRemessa;
	protected String placa;
	protected Integer idImagem;
	protected InfracaoObliteracao obliteracao;
	
	protected Integer sequenciaImagemLocal;
	protected byte[] imagem;
	
	protected Integer comVideo;
	
	private ItemExportaRemessaCET_VM velocidadeMedia;
	
	protected ItemExportaRemessa(Integer idInfracao, Integer numeroAuto,
		Integer idEnquadramento, Integer codLocal, String nomeLocal, Integer codEquipamento,
		Date dataInfracao, Integer velocidade, Integer codAgente, String serieRemessa,
		String placa, Integer idImagem, Integer sequenciaImagemLocal, byte[] imagem, Integer comVideo,
		InfracaoObliteracao obliteracao) {
		
		this.idInfracao = idInfracao;
		this.numeroAuto = numeroAuto;
		
		this.idEnquadramento = idEnquadramento;
		this.codLocal = codLocal;
		this.nomeLocal = nomeLocal;
		this.codEquipamento = codEquipamento;
		this.dataInfracao = dataInfracao;
		this.velocidade = velocidade;
		this.codAgente = codAgente;
		this.serieRemessa = serieRemessa;
		this.placa = placa;
		this.idImagem = idImagem;
		this.obliteracao = obliteracao;
		
		this.sequenciaImagemLocal = sequenciaImagemLocal;
		this.imagem = imagem;
		
		this.comVideo = comVideo;
	}

//	@NotNull(message = "Identificador da infração não pode ser nulo.")
//	@Min(value = 1, message = "Identificador da infração não poava.util.concurrent. de ser menor que 1.")
	public Integer getIdInfracao() {
		return idInfracao;
	}

//	@NotNull(message = "Número do auto não pode ser nulo.")
//	@Min(value = 1, message = "Número do auto não pode ser menor que 1.")
	public Integer getAuto() {
		return numeroAuto;
	}

	@NotNull(message = "Identificador do enquadramento não pode ser nulo.")
	@Min(value = 2, message = "Identificador do enquadramento não pode ser menor que 2.")		
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	// Validações de nome de local devem ser específicas de cada classe-filha.
	public String getNomeLocal() {
		return nomeLocal;
	}

//	@NotNull(message = "Código do equipamento não pode ser nulo.")
//	@Min(value = 2, message = "Código do equipamento não pode ser menor que 1.")				
	public Integer getCodEquipamento() {
		return codEquipamento;
	}

	@NotNull(message = "A data da infração não pode ser nula.")
//	@Past(message = "A data da infração não pode ser no futuro.")
	public Date getDataInfracao() {
		return dataInfracao;
	}

//	@NotNull(message = "Código do agente não pode ser nulo.")
//	@Min(value = 2, message = "Código do agente não pode ser menor que 1.")				
	public Integer getCodAgente() {
		return codAgente;
	}

//	@NotEmpty(message = "A série do auto de infração não pode ser nula e nem em branco.")
	public String getSerieRemessa() {
		return serieRemessa;
	}

	// Velocidade deve ser validada apenas se o enquadramento for de velocidade.
	public Integer getVelocidade() {
		return velocidade;
	}	

	@AssertTrue(message="Infração metrológica com problemas na velocidade.")
	protected boolean isVelocidadeOk() {
		if(Enquadramento.isEnquadramentoVelocidade(idEnquadramento))
		{
			// Validações específicas para os enquadramentos de velocidade
			return velocidade != null && velocidade > 10 && velocidade <= 300;  
		}
		else
		{
			return true;
		}
	}
	
//	@NotEmpty(message = "A placa não pode ser nula e nem vazia.")
//	@Pattern(regexp=IExpValida.PLACA, message="A placa não pode ser inválida.")
	public String getPlaca() {
		return placa;
	}
	
	@NotNull(message = "Id da imagem não pode ser nulo.")
	@Min(value = 1, message = "Id da imagem deve ser maior que zero.")		
	public Integer getIdImagem()
	{
		return idImagem;
	}
	
	public InfracaoObliteracao getObliteracao() {
		return obliteracao;
	}

	public void setObliteracao(InfracaoObliteracao obliteracao) {
		this.obliteracao = obliteracao;
	}
	
	public Integer getSequenciaImagemLocal()
	{
		return this.sequenciaImagemLocal;
	}
	
	public byte[] getImagem()
	{
		return this.imagem;
	}
	
	public boolean comVideo()
	{
		return (this.comVideo == 1);
	}

	// Métodos que devem ser implementados pelas classes filhas
	public abstract String getLinhaRemessa();
	public abstract String getNomeArquivoTXT();
	public abstract String getNomeArquivoImagem();
	public abstract String getNomeArquivoVideo(int sequenciaImagemLocal);
	public abstract StringBuilder getDadosTarja() throws ConexaoException, SQLException;

	public ItemExportaRemessaCET_VM getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public void setVelocidadeMedia(ItemExportaRemessaCET_VM velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}
}
