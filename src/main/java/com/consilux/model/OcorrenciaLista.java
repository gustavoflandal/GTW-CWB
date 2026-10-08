/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 23/03/2009

  Descricao: Classe de negócio para busca de lista de informações da um veículo.

  Historico:

    $Log: VeiculoCompletoLista.java,v $
    Revision 1.2  2009/05/08 18:56:36  fos
    Acertado a forma de extrati o bit ligado no flag do veículo.

    Revision 1.1  2009/03/24 21:38:24  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para busca de lista de ocorrências.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since: 29/04/2015
 */
public class OcorrenciaLista {
	private Integer idOcorrencia;
	private Integer idLocal;
	private String nomeLocal;
	private Integer serieEquipamento;
	private Date dataHora;
	private Integer codPista;
	private String motivoResumido;
	private String nomeArquivo;
	private String caminhoArquivo;
	private String usuarioCadastro;
	private Date dataCadastro;
	private String tipoMime;
	private Integer numeroOficio;
	private Integer anoOficio;
	private String estado;
	

	private OcorrenciaLista(Integer idOcorrencia, Integer idLocal, String nomeLocal, Integer serieEquipamento, Date dataHora, Integer codPista,
							String motivoResumido, String nomeArquivo, String caminhoArquivo, String usuarioCadastro, Date dataCadastro,
							Integer numeroOficio, Integer anoOficio, String estado) {
		super();
		this.idOcorrencia = idOcorrencia;
		this.idLocal = idLocal;
		this.nomeLocal = nomeLocal != null ? nomeLocal.trim() : "";
		this.serieEquipamento = serieEquipamento;
		this.dataHora = dataHora;
		this.codPista = codPista;
		this.motivoResumido = motivoResumido != null ? motivoResumido.trim() : "";
		this.nomeArquivo = nomeArquivo != null ? nomeArquivo.trim() : "";
		this.caminhoArquivo = caminhoArquivo != null ? caminhoArquivo.trim() : "";
		this.usuarioCadastro = usuarioCadastro != null ? usuarioCadastro.trim() : "";
		this.dataCadastro = dataCadastro;
		this.numeroOficio = numeroOficio;
		this.anoOficio = anoOficio;
		this.estado = estado != null ? estado.trim() : "";
		
	}
	
	private OcorrenciaLista(Integer idOcorrencia, String nomeArquivo, String caminhoArquivo, String usuarioCadastro, Date dataCadastro, String tipoMime) {
		super();
		this.idOcorrencia = idOcorrencia;
		this.nomeArquivo = nomeArquivo != null ? nomeArquivo.trim() : "";
		this.caminhoArquivo = caminhoArquivo != null ? caminhoArquivo.trim() : "";
		this.usuarioCadastro = usuarioCadastro != null ? usuarioCadastro.trim() : "";
		this.dataCadastro = dataCadastro;
		this.tipoMime = tipoMime;

}


