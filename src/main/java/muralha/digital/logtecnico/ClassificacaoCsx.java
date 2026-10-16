package muralha.digital.logtecnico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Classifica os eventos do Captura (dbo.eventos_csx) pelos domínios
 * eventos_csx_desc_categoria e eventos_csx_desc_evento: categoria normalizada e,
 * quando o evento abre ou fecha um período, a transição e o grupo.
 * Regra: primeiro o par (categoria, evento); depois a categoria; senão SOFTWARE.
 */
public final class ClassificacaoCsx {

    /** Categoria, transição e grupo de um evento. */
    public static final class Classe {
        public final CategoriaLog categoria;
        public final RegistroLogTecnico.Transicao transicao;
        public final String grupo;

        Classe(CategoriaLog categoria, RegistroLogTecnico.Transicao transicao, String grupo) {
            this.categoria = categoria;
            this.transicao = transicao;
            this.grupo = grupo;
        }
    }

    public static final CategoriaLog PADRAO = CategoriaLog.SOFTWARE;

    private static final RegistroLogTecnico.Transicao INI = RegistroLogTecnico.Transicao.INICIO;
    private static final RegistroLogTecnico.Transicao FIM = RegistroLogTecnico.Transicao.FIM;

    /** id_categoria → categoria padrão. TreeMap para SQL determinístico. */
    private static final Map<Integer, CategoriaLog> POR_CATEGORIA = new TreeMap<>();
    /** id_categoria → (id_evento → classe). */
    private static final Map<Integer, Map<Integer, Classe>> POR_EVENTO = new TreeMap<>();

