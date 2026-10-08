/**
 * 
 */
package com.consilux.model.remessa;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Enquadramento;
import com.consilux.model.ExportaRemessa;
import com.consilux.model.Inconsistencia;
import com.consilux.model.InfracaoObliteracao;
import com.consilux.model.ItemExportaRemessaCET;
import com.consilux.model.ItemExportaRemessaCET_VM;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.Remessa;
import com.consilux.model.exception.ModelException;

/**
 * @author Consilux
 * 
 */
public class ExportaRemessaCET extends ExportaRemessa<ItemExportaRemessaCET> {

	protected final static Logger logger = Logger.getLogger(ExportaRemessaCET.class);
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	//private static Map<Integer, String> tipos_apait = EnquadramentoRegraInfracao.TiposAPAIT;

	public ExportaRemessaCET() throws SQLException, ConexaoException,
			ModelException {
	}

	public ExportaRemessaCET(Remessa remessa, boolean comObliteracao)
			throws SQLException, ConexaoException, ModelException {
		super(remessa, comObliteracao, true);
	}

	public static void preparaRemessa(Enquadramento enquadramento, EtapaProcesso etapaProcesso,
			Date dataInicial, Date dataFinal, boolean residual, Inconsistencia inconsistencia)
			throws SQLException, ConexaoException, ModelException {

		PreparedStatement psRemessa = null;
		ResultSet rsRemessa = null;
		Connection conn = null;

		try {
			conn = Conexao.getConexao();

			logger.info("Início da preparação da remessa");
			
			StringBuilder sbSQL = new StringBuilder();	
			
			sbSQL.setLength(0);
			sbSQL.append(" SELECT COUNT(*) AS qtde ");
			sbSQL.append(" FROM   gera_remessa_automatico (NOLOCK) ");
			sbSQL.append(" WHERE  flag_geracao = 0 ");
			
			psRemessa = conn.prepareStatement(sbSQL.toString());
			
			rsRemessa = psRemessa.executeQuery();
			if (!rsRemessa.next() || rsRemessa.getInt(1) > 0) {
				throw new ModelException("Aguarde a conclusão do processo automático para gerar remessas manuais.");
			}

			psRemessa.close();
			
			
			sbSQL.setLength(0);
			sbSQL.append(" SELECT COUNT(i.id_infracao) ");
			sbSQL.append(" FROM   infracao i WITH (NOLOCK) ");
			sbSQL.append("        INNER JOIN veiculo v (NOLOCK) ");
			sbSQL.append("             ON   i.id_veiculo  = v.id_veiculo ");
			sbSQL.append(" 		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
			sbSQL.append(" 			   ON  ir.id_infracao = i.id_infracao ");
			sbSQL.append(" WHERE  i.id_processo = ? ");
			sbSQL.append("	  	  AND i.data BETWEEN ? AND ? ");
			sbSQL.append("	  	  AND i.id_enquadramento = ? ");
			sbSQL.append("	  	  AND ir.id_infracao IS NULL ");
			
			if (inconsistencia != null) {
				logger.info("2 - Adiconado filtro SQL para inconsistencia");
				if (inconsistencia.getIdInconsistencia() == 99)
					sbSQL.append(" AND v.velocidade >= (2 * i.velocidade_limite) ");
				else 
					sbSQL.append(" AND i.id_inconsistencia = ? ");
			}
			
			psRemessa = conn.prepareStatement(sbSQL.toString());
			psRemessa.setInt(1, etapaProcesso.getId());
			psRemessa.setTimestamp(2, new Timestamp(dataInicial.getTime()));
			psRemessa.setTimestamp(3, new Timestamp(dataFinal.getTime()));
			psRemessa.setInt(4, enquadramento.getIdEnquadramento());
			
			if (inconsistencia != null && inconsistencia.getIdInconsistencia() != 99) {
				logger.info("2 - Adiconado paramentro do filtro SQL para inconsistencia");
				psRemessa.setInt(5, inconsistencia.getIdInconsistencia());
			}

			rsRemessa = psRemessa.executeQuery();
			if (!rsRemessa.next() || rsRemessa.getInt(1) == 0) {
				throw new ModelException("Não existem infrações deste processo para os parâmetros informados.");
			}

			psRemessa.close();
			
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	
	public static void preparaRemessaAutomatico(EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal)
			throws SQLException, ConexaoException, ModelException {

		PreparedStatement psRemessa = null;
		ResultSet rsRemessa = null;
		Connection conn = null;

		try {
			conn = Conexao.getConexao();

			StringBuilder sbSQL = new StringBuilder();
			sbSQL.setLength(0);
			sbSQL.append("SELECT COUNT(i.id_infracao) ");
			sbSQL.append("FROM infracao i WITH (NOLOCK) ");
			sbSQL.append("LEFT JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao ");
			sbSQL.append("WHERE i.id_processo = ? ");
			sbSQL.append("AND data < ? ");
			sbSQL.append("AND ir.id_infracao IS NULL ");
			
			psRemessa = conn.prepareStatement(sbSQL.toString());
			psRemessa.setInt(1, etapaProcesso.getId());
			psRemessa.setTimestamp(2, new Timestamp(dataInicial.getTime()));
			
			rsRemessa = psRemessa.executeQuery();
			if (rsRemessa.next() && rsRemessa.getInt(1) > 0) {
				throw new ModelException(
						"Existem ["
								+ rsRemessa.getInt(1)
								+ "] infrações pendentes anteriores a data inicial do agendamento.");
			}
			psRemessa.close();

			
			
			sbSQL.setLength(0);
			sbSQL.append(" SELECT COUNT(i.id_infracao) ");
			sbSQL.append(" FROM   infracao i WITH (NOLOCK) ");
			sbSQL.append(" 		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
			sbSQL.append(" 			   ON  ir.id_infracao = i.id_infracao ");
			sbSQL.append(" WHERE  i.id_processo = ? ");
			sbSQL.append("	  	  AND data BETWEEN ? AND ? ");
			sbSQL.append("	  	  AND ir.id_infracao IS NULL ");
			
			psRemessa = conn.prepareStatement(sbSQL.toString());
			psRemessa.setInt(1, etapaProcesso.getId());
			psRemessa.setTimestamp(2, new Timestamp(dataInicial.getTime()));
			psRemessa.setTimestamp(3, new Timestamp(dataFinal.getTime()));
			
			rsRemessa = psRemessa.executeQuery();
			if (!rsRemessa.next() || rsRemessa.getInt(1) == 0) {
				throw new ModelException("Não existem infrações deste processo para os parâmetros informados.");
			}

			psRemessa.close();

		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	

	@Override
	protected ItemExportaRemessaCET constroiItem(ResultSet rs,
			boolean comBlobImagem) throws SQLException, ModelException,
			IOException {

		final Remessa remessa = getRemessa();

		Integer idInfracao = rs.getInt("id_infracao") == 0 ? null : rs
				.getInt("id_infracao");

		InfracaoObliteracao obliteracao = null;

		boolean com_obliteracao = rs.getBoolean("com_obliteracao");
		
		try {
		if (com_obliteracao) {
			obliteracao = InfracaoObliteracao.buscaInfracaoObliteracaoPorIdInfracao(idInfracao, rs.getInt("id_imagem"));
		}
		} catch(Exception e) {
			logger.error("Erro ao buscar Obliteração!", e);
		}

		Integer sequencia_imagem_local;
		byte[] imagem = null;
		try {
			sequencia_imagem_local = rs.getInt("indice_imagem");
			imagem = rs.getBytes("imagem");
		} catch (SQLException e) {
			sequencia_imagem_local = 0;
		}

		ItemExportaRemessaCET item = new ItemExportaRemessaCET(
				idInfracao,
				rs.getInt("id_imagem_local"), 
				sequencia_imagem_local, 
				imagem,
				rs.getInt("id_inconsistencia"), 
				rs.getInt("inconsistencia_validacao"),
				rs.getInt("auto") == 0 ? null : rs.getInt("auto"), 
				remessa.getCodigoExterno(),
				remessa.getData(), 
				rs.getTimestamp("data_validacao"), //remessa.getDataValidacao(),
				getTipo(),
				remessa.getTipo(),
				rs.getString("serie") != null ? rs.getString("serie").trim() : null, 
				0,//dacAuto, 
				rs.getInt("sequencia"),
				rs.getString("placa") != null ? rs.getString("placa") : null,
				rs.getInt("pais"), 
				rs.getInt("id_marca") == 0 ? null : rs.getInt("id_marca"),
				rs.getInt("id_especie") == 0 ? null : rs.getInt("id_especie"),
				rs.getInt("id_enquadramento") == 0 ? null : rs.getInt("id_enquadramento"),
						
				rs.getInt("cod_pista") == 0 ? 0 : rs.getInt("cod_pista"),
				rs.getString("nome_local") != null ? rs.getString("nome_local") : "", 
				rs.getInt("cod_pista_prodam") == 0 ? 0 : rs.getInt("cod_pista_prodam"),
						
				rs.getTimestamp("data"), 
				rs.getInt("velocidade"),
				rs.getInt("velocidade_considerada"),
				rs.getInt("velocidade_limite"),
				rs.getInt("pista"),
				
				rs.getInt("cod_operador"),
				rs.getInt("cod_agente"),
				
				rs.getTimestamp("data_analise"),
				rs.getInt("id_imagem"), 
				rs.getInt("com_video"),
				obliteracao);
		
		if (item.getIdEnquadramento() == 99999) {
			
			ItemExportaRemessaCET_VM vm = new ItemExportaRemessaCET_VM();
			
			vm.setDescricaoMarca(rs.getString("marca"));
			vm.setDescricaoEspecie(rs.getString("especie"));
			
			vm.setCodigoLocalTrecho(rs.getInt("codigo_prodam_percurso"));
			vm.setDescricaoLocalTrecho(rs.getString("nome_percurso"));
			
			vm.setVelocidadeRegulamentadaTrecho(rs.getInt("velocidade_media_regulamentada"));
			vm.setVelocidadeMediaCalculada(rs.getInt("velocidade_media"));
			vm.setVelocidadeMediaConsiderada(rs.getInt("velocidade_media_considerada"));
			
			vm.setCodigoLocalInicio(rs.getInt("codigo_prodam_origem"));
			vm.setDescricaoLocalInicio(rs.getString("descricao_origem"));
			vm.setDataRegistroInicio(rs.getTimestamp("data_origem"));
			vm.setVelocidadeMedidaInicio(rs.getInt("velocidade_origem"));
			vm.setCodigoEquipamentoInicio(rs.getInt("codigo_equipamento_origem"));
			vm.setSerieEquipamentoInicio(rs.getInt("serie_equipamento_origem"));
			vm.setDataAfericaoInicio(rs.getDate("data_afericao_origem"));
			
			vm.setCodigoLocalFim(rs.getInt("codigo_prodam_destino"));
			vm.setDescricaoLocalFim(rs.getString("descricao_destino"));
			vm.setDataRegistroFim(rs.getTimestamp("data_destino"));
			vm.setVelocidadeMedidaFim(rs.getInt("velocidade_destino"));
			vm.setCodigoEquipamentoFim(rs.getInt("codigo_equipamento_destino"));
			vm.setSerieEquipamentoFim(rs.getInt("serie_equipamento_destino"));
			vm.setDataAfericaoFim(rs.getDate("data_afericao_destino"));
			
			vm.setDescricao(rs.getString("descricao_vm"));
			vm.setNumeroRegistroMontante(rs.getInt("id_veiculo_Local_Montante"));
			
			item.setVelocidadeMedia(vm);
			
		}
		
		return item;
	}

	@Override
	public void validarItem(ItemExportaRemessaCET itemValidar)
			throws ModelException {

		if (itemValidar.getDataRemessa() == null) {
			throw new ModelException("Data da remessa nula!");
		}
//		if (itemValidar.getTipo() == null) {
//			throw new ModelException("Tipo de remessa nulo ou inválido!");
//		}
//		if (!Pattern.matches("^[A-Z][1-9]$", itemValidar.getSerieRemessa())) {
//			throw new ModelException("Série de remessa inválida!");
//		}
//		if (itemValidar.getAuto() > 999999) {
//			throw new ModelException("Número de auto inválido!");
//		}
//		if (itemValidar.getDacAuto() == null || itemValidar.getDacAuto() > 9) {
//			throw new ModelException("DV de auto nulo ou inválido!");
//		}
		if (itemValidar.getIdMarca() == null //|| itemValidar.getIdMarca() > 999
				) {
			throw new ModelException("Identificador da marca nula ou inválida!");
		}
		if (itemValidar.getIdEspecie() == null
				|| itemValidar.getIdEspecie() > 999) {
			throw new ModelException(
					"Identificador da espécie nula ou inválida!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.consilux.model.Remessa#buscarItensRemessa()
	 */
	@Override
	protected ResultSet buscarItensRemessa(boolean comBlobImagem)
			throws SQLException, ConexaoException {

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("	SELECT * FROM ");
		
		if (getRemessa().getIdEnquadramento() == 99999)
			sbSQL.append("		fcn_ObterDadosRemessa_VM(?) ");
		else 
			sbSQL.append("      fcn_ObterDadosRemessa(?) ");
		
		sbSQL.append("	ORDER BY ");
		sbSQL.append("		sequencia ");

		if (connItens == null) {
			connItens = Conexao.getConexao();
			logger.info("Abrindo Conexão de Banco de Dados da classe ExportaRemessaCET.buscarItensRemessa. RM: [" + getRemessa().getIdRemessa() + "]");
		}

		PreparedStatement psItens = connItens
				.prepareStatement(sbSQL.toString());
		psItens.setInt(1, getRemessa().getIdRemessa());
		ResultSet rsItens = psItens.executeQuery();

		return rsItens;
	}

	@Override
	public String getNomeArquivoTXT() {
		String inicial;
		if(getRemessa().getIdEnquadramento() == 99999) {
		if(getTipo() == 0 || getRemessa().getDataValidacao() == null)
			inicial = "XM";
		else
			inicial = "XV";	
		}
		else {
		if(getTipo() == 0 || getRemessa().getDataValidacao() == null)
			inicial = "RM";
		else
			inicial = "LV";
		}
		return String.format("%2s%2s%07d%8s%4s",
				inicial,
				this.getRemessa().getTipo(), 
				//this.getRemessa().getIdRemessa(),
				this.getRemessa().getCodigoExterno(),
				dateFormat.format(this.getRemessa().getData()),
				".TXT");
	}

	@Override
	public String getCabecalhoRemessa() {
		Remessa remessa = getRemessa(); // XXX: Felipe
		Date dataValidacao = remessa.getDataValidacao();
		String cabecalho;
		cabecalho = String.format("%1d%8s%2s%07d%04d%1d",
				1,
				dateFormat.format(remessa.getData()),
				this.getRemessa().getTipo(),
				//remessa.getIdRemessa(),
				remessa.getCodigoExterno(),
				remessa.getTotalInfracao(),
				remessa.getRevisaoBanco());
		if (getTipo() != 0 && dataValidacao != null)
			cabecalho += dateFormat.format(dataValidacao);
		return cabecalho;
	}

	public static Integer calcDACAuto(String tipo, String serie, Integer auto)
			throws ModelException {
		Integer dac = null;

		if (tipo == null || serie == null || auto == null) {
			throw new ModelException("Erro ao calcular o DV do auto.");
		}

		String sAuto = String.format("%06d", auto);
		String tipoSerie = converteTextoTipoSerie(tipo.trim() + serie.trim());
		String autoCompleto = tipoSerie + sAuto;

		Integer fator = 5;
		Integer soma = 0;
		for (Character c : autoCompleto.toCharArray()) {
			Integer mult = Integer.valueOf(String.valueOf(c)) * fator--;
			soma += mult;
			if (fator == 0)
				fator = 10;
		}

		dac = 11 - (soma % 11);

		if (dac > 9)
			dac = 0;

		return dac;
	}

	public static String converteTextoTipoSerie(String tipoSerie)
			throws ModelException {

		StringBuilder sbRet = new StringBuilder();

		for (Character c : tipoSerie.toCharArray()) {
			if (Character.getType(c) == Character.UPPERCASE_LETTER
					|| Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER) {
				sbRet.append(String.format("%02d", Character.getNumericValue(c)));
			} else {
				throw new ModelException("Caractere inválido: " + c);
			}
		}
		return sbRet.toString();
	}

	@Override
	public void fecharConexaoItens() throws SQLException {
		if (connItens != null) {
			connItens.close();
		}
	}

	@Override
	public String getNomeArquivoZip() {
		return getNomeArquivoTXT().replace(".TXT", ".ZIP");
	}
}
