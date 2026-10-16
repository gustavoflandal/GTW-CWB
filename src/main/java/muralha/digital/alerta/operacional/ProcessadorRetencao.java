package muralha.digital.alerta.operacional;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * Processa a detecção de retenção/congestionamento para um lote de equipamentos.
 * Usa dados de veiculo_sumarizado para avaliar se a velocidade média está abaixo
 * do limiar com ocupação alta por N horas consecutivas.
 */
public class ProcessadorRetencao {

    private static final Logger logger = Logger.getLogger(ProcessadorRetencao.class);

    static final UUID ID_TIPO_CONGESTIONAMENTO =
        UUID.fromString("E2A31F01-0001-4A00-B001-AE7E4CA00001");

    private final RepositorioRetencao repo;

    public ProcessadorRetencao(RepositorioRetencao repo) {
        this.repo = repo;
    }

    public static class Resumo {
        private int verificados;
        private int alertasGerados;
        private int jaAlertados;
        private int falhas;

        public int getVerificados() { return verificados; }
        public int getAlertasGerados() { return alertasGerados; }
        public int getJaAlertados() { return jaAlertados; }
        public int getFalhas() { return falhas; }
    }

    public Resumo processar(ParametrosRetencao params) {
        Resumo resumo = new Resumo();
        List<Integer> equipamentos = repo.equipamentosComDados(params.getJanelaHoras(), params.getLote());

        for (int idLocal : equipamentos) {
            try {
                List<RegraRetencao.DadoHora> dados = repo.dadosPorEquipamento(idLocal, params.getJanelaHoras());
                RegraRetencao.Avaliacao avaliacao = RegraRetencao.avaliar(
                    dados, params.getOcupacaoMinima(), params.getVelocidadeMaxima(), params.getDuracaoHoras());

                resumo.verificados++;

                if (!avaliacao.isRetencao()) continue;

                if (repo.alertaRecente(idLocal, params.getEsperaReincidenciaMin())) {
                    resumo.jaAlertados++;
                    continue;
                }

                repo.gravarAlerta(idLocal, avaliacao, ID_TIPO_CONGESTIONAMENTO);
                resumo.alertasGerados++;
                logger.info("Alerta de retenção gerado: local=" + idLocal + " — " + avaliacao.getDetalhe());
            } catch (Exception e) {
                resumo.falhas++;
                logger.error("Falha ao avaliar retenção do local " + idLocal + ": " + e.getMessage(), e);
            }
        }

        return resumo;
    }
}
