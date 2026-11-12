package muralha.digital.auditoria;

import com.consilux.lib.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cache das regras de captura (dbo.sis_log_regra) com recarga sob demanda.
 *
 * As regras ficam em memória num List imutável referenciado por campo volátil,
 * permitindo troca atômica no recarregamento sem travar a avaliação. A primeira
 * carga é preguiçosa (na primeira avaliação) e a tela administrativa chama
 * {@link #recarregar()} após salvar alterações, sem necessidade de redeploy.
 */
public final class MotorRegras {

    private static final Logger log = Logger.getLogger(MotorRegras.class.getName());

    private static volatile List<RegraCaptura> cache = null;

    private MotorRegras() {}

    /** Avalia rota+método e devolve a primeira regra ativa que casar, ou null. */
    public static RegraCaptura avaliar(String rota, String metodo) {
        List<RegraCaptura> regras = cache;
        if (regras == null) {
            regras = carregar(); // tenta carregar; se falhar, volta lista vazia SEM envenenar o cache
        }
        if (rota == null) rota = "";
        for (RegraCaptura r : regras) {
            if (r.getMetodo() != null && !r.getMetodo().equalsIgnoreCase(metodo)) {
                continue;
            }
            if (casaPadrao(rota, r.getPadraoRota())) {
                return r;
            }
        }
        return null;
    }

    /** Força a releitura das regras do banco (chamado pela tela de configuração). */
    public static synchronized void recarregar() {
        List<RegraCaptura> lista = carregarDoBanco();
        if (lista != null) cache = lista; // só substitui se a leitura teve sucesso
    }

    /**
     * Carrega preguiçosamente. Em caso de falha (ex.: conexão ainda não
     * configurada no startup), NÃO grava no cache e devolve lista vazia
     * transitória, para que a próxima requisição tente novamente.
     */
    private static synchronized List<RegraCaptura> carregar() {
        if (cache != null) return cache;
        List<RegraCaptura> lista = carregarDoBanco();
        if (lista != null) {
            cache = lista;
            return cache;
        }
        return java.util.Collections.emptyList();
    }

    /** Devolve a lista de regras, ou {@code null} se a leitura falhar. */
    private static List<RegraCaptura> carregarDoBanco() {
        List<RegraCaptura> lista = new ArrayList<>();
        String sql =
            "SELECT id, ordem, descricao, padrao_rota, metodo, capturar, nivel, " +
            "funcionalidade, registrar_parametros, mascarar_campos, garantido, ativo " +
            "FROM dbo.sis_log_regra WHERE ativo = 1 ORDER BY ordem, id";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegraCaptura r = new RegraCaptura();
                    r.setId(rs.getInt("id"));
                    r.setOrdem(rs.getInt("ordem"));
                    r.setDescricao(rs.getString("descricao"));
                    r.setPadraoRota(rs.getString("padrao_rota"));
                    r.setMetodo(rs.getString("metodo"));
                    r.setCapturar(rs.getBoolean("capturar"));
                    r.setNivel(rs.getString("nivel"));
                    r.setFuncionalidade(rs.getString("funcionalidade"));
                    r.setRegistrarParametros(rs.getBoolean("registrar_parametros"));
                    r.setMascararCampos(rs.getString("mascarar_campos"));
                    r.setGarantido(rs.getBoolean("garantido"));
                    r.setAtivo(rs.getBoolean("ativo"));
                    lista.add(r);
                }
            }
            log.info("[MotorLogs] " + lista.size() + " regras de captura carregadas.");
        } catch (Exception e) {
            // Falha de leitura (ex.: conexao ainda nao configurada): devolve null para
            // nao envenenar o cache; o motor opera em modo seguro ate a proxima tentativa.
            log.log(Level.WARNING, "[MotorLogs] Falha ao carregar regras de captura; nova tentativa na proxima requisicao.", e);
            lista = null;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return lista;
    }

    /**
     * Casamento de padrão simples: '%' é curinga (zero ou mais caracteres),
     * comparação sem distinção de maiúsculas/minúsculas. Sem '%' exige igualdade.
     */
    private static boolean casaPadrao(String rota, String padrao) {
        if (padrao == null || padrao.isEmpty()) return false;
        if (padrao.indexOf('%') < 0) {
            return rota.equalsIgnoreCase(padrao);
        }
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < padrao.length(); i++) {
            char c = padrao.charAt(i);
            if (c == '%') {
                regex.append(".*");
            } else {
                regex.append(Pattern.quote(String.valueOf(c)));
            }
        }
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(rota).matches();
    }
}
