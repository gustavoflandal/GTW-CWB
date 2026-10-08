package com.consilux.model.ferramenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**********************************************************************************
Projeto: GTW
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Thiago Surgik
Data: 03/12/2015
*********************************************************************************/

/**
* Classe de negócio para abstrair Grupo de Evento Manual
* @author Thiago Surgik - Consilux Tecnologia
* Data: 03/12/2015
*/
public  class EventoManualGrupo {
	
	public enum TipoEventoManual {
		INICIO(1),
		RETORNO(2);
	
		private Integer id;
		TipoEventoManual(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static TipoEventoManual valueOfId(Integer id) {
			for (TipoEventoManual tipo: values()) {
				if (tipo.getId() == id.intValue())
					return tipo;
			}
			return null;
		}
	}
	
	private Integer idEventoManualCategoria;
	private String descricao;
	
	public EventoManualGrupo(Integer idEventoManualCategoria, String descricao) {

		this.idEventoManualCategoria = idEventoManualCategoria;
		this.descricao = descricao;
	}
	
	public static List<EventoManualGrupo> listarEventoManualGrupoPorId(Integer idEventoManualCategoria) throws ConexaoException {
		
		List<EventoManualGrupo> lRet = new ArrayList<EventoManualGrupo>();
		StringBuilder sbSqlQuery = new StringBuilder();
		
		sbSqlQuery.append(" SELECT id_evento_manual_categoria, ");
		sbSqlQuery.append(" 	   descricao ");
		sbSqlQuery.append(" FROM   cad_evento_manual_categoria ");
		sbSqlQuery.append(" WHERE  id_evento_manual_categoria = ? ");
		sbSqlQuery.append(" ORDER BY ");
		sbSqlQuery.append(" 	   descricao ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSqlQuery.toString());
			ps.setInt(1, idEventoManualCategoria);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				lRet.add(new EventoManualGrupo(rs.getInt(1), rs.getString(2)));
			}
			
			rs.close();
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
			
		} finally {
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
	
		return lRet;
	}
	
	
	public Integer getIdEventoManualCategoria() {
		return idEventoManualCategoria;
	}
	public void setIdEventoManualCategoria(Integer idEventoManualCategoria) {
		this.idEventoManualCategoria = idEventoManualCategoria;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
