/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Edson Jan Ferreira Lopes
  Data: 14/04/2010

  Descricao: Classe Retorna um relatório para a quantidade de infrações em processamento em processos que não sejam finais (remessas, Remessa de Inconsistencia).


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Processamento.EtapaProcesso;

/**
 * Classe para buscar infrações em processos não finais
 * @author Edson Jan F Lopes- Consilux Tecnologia
 */
public class RelatorioInfracoesNaRemessa {
	
	
	private int idEnquadramento;
	private String nome;
	private Date data;
	private int total;
	private String espera;
	private Integer idInconsistencia;
	private String descricaoInconsistencia;
	
	/**
	 * Busca a quantidade de infrações em cada processo.
	 * @return Relatório de processamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public List<RelatorioInfracoesNaRemessa> buscaRelatorio() throws ConexaoException, SQLException {
		//Map<String, TipoRemessa> mapaTipos = ConfiguracaoProvider.getInstance().getConfiguracaoRemessa().getMapaTipos();
		
//		List<Enquadramento> enquadramentos = Enquadramento.buscaEnquadramentoDisponivelPorIdProcesso(EtapaProcesso.REMESSA_VALIDADA.getId());
		
//		List<String> ids = new ArrayList<String>();
//		
//	    for (Map.Entry<String, TipoRemessa> e: mapaTipos.entrySet()) {
//	    	ids.add(String.valueOf(((TipoRemessa)e.getValue()).getIdProcesso()));
//	    }
		
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append(" SELECT ");
//		sbSQL.append(" 		p.id_processo, ");
		sbSQL.append("      i.id_enquadramento, ");
//		sbSQL.append(" 		p.nome, ");
		sbSQL.append("      e.descricao, ");
		sbSQL.append(" 		CAST( data as Date ) as Data, ");
		sbSQL.append(" 		COUNT(*) as total ");
		sbSQL.append(" FROM  ");
		sbSQL.append(" 		infracao i  ");
		sbSQL.append(" 		left join infracao_remessa ir on ir.id_infracao = i.id_infracao ");
//		sbSQL.append(" 		join processo p on p.id_processo = i.id_processo ");
		sbSQL.append("      join enquadramento e on e.id_enquadramento = i.id_enquadramento ");
		sbSQL.append(" WHERE ");
		sbSQL.append("     ir.id_infracao is null ");
		sbSQL.append("     AND i.id_processo = ? ");
//		sbSQL.append("     and p.id_processo in ( " + Funcoes.concatStringArray(ids, ",") + " ) ");
		sbSQL.append(" GROUP BY  ");
//		sbSQL.append("     p.id_processo, ");
		sbSQL.append("      i.id_enquadramento, ");
//		sbSQL.append("     p.nome, ");
		sbSQL.append("      e.descricao, ");
		sbSQL.append("     CAST( data as Date ) ");
		sbSQL.append(" ORDER BY 1, 3");
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		List<RelatorioInfracoesNaRemessa> lrip = new ArrayList<RelatorioInfracoesNaRemessa>();
		RelatorioInfracoesNaRemessa rip;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, EtapaProcesso.REMESSA_GERAL.getId());

			rs = ps.executeQuery();
			while (rs.next()){
				rip = new RelatorioInfracoesNaRemessa();
				rip.setIdEnquadramento(rs.getInt("id_enquadramento")); 
				rip.setNome(rs.getString("descricao"));
				rip.setData(new Date(rs.getDate("Data").getTime()));
				rip.setTotal(rs.getInt("total"));
				lrip.add(rip);
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return lrip;
	}

	
	/**
	 * Busca a quantidade de infrações em cada regra X inconsistencia.
	 * @return Relatório de processamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public List<RelatorioInfracoesNaRemessa> buscaRelatorioInconsistencia() throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append(" SELECT id_enquadramento, ");
		sbSQL.append("        descricao, ");
		sbSQL.append("        id_inconsistencia, ");
		sbSQL.append("        descricao_inconsistencia, ");
		sbSQL.append(" 		  Data, ");
		sbSQL.append(" 		  total ");
		sbSQL.append(" FROM   v_infracao_enquadramento_inconsistencia ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("        id_inconsistencia DESC, ");
		sbSQL.append("        id_enquadramento, ");
		sbSQL.append("        Data ");
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		List<RelatorioInfracoesNaRemessa> lrip = new ArrayList<RelatorioInfracoesNaRemessa>();
		RelatorioInfracoesNaRemessa rip;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			//ps.setInt(1, EtapaProcesso.REMESSA_GERAL.getId());

			rs = ps.executeQuery();
			while (rs.next()){
				rip = new RelatorioInfracoesNaRemessa();
				rip.setIdEnquadramento(rs.getInt("id_enquadramento")); 
				rip.setNome(rs.getString("descricao"));
				rip.setIdInconsistencia(rs.getInt("id_inconsistencia")); 
				rip.setDescricaoInconsistencia(rs.getString("descricao_inconsistencia"));
				rip.setData(new Date(rs.getDate("Data").getTime()));
				rip.setTotal(rs.getInt("total"));
				lrip.add(rip);
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return lrip;
	}

//	public int getIdProcesso() {
//		return idProcesso;
//	}
//	public void setIdProcesso(int idProcesso) {
//		this.idProcesso = idProcesso;
//	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
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

	public int getIdEnquadramento() {
		return idEnquadramento;
	}
	public void setIdEnquadramento(int idEnquadramento) {
		this.idEnquadramento = idEnquadramento;
	}
	
	public String getEspera() {
		return espera;
	}
	public void setEspera(String espera) {
		this.espera = espera;
	}

	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}
	public void setIdInconsistencia(Integer idInconsistencia) {
		this.idInconsistencia = idInconsistencia;
	}

	public String getDescricaoInconsistencia() {
		return descricaoInconsistencia;
	}
	public void setDescricaoInconsistencia(String descricaoInconsistencia) {
		this.descricaoInconsistencia = descricaoInconsistencia;
	}

	
}
