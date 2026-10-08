//package com.consilux.model.remessa;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.Date;
//
//import com.consilux.infra.ExpValida;
//import com.consilux.infra.exception.ConexaoException;
//import com.consilux.lib.Conexao;
//import com.consilux.model.Enquadramento;
//import com.consilux.model.ExportaRemessa;
//import com.consilux.model.ItemExportaRemessaURBS;
//import com.consilux.model.ModeloInstrumento;
//import com.consilux.model.Remessa;
//import com.consilux.model.Processamento.EtapaProcesso;
//import com.consilux.model.exception.ModelException;
//
//public class ExportaRemessaURBS extends ExportaRemessa<ItemExportaRemessaURBS> {
//	
//	public ExportaRemessaURBS() {
//		// Construtor sem parâmetros, pois pode ser criada uma instância
//		// desta classe via reflections.
//		super(true);
//	}
//	
//	public ExportaRemessaURBS(Remessa remessa, boolean comObliteracao) throws SQLException, ConexaoException, ModelException {
//		super(remessa, comObliteracao, true);
//	}
//	
//	public static void preparaRemessa(EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal, boolean residual)
//	throws SQLException, ConexaoException, ModelException {
//		
//		//Verificando se a remessa já foi gerada:
//		if (residual)
//			return;
//
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		Connection conn = null;
//		
//		try {
//			conn = Conexao.getConexao();
//			
//			StringBuilder sbSQL = new StringBuilder();
//			sbSQL.append("SELECT id_remessa ");
//			sbSQL.append("FROM remessa WITH (NOLOCK) ");
//			sbSQL.append("WHERE id_processo = ? AND ");
//			sbSQL.append("(? BETWEEN data_inicial AND data_final OR ");
//			sbSQL.append("? BETWEEN data_inicial AND data_final)");
//			
//			ps = conn.prepareStatement(sbSQL.toString());
//			ps.setInt(1, etapaProcesso.getId());
//			ps.setTimestamp(2, new Timestamp(dataInicial.getTime()));
//			ps.setTimestamp(3, new Timestamp(dataFinal.getTime()));
//				
//			rs = ps.executeQuery();
//			if (rs.next())
//				throw new ModelException("Remessa já foi gerada para este período.");
//
//			sbSQL.setLength(0);
//			sbSQL.append("SELECT ");
//			sbSQL.append("	COUNT(i.id_infracao) ");
//			sbSQL.append("FROM ");
//			sbSQL.append("	remessa r WITH (NOLOCK) ");
//			sbSQL.append("	JOIN infracao_remessa ir WITH (NOLOCK) ");
//			sbSQL.append("		ON ir.id_remessa = r.id_remessa ");
//			sbSQL.append("	LEFT JOIN infracao i WITH (NOLOCK) ");
//			sbSQL.append("		ON i.id_infracao = ir.id_infracao ");
//			sbSQL.append("		AND i.data BETWEEN r.data_inicial AND r.data_final ");
//			sbSQL.append("WHERE ");
//			sbSQL.append("	r.id_processo = ? ");
//			sbSQL.append("	AND i.id_infracao IS NULL ");
//			sbSQL.append("	AND (? BETWEEN r.data_inicial AND r.data_final	 ");
//			sbSQL.append("	OR ? BETWEEN r.data_inicial AND r.data_final) ");
//			
//			ps = conn.prepareStatement(sbSQL.toString());
//			
//			ps.setInt(1, etapaProcesso.getId());
//			ps.setTimestamp(2, new Timestamp(dataInicial.getTime()));
//			ps.setTimestamp(3, new Timestamp(dataFinal.getTime()));
//			
//			rs = ps.executeQuery();
//			if (!rs.next()) {
//				int nRemessas = rs.getInt(1);
//				if (nRemessas == 0) {
//					throw new ModelException("No período especificado, não existem infrações deste processo a remeter.");
//				}
//			}
//			
//			ps.close();
//			
//			sbSQL.setLength(0);
//			sbSQL.append("SELECT COUNT(id_infracao) ");
//			sbSQL.append("FROM infracao i WITH (NOLOCK) ");
//			sbSQL.append("WHERE i.id_enquadramento IN (SELECT id_enquadramento FROM infracao where id_processo = ?) AND ");
//			sbSQL.append("	  data < ? AND i.id_inconsistencia = 0 AND");
//			sbSQL.append("	  NOT EXISTS (SELECT id_infracao FROM infracao_remessa WHERE id_infracao = i.id_infracao) ");
//			
//			ps = conn.prepareStatement(sbSQL.toString()); 
//			ps.setInt(1, etapaProcesso.getId());
//			ps.setTimestamp(2, new Timestamp(dataInicial.getTime()));
//			
//			rs = ps.executeQuery();
//			if (rs.next() && rs.getInt(1) > 0) {
//				throw new ModelException("Existem ["+rs.getInt(1)+"] infrações pendentes anteriores a data inicial da remessa.");
//			}
//			
//		} 
//		finally {
//			if (conn != null)
//				conn.close();							
//		}
//	}
//	
//	@Override
//	protected ItemExportaRemessaURBS constroiItem(ResultSet rs, boolean comBlobImagem)
//	throws SQLException, ModelException, IOException {
//
//		Integer codAgente = rs.getInt("cod_agente");
//		if (rs.wasNull())
//		{
//			codAgente = null;
//		}
//		
//		String ufAgente = rs.getString("uf_agente");
//		
//		ItemExportaRemessaURBS item = new ItemExportaRemessaURBS (
//				rs.getInt("id_infracao"),
//				rs.getInt("auto"),
//				rs.getString("tipo").trim(),
//				rs.getString("serie").trim(),
//				rs.getString("sigla_infracao_cliente"),
//				rs.getString("placa").trim(),
//				rs.getString("uf_placa"),
//				rs.getInt("id_marca"),
//				new java.util.Date(rs.getTimestamp("data_infracao").getTime()),
//				rs.getString("nome_local"),
//				rs.getInt("id_enquadramento"),
//				rs.getInt("parametro_limite"), 
//				rs.getInt("parametro_registrado"),
//				rs.getInt("serie_equipamento"),
//				codAgente,
//				ufAgente,
//				ModeloInstrumento.FIXO,
//				new java.util.Date(rs.getDate("data_afericao").getTime()),
//				rs.getInt("id_imagem"),
//				new java.util.Date(rs.getDate("data_validade_afericao").getTime()));
//				
//		return item;
//	}
//
//	@Override
//	public void validarItem(ItemExportaRemessaURBS itemValidar) throws ModelException {
//
//		// Validações específicas da URBS.
//		
//		String sCodAgente = Integer.toString(itemValidar.getCodAgente());
//		if (sCodAgente.length() > 10)
//			throw new ModelException("Código do agente não pode exceder 10 caracteres.");		
//		
//		if (Enquadramento.isEnquadramentoMetrologico(itemValidar.getIdEnquadramento()) && (itemValidar.getDataValidadeAfericao() == null
//			|| itemValidar.getDataValidadeAfericao().before(itemValidar.getDataInfracao()))) 
//			throw new ModelException ("Para infrações metrológicas, a data de vencimento aferição do equipamento não pode ser nula e ser anterior a data da infração.");
//		
//		if (itemValidar.getNomeLocal() != null && itemValidar.getNomeLocal().length() > 30)
//			throw new ModelException ("A descrição do local não pode exceder 30 caracteres");
//		
//		if (itemValidar.getSerieEquipamento() == 0)
//			throw new ModelException ("O número de série do instrumento não pode ser zero.");
//		
//		if (itemValidar.getPlaca() == null || itemValidar.getPlaca().length() == 0)
//			throw new ModelException ("A placa não pode ser nula e nem vazia.");
//
//		if (itemValidar.getSiglaInfracaoCliente() == null || itemValidar.getSiglaInfracaoCliente().length() != 3)
//			throw new ModelException ("A sigla de infração do cliente deve possuir 3 caracteres.");
//
//		if (itemValidar.getUfAgente() == null || itemValidar.getUfAgente().length() != 2)
//			throw new ModelException ("A UF do agente não pode nula e deve ter exatamente 2 caracteres.");
//
//		if (itemValidar.getUfAgente() != null && !ExpValida.UF.validar(itemValidar.getUfAgente()))
//			throw new ModelException ("A UF do agente não é um estado brasileiro válido");
//		
//		if (itemValidar.getUfPlaca() == null || itemValidar.getUfPlaca().length() != 2)
//			throw new ModelException ("A UF do veículo não pode nula e deve ter exatamente 2 caracteres.");
//
//		if (itemValidar.getUfPlaca() != null && !ExpValida.UF.validar(itemValidar.getUfPlaca()))
//			throw new ModelException ("A UF do veículo não é um estado brasileiro válido");
//		
//		// Se a UF (da placa) for 'PR', o id_marca não pode ser zero (VER MELHOR)
// 
//	}	
//	
//	@Override
//	protected ResultSet buscarItensRemessa(boolean comBlobImagem) throws SQLException, ConexaoException {
//		
//		PreparedStatement ps = null;
//		StringBuilder sbSQL = new StringBuilder();
//
//		sbSQL.append("SELECT ");
//		sbSQL.append("	ir.id_infracao, ");
//		sbSQL.append("	ir.serie, ");
//		sbSQL.append("	ir.auto, ");
//		sbSQL.append("	inf.placa, ");
//		sbSQL.append("	COALESCE(cal.uf, cup.uf) AS uf_placa, ");
//		sbSQL.append("	COALESCE(cav.id_marca, 0) AS id_marca, ");
//		sbSQL.append("	inf.data AS data_infracao, ");
//		sbSQL.append("	cep.nome_pista AS nome_local, ");
//		sbSQL.append("	inf.id_enquadramento, ");
//		
//		// Parâmetro Limite "Limite Permitido"
//		sbSQL.append("	parametro_limite = CASE enq.tipo_info_especifica ");
//		sbSQL.append("		WHEN 'V' THEN inf.velocidade_limite ");
//		sbSQL.append("		ELSE 0 ");
//		sbSQL.append("	END, ");
//		
//		// Parâmetro Registrado "Valor Aferido"
//		sbSQL.append("	parametro_registrado = CASE enq.tipo_info_especifica ");
//		sbSQL.append("		WHEN 'V' THEN CAST(ROUND(vei.velocidade, 0, 1) AS INT)  ");
//		sbSQL.append("		WHEN 'S' THEN CAST(ROUND(100 * vei.segundos, 0, 1) AS INT) ");
//		// Confirmar isso com o fds e com o m.souza depois: segundos para a parada faixa
//		sbSQL.append("		WHEN 'P' THEN CAST(ROUND(100 * vei.segundos, 0, 1) AS INT) ");
//		sbSQL.append("		ELSE 0 ");
//		
//		sbSQL.append("	END, ");
//		sbSQL.append("	COALESCE(ir.sigla_infracao_cliente, '   ') AS sigla_infracao_cliente, ");
//		sbSQL.append("	cfe.serie_equipamento, ");
//		sbSQL.append("	usu.cod_agente, ");
//		sbSQL.append("	usu.uf_agente, ");
//		sbSQL.append("	CAST(afe.data AS DATE) data_afericao, ");
//		sbSQL.append("	CAST(afe.data_validade AS DATE) data_validade_afericao, ");
//		sbSQL.append("	iim.id_imagem_obj AS id_imagem, ");
//		sbSQL.append("	COALESCE(obl.x, 0) AS x, ");
//		sbSQL.append("	COALESCE(obl.y, 0) AS y, ");
//		sbSQL.append("	COALESCE(obl.altura, 0) AS altura, ");
//		sbSQL.append("	COALESCE(obl.largura, 0) AS largura, ");
//		sbSQL.append("	COALESCE(lcl.id_localidade, 0) AS id_localidade, ");
//		sbSQL.append("	RTRIM(r.tipo) AS tipo, ");
//		sbSQL.append("	ir.serie ");
//		
////		sbSQL.append("	CASE WHEN img.imagem IS NOT NULL AND DATALENGTH(img.imagem) ");
////		sbSQL.append("  > 0 THEN CAST(1 AS BIT) ELSE CAST (0 AS BIT) END AS com_imagem ");
//		
//		sbSQL.append(" FROM    ");
//		sbSQL.append("	remessa r WITH (NOLOCK) ");
//		sbSQL.append("	JOIN infracao_remessa ir WITH (NOLOCK) ");
//		sbSQL.append("		ON r.id_remessa = ir.id_remessa ");
//		sbSQL.append("	JOIN infracao inf WITH (NOLOCK) ");
//		sbSQL.append("		ON inf.id_infracao = ir.id_infracao ");
//		sbSQL.append("	JOIN veiculo vei WITH (NOLOCK) ");
//		sbSQL.append("		ON vei.id_veiculo = inf.id_veiculo ");
//		sbSQL.append("	JOIN local lcl WITH (NOLOCK) ");
//		sbSQL.append("		ON lcl.id_local = inf.id_local ");
//		sbSQL.append("		AND lcl.sequencia_local = inf.sequencia_local ");
//		sbSQL.append("	JOIN enquadramento enq WITH (NOLOCK) ");
//		sbSQL.append("		ON inf.id_enquadramento = enq.id_enquadramento ");
//		sbSQL.append("	JOIN configuracao_equipamento cfe WITH (NOLOCK) ");
//		sbSQL.append("		ON cfe.id_configuracao_equipamento = lcl.id_configuracao_equipamento ");
//		sbSQL.append("	JOIN configuracao_equipamento_pista cep WITH (NOLOCK) ");
//		sbSQL.append("		ON cep.id_configuracao_equipamento = cfe.id_configuracao_equipamento ");
//		sbSQL.append("		AND cep.id_pista = inf.pista ");
//		sbSQL.append("	JOIN configuracao_equipamento_afericao afe WITH (NOLOCK) ");
//		sbSQL.append("		ON afe.id_configuracao_equipamento = cfe.id_configuracao_equipamento ");
//		sbSQL.append("		AND afe.id_pista = inf.pista ");
//
//		sbSQL.append("	JOIN infracao_imagem iim WITH (NOLOCK) ");
//		sbSQL.append("		ON iim.id_infracao = ir.id_infracao ");
//		
//		if (comBlobImagem)
//		{
//			sbSQL.append("	JOIN imagem img WITH (NOLOCK) ");
//			sbSQL.append("		ON img.id_imagem = iim.id_imagem_obj ");
//		}
//		
//		sbSQL.append("	LEFT JOIN infracao_obliteracao obl WITH (NOLOCK) ");
//		sbSQL.append("		ON obl.id_infracao = ir.id_infracao ");
//		sbSQL.append("	LEFT JOIN sis_usuario usu WITH (NOLOCK) ");
//		sbSQL.append("		ON usu.id_usuario = inf.id_usuario_final ");
//		sbSQL.append("	LEFT JOIN cad_veiculo cav WITH (NOLOCK) ");
//		sbSQL.append("		ON cav.placa = inf.placa ");
//		sbSQL.append("	LEFT JOIN cad_localidade cal WITH (NOLOCK) ");
//		sbSQL.append("		ON cal.id_localidade = cav.id_localidade ");
//		sbSQL.append("	LEFT JOIN cad_uf_processo cup WITH (NOLOCK) ");
//		sbSQL.append("		ON cup.placa = inf.placa ");
//		sbSQL.append(" WHERE ");
//		sbSQL.append("	ir.id_remessa = ? ");
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append("	ir.auto ");
//		
//		if (connItens == null)
//			connItens = Conexao.getConexao();
//			
//		ps = connItens.prepareStatement(sbSQL.toString());
//		ps.setInt(1, getRemessa().getIdRemessa());
//		
//		return ps.executeQuery();
//		
//	}
//
//	@Override
//	public String getNomeArquivoTXT() {
//		return String.format("%-3s%05d.txt",
//			getRemessa().getTipo(),
//			getRemessa().getCodigoExterno());
//	}
//
//	@Override
//	public String getCabecalhoRemessa() {
//		return null;
//	}
//	
//	@Override
//	public void fecharConexaoItens() throws SQLException {
//		if (connItens != null) {
//			connItens.close();
//			connItens = null;
//		}
//	}
//	
//	@Override
//	public String getNomeArquivoZip() {
//		return String.format("%-3s%05d.zip",
//			getRemessa().getTipo(),
//			getRemessa().getCodigoExterno());
//	}
//	
//}
