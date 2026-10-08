package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class RegistroDeFatoAnotacoes {

    private static Logger logger = LogManager.getLogger(RegistroDeFatoAnotacoes.class);

    /**
     * Insere uma única anotação.
     */
    public static void inserirAnotacao(RegistroDeFatoAnotacao anotacao) throws ConexaoException, SQLException {
        if (anotacao == null) return;

        String sql = "INSERT INTO muralha.registro_fato_anotacao "
                   + "(id_registro_fato, texto, id_usuario, data_criacao) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // id_registro_fato (LONG)
            if (anotacao.getIdRegistroFato() != null) {
                ps.setLong(1, anotacao.getIdRegistroFato());
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }

            // texto (VARCHAR)
            ps.setString(2, anotacao.getTexto());

            // id_usuario (INT) - trata nulo por segurança
            if (anotacao.getIdUsuario() != null) {
                ps.setInt(3, anotacao.getIdUsuario());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            // data_criacao (TIMESTAMP) - converte java.util.Date -> java.sql.Timestamp
            if (anotacao.getDataCriacao() != null) {
                ps.setTimestamp(4, new Timestamp(anotacao.getDataCriacao().getTime()));
            } else {
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }

            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao inserir anotacao do registro de fato", e);
            throw e;
        }
    }

    /**
     * Insere uma lista de anotações em batch.
     */
    public static void inserirListaAnotacoes(List<RegistroDeFatoAnotacao> anotacoes) throws ConexaoException, SQLException {
        if (anotacoes == null || anotacoes.isEmpty()) return;

        String sql = "INSERT INTO muralha.registro_fato_anotacao "
                   + "(id_registro_fato, texto, id_usuario, data_criacao) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (RegistroDeFatoAnotacao a : anotacoes) {
                ps.setLong(1, a.getIdRegistroFato());
                ps.setString(2, a.getTexto());
                ps.setInt(3, a.getIdUsuario());

                if (a.getDataCriacao() != null) {
                	ps.setTimestamp(4, new Timestamp(a.getDataCriacao().getTime()));
                } else {
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                }

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao inserir lista de anotacoes do registro de fato", e);
            throw e;
        }
    }

    /**
     * Remove uma anotação pelo ID.
     */
    public static void removerAnotacao(Integer idAnotacao) throws ConexaoException, SQLException {
        if (idAnotacao == null) return;

        String sql = "DELETE FROM muralha.registro_fato_anotacao WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAnotacao);
            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao remover anotacao do registro de fato", e);
            throw e;
        }
    }
    
    public static void removerListaAnotacoes(List<Integer> ids) throws ConexaoException, SQLException {
        if (ids == null || ids.isEmpty()) return;

        String placeholders = ids.stream()
                .map(i -> "?")
                .collect(Collectors.joining(", "));

        String sql = "DELETE FROM muralha.registro_fato_anotacao WHERE id IN (" + placeholders + ")";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int index = 1;
            for (Integer id : ids) {
                ps.setInt(index++, id);
            }

            ps.executeUpdate();

        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao remover anotacoes em lote: " + ids, e);
            throw e;
        }
    }


    /**
     * Útil se precisar apagar tudo ao remover o registro.
     */
    public static void removerAnotacoesPorRegistro(Integer idRegistroFato) throws ConexaoException, SQLException {
        if (idRegistroFato == null) return;

        String sql = "DELETE FROM muralha.registro_fato_anotacao WHERE id_registro_fato = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRegistroFato);
            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao remover anotacoes do registro de fato id=" + idRegistroFato, e);
            throw e;
        }
    }
}
