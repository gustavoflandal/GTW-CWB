package muralha.digital.alerta.operacional;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcRepositorioRetencao implements RepositorioRetencao {

    private static final Logger logger = Logger.getLogger(JdbcRepositorioRetencao.class);

    private final Connection conn;

    public JdbcRepositorioRetencao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Integer> equipamentosComDados(int janelaHoras, int lote) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT TOP (?) s.id_local "
            + "FROM dbo.veiculo_sumarizado s "
            + "WHERE s.data >= CAST(DATEADD(HOUR, -?, GETDATE()) AS DATE) "
            + "AND s.trafego > 0 "
            + "ORDER BY s.id_local";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lote);
            ps.setInt(2, janelaHoras);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar equipamentos com dados: " + e.getMessage(), e);
        }
        return ids;
    }

    @Override
    public List<RegraRetencao.DadoHora> dadosPorEquipamento(int idLocal, int janelaHoras) {
        List<RegraRetencao.DadoHora> dados = new ArrayList<>();
        String sql = "SELECT s.hora, "
            + "AVG(s.media_velocidade) AS vel, "
            + "SUM(s.ocupacao) AS ocup, "
            + "SUM(s.trafego) AS traf "
            + "FROM dbo.veiculo_sumarizado s "
            + "WHERE s.id_local = ? "
            + "AND s.data >= CAST(DATEADD(HOUR, -?, GETDATE()) AS DATE) "
            + "AND s.hora >= DATEPART(HOUR, DATEADD(HOUR, -?, GETDATE())) "
            + "GROUP BY s.hora ORDER BY s.hora";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLocal);
            ps.setInt(2, janelaHoras);
            ps.setInt(3, janelaHoras);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dados.add(new RegraRetencao.DadoHora(
                        idLocal, rs.getInt("hora"),
                        rs.getDouble("vel"), rs.getInt("ocup"), rs.getInt("traf")));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao obter dados sumarizados do local " + idLocal + ": " + e.getMessage(), e);
        }
        return dados;
    }

    @Override
    public boolean alertaRecente(int idLocal, int esperaMinutos) {
        String sql = "SELECT TOP 1 1 FROM muralha.alerta a "
            + "JOIN muralha.alerta_retencao ar ON ar.id_alerta = a.id "
            + "WHERE ar.id_local = ? AND a.data >= DATEADD(MINUTE, -?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLocal);
            ps.setInt(2, esperaMinutos);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar alerta recente: " + e.getMessage(), e);
            return true;
        }
    }

    @Override
    public UUID gravarAlerta(int idLocal, RegraRetencao.Avaliacao avaliacao, UUID idTipoCongestionamento) {
        UUID idAlerta = UUID.randomUUID();
        UUID statusPendente = UUID.fromString("5479C6D9-7381-4492-99BE-442EF2E741B0");
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.alerta (id, id_tipo_alerta_ocorrencia, id_status_alerta, data, "
                    + "enviado_cliente, lembrete_visualizado, alerta_vinculado, assinado, supervisionado, privado, "
                    + "observacao) "
                    + "VALUES (?, ?, ?, GETDATE(), 0, 0, 0, 0, 0, 0, ?)")) {
                ps.setString(1, idAlerta.toString());
                ps.setString(2, idTipoCongestionamento.toString());
                ps.setString(3, statusPendente.toString());
                ps.setString(4, avaliacao.getDetalhe());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.alerta_retencao (id_alerta, id_local, horas_consecutivas, "
                    + "velocidade_media, ocupacao_media, severidade) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, idAlerta.toString());
                ps.setInt(2, idLocal);
                ps.setInt(3, avaliacao.getHorasConsecutivas());
                ps.setDouble(4, avaliacao.getVelocidadeMedia());
                ps.setInt(5, avaliacao.getOcupacaoMedia());
                ps.setString(6, avaliacao.getSeveridade().name());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Erro ao gravar alerta de retenção: " + e.getMessage(), e);
        }
        return idAlerta;
    }
}