    static {
        // Captura
        categoria(1, CategoriaLog.SOFTWARE);          // CAPTURA_APPLICATION
        evento(1, 14, CategoriaLog.SINCRONISMO, null, null);   // sincronismo de horário falhou
        evento(1, 38, CategoriaLog.SINCRONISMO, null, null);   // sincronismo bem sucedido
        evento(1, 40, CategoriaLog.USUARIO, null, null);       // login
        evento(1, 41, CategoriaLog.USUARIO, null, null);       // logout
        evento(1, 42, CategoriaLog.USUARIO, null, null);       // login falhou
        categoria(2, CategoriaLog.PARAMETRO);         // CAPTURA_CONFIG
        evento(2, 15, CategoriaLog.INTERVENCAO, INI, "MANUTENCAO");     // manutenção aberta
        evento(2, 16, CategoriaLog.INTERVENCAO, FIM, "MANUTENCAO");     // manutenção fechada
        evento(2, 19, CategoriaLog.PARAMETRO, INI, "CONFIGURACAO");     // configurações abertas
        evento(2, 20, CategoriaLog.PARAMETRO, FIM, "CONFIGURACAO");     // configurações fechadas
        evento(2, 30, CategoriaLog.INTERVENCAO, INI, "AFERICAO");       // modo aferição ligado
        evento(2, 31, CategoriaLog.INTERVENCAO, FIM, "AFERICAO");       // modo aferição desligado
        evento(2, 47, CategoriaLog.SOFTWARE, null, null);
        evento(2, 53, CategoriaLog.SOFTWARE, null, null);
        evento(2, 54, CategoriaLog.SOFTWARE, null, null);
        evento(2, 56, CategoriaLog.SOFTWARE, null, null);
        categoria(3, CategoriaLog.HARDWARE);          // CAPTURA_HARDWARE
        evento(3, 8, CategoriaLog.ENERGIA, INI, "ENERGIA");    // rede elétrica sem energia (bateria)
        evento(3, 7, CategoriaLog.ENERGIA, FIM, "ENERGIA");    // retorno da rede elétrica
        evento(3, 6, CategoriaLog.ENERGIA, FIM, "ENERGIA");    // rede elétrica com energia
        evento(3, 43, CategoriaLog.HARDWARE, INI, "SEMAFORO"); // falha na sequência semafórica
        evento(3, 44, CategoriaLog.HARDWARE, FIM, "SEMAFORO"); // sequência semafórica OK
        evento(3, 18, CategoriaLog.HARDWARE, INI, "DIV");      // erro no DIV
        evento(3, 17, CategoriaLog.HARDWARE, FIM, "DIV");      // DIV OK
        categoria(4, CategoriaLog.COMUNICACAO);       // CAPTURA_NETWORK
        evento(4, 10, CategoriaLog.COMUNICACAO, INI, "CONEXAO_SERVIDOR"); // desconectado
        evento(4, 9, CategoriaLog.COMUNICACAO, FIM, "CONEXAO_SERVIDOR");  // conectado
        categoria(5, CategoriaLog.SENSOR);            // CAPTURA_VEHICLE_DETECTION
        categoria(1002, CategoriaLog.PARAMETRO);      // CONFIG_EQUIP_APP_CONFIG
        categoria(2001, CategoriaLog.SOFTWARE);       // GTW
        evento(2001, 2001, CategoriaLog.USUARIO, null, null);  // usuário criado
        evento(2001, 2002, CategoriaLog.USUARIO, null, null);  // usuário modificado
        categoria(2002, CategoriaLog.COMUNICACAO);    // GTW_REMOTE
        categoria(2003, CategoriaLog.SOFTWARE);       // GTW_EMAIL
        categoria(3001, CategoriaLog.HARDWARE);       // CSXCAM
        categoria(3002, CategoriaLog.PARAMETRO);      // CSXCAM_AGENDA
        categoria(4001, CategoriaLog.COMUNICACAO);    // DIALUP
        categoria(4002, CategoriaLog.COMUNICACAO);    // DIALUP_MODEM3G
        categoria(5000, CategoriaLog.HARDWARE);       // VELSIS
        categoria(5001, CategoriaLog.SINCRONISMO);    // "OCR": replicação de ConfigEquip e tabelas de hash
        categoria(5002, CategoriaLog.ENERGIA);
        categoria(5003, CategoriaLog.SOFTWARE);
        categoria(5004, CategoriaLog.SENSOR);
        categoria(5005, CategoriaLog.HARDWARE);       // semáforo
        categoria(5006, CategoriaLog.SOFTWARE);       // watchdog
        categoria(5007, CategoriaLog.HARDWARE);       // câmera
        // Categorias padronizadas 10001-10019 (sem uso no DEV em 2026-10-16)
        categoria(10001, CategoriaLog.HARDWARE);
        categoria(10002, CategoriaLog.HARDWARE);
        categoria(10003, CategoriaLog.SOFTWARE);
        categoria(10004, CategoriaLog.SOFTWARE);
        categoria(10005, CategoriaLog.ENERGIA);
        categoria(10006, CategoriaLog.ENERGIA);
        categoria(10007, CategoriaLog.COMUNICACAO);
        categoria(10008, CategoriaLog.COMUNICACAO);
        categoria(10009, CategoriaLog.SINCRONISMO);
        categoria(10010, CategoriaLog.SINCRONISMO);
        categoria(10011, CategoriaLog.USUARIO);
        categoria(10012, CategoriaLog.PARAMETRO);
        categoria(10013, CategoriaLog.INTERVENCAO);
        categoria(10014, CategoriaLog.PROCESSAMENTO);
        categoria(10016, CategoriaLog.SENSOR);
        categoria(10018, CategoriaLog.SOFTWARE);
        categoria(10019, CategoriaLog.HARDWARE);
    }

    /** Transições das categorias padronizadas (valem para qualquer id_evento). */
    private static final Map<Integer, Classe> TRANSICAO_CATEGORIA = new HashMap<>();

