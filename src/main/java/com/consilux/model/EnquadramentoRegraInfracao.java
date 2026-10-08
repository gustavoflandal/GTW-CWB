package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class EnquadramentoRegraInfracao {
	
	private Integer idEnquadramentoRegraInfracao;
	private String descApait;
	private String tipoApait;
	
	/**
	* Luiz Fernando Amaral
	* Constrói o objeto EnquadramentoRegraInfracao  - Apenas descrição
	* @param descrição
	*/
	private EnquadramentoRegraInfracao(String descricao) {
		super();
		this.descApait = descricao;
	}
	
	public EnquadramentoRegraInfracao() {}
	
	
	
	//public static Map<Integer, String> TiposAPAIT = ObterTiposAPAITHarCode();
	
	private static Map<Integer, String> i_TiposAPAIT = null;

	public static Map<Integer, String> ObterTiposAPAIT()  {
		if (i_TiposAPAIT == null)
			i_TiposAPAIT = i_ObterTiposAPAIT();
		
		return i_TiposAPAIT;
	}
	
	private static Map<Integer, String> i_ObterTiposAPAIT()  {
		
		Map<Integer, String> lRet = new HashMap<Integer, String>();

		String sbSQL = "SELECT id_enquadramento, tipo_apait FROM enquadramento_regra_infracao (NOLOCK) WHERE id_enquadramento > 1";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL);
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.put(rs.getInt(1), rs.getString(2));
			}
		}
		catch(Exception e) {}
		finally {
			try {
			if (conn != null)
				conn.close();
			}
			catch(Exception e) {}
		}
		return lRet;
	}
	
//	public static Map<Integer, String> ObterTiposAPAITHarCode()  {
//		
//		Map<Integer, String> lRet = new HashMap<Integer, String>();
//		try {
//			lRet.put(60411 , "QD");
//			lRet.put(60503 , "QQ");
//			lRet.put(60412 , "QY");
//			lRet.put(56810 , "QT");
//			lRet.put(57030 , "QC");
//			lRet.put(56900 , "QX");
//			lRet.put(57463 , "QZ");
//			lRet.put(57461 , "QL");
//			lRet.put(56732 , "QW");
//			lRet.put(57462 , "QR");
//			lRet.put(74550 , "QV");
//			lRet.put(74710 , "QV");
//			lRet.put(74630 , "QV");
//			lRet.put(75870 , "QA");
//			lRet.put(99999 , "L4");
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		return lRet;
//	}

	/*-----------------------------------------------------------------------------------------------------------------
	Método: ObterDescTiposAPAIT   
	Descrição: Obter as descrições do APAIT para formar o movimento de Lote - Projeto São Paulo
	Criador: Luiz Fernando Martins do Amaral
	Data: 19/05/2014
	-------------------------------------------------------------------------------------------------------------------
	Alteração
	-------------------------------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------------------------*/
	public static Map<Integer, String> ObterDescTiposAPAIT() {
		
		Map<Integer, String> lRet = new HashMap<Integer, String>();

		String sbSQL = "SELECT id_enquadramento, " +
					   "descricao_apait " +
					   "FROM enquadramento_regra_infracao (NOLOCK) " +
					   "WHERE id_enquadramento > 1";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL);
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.put(rs.getInt(1), rs.getString(2));
			}
		}
		catch(Exception e) {}
		finally {
			try {
			if (conn != null)
				conn.close();
			}
			catch(Exception e) {}
		}
		return lRet;
	}
	
	/**
	 * Luiz Fernando Amaral
	 * Busca todos os nomes de Enquadramentos de Regra Infração no BD SEM CARATCTERES ESPECIAIS.
	 * @return Descrição Enquadramentos de Regra Infração.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<EnquadramentoRegraInfracao> buscaTodosEnquadraRegra() throws ConexaoException  {
		List<EnquadramentoRegraInfracao> lRet = new ArrayList<EnquadramentoRegraInfracao>();

		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT ");
		sbSQL.append(" REPLACE(replace(replace(replace(descricao_apait, 'Á','A'), 'Í', 'I'), 'Ç', 'C'), 'Ã', 'A') as descricao_apait ");
		sbSQL.append("FROM enquadramento_regra_infracao ");
		sbSQL.append("where id_enquadramento > 1 ");
		sbSQL.append("group by descricao_apait ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new EnquadramentoRegraInfracao(rs.getString("descricao_apait")));
			}

		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL - obtenção de buscaTodosEnquadraRegra", e);
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
				throw new ConexaoException("ERRO de SQL - obtenção de buscaTodosEnquadraRegra", e);
			}			
		}

		return lRet;
	}

	
	
	public Integer getIdEnquadramentoRegraInfracao() {
		return idEnquadramentoRegraInfracao;
	}


	public void setIdEnquadramentoRegraInfracao(Integer idEnquadramentoRegraInfracao) {
		this.idEnquadramentoRegraInfracao = idEnquadramentoRegraInfracao;
	}


	public String getDescApait() {
		return descApait;
	}

	public void setDescApait(String descApait) {
		this.descApait = descApait;
	}

	public String getTipoApait() {
		return tipoApait;
	}

	public void setTipoApait(String tipoApait) {
		this.tipoApait = tipoApait;
	}

}
