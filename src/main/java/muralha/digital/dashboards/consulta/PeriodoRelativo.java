package muralha.digital.dashboards.consulta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Converte o dateRange do widget em um intervalo [inicio, fim).
 * Periodos "ultimos N" incluem o dia (ou mes) corrente.
 */
public final class PeriodoRelativo {

    public static final ZoneId FUSO = ZoneId.of("America/Bahia");

    public static final class Intervalo {
        public final LocalDateTime inicio;
        public final LocalDateTime fim;
        public final boolean fimInclusivo;

        Intervalo(LocalDateTime inicio, LocalDateTime fim, boolean fimInclusivo) {
            this.inicio = inicio;
            this.fim = fim;
            this.fimInclusivo = fimInclusivo;
        }
    }

    private final Clock relogio;

    public PeriodoRelativo() {
        this(Clock.system(FUSO));
    }

    public PeriodoRelativo(Clock relogio) {
        this.relogio = relogio;
    }

    public Intervalo resolver(JsonElement dateRange) {
        if (dateRange == null || dateRange.isJsonNull()) {
            return null;
        }
        if (dateRange.isJsonArray()) {
            return explicito(dateRange.getAsJsonArray());
        }
        if (dateRange.isJsonPrimitive() && dateRange.getAsJsonPrimitive().isString()) {
            return relativo(dateRange.getAsString().trim().toLowerCase(Locale.ROOT));
        }
        throw new ErroConsulta("Periodo invalido: " + dateRange);
    }

    private Intervalo relativo(String valor) {
        LocalDate hoje = LocalDate.now(relogio);
        LocalDate amanha = hoje.plusDays(1);
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        switch (valor) {
            case "today":          return dias(hoje, amanha);
            case "yesterday":      return dias(hoje.minusDays(1), hoje);
            case "last 7 days":    return dias(hoje.minusDays(6), amanha);
            case "last 30 days":   return dias(hoje.minusDays(29), amanha);
            case "this month":     return dias(inicioMes, amanha);
            case "last month":     return dias(inicioMes.minusMonths(1), inicioMes);
            case "last 3 months":  return dias(inicioMes.minusMonths(2), amanha);
            case "last 6 months":  return dias(inicioMes.minusMonths(5), amanha);
            case "last 12 months": return dias(inicioMes.minusMonths(11), amanha);
            case "this year":      return dias(hoje.withDayOfYear(1), amanha);
            case "last year":      return dias(hoje.withDayOfYear(1).minusYears(1), hoje.withDayOfYear(1));
            default:
                throw new ErroConsulta("Periodo nao suportado: " + valor);
        }
    }

    private Intervalo explicito(JsonArray arr) {
        if (arr.size() != 2 || !arr.get(0).isJsonPrimitive() || !arr.get(1).isJsonPrimitive()) {
            throw new ErroConsulta("Periodo explicito deve ter data inicial e final");
        }
        String ini = arr.get(0).getAsString().trim();
        String fim = arr.get(1).getAsString().trim();
        LocalDateTime inicio = parseDataHora(ini);
        LocalDateTime termino;
        boolean inclusivo;
        if (ehSoData(fim)) {
            termino = parseDataHora(fim).plusDays(1);
            inclusivo = false;
        } else {
            termino = parseDataHora(fim);
            inclusivo = true;
        }
        if (inicio.isAfter(termino)) {
            throw new ErroConsulta("Data inicial posterior a data final");
        }
        return new Intervalo(inicio, termino, inclusivo);
    }

    private static Intervalo dias(LocalDate inicio, LocalDate fimExclusivo) {
        return new Intervalo(inicio.atStartOfDay(), fimExclusivo.atStartOfDay(), false);
    }

    static boolean ehSoData(String valor) {
        return valor.length() == 10;
    }

    /** Aceita AAAA-MM-DD ou AAAA-MM-DDTHH:mm[:ss[.SSS]] (com 'T' ou espaco, 'Z' final ignorado). */
    public static LocalDateTime parseDataHora(String valor) {
        String v = valor.trim();
        if (v.endsWith("Z")) {
            v = v.substring(0, v.length() - 1);
        }
        try {
            if (ehSoData(v)) {
                return LocalDate.parse(v).atStartOfDay();
            }
            return LocalDateTime.parse(v.replace(' ', 'T'));
        } catch (DateTimeParseException e) {
            throw new ErroConsulta("Data invalida: " + valor);
        }
    }
}
