/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 19/04/2010

  Descricao: Classe Retorna um relatório para a quantidade de imagens processadas por operador


 *********************************************************************************/
package com.consilux.model.relatorio;

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
 * @author Edson Jan F Lopes- Consilux Tecnologia
 */
public class RelatorioProdutividadeDetalhado {
	
	
	private Integer hora;
	private Integer minuto;
	private Integer idEnquadramento;
	private String processo;
	private String usuario;
	private String ip;
	private Integer qtdImagens;
	private Integer mediaTempoSistema;
	private Integer mediaTempoUsuario;
	private Integer maiorTempoUsuario;
	private Integer menorTempoUsuario;
	
	/**
	 * @param hora
	 * @param minuto
	 * @param idEnquadramento
	 * @param processo
	 * @param usuario
	 * @param ip
	 * @param qtdImagens
	 * @param mediaTempoSistema
	 * @param mediaTempoUsuario
	 * @param maiorTempoUsuario
	 * @param menorTempoUsuario
	 */
	public RelatorioProdutividadeDetalhado(Integer hora, Integer minuto,
			Integer idEnquadramento, String processo, String usuario,
			String ip, Integer qtdImagens, Integer mediaTempoSistema,
			Integer mediaTempoUsuario, Integer maiorTempoUsuario,
			Integer menorTempoUsuario) {
		super();
		this.hora = hora;
		this.minuto = minuto;
		this.idEnquadramento = idEnquadramento;
		this.processo = processo;
		this.usuario = usuario;
		this.ip = ip;
		this.qtdImagens = qtdImagens;
		this.mediaTempoSistema = mediaTempoSistema;
		this.mediaTempoUsuario = mediaTempoUsuario;
		this.maiorTempoUsuario = maiorTempoUsuario;
		this.menorTempoUsuario = menorTempoUsuario;
	}

	/**
	 * Busca a quantidade de infrações em cada processo.
	 * @return Relatório de processamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<RelatorioProdutividadeDetalhado> buscaRelatorio(Date dataBase, Integer idUsuario) throws ConexaoException, SQLException {

		List<RelatorioProdutividadeDetalhado> lRet = new ArrayList<RelatorioProdutividadeDetalhado>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("SELECT");
		sbSQL.append("	DATEPART(hour,ip.data) AS hora,");
		sbSQL.append("	DATEPART(minute,ip.data) AS minuto,");
		sbSQL.append("	i.id_enquadramento,");
		sbSQL.append("	p.nome as processo,");
		sbSQL.append("	u.usuario as usuario,");
		sbSQL.append("	ll.maquina as IP,");
		sbSQL.append("	COUNT(ip.id_infracao_processo) as qtd_imagens,");
		sbSQL.append("	AVG(tempo-tempo_cliente) as media_tempo_sistema,");
		sbSQL.append("	AVG(tempo_cliente) as media_tempo_usuario,");
		sbSQL.append("	MAX(tempo_cliente) as maior_tempo_usuario,");
		sbSQL.append("	MIN(tempo_cliente) as menor_tempo_usuario ");
		sbSQL.append("FROM ");
		sbSQL.append("	infracao_processo ip WITH (NOLOCK)");
		sbSQL.append("	JOIN infracao i WITH (NOLOCK)");
		sbSQL.append("		ON i.id_infracao = ip.id_infracao");
		sbSQL.append("	JOIN processo p WITH (NOLOCK)");
		sbSQL.append("		ON p.id_processo = ip.id_processo");
		sbSQL.append("	JOIN sis_usuario u WITH (NOLOCK)");
		sbSQL.append("		ON u.id_usuario = ip.id_usuario");
		sbSQL.append("	JOIN sis_logon_logoff_usuario ll ON"); 
		sbSQL.append("		ll.id_logon = (SELECT MAX(id_logon) from sis_logon_logoff_usuario ");
		sbSQL.append("						WHERE id_usuario = ip.id_usuario AND data_logon < ip.data"); 
		sbSQL.append("						AND (data_logoff IS NULL OR data_logoff > ip.data)) ");
		sbSQL.append("WHERE");
		sbSQL.append("	ip.data > ? AND ip.tempo > 0");
		sbSQL.append(" 	"+(idUsuario != null ? "AND ip.id_usuario = ? " : " "));
		sbSQL.append("GROUP BY");
		sbSQL.append("	DATEPART(hour,ip.data),");
		sbSQL.append("	DATEPART(minute,ip.data),");
		sbSQL.append("	i.id_enquadramento,");
		sbSQL.append("	p.nome,");
		sbSQL.append("	u.usuario,");
		sbSQL.append("	ll.maquina ");
		sbSQL.append("ORDER BY DATEPART(hour,ip.data) desc, DATEPART(minute,ip.data) desc, COUNT(ip.id_infracao_processo)");
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, new Timestamp(dataBase.getTime()));
			
			if (idUsuario != null)
				ps.setInt(2, idUsuario);
				
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new RelatorioProdutividadeDetalhado(
							rs.getInt("hora"),
							rs.getInt("minuto"),
							rs.getInt("id_enquadramento"),
							rs.getString("processo"),
							rs.getString("usuario"),
							rs.getString("ip"),
							rs.getInt("qtd_imagens"),
							rs.getInt("media_tempo_sistema"),
							rs.getInt("media_tempo_usuario"),
							rs.getInt("maior_tempo_usuario"),
							rs.getInt("menor_tempo_usuario")
						));
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return lRet;
	}

	/**
	 * @return the hora
	 */
	public Integer getHora() {
		return hora;
	}

	/**
	 * @return the minuto
	 */
	public Integer getMinuto() {
		return minuto;
	}

	/**
	 * @return the idEnquadramento
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return the processo
	 */
	public String getProcesso() {
		return processo;
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the qtdImagens
	 */
	public Integer getQtdImagens() {
		return qtdImagens;
	}

	/**
	 * @return the mediaTempoSistema
	 */
	public Integer getMediaTempoSistema() {
		return mediaTempoSistema;
	}

	/**
	 * @return the mediaTempoUsuario
	 */
	public Integer getMediaTempoUsuario() {
		return mediaTempoUsuario;
	}

	/**
	 * @return the maiorTempoUsuario
	 */
	public Integer getMaiorTempoUsuario() {
		return maiorTempoUsuario;
	}

	/**
	 * @return the menorTempoUsuario
	 */
	public Integer getMenorTempoUsuario() {
		return menorTempoUsuario;
	}

}
