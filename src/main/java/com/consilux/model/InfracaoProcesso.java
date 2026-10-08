package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de informações da uma infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.10 $ $Date: 2009/05/08 18:53:07 $ $Author: fos $
 */
public class InfracaoProcesso {
	Integer id;
	Integer idInfracao;
	Integer idUsuario;
	String usuario;
	Integer idProcesso;
	String descricaoProcesso;
	Integer IdInconsistencia;
	String descricaoInconsistencia;
	Date data;
	Integer tempo;
	String placa;
	Integer IdMarcaCET;
	Integer IdMarca;
	Integer IdEspecie;
	Integer IdTipo;
	String uf;
	String marcaCET;
	String marca;
	String especie;
	String tipo;
	Integer x;
	Integer y;
	Integer altura;
	Integer largura;
	Integer idImagem;
	String descricaoStatus;
	Integer concluido;

	private static Logger logger = LogManager.getLogger(InfracaoProcesso.class);
	
	/**
	 * Constrói o objeto Infracao com os seus respectivos atributos.
	 * @param id Indentificador da Infração do Processo.
	 * @param idInfracao Indentificador da Infração.
	 * @param idUsuario Indentificador do Usuário.
	 * @param usuario Login do Usuário.
	 * @param idProcesso Identificador do Processo.
	 * @param descricaoProcesso Descrição do Processo.
	 * @param idInconsistencia Identificador da Inconsistência.
	 * @param idMarcaCET Identificador da Marca CET.
	 * @param idEspecie Identificador da Espécie.
	 * @param idTipo Identificador do tipo de veículo.
	 * @param uf Identificador da UF.
	 * @param descricaoInconsistencia Descrição da inconsistência.
	 * @param data Data do processamento.
	 * @param tempo Tempo gasto para o processamento.
	 * @param placa Placa digitada pelo usuário.
	 * @param marca Marca escolhida pelo usuário.
	 * @param especie Espécie escolhida pelo usuário.
	 * @param tipo Tipo do cadastro no momento do processamento.
	 * @param x Posição X da obliteração.
	 * @param y Posição Y da obliteração.
	 * @param altura Altura da obliteração.
	 * @param largura Largura da obliteração.
	 * @param idImagem Identificador da imagem selecionada.
	 * @param descricaoStatus Descrição do status do histórico.
	 */
	private InfracaoProcesso(Integer id, Integer idInfracao, Integer idUsuario,
			String usuario, Integer idProcesso, String descricaoProcesso,
			Integer idInconsistencia, String descricaoInconsistencia,
			Date data, Integer tempo, String placa, Integer idMarcaCET,
			Integer idMarca, Integer idEspecie, Integer idTipo, String uf, 
			String marcaCET, String marca, String especie, String tipo, Integer x, 
			Integer y, Integer largura, Integer altura, Integer idImagem, String descricaoStatus,
			Integer concluido) {
		super();
		this.id = id;
		this.idInfracao = idInfracao;
		this.idUsuario = idUsuario;
		this.usuario = usuario;
		this.idProcesso = idProcesso;
		this.descricaoProcesso = descricaoProcesso;
		this.IdInconsistencia = idInconsistencia;
		this.descricaoInconsistencia = descricaoInconsistencia;
		this.data = data;
		this.tempo = tempo;
		this.placa = placa;
		this.IdMarcaCET = idMarcaCET;
		this.IdMarca = idMarca;
		this.IdEspecie = idEspecie;
		this.IdTipo = idTipo;
		this.uf = uf;
		this.marcaCET = marcaCET;
		this.marca = marca;
		this.especie = especie;
		this.tipo = tipo;
		this.x = x;
		this.y = y;
		this.altura = altura;
		this.largura = largura;
		this.idImagem = idImagem;
		this.descricaoStatus = descricaoStatus;
		this.concluido = concluido;
	}
	 
