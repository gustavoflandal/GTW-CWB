package muralha.digital.logtecnico;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Monta períodos a partir de eventos de início e fim (TR 5.2.7.2): queda de energia → retorno,
 * desconexão → reconexão, manutenção aberta → fechada etc. A chave é equipamento + origem + grupo,
 * para não misturar fontes diferentes do mesmo fenômeno. Um segundo início com o período aberto
 * é ignorado (vale o primeiro); um fim sem início aberto é ignorado. Período sem fim fica em aberto.
 */
public final class CorrelacionadorIntervalos {

    private CorrelacionadorIntervalos() {}

    /** Período de indisponibilidade/intervenção. */
    public static final class Intervalo {
        private final Integer idLocal;
        private final String origem;
        private final String grupo;
        private final CategoriaLog categoria;
        private final Timestamp inicio;
        private final Timestamp fim;
        private final String descricaoInicio;
        private final String descricaoFim;

        Intervalo(RegistroLogTecnico ini, Timestamp fim, String descricaoFim) {
            this.idLocal = ini.getIdLocal();
            this.origem = ini.getOrigem();
            this.grupo = ini.getGrupo();
            this.categoria = ini.getCategoria();
            this.inicio = ini.getDataHora();
            this.fim = fim;
            this.descricaoInicio = ini.getDescricao();
            this.descricaoFim = descricaoFim;
        }

        public Integer getIdLocal() { return idLocal; }
        public String getOrigem() { return origem; }
        public String getGrupo() { return grupo; }
        public CategoriaLog getCategoria() { return categoria; }
        public Timestamp getInicio() { return inicio; }
        /** Nulo = em aberto. */
        public Timestamp getFim() { return fim; }
        public String getDescricaoInicio() { return descricaoInicio; }
        public String getDescricaoFim() { return descricaoFim; }

        /** Duração em ms até o fim, ou até {@code ate} se em aberto. */
        public long duracaoMs(Timestamp ate) {
            Timestamp f = fim != null ? fim : ate;
            return f == null ? 0 : Math.max(0, f.getTime() - inicio.getTime());
        }
    }

    public static List<Intervalo> montar(List<RegistroLogTecnico> registros) {
        List<RegistroLogTecnico> ordenados = new ArrayList<>(registros);
        ordenados.sort(Comparator.comparing(RegistroLogTecnico::getDataHora,
            Comparator.nullsLast(Comparator.naturalOrder())));

        List<Intervalo> r = new ArrayList<>();
        Map<String, RegistroLogTecnico> abertos = new HashMap<>();
        for (RegistroLogTecnico reg : ordenados) {
            if (reg.getDataHora() == null) continue;
            if (reg.getDataHoraFim() != null || (reg.getGrupo() != null && reg.getTransicao() == null
                    && "MANUTENCAO".equals(reg.getGrupo()))) {
                r.add(new Intervalo(reg, reg.getDataHoraFim(), null));
                continue;
            }
            if (reg.getTransicao() == null || reg.getGrupo() == null) continue;
            String chave = Objects.toString(reg.getIdLocal()) + "|" + reg.getOrigem() + "|" + reg.getGrupo();
            if (reg.getTransicao() == RegistroLogTecnico.Transicao.INICIO) {
                abertos.putIfAbsent(chave, reg);
            } else {
                RegistroLogTecnico ini = abertos.remove(chave);
                if (ini != null) r.add(new Intervalo(ini, reg.getDataHora(), reg.getDescricao()));
            }
        }
        for (RegistroLogTecnico ini : abertos.values()) r.add(new Intervalo(ini, null, null));
        r.sort(Comparator.comparing(Intervalo::getInicio));
        return r;
    }
}
