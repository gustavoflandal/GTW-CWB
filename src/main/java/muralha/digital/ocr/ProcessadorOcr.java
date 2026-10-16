package muralha.digital.ocr;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Avalia passagens novas: monta as leituras (colunas da passagem + score conforme a origem do
 * equipamento + leituras já gravadas), aplica a {@link RegraRevisao} com o limiar do equipamento
 * e encaminha para a fila de revisão manual quando necessário (TR 5.7.4.2-4, 5.2.2.5).
 */
public class ProcessadorOcr {

    private static final Logger logger = Logger.getLogger(ProcessadorOcr.class);

    /** Configuração de um equipamento (origem do score e limiar próprio, ambos opcionais). */
    public static final class ConfigEquipamento {
        final OrigemScore origem;
        final Double limiar;

        public ConfigEquipamento(OrigemScore origem, Double limiar) {
            this.origem = origem;
            this.limiar = limiar;
        }
    }

    /** Contagem de um lote. */
    public static final class Resumo {
        private final Map<RegraRevisao.Motivo, Integer> encaminhadas = new EnumMap<>(RegraRevisao.Motivo.class);
        private int avaliadas;
        private int aguardandoScore;
        private int falhas;

        public int getAvaliadas() { return avaliadas; }
        public int getAguardandoScore() { return aguardandoScore; }
        public int getFalhas() { return falhas; }
        public int getEncaminhadas(RegraRevisao.Motivo m) { return encaminhadas.getOrDefault(m, 0); }

        @Override
        public String toString() {
            return "avaliadas=" + avaliadas + " encaminhadas=" + encaminhadas + " aguardandoScore=" + aguardandoScore + " falhas=" + falhas;
        }
    }

    private final RepositorioOcr repositorio;

    public ProcessadorOcr(RepositorioOcr repositorio) {
        this.repositorio = repositorio;
    }

    public Resumo processar(List<PassagemPendente> passagens, ParametrosOcr p,
            Map<Integer, ConfigEquipamento> porEquipamento, long agoraMs) {
        Resumo r = new Resumo();
        for (PassagemPendente pp : passagens) {
            try {
                ConfigEquipamento cfg = porEquipamento.get(pp.getIdLocal());
                OrigemScore origem = cfg != null && cfg.origem != null ? cfg.origem : p.getOrigemPadrao();
                double limiar = cfg != null && cfg.limiar != null ? cfg.limiar : p.getLimiar();

                List<LeituraPlaca> gravadas = repositorio.leiturasGravadas(pp.getIdVeiculoTempoReal());
                List<LeituraPlaca> embarcadasGravadas = new ArrayList<>();
                for (LeituraPlaca l : gravadas) if (l.getOrigem() == LeituraPlaca.Origem.EMBARCADA) embarcadasGravadas.add(l);

                List<LeituraPlaca> novas = new ArrayList<>();
                List<LeituraPlaca> avaliar = new ArrayList<>(gravadas);
                if (embarcadasGravadas.isEmpty()) {
                    Double[] scores = null;
                    if (origem.getTipo() == OrigemScore.Tipo.DADO_BRUTO && pp.getIdCaptura() != null) {
                        scores = RegraRevisao.scoresDoDadoBruto(repositorio.dadoBruto(pp.getIdCaptura()), origem.getCampo(), p.getEscala());
                    }
                    novas = RegraRevisao.leiturasDaPassagem(pp.getPlaca(), pp.getPlacaFrontal(), scores);
                    boolean temScore = scores != null && scores.length > 0 && scores[0] != null;
                    boolean esperaScore = origem.getTipo() != OrigemScore.Tipo.AUSENTE && !temScore;
                    if (esperaScore && aindaNoPrazo(pp, p.getEsperaScoreMin(), agoraMs)) {
                        r.aguardandoScore++;
                        continue;
                    }
                    avaliar.addAll(novas);
                }

                RegraRevisao.Avaliacao a = RegraRevisao.avaliar(avaliar, limiar, p.isRevisarSemScore(), p.isRevisarSemLeitura());
                if (!novas.isEmpty()) repositorio.gravarLeituras(pp, novas);
                repositorio.registrarAvaliacao(pp, a, limiar);
                r.avaliadas++;
                if (a.encaminhar()) r.encaminhadas.merge(a.getMotivo(), 1, Integer::sum);
            } catch (Exception e) {
                r.falhas++;
                logger.error("Falha ao avaliar a passagem " + pp.getIdVeiculoTempoReal(), e);
            }
        }
        return r;
    }

    static boolean aindaNoPrazo(PassagemPendente pp, int esperaMin, long agoraMs) {
        Timestamp ref = pp.getDataImportado() != null ? pp.getDataImportado() : pp.getData();
        if (ref == null || esperaMin <= 0) return false;
        return agoraMs - ref.getTime() < esperaMin * 60_000L;
    }
}
