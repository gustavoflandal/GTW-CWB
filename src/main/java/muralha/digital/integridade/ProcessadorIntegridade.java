package muralha.digital.integridade;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Confere um lote de imagens: obtém o hash de origem conforme a configuração do equipamento,
 * compara com o SHA-256 calculado, grava o resultado e, se divergente, gera alerta operacional
 * (um por passagem) e registro de auditoria.
 */
public class ProcessadorIntegridade {

    private static final Logger logger = Logger.getLogger(ProcessadorIntegridade.class);

    private final RepositorioIntegridade repositorio;

    public ProcessadorIntegridade(RepositorioIntegridade repositorio) {
        this.repositorio = repositorio;
    }

    /** Contagem do que aconteceu num lote. */
    public static final class Resumo {
        private final Map<StatusIntegridade, Integer> porStatus = new EnumMap<>(StatusIntegridade.class);
        private int aguardandoHash;
        private int alertas;
        private int falhas;

        void contar(StatusIntegridade s) { porStatus.merge(s, 1, Integer::sum); }

        public int getQuantidade(StatusIntegridade s) { return porStatus.getOrDefault(s, 0); }

        public int getConferidas() {
            int total = 0;
            for (int n : porStatus.values()) total += n;
            return total;
        }

        /** Imagens deixadas para a próxima execução porque o hash de origem ainda pode chegar. */
        public int getAguardandoHash() { return aguardandoHash; }

        public int getAlertas() { return alertas; }

        /** Imagens não gravadas por erro inesperado (ficam pendentes). */
        public int getFalhas() { return falhas; }

        @Override
        public String toString() {
            return "conferidas=" + getConferidas() + " " + porStatus + " aguardandoHash=" + aguardandoHash
                + " alertas=" + alertas + " falhas=" + falhas;
        }
    }

    /**
     * @param imagens         imagens pendentes
     * @param parametros      parâmetros globais
     * @param porEquipamento  origem configurada por id_local (equipamentos ausentes usam a origem padrão)
     * @param agoraMs         instante atual (para a espera do hash de origem)
     */
    public Resumo processar(List<ImagemPendente> imagens, ParametrosIntegridade parametros,
            Map<Integer, OrigemHash> porEquipamento, long agoraMs) {
        Resumo resumo = new Resumo();
        Map<String, String> dadoBrutoPorCaptura = new HashMap<>();
        Set<String> passagensAlertadas = new HashSet<>();

        for (ImagemPendente img : imagens) {
            try {
                OrigemHash origem = porEquipamento.getOrDefault(img.getIdLocal(), parametros.getOrigemPadrao());
                String hashOrigem = obterHashOrigem(img, origem, dadoBrutoPorCaptura);

                if (origem.getTipo() != OrigemHash.Tipo.AUSENTE && hashOrigem == null
                        && aindaNoPrazo(img, parametros.getEsperaHashMin(), agoraMs)) {
                    resumo.aguardandoHash++;
                    continue;
                }

                ResultadoConferencia res = ConferenciaIntegridade.conferir(img.getImagem(), origem, hashOrigem);
                repositorio.gravar(img, origem, res);
                resumo.contar(res.getStatus());

                if (res.getStatus() == StatusIntegridade.DIVERGENTE) {
                    repositorio.auditar(img, res);
                    if (parametros.isAlertaHabilitado()
                            && passagensAlertadas.add(img.getIdVeiculoTempoReal())
                            && !repositorio.existeAlerta(img.getIdVeiculoTempoReal())
                            && repositorio.gerarAlerta(img, res)) {
                        resumo.alertas++;
                    }
                }
            } catch (Exception e) {
                resumo.falhas++;
                logger.error("Falha ao conferir a integridade da imagem " + img.getIdImagem(), e);
            }
        }
        return resumo;
    }

    private String obterHashOrigem(ImagemPendente img, OrigemHash origem,
            Map<String, String> dadoBrutoPorCaptura) throws Exception {
        switch (origem.getTipo()) {
            case PAYLOAD_SOAP:
                return repositorio.hashPayload(img.getIdImagem());
            case DADO_BRUTO:
                if (img.getIdCaptura() == null) return null;
                String json;
                if (dadoBrutoPorCaptura.containsKey(img.getIdCaptura())) {
                    json = dadoBrutoPorCaptura.get(img.getIdCaptura());
                } else {
                    json = repositorio.dadoBruto(img.getIdCaptura());
                    dadoBrutoPorCaptura.put(img.getIdCaptura(), json);
                }
                return ConferenciaIntegridade.extrairDoDadoBruto(json, origem.getCampo(), img.getIndiceImagem());
            default:
                return null;
        }
    }

    /** Se a passagem é recente o bastante para o hash de origem ainda chegar. */
    static boolean aindaNoPrazo(ImagemPendente img, int esperaMin, long agoraMs) {
        Timestamp ref = img.getDtRecepcao() != null ? img.getDtRecepcao() : img.getDtCaptura();
        if (ref == null || esperaMin <= 0) return false;
        return agoraMs - ref.getTime() < esperaMin * 60_000L;
    }
}
