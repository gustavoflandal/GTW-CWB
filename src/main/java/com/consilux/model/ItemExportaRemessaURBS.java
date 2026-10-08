//package com.consilux.model;
//
//import java.util.Date;
//
//import javax.validation.constraints.AssertTrue;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;
//
//import com.consilux.infra.Funcoes;
//
//public class ItemExportaRemessaURBS extends ItemExportaRemessa {
//
//	private static final int ORGAO_AUTUADOR_URBS = 275350;
//	private static final int LOCALIDADE_CURITIBA = 7535; 
//	
//	private final Integer orgaoAutuador;		// %1
//	private String serieRemessa;				// %2
//	// HERDADO: auto							// %3
//	// HERDADO: placa;							// %4
//	private final String ufPlaca;				// %5
//	private final Integer idMarca;				// %6
//	// HERDADO: dataInfracao;					// %7
//	
//	// HERDADO: nomeLocal;						// %8
//	
//	private final Integer idLocalidade;			// %9
//	// HERDADO: idEnquadramento;				// %10
//	
//	private Integer parametroLimite;			//	%11 (p1)
//	private Integer parametroRegistrado;		//  %12 (p2)
//	
//	private String siglaInfracaoCliente; 		// %13 BEL,RAD, etc
//	private Integer serieEquipamento;			// %14 (nº de série do equipamento) 990XXXX
//	// HERDADO: codAgente;						// %15
//	private String ufAgente;					// %16
//	private Boolean existeFoto;					// %17
//	private ModeloInstrumento modeloIntrumento;	// %18
//	private Date dataAfericao;					// %19
//	
//	private String tipoRemessa;
//	private Date dataValidadeAfericao;
//	
//	// http://java.sun.com/j2se/1.5.0/docs/api/java/util/Formatter.html#syntax
//	private static String formatador = 
//		"%1$06d" +						// ZERO-PADDED, tamanho = 6, Inteiro
//		 "%2$1s" + 						// tamanho = 1, String
//		 "%3$08d" +						// ZERO-PADDED, tamanho = 8, Inteiro
//		 "%4$-10s" +					// Left-Justified, tamanho = 10, String
//		 "%5$2S" +						// tamanho = 2, UPPERCASE, String
//		 "%6$06d" +						// ZERO-PADDED, tamanho = 6, Inteiro
//		 "%7$tY%7$tm%7$td%7$tH%7$tM" +	// "aaaammddhhmm"
//		 "%8$-30s" +					// Left-Justified, tamanho = 30, String
//		 "%9$04d" +						// ZERO-PADDED, tamanho = 4, Inteiro
//		 "%10$3s" +						// tamanho = 3, String
//		 "%11$05d" +					// ZERO-PADDED, tamanho = 5, Inteiro
//		 "%12$05d" +					// ZERO-PADDED, tamanho = 5, Inteiro
//		 "%13$3s" +						// tamanho = 3, String
//		 "%14$-10s" +					// Left-Justified, tamanho = 10, String
//		 "%15$-10s" +					// Left-Justified, tamanho = 10, String
//		 "%16$2S" +						// tamanho = 2, UpperCase, String
//		 "%17$1s" +						// tamanho = 1, String
//		 "%18$1s" +						// tamanho = 1, String
//		 "%19$8s";						// tamanho = 8, String, "aaaammdd"
//	
//	public ItemExportaRemessaURBS(Integer idInfracao, Integer numeroAuto,
//		String tipoRemessa, String serieRemessa, String siglaInfracaoCliente,
//		String placa, String ufPlaca, Integer idMarca, Date dataInfracao, String nomeLocal,
//		Integer idEnquadramento, Integer parametroLimite, Integer parametroRegistrado,
//		Integer serieEquipamento, Integer codAgente, String ufAgente,
//		ModeloInstrumento modeloIntrumento, Date dataAfericao, Integer idImagem,
//		Date dataValidadeAfericao)
//	{
//		
//		super(idInfracao, numeroAuto, idEnquadramento, null, nomeLocal,
//				serieEquipamento, dataInfracao, null, codAgente, serieRemessa, placa, idImagem, null);
//		
//		
//		this.tipoRemessa = tipoRemessa;
//		
//		this.orgaoAutuador = ORGAO_AUTUADOR_URBS; 					// %1
//		// HERDADO: auto											// %2
//		this.serieRemessa = serieRemessa;							// %3
//		
//		// HERDADO: auto 											// %4
//		this.ufPlaca = ufPlaca;										// %5
//		this.idMarca = idMarca;										// %6
//		this.dataInfracao = dataInfracao;							// %7
//		
//		if (nomeLocal != null)										// %8
//		{
//			this.nomeLocal = Funcoes.retirarAcentos(nomeLocal.trim());
//		}
//
//		this.idLocalidade = LOCALIDADE_CURITIBA;					// %9
//		
//		this.idEnquadramento = idEnquadramento;						// %10
//		
//		this.parametroLimite = parametroLimite;						// %11
//		this.parametroRegistrado = parametroRegistrado;				// %12
//		if (Enquadramento.isEnquadramentoVelocidade(idEnquadramento));
//		{
//			super.velocidade = parametroRegistrado;
//		}
//		
//		this.siglaInfracaoCliente = siglaInfracaoCliente;			// %13
//		if (this.siglaInfracaoCliente != null && this.siglaInfracaoCliente.length() > 3)
//		{
//			this.siglaInfracaoCliente = this.siglaInfracaoCliente.substring(0, 3);
//		}
//		
//		this.serieEquipamento = serieEquipamento;					// %14
//		
//		// HERDADO: codAgente										// %15
//		
//		this.ufAgente = ufAgente;									// %16
//		this.existeFoto = true;										// %17
//		this.modeloIntrumento = modeloIntrumento;					// %18
//		
//		this.dataAfericao = dataAfericao;							// %19
//		this.dataValidadeAfericao = dataValidadeAfericao;
//
//	}
//
//	@Override
//	public String getLinhaRemessa() {
//		
//		// Ajuste específico para a CELEPAR, pois só serão enviados os 3 primeiros
//		// dígitos do enquadramento;
//		String sEnquadramento = Integer.toString(idEnquadramento);	// %10
//		if (sEnquadramento.length() > 3)
//			sEnquadramento = sEnquadramento.substring(0, 3);
//
//		// Realiza uma juste porque nosso nº de série é numérico, mas o campo
//		// para a CELEPAR é Alfa-Numérico.
//		String sSerieEquipamento = Integer.toString(serieEquipamento);	// %14
//		if (sSerieEquipamento.length() > 10)
//			sSerieEquipamento = sSerieEquipamento.substring(0, 10);		
//		
//		// Mais um ajuste, pois o código do agente é alfa-numérico
//		String sCodAgente = "";
//		if (codAgente != null)
//		{
//			sCodAgente = Integer.toString(codAgente);				// %15
//		}		
//		
//		String sDataAfericao = "00000000";
//		// Somente se for uma infração metrológica, coloca junto a data de aferição.
//		if (Enquadramento.isEnquadramentoMetrologico(idEnquadramento))
//		{
//			sDataAfericao = String.format("%1$tY%1$tm%1$td", dataAfericao); // %19
//		} 	
//		
//		final String sRet = String.format(formatador,
//				orgaoAutuador,						// %1	(6)	OBS: "URBS - 275350"
//				serieRemessa,						// %2	(1)
//				getAuto(),							// %3	(8)
//				placa,								// %4	(10)
//				ufPlaca,							// %5	(2)
//				idMarca,							// %6	(6)
//				dataInfracao,						// %7	(12) "aaaammddhhmm"
//				nomeLocal,							// %8	(30)
//				idLocalidade,						// %9   (4)  OBS: "Curitiba - 7535"
//				sEnquadramento,						// %10	(3)
//				parametroLimite,					// %11	(5) (p1)
//				parametroRegistrado,				// %12	(5)	(p2)
//				siglaInfracaoCliente,				// %13	(3) "RAD, BEL, FEL, LEL", etc...
//				sSerieEquipamento,					// %14	(10)
//				sCodAgente,							// %15	(10)
//				ufAgente,							// %16	(2)
//				existeFoto ? "S" : "N",				// %17	(1)	OBS: Valor constante: "S"
//				modeloIntrumento.getCodigoModelo(),	// %18	(1)	"F=Fixo, P=Portátil, M=Movel, E=Estático"...
//				sDataAfericao						// %19	(8)	"aaaammdd"
//			);
//		
//		return sRet;
//	}
//
//	@Override
//	public String getNomeArquivoImagem() {
//		
//		// Nome de cada imagem é formatado como: "XXXXXXXX.YYY", onde
//		// "XXXXXXXXXXX" = Número do auto
//		// "YYY" = tipo da remessa, 3 caracteres
//		
//		return String.format("%1$08d.%2$3S",
//				getAuto(), 	   // %1
//				tipoRemessa);  // %2
//	}
//
//	public Integer getOrgaoAutuador() {
//		return orgaoAutuador;
//	}
//
//	public String getSerieRemessa() {
//		return serieRemessa;
//	}
//
//	public String getUfPlaca() {
//		return ufPlaca;
//	}
//
//	public Integer getIdMarca() {
//		return idMarca;
//	}
//
//	@NotNull(message="A descrição do local não pode ser nula.")
//	@Size(min = 1, max = 30, message="A descrição do local deve possuir entre 1 e 30 caracteres.")  	
//	public String getNomeLocal() {
//		return nomeLocal;
//	}
//
//	public Integer getIdLocalidade() {
//		return idLocalidade;
//	}
//
//	public Integer getParametroLimite() {
//		return parametroLimite;
//	}
//
//	public Integer getParametroRegistrado() {
//		return parametroRegistrado;
//	}
//
//	public String getSiglaInfracaoCliente() {
//		return siglaInfracaoCliente;
//	}
//
//	public Integer getSerieEquipamento() {
//		return serieEquipamento;
//	}
//
//	@NotNull(message = "UF do agente não pode ser nulo.")
//	@Size(min = 2, max = 2, message = "UF do agente deve possuir exatamente 2 caracteres.")
//	@Pattern(regexp="A[CLMP]|BA|CE|DF|ES|GO|M[AGST]|P[ABEIR]|R[JNORS]|S[CEP]|TO", message="UF do agente dev ser um estado brasileiro.")
//	public String getUfAgente() {
//		return ufAgente;
//	}
//
//	public Boolean isExisteFoto() {
//		return existeFoto;
//	}
//
//	public ModeloInstrumento getModeloIntrumento() {
//		return modeloIntrumento;
//	}
//
//	@AssertTrue(message="Data de aferição com problemas.")
//	protected boolean isDataAfericaoOk() {
//		
//		if(Enquadramento.isEnquadramentoVelocidade(idEnquadramento)) {
//			return dataValidadeAfericao != null && getDataInfracao().before(dataValidadeAfericao) ;
//		}
//		else
//		{
//			return true;
//		}
//	}	
//	
//	public Date getDataAfericao() {
//		return dataAfericao;
//	}
//
//	public String getTipoRemessa() {
//		return tipoRemessa;
//	}
//	
//	/* (non-Javadoc)
//	 * Sobrescreve o método original, pois para o caso da URBS a velocidade
//	 * é armazenada no atributo "parametroRegistrado".  
//	 * @see com.consilux.model.ItemExportaRemessa#getVelocidade()
//	 */
//	@Override
//	
//	public Integer getVelocidade() {
//		return this.parametroRegistrado;
//	}
//
//	public Date getDataValidadeAfericao() {
//		return dataValidadeAfericao;
//	}
//
//	@Override
//	public String getNomeArquivoTXT() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//}
