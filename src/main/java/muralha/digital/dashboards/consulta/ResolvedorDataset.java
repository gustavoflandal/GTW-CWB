package muralha.digital.dashboards.consulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Le do schema dash tudo o que o MontadorSql precisa para um dataset.
 */
public final class ResolvedorDataset {

    private static final String TABELAS_DO_DATASET =
            "SELECT ? UNION SELECT id_tabela FROM dash.dataset_tabela WHERE id_dataset = ?";

    private ResolvedorDataset() {
    }

    public static DatasetResolvido carregar(Connection conn, int idDataset) throws SQLException {
        int idBase;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_tabela_base FROM dash.dataset WHERE id = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ErroConsulta("Dataset nao encontrado: " + idDataset);
                }
                idBase = rs.getInt(1);
            }
        }

        DatasetResolvido ds = new DatasetResolvido(idDataset, idBase);

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ct.id, ct.schema_fisico, ct.nome_fisico, ct.chave_primaria, dt.alias " +
                "FROM dash.catalogo_tabela ct " +
                "LEFT JOIN dash.dataset_tabela dt ON dt.id_tabela = ct.id AND dt.id_dataset = ? " +
                "WHERE ct.ativo = 1 AND (ct.id = ? OR dt.id IS NOT NULL) " +
                "ORDER BY CASE WHEN ct.id = ? THEN 0 ELSE 1 END, dt.ordem")) {
            ps.setInt(1, idDataset);
            ps.setInt(2, idBase);
            ps.setInt(3, idBase);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.adicionarTabela(rs.getInt("id"), rs.getString("schema_fisico"),
                            rs.getString("nome_fisico"), rs.getString("alias"), rs.getString("chave_primaria"));
                }
            }
        }
        if (ds.tabela(idBase) == null) {
            throw new ErroConsulta("Tabela base do dataset esta inativa no catalogo");
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_tabela, nome_fisico, tipo FROM dash.catalogo_coluna " +
                "WHERE ativo = 1 AND id_tabela IN (" + TABELAS_DO_DATASET + ")")) {
            ps.setInt(1, idBase);
            ps.setInt(2, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.adicionarColuna(rs.getInt("id_tabela"), rs.getString("nome_fisico"), rs.getString("tipo"));
                }
            }
        }
        for (DatasetResolvido.Tabela t : ds.tabelas()) {
            if (t.chavePrimaria != null && !t.temColuna(t.chavePrimaria)) {
                ds.adicionarColuna(t.id, t.chavePrimaria, null);
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dc.nome_alias, dc.rotulo, dc.expressao, dc.agregacao, dc.tipo_campo, " +
                "       dc.id_coluna, cc.id_tabela, cc.nome_fisico, cc.tipo, cc.ativo " +
                "FROM dash.dataset_campo dc " +
                "LEFT JOIN dash.catalogo_coluna cc ON cc.id = dc.id_coluna " +
                "WHERE dc.id_dataset = ? ORDER BY dc.ordem, dc.id")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idTabela = rs.getInt("id_tabela");
                    Integer tabela = rs.wasNull() ? null : idTabela;
                    ds.adicionarCampo(
                            rs.getString("nome_alias"),
                            rs.getString("rotulo"),
                            tabela,
                            rs.getString("nome_fisico"),
                            rs.getString("tipo"),
                            rs.getBoolean("ativo"),
                            rs.getString("agregacao"),
                            rs.getString("tipo_campo"),
                            rs.getString("expressao"));
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_tabela_origem, coluna_origem, id_tabela_destino, coluna_destino, cardinalidade " +
                "FROM dash.dataset_relacao WHERE id_dataset = ? AND ativo = 1 ORDER BY id")) {
            ps.setInt(1, idDataset);
            lerRelacoes(ps, ds);
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_tabela_origem, coluna_origem, id_tabela_destino, coluna_destino, cardinalidade " +
                "FROM dash.catalogo_relacao WHERE ativo = 1 " +
                "AND id_tabela_origem IN (" + TABELAS_DO_DATASET + ") " +
                "AND id_tabela_destino IN (" + TABELAS_DO_DATASET + ") ORDER BY id")) {
            ps.setInt(1, idBase);
            ps.setInt(2, idDataset);
            ps.setInt(3, idBase);
            ps.setInt(4, idDataset);
            lerRelacoes(ps, ds);
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT coluna, operador, valor FROM dash.dataset_filtro_fixo " +
                "WHERE id_dataset = ? ORDER BY ordem, id")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.adicionarFiltroFixo(rs.getString("coluna"), rs.getString("operador"), rs.getString("valor"));
                }
            }
        }

        return ds;
    }

    private static void lerRelacoes(PreparedStatement ps, DatasetResolvido ds) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.adicionarRelacao(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5));
            }
        }
    }
}
