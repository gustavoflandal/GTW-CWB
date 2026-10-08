package muralha.digital.perguntasRespostas;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "AlertasQuestionarios")
@XmlAccessorType(XmlAccessType.FIELD)
public class AlertasQuestionarios {

    @XmlElementWrapper(name = "ResultAlertasQuestionarios")
    @XmlElement(name = "alertasQuestionarios")
    private List<AlertaQuestionario> listaAlertasQuestionarios;

    @XmlElementWrapper(name = "ResultRespostasQuestionario")
    @XmlElement(name = "respostasQuestionario")
    private List<RespostaQuestionarioDTO> listaRespostasQuestionario;

    public AlertasQuestionarios() {}

    public List<AlertaQuestionario> getListaAlertaQuestionario() {
        return listaAlertasQuestionarios;
    }

    public void setListaAlertaQuestionario(List<AlertaQuestionario> listaAlertasQuestionarios) {
        this.listaAlertasQuestionarios = listaAlertasQuestionarios;
    }

    public List<RespostaQuestionarioDTO> getListaRespostasQuestionario() {
        return listaRespostasQuestionario;
    }

    public void setListaRespostasQuestionario(List<RespostaQuestionarioDTO> listaRespostasQuestionario) {
        this.listaRespostasQuestionario = listaRespostasQuestionario;
    }
	
	public static List<AlertaQuestionario> ObterQuestionariosAlerta() throws ConexaoException, SQLException {
		
		List<AlertaQuestionario> listaRet = new ArrayList<AlertaQuestionario>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT * FROM muralha.alerta_questionario where deletado = 0 ORDER BY obrigatorio DESC");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				AlertaQuestionario item = new AlertaQuestionario();
				item.setId(rs.getInt("id"));
				item.setObrigatorio(rs.getInt("obrigatorio")); //Campo do tipo bit (boolean)
				item.setPergunta(rs.getString("pergunta"));
				item.setDeletado(rs.getInt("deletado")); //Campo do tipo bit (boolean)
				item.setData_criacao(rs.getDate("data_criacao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterQuestionariosAlerta):: ", e);
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
		return listaRet;
	}
	
	public static void RegistrarAcessoUsuario(String idAlerta, int idUsuario) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO muralha.alerta_usuario_visualiza(id_alerta, id_usuario, data_acesso) VALUES ( ?, ?, GETDATE() )");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			ps.setString(1, idAlerta);
			ps.setInt(2, idUsuario);
			
			ps.execute();	
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (RegistrarAcessoUsuario):: ", e);
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
	
	public static void RegistrarRespostasUsuario(List<AlertaQuestionarioResposta> respostas, int idUsuario) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO muralha.alerta_questionario_resposta ");
		sbSQL.append("(id_alerta, id_questionario_alerta, id_usuario_resposta, resposta_simples, resposta_livre_usuario, data_criacao) ");
		sbSQL.append("VALUES ");
		sbSQL.append("(?, ?, ?, ?, ?, GETDATE() )");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			for (AlertaQuestionarioResposta request : respostas) {
				ps.setString(1, request.getId_alerta().toString());
				ps.setInt(2, request.getId_questionario_alerta());
				ps.setInt(3, idUsuario);
				ps.setString(4, request.getResposta_simples() != null ? request.getResposta_simples().toUpperCase() : null);
				ps.setString(5, request.getResposta_livre_usuario());		
				
				ps.addBatch();
			}
			
			ps.executeBatch();
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (RegistrarRespostasUsuario):: ", e);
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
	
	public static Boolean VerificarQuestionarioObrigatorio() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT * FROM muralha.config_chave_valor WHERE chave = 'questionario_obrigatorio'");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				Boolean result = rs.getInt("valor") == 1 ? true : false;
				return result;
			}else {
				return false;
			}			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (RegistrarRespostasUsuario):: ", e);
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
	
	public static List<RespostaQuestionarioDTO> ObterRespostasQuestionario(String idAlerta) throws ConexaoException, SQLException {
		
		List<RespostaQuestionarioDTO> listaRet = new ArrayList<RespostaQuestionarioDTO>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT aq.pergunta, aq.obrigatorio, aqr.id_usuario_resposta, ");
		sbSQL.append(" sisu.nome, aqr.data_criacao as data_resposta, aqr.resposta_simples, ");
		sbSQL.append(" aqr.resposta_livre_usuario as resposta_usuario, ");
		sbSQL.append(" tao.tipo as tipo_alerta ");
		sbSQL.append(" FROM muralha.alerta_questionario_resposta aqr ");
		sbSQL.append(" JOIN muralha.alerta_questionario aq on aqr.id_questionario_alerta = aq.id ");
		sbSQL.append(" JOIN dbo.sis_usuario sisu on sisu.id_usuario = aqr.id_usuario_resposta ");
		sbSQL.append(" JOIN muralha.alerta al on al.id = aqr.id_alerta ");
		sbSQL.append(" JOIN muralha.tipo_alerta_ocorrencia tao on tao.id = al.id_tipo_alerta_ocorrencia ");
		sbSQL.append(" WHERE aq.deletado != 1 and id_alerta = ? ");
		sbSQL.append(" ORDER BY aqr.id_usuario_resposta, aqr.data_criacao ASC ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			ps.setString(1, idAlerta);
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				RespostaQuestionarioDTO item = new RespostaQuestionarioDTO();
				item.setPergunta(rs.getString("pergunta"));
				item.setObrigatorio(rs.getInt("obrigatorio"));
				item.setId_usuario_resposta(rs.getInt("id_usuario_resposta"));
				item.setNome(rs.getString("nome"));
				item.setData_resposta(rs.getTimestamp("data_resposta"));
				item.setResposta_simples(rs.getString("resposta_simples"));
				item.setResposta_usuario(rs.getString("resposta_usuario"));
				item.setTipo_alerta(rs.getString("tipo_alerta"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterQuestionariosAlerta):: ", e);
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
		return listaRet;
	}
}
