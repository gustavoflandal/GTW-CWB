package com.consilux.model.documento;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

public class Documento {
	
	private Integer idDocumento;
	private String identificadorExterno;
	private String nomeArquivo;
	private Integer idClassificador;
	private Date dataCriacao;
	private Integer idUsuarioCriacao;
	private Date dataExclusao;
	private Integer idUsuarioExclusao;
	
	public enum Classficador {

		PROTOCOLO_CD_COMPROVACAO(3),
		PLANILHA_CD_COMPROVACAO(4),
		PROTOCOLO_CD_COMPLEMENTO_COMPROVACAO(5),
		PLANILHA_QUANTITATIVOS(6);

		private Integer id;
		
		Classficador(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static Classficador valueOfId(Integer id) {
			for (Classficador classificador: values()) {
				if (classificador.getId() == id.intValue())
					return classificador;
			}
			return null;
		}
		
	}
	
	/**
	 * @param idDocumento
	 * @param identificadorExterno
	 * @param nomeArquivo
	 * @param idClassificador
	 * @param dataCriacao
	 * @param idUsuarioCriacao
	 * @param dataExclusao
	 * @param idUsuarioExclusao
	 */
	public Documento(Integer idDocumento, String identificadorExterno,
			String nomeArquivo, Integer idClassificador, Date dataCriacao,
			Integer idUsuarioCriacao, Date dataExclusao,
			Integer idUsuarioExclusao) {
		super();
		this.idDocumento = idDocumento;
		this.identificadorExterno = identificadorExterno;
		this.nomeArquivo = nomeArquivo;
		this.idClassificador = idClassificador;
		this.dataCriacao = dataCriacao;
		this.idUsuarioCriacao = idUsuarioCriacao;
		this.dataExclusao = dataExclusao;
		this.idUsuarioExclusao = idUsuarioExclusao;
	}

	public static Boolean verificaExistencia(FileItem fi) throws IOException, ConexaoException, SQLException, ModelException {
		return verificaExistencia(fi, false);
	}

	public static Boolean verificaExistencia(FileItem fi, Boolean verificarMD5) throws IOException, ConexaoException, SQLException, ModelException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nome_arquivo", fi.getName());
		if (verificarMD5) {
			map.put("md5", Funcoes.geraMD5(fi.getInputStream()));
		}
		
		List<Documento> lRet = buscarDocumentoPor(map);

		return (lRet != null && lRet.size() > 0);
	}

	public static Documento adicionar(String identificador, Integer classficador, Integer idUsuario, FileItem fi) throws ConexaoException, SQLException, IOException, ModelException {
		Integer idDoc = null;
		Connection conn = Conexao.getConexao();
		
		try { 
			CallableStatement cs = conn.prepareCall("{? = call spu_adic_documento(?, ?, ?, ?, ?, ?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, identificador);
			cs.setString(3, fi.getName());
			cs.setInt(4, classficador);
			cs.setInt(5, idUsuario);
			cs.setString(6, Funcoes.geraMD5(fi.getInputStream()));
			cs.setBinaryStream(7, fi.getInputStream(), (int)fi.getSize());
			cs.execute();
			idDoc = cs.getInt(1);
			cs.close();
		}
		finally {
			if (conn != null) 
				conn.close();
		}
		return Documento.buscarDocumentoPorId(idDoc);
	}

	public static Documento buscarDocumentoPorId(Integer idDocumento) throws ConexaoException, SQLException, ModelException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id_documento", idDocumento);
		
		List<Documento> lRet = buscarDocumentoPor(map);

		if (lRet != null && lRet.size() == 1)
			return lRet.get(0);
		else
			return null;
		
	}

	public static List<Documento> buscarDocumentoPorIdentificadorClassificador(String identificador, Integer classficador) throws ConexaoException, SQLException, ModelException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("identificador_externo", identificador);
		map.put("id_classificador", classficador);
		
		List<Documento> lRet = buscarDocumentoPor(map);

		return lRet;
	}

	public static List<Documento> buscarDocumentoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		List<Documento> lRet = new ArrayList<Documento>();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	id_documento,");
		sbSQL.append("	identificador_externo,");
		sbSQL.append("	nome_arquivo,");
		sbSQL.append("	id_classificador,");
		sbSQL.append("	data_criacao,");
		sbSQL.append("	id_usuario_criacao,");
		sbSQL.append("	data_exclusao,");
		sbSQL.append("	id_usuario_exclusao ");
		sbSQL.append("FROM sis_documento ");
		sbSQL.append("WHERE ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();

			Map<String, String> mRegras = new HashMap<String, String>();
			mRegras.put("md5", "id_documento in (SELECT id_documento FROM sis_documento_conteudo WHERE md5 = ?)");
			
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY id_documento");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
		
			rs = ps.executeQuery();

			while (rs.next()) {				
				lRet.add(new Documento(
						rs.getInt("id_documento"),
						rs.getString("identificador_externo"),
						rs.getString("nome_arquivo"),
						rs.getInt("id_classificador"),
						rs.getTimestamp("data_criacao"),
						rs.getInt("id_usuario_criacao"),
						rs.getTimestamp("data_exclusao"),
						rs.getInt("id_usuario_exclusao")
					)
				);
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return lRet;		
	}
	
	public static byte[] getDocumentoBD(Integer idDocumento) throws SQLException, ConexaoException {
		byte bRet[] = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT conteudo");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_documento_conteudo WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_documento = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Ajustando os valores dos parametros para os wheres:
			ps.setInt(1, idDocumento);
			
			rs = ps.executeQuery();
			if (rs.next())
				bRet = rs.getBytes("conteudo");
		}
		finally {
			if (conn != null)
				conn.close();
		}
			
		return bRet;
	}
	
	/**
	 * @return the idDocumento
	 */
	public Integer getIdDocumento() {
		return idDocumento;
	}

	/**
	 * @return the identificadorExterno
	 */
	public String getIdentificadorExterno() {
		return identificadorExterno;
	}

	/**
	 * @return the nomeArquivo
	 */
	public String getNomeArquivo() {
		return nomeArquivo;
	}

	/**
	 * @return the idClassificador
	 */
	public Integer getIdClassificador() {
		return idClassificador;
	}

	/**
	 * @return the dataCriacao
	 */
	public Date getDataCriacao() {
		return dataCriacao;
	}

	/**
	 * @return the idUsuarioCriacao
	 */
	public Integer getIdUsuarioCriacao() {
		return idUsuarioCriacao;
	}

	/**
	 * @return the dataExclusao
	 */
	public Date getDataExclusao() {
		return dataExclusao;
	}

	/**
	 * @return the idUsuarioExclusao
	 */
	public Integer getIdUsuarioExclusao() {
		return idUsuarioExclusao;
	}

}