	public static InfracaoProcesso buscaInfracaoProcessoPorIdInfracao(Integer idInfracao, Integer idProcesso) throws SQLException, ConexaoException {
		InfracaoProcesso iRet = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("IF EXISTS(SELECT 1 FROM infracao_processo (NOLOCK) WHERE id_processo = ? AND id_infracao = ? AND status_processo = 0) ");
		sbSQL.append("SELECT ip.id_infracao_processo,");
		sbSQL.append("		ip.id_infracao,");
		sbSQL.append("		ip.id_usuario,");
		sbSQL.append("		u.usuario,");
		sbSQL.append("		ip.id_processo,");
		sbSQL.append("		p.nome,");
		sbSQL.append("		ip.id_inconsistencia,");
		sbSQL.append("		ip.id_inconsistencia,");
		sbSQL.append("		i.descricao  AS descricao_inconsistencia,");
		sbSQL.append("		ip.data,");
		sbSQL.append("		ip.tempo,");
		sbSQL.append("		id.placa,");
		sbSQL.append("		m.id_marca_cet,");
		sbSQL.append("		m.descricao AS descricao_marca_cet,");
		sbSQL.append("		mc.id_marca,");
		sbSQL.append("		mc.descricao AS descricao_marca,");
		sbSQL.append("		e.id_especie,");
		sbSQL.append("		t.id_tipo,");
		sbSQL.append("		t.descricao AS descricao_tipo,");
		sbSQL.append("		id.uf,");
		sbSQL.append("		e.descricao AS descricao_especie,");
		sbSQL.append("		io.x,");
		sbSQL.append("		io.y,");
		sbSQL.append("		io.largura,");
		sbSQL.append("		io.altura,");
		sbSQL.append("		ip.id_imagem,");
		sbSQL.append("		sp.descricao_status,");
		sbSQL.append(" 		CASE WHEN ipc.id_infracao_processo_concluido IS NULL THEN 0 ELSE 1 END AS [concluido] ");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao_processo ip (NOLOCK) ");
		sbSQL.append("		JOIN sis_usuario u (NOLOCK) ON u.id_usuario = ip.id_usuario");
		sbSQL.append("		JOIN processo p (NOLOCK) ON p.id_processo = ip.id_processo");
		sbSQL.append("		LEFT JOIN inconsistencia i (NOLOCK) ON i.id_inconsistencia = ip.id_inconsistencia");
		sbSQL.append("		LEFT JOIN infracao_processo_digitacao id (NOLOCK) ON id.id_infracao_processo = ip.id_infracao_processo");
		sbSQL.append("		LEFT JOIN cad_veiculo cv (NOLOCK) ON cv.placa = id.placa");
		sbSQL.append("		LEFT JOIN cad_marca mc (NOLOCK) ON mc.id_marca = COALESCE(id.id_marca, cv.id_marca)");
		sbSQL.append("		LEFT JOIN cad_marca_cet m (NOLOCK) ON m.id_marca_cet = id.id_marca_cet");
		sbSQL.append("		LEFT JOIN cad_especie e (NOLOCK) ON e.id_especie = id.id_especie");
		sbSQL.append("		LEFT JOIN cad_tipo t (NOLOCK) ON t.id_tipo = COALESCE(id.id_tipo, cv.id_tipo)");
		sbSQL.append("		LEFT JOIN infracao_processo_obliteracao io (NOLOCK) ON io.id_infracao_processo = ip.id_infracao_processo");
		sbSQL.append("		LEFT JOIN status_processo sp (NOLOCK) ON sp.status_processo = ip.status_processo");
		sbSQL.append("      LEFT JOIN infracao_processo_concluido ipc (NOLOCK) ON ip.id_infracao = ipc.id_infracao AND ip.id_processo = ipc.id_processo ");
		sbSQL.append("  WHERE ip.id_processo = ? ");
		sbSQL.append("  AND ip.id_infracao = ? ");
		sbSQL.append("  AND ip.status_processo = 0 ");
		sbSQL.append("  ORDER BY id_infracao_processo DESC ");
		sbSQL.append(" ELSE   SELECT NULL AS nulo WHERE 1 = 0 ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idProcesso);
			ps.setInt(2, idInfracao);
			ps.setInt(3, idProcesso);
			ps.setInt(4, idInfracao);

			rs = ps.executeQuery();
			if (rs.next()) {
				iRet = new InfracaoProcesso(
						rs.getInt("id_infracao_processo"),
						rs.getInt("id_infracao"),
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getInt("id_processo"),
						rs.getString("nome"),
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao_inconsistencia"),
						rs.getTimestamp("data"),
						rs.getInt("tempo"),
						rs.getString("placa"),
						rs.getInt("id_marca_cet"),
						rs.getInt("id_marca"),
						rs.getInt("id_especie"),
						rs.getInt("id_tipo"),
						rs.getString("uf"),
						rs.getString("descricao_marca_cet"),
						rs.getString("descricao_marca"),
						rs.getString("descricao_especie"),
						rs.getString("descricao_tipo"),
						rs.getInt("x"),
						rs.getInt("y"),
						rs.getInt("largura"),
						rs.getInt("altura"),
						rs.getInt("id_imagem"),
						rs.getString("descricao_status"),
						rs.getInt("concluido")
				);
			}

		} catch (SQLException e) {
			logger.error("ERRO de SQL", e);
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

		return iRet;
	}

	public static List<InfracaoProcesso> buscaInfracaoProcessoPor(Map<String,Object> mFiltros) throws SQLException, ConexaoException {
		List<InfracaoProcesso> lRet = new ArrayList<InfracaoProcesso>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT ip.id_infracao_processo,");
		sbSQL.append("		ip.id_infracao,");
		sbSQL.append("		ip.id_usuario,");
		sbSQL.append("		u.usuario,");
		sbSQL.append("		ip.id_processo,");
		sbSQL.append("		p.nome,");
		sbSQL.append("		ip.id_inconsistencia,");
		sbSQL.append("		ip.id_inconsistencia,");
		sbSQL.append("		COALESCE(ipcontdesc.descricao,i.descricao)  AS descricao_inconsistencia,"); ///XXX
		sbSQL.append("		ip.data,");
		sbSQL.append("		ip.tempo,");
		sbSQL.append("		id.placa,");
		sbSQL.append("		m.id_marca_cet,");
		sbSQL.append("		m.descricao AS descricao_marca_cet,");
		sbSQL.append("		mc.id_marca,");
		sbSQL.append("		mc.descricao AS descricao_marca,");
		sbSQL.append("		e.id_especie,");
		sbSQL.append("		t.id_tipo,");
		sbSQL.append("		t.descricao AS descricao_tipo,");
		sbSQL.append("		id.uf,");
		sbSQL.append("		e.descricao AS descricao_especie,");
		sbSQL.append("		0 AS x,");
		sbSQL.append("		0 AS y,");
		sbSQL.append("		0 AS largura,");
		sbSQL.append("		0 AS altura,");
		sbSQL.append("		ip.id_imagem,");
		sbSQL.append("		sp.descricao_status,");
//		sbSQL.append(" 		CASE WHEN ipc.id_infracao_processo_concluido IS NULL THEN 0 ELSE 1 END AS [concluido] ");
		sbSQL.append(" 		0 AS [concluido] ");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao_processo ip WITH (NOLOCK) ");
		sbSQL.append("		JOIN sis_usuario u ON u.id_usuario = ip.id_usuario");
		sbSQL.append("		JOIN processo p ON p.id_processo = ip.id_processo");
		sbSQL.append("		LEFT JOIN inconsistencia i ON i.id_inconsistencia = ip.id_inconsistencia");
		sbSQL.append("		LEFT JOIN infracao_processo_digitacao id ON id.id_infracao_processo = ip.id_infracao_processo");
		sbSQL.append("		LEFT JOIN cad_veiculo cv ON cv.placa = id.placa");
		sbSQL.append("		LEFT JOIN cad_marca mc ON mc.id_marca = COALESCE(id.id_marca, cv.id_marca)");
		sbSQL.append("		LEFT JOIN cad_marca_cet m ON m.id_marca_cet = id.id_marca_cet");
		sbSQL.append("		LEFT JOIN cad_especie e ON e.id_especie = id.id_especie");
		sbSQL.append("		LEFT JOIN cad_tipo t ON t.id_tipo = COALESCE(id.id_tipo, cv.id_tipo)");
//		sbSQL.append("		LEFT JOIN infracao_processo_obliteracao io ON io.id_infracao_processo = ip.id_infracao_processo");
		sbSQL.append("		LEFT JOIN status_processo sp ON sp.status_processo = ip.status_processo");
//		sbSQL.append("      LEFT JOIN infracao_processo_concluido ipc (NOLOCK) ON ip.id_infracao = ipc.id_infracao AND ip.id_processo = ipc.id_processo ");
		sbSQL.append("		LEFT JOIN infracao_processo_contestacao ipcont (NOLOCK) ON ip.id_infracao_processo = ipcont.id_infracao_processo ");
		sbSQL.append("		LEFT JOIN infracao_contestacao_decisao ipcontdesc (NOLOCK) ON ipcont.decisao = ipcontdesc.id_decisao ");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("id_infracao", "ip.id_infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY ip.data");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new InfracaoProcesso(
						rs.getInt("id_infracao_processo"),
						rs.getInt("id_infracao"),
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getInt("id_processo"),
						rs.getString("nome"),
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao_inconsistencia"),
						rs.getTimestamp("data"),
						rs.getInt("tempo"),
						rs.getString("placa"),
						rs.getInt("id_marca_cet"),
						rs.getInt("id_marca"),
						rs.getInt("id_especie"),
						rs.getInt("id_tipo"),
						rs.getString("uf"),
						rs.getString("descricao_marca_cet"),
						rs.getString("descricao_marca"),
						rs.getString("descricao_especie"),
						rs.getString("descricao_tipo"),
						rs.getInt("x"),
						rs.getInt("y"),
						rs.getInt("largura"),
						rs.getInt("altura"),
						rs.getInt("id_imagem"),
						rs.getString("descricao_status"),
						rs.getInt("concluido")
				));
			}

		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
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

