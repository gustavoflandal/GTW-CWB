package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** A cada 1 min: envia a fila muralha.chamado_notificacao (spec §3.5). */
@DisallowConcurrentExecution
public class ChamadoEmailJob implements Job {

    private static final Logger logger = Logger.getLogger(ChamadoEmailJob.class);

    private static final class Pendente {
        long id;
        String tipo;
        String destinatario;
        int tentativas;
        String protocolo;
        String titulo;
        String prioridade;
        Timestamp prazoAtendimento;
        Timestamp prazoResolucao;
        String justificativa;
    }

    @Override
    public void execute(JobExecutionContext ctx) {
        try (Connection conn = Conexao.getConexao()) {
            if (!"1".equals(ChamadoConfigDao.valor(conn, "chamado_email_ativo", "1"))) return;
            for (Pendente p : pendentes(conn)) {
                boolean enviado = false;
                String erro = null;
                try {
                    String html = EmailChamado.corpoHtml(p.tipo, p.protocolo, p.titulo, p.prioridade,
                            p.prazoAtendimento.toLocalDateTime(), p.prazoResolucao.toLocalDateTime(), p.justificativa);
                    enviado = EmailChamado.enviar(p.destinatario, EmailChamado.assunto(p.tipo, p.protocolo), html);
                    if (!enviado) erro = "Falha no envio SMTP (ver log)";
                } catch (Exception e) {
                    erro = e.getMessage();
                    logger.warn("ChamadoEmailJob: falha ao enviar notificacao " + p.id, e);
                }
                try {
                    registrar(conn, p.id, EmailChamado.statusAposTentativa(enviado, p.tentativas), erro);
                } catch (Exception e) {
                    logger.error("ChamadoEmailJob: falha ao registrar resultado da notificacao " + p.id, e);
                }
            }
        } catch (Exception e) {
            logger.error("ChamadoEmailJob: falha geral", e);
        }
    }

    private static List<Pendente> pendentes(Connection conn) throws Exception {
        List<Pendente> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 50 n.id, n.tipo, n.destinatario, n.tentativas, c.protocolo, c.titulo, p.nome AS prioridade, " +
                "c.dt_prazo_atendimento, c.dt_prazo_resolucao, " +
                "(SELECT TOP 1 h.texto FROM muralha.chamado_historico h WHERE h.id_chamado = c.id " +
                " AND h.tipo = 'RECLASSIFICACAO' ORDER BY h.id DESC) AS justificativa " +
                "FROM muralha.chamado_notificacao n JOIN muralha.chamado c ON c.id = n.id_chamado " +
                "JOIN muralha.chamado_prioridade p ON p.id = c.id_prioridade " +
                "WHERE n.status = 'PENDENTE' ORDER BY n.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pendente p = new Pendente();
                p.id = rs.getLong("id");
                p.tipo = rs.getString("tipo");
                p.destinatario = rs.getString("destinatario");
                p.tentativas = rs.getInt("tentativas");
                p.protocolo = rs.getString("protocolo");
                p.titulo = rs.getString("titulo");
                p.prioridade = rs.getString("prioridade");
                p.prazoAtendimento = rs.getTimestamp("dt_prazo_atendimento");
                p.prazoResolucao = rs.getTimestamp("dt_prazo_resolucao");
                p.justificativa = rs.getString("justificativa");
                lista.add(p);
            }
        }
        return lista;
    }

    private static void registrar(Connection conn, long id, String status, String erro) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.chamado_notificacao SET status = ?, tentativas = tentativas + 1, " +
                "dt_envio = CASE WHEN ? = 'ENVIADO' THEN SYSDATETIME() ELSE dt_envio END, erro = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setString(3, erro == null ? null : (erro.length() > 500 ? erro.substring(0, 500) : erro));
            ps.setLong(4, id);
            ps.executeUpdate();
        }
    }
}
