package muralha.digital.chamado;

import java.time.Duration;
import java.time.LocalDateTime;

/** Prazos e marcas de SLA em horas corridas 24x7 (spec §1.3). Pura. */
public final class CalculadoraSla {

    public enum Marca { NO_PRAZO, EM_RISCO, VENCIDO, CUMPRIDO, VIOLADO }

    private CalculadoraSla() {
    }

    public static LocalDateTime prazoAtendimento(LocalDateTime abertura, int minutosAtendimento) {
        return abertura.plusMinutes(minutosAtendimento);
    }

    public static LocalDateTime prazoResolucao(LocalDateTime abertura, int minutosResolucao, int minutosPausados) {
        return abertura.plusMinutes((long) minutosResolucao + minutosPausados);
    }

    public static int minutosEntre(LocalDateTime inicio, LocalDateTime fim) {
        long minutos = Duration.between(inicio, fim).toMinutes();
        return minutos < 0 ? 0 : (int) minutos;
    }

    /**
     * @param cumpridoEm momento em que o prazo foi cumprido (primeiro atendimento ou resolucao); null se ainda nao
     * @param pausado    relogio parado (aguardando solicitante): a marca nao evolui
     * @param atual      marca vigente, preservada enquanto pausado
     */
    public static Marca marca(LocalDateTime abertura, LocalDateTime prazo, LocalDateTime cumpridoEm,
                              LocalDateTime agora, int percentualRisco, boolean pausado, Marca atual) {
        if (cumpridoEm != null) {
            return cumpridoEm.isAfter(prazo) ? Marca.VIOLADO : Marca.CUMPRIDO;
        }
        if (pausado) {
            return atual == null ? Marca.NO_PRAZO : atual;
        }
        if (agora.isAfter(prazo)) {
            return Marca.VENCIDO;
        }
        long total = Duration.between(abertura, prazo).toSeconds();
        long consumido = Duration.between(abertura, agora).toSeconds();
        if (total > 0 && consumido * 100 >= total * percentualRisco) {
            return Marca.EM_RISCO;
        }
        return Marca.NO_PRAZO;
    }
}
