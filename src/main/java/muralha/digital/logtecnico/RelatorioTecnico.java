package muralha.digital.logtecnico;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Relatório técnico de um equipamento no período (TR 5.4.7.5 d): contagem por categoria e
 * severidade, períodos por grupo (quantidade, duração somada dentro do período, em aberto),
 * parâmetros alterados e últimos erros/críticos.
 */
public final class RelatorioTecnico {

    public static final int MAX_OCORRENCIAS = 50;

    /** Resumo de um grupo de períodos (ex.: ENERGIA). */
    public static final class ResumoGrupo {
        public final String grupo;
        public int quantidade;
        public long duracaoMs;
        public int emAberto;

        ResumoGrupo(String grupo) { this.grupo = grupo; }
    }

    private final Map<CategoriaLog, Map<SeveridadeLog, Integer>> contagem = new EnumMap<>(CategoriaLog.class);
    private final Map<String, ResumoGrupo> grupos = new TreeMap<>();
    private final List<RegistroLogTecnico> parametros = new ArrayList<>();
    private final List<RegistroLogTecnico> ocorrencias = new ArrayList<>();
    private int total;

    private RelatorioTecnico() {}

    /**
     * @param registros  registros do equipamento no período, em qualquer ordem
     * @param inicio     início do período (a duração é recortada a ele)
     * @param fim        fim do período (períodos em aberto contam até aqui)
     */
    public static RelatorioTecnico montar(List<RegistroLogTecnico> registros, Timestamp inicio, Timestamp fim) {
        RelatorioTecnico r = new RelatorioTecnico();
        List<RegistroLogTecnico> desc = new ArrayList<>(registros);
        ServicoLogTecnico.ordenar(desc, false);
        for (RegistroLogTecnico reg : desc) {
            r.total++;
            r.contagem.computeIfAbsent(reg.getCategoria(), k -> new EnumMap<>(SeveridadeLog.class))
                .merge(reg.getSeveridade(), 1, Integer::sum);
            if (reg.getCategoria() == CategoriaLog.PARAMETRO) r.parametros.add(reg);
            if ((reg.getSeveridade() == SeveridadeLog.ERRO || reg.getSeveridade() == SeveridadeLog.CRITICO)
                    && r.ocorrencias.size() < MAX_OCORRENCIAS) {
                r.ocorrencias.add(reg);
            }
        }
        for (CorrelacionadorIntervalos.Intervalo i : CorrelacionadorIntervalos.montar(registros)) {
            ResumoGrupo g = r.grupos.computeIfAbsent(i.getGrupo(), ResumoGrupo::new);
            g.quantidade++;
            if (i.getFim() == null) g.emAberto++;
            long ini = Math.max(i.getInicio().getTime(), inicio.getTime());
            long f = Math.min(i.getFim() != null ? i.getFim().getTime() : fim.getTime(), fim.getTime());
            g.duracaoMs += Math.max(0, f - ini);
        }
        return r;
    }

    public int getTotal() { return total; }

    public Map<CategoriaLog, Map<SeveridadeLog, Integer>> getContagem() { return contagem; }

    public Map<String, ResumoGrupo> getGrupos() { return grupos; }

    /** Alterações de parâmetros, da mais recente para a mais antiga. */
    public List<RegistroLogTecnico> getParametros() { return parametros; }

    /** Até {@link #MAX_OCORRENCIAS} erros e críticos, do mais recente para o mais antigo. */
    public List<RegistroLogTecnico> getOcorrencias() { return ocorrencias; }
}
