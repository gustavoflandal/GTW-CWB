
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para busca de status do local.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.12 $ $Date: 2009/03/12 13:07:37 $ $Author: raoni $
 */
public class LocalStatusCompleto {

	private Integer idLocal;
	private Integer serieEquipamento;
	private String nome;
	private Integer statusConexao;
	private Integer statusDIV;
	private Integer statusEnergia;
	private String IP;
	private String ultimaDeteccao;
	private String versao;
	private String tempoConectado;
	private String tempoDesconectado;
	private Integer tempoExecutando;
	private String statusCopia;
	private Boolean veiculoIrregular;
	private Double posicaoLat;
	private Double posicaoLon;
	
	private LocalStatusCompleto() {
		super();
	}

	
	/**
	 * Busca locais vegentes no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: nome, grupo.
	 * @param iOrdem Número da coluna de ordem.
	 * @return Lista de objetos Local
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<LocalStatusCompleto> buscaLocalStatusCompletoPorLatLng(double latMin, 
			                                                    double latMax, 
			                                                    double lngMin, 
			                                                    double lngMax,
			                                                    String timestamp,
			                                                    boolean isGetEveryone) throws ConexaoException {
		List<LocalStatusCompleto> lRet = new ArrayList<LocalStatusCompleto>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT\n");
		sbSQL.append("	id_local,\n");
		sbSQL.append("	serie_equipamento,\n");
		sbSQL.append("	nome,\n");
		sbSQL.append("	status_conexao,\n");
		sbSQL.append("	status_energia,\n");
		sbSQL.append("	status_DIV,\n");
		sbSQL.append("	ip,\n");
		sbSQL.append("	ultima_deteccao,\n");
		sbSQL.append("	versao,\n");
		sbSQL.append("	tempo_conectado,\n");
		sbSQL.append("	tempo_desconectado,\n");
		sbSQL.append("	tempo_executando,\n");
		sbSQL.append("	status_copia,\n");
		sbSQL.append("	veiculo_irregular,\n");
		sbSQL.append("	posicao_lat,\n");
		sbSQL.append("	posicao_lon\n");
		sbSQL.append("FROM\n");
		sbSQL.append("	local_status_tempo_real\n");
		sbSQL.append("WHERE\n");
		sbSQL.append("	(posicao_lat BETWEEN "+latMin+" AND "+latMax+") AND\n");
		sbSQL.append("	(posicao_lon BETWEEN "+lngMin+" AND "+lngMax+") AND\n");
		sbSQL.append("	data_inicio_operacao <= GETDATE()\n");
		if (!isGetEveryone){
			sbSQL.append("AND\n	data_atualizacao >= '"+timestamp+"'");
		}
		
		//System.out.println(sbSQL.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Ajustando os valores dos parametros para os wheres:
			// Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			
			rs = ps.executeQuery();
			LocalStatusCompleto lsc = null;
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			while (rs.next()) {
				lsc = new LocalStatusCompleto();
				lsc.setIdLocal(rs.getInt("id_local"));
				lsc.setSerieEquipamento(rs.getInt("serie_equipamento"));
				lsc.setNome(rs.getString("nome"));
				lsc.setStatusConexao(rs.getInt("status_conexao"));
				lsc.setStatusEnergia(rs.getInt("status_energia"));
				lsc.setStatusDIV(rs.getInt("status_DIV"));
				lsc.setIP(rs.getString("ip"));
				
				Timestamp ultimaDetectcao = rs.getTimestamp("ultima_deteccao");
				lsc.setUltimaDeteccao(rs.wasNull() ? null : fmt.format(ultimaDetectcao));
				
				lsc.setVersao(rs.getString("versao"));
				lsc.setTempoConectado(rs.getString("tempo_conectado"));
				lsc.setTempoDesconectado(rs.getString("tempo_desconectado"));
				lsc.setTempoExecutando(rs.getInt("tempo_executando"));
				lsc.setStatusCopia(rs.getString("status_copia"));
				lsc.setVeiculoIrregular("t".compareToIgnoreCase(rs.getString("veiculo_irregular")) == 0 ? true : false );
				lsc.setPosicaoLat(rs.getDouble("posicao_lat"));
				lsc.setPosicaoLon(rs.getDouble("posicao_lon"));
				lRet.add(lsc);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
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
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return lRet;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idLocal == null) ? 0 : idLocal.hashCode());
		result = prime * result
				+ ((statusConexao == null) ? 0 : statusConexao.hashCode());
		result = prime
				* result
				+ ((veiculoIrregular == null) ? 0 : veiculoIrregular.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalStatusCompleto other = (LocalStatusCompleto) obj;
		if (idLocal == null) {
			if (other.idLocal != null)
				return false;
		} else if (!idLocal.equals(other.idLocal))
			return false;
		if (statusConexao == null) {
			if (other.statusConexao != null)
				return false;
		} else if (!statusConexao.equals(other.statusConexao))
			return false;
		if (veiculoIrregular == null) {
			if (other.veiculoIrregular != null)
				return false;
		} else if (!veiculoIrregular.equals(other.veiculoIrregular))
			return false;
		return true;
	}


	public Integer getIdLocal() {
		return idLocal;
	}


	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}


	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}


	public void setSerieEquipamento(Integer serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Integer getStatusConexao() {
		return statusConexao;
	}


	public void setStatusConexao(Integer statusConexao) {
		this.statusConexao = statusConexao;
	}


	public Integer getStatusDIV() {
		return statusDIV;
	}


	public void setStatusDIV(Integer statusDIV) {
		this.statusDIV = statusDIV;
	}


	public Integer getStatusEnergia() {
		return statusEnergia;
	}


	public void setStatusEnergia(Integer statusEnergia) {
		this.statusEnergia = statusEnergia;
	}


	public String getIP() {
		return IP;
	}


	public void setIP(String ip) {
		IP = ip;
	}


	public String getUltimaDeteccao() {
		return ultimaDeteccao;
	}


	public void setUltimaDeteccao(String ultimaDeteccao) {
		this.ultimaDeteccao = ultimaDeteccao;
	}


	public String getVersao() {
		return versao;
	}


	public void setVersao(String versao) {
		this.versao = versao;
	}


	public String getTempoConectado() {
		return tempoConectado;
	}


	public void setTempoConectado(String tempoConectado) {
		this.tempoConectado = tempoConectado;
	}


	public String getTempoDesconectado() {
		return tempoDesconectado;
	}


	public void setTempoDesconectado(String tempoDesconectado) {
		this.tempoDesconectado = tempoDesconectado;
	}


	public Integer getTempoExecutando() {
		return tempoExecutando;
	}


	public void setTempoExecutando(Integer i) {
		this.tempoExecutando = i;
	}


	public String getStatusCopia() {
		return statusCopia;
	}


	public void setStatusCopia(String statusCopia) {
		this.statusCopia = statusCopia;
	}


	public Double getPosicaoLat() {
		return posicaoLat;
	}


	public void setPosicaoLat(Double posicaoLat) {
		this.posicaoLat = posicaoLat;
	}


	public Double getPosicaoLon() {
		return posicaoLon;
	}


	public void setPosicaoLon(Double posicaoLon) {
		this.posicaoLon = posicaoLon;
	}


	public void setVeiculoIrregular(Boolean veiculoIrregular) {
		this.veiculoIrregular = veiculoIrregular;
	}


	public Boolean getVeiculoIrregular() {
		return veiculoIrregular;
	}

	
}
