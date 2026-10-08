package com.consilux.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.AnexoEmailBean;
import com.consilux.model.beans.EmailEnviarBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para o controle do envio de emails.
 * @author raoni
 */
public class EmailEnviar implements Serializable {

	private static final long serialVersionUID = 2035188461287315850L;
	private static Logger logger = Logger.getLogger(EmailEnviar.class);
	
	public static Queue<EmailEnviarBean> listarEmails(boolean somentePendentes)
	throws SQLException, ConexaoException {
		
		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			return listarEmails(conn, somentePendentes);
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public static Queue<EmailEnviarBean> listarEmails(Connection conn, boolean somentePendentes)
	throws SQLException { 
		
		Queue<EmailEnviarBean> fRet = new LinkedList<EmailEnviarBean>();
		
		PreparedStatement psBuscaEmails = null;
		PreparedStatement psBuscaDestinos = null;

		ResultSet rsEmails = null;
		ResultSet rsDestinos = null;
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT id_email_enviar, data_criacao, ");
		sbSQL.append(" data_envio, assunto_email, corpo_email, remetente ");		
		sbSQL.append(" FROM email_enviar ");
		

		if (somentePendentes)
			sbSQL.append(" WHERE data_envio IS NULL ");
		
		sbSQL.append(" ORDER BY data_criacao ");
		
		try {
			
			psBuscaEmails = conn.prepareStatement(sbSQL.toString());
			psBuscaDestinos = conn.prepareStatement("SELECT endereco_email FROM destino_email_enviar WHERE id_email_enviar = ?");
			
			rsEmails = psBuscaEmails.executeQuery();
			
			EmailEnviarBean emailEnviar;
			Timestamp dataEnvio;
			
			while (rsEmails.next()) {
				
				emailEnviar = new EmailEnviarBean();
				
				emailEnviar.setIdEmail(rsEmails.getInt("id_email_enviar"));
				emailEnviar.setDataCriacao(new Date(rsEmails.getTimestamp("data_criacao").getTime()));
				
				dataEnvio = rsEmails.getTimestamp("data_envio");
				if (rsEmails.wasNull())
					emailEnviar.setDataEnvio(null);
				else
					emailEnviar.setDataEnvio(new Date(dataEnvio.getTime()));
					
				emailEnviar.setAssunto(rsEmails.getString("assunto_email"));
				emailEnviar.setCorpo(rsEmails.getString("corpo_email"));
				
				emailEnviar.setRemetente(new InternetAddress(rsEmails.getString("remetente")));
				
				// Recupera os destinos
				psBuscaDestinos.setInt(1, emailEnviar.getIdEmail());
				rsDestinos = psBuscaDestinos.executeQuery();
				while (rsDestinos.next()) {
					emailEnviar.getDestinatarios().add(new InternetAddress(
						rsDestinos.getString("endereco_email")));
				}
				
				fRet.add(emailEnviar);
			}

		} catch (Exception ex) {
			logger.error("Erro ao recuperar os emails do banco de dados.", ex);
			conn.rollback();
		}
		finally {
			
			if (psBuscaDestinos != null) {
				psBuscaDestinos.close();
				psBuscaDestinos = null;
			}
			
			if (psBuscaEmails != null) {
				psBuscaEmails.close();
				psBuscaEmails = null;
			}
		}
		
		return fRet;
	}
	
	public static void marcarComoEnviado(Connection conn, EmailEnviarBean emailEnviar)
	throws ModelException, SQLException {

		if (conn == null)
			throw new ModelException("Argumento nulo: conn");
		
		if (emailEnviar == null)
			throw new ModelException("Argumento nulo: emailEnviar");
		
		if (emailEnviar.getDataEnvio() != null)
			throw new ModelException("Argumento inválido: email já foi enviado.");
		
		emailEnviar.setDataEnvio(new Date());
		
		PreparedStatement ps = conn.prepareStatement("UPDATE email_enviar SET data_envio = ? WHERE id_email_enviar = ?");
		ps.setTimestamp(1, new Timestamp(emailEnviar.getDataEnvio().getTime()));
		ps.setInt(2, emailEnviar.getIdEmail());
		
		ps.executeUpdate();
		
	}
	
	
	public static void marcarComoEnviado(EmailEnviarBean emailEnviar)
	throws ConexaoException, SQLException, ModelException
	{
		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			marcarComoEnviado(conn, emailEnviar);
			conn.commit();
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public static void inserir(EmailEnviarBean emailEnviar, int idVeiculoMonitorado)
	throws ModelException, ConexaoException, SQLException {
		
		Connection conn = null;
		
		try {
			
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			inserir(conn, emailEnviar, idVeiculoMonitorado);
			
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
		
		
	}
	
	public static void inserir(Connection conn, EmailEnviarBean emailEnviar, int idVeiculoMonitorado)
	throws ModelException, ConexaoException, SQLException {
		
		if (emailEnviar == null)
			throw new ModelException("Argumento nulo: emailEnviar");

		if (emailEnviar.getDestinatarios().size() == 0)
			throw new ModelException("ERRO: Tentativa de enviar um email sem destinatários.");

		if (emailEnviar.getRemetente() == null)
			throw new ModelException("ERRO: Tentativa de enviar um email sem remetente.");
		
		if (emailEnviar.getDataEnvio() != null)
			throw new ModelException("ERRO: Tentativa de inserir um email que já foi enviado.");
			
		// Cria o SQL
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("INSERT INTO email_enviar (data_criacao, data_envio, ");
		sbSQL.append(" assunto_email, corpo_email, remetente) VALUES ( ");					
		sbSQL.append(" ?,?,?,?,?) ");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		int idEmail;
		int idAnexo;
		
		try {

			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
			
			// Define os parâmetros
			ps.setTimestamp(1, new Timestamp(emailEnviar.getDataCriacao().getTime()));
			ps.setNull(2, Types.TIMESTAMP);
			ps.setString(3, emailEnviar.getAssunto());
			ps.setString(4, emailEnviar.getCorpo());
			ps.setString(5, emailEnviar.getRemetente().getAddress());
			
			// Recupera o identity
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			idEmail = rs.getInt(1);
			
			emailEnviar.setIdEmail(idEmail);
			
			// Atualiza o seu respectivo veículo monitorado.
			ps = conn.prepareStatement("UPDATE veiculo_monitorado SET id_email_enviar = ? WHERE id_veiculo_monitorado = ?");
			ps.setInt(1, idEmail);
			ps.setInt(2, idVeiculoMonitorado);
			ps.executeUpdate();
			
			// Limpa o builder de SQL
			sbSQL.setLength(0);
			
			// Prepara um statement para inserir os anexos.
			sbSQL.append(" INSERT INTO anexo_email_enviar ( ");
			sbSQL.append(" id_email_enviar, nome_arquivo, bytes_arquivo, content_type) ");
			sbSQL.append(" VALUES (?,?,?,?) ");
			
			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
			
			// Insere os anexos
			for (AnexoEmailBean anexo : emailEnviar.getAnexos()) {
				
				// Define os parâmetros
				ps.setInt(1, idEmail);
				ps.setString(2, anexo.getNomeArquivo());
				ps.setBytes(3, anexo.getBytesArquivo());
				ps.setString(4, anexo.getContentType());
				
				// Recupera o identity
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.next();
				idAnexo = rs.getInt(1);
				anexo.setIdAnexo(idAnexo);
			}
			
			// Limpa o builder de SQL
			sbSQL.setLength(0);
			
			// Prepara um statement para inserir os destinatários.
			sbSQL.append("INSERT INTO destino_email_enviar ( ");
			sbSQL.append(" id_email_enviar, endereco_email) ");
			sbSQL.append(" VALUES (?,?) ");
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Insere os destinatários
			for (InternetAddress dest : emailEnviar.getDestinatarios()) {
				
				// Define os parâmetros
				ps.setInt(1, idEmail);
				ps.setString(2, dest.getAddress());
				ps.executeUpdate();
			}				
			
			// Por fim, faz o commit de toda a transação.
			conn.commit();
		} catch (Exception ex) {
			logger.error("Erro ao salvar um email.", ex);
			conn.rollback();
		}
		finally {
			if (rs != null)
				rs.close();				
			if (ps != null)
				ps.close();
		}
	}
}