	/**
	 * Busca uma infração no processo.
	 * @param iInfracaoProcesso Identificador da infração no processo
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static InfracaoProcesso buscaInfracaoProcessoPorId(Integer iInfracaoProcesso) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("SELECT ip.id_infracao_processo,");
		sbSQL.append("		ip.id_infracao,");
		sbSQL.append("		ip.id_usuario,");
		sbSQL.append("		u.usuario,");
		sbSQL.append("		ip.id_processo,");
		sbSQL.append("		p.nome,");
		sbSQL.append("		ip.id_inconsistencia,");
		sbSQL.append("		i.descricao  AS descricao_inconsistencia,");
		sbSQL.append("		ip.data,");
		sbSQL.append("		ip.tempo,");
		sbSQL.append("		id.placa,");
		sbSQL.append("		m.id_marca_cet,");
		sbSQL.append("		m.descricao AS descricao_marca_cet,");
		sbSQL.append("		mc.id_marca,");
		sbSQL.append("		mc.descricao AS descricao_marca,");
		sbSQL.append("		t.id_tipo,");
		sbSQL.append("		t.descricao AS descricao_tipo,");
		sbSQL.append("		e.id_especie,");
		sbSQL.append("		id.uf,");
		sbSQL.append("		e.descricao AS descricao_especie,");
		sbSQL.append("		io.x,");
		sbSQL.append("		io.y,");
		sbSQL.append("		io.largura,");
		sbSQL.append("		io.altura,");
		sbSQL.append("		ip.id_imagem,");
		sbSQL.append("		sp.descricao_status,");
		sbSQL.append(" 		CASE WHEN ipc.id_infracao_processo_concluido IS NULL THEN 0 ELSE 1 END AS [concluido] ");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao_processo ip WITH (NOLOCK) ");
		sbSQL.append("		JOIN sis_usuario u ON u.id_usuario = ip.id_usuario");
		sbSQL.append("		JOIN processo p ON p.id_processo = ip.id_processo");
		sbSQL.append("		LEFT JOIN inconsistencia i ON i.id_inconsistencia = ip.id_inconsistencia");
		sbSQL.append("		LEFT JOIN infracao_processo_digitacao id ON id.id_infracao_processo = ip.id_infracao_processo");
		sbSQL.append("		LEFT JOIN cad_veiculo cv ON cv.placa = id.placa");
		sbSQL.append("		LEFT JOIN cad_marca mc ON mc.id_marca = COALESCE(id.id_marca, cv.id_marca)");
		sbSQL.append("		LEFT JOIN cad_marca_cet m ON m.id_marca_cet = id.id_marca_cet");
		sbSQL.append("		LEFT JOIN cad_especie e ON e.id_especie = id.id_especie");
		sbSQL.append("		LEFT JOIN cad_tipo t ON t.id_tipo = COALESCE(id.id_tipo, cv.id_tipo)");
		sbSQL.append("		LEFT JOIN infracao_processo_obliteracao io ON io.id_infracao_processo = ip.id_infracao_processo");
		sbSQL.append("		LEFT JOIN status_processo sp ON sp.status_processo = ip.status_processo");
		sbSQL.append("      LEFT JOIN infracao_processo_concluido ipc (NOLOCK) ON ip.id_infracao = ipc.id_infracao AND ip.id_processo = ipc.id_processo ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		ip.id_infracao_processo = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());				
			ps.setInt(1, iInfracaoProcesso);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new InfracaoProcesso(
						rs.getInt("id_infracao_processo"),
						rs.getInt("id_infracao"),
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getInt("id_processo"),
						rs.getString("nome"),
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao_inconsistencia"),
						rs.getTimestamp("data"),
						rs.getInt("tempo"),
						rs.getString("placa"),
						rs.getInt("id_marca_cet"),
						rs.getInt("id_marca"),
						rs.getInt("id_especie"),
						rs.getInt("id_tipo"),
						rs.getString("uf"),
						rs.getString("descricao_marca_cet"),
						rs.getString("descricao_marca"),
						rs.getString("descricao_especie"),
						rs.getString("descricao_tipo"),
						rs.getString("x") == null ? null : rs.getInt("x"),
						rs.getString("y") == null ? null : rs.getInt("y"),
						rs.getString("largura") == null ? null : rs.getInt("largura"),
						rs.getString("altura") == null ? null : rs.getInt("altura"),
						rs.getString("id_imagem") == null ? null : rs.getInt("id_imagem"),
						rs.getString("descricao_status"),
						rs.getInt("concluido")
				);
			}
			else {
				return null;
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
	}

	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Retorna o valor de idInfracao atual.
	 */
	public Integer getIdInfracao() {
		return idInfracao;
	}

