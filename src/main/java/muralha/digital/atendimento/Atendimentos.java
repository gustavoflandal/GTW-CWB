package muralha.digital.atendimento;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital.guarnicao.Guarnicao;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Atendimentos extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@XmlTransient
	private static final Logger logger = Logger.getLogger(Atendimentos.class);

	@XmlElementWrapper(name = "listaAtendimentos")
	@XmlElement(name = "atendimento")
	private List<Atendimento> atendimentos;

	public List<Atendimento> getAlertas() {
		return atendimentos;
	}
	public void setAlertas(List<Atendimento> alerts) {
		this.atendimentos = alerts;
	}
	
	
	public static List<Atendimento> ObterListaOcorrencias() {
		List<Atendimento> atendimento = new ArrayList<Atendimento>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
            sbSQL.append(" EXEC  muralha.spu_obtem_ocorrencias ");
			
			

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				Atendimento item = new Atendimento();
				
				item.setId_ocorrencia(rs.getString("id"));
				//item.setOrigem(rs.getString("origem"));
				item.setTipoAlerta(rs.getString("tipo"));
				item.setDtAlertaStr(rs.getString("data"));
				item.setDataAlerta(rs.getTimestamp("data"));
				
				item.setPlacaVeiculo(rs.getString("placa"));
				item.setObservacao(rs.getString("observacao"));
				item.setDescricao(rs.getString("descricao"));
				item.setOrigemRegistro(rs.getInt("id_origem_registro"));
				item.setPrioridade(rs.getInt("prioridade"));
				item.setStatusOcorrencia(rs.getString("status"));
				item.setDataAtendimentoCriacao(rs.getTimestamp("data_criacao"));
				item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
				item.setIdSituacaoEnvio(rs.getInt("Id_situacao_envio"));
				item.setIdAtendimento(rs.getInt("id_atendimento"));
				item.setIdGuarnicao(rs.getInt("id_guarnicao"));
				String protocoloStr = rs.getString("protocolo");
				item.setEndereco_alerta(rs.getString("endereco_alerta"));
				item.setEndereco_local_evento(rs.getString("endereco_local_evento"));
				item.setId_local(rs.getInt("id_local"));;
				if (protocoloStr != null && !protocoloStr.isEmpty()) {
				    item.setProtocolo(protocoloStr);
				} else {
				    item.setProtocolo(null);
				}
				
				String idAlertaStr = rs.getString("id_alerta");
				if (idAlertaStr != null && !idAlertaStr.isEmpty()) {
				    item.setIdAlerta(UUID.fromString(idAlertaStr));
				} else {
				    item.setIdAlerta(null);
				}			
				
				item.setDataAlertaFormatada(item.getDataAlertaFormatada());
				item.setDataAtendimentoFormatada(item.getDataAtendimentoFormatada());
				item.setDataEncerramentoFormatada(item.getDataEncerramentoFormatada());
				item.setIdRegistroFato(rs.getInt("id_registro_fato"));
				item.setTemBoletim(rs.getInt("tem_boletim"));
				
				atendimento.add(item);
			}
		} catch (Exception e) {
			logger.error("Erro ao obter novas ocorrencias: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return atendimento;
	}	
	
	public static List<Guarnicao> ObterGuarnicoes(){
		List<Guarnicao> guarnicao = new ArrayList<Guarnicao>();		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		
		try {
            sbSQL.append(" SELECT 										");
            sbSQL.append(" g.id, g.nome									");
            sbSQL.append(" FROM muralha.guarnicao g 					");
            sbSQL.append(" WHERE g.disponivel = 1 AND g.ativo = 1 		");
           

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				Guarnicao item = new Guarnicao();				
				
				item.setId(rs.getInt("id"));	
				item.setNome(rs.getString("nome"));
				
				guarnicao.add(item);			
		
			}
		} catch (Exception e) {
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return guarnicao;
		
	}
	
	
	public static List<Guarnicao> ObterGuarnicoesStatus(){
		List<Guarnicao> guarnicao = new ArrayList<Guarnicao>();		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		
		try {
            sbSQL.append(" SELECT  g.id, g.nome,						");
            sbSQL.append(" g.ativo, g.disponivel, su.nome AS usuario	");
            sbSQL.append(" FROM muralha.guarnicao g 					");
            sbSQL.append(" JOIN sis_usuario su on 						");
            sbSQL.append(" g.id_usuario_responsavel = su.id_usuario 	");           
           

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				Guarnicao item = new Guarnicao();				
				
				item.setId(rs.getInt("id"));	
				item.setNome(rs.getString("nome"));
				item.setAtivo(rs.getInt("ativo"));
				item.setDisponivel(rs.getInt("disponivel"));
				item.setResponsavel(rs.getString("usuario"));
				
				guarnicao.add(item);			
		
			}
		} catch (Exception e) {
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return guarnicao;
		
	}
	
	
	
	public Atendimento IniciarAtendimento(String idOcorrencia, String origemRegistro, String idRegistroFato, int idUsuario) throws SQLException, ConexaoException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
	    StringBuilder sbSQL = new StringBuilder();
	    Atendimento atend = new Atendimento();
	    String protocolo = "ATD-" + idRegistroFato + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

	    try {
	        sbSQL.append("INSERT INTO muralha.atendimento (						");
	        sbSQL.append("protocolo, id_origem, id_registro_fato,				");
	        sbSQL.append("id_situacao, data_criacao, id_usuario_criacao 		");
	        if ("1".equals(origemRegistro)) {
            sbSQL.append(", id_ocorrencia 										");
	        }
	        sbSQL.append(" )													");
	        sbSQL.append("VALUES (?, ?, ?, 2, GETDATE(), ?				");
	        if ("1".equals(origemRegistro)) {
	        	sbSQL.append(", ?												");
	        }
	        sbSQL.append(" )													");
	        
	      
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS); 
	        ps.setString(1, protocolo);
	        ps.setString(2, origemRegistro);
	        ps.setString(3, idRegistroFato);
	        ps.setInt(4, idUsuario);
	        if ("1".equals(origemRegistro)) {
	        	 ps.setString(5, idOcorrencia);					
	        	}	       
	       
	        int affectedRows = ps.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Falha ao inserir atendimento, nenhuma linha afetada.");
	        }

	        rs = ps.getGeneratedKeys();
	        if (rs.next()) {
	            atend.setIdAtendimento(rs.getInt(1));           
	           
	        }
	    } catch (SQLException e) {
	        logger.error("Erro ao iniciar atendimento para idOcorrencia: " + idOcorrencia + ", origemRegistro: " + origemRegistro, e);
	        throw e;
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            logger.error("Erro ao fechar recursos de banco de dados", e);
	        }
	    }

	    return atend;
	}
	
	public void EnviarGuarnicao(HttpServletResponse response, int idAtendimento, int idguarnicao, String observacao)throws SQLException, ConexaoException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;
	    StringBuilder sbSQL = new StringBuilder();
	    
	    try {
	        sbSQL.append("INSERT INTO muralha.atendimento_guarnicao (		");
	        sbSQL.append("id_atendimento, id_situacao, 							");	        
	        sbSQL.append("id_guarnicao, observacao, data) 						");
	        sbSQL.append("VALUES (?, 1, ? ,?, GETDATE())						");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString()); 
	        ps.setInt(1, idAtendimento);
	        ps.setInt(2, idguarnicao);
	        ps.setString(3, observacao);

	        ps.executeUpdate();
	        
	        String updateSQL = "UPDATE muralha.guarnicao SET disponivel = 0 WHERE id = ?";
	        psUpdate = conn.prepareStatement(updateSQL);
	        psUpdate.setInt(1, idguarnicao);
	        psUpdate.executeUpdate();

	        conn.commit(); // Confirma as alterações
	        
	        EnviarRespostaTexto(response, "Guarnição enviada com sucesso!", true);
	       
	    } catch (SQLException e) {
	        logger.error("Erro ao enviar gurnição", e);
	        EnviarRespostaTexto(response, "Erro ao enviar guarnição!", false);
	        throw e;
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            logger.error("Erro ao fechar recursos de banco de dados", e);
	        }
	    }
	}
	
	public void EncerrarAtendimento(HttpServletResponse response, int idAtendimento, int idguarnicao)throws SQLException, ConexaoException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			sbSQL.append("EXEC muralha.spu_encerrar_atendimento ?, ?");
			
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString()); 
			ps.setInt(1, idAtendimento);
			ps.setInt(2, idguarnicao);
			
			
			ps.executeUpdate();
			
			EnviarRespostaTexto(response, "Atendimento finalizado!", true);
			
		} catch (SQLException e) {
			logger.error("Erro ao finalizar atendimento", e);
			EnviarRespostaTexto(response, "Erro ao finalizar atendimento!", false);
			throw e;
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar recursos de banco de dados", e);
			}
		}
	}
	
	public void AnexarDocumento(HttpServletResponse response, int idAtendimento, String detalhamentoStr)throws SQLException, ConexaoException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;		
		ResultSet rs = null;
	    StringBuilder sbSQL = new StringBuilder();
	    
	    try {
	        sbSQL.append("INSERT INTO muralha.atendimento_documento (		");
	        sbSQL.append("id_atendimento, tipo, 							");	        
	        sbSQL.append("detalhamento,dir_arquivo) 						");
	        sbSQL.append("VALUES (?, 1, ? ,?)								");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString()); 
	        ps.setInt(1, idAtendimento);
	        ps.setString(2, detalhamentoStr);	    

	        ps.executeUpdate();       
	       
	        
	        EnviarRespostaTexto(response, "Arquivo anexado!", true);
	       
	    } catch (SQLException e) {
	        logger.error("Erro ao enviar gurnição", e);
	        EnviarRespostaTexto(response, "Erro ao anexar arquivo!", false);
	        throw e;
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            logger.error("Erro ao fechar recursos de banco de dados", e);
	        }
	    }
	}
	
	
	public void VincularAtendimentoBo(HttpServletResponse response, int idAtendimento, int idBoletim)throws SQLException, ConexaoException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			sbSQL.append("UPDATE muralha.atendimento		");
			sbSQL.append("SET id_boletim = ? 				");	        
			sbSQL.append("WHERE id = ?  					");
			
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString()); 
			ps.setInt(1, idBoletim);
			ps.setInt(2, idAtendimento);
			
			ps.executeUpdate();
			
			//EnviarRespostaTexto(response, "Guarnição enviada com sucesso!", true);
			
		} catch (SQLException e) {
			logger.error("Erro vincular boletim ao atendimento", e);
			//EnviarRespostaTexto(response, "Erro ao enviar guarnição!", false);
			throw e;
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar recursos de banco de dados", e);
			}
		}
	}
	
	public void Historico( int idAtendimento, int id_tipo_historico, String evento, int idUsuario)throws SQLException, ConexaoException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
	    StringBuilder sbSQL = new StringBuilder();
	    
	    try {
	        sbSQL.append("INSERT INTO muralha.atendimento_historico (		");
	        sbSQL.append("id_atendimento, id_tipo_historico, 				");	        
	        sbSQL.append("evento, data, id_usuario) 						");
	        sbSQL.append("VALUES (?, ?, ? , GETDATE(), ?)					");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString()); 
	        ps.setInt(1, idAtendimento);
	        ps.setInt(2, id_tipo_historico);
	        ps.setString(3, evento);
	        ps.setInt(4, idUsuario);

	        ps.executeUpdate();
	        
	        //EnviarRespostaTexto(response, "Guarnição enviada com sucesso!", true);
	       
	    } catch (SQLException e) {
	        logger.error("Erro ao gravar historico", e);
	        //EnviarRespostaTexto(response, "Erro ao enviar guarnição!", false);
	        throw e;
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            logger.error("Erro ao fechar recursos de banco de dados", e);
	        }
	    }
	}
	
	
	
	
	@SuppressWarnings("unused")
	private void EnviarRespostaTexto(HttpServletResponse response, String texto, boolean sucesso) throws IOException
	{
		response.setContentType("text/plain");
		
		if (sucesso) { 
			response.setStatus(HttpServletResponse.SC_OK); 
		}else { 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
		}
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(texto);                         
		response.getWriter().flush();
	}
	
	

}
