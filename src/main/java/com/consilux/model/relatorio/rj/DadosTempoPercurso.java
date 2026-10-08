/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 09/11/2016

*********************************************************************************/

package com.consilux.model.relatorio.rj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados para os relatórios de medição de tempo de percurso
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 09/11/2016
 */
public class DadosTempoPercurso {

	private static final Logger logger = Logger.getLogger(DadosTempoPercurso.class);
	private static final String strRelatorio1MedicaoTempoPercurso = "Relatorio1MedicaoTempoPercurso";
	private static final String strRelatorio1MedicaoTempoPercursoFaixa = "Relatorio1MedicaoTempoPercursoFaixa";
	private static final String strRelatorio2MedicaoTempoPercurso = "Relatorio2MedicaoTempoPercurso";
	private static final String strRelatorio2MedicaoTempoPercursoFaixa = "Relatorio2MedicaoTempoPercursoFaixa";
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 01 - Cada endereço mensal por data e horário - fluxo, velocidade e autos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 09/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio1TempoPercurso(Integer intMes, Integer intAno) throws Exception {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String strQuery = null;
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();

		
		sb1.append(" SELECT TOP 1 * FROM configuracao_relatorios_rj (NOLOCK) WHERE nome_relatorio = '" + strRelatorio1MedicaoTempoPercurso.trim() + "' ORDER BY data_atualizacao DESC ");
		
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(sb1.toString());
		
		rs = ps.executeQuery();
		
		if(rs.next()) {
			strQuery = rs.getString("query_executar");
		}
		
		ps = null;
		

		if (strQuery != null) {
			sb2.append(strQuery);
		
			ItemTempoPercurso itens;
			Integer[] celulasTempoMedio;
			Double[] celulasIndiceMobilidade;
			Integer[] celulasVelocidadePercurso;
			Integer[] celulasVolumeVeicular;
			
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	    
			
			Integer intUltimoDiaMes = 31;
			String strData = null;
		    Date dtData = null, data_ini = null, data_fim = null;
		    
		    Calendar calendarioData = Calendar.getInstance();
		    
		    strData = "01/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 00:00:00";
		    dtData = sdf2.parse(strData);
	    	calendarioData.setTime(dtData);
		    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);

		    data_ini = dtData;
		    data_fim = sdf2.parse(intUltimoDiaMes + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 23:59:59");
			
			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sb2.toString());
				ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
				ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
				
				rs = ps.executeQuery();
				
				while (rs.next()){
					
					celulasTempoMedio = new Integer[31];
					celulasIndiceMobilidade = new Double[31];
					celulasVelocidadePercurso = new Integer[31];
					celulasVolumeVeicular = new Integer[31];
					
					Integer valorTempoMedio = 0;
					Double valorIndiceMobilidade = 0.0;
					Integer valorVelocidadePercurso = 0;
					Integer valorVolumeVeicular = 0;
					
					
					for (int i = 1; i <= 31; i++) {
						
						valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
						celulasTempoMedio[i-1] = valorTempoMedio;
						
						valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
						celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
						
						valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
						celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
						
						valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
						celulasVolumeVeicular[i-1] = valorVolumeVeicular;
					}
					
					itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
												  rs.getString("nome_trecho"),
												  rs.getInt("hora"),
												  rs.getString("hora_desc"),
												  celulasTempoMedio,
												  celulasIndiceMobilidade,
												  celulasVelocidadePercurso,
												  celulasVolumeVeicular,
												  rs.getInt("tempo_medio"),
												  rs.getDouble("indice_mobilidade"),
												  rs.getInt("velocidade_media"),
												  rs.getInt("volume_veicular"));
					
					listDadosRelatorio.add(itens);
				}
				
			}catch (SQLException e) {
				throw new ModelException("ERRO de SQL", e);
			}	
			finally {
				if (conn != null)
					conn.close();
			}
		} else {
			String strMsgErro = "Não foi possível obter query para o relatório de medição [" + strRelatorio1MedicaoTempoPercurso.trim() + "]";
			logger.error(strMsgErro);
			throw new Exception(strMsgErro);
		}
		
		return listDadosRelatorio;
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de tempo de percurso (FAIXA) - Relatório 02 - Tempo de percurso, volume, velocidade e mobilidade de corredores por hora e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 09/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio1TempoPercursoFaixa(Integer intMes, Integer intAno) throws Exception {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String strQuery = null;
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();

		
		sb1.append(" SELECT TOP 1 * FROM configuracao_relatorios_rj (NOLOCK) WHERE nome_relatorio = '" + strRelatorio1MedicaoTempoPercursoFaixa.trim() + "' ORDER BY data_atualizacao DESC");
		
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(sb1.toString());
		
		rs = ps.executeQuery();
		
		if(rs.next()) {
			strQuery = rs.getString("query_executar");
		}
		
		ps = null;
		

		if (strQuery != null) {
			sb2.append(strQuery);
		
			ItemTempoPercurso itens;
			Integer[] celulasTempoMedio;
			Double[] celulasIndiceMobilidade;
			Integer[] celulasVelocidadePercurso;
			Integer[] celulasVolumeVeicular;
			
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	    
			
			Integer intUltimoDiaMes = 31;
			String strData = null;
		    Date dtData = null, data_ini = null, data_fim = null;
		    
		    Calendar calendarioData = Calendar.getInstance();
		    
		    strData = "01/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 00:00:00";
		    dtData = sdf2.parse(strData);
	    	calendarioData.setTime(dtData);
		    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);

		    data_ini = dtData;
		    data_fim = sdf2.parse(intUltimoDiaMes + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 23:59:59");
			
			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sb2.toString());
				ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
				ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
				
				rs = ps.executeQuery();
				
				while (rs.next()){
					
					celulasTempoMedio = new Integer[31];
					celulasIndiceMobilidade = new Double[31];
					celulasVelocidadePercurso = new Integer[31];
					celulasVolumeVeicular = new Integer[31];
					
					Integer valorTempoMedio = 0;
					Double valorIndiceMobilidade = 0.0;
					Integer valorVelocidadePercurso = 0;
					Integer valorVolumeVeicular = 0;
					
					
					for (int i = 1; i <= 31; i++) {
						
						valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
						celulasTempoMedio[i-1] = valorTempoMedio;
						
						valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
						celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
						
						valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
						celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
						
						valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
						celulasVolumeVeicular[i-1] = valorVolumeVeicular;
					}
					
					itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
												  rs.getString("nome_trecho"),
												  rs.getInt("hora"),
												  rs.getString("hora_desc"),
												  celulasTempoMedio,
												  celulasIndiceMobilidade,
												  celulasVelocidadePercurso,
												  celulasVolumeVeicular,
												  rs.getInt("tempo_medio"),
												  rs.getDouble("indice_mobilidade"),
												  rs.getInt("velocidade_media"),
												  rs.getInt("volume_veicular"));
					
					listDadosRelatorio.add(itens);
				}
				
			}catch (SQLException e) {
				throw new ModelException("ERRO de SQL", e);
			}	
			finally {
				if (conn != null)
					conn.close();
			}
		} else {
			String strMsgErro = "Não foi possível obter query para o relatório de medição [" + strRelatorio1MedicaoTempoPercursoFaixa.trim() + "]";
			logger.error(strMsgErro);
			throw new Exception(strMsgErro);
		}
		
		return listDadosRelatorio;
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 02 - Tempo de percurso, volume, velocidade e mobilidade de corredores por hora e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 09/11/2016 
	 */
	public ArrayList<ItemTempoPercurso> relatorio2TempoPercurso(Integer intMes, Integer intAno) throws Exception {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String strQuery = null;
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();

		
		sb1.append(" SELECT TOP 1 * FROM configuracao_relatorios_rj (NOLOCK) WHERE nome_relatorio = '" + strRelatorio2MedicaoTempoPercurso.trim() + "' ORDER BY data_atualizacao DESC ");
		
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(sb1.toString());
		
		rs = ps.executeQuery();
		
		if(rs.next()) {
			strQuery = rs.getString("query_executar");
		}
		
		ps = null;
		
		if (strQuery != null) {
			sb2.append(strQuery);
			
			ItemTempoPercurso itens;
			Integer[] celulasTempoMedio;
			Double[] celulasIndiceMobilidade;
			Integer[] celulasVelocidadePercurso;
			Integer[] celulasVolumeVeicular;
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	    
			
			Integer intUltimoDiaMes = 31;
			String strData = null;
		    Date dtData = null, data_ini = null, data_fim = null;
		    
		    Calendar calendarioData = Calendar.getInstance();
		    
		    strData = "01/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 00:00:00";
		    dtData = sdf2.parse(strData);
	    	calendarioData.setTime(dtData);
		    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);

		    data_ini = dtData;
		    data_fim = sdf2.parse(intUltimoDiaMes + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 23:59:59");
			
			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sb2.toString());
				ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
				ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
				
				rs = ps.executeQuery();
				
				while (rs.next()){
					
					celulasTempoMedio = new Integer[31];
					celulasIndiceMobilidade = new Double[31];
					celulasVelocidadePercurso = new Integer[31];
					celulasVolumeVeicular = new Integer[31];
					
					Integer valorTempoMedio = 0;
					Double valorIndiceMobilidade = 0.0;
					Integer valorVelocidadePercurso = 0;
					Integer valorVolumeVeicular = 0;
					
					
					for (int i = 1; i <= 31; i++) {
						
						valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
						celulasTempoMedio[i-1] = valorTempoMedio;
						
						valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
						celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
						
						valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
						celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
						
						valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
						celulasVolumeVeicular[i-1] = valorVolumeVeicular;
					}
					
					itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
							 					  rs.getString("nome_corredor"),
							 					  rs.getInt("hora"),
							 					  rs.getString("hora_desc"),
							 					  celulasTempoMedio,
							 					  celulasIndiceMobilidade,
							 					  celulasVelocidadePercurso,
							 					  celulasVolumeVeicular,
							 					  rs.getInt("tempo_medio"),
							 					  rs.getDouble("indice_mobilidade"),
							 					  rs.getInt("velocidade_media"),
							 					  rs.getInt("volume_veicular"));
					
					listDadosRelatorio.add(itens);
				}
				
			}catch (SQLException e) {
				throw new ModelException("ERRO de SQL", e);
			}	
			finally {
				if (conn != null)
					conn.close();
			}
		} else {
			String strMsgErro = "Não foi possível obter query para o relatório de medição [" + strRelatorio2MedicaoTempoPercurso.trim() + "]";
			logger.error(strMsgErro);
			throw new Exception(strMsgErro);
		}
		
		return listDadosRelatorio;
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de tempo de percurso (FAIXA) - Relatório 02 - Tempo de percurso, volume, velocidade e mobilidade de corredores por hora e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 09/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio2TempoPercursoFaixa(Integer intMes, Integer intAno) throws Exception {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String strQuery = null;
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();

		
		sb1.append(" SELECT TOP 1 * FROM configuracao_relatorios_rj (NOLOCK) WHERE nome_relatorio = '" + strRelatorio2MedicaoTempoPercursoFaixa.trim() + "' ORDER BY data_atualizacao DESC ");
		
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(sb1.toString());
		
		rs = ps.executeQuery();
		
		if(rs.next()) {
			strQuery = rs.getString("query_executar");
		}
		
		ps = null;
		
		if (strQuery != null) {
			sb2.append(strQuery);
			
			ItemTempoPercurso itens;
			Integer[] celulasTempoMedio;
			Double[] celulasIndiceMobilidade;
			Integer[] celulasVelocidadePercurso;
			Integer[] celulasVolumeVeicular;

			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			Integer intUltimoDiaMes = 31;
			String strData = null;
		    Date dtData = null, data_ini = null, data_fim = null;
		    
		    Calendar calendarioData = Calendar.getInstance();
		    
		    strData = "01/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 00:00:00";
		    dtData = sdf2.parse(strData);
	    	calendarioData.setTime(dtData);
		    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);

		    data_ini = dtData;
		    data_fim = sdf2.parse(intUltimoDiaMes + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno) + " 23:59:59");
			
			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sb2.toString());
				ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
				ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
			
				rs = ps.executeQuery();
				
				while (rs.next()){
					
					celulasTempoMedio = new Integer[31];
					celulasIndiceMobilidade = new Double[31];
					celulasVelocidadePercurso = new Integer[31];
					celulasVolumeVeicular = new Integer[31];
					
					Integer valorTempoMedio = 0;
					Double valorIndiceMobilidade = 0.0;
					Integer valorVelocidadePercurso = 0;
					Integer valorVolumeVeicular = 0;
					
					
					for (int i = 1; i <= 31; i++) {
						
						valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
						celulasTempoMedio[i-1] = valorTempoMedio;
						
						valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
						celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
						
						valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
						celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
						
						valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
						celulasVolumeVeicular[i-1] = valorVolumeVeicular;
					}
					
					itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
							 					  rs.getString("nome_corredor"),
							 					  rs.getInt("hora"),
							 					  rs.getString("hora_desc"),
							 					  celulasTempoMedio,
							 					  celulasIndiceMobilidade,
							 					  celulasVelocidadePercurso,
							 					  celulasVolumeVeicular,
							 					  rs.getInt("tempo_medio"),
							 					  rs.getDouble("indice_mobilidade"),
							 					  rs.getInt("velocidade_media"),
							 					  rs.getInt("volume_veicular"));
					
					listDadosRelatorio.add(itens);
				}
				
			}catch (SQLException e) {
				throw new ModelException("ERRO de SQL", e);
			}	
			finally {
				if (conn != null)
					conn.close();
			}
		} else {
			String strMsgErro = "Não foi possível obter query para o relatório de medição [" + strRelatorio2MedicaoTempoPercursoFaixa.trim() + "]";
			logger.error(strMsgErro);
			throw new Exception(strMsgErro);
		}

		return listDadosRelatorio;
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 03 - Média horária de tempo de percurso de trechos por dia da semana.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio3TempoPercurso(Integer intMes, Integer intAno, Integer intIdPercurso) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio3MedicaoTempoPercurso @Data_Ini, @Data_Fim, @Id_Percurso ");
		