	/**
	 * @return Retorna o valor de idUsuario atual.
	 */
	public Integer getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @return Retorna o valor de idProcesso atual.
	 */
	public Integer getIdProcesso() {
		return idProcesso;
	}

	/**
	 * @return Retorna o valor de idInconsistencia atual.
	 */
	public Integer getIdInconsistencia() {
		return IdInconsistencia;
	}

	/**
	 * @return Retorna o valor de data atual.
	 */
	public Date getData() {
		return data;
	}

	/**
	 * @return Retorna o valor de tempo atual.
	 */
	public Integer getTempo() {
		return tempo;
	}

	/**
	 * @return Retorna o valor de placa atual.
	 */
	public String getPlaca() {
		return placa;
	}


	/**
	 * @return Retorna o valor de usuario atual.
	 */
	public String getUsuario() {
		return usuario;
	}


	/**
	 * @return Retorna o valor de descricaoProcesso atual.
	 */
	public String getDescricaoProcesso() {
		return descricaoProcesso;
	}


	/**
	 * @return Retorna o valor de descricaoInconsistencia atual.
	 */
	public String getDescricaoInconsistencia() {
		return descricaoInconsistencia;
	}

	
	/**
	 * @return Retorna o valor de x atual.
	 */
	public Integer getX() {
		return x;
	}


