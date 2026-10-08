/**
 * 
 */
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe que representa um item de uma remessa da CET.
 * 
 * @author fos
 */
public class ItemExportaRemessaCET extends ItemExportaRemessa {

//	private static Logger logger = Logger
//			.getLogger(ItemExportaRemessaCET.class);

	private Integer idRemessa;
	private Date dataRemessa;
	private Date dataValidacao;
	private String tipo;
	private Integer dacAuto;
	private Integer pais;
	private Integer idMarca;
	private Integer idEspecie;
	private Integer sequencia;
	private Integer pista;

	private Integer idImagemLocal;
	private Integer idInconsistencia;
	private Integer idInconsistenciaValidacao;

//	private String codigo_empresa;
	
	//private Map<Integer, String> codigos_apait = EnquadramentoRegraInfracao.TiposAPAIT;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	
	private int velocidade_considerada;
	private int velocidade_regul;
	
	private Integer tipoExportaRemessa;
	private Date data_analise;
	
	private Integer codOperador;

	public ItemExportaRemessaCET(
			Integer idInfracao, 
			Integer idImagemLocal,
			Integer sequenciaImagemLocal, 
			byte[] imagem,
			Integer idInconsistencia, 
			Integer idInconsistenciaValidacao,
			Integer numeroAuto, 
			Integer idRemessa, 
			Date dataRemessa,
			Date dataValidacao, 
			Integer tipoExportaRemessa, 
			String tipo, 
			String serieRemessa,
			Integer dacAuto, 
			Integer sequencia, 
			String placa, 
			Integer pais,
			Integer idMarca, 
			Integer idEspecie, 
			Integer idEnquadramento,
			Integer codLocal, 
			String nomeLocal, 
			Integer codEquipamento,
			Date dataInfracao, 
			Integer velocidade,
			Integer velocidade_considerada, 
			Integer velocidade_regul,
			Integer pista, 
			Integer codOperador,
			Integer codAgente, 
			Date data_analise, 
			Integer idImagem, 
			Integer comVideo, 
			InfracaoObliteracao obliteracao
			) {

		super(idInfracao, numeroAuto, idEnquadramento, codLocal, nomeLocal,
				codEquipamento, dataInfracao, velocidade, codAgente,
				serieRemessa, placa, idImagem, sequenciaImagemLocal, imagem, comVideo,
				obliteracao);

		this.dataRemessa = dataRemessa;
		this.dataValidacao = dataValidacao;
		this.tipoExportaRemessa = tipoExportaRemessa;
		this.tipo = tipo;
		this.dacAuto = dacAuto;
		this.pais = pais;
		this.idMarca = idMarca == null ? 0 : idMarca;
		this.idEspecie = idEspecie == null ? 0 : idEspecie;
		this.codOperador = codOperador;
		this.sequencia = sequencia;
		this.idImagemLocal = idImagemLocal;
		this.idInconsistencia = idInconsistencia;
		this.idInconsistenciaValidacao = idInconsistenciaValidacao;
		this.idRemessa = idRemessa;

		this.pista = pista;
		this.data_analise = data_analise;
		this.velocidade_considerada = velocidade_considerada;
		this.velocidade_regul = velocidade_regul;
	}

	/**
	 * Retorna o valor do campo 'dataRemessa' atual.
	 * 
	 * @return the data
	 */
	public Date getDataRemessa() {
		return this.dataRemessa;
	}

	public String getTipo() {
		return tipo;
	}

	public Integer getDacAuto() {
		return dacAuto;
	}

	public Integer getPais() {
		return pais;
	}

	public Integer getIdMarca() {
		return idMarca;
	}

	public Integer getIdEspecie() {
		return idEspecie;
	}

//	@NotNull(message = "Código do local não pode ser nulo.")
//	@Min(value = 2, message = "Código do local não pode ser menor que 1.")
	public Integer getCodLocal() {
		return codLocal;
	}

	public int getVelocidade_regul() {
		return velocidade_regul;
	}

