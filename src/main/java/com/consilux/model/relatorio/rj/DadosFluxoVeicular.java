/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 28/09/2016

*********************************************************************************/

package com.consilux.model.relatorio.rj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.CSVUtils;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 28/09/2016
 */
public class DadosFluxoVeicular {
	
	/**
	 * Classe de negócio para busca de dados para o relatório de fluxo veicular por hora.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 03/01/2019
	 */
	public ArrayList<ItemFluxoVeicular> relatorioFluxoVeicularPorHora(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorioFluxoVeicularPorHora(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxo;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxo = new Integer[31];
				
				Integer valorFluxo = 0;
				
				for (int i = 1; i <= 31; i++) {
					valorFluxo = (int) rs.getInt(String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxo,
											  rs.getInt("total_hora"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 01 - Cada endereço mensal por data e horário - fluxo, velocidade e autos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 06/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio1FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio1MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxo;
		Integer[] celulasVelMedia;
		Integer[] celulasVelMax;
		Integer[] celulasAutosDetectados;
		Integer[] celulasAutosValidos;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxo = new Integer[31];
				celulasVelMedia = new Integer[31];
				celulasVelMax = new Integer[31];
				celulasAutosDetectados = new Integer[31];
				celulasAutosValidos = new Integer[31];
				
				Integer valorFluxo = 0;
				Integer valorVelMedia = 0;
				Integer valorVelMax = 0;
				Integer valorAutosDetectados = 0;
				Integer valorAutosValidos = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxo = (int) rs.getInt("fv_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
					valorVelMedia = (int) rs.getInt("vm_" + String.valueOf(i));
					celulasVelMedia[i-1] = Integer.valueOf(valorVelMedia);
					
					valorVelMax = (int) rs.getInt("vmax_" + String.valueOf(i));
					celulasVelMax[i-1] = Integer.valueOf(valorVelMax);
					
					valorAutosDetectados = (int) rs.getInt("ad_" + String.valueOf(i));
					celulasAutosDetectados[i-1] = Integer.valueOf(valorAutosDetectados);
					
					valorAutosValidos = (int) rs.getInt("av_" + String.valueOf(i));
					celulasAutosValidos[i-1] = Integer.valueOf(valorAutosValidos);
					
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxo,
											  celulasVelMedia,
											  celulasVelMax,
											  celulasAutosDetectados,
											  celulasAutosValidos,
											  rs.getInt("total_fluxo"),
											  rs.getInt("total_velocidade_media"),
											  rs.getInt("total_velocidade_maxima"),
											  rs.getInt("total_autos_detectados"),
											  rs.getInt("total_autos_validos")
											);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 02 - Cada endereço mensal por data - fluxo, velocidade e autos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/09/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio2FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio2MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.dia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()) {
				
				itens = new ItemFluxoVeicular(rs.getString("dia"),
											  rs.getInt("dia_semana"),
											  rs.getString("dia_semana_desc"),
											  rs.getInt("fluxo_veicular"),
											  rs.getInt("velocidade_media"),
											  rs.getInt("velocidade_maxima"),
											  rs.getInt("autos_detectados"),
											  rs.getInt("autos_validos")
										);
				itens.setAutosInvalidosMotivoTecnico(rs.getInt("invalidas_tecnicos"));
				itens.setAutosInvalidosMotivoNaoTecnico(rs.getInt("invalidas_nao_tecnicos"));
				itens.setAutosInvalidos(itens.getAutosInvalidosMotivoNaoTecnico()+itens.getAutosInvalidosMotivoTecnico());
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 03 - Todos os endereços mensal por data - volume veicular.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 07/11/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio3FluxoVeicular(Integer intMes, Integer intAno, boolean faixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		if (faixa)
		sbSQL.append(" EXEC dbo.spu_getRelatorio3MedicaoFluxoVeicularPista @Data_Ini, @Data_Fim ");
		else
		sbSQL.append(" EXEC dbo.spu_getRelatorio3MedicaoFluxoVeicular @Data_Ini, @Data_Fim ");	
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		String[] celulasColunasRelatorio;
		Integer[] celulasValorColuna;
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			Integer columnCount = rsmd.getColumnCount();
			
			celulasColunasRelatorio = new String[columnCount];
			String colunaRelatorio = "";
			
			for (int i = 1; i <= columnCount; i++ ) {
			  colunaRelatorio = rsmd.getColumnName(i);
			  celulasColunasRelatorio[i-1] = colunaRelatorio;
			}
			

			while (rs.next()){
				
				celulasValorColuna = new Integer[columnCount];
				
				for (int h = 1; h <= columnCount; h++ ) {
					celulasValorColuna[h-1] = (h > 3 ? rs.getInt(h) : 0);
				}
				
				itens = new ItemFluxoVeicular(rs.getString("Data"),
													 rs.getInt("dia_semana"),
													 rs.getString("dia_semana_desc"),
													 celulasColunasRelatorio,
													 celulasValorColuna,
													 columnCount
													);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 04 - Cada endereço mensal por data - volume veicular por porte veicular.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 01/11/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio4FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio4MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxoMoto;
		Integer[] celulasFluxoPequeno;
		Integer[] celulasFluxoMedio;
		Integer[] celulasFluxoGrande;
		Integer[] celulasFluxoSemId;
		Integer[] celulasFluxo;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxoMoto = new Integer[31];
				celulasFluxoPequeno = new Integer[31];
				celulasFluxoMedio = new Integer[31];
				celulasFluxoGrande = new Integer[31];
				celulasFluxoSemId = new Integer[31];
				celulasFluxo = new Integer[31];
				
				Integer valorFluxoMoto = 0;
				Integer valorFluxoPequeno = 0;
				Integer valorFluxoMedio = 0;
				Integer valorFluxoGrande = 0;
				Integer valorFluxoSemId = 0;
				Integer valorFluxo = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxoMoto = (int) rs.getInt("moto_" + String.valueOf(i));
					celulasFluxoMoto[i-1] = Integer.valueOf(valorFluxoMoto);
					
					valorFluxoPequeno = (int) rs.getInt("pequeno_" + String.valueOf(i));
					celulasFluxoPequeno[i-1] = Integer.valueOf(valorFluxoPequeno);
					
					valorFluxoMedio = (int) rs.getInt("medio_" + String.valueOf(i));
					celulasFluxoMedio[i-1] = Integer.valueOf(valorFluxoMedio);
					
					valorFluxoGrande = (int) rs.getInt("grande_" + String.valueOf(i));
					celulasFluxoGrande[i-1] = Integer.valueOf(valorFluxoGrande);
					
					valorFluxoSemId = (int) rs.getInt("sem_id_" + String.valueOf(i));
					celulasFluxoSemId[i-1] = Integer.valueOf(valorFluxoSemId);
					
					valorFluxo = (int) rs.getInt("total_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxoMoto,
											  celulasFluxoPequeno,
											  celulasFluxoMedio,
											  celulasFluxoGrande,
											  celulasFluxoSemId,
											  celulasFluxo,
											  rs.getInt("total_fluxo"),
											  rs.getInt("total_moto"),
											  rs.getInt("total_pequeno"),
											  rs.getInt("total_medio"),
											  rs.getInt("total_grande"),
											  rs.getInt("total_sem_id")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}

	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 05 - Cada endereço mensal por data e hora - volume veicular.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/09/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio5FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio5MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulas = new Integer[31];
				Integer valor = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0.0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulas,
											  rs.getInt("total_hora")
											);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 06 - Cada endereço (por pista-sentido) mensal por data e hora - velocidade veicular média.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/09/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio6FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista)
		throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio6MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulas = new Integer[31];
				Integer valor = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0.0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulas
											);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 07 - Cada endereço (por pista-sentido) mensal por data e horário - volume veicular e autos (avanço, parada e velocidade).
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 10/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio7FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio7MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxo;
		Integer[] celulasRegistroOCR;
		Integer[] celulasAutosDetectadosEnq56732;
		Integer[] celulasAutosDetectadosEnq60503;
		Integer[] celulasAutosDetectadosEnq74550;
		Integer[] celulasAutosDetectadosEnq74630;
		Integer[] celulasAutosDetectadosEnq74710;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxo = new Integer[31];
				celulasRegistroOCR = new Integer[31];
				celulasAutosDetectadosEnq56732 = new Integer[31];
				celulasAutosDetectadosEnq60503 = new Integer[31];
				celulasAutosDetectadosEnq74550 = new Integer[31];
				celulasAutosDetectadosEnq74630 = new Integer[31];
				celulasAutosDetectadosEnq74710 = new Integer[31];
				
				Integer valorFluxo = 0;
				Integer valorRegistroOCR = 0;
				Integer valorAutosDetectadosEnq56732 = 0;
				Integer valorAutosDetectadosEnq60503 = 0;
				Integer valorAutosDetectadosEnq74550 = 0;
				Integer valorAutosDetectadosEnq74630 = 0;
				Integer valorAutosDetectadosEnq74710 = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxo = (int) rs.getInt("fv_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					valorRegistroOCR = (int) rs.getInt("ocr_" + String.valueOf(i));
					celulasRegistroOCR[i-1] = Integer.valueOf(valorRegistroOCR);
					valorAutosDetectadosEnq56732 = (int) rs.getInt("ad_56732_" + String.valueOf(i));
					celulasAutosDetectadosEnq56732[i-1] = Integer.valueOf(valorAutosDetectadosEnq56732);
					valorAutosDetectadosEnq60503 = (int) rs.getInt("ad_60503_" + String.valueOf(i));
					celulasAutosDetectadosEnq60503[i-1] = Integer.valueOf(valorAutosDetectadosEnq60503);
					valorAutosDetectadosEnq74550 = (int) rs.getInt("ad_74550_" + String.valueOf(i));
					celulasAutosDetectadosEnq74550[i-1] = Integer.valueOf(valorAutosDetectadosEnq74550);
					valorAutosDetectadosEnq74630 = (int) rs.getInt("ad_74630_" + String.valueOf(i));
					celulasAutosDetectadosEnq74630[i-1] = Integer.valueOf(valorAutosDetectadosEnq74630);
					valorAutosDetectadosEnq74710 = (int) rs.getInt("ad_74710_" + String.valueOf(i));
					celulasAutosDetectadosEnq74710[i-1] = Integer.valueOf(valorAutosDetectadosEnq74710);
					
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxo,
											  celulasRegistroOCR,
											  celulasAutosDetectadosEnq56732,
						  					  celulasAutosDetectadosEnq60503,
						  					  celulasAutosDetectadosEnq74550,
						  					  celulasAutosDetectadosEnq74630,
						  					  celulasAutosDetectadosEnq74710,
						  					  rs.getInt("total_fluxo"),
						  					  rs.getInt("total_ocr"),
						  					  rs.getInt("total_ad_56732"),
						  					  rs.getInt("total_ad_60503"),
						  					  rs.getInt("total_ad_74550"),
											  rs.getInt("total_ad_74630"),
											  rs.getInt("total_ad_74710")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 08 - Cada endereço mensal por hora - volume veicular por dia da semana.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 03/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio8FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio8MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  rs.getInt("segunda"),
											  rs.getInt("terca"),
											  rs.getInt("quarta"),
											  rs.getInt("quinta"),
											  rs.getInt("sexta"),
											  rs.getInt("sabado"),
											  rs.getInt("domingo")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
		
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 9 - Cada endereço diário por hora - perfil de velocidade.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 06/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio9FluxoVeicular(Date dtDia, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Data		DATE = ? ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio9MedicaoFluxoVeicular(@Data, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(1, new java.sql.Date(dtDia.getTime()));
			ps.setInt(2, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(3, intIdPista);
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulas = new Integer[42];
				
				for (int i = 1; i <= 42; i++) {
					
					Integer valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulas
				);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 10 - Todos os endereços mensal por datas - fluxo, velocidade e autos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 07/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio10FluxoVeicular_PorLocal(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio10MedicaoFluxoVeicular_PorLocal(@Data_Ini, @Data_Fim) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.id_local ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxo;
		Integer[] celulasVelMedia;
		Integer[] celulasVelMax;
		Integer[] celulasAutosDetectados;
		Integer[] celulasAutosValidos;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxo = new Integer[31];
				celulasVelMedia = new Integer[31];
				celulasVelMax = new Integer[31];
				celulasAutosDetectados = new Integer[31];
				celulasAutosValidos = new Integer[31];
				
				Integer valorFluxo = 0;
				Integer valorVelMedia = 0;
				Integer valorVelMax = 0;
				Integer valorAutosDetectados = 0;
				Integer valorAutosValidos = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxo = (int) rs.getInt("fv_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
					valorVelMedia = (int) rs.getInt("vm_" + String.valueOf(i));
					celulasVelMedia[i-1] = Integer.valueOf(valorVelMedia);
					
					valorVelMax = (int) rs.getInt("vmax_" + String.valueOf(i));
					celulasVelMax[i-1] = Integer.valueOf(valorVelMax);
					
					valorAutosDetectados = (int) rs.getInt("ad_" + String.valueOf(i));
					celulasAutosDetectados[i-1] = Integer.valueOf(valorAutosDetectados);
					
					valorAutosValidos = (int) rs.getInt("av_" + String.valueOf(i));
					celulasAutosValidos[i-1] = Integer.valueOf(valorAutosValidos);

				}
				
				itens = new ItemFluxoVeicular(rs.getInt("id_local"),
											  rs.getLong("serie_equipamento"),
											  rs.getString("nome_pista_sentido"),
											  rs.getDouble("latitude"),
											  rs.getDouble("longitude"),
											  rs.getString("codigos_equipamentos"),
											  rs.getString("velocidade_permitida"),
											  celulasFluxo,
											  celulasVelMedia,
											  celulasVelMax,
											  celulasAutosDetectados,
											  celulasAutosValidos,
											  rs.getInt("total_fluxo"),
											  rs.getInt("total_velocidade_media"),
											  rs.getInt("total_velocidade_maxima"),
											  rs.getInt("total_autos_detectados"),
											  rs.getInt("total_autos_validos"),
											  rs.getInt("total_autos_invalidos_tecnicos"),
											  rs.getInt("total_autos_invalidos_nao_tecnicos")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 10 - Todos os endereços mensal por datas - fluxo, velocidade e autos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 07/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio10FluxoVeicular_PorFaixa(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio10MedicaoFluxoVeicular_PorFaixa(@Data_Ini, @Data_Fim) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.id_local, ");
		sbSQL.append(" 	 	  rel.faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxo;
		Integer[] celulasVelMedia;
		Integer[] celulasVelMax;
		Integer[] celulasAutosDetectados;
		Integer[] celulasAutosValidos;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxo = new Integer[31];
				celulasVelMedia = new Integer[31];
				celulasVelMax = new Integer[31];
				celulasAutosDetectados = new Integer[31];
				celulasAutosValidos = new Integer[31];
				
				Integer valorFluxo = 0;
				Integer valorVelMedia = 0;
				Integer valorVelMax = 0;
				Integer valorAutosDetectados = 0;
				Integer valorAutosValidos = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxo = (int) rs.getInt("fv_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
					valorVelMedia = (int) rs.getInt("vm_" + String.valueOf(i));
					celulasVelMedia[i-1] = Integer.valueOf(valorVelMedia);
					
					valorVelMax = (int) rs.getInt("vmax_" + String.valueOf(i));
					celulasVelMax[i-1] = Integer.valueOf(valorVelMax);
					
					valorAutosDetectados = (int) rs.getInt("ad_" + String.valueOf(i));
					celulasAutosDetectados[i-1] = Integer.valueOf(valorAutosDetectados);
					
					valorAutosValidos = (int) rs.getInt("av_" + String.valueOf(i));
					celulasAutosValidos[i-1] = Integer.valueOf(valorAutosValidos);

				}
				
				itens = new ItemFluxoVeicular(rs.getInt("id_local"),
											  rs.getLong("serie_equipamento"),
											  rs.getString("nome_pista_sentido_faixa"),
											  rs.getDouble("latitude"),
											  rs.getDouble("longitude"),
											  rs.getString("codigo_equipamento"),
											  rs.getInt("velocidade_permitida"),
											  celulasFluxo,
											  celulasVelMedia,
											  celulasVelMax,
											  celulasAutosDetectados,
											  celulasAutosValidos,
											  rs.getInt("total_fluxo"),
											  rs.getInt("total_velocidade_media"),
											  rs.getInt("total_velocidade_maxima"),
											  rs.getInt("total_autos_detectados"),
											  rs.getInt("total_autos_validos"),
											  rs.getInt("total_autos_invalidos_tecnicos"),
											  rs.getInt("total_autos_invalidos_nao_tecnicos")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 13 - Todos os endeços (por pista-sentido) mensal - volume veicualar e autos (avanço, parada e velocidade).
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio11FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio11MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasAutosValidosEnq56732;
		Integer[] celulasAutosValidosEnq60503;
		Integer[] celulasAutosValidosEnq74550;
		Integer[] celulasAutosValidosEnq74630;
		Integer[] celulasAutosValidosEnq74710;
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasAutosValidosEnq56732 = new Integer[31];
				celulasAutosValidosEnq60503 = new Integer[31];
				celulasAutosValidosEnq74550 = new Integer[31];
				celulasAutosValidosEnq74630 = new Integer[31];
				celulasAutosValidosEnq74710 = new Integer[31];
				
				Integer valorAutosValidosEnq56732 = 0;
				Integer valorAutosValidosEnq60503 = 0;
				Integer valorAutosValidosEnq74550 = 0;
				Integer valorAutosValidosEnq74630 = 0;
				Integer valorAutosValidosEnq74710 = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorAutosValidosEnq56732 = (int) rs.getInt("av_56732_" + String.valueOf(i));
					celulasAutosValidosEnq56732[i-1] = Integer.valueOf(valorAutosValidosEnq56732);
					valorAutosValidosEnq60503 = (int) rs.getInt("av_60503_" + String.valueOf(i));
					celulasAutosValidosEnq60503[i-1] = Integer.valueOf(valorAutosValidosEnq60503);
					valorAutosValidosEnq74550 = (int) rs.getInt("av_74550_" + String.valueOf(i));
					celulasAutosValidosEnq74550[i-1] = Integer.valueOf(valorAutosValidosEnq74550);
					valorAutosValidosEnq74630 = (int) rs.getInt("av_74630_" + String.valueOf(i));
					celulasAutosValidosEnq74630[i-1] = Integer.valueOf(valorAutosValidosEnq74630);
					valorAutosValidosEnq74710 = (int) rs.getInt("av_74710_" + String.valueOf(i));
					celulasAutosValidosEnq74710[i-1] = Integer.valueOf(valorAutosValidosEnq74710);
					
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
						  					  rs.getString("hora_desc"),
						  					  celulasAutosValidosEnq56732,
						  					  celulasAutosValidosEnq60503,
						  					  celulasAutosValidosEnq74550,
						  					  celulasAutosValidosEnq74630,
						  					  celulasAutosValidosEnq74710,
						  					  rs.getInt("total_av_56732"),
						  					  rs.getInt("total_av_60503"),
						  					  rs.getInt("total_av_74550"),
											  rs.getInt("total_av_74630"),
											  rs.getInt("total_av_74710"),true
				);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 13 - Todos os endeços (por pista-sentido) mensal - volume veicualar e autos (avanço, parada e velocidade).
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio12FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio12MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.dia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				itens = new ItemFluxoVeicular(rs.getString("dia"),
											  rs.getInt("dia_semana"),
											  rs.getString("dia_semana_desc"),
	  					  					  rs.getInt("fluxo_veicular"),
	  					  					  rs.getInt("validos_ocr"),
	  					  					  rs.getInt("total_detectados"),
	  					  					  rs.getInt("ad_56732"),rs.getInt("ad_60503"),rs.getInt("ad_74550"),
	  					  					  rs.getInt("ad_74630"),rs.getInt("ad_74710"),
	  					  					  rs.getInt("total_validos"),
	  					  					  rs.getInt("av_56732"),rs.getInt("av_60503"),rs.getInt("av_74550"),
	  					  					  rs.getInt("av_74630"),rs.getInt("av_74710")
				);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 13 - Todos os endeços (por pista-sentido) mensal - volume veicualar e autos (avanço, parada e velocidade).
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio13FluxoVeicular_PorLocal(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorLocal(@Data_Ini, @Data_Fim) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.id_local ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				itens = new ItemFluxoVeicular(rs.getInt("id_local"),
	  					  					  rs.getLong("serie_equipamento"),
	  					  					  rs.getString("nome_pista_sentido"),
	  					  					  rs.getDouble("latitude"),
	  					  					  rs.getDouble("longitude"),
	  					  					  rs.getString("codigos_equipamentos"),
	  					  					  rs.getInt("fluxo_veicular"),
	  					  					  rs.getInt("total_detectados"),
	  					  					  rs.getInt("ad_56732"),rs.getInt("ad_60503"),rs.getInt("ad_74550"),
	  					  					  rs.getInt("ad_74630"),rs.getInt("ad_74710"),
	  					  					  rs.getInt("total_validos"),
	  					  					  rs.getInt("av_56732"),rs.getInt("av_60503"),rs.getInt("av_74550"),
	  					  					  rs.getInt("av_74630"),rs.getInt("av_74710")
				);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 13 - Todos os endeços (por pista-sentido) mensal - volume veicualar e autos (avanço, parada e velocidade).
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio13FluxoVeicular_PorFaixa(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorFaixa(@Data_Ini, @Data_Fim) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.id_local, ");
		sbSQL.append(" 	 	  rel.id_pista ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				itens = new ItemFluxoVeicular(rs.getInt("id_local"),
						  					  rs.getLong("serie_equipamento"),
						  					  rs.getString("nome_pista_sentido_faixa"),
						  					  rs.getInt("faixa"),
						  					  rs.getDouble("latitude"),
						  					  rs.getDouble("longitude"),
	  					  					  rs.getString("codigo_equipamento"),
						  					  rs.getInt("fluxo_veicular"),
						  					  rs.getInt("total_detectados"),
						  					  rs.getInt("ad_56732"),rs.getInt("ad_60503"),rs.getInt("ad_74550"),
	  					  					  rs.getInt("ad_74630"),rs.getInt("ad_74710"),
	  					  					  rs.getInt("total_validos"),
	  					  					  rs.getInt("av_56732"),rs.getInt("av_60503"),rs.getInt("av_74550"),
	  					  					  rs.getInt("av_74630"),rs.getInt("av_74710"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 14 - Cada endereço (por pista-sentido) mensal por hora - volume veicular e autos (avanço e tempo de vermelho).
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/10/2016
	 */
	public ArrayList<ItemFluxoVeicular> relatorio14FluxoVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio14MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular itens;
		Integer[] celulasFluxo;
		Integer[] celulasAutosDetectadosAvanco;
		Integer[] celulasAutosDetectadosTVeAte2;
		Integer[] celulasAutosDetectadosTVeEntre2e5;
		Integer[] celulasAutosDetectadosTVeEntre5e10;
		Integer[] celulasAutosDetectadosTVeAcima10;
		Integer[] celulasAutosValidosAvanco;
		Integer[] celulasAutosValidosTVeAte2;
		Integer[] celulasAutosValidosTVeEntre2e5;
		Integer[] celulasAutosValidosTVeEntre5e10;
		Integer[] celulasAutosValidosTVeAcima10;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxo = new Integer[31];
				celulasAutosDetectadosAvanco = new Integer[31];
				celulasAutosDetectadosTVeAte2 = new Integer[31];
				celulasAutosDetectadosTVeEntre2e5 = new Integer[31];
				celulasAutosDetectadosTVeEntre5e10 = new Integer[31];
				celulasAutosDetectadosTVeAcima10 = new Integer[31];
				celulasAutosValidosAvanco = new Integer[31];
				celulasAutosValidosTVeAte2 = new Integer[31];
				celulasAutosValidosTVeEntre2e5 = new Integer[31];
				celulasAutosValidosTVeEntre5e10 = new Integer[31];
				celulasAutosValidosTVeAcima10 = new Integer[31];
				
				Integer valorFluxo = 0;
				Integer valorAutosDetectadosAvanco = 0;
				Integer valorAutosDetectadosTVeAte2 = 0;
				Integer valorAutosDetectadosTVeEntre2e5 = 0;
				Integer valorAutosDetectadosTVeEntre5e10 = 0;
				Integer valorAutosDetectadosTVeAcima10 = 0;
				Integer valorAutosValidosAvanco = 0;
				Integer valorAutosValidosTVeAte2 = 0;
				Integer valorAutosValidosTVeEntre2e5 = 0;
				Integer valorAutosValidosTVeEntre5e10 = 0;
				Integer valorAutosValidosTVeAcima10 = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxo = (int) rs.getInt("fv_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
					valorAutosDetectadosAvanco = (int) rs.getInt("ad_avs_" + String.valueOf(i));
					celulasAutosDetectadosAvanco[i-1] = Integer.valueOf(valorAutosDetectadosAvanco);
					
					valorAutosDetectadosTVeAte2 = (int) rs.getInt("ad_avs_tve_2_" + String.valueOf(i));
					celulasAutosDetectadosTVeAte2[i-1] = Integer.valueOf(valorAutosDetectadosTVeAte2);
					
					valorAutosDetectadosTVeEntre2e5 = (int) rs.getInt("ad_avs_tve_2_5_" + String.valueOf(i));
					celulasAutosDetectadosTVeEntre2e5[i-1] = Integer.valueOf(valorAutosDetectadosTVeEntre2e5);
					
					valorAutosDetectadosTVeEntre5e10 = (int) rs.getInt("ad_avs_tve_5_10_" + String.valueOf(i));
					celulasAutosDetectadosTVeEntre5e10[i-1] = Integer.valueOf(valorAutosDetectadosTVeEntre5e10);
					
					valorAutosDetectadosTVeAcima10 = (int) rs.getInt("ad_avs_tve_10_" + String.valueOf(i));
					celulasAutosDetectadosTVeAcima10[i-1] = Integer.valueOf(valorAutosDetectadosTVeAcima10);
					
					valorAutosValidosAvanco = (int) rs.getInt("av_avs_" + String.valueOf(i));
					celulasAutosValidosAvanco[i-1] = Integer.valueOf(valorAutosValidosAvanco);
					
					valorAutosValidosTVeAte2 = (int) rs.getInt("av_avs_tve_2_" + String.valueOf(i));
					celulasAutosValidosTVeAte2[i-1] = Integer.valueOf(valorAutosValidosTVeAte2);
					
					valorAutosValidosTVeEntre2e5 = (int) rs.getInt("av_avs_tve_2_5_" + String.valueOf(i));
					celulasAutosValidosTVeEntre2e5[i-1] = Integer.valueOf(valorAutosValidosTVeEntre2e5);
					
					valorAutosValidosTVeEntre5e10 = (int) rs.getInt("av_avs_tve_5_10_" + String.valueOf(i));
					celulasAutosValidosTVeEntre5e10[i-1] = Integer.valueOf(valorAutosValidosTVeEntre5e10);
					
					valorAutosValidosTVeAcima10 = (int) rs.getInt("av_avs_tve_10_" + String.valueOf(i));
					celulasAutosValidosTVeAcima10[i-1] = Integer.valueOf(valorAutosValidosTVeAcima10);
				}
				
				itens = new ItemFluxoVeicular(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxo,
											  celulasAutosDetectadosAvanco,
											  celulasAutosDetectadosTVeAte2,
											  celulasAutosDetectadosTVeEntre2e5,
											  celulasAutosDetectadosTVeEntre5e10,
											  celulasAutosDetectadosTVeAcima10,
											  celulasAutosValidosAvanco,
											  celulasAutosValidosTVeAte2,
											  celulasAutosValidosTVeEntre2e5,
											  celulasAutosValidosTVeEntre5e10,
											  celulasAutosValidosTVeAcima10
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	
	public ItemFluxoVeicular obterInfoVelocidade85PorLocalData(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista)
		throws ConexaoException, SQLException, ModelException {
		
		ItemFluxoVeicular listDadosRelatorio = null;
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_obterInfoVelocidade85PorLocalData @Id_Local, @Id_Pista, @Data_Ini, @Data_Fim ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer[] celulas;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			if (rs.next()) {
				
				celulas = new Integer[31];
				Integer valor = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0.0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				listDadosRelatorio = new ItemFluxoVeicular(
											  rs.getInt("id_local"),
											  celulas
											);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return listDadosRelatorio;
	}
	
	public ArrayList<ItemFluxoVeicular> RelatorioFluxoMensalPorClassificacao(Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException
	{
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular item;
			
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Data_Ini DATETIME = ?,	@Data_Fim DATETIME = ? ");
		sbSQL.append(" EXEC spu_getRelatorioFluxoMensalPorClassificacao @Data_Ini, @Data_Fim ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(dataInicio.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				item = new ItemFluxoVeicular(rs.getInt("id_local"),
											 rs.getInt("pista"),
											 rs.getString("endereco"),
											 rs.getInt("grin"),
											 rs.getDate("data"),
											 rs.getInt("Moto"),
											 rs.getInt("Passeio"),
											 rs.getInt("Medio"),
											 rs.getInt("Grande"),
											 rs.getInt("Outros"),
											 rs.getInt("total"));
				
				listDadosRelatorio.add(item);
			}
		}
		catch (SQLException e)
		{
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return listDadosRelatorio;
	}
	
	public ArrayList<ItemFluxoVeicular> RelatorioFluxoDiarioPorClassificacao(Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException
	{
		ArrayList<ItemFluxoVeicular> listDadosRelatorio =  new ArrayList<ItemFluxoVeicular>();
		ItemFluxoVeicular item;
			
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Data_Ini DATETIME = ?,	@Data_Fim DATETIME = ? ");
		sbSQL.append(" EXEC spu_getRelatorioFluxoDiarioPorClassificacao @Data_Ini, @Data_Fim ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(dataInicio.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				item = new ItemFluxoVeicular(rs.getInt("id_local"),
											 rs.getInt("pista"),
											 rs.getString("endereco"),
											 rs.getInt("grin"),
											 rs.getDate("data"),
											 rs.getInt("Moto"),
											 rs.getInt("Passeio"),
											 rs.getInt("Medio"),
											 rs.getInt("Grande"),
											 rs.getInt("Outros"),
											 rs.getInt("total"));
				
				listDadosRelatorio.add(item);
			}
		}
		catch (SQLException e)
		{
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return listDadosRelatorio;
	}
	
	public StringBuilder RelatorioFluxoHoraPorClassificacao(Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException
	{
		StringBuilder dadosArquivo = new StringBuilder();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Data_Ini DATETIME = ?,	@Data_Fim DATETIME = ? ");
		sbSQL.append(" EXEC spu_getRelatorioFluxoHoraPorClassificacao @Data_Ini, @Data_Fim ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(dataInicio.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			
			rs = ps.executeQuery();
			if (rs.next())
			{
				dadosArquivo = CSVUtils.ObterCVSdeResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return dadosArquivo;
	}
	
	public StringBuilder RelatorioFluxo15MinutosPorClassificacao(Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException
	{
		StringBuilder dadosArquivo = new StringBuilder();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Data_Ini DATETIME = ?,	@Data_Fim DATETIME = ? ");
		sbSQL.append(" EXEC spu_getRelatorioFluxo15MinPorClassificacao @Data_Ini, @Data_Fim ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(dataInicio.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			
			rs = ps.executeQuery();
			if (rs.next())
			{
				dadosArquivo = CSVUtils.ObterCVSdeResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return dadosArquivo;
	}
}