	/**
	 * Busca ocorrências no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: 
	 * data_ini, data_fim, local.
	 * @return Lista de objetos OcorrenciaLista
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ParseException 
	 */
	public static List<OcorrenciaLista> buscaOcorrenciaPor( Map<String,Object> mFiltros ) throws ConexaoException, SQLException, ParseException {

		List<OcorrenciaLista> lRet = new ArrayList<OcorrenciaLista>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT o.id_ocorrencia ");
		sbSQL.append(" 		 ,o.id_local ");
		sbSQL.append(" 		 ,cem.descricao AS nome_local ");
		sbSQL.append(" 		 ,o.serie_equipamento ");
		sbSQL.append(" 		 ,o.data_hora ");
		sbSQL.append(" 		 ,o.cod_pista ");
		sbSQL.append(" 		 ,o.motivo_resumido ");
		sbSQL.append(" 		 ,o.nome_arquivo ");
		sbSQL.append(" 		 ,o.caminho_arquivo ");
		sbSQL.append(" 		 ,o.usuario_cadastro ");
		sbSQL.append(" 		 ,o.data_cadastro ");
		sbSQL.append(" 		 ,o.numero_oficio ");
		sbSQL.append(" 		 ,o.ano_oficio ");
		sbSQL.append(" 		 ,o.estado ");
		sbSQL.append(" FROM   ocorrencia o (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN configuracao_equipamento_medicao cem (NOLOCK) ");
		sbSQL.append(" 			  ON  cem.id_local = o.id_local ");
		
		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		
		if (!mFiltros.isEmpty()) {
			
			sbSQL.append(" WHERE ");
			
			if (mFiltros.get("id_local") != null) {
				mRegras.put("id_local", "cem.cod_pista = ?");
			}
			if (mFiltros.get("data_ocorrencia_ini") != null) {
				mRegras.put("data_ocorrencia_ini", "o.data_hora >= ?");
			}
			if (mFiltros.get("data_ocorrencia_fim") != null) {
				mRegras.put("data_ocorrencia_fim", "o.data_hora <= ?");
			}
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			if (!mFiltros.isEmpty()) {
				sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			}
			
			sbSQL.append(" GROUP BY ");
			sbSQL.append(" 		  o.id_ocorrencia ");
			sbSQL.append(" 		 ,o.id_local ");
			sbSQL.append(" 		 ,cem.descricao ");
			sbSQL.append(" 		 ,o.serie_equipamento ");
			sbSQL.append(" 		 ,o.data_hora ");
			sbSQL.append(" 		 ,o.cod_pista ");
			sbSQL.append(" 		 ,o.motivo_resumido ");
			sbSQL.append(" 		 ,o.nome_arquivo ");
			sbSQL.append(" 		 ,o.caminho_arquivo ");
			sbSQL.append(" 		 ,o.usuario_cadastro ");
			sbSQL.append(" 		 ,o.data_cadastro ");
			sbSQL.append(" 		 ,o.numero_oficio ");
			sbSQL.append(" 		 ,o.ano_oficio ");
			sbSQL.append(" 		 ,o.estado ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  o.data_hora; ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			if (!mFiltros.isEmpty()) {
				Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			}
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new OcorrenciaLista(
						rs.getInt("id_ocorrencia"),
						rs.getInt("id_local"),
						rs.getString("nome_local"),
						rs.getInt("serie_equipamento"),
						rs.getTimestamp("data_hora"),
						rs.getInt("cod_pista"),
						rs.getString("motivo_resumido"),
						rs.getString("nome_arquivo"),
						rs.getString("caminho_arquivo"),
						rs.getString("usuario_cadastro"),
						rs.getTimestamp("data_cadastro"),
						rs.getInt("numero_oficio"),
						rs.getInt("ano_oficio"),
						rs.getString("estado")
				)
				);
			}
		} catch (Exception e) {
			throw new SQLException("Não foi possível consultar os dados.", e);
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
	
	public static OcorrenciaLista buscaOcorrenciaPorId( Integer intIdOcorrencia ) throws ConexaoException, SQLException, ParseException {

		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT o.id_ocorrencia ");
		sbSQL.append(" 		 ,o.data_hora ");
		sbSQL.append(" 		 ,o.nome_arquivo ");
		sbSQL.append(" 		 ,o.caminho_arquivo ");
		sbSQL.append(" 		 ,o.usuario_cadastro ");
		sbSQL.append(" 		 ,o.data_cadastro ");
		sbSQL.append(" 		 ,o.tipo_mime ");
		sbSQL.append(" FROM   ocorrencia o (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN configuracao_equipamento_medicao cem (NOLOCK) ");
		sbSQL.append(" 			  ON  cem.id_local = o.id_local ");
		sbSQL.append(" WHERE  o.id_ocorrencia = " + intIdOcorrencia.toString());
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  o.id_ocorrencia ");
		sbSQL.append(" 		 ,o.data_hora ");
		sbSQL.append(" 		 ,o.nome_arquivo ");
		sbSQL.append(" 		 ,o.caminho_arquivo ");
		sbSQL.append(" 		 ,o.usuario_cadastro ");
		sbSQL.append(" 		 ,o.data_cadastro ");
		sbSQL.append(" 		 ,o.tipo_mime ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  o.id_ocorrencia; ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return new OcorrenciaLista(rs.getInt("id_ocorrencia"),
										   rs.getString("nome_arquivo"),
										   rs.getString("caminho_arquivo"),
										   rs.getString("usuario_cadastro"),
										   rs.getTimestamp("data_cadastro"),
										   rs.getString("tipo_mime")
				);
			}
			else {
				return null;
			}
			
		} catch (Exception e) {
			throw new SQLException("Não foi possível consultar os dados.", e);
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
	}

	public Integer getIdOcorrencia() {
		return idOcorrencia;
	}
	public void setIdOcorrencia(Integer idOcorrencia) {
		this.idOcorrencia = idOcorrencia;
	}

	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public String getNomeLocal() {
		return nomeLocal;
	}
	public void setNomeLocal(String nomeLocal) {
		this.nomeLocal = nomeLocal;
	}

	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}
	public void setSerieEquipamento(Integer serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Integer getCodPista() {
		return codPista;
	}
	public void setCodPista(Integer codPista) {
		this.codPista = codPista;
	}

	public String getMotivoResumido() {
		return motivoResumido;
	}
	public void setMotivoResumido(String motivoResumido) {
		this.motivoResumido = motivoResumido;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}
	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}

	public String getUsuarioCadastro() {
		return usuarioCadastro;
	}
	public void setUsuarioCadastro(String usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getTipoMime() {
		return tipoMime;
	}
	public void setTipoMime(String tipoMime) {
		this.tipoMime = tipoMime;
	}

	public Integer getNumeroOficio() {
		return numeroOficio;
	}
	public void setNumeroOficio(Integer numeroOficio) {
		this.numeroOficio = numeroOficio;
	}

	public Integer getAnoOficio() {
		return anoOficio;
	}
	public void setAnoOficio(Integer anoOficio) {
		this.anoOficio = anoOficio;
	}

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
}
