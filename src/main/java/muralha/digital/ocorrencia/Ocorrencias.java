package muralha.digital.ocorrencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital.consulta.StatusAlertaOcorrencia.StatusOcorrencia;
import muralha.digital.util.Resultado;

public class Ocorrencias 
{
	@XmlTransient
	private static final Logger logger = Logger.getLogger(Ocorrencias.class);
	
	public Ocorrencias() {}
	
	public static boolean GerarOcorrencia(UUID idOcorrencia, UUID idAlerta, UUID idTipoAlerta, UUID idStatusAlertaOcorrenciaGerada, UUID idStatusOcorrenciaPendente, Integer idUsuario) throws ConexaoException, SQLException 
	{
		Date dataOcorrencia = new Date();
		return GerarOcorrencia(idOcorrencia, idAlerta, idTipoAlerta, idStatusAlertaOcorrenciaGerada, idStatusOcorrenciaPendente, idUsuario, dataOcorrencia, false, null);
	}
	
	public static boolean GerarOcorrencia(UUID idOcorrencia, UUID idAlerta, UUID idTipoAlerta, UUID idStatusAlertaOcorrenciaGerada, UUID idStatusOcorrenciaPendente, Integer idUsuario, Date dataOcorrencia, boolean alertaVinculado, UUID idAlertaVinculado) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		CallableStatement cs = null;
		boolean retorno = false;
		
		try {
		
			sbSQL.append(" {? = call muralha.spu_gerar_ocorrencia(?, ?, ?, ?, ?, ?, ?, ?, ?)} ");

			conn = Conexao.getConexao();
			cs = conn.prepareCall(sbSQL.toString());
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, idOcorrencia.toString());
			cs.setString(3, idAlerta.toString());
			cs.setString(4, idTipoAlerta.toString());
			cs.setString(5, idStatusAlertaOcorrenciaGerada.toString());
			cs.setString(6, idStatusOcorrenciaPendente.toString());
			cs.setTimestamp(7, new Timestamp(dataOcorrencia.getTime()));
			cs.setInt(8, idUsuario);
			cs.setBoolean(9, alertaVinculado);
			
			if (idAlertaVinculado == null)
				cs.setNull(10, Types.VARCHAR);
			else
				cs.setString(10, idAlertaVinculado.toString());
			
			cs.execute();
			
