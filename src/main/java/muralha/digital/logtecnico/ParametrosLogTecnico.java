package muralha.digital.logtecnico;

import muralha.digital.util.ConfigChaveValor;

import java.sql.Connection;
import java.sql.SQLException;

/** Parâmetros do repositório de logs técnicos em muralha.config_chave_valor. */
public final class ParametrosLogTecnico {

    public static final String CHAVE_PERIODO_MAX_DIAS = "LOG_TECNICO_PERIODO_MAX_DIAS";
    public static final String CHAVE_PROCESSOS = "LOG_TECNICO_PROCESSOS";
    public static final String CHAVE_PROCESSO_ATRASO_MIN = "LOG_TECNICO_PROCESSO_ATRASO_MIN";
    public static final String CHAVE_EXPORTACAO_MAX = "LOG_TECNICO_EXPORTACAO_MAX";
    public static final String CHAVE_JANELA_CORRELACAO_MIN = "LOG_TECNICO_JANELA_CORRELACAO_MIN";

    /** O que mostrar de dbo.log_processos (uma linha por minuto da importação). */
    public enum ModoProcessos { NENHUM, NAO_FINALIZADOS, TODOS }

    private final int periodoMaxDias;
    private final ModoProcessos modoProcessos;
    private final int processoAtrasoMin;
    private final int exportacaoMax;
    private final int janelaCorrelacaoMin;

    public ParametrosLogTecnico(int periodoMaxDias, ModoProcessos modoProcessos, int processoAtrasoMin,
            int exportacaoMax, int janelaCorrelacaoMin) {
        this.periodoMaxDias = limitar(periodoMaxDias, 1, 366);
        this.modoProcessos = modoProcessos == null ? ModoProcessos.NAO_FINALIZADOS : modoProcessos;
        this.processoAtrasoMin = limitar(processoAtrasoMin, 1, 24 * 60);
        this.exportacaoMax = limitar(exportacaoMax, 100, 500_000);
        this.janelaCorrelacaoMin = limitar(janelaCorrelacaoMin, 1, 24 * 60);
    }

    /** Valores padrão (usados também nos testes). */
    public static ParametrosLogTecnico padrao() {
        return new ParametrosLogTecnico(31, ModoProcessos.NAO_FINALIZADOS, 30, 50_000, 10);
    }

    public static ParametrosLogTecnico carregar(Connection conn) throws SQLException {
        ParametrosLogTecnico p = padrao();
        ModoProcessos modo;
        try {
            modo = ModoProcessos.valueOf(ConfigChaveValor.obter(conn, CHAVE_PROCESSOS, p.modoProcessos.name()).toUpperCase());
        } catch (IllegalArgumentException e) {
            modo = p.modoProcessos;
        }
        return new ParametrosLogTecnico(
            ConfigChaveValor.obterInt(conn, CHAVE_PERIODO_MAX_DIAS, p.periodoMaxDias),
            modo,
            ConfigChaveValor.obterInt(conn, CHAVE_PROCESSO_ATRASO_MIN, p.processoAtrasoMin),
            ConfigChaveValor.obterInt(conn, CHAVE_EXPORTACAO_MAX, p.exportacaoMax),
            ConfigChaveValor.obterInt(conn, CHAVE_JANELA_CORRELACAO_MIN, p.janelaCorrelacaoMin));
    }

    private static int limitar(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    public int getPeriodoMaxDias() { return periodoMaxDias; }

    public ModoProcessos getModoProcessos() { return modoProcessos; }

    /** Minutos sem atualização para considerar um processo "não finalizado". */
    public int getProcessoAtrasoMin() { return processoAtrasoMin; }

    public int getExportacaoMax() { return exportacaoMax; }

    public int getJanelaCorrelacaoMin() { return janelaCorrelacaoMin; }
}
