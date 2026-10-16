package muralha.digital.alerta.operacional;

import java.util.List;

/**
 * Avalia se um equipamento está em retenção/congestionamento com base nos dados
 * sumarizados de tráfego. Uma retenção é detectada quando a velocidade média
 * fica abaixo do limiar E a ocupação fica acima do mínimo por N horas consecutivas.
 */
public final class RegraRetencao {

    private RegraRetencao() {}

    public static class DadoHora {
        private final int idLocal;
        private final int hora;
        private final double velocidadeMedia;
        private final int ocupacao;
        private final int trafego;

        public DadoHora(int idLocal, int hora, double velocidadeMedia, int ocupacao, int trafego) {
            this.idLocal = idLocal;
            this.hora = hora;
            this.velocidadeMedia = velocidadeMedia;
            this.ocupacao = ocupacao;
            this.trafego = trafego;
        }

        public int getIdLocal() { return idLocal; }
        public int getHora() { return hora; }
        public double getVelocidadeMedia() { return velocidadeMedia; }
        public int getOcupacao() { return ocupacao; }
        public int getTrafego() { return trafego; }
    }

    public enum Severidade { ALTA, MEDIA, BAIXA }

    public static class Avaliacao {
        private final boolean retencao;
        private final Severidade severidade;
        private final int horasConsecutivas;
        private final double velocidadeMedia;
        private final int ocupacaoMedia;
        private final String detalhe;

        private Avaliacao(boolean retencao, Severidade severidade, int horasConsecutivas,
                         double velocidadeMedia, int ocupacaoMedia, String detalhe) {
            this.retencao = retencao;
            this.severidade = severidade;
            this.horasConsecutivas = horasConsecutivas;
            this.velocidadeMedia = velocidadeMedia;
            this.ocupacaoMedia = ocupacaoMedia;
            this.detalhe = detalhe;
        }

        public boolean isRetencao() { return retencao; }
        public Severidade getSeveridade() { return severidade; }
        public int getHorasConsecutivas() { return horasConsecutivas; }
        public double getVelocidadeMedia() { return velocidadeMedia; }
        public int getOcupacaoMedia() { return ocupacaoMedia; }
        public String getDetalhe() { return detalhe; }
    }

    /**
     * Avalia os dados horários de um equipamento.
     * @param dados horas recentes, ordenadas cronologicamente (mais antiga primeiro)
     * @param ocupacaoMinima limiar de ocupação acima do qual há tráfego significativo
     * @param velocidadeMaxima limiar de velocidade abaixo do qual há retenção
     * @param duracaoMinima mínimo de horas consecutivas em retenção para gerar alerta
     */
    public static Avaliacao avaliar(List<DadoHora> dados, int ocupacaoMinima,
                                    double velocidadeMaxima, int duracaoMinima) {
        if (dados == null || dados.isEmpty()) {
            return semRetencao("Sem dados sumarizados");
        }

        int consecutivas = 0;
        double somaVel = 0;
        long somaOcup = 0;
        int maxConsecutivas = 0;
        double velAcumulada = 0;
        long ocupAcumulada = 0;

        for (DadoHora d : dados) {
            boolean velBaixa = d.velocidadeMedia < velocidadeMaxima && d.trafego > 0;
            boolean ocupAlta = d.ocupacao >= ocupacaoMinima;

            if (velBaixa && ocupAlta) {
                consecutivas++;
                somaVel += d.velocidadeMedia;
                somaOcup += d.ocupacao;
                if (consecutivas > maxConsecutivas) {
                    maxConsecutivas = consecutivas;
                    velAcumulada = somaVel;
                    ocupAcumulada = somaOcup;
                }
            } else {
                consecutivas = 0;
                somaVel = 0;
                somaOcup = 0;
            }
        }

        if (maxConsecutivas >= duracaoMinima) {
            double velMedia = velAcumulada / maxConsecutivas;
            int ocupMedia = (int) (ocupAcumulada / maxConsecutivas);
            Severidade sev = maxConsecutivas >= duracaoMinima * 3 ? Severidade.ALTA
                           : maxConsecutivas >= duracaoMinima * 2 ? Severidade.MEDIA
                           : Severidade.BAIXA;
            String detalhe = String.format(
                "Retenção detectada: %d h consecutivas, vel. média %.1f km/h (limiar %.1f), ocupação média %d (limiar %d)",
                maxConsecutivas, velMedia, velocidadeMaxima, ocupMedia, ocupacaoMinima);
            return new Avaliacao(true, sev, maxConsecutivas, velMedia, ocupMedia, detalhe);
        }

        return semRetencao(String.format("Máximo %d h consecutivas (mínimo %d)", maxConsecutivas, duracaoMinima));
    }

    private static Avaliacao semRetencao(String detalhe) {
        return new Avaliacao(false, null, 0, 0, 0, detalhe);
    }
}
