package com.consilux.model.relatorio.rj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class DadosRelatorioSinalizacao {
	
	private static final Logger logger = Logger.getLogger(DadosRelatorioSinalizacao.class);

	private long numeroEquipamento;
	private String codigoEquipamentoDER;
	private Date dataInicioOperacao;
	private HashMap<Integer, Date> datasVideos;
	
	public DadosRelatorioSinalizacao() {
		datasVideos = new HashMap<Integer, Date>();
	}
	
	public static List<DadosRelatorioSinalizacao> ObterItensRelatorio(boolean imagens) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ArrayList<DadosRelatorioSinalizacao> listDadosRelatorio =  new ArrayList<DadosRelatorioSinalizacao>();
		DadosRelatorioSinalizacao itens = null;
		
		
		try {
			
			StringBuilder sbSQL = new StringBuilder();

			sbSQL.append(" SELECT serie_equipamento,        ");
			sbSQL.append("        codigo_equipamento,       ");
			sbSQL.append("        data_inicio_operacao,     ");
			sbSQL.append("	      ano_mes,    		        ");
			sbSQL.append("	      data_video 			    ");
			if (imagens)
				sbSQL.append("FROM  v_locais_imagens_sinalizacao	");
			else
				sbSQL.append("FROM  v_locais_videos_sinalizacao	");
			sbSQL.append("ORDER BY 1, 4                     ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			long serie_equipamento;
			Integer ano_mes;
			while (rs.next()){
				
				serie_equipamento = rs.getLong(1);
				
				if (itens == null || itens.getNumeroEquipamento() != serie_equipamento) {
					itens = new DadosRelatorioSinalizacao();
					itens.setNumeroEquipamento(serie_equipamento);
					itens.setCodigoEquipamentoDER(rs.getString(2).trim());
					itens.setDataInicioOperacao(rs.getDate(3));
					listDadosRelatorio.add(itens);
				}
				ano_mes = rs.getInt(4);
				if (!rs.wasNull()) {
					itens.getDatasVideos().put(ano_mes, rs.getDate(5));
				}
				
				
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

	public long getNumeroEquipamento() {
		return numeroEquipamento;
	}

	public void setNumeroEquipamento(long numeroEquipamento) {
		this.numeroEquipamento = numeroEquipamento;
	}

	public String getCodigoEquipamentoDER() {
		return codigoEquipamentoDER;
	}

	public void setCodigoEquipamentoDER(String codigoEquipamentoDER) {
		this.codigoEquipamentoDER = codigoEquipamentoDER;
	}

	public Date getDataInicioOperacao() {
		return dataInicioOperacao;
	}

	public void setDataInicioOperacao(Date dataInicioOperacao) {
		this.dataInicioOperacao = dataInicioOperacao;
	}

	public HashMap<Integer, Date> getDatasVideos() {
		return datasVideos;
	}

	public void setDatasVideos(HashMap<Integer, Date> datasVideos) {
		this.datasVideos = datasVideos;
	}
	
}