	@Override
	public String getLinhaRemessa() {
		// XXX: Felipe
		String linha = null;
		boolean enquadramento_velocidade = this.idEnquadramento == 74550 || this.idEnquadramento == 74630 || this.idEnquadramento == 74710;
		if (this.idEnquadramento == 99999)
		{
		String formato = "%1d%8s%2s%07d%04d%07d%7.7s%02d%03d%-20.20s%03d%-20.20s%04d%-80.80s%05d%05d%05d%04d%-80.80s%8s%6s%05d%04d%-15.15s%8s%04d%-80.80s%8s%6s%05d%04d%-15.15s%8s%-200.200s%1d%06d%8s%07d%1d%02d%1d";
		linha = String.format(formato, 
				2, // %1d
				dateFormat.format(this.dataRemessa), // %2
				this.tipo, // %3
				this.idRemessa,  
				this.sequencia,
				this.idImagemLocal,
				this.placa, // %8
				this.pais,
				this.idMarca, // %10
				getVelocidadeMedia().getDescricaoMarca(),
				this.idEspecie, // %11
				getVelocidadeMedia().getDescricaoEspecie(),
				
				// Dados do TRECHO
				getVelocidadeMedia().getCodigoLocalTrecho(),
				getVelocidadeMedia().getDescricaoLocalTrecho(),
				// Dados de Velocidade Média
				getVelocidadeMedia().getVelocidadeRegulamentadaTrecho() * 100,
				getVelocidadeMedia().getVelocidadeMediaCalculada() * 100,
				getVelocidadeMedia().getVelocidadeMediaConsiderada() * 100,
				
				// Dados do MONTANTE
				getVelocidadeMedia().getCodigoLocalInicio(),
				getVelocidadeMedia().getDescricaoLocalInicio(),
				dateFormat.format(getVelocidadeMedia().getDataRegistroInicio()),
				timeFormat.format(getVelocidadeMedia().getDataRegistroInicio()),
				getVelocidadeMedia().getVelocidadeMedidaInicio() * 100,
				getVelocidadeMedia().getCodigoEquipamentoInicio(),
				getVelocidadeMedia().getSerieEquipamentoInicio().toString(),
				dateFormat.format(getVelocidadeMedia().getDataAfericaoInicio()),
				
				// Dados do JUSANTE
				getVelocidadeMedia().getCodigoLocalFim(),
				getVelocidadeMedia().getDescricaoLocalFim(),
				dateFormat.format(getVelocidadeMedia().getDataRegistroFim()),
				timeFormat.format(getVelocidadeMedia().getDataRegistroFim()),
				getVelocidadeMedia().getVelocidadeMedidaFim() * 100,
				getVelocidadeMedia().getCodigoEquipamentoFim(),
				getVelocidadeMedia().getSerieEquipamentoFim().toString(),
				dateFormat.format(getVelocidadeMedia().getDataAfericaoFim()),
				
				// Outros 
				getVelocidadeMedia().getDescricao(),
				this.pista,
				this.codOperador,
				dateFormat.format(this.data_analise),
				getVelocidadeMedia().getNumeroRegistroMontante(),
				(this.idInconsistencia > 0 ? 0 : 1),
				this.idInconsistencia,
				0 // Campo 20 - Imagem da notificação 
				);
		}
		else {
		String formato = "%1d%8s%2s%07d%04d%07d%7.7s%02d%03d%03d%05d%04d%-80.80s%04d%8s%6s%03d%03d%03d%1d%06d%8s%04d%1d%02d%1d";
		linha = String
				.format(formato,
						2, // %1
						dateFormat.format(this.dataRemessa), // %2
						this.tipo, //codigos_apait.get(this.idEnquadramento), // %3
						this.idRemessa,  // XXX: this.cod_externo (safe? ver ExportaRemessaCET)
						this.sequencia,
						this.idImagemLocal,
						this.placa, // %8
						this.pais,
						this.idMarca, // %10
						this.idEspecie, // %11
						this.idEnquadramento, // %12
						this.codLocal != null ? this.codLocal : 0, // %13
						this.nomeLocal != null ? this.nomeLocal : "", // %14 //XXX VER
						this.codEquipamento != null ? this.codEquipamento : 0, // %15
						dateFormat.format(this.dataInfracao), // %16
						timeFormat.format(this.dataInfracao), // %17
						enquadramento_velocidade ? this.velocidade : 0,
						enquadramento_velocidade ? this.velocidade_considerada : 0,
						enquadramento_velocidade ? this.velocidade_regul : 0,
						this.pista,		
						this.codOperador != null ? this.codOperador : 0, 
						dateFormat.format(this.data_analise),
						0, // TODO: Felipe Campo 19 - Número de registro da Montante
						this.idInconsistencia > 0 ? 0 : 1,
						this.idInconsistencia,
						0 // Campo 20 - Imagem da notificação 
						);
		}
		if (tipoExportaRemessa != 0 && dataValidacao != null && this.idInconsistenciaValidacao >= 0)
			linha += String.format("%1d%8s%07d",
					this.idInconsistenciaValidacao > 0 ? 0 : 1,
					dateFormat.format(this.dataValidacao),
					this.codAgente);
		return linha;
	}

