package com.consilux.model.relatorio.rj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class DadosRelatorioFuncionamento {

	private static final Logger logger = Logger.getLogger(DadosRelatorioFuncionamento.class);
	
	public static List<ItemRelatorioFuncionamento> ObterItensRelatorioFuncionamento(Date data_ini, Date data_fim) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		ArrayList<ItemRelatorioFuncionamento> listDadosRelatorio =  new ArrayList<ItemRelatorioFuncionamento>();
		ItemRelatorioFuncionamento itens;
		Integer[] celulasHorasFuncionamento;
		String[] celulasColunasRelatorio;
		
		
		try {
			
			StringBuilder sbSQL = new StringBuilder();

			sbSQL.append(" EXEC dbo.spu_getRelatorioFuncionamento ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp( data_ini.getTime() ));
			ps.setTimestamp(2, new java.sql.Timestamp( data_fim.getTime() ));
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			Integer columnCount = (rsmd.getColumnCount() - 4);
			
			celulasColunasRelatorio = new String[columnCount];
			String colunaRelatorio = "";
			
			for (int i = 1; i <= columnCount; i++ ) {
				
				int c = i+3;
				
				colunaRelatorio = rsmd.getColumnName(c);
				celulasColunasRelatorio[i-1] = colunaRelatorio;
				
			}
			
			while (rs.next()){
				
				celulasHorasFuncionamento = new Integer[columnCount];
				
				Integer valorHorasFuncionamento = 0;
				
				for (int i = 1; i <= celulasColunasRelatorio.length; i++) {
					
					String dia = celulasColunasRelatorio[i-1];
					
					valorHorasFuncionamento = (int) rs.getInt(dia);
					celulasHorasFuncionamento[i-1] = Integer.valueOf(valorHorasFuncionamento);
				}
				
				itens = new ItemRelatorioFuncionamento(rs.getLong("serie_equipamento"),
													   rs.getString("codigo_equipamento"),
													   rs.getDate("data_inicio_operacao"),
													   celulasColunasRelatorio,
													   celulasHorasFuncionamento,
													   rs.getDouble("aproveitamento"),
													   columnCount);
				
				listDadosRelatorio.add(itens);
			}
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter dados de funcionamento", e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
				
				if (ps != null)
					ps.close();
				
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {
				logger.error("Erro ao fechar conexões", e);
			}
		}
		
		return listDadosRelatorio;
	}
	
}
