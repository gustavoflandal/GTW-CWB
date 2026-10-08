package com.consilux.model.relatorio.rj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.lib.Conexao;

public class DadosRelatorioMedicao
{
	private static final Logger logger = Logger.getLogger(DadosRelatorioMedicao.class);
	
	public enum TipoOcorrencia
	{
		INVALIDO,
		AVANCO,
		PARADA,
		VELOCIDADE,
		OCR,
		FLUXO_VEICULAR,
		IMAGEM_TESTE
	}
	
	public static List<ItemRelatorioMedicao> ObterItensRelatorioMedicao(Date data_ini, Date data_fim)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		ArrayList<ItemRelatorioMedicao> listDadosRelatorio =  new ArrayList<ItemRelatorioMedicao>();
		ItemRelatorioMedicao itens;
		Integer[] celulasValores;
		String[] celulasColunasRelatorio;
		
		try
		{
			StringBuilder sbSQL = new StringBuilder();

			sbSQL.append(" EXEC dbo.spu_getPlanilhaAcompanhamento ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
			ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			Integer columnCount = (rsmd.getColumnCount() - 10);
			
			celulasColunasRelatorio = new String[columnCount];
			String colunaRelatorio = "";
			
			for (int i = 1; i <= columnCount; i++ )
			{
				int c = i+7;
				
				colunaRelatorio = rsmd.getColumnName(c);
				celulasColunasRelatorio[i-1] = colunaRelatorio;
			}
			
			while (rs.next())
			{
				celulasValores = new Integer[columnCount];
				
				Integer valorCelula = 0;
				
				for (int i = 1; i <= celulasColunasRelatorio.length; i++)
				{
					String dia = celulasColunasRelatorio[i-1];
					
					valorCelula = (int) rs.getInt(dia);
					celulasValores[i-1] = Integer.valueOf(valorCelula);
				}
				
				itens = new ItemRelatorioMedicao
				(
					rs.getLong("numero_equipamento"),
					rs.getString("endereco_equipamento"),
					rs.getLong("codigo_CET"),
					rs.getShort("faixa"),
					rs.getShort("id_ocorrencia"),
					rs.getString("ocorrencia"),
					rs.getDate("data_publicacao"),
					celulasColunasRelatorio,
					celulasValores,
					columnCount,
					rs.getShort("dias_sem_funcionamento"),
					rs.getString("justificativa"),
					rs.getShort("regras")
				);
				
				listDadosRelatorio.add(itens);
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter dados de funcionamento", e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				
				if (ps != null)
					ps.close();
				
				if (rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				logger.error("Erro ao fechar conexões", e);
			}
		}
		
		return listDadosRelatorio;
	}
	
	public static List<ItemRelatorioMedicao> ObterItensRelatorioAcompanhamento(Date data_ini, Date data_fim)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		ArrayList<ItemRelatorioMedicao> listDadosRelatorio =  new ArrayList<ItemRelatorioMedicao>();
		ItemRelatorioMedicao itens;
		Integer[] celulasValores;
		String[] celulasColunasRelatorio;
		
		try
		{
			StringBuilder sbSQL = new StringBuilder();

			sbSQL.append(" EXEC dbo.spu_getRelatorioAcompanhamento ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
			ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			Integer columnCount = 0;
			String colunaRelatorio = "";
			List<String> celulasColunasRelatorioList = new ArrayList<String>();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
			{
				colunaRelatorio = rsmd.getColumnName(i);
				if (Funcoes.isDataValida(colunaRelatorio, "dd/MM/yyyy"))
				{
					celulasColunasRelatorioList.add(colunaRelatorio);
					columnCount++;
				}
			}
			
			celulasColunasRelatorio = new String[celulasColunasRelatorioList.size()];
			celulasColunasRelatorio = celulasColunasRelatorioList.toArray(celulasColunasRelatorio);
			
			while (rs.next())
			{
				celulasValores = new Integer[columnCount];
				
				Integer valorCelula = 0;
				
				for (int i = 1; i <= celulasColunasRelatorio.length; i++)
				{
					String dia = celulasColunasRelatorio[i-1];
					
					valorCelula = (int) rs.getInt(dia);
					celulasValores[i-1] = Integer.valueOf(valorCelula);
				}
				
				itens = new ItemRelatorioMedicao
				(
					rs.getLong("numero_equipamento"),
					rs.getString("endereco_equipamento"),
					rs.getShort("faixa"),
					rs.getShort("id_ocorrencia"),
					rs.getString("ocorrencia"),
					rs.getDate("data_publicacao"),
					celulasColunasRelatorio,
					celulasValores,
					columnCount
				);
				
				listDadosRelatorio.add(itens);
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter dados de funcionamento", e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				
				if (ps != null)
					ps.close();
				
				if (rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				logger.error("Erro ao fechar conexões", e);
			}
		}
		
		return listDadosRelatorio;
	}

}
