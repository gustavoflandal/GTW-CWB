package muralha.digital.ocr;

import muralha.digital.util.ConfigChaveValor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/** Parâmetros do score de OCR e da revisão automática, em muralha.config_chave_valor. */
public final class ParametrosOcr {

    private static final Logger logger = Logger.getLogger(ParametrosOcr.class);

    public static final String CHAVE_ORIGEM = "OCR_SCORE_ORIGEM";
    public static final String CHAVE_LIMIAR = "OCR_SCORE_LIMIAR";
    public static final String CHAVE_ESCALA = "OCR_SCORE_ESCALA";
    public static final String CHAVE_REVISAR_SEM_SCORE = "OCR_REVISAR_SEM_SCORE";
    public static final String CHAVE_REVISAR_SEM_LEITURA = "OCR_REVISAR_SEM_LEITURA";
    public static final String CHAVE_JANELA_HORAS = "OCR_JANELA_HORAS";
    public static final String CHAVE_LOTE = "OCR_LOTE";
    public static final String CHAVE_ESPERA_SCORE_MIN = "OCR_ESPERA_SCORE_MIN";

    public static final double LIMIAR_PADRAO = 85;

    private final OrigemScore origemPadrao;
    private final double limiar;
    private final double escala;
    private final boolean revisarSemScore;
    private final boolean revisarSemLeitura;
    private final int janelaHoras;
    private final int lote;
    private final int esperaScoreMin;

    public ParametrosOcr(OrigemScore origemPadrao, double limiar, double escala, boolean revisarSemScore,
            boolean revisarSemLeitura, int janelaHoras, int lote, int esperaScoreMin) {
        this.origemPadrao = origemPadrao == null ? OrigemScore.AUSENTE : origemPadrao;
        this.limiar = limiarValido(limiar) ? limiar : LIMIAR_PADRAO;
        this.escala = escala == 1 ? 1 : 100;
        this.revisarSemScore = revisarSemScore;
        this.revisarSemLeitura = revisarSemLeitura;
        this.janelaHoras = Math.max(1, Math.min(24 * 400, janelaHoras));
        this.lote = Math.max(1, Math.min(5000, lote));
        this.esperaScoreMin = Math.max(0, Math.min(24 * 60, esperaScoreMin));
    }

    public static boolean limiarValido(double v) { return v >= 0 && v <= 100; }

    public static ParametrosOcr carregar(Connection conn) throws SQLException {
        String texto = ConfigChaveValor.obter(conn, CHAVE_ORIGEM, OrigemScore.AUSENTE.formatar());
        OrigemScore origem;
        try {
            origem = OrigemScore.interpretar(texto);
        } catch (IllegalArgumentException e) {
            logger.warn("Valor inválido em " + CHAVE_ORIGEM + " (" + texto + "); usando AUSENTE.");
            origem = OrigemScore.AUSENTE;
        }
        return new ParametrosOcr(origem, decimal(ConfigChaveValor.obter(conn, CHAVE_LIMIAR, null), LIMIAR_PADRAO),
            decimal(ConfigChaveValor.obter(conn, CHAVE_ESCALA, null), 100),
            ConfigChaveValor.obterInt(conn, CHAVE_REVISAR_SEM_SCORE, 0) == 1,
            ConfigChaveValor.obterInt(conn, CHAVE_REVISAR_SEM_LEITURA, 0) == 1,
            ConfigChaveValor.obterInt(conn, CHAVE_JANELA_HORAS, 24),
            ConfigChaveValor.obterInt(conn, CHAVE_LOTE, 500),
            ConfigChaveValor.obterInt(conn, CHAVE_ESPERA_SCORE_MIN, 10));
    }

    static double decimal(String s, double padrao) {
        if (s == null) return padrao;
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    /** Origem para equipamentos sem configuração própria. */
    public OrigemScore getOrigemPadrao() { return origemPadrao; }

    /** Limiar global (0-100); cada equipamento pode ter o seu. */
    public double getLimiar() { return limiar; }

    /** 100 = scores em 0-100; 1 = scores em 0-1. */
    public double getEscala() { return escala; }

    public boolean isRevisarSemScore() { return revisarSemScore; }

    /** Desligado por padrão: no DEV, 23% das passagens não têm placa lida e inundariam a fila. */
    public boolean isRevisarSemLeitura() { return revisarSemLeitura; }

    public int getJanelaHoras() { return janelaHoras; }

    public int getLote() { return lote; }

    /** Minutos esperando o score chegar antes de avaliar sem ele. */
    public int getEsperaScoreMin() { return esperaScoreMin; }
}
