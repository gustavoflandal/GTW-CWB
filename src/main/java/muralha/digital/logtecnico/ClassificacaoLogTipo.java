package muralha.digital.logtecnico;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Classifica dbo.log_alerta pelo domínio dbo.log_tipo (32 tipos). Os tipos de mobilidade
 * (29-33 e 115-119: circulação irregular, congestionamento, bloqueio...) não são logs
 * técnicos e ficam de fora (são escopo dos alertas operacionais, Fase 6).
 */
public final class ClassificacaoLogTipo {

    static final class Classe {
        final CategoriaLog categoria;
        final SeveridadeLog severidade;
        final RegistroLogTecnico.Transicao transicao;
        final String grupo;

        Classe(CategoriaLog c, SeveridadeLog s, RegistroLogTecnico.Transicao t, String g) {
            categoria = c; severidade = s; transicao = t; grupo = g;
        }
    }

    private static final Map<Integer, Classe> TIPOS = new HashMap<>();
    private static final RegistroLogTecnico.Transicao INI = RegistroLogTecnico.Transicao.INICIO;
    private static final RegistroLogTecnico.Transicao FIM = RegistroLogTecnico.Transicao.FIM;

    static {
        t(11, CategoriaLog.USUARIO, SeveridadeLog.INFO, null, null);           // login
        t(12, CategoriaLog.USUARIO, SeveridadeLog.INFO, null, null);           // logout
        t(13, CategoriaLog.PARAMETRO, SeveridadeLog.INFO, null, null);         // alteração de configurações
        t(14, CategoriaLog.USUARIO, SeveridadeLog.INFO, null, null);           // cadastramento de senhas
        t(15, CategoriaLog.INTERVENCAO, SeveridadeLog.INFO, INI, "MANUTENCAO");
        t(16, CategoriaLog.INTERVENCAO, SeveridadeLog.INFO, FIM, "MANUTENCAO");
        t(17, CategoriaLog.INTERVENCAO, SeveridadeLog.INFO, INI, "AFERICAO");
        t(18, CategoriaLog.INTERVENCAO, SeveridadeLog.INFO, FIM, "AFERICAO");
        t(19, CategoriaLog.SOFTWARE, SeveridadeLog.ALERTA, null, null);        // numeração sequencial reiniciada
        t(21, CategoriaLog.ENERGIA, SeveridadeLog.INFO, FIM, "ENERGIA");       // com energia
        t(22, CategoriaLog.ENERGIA, SeveridadeLog.ALERTA, INI, "ENERGIA");     // sem energia (bateria)
        t(23, CategoriaLog.SENSOR, SeveridadeLog.ERRO, null, null);            // falha nos sensores
        t(24, CategoriaLog.ENERGIA, SeveridadeLog.ALERTA, null, null);         // tensão da bateria
        t(25, CategoriaLog.HARDWARE, SeveridadeLog.ALERTA, null, null);        // temperatura interna
        t(26, CategoriaLog.HARDWARE, SeveridadeLog.ERRO, null, null);          // falha de comunicação com a câmera
        t(27, CategoriaLog.HARDWARE, SeveridadeLog.ERRO, INI, "SEMAFORO");
        t(28, CategoriaLog.HARDWARE, SeveridadeLog.INFO, FIM, "SEMAFORO");
        t(110, CategoriaLog.SINCRONISMO, SeveridadeLog.INFO, null, null);      // ajuste de relógio
        t(111, CategoriaLog.SINCRONISMO, SeveridadeLog.INFO, null, null);      // horário de verão
        t(112, CategoriaLog.COMUNICACAO, SeveridadeLog.INFO, FIM, "CONEXAO_SERVIDOR");
        t(113, CategoriaLog.COMUNICACAO, SeveridadeLog.ALERTA, INI, "CONEXAO_SERVIDOR");
        t(114, CategoriaLog.SOFTWARE, SeveridadeLog.INFO, null, null);         // cópia de arquivos
    }

    private ClassificacaoLogTipo() {}

    private static void t(int id, CategoriaLog c, SeveridadeLog s, RegistroLogTecnico.Transicao tr, String g) {
        TIPOS.put(id, new Classe(c, s, tr, g));
    }

    /** Classe do tipo, ou nulo se o tipo não for log técnico. */
    static Classe classificar(int idTipo) {
        return TIPOS.get(idTipo);
    }

    /** ids de log_tipo que atendem às categorias e severidades (vazias = todas). Ordenados. */
    static TreeSet<Integer> tipos(Collection<CategoriaLog> categorias, Collection<SeveridadeLog> severidades) {
        TreeSet<Integer> ids = new TreeSet<>();
        for (Map.Entry<Integer, Classe> e : TIPOS.entrySet()) {
            boolean cat = categorias == null || categorias.isEmpty() || categorias.contains(e.getValue().categoria);
            boolean sev = severidades == null || severidades.isEmpty() || severidades.contains(e.getValue().severidade);
            if (cat && sev) ids.add(e.getKey());
        }
        return ids;
    }
}