			retorno = cs.getInt(1) > 0;
				
		}
		catch(Exception e) {
			String msgErro = "Erro ao gerar irregularidade!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (cs != null)
					cs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
	
	public static boolean FinalizarOcorrencia(UUID idOcorrencia, UUID idStatusFinalizarOcorrencia, String strObsFinalizarOcorrencia, Integer idUsuario) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		
		try {
		
			sbSQL.append(" UPDATE muralha.ocorrencia ");
			sbSQL.append(" SET    id_status_ocorrencia = ?, ");
			sbSQL.append(" 		  observacao = ?, ");
			sbSQL.append(" 		  id_usuario = ?, ");
			sbSQL.append(" 		  data_modificacao = ? ");
			sbSQL.append(" WHERE id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idStatusFinalizarOcorrencia.toString());
			ps.setString(2, strObsFinalizarOcorrencia);
			ps.setInt(3, idUsuario);
			ps.setTimestamp(4, new Timestamp(new Date().getTime()));
			ps.setString(5, idOcorrencia.toString());
			
			retorno = (ps.executeUpdate() == 1);
				
		}
		catch(Exception e) {
			String msgErro = "Erro ao descartar alerta!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
	
	public static Resultado GravarConfigNotificacao(UUID idOcorrencia, UUID idTipoNotificacaoEmail, UUID idTipoNotificacaoSMS, UUID idStatusNotificacaoPendente, Integer idUsuario, List<Integer> gruposEmail, List<Integer> gruposSMS)
			throws ConexaoException, SQLException
	{
		StringBuilder sbSQL = new StringBuilder();
		String sqlAux = "";
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false, erro = false;
		String mensagem = "";
		
		try
		{
			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();
			
			// Salvar histórico de notificações da ocorrência
			sqlAux = sqlAux + " INSERT INTO muralha.ocorrencia_notificacao_historico SELECT o.* FROM muralha.ocorrencia_notificacao o LEFT JOIN muralha.ocorrencia_notificacao_historico h ON h.id = o.id WHERE o.id_ocorrencia = ? AND h.id IS NULL ";
			mapaParametros.put(paramIndex++, idOcorrencia);
			
			// Remover registros da tabela principal
			sqlAux = sqlAux + " DELETE FROM muralha.ocorrencia_notificacao WHERE id_ocorrencia = ? ";
			mapaParametros.put(paramIndex++, idOcorrencia);
			
			// Salvar nova configuração de notificação da ocorrência
			for (Integer idGrupo : gruposEmail)
			{
				sqlAux = sqlAux + " INSERT INTO muralha.ocorrencia_notificacao (id_ocorrencia, id_grupo, id_tipo_notificacao, id_status_notificacao, id_usuario) VALUES (?, ?, ?, ?, ?) ";
				
				mapaParametros.put(paramIndex++, idOcorrencia);
				mapaParametros.put(paramIndex++, idGrupo);
				mapaParametros.put(paramIndex++, idTipoNotificacaoEmail);
				mapaParametros.put(paramIndex++, idStatusNotificacaoPendente);
				mapaParametros.put(paramIndex++, idUsuario);
			}
			
			for (Integer idGrupo : gruposSMS)
			{
				sqlAux = sqlAux + " INSERT INTO muralha.ocorrencia_notificacao (id_ocorrencia, id_grupo, id_tipo_notificacao, id_status_notificacao, id_usuario) VALUES (?, ?, ?, ?, ?) ";
				
				mapaParametros.put(paramIndex++, idOcorrencia);
				mapaParametros.put(paramIndex++, idGrupo);
				mapaParametros.put(paramIndex++, idTipoNotificacaoSMS);
				mapaParametros.put(paramIndex++, idStatusNotificacaoPendente);
				mapaParametros.put(paramIndex++, idUsuario);
			}
			
			sbSQL.append(sqlAux);
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Iterando e ajustando os valores dos parametros para os wheres (as interrogações):
			Object valorParam = null;
			for (Entry<Integer, Object> paramQuery : mapaParametros.entrySet()) {
				valorParam = paramQuery.getValue();
				
				if (valorParam == null)
					continue;				
				else if (valorParam instanceof UUID)
					ps.setString(paramQuery.getKey(), valorParam.toString());
				else if (valorParam instanceof String)
					ps.setString(paramQuery.getKey(), (String) valorParam);
				else if (valorParam instanceof Integer)
					ps.setInt(paramQuery.getKey(), ((Integer)valorParam).intValue());
				else if (valorParam instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean)valorParam).booleanValue());				
				else if (valorParam instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp)valorParam);
				else {
					erro = true;
					mensagem = "Ocorreu um erro ao preparar os filtros para gravação!";
					break;
				}
			}
			
			if (!erro)
			{
				retorno = (ps.executeUpdate() == 1);
				mensagem = "Configuração de Notificação da Irregularidade gravada com sucesso!";
			}
				
		}
		catch(Exception e)
		{
			erro = true;
			retorno = false;
			mensagem = "Erro ao gravar Configuração de Notificação da Irregularidade!";
			logger.error(mensagem + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return new Resultado(retorno, mensagem);
	}
	
	public static boolean AtualizarStatus(UUID idOcorrencia) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		
		Configuracao conf = ConfiguracaoProvider.getInstance();
		int idUsuarioSistema = Integer.parseInt(conf.getConfiguracaoChaveValor().get("usuario_sistema"));
		
		boolean atualizarStatus = false, isOcorrenciaEmAberto = false;
		
		isOcorrenciaEmAberto = isOcorrenciaEmAberto(idOcorrencia);
		
		if (isOcorrenciaEmAberto)
			atualizarStatus = AtualizarStatus(idOcorrencia, StatusOcorrencia.NOTIFICACAO_ENVIADA, idUsuarioSistema);
		
		
		if (!isOcorrenciaEmAberto)
			logger.debug("Status da irregularidade não é EM ABERTO, portanto não será atualizado!");
		else if (atualizarStatus)
			logger.debug("Status da irregularidade atualizado com sucesso após envio das notificações!");
		else
			logger.error("Erro ao atualizar o status da irregularidade após envio das notificações");
		
		retorno = (atualizarStatus || !isOcorrenciaEmAberto);
		
		return retorno;
	}
	
	public static boolean AtualizarStatus(UUID idOcorrencia, StatusOcorrencia statusOcorrencia, Integer idUsuario) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		
		try
		{
			sbSQL.append(" UPDATE muralha.ocorrencia ");
			sbSQL.append(" SET    id_status_ocorrencia = ?, ");
			sbSQL.append(" 		  data_modificacao = ?, ");
			sbSQL.append(" 		  id_usuario_modificacao = ? ");
			sbSQL.append(" WHERE  id = ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, statusOcorrencia.GetID().toString());
			ps.setTimestamp(2, new Timestamp(new Date().getTime()));
			ps.setInt(3, idUsuario);
			ps.setString(4, idOcorrencia.toString());
			
			retorno = (ps.executeUpdate() == 1);
		}
		catch(Exception e)
		{
			String msgErro = "Erro ao atualizar status da irregularidade!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
	
	
	public static boolean isOcorrenciaEmAberto(UUID idOcorrencia) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean retorno = false;
		
		try
		{
			sbSQL.append(" SELECT id ");
			sbSQL.append(" FROM   muralha.ocorrencia ");
			sbSQL.append(" WHERE  id = ? ");
			sbSQL.append(" 		  AND id_status_ocorrencia = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idOcorrencia.toString());
			ps.setString(2, StatusOcorrencia.EM_ABERTO.GetID().toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = true; }
		}
		catch(Exception e)
		{
			String msgErro = "Erro ao verificar se a irregularidade está em aberto!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
	
	
	public static Resultado AtualizarStatusAtendimento(UUID idOcorrencia, boolean permiteAtualizar, Integer idUsuario) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		String mensagem = "";
		
		if (isPermiteAlterarConfigAtendimento(idOcorrencia))
		{
			try
			{
				sbSQL.append(" UPDATE muralha.ocorrencia ");
				sbSQL.append(" SET    permite_atendimento = ?, ");
				sbSQL.append(" 		  data_modificacao = ?, ");
				sbSQL.append(" 		  id_usuario_modificacao = ? ");
				sbSQL.append(" WHERE  id = ? ");
				
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString());
				
				ps.setBoolean(1, permiteAtualizar);
				ps.setTimestamp(2, new Timestamp(new Date().getTime()));
				ps.setInt(3, idUsuario);
				ps.setString(4, idOcorrencia.toString());
				
				retorno = (ps.executeUpdate() == 1);
			}
			catch(Exception e)
			{
				retorno = false;
				mensagem = "Erro ao atualizar configurações de atendimento da ocorrência!";
				logger.error(mensagem + ": " + e.getMessage(), e);
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
					if (ps != null)
						ps.close();
				}
				catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
			}
		}
		else
		{
			retorno = false;
			mensagem = "Não foi possível alterar a configuração de atendimento da ocorrência. Já existe um atendimento iniciado!";
			logger.error(mensagem);
		}
		
		return new Resultado(retorno, mensagem);
	}
	
	
	public static boolean isPermiteAlterarConfigAtendimento(UUID idOcorrencia) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean retorno = true;
		
		try
		{
			sbSQL.append(" SELECT id ");
			sbSQL.append(" FROM   muralha.atendimento atend ");
			sbSQL.append(" WHERE  id_ocorrencia = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idOcorrencia.toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = false; }
		}
		catch(Exception e)
		{
			String msgErro = "Erro ao verificar se a configuração de atendimento da irregularidade pode ser alterado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
}
