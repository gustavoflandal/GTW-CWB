/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Edson Jan Ferreira Lopes
  Data: 14/04/2010

  Descricao: Classe Retorna um relatório para a quantidade de imagens processadas por operador


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe para buscar quantidade de imagens processadas por operador
 * 
 * @author Edson Jan F Lopes- Consilux Tecnologia
 */
public class RelatorioOperadores {

	private String processo;
	private String usuario;
	private Date data;
	private int total;

	/**
	 * Busca a quantidade de infrações em cada processo.
	 * 
	 * @return Relatório de processamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public List<RelatorioOperadores> buscaRelatorio(Date dataIni,
			Date dataFim, Integer idUsuario) throws ConexaoException,
			SQLException {

		StringBuilder sbSQL = new StringBuilder();

		String funcao;
		if (idUsuario != null)
			//  com id_usuario, data_inicial, data_final
			funcao = "fcn_getOperadores_Alt2(?, ?, ?)";
		else
			//  com data_inicial e data_final
			funcao = "fcn_getOperadores_Alt3(?, ?)";

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append(" SELECT ");
		sbSQL.append(" data as Data ");
		sbSQL.append(" 	,usuario ");
		sbSQL.append(" 	,processo ");
		sbSQL.append(" 	,total ");
		sbSQL.append(" FROM " + funcao);
		sbSQL.append(" ORDER BY id_processo, data DESC, total DESC, usuario ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<RelatorioOperadores> lrip = new ArrayList<RelatorioOperadores>();
		RelatorioOperadores rip;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			int t = 1;
			if (idUsuario != null) {
				ps.setInt(t++, idUsuario);
			}
			ps.setTimestamp(t++, new Timestamp(dataIni.getTime()));
			ps.setTimestamp(t++, new Timestamp(dataFim.getTime()));

			rs = ps.executeQuery();
			while (rs.next()) {
				rip = new RelatorioOperadores();
				rip.setData(new Date(rs.getDate("data").getTime()));
				rip.setUsuario(rs.getString("usuario"));
				rip.setProcesso(rs.getString("processo"));
				rip.setTotal(rs.getInt("total"));
				lrip.add(rip);
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return lrip;
	}

	public List<RelatorioOperadores> buscaRelatorio(int idUsuario)
			throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append(" SELECT ");
		sbSQL.append(" data as data ");
		sbSQL.append(" 	,usuario ");
		sbSQL.append(" 	,processo ");
		sbSQL.append(" 	,total ");
		//  com id_usuario e data_inicial
		sbSQL.append(" FROM fcn_getOperadores_Alt1(?, ?) ");
		sbSQL.append(" ORDER BY id_processo, data DESC, total DESC, usuario ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<RelatorioOperadores> lrip = new ArrayList<RelatorioOperadores>();
		RelatorioOperadores rip;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, idUsuario);
			ps.setTimestamp(2, new Timestamp(new Date().getTime() - 259200000));

			rs = ps.executeQuery();
			while (rs.next()) {
				rip = new RelatorioOperadores();
				rip.setData(new Date(rs.getDate("Data").getTime()));
				rip.setUsuario(rs.getString("Usuario"));
				rip.setProcesso(rs.getString("Processo"));
				rip.setTotal(rs.getInt("Total"));
				lrip.add(rip);
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return lrip;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
