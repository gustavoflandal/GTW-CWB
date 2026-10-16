package muralha.digital.integridade;

import muralha.digital.util.ConfigChaveValor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/** Parâmetros da conferência de integridade, lidos de muralha.config_chave_valor. */
public final class ParametrosIntegridade {

    private static final Logger logger = Logger.getLogger(ParametrosIntegridade.class);

    public static final String CHAVE_ORIGEM = "INTEGRIDADE_ORIGEM_HASH";
    public static final String CHAVE_LOTE = "INTEGRIDADE_LOTE";
    public static final String CHAVE_JANELA_HORAS = "INTEGRIDADE_JANELA_HORAS";
    public static final String CHAVE_ESPERA_HASH_MIN = "INTEGRIDADE_ESPERA_HASH_MIN";
    public static final String CHAVE_ALERTA = "INTEGRIDADE_ALERTA_HABILITADO";

    public static final int LOTE_PADRAO = 200;
    public static final int JANELA_HORAS_PADRAO = 24;
    public static final int ESPERA_HASH_MIN_PADRAO = 10;

    private final OrigemHash origemPadrao;
    private final int lote;
    private final int janelaHoras;
    private final int esperaHashMin;
    private final boolean alertaHabilitado;

    public ParametrosIntegridade(OrigemHash origemPadrao, int lote, int janelaHoras,
            int esperaHashMin, boolean alertaHabilitado) {
        this.origemPadrao = origemPadrao == null ? OrigemHash.AUSENTE : origemPadrao;
        this.lote = limitar(lote, 1, 2000);
        this.janelaHoras = limitar(janelaHoras, 1, 24 * 400);
        this.esperaHashMin = limitar(esperaHashMin, 0, 24 * 60);
        this.alertaHabilitado = alertaHabilitado;
    }

    public static ParametrosIntegridade carregar(Connection conn) throws SQLException {
        OrigemHash origem;
        String texto = ConfigChaveValor.obter(conn, CHAVE_ORIGEM, OrigemHash.AUSENTE.formatar());
        try {
            origem = OrigemHash.interpretar(texto);
        } catch (IllegalArgumentException e) {
            logger.warn("Valor inválido em " + CHAVE_ORIGEM + " (" + texto + "); usando AUSENTE.");
            origem = OrigemHash.AUSENTE;
        }
        return new ParametrosIntegridade(
            origem,
            ConfigChaveValor.obterInt(conn, CHAVE_LOTE, LOTE_PADRAO),
            ConfigChaveValor.obterInt(conn, CHAVE_JANELA_HORAS, JANELA_HORAS_PADRAO),
            ConfigChaveValor.obterInt(conn, CHAVE_ESPERA_HASH_MIN, ESPERA_HASH_MIN_PADRAO),
            ConfigChaveValor.obterInt(conn, CHAVE_ALERTA, 1) == 1);
    }

    private static int limitar(int valor, int min, int max) {
        return Math.max(min, Math.min(max, valor));
    }

    /** Origem usada pelos equipamentos sem configuração própria. */
    public OrigemHash getOrigemPadrao() { return origemPadrao; }

    /** Imagens por execução do job. */
    public int getLote() { return lote; }

    /** Só confere imagens de passagens capturadas nas últimas N horas. */
    public int getJanelaHoras() { return janelaHoras; }

    /** Minutos que se espera o hash de origem chegar antes de concluir "não recebido". */
    public int getEsperaHashMin() { return esperaHashMin; }

    public boolean isAlertaHabilitado() { return alertaHabilitado; }
}
