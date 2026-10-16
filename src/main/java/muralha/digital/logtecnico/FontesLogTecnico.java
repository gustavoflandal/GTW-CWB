package muralha.digital.logtecnico;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * As fontes do repositório de logs técnicos (TR 5.4.7.1-4). Cada uma consulta sua tabela de
 * origem só com colunas brutas; a classificação fica em {@link FonteLog#normalizar}.
 */
public final class FontesLogTecnico {

    private FontesLogTecnico() {}

    /** Todas as fontes, na ordem de exibição. */
    public static List<FonteLog> todas() {
        return Collections.unmodifiableList(Arrays.asList(
            new Captura(), new StatusConexao(), new StatusEnergia(), new Manutencao(), new LogAlerta(),
            new Disponibilidade(), new Processos(), new SlaLatencia(), new SlaPreprocessamento(),
            new Auditoria(), new ConfigChaveValorHist(), new ParametroHistorico()));
    }

    /** Mapa série do equipamento → id_local (local_vigente). */
    static final String SERIE_PARA_LOCAL =
        "(SELECT CAST(serie_equipamento AS VARCHAR(20)) AS serie, MIN(id_local) AS id_local " +
        " FROM local_vigente GROUP BY serie_equipamento)";

    // ------------------------------------------------------------------ utilitários

    static Timestamp ts(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v instanceof Timestamp ? (Timestamp) v : null;
    }

    static String str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v == null ? null : v.toString().trim();
    }

    static Integer inteiro(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof Boolean) return ((Boolean) v) ? 1 : 0;
        if (v != null) {
            try { return Integer.parseInt(v.toString().trim()); } catch (NumberFormatException ignorada) { return null; }
        }
        return null;
    }

    static String juntar(String a, String b) {
        boolean temA = a != null && !a.isEmpty(), temB = b != null && !b.isEmpty();
        if (temA && temB) return a + ": " + b;
        return temA ? a : (temB ? b : "");
    }

    private static boolean aceitaAlguma(FiltroLogTecnico f, CategoriaLog c, SeveridadeLog... sevs) {
        if (!f.aceita(c)) return false;
        for (SeveridadeLog s : sevs) if (f.aceita(s)) return true;
        return false;
    }

    private static FonteLog.Where periodo(String coluna, FiltroLogTecnico f) {
        return new FonteLog.Where().e(coluna + " >= ?", f.getInicio()).e(coluna + " < ?", f.getFim());
    }

    private static FonteLog.Where comLocais(FonteLog.Where w, String coluna, FiltroLogTecnico f) {
        return f.getIdLocais().isEmpty() ? w : w.emLista(coluna, f.getIdLocais());
    }


    // ------------------------------------------------------------------ fontes

    /** dbo.eventos_csx: eventos enviados pelos equipamentos (Captura) e pelo GTW. */
    static final class Captura implements FonteLog {
        public String origem() { return "CAPTURA"; }
        public String tabela() { return "dbo.eventos_csx"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            FonteLog.Where w = periodo("e.data_hora", f);
            comLocais(w, "lp.id_local", f);
            String cat = ClassificacaoCsx.predicado("e", f.getCategorias());
            if (cat != null) w.e(cat);
            if (!f.getSeveridades().isEmpty()) w.emLista("e.id_nivel", SeveridadeLog.niveisCsx(f.getSeveridades()));
            return w.montar(
                "SELECT e.id, e.data_hora, e.id_categoria, e.id_evento, e.id_nivel, e.mensagem, e.usuario, " +
                "       d.evento AS desc_evento, p.proprietario, lp.id_local " +
                "FROM dbo.eventos_csx e " +
                "JOIN dbo.eventos_csx_desc_proprietario p ON p.id_proprietario = e.id_proprietario " +
                "LEFT JOIN dbo.eventos_csx_desc_evento d ON d.id_evento = e.id_evento " +
                "LEFT JOIN " + SERIE_PARA_LOCAL + " lp ON lp.serie = LTRIM(RTRIM(p.proprietario))");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            int idCat = inteiro(m, "id_categoria"), idEv = inteiro(m, "id_evento");
            ClassificacaoCsx.Classe c = ClassificacaoCsx.classificar(idCat, idEv);
            Integer idLocal = inteiro(m, "id_local");
            String evento = str(m, "desc_evento");
            if (evento == null || evento.isEmpty()) evento = "Evento " + idCat + "/" + idEv;
            String descricao = juntar(evento, str(m, "mensagem"));
            if (idLocal == null) descricao = "[" + str(m, "proprietario") + "] " + descricao;
            return new RegistroLogTecnico(ts(m, "data_hora"), null, idLocal, origem(), c.categoria,
                SeveridadeLog.doNivelCsx(inteiro(m, "id_nivel")), descricao, str(m, "usuario"),
                "eventos_csx:" + m.get("id"), c.transicao, c.grupo);
        }
    }

    /** dbo.status_conexao: transições online (1) / offline (2) por equipamento. */
    static final class StatusConexao implements FonteLog {
        public String origem() { return "STATUS_CONEXAO"; }
        public String tabela() { return "dbo.status_conexao"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            return statusConsulta(f, CategoriaLog.COMUNICACAO, "dbo.status_conexao", "id_status_conexao", ", s.ip");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            int st = inteiro(m, "status");
            String ip = str(m, "ip");
            if (st == 2) return transicao(m, origem(), CategoriaLog.COMUNICACAO, SeveridadeLog.ALERTA,
                "Equipamento offline: conexão perdida", RegistroLogTecnico.Transicao.INICIO, "COMUNICACAO");
            if (st == 1) return transicao(m, origem(), CategoriaLog.COMUNICACAO, SeveridadeLog.INFO,
                "Equipamento online" + (ip == null || ip.isEmpty() ? "" : " (IP " + ip + ")"),
                RegistroLogTecnico.Transicao.FIM, "COMUNICACAO");
            return transicao(m, origem(), CategoriaLog.COMUNICACAO, SeveridadeLog.INFO,
                "Status de conexão não verificado", null, null);
        }
    }

    /** dbo.status_energia: transições com energia (1) / sem energia (2) por equipamento. */
    static final class StatusEnergia implements FonteLog {
        public String origem() { return "STATUS_ENERGIA"; }
        public String tabela() { return "dbo.status_energia"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            return statusConsulta(f, CategoriaLog.ENERGIA, "dbo.status_energia", "id_status_energia", "");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            int st = inteiro(m, "status");
            if (st == 2) return transicao(m, origem(), CategoriaLog.ENERGIA, SeveridadeLog.ALERTA,
                "Sem energia da rede elétrica", RegistroLogTecnico.Transicao.INICIO, "ENERGIA");
            if (st == 1) return transicao(m, origem(), CategoriaLog.ENERGIA, SeveridadeLog.INFO,
                "Energia da rede elétrica presente", RegistroLogTecnico.Transicao.FIM, "ENERGIA");
            return transicao(m, origem(), CategoriaLog.ENERGIA, SeveridadeLog.INFO,
                "Status de energia não verificado", null, null);
        }
    }

    private static FonteLog.Consulta statusConsulta(FiltroLogTecnico f, CategoriaLog c, String tabela, String pk, String extra) {
        if (!aceitaAlguma(f, c, SeveridadeLog.ALERTA, SeveridadeLog.INFO)) return null;
        FonteLog.Where w = periodo("s.data_atualizacao", f);
        comLocais(w, "s.id_local", f);
        if (!f.getSeveridades().isEmpty()) {
            List<Integer> st = new ArrayList<>();
            if (f.aceita(SeveridadeLog.ALERTA)) st.add(2);
            if (f.aceita(SeveridadeLog.INFO)) { st.add(1); st.add(0); }
            w.emLista("s.status", st);
        }
        return w.montar("SELECT s." + pk + " AS id, s.id_local, s.data_atualizacao AS data_hora, s.status" + extra +
            " FROM " + tabela + " s");
    }

    private static RegistroLogTecnico transicao(Map<String, Object> m, String origem, CategoriaLog c, SeveridadeLog s,
            String descricao, RegistroLogTecnico.Transicao t, String grupo) {
        return new RegistroLogTecnico(ts(m, "data_hora"), null, inteiro(m, "id_local"), origem, c, s, descricao,
            str(m, "usuario"), origem.toLowerCase() + ":" + m.get("id"), t, grupo);
    }

    /** dbo.manutencao: intervenções técnicas (cadastro do GTW clássico), com início e conclusão. */
    static final class Manutencao implements FonteLog {
        public String origem() { return "MANUTENCAO"; }
        public String tabela() { return "dbo.manutencao"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (!aceitaAlguma(f, CategoriaLog.INTERVENCAO, SeveridadeLog.INFO)) return null;
            String data = "COALESCE(m.data_inicio, m.data_ocorrencia, m.data_cadastro)";
            FonteLog.Where w = periodo(data, f);
            comLocais(w, "m.id_local", f);
            return w.montar(
                "SELECT m.id_manutencao AS id, m.id_local, " + data + " AS data_hora, m.data_conclusao, " +
                "       m.descricao, ms.descricao AS status_desc, u.usuario " +
                "FROM dbo.manutencao m " +
                "LEFT JOIN dbo.manutencao_status ms ON ms.id_status = m.id_status " +
                "LEFT JOIN dbo.sis_usuario u ON u.id_usuario = m.id_ultimo_usuario");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            String desc = "Manutenção" + (str(m, "descricao") == null ? "" : ": " + str(m, "descricao"))
                + (str(m, "status_desc") == null ? "" : " (" + str(m, "status_desc") + ")");
            return new RegistroLogTecnico(ts(m, "data_hora"), ts(m, "data_conclusao"), inteiro(m, "id_local"),
                origem(), CategoriaLog.INTERVENCAO, SeveridadeLog.INFO, desc, str(m, "usuario"),
                "manutencao:" + m.get("id"), null, "MANUTENCAO");
        }
    }

    /** dbo.log_alerta: estados enviados pelo equipamento, classificados por dbo.log_tipo. */
    static final class LogAlerta implements FonteLog {
        public String origem() { return "LOG_ALERTA"; }
        public String tabela() { return "dbo.log_alerta"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            Set<Integer> tipos = ClassificacaoLogTipo.tipos(f.getCategorias(), f.getSeveridades());
            if (tipos.isEmpty()) return null;
            FonteLog.Where w = periodo("l.data", f).emLista("l.id_tipo", tipos);
            comLocais(w, "lp.id_local", f);
            return w.montar(
                "SELECT l.id, l.data AS data_hora, l.id_tipo, t.descricao AS tipo_desc, l.serial, lp.id_local " +
                "FROM dbo.log_alerta l " +
                "LEFT JOIN dbo.log_tipo t ON t.id = l.id_tipo " +
                "LEFT JOIN " + SERIE_PARA_LOCAL + " lp ON lp.serie = l.serial");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            ClassificacaoLogTipo.Classe c = ClassificacaoLogTipo.classificar(inteiro(m, "id_tipo"));
            if (c == null) c = new ClassificacaoLogTipo.Classe(CategoriaLog.SOFTWARE, SeveridadeLog.INFO, null, null);
            Integer idLocal = inteiro(m, "id_local");
            String desc = str(m, "tipo_desc");
            if (idLocal == null && str(m, "serial") != null) desc = "[" + str(m, "serial") + "] " + desc;
            return new RegistroLogTecnico(ts(m, "data_hora"), null, idLocal, origem(), c.categoria, c.severidade,
                desc, null, "log_alerta:" + m.get("id"), c.transicao, c.grupo);
        }
    }

    /** muralha.equipamento_disponibilidade (Plano 18): só as mudanças de disponível ↔ indisponível. */
    static final class Disponibilidade implements FonteLog {
        public String origem() { return "DISPONIBILIDADE"; }
        public String tabela() { return "muralha.equipamento_disponibilidade"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (!aceitaAlguma(f, CategoriaLog.COMUNICACAO, SeveridadeLog.ALERTA, SeveridadeLog.INFO)) return null;
            FonteLog.Where w = periodo("d.dt_verificacao", f);
            comLocais(w, "d.id_local", f);
            String interno = w.montar(
                "SELECT d.id, d.id_local, CAST(d.dt_verificacao AS DATETIME) AS data_hora, " +
                "       CAST(d.disponivel AS INT) AS disponivel, d.minutos_offline, " +
                "       LAG(CAST(d.disponivel AS INT)) OVER (PARTITION BY d.id_local ORDER BY d.dt_verificacao) AS anterior " +
                "FROM muralha.equipamento_disponibilidade d").sql;
            List<Object> ps = w.montar("").parametros;
            String externo = "SELECT x.* FROM (" + interno + ") x " +
                "WHERE ((x.anterior IS NULL AND x.disponivel = 0) OR x.anterior <> x.disponivel)";
            if (!f.getSeveridades().isEmpty()) {
                if (!f.aceita(SeveridadeLog.ALERTA)) externo += " AND x.disponivel = 1";
                if (!f.aceita(SeveridadeLog.INFO)) externo += " AND x.disponivel = 0";
            }
            return new Consulta(externo, ps);
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            boolean disponivel = Integer.valueOf(1).equals(inteiro(m, "disponivel"));
            return transicao(m, origem(), CategoriaLog.COMUNICACAO,
                disponivel ? SeveridadeLog.INFO : SeveridadeLog.ALERTA,
                disponivel ? "Equipamento voltou a enviar passagens"
                    : "Equipamento sem passagens há " + inteiro(m, "minutos_offline") + " min",
                disponivel ? RegistroLogTecnico.Transicao.FIM : RegistroLogTecnico.Transicao.INICIO,
                "DISPONIBILIDADE");
        }
    }

    /** dbo.log_processos: execuções das rotinas de importação/processamento (sem equipamento). */
    static final class Processos implements FonteLog {
        public String origem() { return "PROCESSOS"; }
        public String tabela() { return "dbo.log_processos"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (f.restringeEquipamento() || !f.aceita(CategoriaLog.PROCESSAMENTO)
                    || p.getModoProcessos() == ParametrosLogTecnico.ModoProcessos.NENHUM) return null;
            boolean querErro = f.aceita(SeveridadeLog.ERRO);
            boolean querInfo = f.aceita(SeveridadeLog.INFO) && p.getModoProcessos() == ParametrosLogTecnico.ModoProcessos.TODOS;
            if (!querErro && !querInfo) return null;
            FonteLog.Where w = periodo("lp.data_inicio", f);
            String naoFinalizado = "(lp.data_finalizado IS NULL AND lp.data_atualizado < DATEADD(MINUTE, -?, GETDATE()))";
            if (querErro && !querInfo) {
                w.e(naoFinalizado, p.getProcessoAtrasoMin());
            } else if (!querErro) {
                w.e("NOT " + naoFinalizado, p.getProcessoAtrasoMin());
            }
            return w.montar(
                "SELECT lp.id, lp.nome, lp.data_inicio AS data_hora, lp.registros, lp.total, lp.data_atualizado, " +
                "       lp.data_finalizado, CASE WHEN lp.data_finalizado IS NULL AND lp.data_atualizado < " +
                "       DATEADD(MINUTE, -" + p.getProcessoAtrasoMin() + ", GETDATE()) THEN 1 ELSE 0 END AS atrasado " +
                "FROM dbo.log_processos lp");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            boolean atrasado = Integer.valueOf(1).equals(inteiro(m, "atrasado"));
            String nome = str(m, "nome");
            if (atrasado) {
                return RegistroLogTecnico.de(ts(m, "data_hora"), null, origem(), CategoriaLog.PROCESSAMENTO,
                    SeveridadeLog.ERRO, "Processo " + nome + " não finalizado (última atualização "
                        + ts(m, "data_atualizado") + ")", null, "log_processos:" + m.get("id"));
            }
            return new RegistroLogTecnico(ts(m, "data_hora"), ts(m, "data_finalizado"), null, origem(),
                CategoriaLog.PROCESSAMENTO, SeveridadeLog.INFO,
                "Processo " + nome + ": " + inteiro(m, "registros") + " registros (total " + inteiro(m, "total") + ")",
                null, "log_processos:" + m.get("id"), null, null);
        }
    }

    /** muralha.alerta_sla (Plano 07): só as janelas com violação do limiar de latência. */
    static final class SlaLatencia implements FonteLog {
        public String origem() { return "SLA_LATENCIA"; }
        public String tabela() { return "muralha.alerta_sla"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (f.restringeEquipamento() || !aceitaAlguma(f, CategoriaLog.PROCESSAMENTO, SeveridadeLog.ALERTA)) return null;
            return periodo("a.dt_alerta", f).e("a.violacao = 1").montar(
                "SELECT a.id, CAST(a.dt_alerta AS DATETIME) AS data_hora, a.total_passagens, a.percentil95_ms, " +
                "       a.threshold_ms, a.locais_top FROM muralha.alerta_sla a");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            return RegistroLogTecnico.de(ts(m, "data_hora"), null, origem(), CategoriaLog.PROCESSAMENTO,
                SeveridadeLog.ALERTA, "Latência P95 de " + inteiro(m, "percentil95_ms") + " ms acima do limiar de "
                    + inteiro(m, "threshold_ms") + " ms (" + inteiro(m, "total_passagens") + " passagens; locais mais lentos "
                    + str(m, "locais_top") + ")", null, "alerta_sla:" + m.get("id"));
        }
    }

    /** muralha.alerta_sla_preproc (Plano 10): verificações com registros acima de 72 h. */
    static final class SlaPreprocessamento implements FonteLog {
        public String origem() { return "SLA_PREPROCESSAMENTO"; }
        public String tabela() { return "muralha.alerta_sla_preproc"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (f.restringeEquipamento() || !aceitaAlguma(f, CategoriaLog.PROCESSAMENTO, SeveridadeLog.ALERTA)) return null;
            return periodo("a.dt_alerta", f).e("a.acima_72h > 0").montar(
                "SELECT a.id, CAST(a.dt_alerta AS DATETIME) AS data_hora, a.total_pendentes, a.acima_72h " +
                "FROM muralha.alerta_sla_preproc a");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            return RegistroLogTecnico.de(ts(m, "data_hora"), null, origem(), CategoriaLog.PROCESSAMENTO,
                SeveridadeLog.ALERTA, inteiro(m, "acima_72h") + " registros pendentes de pré-processamento há mais de 72 h"
                    + " (total pendente " + inteiro(m, "total_pendentes") + ")", null, "alerta_sla_preproc:" + m.get("id"));
        }
    }

    /** dbo.sis_log_auditoria (Plano 01): ações dos usuários. */
    static final class Auditoria implements FonteLog {
        public String origem() { return "AUDITORIA"; }
        public String tabela() { return "dbo.sis_log_auditoria"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (f.restringeEquipamento() || !aceitaAlguma(f, CategoriaLog.USUARIO, SeveridadeLog.INFO)) return null;
            return periodo("a.dt_operacao", f).montar(
                "SELECT a.id, CAST(a.dt_operacao AS DATETIME) AS data_hora, a.login AS usuario, a.funcionalidade, " +
                "       a.operacao, a.id_registro, a.descricao FROM dbo.sis_log_auditoria a");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            String desc = str(m, "funcionalidade") + " / " + str(m, "operacao")
                + (str(m, "descricao") == null || str(m, "descricao").isEmpty() ? "" : ": " + str(m, "descricao"));
            String ref = str(m, "id_registro");
            return RegistroLogTecnico.de(ts(m, "data_hora"), null, origem(), CategoriaLog.USUARIO, SeveridadeLog.INFO,
                desc, str(m, "usuario"), "sis_log_auditoria:" + m.get("id") + (ref == null ? "" : " (" + ref + ")"));
        }
    }

    /** muralha.config_chave_valor_hist: alterações de parâmetros gravadas pelas telas antigas. */
    static final class ConfigChaveValorHist implements FonteLog {
        public String origem() { return "CONFIG_HIST"; }
        public String tabela() { return "muralha.config_chave_valor_hist"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (f.restringeEquipamento() || !aceitaAlguma(f, CategoriaLog.PARAMETRO, SeveridadeLog.INFO)) return null;
            return periodo("h.data_atualizacao", f).montar(
                "SELECT h.id, h.data_atualizacao AS data_hora, h.evento, u.usuario " +
                "FROM muralha.config_chave_valor_hist h LEFT JOIN dbo.sis_usuario u ON u.id_usuario = h.id_usuario");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            return RegistroLogTecnico.de(ts(m, "data_hora"), null, origem(), CategoriaLog.PARAMETRO, SeveridadeLog.INFO,
                "Parâmetro alterado: " + str(m, "evento"), str(m, "usuario"), "config_chave_valor_hist:" + m.get("id"));
        }
    }

    /** muralha.parametro_historico (Plano 26): valor anterior e novo de cada parâmetro alterado. */
    static final class ParametroHistorico implements FonteLog {
        public String origem() { return "PARAMETRO_HISTORICO"; }
        public String tabela() { return "muralha.parametro_historico"; }

        public Consulta consulta(FiltroLogTecnico f, ParametrosLogTecnico p) {
            if (!aceitaAlguma(f, CategoriaLog.PARAMETRO, SeveridadeLog.INFO)) return null;
            FonteLog.Where w = periodo("ph.dt", f);
            if (!f.getIdLocais().isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (Integer id : f.getIdLocais()) ids.add(String.valueOf(id));
                w.e("ph.tipo_recurso = ?", HistoricoParametros.RECURSO_EQUIPAMENTO).emLista("ph.id_recurso", ids);
            }
            return w.montar(
                "SELECT ph.id, CAST(ph.dt AS DATETIME) AS data_hora, ph.tipo_recurso, ph.id_recurso, ph.parametro, " +
                "       ph.valor_anterior, ph.valor_novo, ph.login AS usuario, ph.origem AS origem_alteracao " +
                "FROM muralha.parametro_historico ph");
        }

        public RegistroLogTecnico normalizar(Map<String, Object> m) {
            String tipo = str(m, "tipo_recurso");
            Integer idLocal = HistoricoParametros.RECURSO_EQUIPAMENTO.equals(tipo) ? inteiro(m, "id_recurso") : null;
            String desc = tipo + " " + str(m, "id_recurso") + ": " + str(m, "parametro") + " de "
                + valor(str(m, "valor_anterior")) + " para " + valor(str(m, "valor_novo"));
            return RegistroLogTecnico.de(ts(m, "data_hora"), idLocal, origem(), CategoriaLog.PARAMETRO,
                SeveridadeLog.INFO, desc, str(m, "usuario"), "parametro_historico:" + m.get("id"));
        }

        private static String valor(String v) { return v == null ? "(vazio)" : "\"" + v + "\""; }
    }

    /** Categorias que cada fonte pode produzir (para a legenda e os testes). */
    static Set<CategoriaLog> categoriasPossiveis(FonteLog f) {
        if (f instanceof Captura) return EnumSet.allOf(CategoriaLog.class);
        if (f instanceof LogAlerta) return EnumSet.of(CategoriaLog.USUARIO, CategoriaLog.PARAMETRO, CategoriaLog.INTERVENCAO,
            CategoriaLog.SOFTWARE, CategoriaLog.ENERGIA, CategoriaLog.SENSOR, CategoriaLog.HARDWARE,
            CategoriaLog.SINCRONISMO, CategoriaLog.COMUNICACAO);
        if (f instanceof StatusConexao || f instanceof Disponibilidade) return EnumSet.of(CategoriaLog.COMUNICACAO);
        if (f instanceof StatusEnergia) return EnumSet.of(CategoriaLog.ENERGIA);
        if (f instanceof Manutencao) return EnumSet.of(CategoriaLog.INTERVENCAO);
        if (f instanceof Auditoria) return EnumSet.of(CategoriaLog.USUARIO);
        if (f instanceof ConfigChaveValorHist || f instanceof ParametroHistorico) return EnumSet.of(CategoriaLog.PARAMETRO);
        return EnumSet.of(CategoriaLog.PROCESSAMENTO);
    }
}