//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.[segunda], ");
//		sbSQL.append(" 		  rel.[terca], ");
//		sbSQL.append(" 		  rel.[quarta], ");
//		sbSQL.append(" 		  rel.[quinta], ");
//		sbSQL.append(" 		  rel.[sexta], ");
//		sbSQL.append(" 		  rel.[sabado], ");
//		sbSQL.append(" 		  rel.[domingo], ");
//		sbSQL.append(" 		  rel.[dias_uteis] ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio3MedicaoTempoPercurso(@Data_Ini, @Data_Fim, @Id_Trecho) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_trecho, ");
//		sbSQL.append(" 		  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
						 					  rs.getString("nome_trecho"),
						 					  rs.getInt("hora"),
						 					  rs.getString("hora_desc"),
						 					  rs.getInt("domingo"),
						 					  rs.getInt("segunda"),
						 					  rs.getInt("terca"),
						 					  rs.getInt("quarta"),
						 					  rs.getInt("quinta"),
						 					  rs.getInt("sexta"),
						 					  rs.getInt("sabado"),
						 					  rs.getInt("dias_uteis"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 03 - Média horária de tempo de percurso de trechos por dia da semana.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio3TempoPercursoFaixa(Integer intMes, Integer intAno, Integer intIdPercurso, Integer intFaixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		sbSQL.append(" DECLARE @Faixa		INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio3MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim, @Id_Percurso, @Faixa ");
		
//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.[segunda], ");
//		sbSQL.append(" 		  rel.[terca], ");
//		sbSQL.append(" 		  rel.[quarta], ");
//		sbSQL.append(" 		  rel.[quinta], ");
//		sbSQL.append(" 		  rel.[sexta], ");
//		sbSQL.append(" 		  rel.[sabado], ");
//		sbSQL.append(" 		  rel.[domingo], ");
//		sbSQL.append(" 		  rel.[dias_uteis] ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio3MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim, @Id_Trecho, @Faixa) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_trecho, ");
//		sbSQL.append(" 	 	  rel.faixa, ");
//		sbSQL.append(" 		  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdPercurso);
			ps.setInt(4, intFaixa);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
						 					  rs.getString("nome_trecho"),
						 					  rs.getInt("hora"),
						 					  rs.getString("hora_desc"),
						 					  rs.getInt("domingo"),
						 					  rs.getInt("segunda"),
						 					  rs.getInt("terca"),
						 					  rs.getInt("quarta"),
						 					  rs.getInt("quinta"),
						 					  rs.getInt("sexta"),
						 					  rs.getInt("sabado"),
						 					  rs.getInt("dias_uteis"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 04 - Média horária de tempo de percurso de corredores por dia da semana.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio4TempoPercurso(Integer intMes, Integer intAno, Integer intIdPercurso) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio4MedicaoTempoPercurso @Data_Ini, @Data_Fim, @Id_Percurso ");
		
//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.[segunda], ");
//		sbSQL.append(" 		  rel.[terca], ");
//		sbSQL.append(" 		  rel.[quarta], ");
//		sbSQL.append(" 		  rel.[quinta], ");
//		sbSQL.append(" 		  rel.[sexta], ");
//		sbSQL.append(" 		  rel.[sabado], ");
//		sbSQL.append(" 		  rel.[domingo], ");
//		sbSQL.append(" 		  rel.[dias_uteis] ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio4MedicaoTempoPercurso(@Data_Ini, @Data_Fim, @Id_Corredor) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_corredor, ");
//		sbSQL.append(" 		  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
						 					  rs.getString("nome_corredor"),
						 					  rs.getInt("hora"),
						 					  rs.getString("hora_desc"),
						 					  rs.getInt("domingo"),
						 					  rs.getInt("segunda"),
						 					  rs.getInt("terca"),
						 					  rs.getInt("quarta"),
						 					  rs.getInt("quinta"),
						 					  rs.getInt("sexta"),
						 					  rs.getInt("sabado"),
						 					  rs.getInt("dias_uteis"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 03 - Média horária de tempo de percurso de trechos por dia da semana.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio4TempoPercursoFaixa(Integer intMes, Integer intAno, Integer intIdPercurso, Integer intFaixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		sbSQL.append(" DECLARE @Faixa		INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio4MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim, @Id_Percurso, @Faixa ");
		
//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.[segunda], ");
//		sbSQL.append(" 		  rel.[terca], ");
//		sbSQL.append(" 		  rel.[quarta], ");
//		sbSQL.append(" 		  rel.[quinta], ");
//		sbSQL.append(" 		  rel.[sexta], ");
//		sbSQL.append(" 		  rel.[sabado], ");
//		sbSQL.append(" 		  rel.[domingo], ");
//		sbSQL.append(" 		  rel.[dias_uteis] ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio4MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim, @Id_Corredor, @Faixa) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_corredor, ");
//		sbSQL.append(" 	 	  rel.faixa, ");
//		sbSQL.append(" 		  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdPercurso);
			ps.setInt(4, intFaixa);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
						 					  rs.getString("nome_corredor"),
						 					  rs.getInt("hora"),
						 					  rs.getString("hora_desc"),
						 					  rs.getInt("domingo"),
						 					  rs.getInt("segunda"),
						 					  rs.getInt("terca"),
						 					  rs.getInt("quarta"),
						 					  rs.getInt("quinta"),
						 					  rs.getInt("sexta"),
						 					  rs.getInt("sabado"),
						 					  rs.getInt("dias_uteis"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 05 - Média horária do tempo de percurso e volume médio de trechos em dias úteis por mês.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio5TempoPercurso(Integer intMes, Integer intAno, Integer intIdPercurso) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio5MedicaoTempoPercurso @Data_Ini, @Data_Fim, @Id_Percurso ");
		
//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio5MedicaoTempoPercurso(@Data_Ini, @Data_Fim, @Id_Trecho) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_trecho, ");
//		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
											  rs.getString("nome_trecho"),
											  rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  rs.getInt("tempo_medio"),
											  rs.getDouble("indice_mobilidade"),
											  rs.getInt("velocidade_percurso"),
											  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 05 - Média horária do tempo de percurso e volume médio de trechos em dias úteis por mês.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio5TempoPercursoFaixa(Integer intMes, Integer intAno, Integer intIdPercurso, Integer intFaixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		sbSQL.append(" DECLARE @Faixa		INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio5MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim, @Id_Percurso, @Faixa ");

//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio5MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim, @Id_Trecho, @Faixa) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_trecho, ");
//		sbSQL.append(" 	 	  rel.faixa, ");
//		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			ps.setInt(4, intFaixa);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
											  rs.getString("nome_trecho"),
											  rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  rs.getInt("tempo_medio"),
											  rs.getDouble("indice_mobilidade"),
											  rs.getInt("velocidade_percurso"),
											  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 06 - Média horária do tempo de percurso e volume médio de corredores em dias úteis por mês.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio6TempoPercurso(Integer intMes, Integer intAno, Integer intIdPercurso) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio6MedicaoTempoPercurso @Data_Ini, @Data_Fim, @Id_Percurso ");

//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio6MedicaoTempoPercurso(@Data_Ini, @Data_Fim, @Id_Corredor) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_corredor, ");
//		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
											  rs.getString("nome_corredor"),
											  rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  rs.getInt("tempo_medio"),
											  rs.getDouble("indice_mobilidade"),
											  rs.getInt("velocidade_percurso"),
											  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 06 - Média horária do tempo de percurso e volume médio de corredores em dias úteis por mês.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio6TempoPercursoFaixa(Integer intMes, Integer intAno, Integer intIdPercurso, Integer intFaixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso	INT = ? ");
		sbSQL.append(" DECLARE @Faixa		INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio6MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim, @Id_Percurso, @Faixa ");

//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.hora, ");
//		sbSQL.append(" 		  rel.hora_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio6MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim, @Id_Corredor, @Faixa) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_corredor, ");
//		sbSQL.append(" 	 	  rel.faixa, ");
//		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			ps.setInt(4, intFaixa);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
											  rs.getString("nome_corredor"),
											  rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  rs.getInt("tempo_medio"),
											  rs.getDouble("indice_mobilidade"),
											  rs.getInt("velocidade_percurso"),
											  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 07 - Média diária do tempo de percurso e volume médio de trechos mensal por data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio7TempoPercurso(Integer intMes, Integer intAno, Integer intIdPercurso) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio7MedicaoTempoPercurso @Data_Ini, @Data_Fim, @Id_Percurso ");
		
//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.dia, ");
//		sbSQL.append(" 		  rel.dia_semana, ");
//		sbSQL.append(" 		  rel.dia_semana_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio7MedicaoTempoPercurso(@Data_Ini, @Data_Fim, @Id_Percurso) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.dia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
						 					  rs.getString("nome_trecho"),
						 					  rs.getDate("data"),
						 					  rs.getInt("dia"),
						 					  rs.getInt("dia_semana"),
						 					  rs.getString("dia_semana_desc"),
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_percurso"),
						 					  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	public ArrayList<ItemTempoPercurso> relatorio7TempoPercursoFaixa(Integer intMes, Integer intAno, Integer intIdPercurso, Integer intFaixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso INT = ? ");
		sbSQL.append(" DECLARE @Faixa       INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");
		
		sbSQL.append(" EXEC dbo.spu_getRelatorio7MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim, @Id_Percurso, @Faixa ");

//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.dia, ");
//		sbSQL.append(" 		  rel.dia_semana, ");
//		sbSQL.append(" 		  rel.dia_semana_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio7MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim, @Id_Percurso, @Faixa) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.dia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdPercurso);
			ps.setInt(4, intFaixa);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
						 					  rs.getString("nome_trecho"),
						 					  rs.getDate("data"),
						 					  rs.getInt("dia"),
						 					  rs.getInt("dia_semana"),
						 					  rs.getString("dia_semana_desc"),
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_percurso"),
						 					  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 08 - Média diária do tempo de percurso e volume médio de corredores mensal por data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio8TempoPercurso(Integer intMes, Integer intAno, Integer intIdPercurso) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio8MedicaoTempoPercurso @Data_Ini, @Data_Fim, @Id_Percurso ");
		
//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.dia, ");
//		sbSQL.append(" 		  rel.dia_semana, ");
//		sbSQL.append(" 		  rel.dia_semana_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio8MedicaoTempoPercurso(@Data_Ini, @Data_Fim, @Id_Percurso) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.dia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
						 					  rs.getString("nome_corredor"),
						 					  rs.getDate("data"),
						 					  rs.getInt("dia"),
						 					  rs.getInt("dia_semana"),
						 					  rs.getString("dia_semana_desc"),
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_percurso"),
						 					  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
public ArrayList<ItemTempoPercurso> relatorio8TempoPercursoFaixa(Integer intMes, Integer intAno, Integer intIdPercurso, Integer intFaixa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Percurso INT = ? ");
		sbSQL.append(" DECLARE @Faixa       INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio8MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim, @Id_Percurso, @Faixa ");
		
//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.dia, ");
//		sbSQL.append(" 		  rel.dia_semana, ");
//		sbSQL.append(" 		  rel.dia_semana_desc, ");
//		sbSQL.append(" 		  rel.tempo_medio, ");
//		sbSQL.append(" 		  rel.indice_mobilidade, ");
//		sbSQL.append(" 		  rel.velocidade_percurso, ");
//		sbSQL.append(" 		  rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio8MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim, @Id_Percurso, @Faixa) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.dia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setLong(3, intIdPercurso);
			ps.setInt(4, intFaixa);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
						 					  rs.getString("nome_corredor"),
						 					  rs.getDate("data"),
						 					  rs.getInt("dia"),
						 					  rs.getInt("dia_semana"),
						 					  rs.getString("dia_semana_desc"),
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_percurso"),
						 					  rs.getInt("volume_veicular"));
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 09 - Tempo de percurso, volume, velocidade e mobilidade de trechos por período de pico e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 25/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio9TempoPercurso(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio9MedicaoTempoPercurso @Data_Ini, @Data_Fim ");
		
//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.periodo, ");
//		sbSQL.append(" 		  rel.desc_periodo, ");
//		sbSQL.append(" 		  rel.[tm_1],rel.[im_1],rel.[vel_1],rel.[vv_1], ");
//		sbSQL.append(" 		  rel.[tm_2],rel.[im_2],rel.[vel_2],rel.[vv_2], ");
//		sbSQL.append(" 		  rel.[tm_3],rel.[im_3],rel.[vel_3],rel.[vv_3], ");
//		sbSQL.append(" 		  rel.[tm_4],rel.[im_4],rel.[vel_4],rel.[vv_4], ");
//		sbSQL.append(" 		  rel.[tm_5],rel.[im_5],rel.[vel_5],rel.[vv_5], ");
//		sbSQL.append(" 		  rel.[tm_6],rel.[im_6],rel.[vel_6],rel.[vv_6], ");
//		sbSQL.append(" 		  rel.[tm_7],rel.[im_7],rel.[vel_7],rel.[vv_7], ");
//		sbSQL.append(" 		  rel.[tm_8],rel.[im_8],rel.[vel_8],rel.[vv_8], ");
//		sbSQL.append(" 		  rel.[tm_9],rel.[im_9],rel.[vel_9],rel.[vv_9], ");
//		sbSQL.append(" 		  rel.[tm_10],rel.[im_10],rel.[vel_10],rel.[vv_10], ");
//		sbSQL.append(" 		  rel.[tm_11],rel.[im_11],rel.[vel_11],rel.[vv_11], ");
//		sbSQL.append(" 		  rel.[tm_12],rel.[im_12],rel.[vel_12],rel.[vv_12], ");
//		sbSQL.append(" 		  rel.[tm_13],rel.[im_13],rel.[vel_13],rel.[vv_13], ");
//		sbSQL.append(" 		  rel.[tm_14],rel.[im_14],rel.[vel_14],rel.[vv_14], ");
//		sbSQL.append(" 		  rel.[tm_15],rel.[im_15],rel.[vel_15],rel.[vv_15], ");
//		sbSQL.append(" 		  rel.[tm_16],rel.[im_16],rel.[vel_16],rel.[vv_16], ");
//		sbSQL.append(" 		  rel.[tm_17],rel.[im_17],rel.[vel_17],rel.[vv_17], ");
//		sbSQL.append(" 		  rel.[tm_18],rel.[im_18],rel.[vel_18],rel.[vv_18], ");
//		sbSQL.append(" 		  rel.[tm_19],rel.[im_19],rel.[vel_19],rel.[vv_19], ");
//		sbSQL.append(" 		  rel.[tm_20],rel.[im_20],rel.[vel_20],rel.[vv_20], ");
//		sbSQL.append(" 		  rel.[tm_21],rel.[im_21],rel.[vel_21],rel.[vv_21], ");
//		sbSQL.append(" 		  rel.[tm_22],rel.[im_22],rel.[vel_22],rel.[vv_22], ");
//		sbSQL.append(" 		  rel.[tm_23],rel.[im_23],rel.[vel_23],rel.[vv_23], ");
//		sbSQL.append(" 		  rel.[tm_24],rel.[im_24],rel.[vel_24],rel.[vv_24], ");
//		sbSQL.append(" 		  rel.[tm_25],rel.[im_25],rel.[vel_25],rel.[vv_25], ");
//		sbSQL.append(" 		  rel.[tm_26],rel.[im_26],rel.[vel_26],rel.[vv_26], ");
//		sbSQL.append(" 		  rel.[tm_27],rel.[im_27],rel.[vel_27],rel.[vv_27], ");
//		sbSQL.append(" 		  rel.[tm_28],rel.[im_28],rel.[vel_28],rel.[vv_28], ");
//		sbSQL.append(" 		  rel.[tm_29],rel.[im_29],rel.[vel_29],rel.[vv_29], ");
//		sbSQL.append(" 		  rel.[tm_30],rel.[im_30],rel.[vel_30],rel.[vv_30], ");
//		sbSQL.append(" 		  rel.[tm_31],rel.[im_31],rel.[vel_31],rel.[vv_31], ");
//		sbSQL.append(" 		  rel.tempo_medio,rel.indice_mobilidade,rel.velocidade_media,rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio9MedicaoTempoPercurso(@Data_Ini, @Data_Fim) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_trecho, ");
//		sbSQL.append(" 	 	  rel.periodo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		Integer[] celulasTempoMedio;
		Double[] celulasIndiceMobilidade;
		Integer[] celulasVelocidadePercurso;
		Integer[] celulasVolumeVeicular;
		
		String strRelatorio = "Relatório por período de pico";
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasTempoMedio = new Integer[31];
				celulasIndiceMobilidade = new Double[31];
				celulasVelocidadePercurso = new Integer[31];
				celulasVolumeVeicular = new Integer[31];
				
				Integer valorTempoMedio = 0;
				Double valorIndiceMobilidade = 0.0;
				Integer valorVelocidadePercurso = 0;
				Integer valorVolumeVeicular = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
					celulasTempoMedio[i-1] = valorTempoMedio;
					
					valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
					celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
					
					valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
					celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
					
					valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
					celulasVolumeVeicular[i-1] = valorVolumeVeicular;
				}
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
						 					  rs.getString("nome_trecho"),
						 					  rs.getInt("periodo"),
						 					  rs.getString("desc_periodo"),
						 					  celulasTempoMedio,
						 					  celulasIndiceMobilidade,
						 					  celulasVelocidadePercurso,
						 					  celulasVolumeVeicular,
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_media"),
						 					  rs.getInt("volume_veicular"),
						 					  strRelatorio);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 09 - Tempo de percurso, volume, velocidade e mobilidade de trechos por período de pico e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 25/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio9TempoPercursoFaixa(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio9MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim ");
		
//		sbSQL.append(" SELECT rel.id_trecho, ");
//		sbSQL.append(" 		  rel.nome_trecho, ");
//		sbSQL.append(" 		  rel.periodo, ");
//		sbSQL.append(" 		  rel.desc_periodo, ");
//		sbSQL.append(" 		  rel.[tm_1],rel.[im_1],rel.[vel_1],rel.[vv_1], ");
//		sbSQL.append(" 		  rel.[tm_2],rel.[im_2],rel.[vel_2],rel.[vv_2], ");
//		sbSQL.append(" 		  rel.[tm_3],rel.[im_3],rel.[vel_3],rel.[vv_3], ");
//		sbSQL.append(" 		  rel.[tm_4],rel.[im_4],rel.[vel_4],rel.[vv_4], ");
//		sbSQL.append(" 		  rel.[tm_5],rel.[im_5],rel.[vel_5],rel.[vv_5], ");
//		sbSQL.append(" 		  rel.[tm_6],rel.[im_6],rel.[vel_6],rel.[vv_6], ");
//		sbSQL.append(" 		  rel.[tm_7],rel.[im_7],rel.[vel_7],rel.[vv_7], ");
//		sbSQL.append(" 		  rel.[tm_8],rel.[im_8],rel.[vel_8],rel.[vv_8], ");
//		sbSQL.append(" 		  rel.[tm_9],rel.[im_9],rel.[vel_9],rel.[vv_9], ");
//		sbSQL.append(" 		  rel.[tm_10],rel.[im_10],rel.[vel_10],rel.[vv_10], ");
//		sbSQL.append(" 		  rel.[tm_11],rel.[im_11],rel.[vel_11],rel.[vv_11], ");
//		sbSQL.append(" 		  rel.[tm_12],rel.[im_12],rel.[vel_12],rel.[vv_12], ");
//		sbSQL.append(" 		  rel.[tm_13],rel.[im_13],rel.[vel_13],rel.[vv_13], ");
//		sbSQL.append(" 		  rel.[tm_14],rel.[im_14],rel.[vel_14],rel.[vv_14], ");
//		sbSQL.append(" 		  rel.[tm_15],rel.[im_15],rel.[vel_15],rel.[vv_15], ");
//		sbSQL.append(" 		  rel.[tm_16],rel.[im_16],rel.[vel_16],rel.[vv_16], ");
//		sbSQL.append(" 		  rel.[tm_17],rel.[im_17],rel.[vel_17],rel.[vv_17], ");
//		sbSQL.append(" 		  rel.[tm_18],rel.[im_18],rel.[vel_18],rel.[vv_18], ");
//		sbSQL.append(" 		  rel.[tm_19],rel.[im_19],rel.[vel_19],rel.[vv_19], ");
//		sbSQL.append(" 		  rel.[tm_20],rel.[im_20],rel.[vel_20],rel.[vv_20], ");
//		sbSQL.append(" 		  rel.[tm_21],rel.[im_21],rel.[vel_21],rel.[vv_21], ");
//		sbSQL.append(" 		  rel.[tm_22],rel.[im_22],rel.[vel_22],rel.[vv_22], ");
//		sbSQL.append(" 		  rel.[tm_23],rel.[im_23],rel.[vel_23],rel.[vv_23], ");
//		sbSQL.append(" 		  rel.[tm_24],rel.[im_24],rel.[vel_24],rel.[vv_24], ");
//		sbSQL.append(" 		  rel.[tm_25],rel.[im_25],rel.[vel_25],rel.[vv_25], ");
//		sbSQL.append(" 		  rel.[tm_26],rel.[im_26],rel.[vel_26],rel.[vv_26], ");
//		sbSQL.append(" 		  rel.[tm_27],rel.[im_27],rel.[vel_27],rel.[vv_27], ");
//		sbSQL.append(" 		  rel.[tm_28],rel.[im_28],rel.[vel_28],rel.[vv_28], ");
//		sbSQL.append(" 		  rel.[tm_29],rel.[im_29],rel.[vel_29],rel.[vv_29], ");
//		sbSQL.append(" 		  rel.[tm_30],rel.[im_30],rel.[vel_30],rel.[vv_30], ");
//		sbSQL.append(" 		  rel.[tm_31],rel.[im_31],rel.[vel_31],rel.[vv_31], ");
//		sbSQL.append(" 		  rel.tempo_medio,rel.indice_mobilidade,rel.velocidade_media,rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio9MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_trecho, ");
//		sbSQL.append(" 	 	  rel.faixa, ");
//		sbSQL.append(" 	 	  rel.periodo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		Integer[] celulasTempoMedio;
		Double[] celulasIndiceMobilidade;
		Integer[] celulasVelocidadePercurso;
		Integer[] celulasVolumeVeicular;
		
		String strRelatorio = "Relatório por período de pico";
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasTempoMedio = new Integer[31];
				celulasIndiceMobilidade = new Double[31];
				celulasVelocidadePercurso = new Integer[31];
				celulasVolumeVeicular = new Integer[31];
				
				Integer valorTempoMedio = 0;
				Double valorIndiceMobilidade = 0.0;
				Integer valorVelocidadePercurso = 0;
				Integer valorVolumeVeicular = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
					celulasTempoMedio[i-1] = valorTempoMedio;
					
					valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
					celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
					
					valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
					celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
					
					valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
					celulasVolumeVeicular[i-1] = valorVolumeVeicular;
				}
				
				itens = new ItemTempoPercurso(rs.getInt("id_trecho"),
						 					  rs.getString("nome_trecho"),
						 					  rs.getInt("periodo"),
						 					  rs.getString("desc_periodo"),
						 					  celulasTempoMedio,
						 					  celulasIndiceMobilidade,
						 					  celulasVelocidadePercurso,
						 					  celulasVolumeVeicular,
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_media"),
						 					  rs.getInt("volume_veicular"),
						 					  strRelatorio);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 10 - Tempo de percurso, volume, velocidade e mobilidade de corredores por período de pico e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 25/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio10TempoPercurso(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio10MedicaoTempoPercurso @Data_Ini, @Data_Fim ");
		
//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.periodo, ");
//		sbSQL.append(" 		  rel.desc_periodo, ");
//		sbSQL.append(" 		  rel.[tm_1],rel.[im_1],rel.[vel_1],rel.[vv_1], ");
//		sbSQL.append(" 		  rel.[tm_2],rel.[im_2],rel.[vel_2],rel.[vv_2], ");
//		sbSQL.append(" 		  rel.[tm_3],rel.[im_3],rel.[vel_3],rel.[vv_3], ");
//		sbSQL.append(" 		  rel.[tm_4],rel.[im_4],rel.[vel_4],rel.[vv_4], ");
//		sbSQL.append(" 		  rel.[tm_5],rel.[im_5],rel.[vel_5],rel.[vv_5], ");
//		sbSQL.append(" 		  rel.[tm_6],rel.[im_6],rel.[vel_6],rel.[vv_6], ");
//		sbSQL.append(" 		  rel.[tm_7],rel.[im_7],rel.[vel_7],rel.[vv_7], ");
//		sbSQL.append(" 		  rel.[tm_8],rel.[im_8],rel.[vel_8],rel.[vv_8], ");
//		sbSQL.append(" 		  rel.[tm_9],rel.[im_9],rel.[vel_9],rel.[vv_9], ");
//		sbSQL.append(" 		  rel.[tm_10],rel.[im_10],rel.[vel_10],rel.[vv_10], ");
//		sbSQL.append(" 		  rel.[tm_11],rel.[im_11],rel.[vel_11],rel.[vv_11], ");
//		sbSQL.append(" 		  rel.[tm_12],rel.[im_12],rel.[vel_12],rel.[vv_12], ");
//		sbSQL.append(" 		  rel.[tm_13],rel.[im_13],rel.[vel_13],rel.[vv_13], ");
//		sbSQL.append(" 		  rel.[tm_14],rel.[im_14],rel.[vel_14],rel.[vv_14], ");
//		sbSQL.append(" 		  rel.[tm_15],rel.[im_15],rel.[vel_15],rel.[vv_15], ");
//		sbSQL.append(" 		  rel.[tm_16],rel.[im_16],rel.[vel_16],rel.[vv_16], ");
//		sbSQL.append(" 		  rel.[tm_17],rel.[im_17],rel.[vel_17],rel.[vv_17], ");
//		sbSQL.append(" 		  rel.[tm_18],rel.[im_18],rel.[vel_18],rel.[vv_18], ");
//		sbSQL.append(" 		  rel.[tm_19],rel.[im_19],rel.[vel_19],rel.[vv_19], ");
//		sbSQL.append(" 		  rel.[tm_20],rel.[im_20],rel.[vel_20],rel.[vv_20], ");
//		sbSQL.append(" 		  rel.[tm_21],rel.[im_21],rel.[vel_21],rel.[vv_21], ");
//		sbSQL.append(" 		  rel.[tm_22],rel.[im_22],rel.[vel_22],rel.[vv_22], ");
//		sbSQL.append(" 		  rel.[tm_23],rel.[im_23],rel.[vel_23],rel.[vv_23], ");
//		sbSQL.append(" 		  rel.[tm_24],rel.[im_24],rel.[vel_24],rel.[vv_24], ");
//		sbSQL.append(" 		  rel.[tm_25],rel.[im_25],rel.[vel_25],rel.[vv_25], ");
//		sbSQL.append(" 		  rel.[tm_26],rel.[im_26],rel.[vel_26],rel.[vv_26], ");
//		sbSQL.append(" 		  rel.[tm_27],rel.[im_27],rel.[vel_27],rel.[vv_27], ");
//		sbSQL.append(" 		  rel.[tm_28],rel.[im_28],rel.[vel_28],rel.[vv_28], ");
//		sbSQL.append(" 		  rel.[tm_29],rel.[im_29],rel.[vel_29],rel.[vv_29], ");
//		sbSQL.append(" 		  rel.[tm_30],rel.[im_30],rel.[vel_30],rel.[vv_30], ");
//		sbSQL.append(" 		  rel.[tm_31],rel.[im_31],rel.[vel_31],rel.[vv_31], ");
//		sbSQL.append(" 		  rel.tempo_medio,rel.indice_mobilidade,rel.velocidade_media,rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio10MedicaoTempoPercurso(@Data_Ini, @Data_Fim) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_corredor, ");
//		sbSQL.append(" 	 	  rel.periodo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		Integer[] celulasTempoMedio;
		Double[] celulasIndiceMobilidade;
		Integer[] celulasVelocidadePercurso;
		Integer[] celulasVolumeVeicular;
		
		String strRelatorio = "Relatório por período de pico";
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasTempoMedio = new Integer[31];
				celulasIndiceMobilidade = new Double[31];
				celulasVelocidadePercurso = new Integer[31];
				celulasVolumeVeicular = new Integer[31];
				
				Integer valorTempoMedio = 0;
				Double valorIndiceMobilidade = 0.0;
				Integer valorVelocidadePercurso = 0;
				Integer valorVolumeVeicular = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
					celulasTempoMedio[i-1] = valorTempoMedio;
					
					valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
					celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
					
					valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
					celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
					
					valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
					celulasVolumeVeicular[i-1] = valorVolumeVeicular;
				}
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
						 					  rs.getString("nome_corredor"),
						 					  rs.getInt("periodo"),
						 					  rs.getString("desc_periodo"),
						 					  celulasTempoMedio,
						 					  celulasIndiceMobilidade,
						 					  celulasVelocidadePercurso,
						 					  celulasVolumeVeicular,
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_media"),
						 					  rs.getInt("volume_veicular"),
						 					  strRelatorio);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de dados para os relatórios de medição de fluxo veicular - Relatório 10 - Tempo de percurso, volume, velocidade e mobilidade de corredores por período de pico e data.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 25/11/2016
	 */
	public ArrayList<ItemTempoPercurso> relatorio10TempoPercursoFaixa(Integer intMes, Integer intAno) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorio10MedicaoTempoPercursoFaixa @Data_Ini, @Data_Fim ");
		
//		sbSQL.append(" SELECT rel.id_corredor, ");
//		sbSQL.append(" 		  rel.nome_corredor, ");
//		sbSQL.append(" 		  rel.periodo, ");
//		sbSQL.append(" 		  rel.desc_periodo, ");
//		sbSQL.append(" 		  rel.[tm_1],rel.[im_1],rel.[vel_1],rel.[vv_1], ");
//		sbSQL.append(" 		  rel.[tm_2],rel.[im_2],rel.[vel_2],rel.[vv_2], ");
//		sbSQL.append(" 		  rel.[tm_3],rel.[im_3],rel.[vel_3],rel.[vv_3], ");
//		sbSQL.append(" 		  rel.[tm_4],rel.[im_4],rel.[vel_4],rel.[vv_4], ");
//		sbSQL.append(" 		  rel.[tm_5],rel.[im_5],rel.[vel_5],rel.[vv_5], ");
//		sbSQL.append(" 		  rel.[tm_6],rel.[im_6],rel.[vel_6],rel.[vv_6], ");
//		sbSQL.append(" 		  rel.[tm_7],rel.[im_7],rel.[vel_7],rel.[vv_7], ");
//		sbSQL.append(" 		  rel.[tm_8],rel.[im_8],rel.[vel_8],rel.[vv_8], ");
//		sbSQL.append(" 		  rel.[tm_9],rel.[im_9],rel.[vel_9],rel.[vv_9], ");
//		sbSQL.append(" 		  rel.[tm_10],rel.[im_10],rel.[vel_10],rel.[vv_10], ");
//		sbSQL.append(" 		  rel.[tm_11],rel.[im_11],rel.[vel_11],rel.[vv_11], ");
//		sbSQL.append(" 		  rel.[tm_12],rel.[im_12],rel.[vel_12],rel.[vv_12], ");
//		sbSQL.append(" 		  rel.[tm_13],rel.[im_13],rel.[vel_13],rel.[vv_13], ");
//		sbSQL.append(" 		  rel.[tm_14],rel.[im_14],rel.[vel_14],rel.[vv_14], ");
//		sbSQL.append(" 		  rel.[tm_15],rel.[im_15],rel.[vel_15],rel.[vv_15], ");
//		sbSQL.append(" 		  rel.[tm_16],rel.[im_16],rel.[vel_16],rel.[vv_16], ");
//		sbSQL.append(" 		  rel.[tm_17],rel.[im_17],rel.[vel_17],rel.[vv_17], ");
//		sbSQL.append(" 		  rel.[tm_18],rel.[im_18],rel.[vel_18],rel.[vv_18], ");
//		sbSQL.append(" 		  rel.[tm_19],rel.[im_19],rel.[vel_19],rel.[vv_19], ");
//		sbSQL.append(" 		  rel.[tm_20],rel.[im_20],rel.[vel_20],rel.[vv_20], ");
//		sbSQL.append(" 		  rel.[tm_21],rel.[im_21],rel.[vel_21],rel.[vv_21], ");
//		sbSQL.append(" 		  rel.[tm_22],rel.[im_22],rel.[vel_22],rel.[vv_22], ");
//		sbSQL.append(" 		  rel.[tm_23],rel.[im_23],rel.[vel_23],rel.[vv_23], ");
//		sbSQL.append(" 		  rel.[tm_24],rel.[im_24],rel.[vel_24],rel.[vv_24], ");
//		sbSQL.append(" 		  rel.[tm_25],rel.[im_25],rel.[vel_25],rel.[vv_25], ");
//		sbSQL.append(" 		  rel.[tm_26],rel.[im_26],rel.[vel_26],rel.[vv_26], ");
//		sbSQL.append(" 		  rel.[tm_27],rel.[im_27],rel.[vel_27],rel.[vv_27], ");
//		sbSQL.append(" 		  rel.[tm_28],rel.[im_28],rel.[vel_28],rel.[vv_28], ");
//		sbSQL.append(" 		  rel.[tm_29],rel.[im_29],rel.[vel_29],rel.[vv_29], ");
//		sbSQL.append(" 		  rel.[tm_30],rel.[im_30],rel.[vel_30],rel.[vv_30], ");
//		sbSQL.append(" 		  rel.[tm_31],rel.[im_31],rel.[vel_31],rel.[vv_31], ");
//		sbSQL.append(" 		  rel.tempo_medio,rel.indice_mobilidade,rel.velocidade_media,rel.volume_veicular ");
//		sbSQL.append(" FROM   dbo.fcn_getRelatorio10MedicaoTempoPercursoFaixa(@Data_Ini, @Data_Fim) rel ");
//
//		sbSQL.append(" ORDER BY ");
//		sbSQL.append(" 	 	  rel.id_corredor, ");
//		sbSQL.append(" 	 	  rel.faixa, ");
//		sbSQL.append(" 	 	  rel.periodo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemTempoPercurso> listDadosRelatorio =  new ArrayList<ItemTempoPercurso>();
		ItemTempoPercurso itens;
		Integer[] celulasTempoMedio;
		Double[] celulasIndiceMobilidade;
		Integer[] celulasVelocidadePercurso;
		Integer[] celulasVolumeVeicular;
		
		String strRelatorio = "Relatório por período de pico";
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasTempoMedio = new Integer[31];
				celulasIndiceMobilidade = new Double[31];
				celulasVelocidadePercurso = new Integer[31];
				celulasVolumeVeicular = new Integer[31];
				
				Integer valorTempoMedio = 0;
				Double valorIndiceMobilidade = 0.0;
				Integer valorVelocidadePercurso = 0;
				Integer valorVolumeVeicular = 0;
				
				
				for (int i = 1; i <= 31; i++) {
					
					valorTempoMedio = rs.getInt("tm_" + String.valueOf(i));
					celulasTempoMedio[i-1] = valorTempoMedio;
					
					valorIndiceMobilidade = (double) rs.getDouble("im_" + String.valueOf(i));
					celulasIndiceMobilidade[i-1] = valorIndiceMobilidade;
					
					valorVelocidadePercurso = (int) rs.getInt("vel_" + String.valueOf(i));
					celulasVelocidadePercurso[i-1] = valorVelocidadePercurso;
					
					valorVolumeVeicular = (int) rs.getInt("vv_" + String.valueOf(i));
					celulasVolumeVeicular[i-1] = valorVolumeVeicular;
				}
				
				itens = new ItemTempoPercurso(rs.getInt("id_corredor"),
						 					  rs.getString("nome_corredor"),
						 					  rs.getInt("periodo"),
						 					  rs.getString("desc_periodo"),
						 					  celulasTempoMedio,
						 					  celulasIndiceMobilidade,
						 					  celulasVelocidadePercurso,
						 					  celulasVolumeVeicular,
						 					  rs.getInt("tempo_medio"),
						 					  rs.getDouble("indice_mobilidade"),
						 					  rs.getInt("velocidade_media"),
						 					  rs.getInt("volume_veicular"),
						 					  strRelatorio);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}

}