    static {
        TRANSICAO_CATEGORIA.put(10001, new Classe(CategoriaLog.HARDWARE, INI, "FALHA"));
        TRANSICAO_CATEGORIA.put(10002, new Classe(CategoriaLog.HARDWARE, FIM, "FALHA"));
        TRANSICAO_CATEGORIA.put(10004, new Classe(CategoriaLog.SOFTWARE, INI, "FORA_DE_OPERACAO"));
        TRANSICAO_CATEGORIA.put(10003, new Classe(CategoriaLog.SOFTWARE, FIM, "FORA_DE_OPERACAO"));
        TRANSICAO_CATEGORIA.put(10005, new Classe(CategoriaLog.ENERGIA, INI, "ENERGIA"));
        TRANSICAO_CATEGORIA.put(10006, new Classe(CategoriaLog.ENERGIA, FIM, "ENERGIA"));
        TRANSICAO_CATEGORIA.put(10007, new Classe(CategoriaLog.COMUNICACAO, INI, "COMUNICACAO"));
        TRANSICAO_CATEGORIA.put(10008, new Classe(CategoriaLog.COMUNICACAO, FIM, "COMUNICACAO"));
    }

    private ClassificacaoCsx() {}

    private static void categoria(int idCategoria, CategoriaLog c) {
        POR_CATEGORIA.put(idCategoria, c);
    }

    private static void evento(int idCategoria, int idEvento, CategoriaLog c,
            RegistroLogTecnico.Transicao t, String grupo) {
        POR_EVENTO.computeIfAbsent(idCategoria, k -> new TreeMap<>()).put(idEvento, new Classe(c, t, grupo));
    }

    public static Classe classificar(int idCategoria, int idEvento) {
        Map<Integer, Classe> eventos = POR_EVENTO.get(idCategoria);
        if (eventos != null && eventos.containsKey(idEvento)) return eventos.get(idEvento);
        Classe porCategoria = TRANSICAO_CATEGORIA.get(idCategoria);
        if (porCategoria != null) return porCategoria;
        CategoriaLog c = POR_CATEGORIA.get(idCategoria);
        return new Classe(c != null ? c : PADRAO, null, null);
    }

    /**
     * Predicado SQL sobre {@code alias.id_categoria}/{@code alias.id_evento} que seleciona os
     * eventos classificados em alguma das categorias. Só usa inteiros das tabelas acima (nunca
     * entrada do usuário). Devolve nulo se todas as categorias forem aceitas.
     */
    public static String predicado(String alias, Collection<CategoriaLog> categorias) {
        if (categorias == null || categorias.isEmpty() || categorias.containsAll(java.util.EnumSet.allOf(CategoriaLog.class))) {
            return null;
        }
        List<String> partes = new ArrayList<>();
        String cat = alias + ".id_categoria";
        String ev = alias + ".id_evento";
        for (Map.Entry<Integer, Map<Integer, Classe>> e : POR_EVENTO.entrySet()) {
            List<Integer> sim = new ArrayList<>();
            for (Map.Entry<Integer, Classe> x : e.getValue().entrySet()) {
                if (categorias.contains(x.getValue().categoria)) sim.add(x.getKey());
            }
            if (!sim.isEmpty()) partes.add("(" + cat + " = " + e.getKey() + " AND " + ev + " IN " + lista(sim) + ")");
        }
        TreeSet<Integer> todasCategorias = new TreeSet<>(POR_CATEGORIA.keySet());
        todasCategorias.addAll(TRANSICAO_CATEGORIA.keySet());
        for (Integer idCat : todasCategorias) {
            Classe porCat = TRANSICAO_CATEGORIA.get(idCat);
            CategoriaLog c = porCat != null ? porCat.categoria : POR_CATEGORIA.get(idCat);
            if (!categorias.contains(c)) continue;
            Map<Integer, Classe> excecoes = POR_EVENTO.get(idCat);
            if (excecoes == null || excecoes.isEmpty()) {
                partes.add(cat + " = " + idCat);
            } else {
                partes.add("(" + cat + " = " + idCat + " AND " + ev + " NOT IN " + lista(excecoes.keySet()) + ")");
            }
        }
        if (categorias.contains(PADRAO)) {
            partes.add(cat + " NOT IN " + lista(todasCategorias));
        }
        return partes.isEmpty() ? "1 = 0" : "(" + String.join(" OR ", partes) + ")";
    }

    private static String lista(Collection<Integer> ids) {
        StringBuilder sb = new StringBuilder("(");
        for (Integer id : ids) {
            if (sb.length() > 1) sb.append(',');
            sb.append(id.intValue());
        }
        return sb.append(')').toString();
    }
}
