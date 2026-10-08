/**
 * Projeto: GTW
 * Nome do Modulo: GTW
 * Empresa: Consilux Tecnologia
 * @author Thiago Surgik
 * Data: 26/01/2016
 */

package com.consilux.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para busca de recursos dos contratos C006 e C008
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 02/02/2016
 */

public  class RecursosC006_C008 implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	private String tipo;
	private String serie;
	private Integer auto;
	private String placa;
	private Integer idDescarga;
	private String numeroAit;
	private String contrato;
	

	public RecursosC006_C008(String tipo, String serie, Integer auto, String placa, Integer idDescarga, String numeroAit, String contrato){
		super();
		this.tipo = tipo;
		this.serie = serie;
		this.auto = auto;
		this.placa = placa;
		this.idDescarga = idDescarga;
		this.numeroAit = numeroAit;
		this.contrato = contrato;
	}
	
	public RecursosC006_C008(){
		
	}


	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}


	public Integer getAuto() {
		return auto;
	}
	public void setAuto(Integer auto) {
		this.auto = auto;
	}


	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}


	public Integer getIdDescarga() {
		return idDescarga;
	}
	public void setIdDescarga(Integer idDescarga) {
		this.idDescarga = idDescarga;
	}


	public String getNumeroAit() {
		return numeroAit;
	}
	public void setNumeroAit(String numeroAit) {
		this.numeroAit = numeroAit;
	}


	public String getContrato() {
		return contrato;
	}
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	
	/**
	 * Autor: Thiago Surgik 02/02/2016
	 * Obter recurso.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */	
	public ArrayList<RecursosC006_C008> obterRecurso(String tipoRemessa, String serie, String autos, String placa) throws ConexaoException, SQLException {

		ArrayList<RecursosC006_C008> listRecursos = new ArrayList<RecursosC006_C008>();
		StringBuilder sbSQL = new StringBuilder();
		String sbSqlWhere = " WHERE ";
		
		sbSQL.append(" SELECT TOP 1000 ");
		sbSQL.append("    	  rc.tipo ");
		sbSQL.append("    	 ,rc.serie ");
		sbSQL.append("    	 ,rc.auto ");
		sbSQL.append("    	 ,rc.placa ");
		sbSQL.append("    	 ,rc.id_descarga ");
		sbSQL.append("    	 ,rc.numero_ait ");
		sbSQL.append("    	 ,rc.contrato ");
		sbSQL.append(" FROM   recursos_c006_c008 rc ");
		
		if (tipoRemessa != null && !(tipoRemessa.trim() == "")) {
			sbSQL.append(sbSqlWhere + " rc.tipo = '" + tipoRemessa.trim() + "' ");
			sbSqlWhere = " AND ";
		}
		if (serie != null && !(serie == "")) {
			sbSQL.append(sbSqlWhere + " rc.serie = '" + serie.trim() + "' ");
			sbSqlWhere = " AND ";
		}
		if (autos != null && !(autos == "")) {
			sbSQL.append(sbSqlWhere + " rc.auto IN (" + autos + ") ");
			sbSqlWhere = " AND ";
		}
		if (placa != null && !(placa == "")) {
			sbSQL.append(sbSqlWhere + " rc.placa = '" + placa.trim() + "' ");
			sbSqlWhere = " AND ";
		}
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  rc.tipo ");
		sbSQL.append(" 		 ,rc.serie ");
		sbSQL.append(" 		 ,rc.auto ");
		sbSQL.append(" 		 ,rc.id_descarga ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				RecursosC006_C008 recurso = new RecursosC006_C008(
						rs.getString("tipo"),
						rs.getString("serie"),
						rs.getInt("auto"),
						rs.getString("placa"),
						rs.getInt("id_descarga"),
						rs.getString("numero_ait"),
						rs.getString("contrato")
				);
				
				listRecursos.add(recurso);
				
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		
		return listRecursos;
	}

}
