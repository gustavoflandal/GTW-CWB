package muralha.digital.notificacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital._ini.Inicializacao;
import muralha.digital.consulta.TipoRegistro;
import muralha.digital.util.EncurtadorURL;
import muralha.digital.veiculo.imagem.VeiculoImagem;
import muralha.digital.veiculo.imagem.VeiculoImagens;


public class Notificacoes
{	
	private static Logger logger = LogManager.getLogger(Notificacoes.class);
	private static String LINK_PAGINA_DETALHE_NOTIFICACAO = Inicializacao.LinkPaginaDetalheNotificacao.trim();
	
	public Notificacoes()
	{
		super();
	}
	
	public static Queue<Notificacao> ObterNotificacoesPendentesEmail(UUID idTipoRegistro) throws ConexaoException, SQLException 
	{
		Queue<Notificacao> listaRet = new LinkedList<Notificacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" EXEC muralha.spu_ObterNotificacoesPendentes ?, ?, ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoRegistro.toString());
			ps.setString(2, StatusNotificacao.Status.PENDENTE.GetID().toString());
			ps.setString(3, TipoNotificacao.Tipo.EMAIL.GetID().toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				Notificacao item = new Notificacao();

				Integer idGrupo = rs.getInt("id_grupo");
				UUID idAlerta = UUID.fromString(rs.getString("id_alerta"));
				UUID idOcorrencia = rs.getString("id_ocorrencia") != null ? UUID.fromString(rs.getString("id_ocorrencia")) : null;
				
				item.setId(UUID.fromString(rs.getString("id")));
				item.setDataCadastro(rs.getDate("data_cadastro"));
				item.setIdOcorrencia(idOcorrencia);
				item.setIdAlerta(idAlerta);
				item.setEquipamento(rs.getString("equipamento"));
				item.setDataVeiculo(rs.getTimestamp("data_veiculo"));
				item.setDataAlerta(rs.getTimestamp("data_alerta"));
				item.setDataOcorrencia(rs.getTimestamp("data_ocorrencia"));
				item.setNomeUsuarioOcorrencia(rs.getString("nome_usuario_ocorrencia"));
				item.setPlacaMonitorada(rs.getString("placa_monitorada"));
				item.setPlacaLida(rs.getString("placa_lida"));
				item.setIdGrupo(idGrupo);
				item.setGrupo(rs.getString("grupo"));
				item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				item.setIdTipoNotificacao(UUID.fromString(rs.getString("id_tipo_notificacao")));
				item.setTipoNotificacao(rs.getString("tipo_notificacao"));
				item.setIdTipoRegistro(UUID.fromString(rs.getString("id_tipo_registro")));
				item.setTipoRegistro(rs.getString("tipo_registro"));
				item.setIdStatusNotificacao(UUID.fromString(rs.getString("id_status_notificacao")));
				item.setStatusNotificacao(rs.getString("status_notificacao"));
				item.setRemetente(rs.getString("remetente"));
				
				//Adicionar destinatários da notificação
				List<InternetAddress> destinatarios = GruposNotificacao.ObterEmailsPorIdGrupo(idGrupo);
				item.setDestinatarios(destinatarios);
				
				//Adicionar imagens para anexo da notificação
				List<VeiculoImagem> imagens = VeiculoImagens.obterListaImagensPorIdAlerta(idAlerta);
				item.setImagens(imagens);
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			String msg = "Erro ao montar SQL (ObterNotificacoesPendentesEmail):: ";
			logger.error(msg);
			throw new SQLException(msg, e);
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
	
	public static Queue<Notificacao> ObterNotificacoesPendentesSMS(UUID idTipoRegistro) throws ConexaoException, SQLException 
	{
		Queue<Notificacao> listaRet = new LinkedList<Notificacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" EXEC muralha.spu_ObterNotificacoesPendentes ?, ?, ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoRegistro.toString());
			ps.setString(2, StatusNotificacao.Status.PENDENTE.GetID().toString());
			ps.setString(3, TipoNotificacao.Tipo.SMS.GetID().toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				Notificacao item = new Notificacao();

				Integer idGrupo = rs.getInt("id_grupo");
				UUID idAlerta = UUID.fromString(rs.getString("id_alerta"));
				UUID idOcorrencia = rs.getString("id_ocorrencia") != null ? UUID.fromString(rs.getString("id_ocorrencia")) : null;
				
				item.setId(UUID.fromString(rs.getString("id")));
				item.setDataCadastro(rs.getDate("data_cadastro"));
				item.setIdOcorrencia(idOcorrencia);
				item.setIdAlerta(idAlerta);
				item.setEquipamento(rs.getString("equipamento"));
				item.setEquipamentoSMS(rs.getString("equipamento_sms"));
				item.setDataVeiculo(rs.getTimestamp("data_veiculo"));
				item.setDataAlerta(rs.getTimestamp("data_alerta"));
				item.setDataOcorrencia(rs.getTimestamp("data_ocorrencia"));
				item.setNomeUsuarioOcorrencia(rs.getString("nome_usuario_ocorrencia"));
				item.setPlacaMonitorada(rs.getString("placa_monitorada"));
				item.setPlacaLida(rs.getString("placa_lida"));
				item.setIdGrupo(idGrupo);
				item.setGrupo(rs.getString("grupo"));
				item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				item.setTipoAlertaOcorrenciaSMS(rs.getString("tipo_alerta_ocorrencia_sms"));
				item.setIdTipoNotificacao(UUID.fromString(rs.getString("id_tipo_notificacao")));
				item.setTipoNotificacao(rs.getString("tipo_notificacao"));
				item.setIdTipoRegistro(UUID.fromString(rs.getString("id_tipo_registro")));
				item.setTipoRegistro(rs.getString("tipo_registro"));
				item.setIdStatusNotificacao(UUID.fromString(rs.getString("id_status_notificacao")));
				item.setStatusNotificacao(rs.getString("status_notificacao"));
				item.setRemetente(rs.getString("remetente"));
				
				//Adicionar destinatários da notificação
				List<String> destinatariosSMS = GruposNotificacao.ObterTelefonesPorIdGrupo(idGrupo);
				item.setDestinatariosSMS(destinatariosSMS);
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			String msg = "Erro ao montar SQL (ObterNotificacoesPendentesSMS):: ";
			logger.error(msg);
			throw new SQLException(msg, e);
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
	
	public static boolean AtualizarNotificacao(UUID idTipoRegistro, UUID idNotificacao, UUID idStatusNotificacao) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		TipoRegistro.Tipo tipoRegistro = null;
		
		try
		{
			tipoRegistro = TipoRegistro.Tipo.GetValue(idTipoRegistro);
			
			if (idTipoRegistro.equals(TipoRegistro.Tipo.IRREGULARIDADES.GetID()))
			{
				sbSQL.append(" UPDATE muralha.ocorrencia_notificacao ");
			}
			else
			{
				sbSQL.append(" UPDATE muralha.alerta_notificacao ");
			}
			sbSQL.append(" SET    id_status_notificacao = ?, ");
			sbSQL.append(" 		  data_processado = ? ");
			sbSQL.append(" WHERE id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idStatusNotificacao.toString());
			ps.setTimestamp(2, new Timestamp(new Date().getTime()));
			ps.setString(3, idNotificacao.toString());
			
			retorno = (ps.executeUpdate() == 1);
		}
		catch(Exception e)
		{
			String msgErro = "Erro ao atualizar notificação de " + tipoRegistro.GetDescricao().trim() + "!";
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

	public static String ObterAssuntoEmail(Notificacao notificacao)
	{
		String assunto = null;
		
		assunto = ("[" + notificacao.getTipoRegistro().trim() + "] " + notificacao.getTipoAlertaOcorrencia().toUpperCase().trim());
		
		return assunto;
	}
	
	public static String ObterCorpoEmail(Notificacao notificacao)
	{
		String corpo = null, texto = null;
		StringBuilder sbEmail = new StringBuilder();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		texto = (notificacao.getIdTipoRegistro().equals(TipoRegistro.Tipo.IRREGULARIDADES.GetID()) ? "registrada uma nova irregularidade" : "registrado um novo alerta");
		
		sbEmail.append("<p>Olá!</p>");
		sbEmail.append("<p>Foi " + texto + " de <b style='color: red;'>" + notificacao.getTipoAlertaOcorrencia().toUpperCase().trim() + "!</b></p>");
		sbEmail.append("<table border='1' solid black>");
		sbEmail.append("	<tr style='background-color:#E6E6E6'>");
		sbEmail.append("		<th style='padding:10px'>Local</th>");
		sbEmail.append("		<th style='padding:10px'>Data</th>");
		sbEmail.append("		<th style='padding:10px'>Placa</th>");
		sbEmail.append("	</tr>");
		sbEmail.append("	<tr>");
		sbEmail.append("		<td align='center' style='padding:10px'>" + notificacao.getEquipamento().trim() + "</td>");
		sbEmail.append("		<td align='center' style='padding:10px'>" + df.format(notificacao.getDataVeiculo()) + "</td>");
		sbEmail.append("		<td align='center' style='padding:10px'>" + notificacao.getPlacaLida().trim() + "</td>");
		sbEmail.append("	</tr>");
		sbEmail.append("</table>");
		sbEmail.append("<br>");
		
		if (!LINK_PAGINA_DETALHE_NOTIFICACAO.trim().equals(""))
		{
			texto = (notificacao.getIdTipoRegistro().equals(TipoRegistro.Tipo.IRREGULARIDADES.GetID()) ? "da irregularidade" : "do alerta");
			
			sbEmail.append("<p>Para mais informações a respeito " + texto + ", por gentileza, utilizar o <i>link</i> abaixo:</p>");
			String linkTelaAlerta = ObterLinkDetalheNotificacao(notificacao.getIdAlerta());
			
			texto = (notificacao.getIdTipoRegistro().equals(TipoRegistro.Tipo.IRREGULARIDADES.GetID()) ? "da irregularidade" : "do Alerta");
			
			sbEmail.append("<h3><a href='"+linkTelaAlerta+"' target='_blank'>Abrir detalhes " + texto + "</a></h3>");
		}
		
		sbEmail.append("<br>");
		sbEmail.append("<p>Atensiosamente</p>");
		
		corpo = sbEmail.toString();
		
		return corpo;
	}
	
	public static String ObterLinkDetalheNotificacao(UUID id)
	{
		String linkTelaAlerta = null; 
		
		try
		{
			linkTelaAlerta = LINK_PAGINA_DETALHE_NOTIFICACAO + "?idAlerta=" + id.toString();
		}
		catch (Exception e)
		{
			logger.error("Erro ao formatar link para tela de notificação!)", e);
		}
		
		return linkTelaAlerta;
	}
	
	public static String ObterMsgSMS(Notificacao notificacao)
	{
		StringBuilder msg = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String msgSMS = null;
		int cnt = 0;
		
		try
		{
			String longURL = ObterLinkDetalheNotificacao(notificacao.getIdAlerta());
			String url = EncurtadorURL.EncurtarUrlBitly(longURL);
			
			msg.append(notificacao.getTipoAlertaOcorrenciaSMS().toUpperCase().trim());
			
			if (url != null && !url.equals(""))
				msg.append("\r\nDetalhes: " + url);
			
			msg.append("\r\n" + notificacao.getEquipamentoSMS());
			msg.append("\r\n" + sdf.format(notificacao.getDataVeiculo()));
			msg.append("\r\n" + notificacao.getPlacaLida().toUpperCase().trim());
			
			cnt = msg.length() > 159 ? 159 : msg.length();
			
			msgSMS = Funcoes.removeAcentosAlt(msg.toString().substring(0, cnt));
		}
		catch (Exception e)
		{
			logger.error("Erro ao obter mensagem SMS!", e);
		}
		
		return msgSMS;
	}
	
	public static String ObterLinkCurtoDetalheNotificacao(String longURL)
	{
		String link = null;
		
		try
		{
			link = EncurtadorURL.EncurtarUrlBitly(longURL);
		}
		catch (Exception e)
		{
			logger.error("Erro ao obter URL curta da notificação.", e);
		}
		
		return link;
	}
}