	@Override
	public String getNomeArquivoTXT() {
		return String.format("%2s%2s%07d%8s%04d%1d%4s", "TX", this.tipo, //codigos_apait.get(this.idEnquadramento),
				idRemessa, dateFormat.format(dataRemessa), sequencia,
				sequenciaImagemLocal, ".TXT");
	}

	@Override
	public String getNomeArquivoImagem() {
		return String.format("%2s%2s%07d%8s%04d%1d%4s", "IM", this.tipo, //codigos_apait.get(this.idEnquadramento),
				idRemessa, dateFormat.format(dataRemessa), sequencia,
				sequenciaImagemLocal, ".JPG");
	}
	
	public String getNomeArquivoVideo(int sequenciaImagemLocal) {
		return String.format("%2s%2s%07d%8s%04d%1d%4s", "IM", this.tipo, //codigos_apait.get(this.idEnquadramento),
				idRemessa, dateFormat.format(dataRemessa), sequencia,
				sequenciaImagemLocal, ".MP4");
	}

	@Override
	public StringBuilder getDadosTarja() throws ConexaoException, SQLException {
		boolean enquadramentoVelocidade = Enquadramento
				.isEnquadramentoVelocidade(idEnquadramento);
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT ");
		sbSQL.append(" [DT INFR], ");
		sbSQL.append(" [HOR INFR], ");
		if (enquadramentoVelocidade)
			sbSQL.append(" [CLASSIF], ");
		sbSQL.append(" [LOCAL/SENTIDO], ");
		sbSQL.append(" [COD EQUIP], ");
		if (idEnquadramento == 56732 || idEnquadramento == 60503)
			sbSQL.append(" [T.DECOR.VERM], ");
		if (idEnquadramento == 56732)
			sbSQL.append(" [T. PERM], ");
		if (idEnquadramento == 60503)
			sbSQL.append(" [T. RETAR], ");
		if (enquadramentoVelocidade)
			sbSQL.append(" [DT AFER], ");
		if (idEnquadramento == 56900 || idEnquadramento == 57461
				|| idEnquadramento == 57463)
			sbSQL.append(" [HORÁRIO PROIBIDO], ");
		if (idEnquadramento != 60411 && idEnquadramento != 60412)
			sbSQL.append(" [FX ROL], ");
		if (idEnquadramento == 57030 || idEnquadramento == 57461
				|| idEnquadramento == 57463)
			sbSQL.append(" [CADASTRO SP], ");
		if (enquadramentoVelocidade) {
			sbSQL.append(" [VEL REG], ");
			sbSQL.append(" [VEL MEDIDA], ");
			sbSQL.append(" [VEL CONS], ");
		}
		if (idEnquadramento == 57462)
			sbSQL.append(" [DIA SEM], ");
		sbSQL.append(" [No. SEQ REG], ");
		sbSQL.append(" [COD ENQ], ");
		sbSQL.append(" [DESCRIÇÃO] ");
		sbSQL.append(" FROM fcn_ObterDadosTarja(?)");

		Connection conn = null;
		PreparedStatement ps;
		ResultSet rs;
		StringBuilder sbResult = new StringBuilder();
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();
			if(rs.next())
			{
				for(int t=1; t<= rs.getMetaData().getColumnCount(); t++)
				{
					sbResult.append (rs.getMetaData().getColumnName(t));
					sbResult.append (": ");
					sbResult.append (rs.getObject(t).toString());
					sbResult.append ("\t");
				}
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		
		return sbResult;
	}

	public Integer getIdInconsistenciaValidacao() {
		return idInconsistenciaValidacao;
	}
	
	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}

}