	/**
	 * @return Retorna o valor de y atual.
	 */
	public Integer getY() {
		return y;
	}


	/**
	 * @return Retorna o valor de altura atual.
	 */
	public Integer getAltura() {
		return altura;
	}


	/**
	 * @return Retorna o valor de largura atual.
	 */
	public Integer getLargura() {
		return largura;
	}


	/**
	 * @return Retorna o valor de idImagem atual.
	 */
	public Integer getIdImagem() {
		return idImagem;
	}


	public Integer getIdMarcaCET() {
		return IdMarcaCET;
	}

	public Integer getIdEspecie() {
		return IdEspecie;
	}
	
	/**
	 * Retorna o valor do campo 'uf' atual.
	 * @return the uf
	 */
	public String getUf() {
		return this.uf;
	}

	/**
	 * Retorna o valor do campo 'marca' atual.
	 * @return the marca
	 */
	public String getMarca() {
		return this.marca;
	}

	/**
	 * @return Retorna o valor de marca atual.
	 */
	public String getMarcaCET() {
		return marcaCET;
	}

	/**
	 * @return Retorna o valor de marca atual.
	 */
	public String getMarcaDisponivel() {
		return marca == null ? marcaCET : marca;
	}


	/**
	 * Retorna o valor do campo 'descricaoStatus' atual.
	 * @return the descricaoStatus
	 */
	public String getDescricaoStatus() {
		return this.descricaoStatus;
	}

	public Integer getConcluido() {
		return concluido;
	}

	public Integer getIdTipo() {
		return IdTipo;
	}

	public String getTipo() {
		return tipo;
	}

}
