package muralha.digital.alerta.operacional;

import muralha.digital.util.ConfigChaveValor;

import java.sql.Connection;
import java.sql.SQLException;

/** Parâmetros da regra de retenção/congestionamento em config_chave_valor. */
public final class ParametrosRetencao {

    public static final String CHAVE_OCUPACAO_MINIMA = "RETENCAO_OCUPACAO_MINIMA";
    public static final String CHAVE_VELOCIDADE_MAXIMA = "RETENCAO_VELOCIDADE_MAXIMA";
    public static final String CHAVE_DURACAO_HORAS = "RETENCAO_DURACAO_HORAS";
    public static final String CHAVE_JANELA_HORAS = "RETENCAO_JANELA_HORAS";
    public static final String CHAVE_LOTE = "RETENCAO_LOTE";
    public static final String CHAVE_ESPERA_MIN = "RETENCAO_ESPERA_REINCIDENCIA_MIN";

    public static final int OCUPACAO_PADRAO = 500;
    public static final double VELOCIDADE_PADRAO = 15.0;
    public static final int DURACAO_PADRAO = 1;
    public static final int JANELA_PADRAO = 3;
    public static final int LOTE_PADRAO = 100;
    public static final int ESPERA_PADRAO = 60;

    private final int ocupacaoMinima;
    private final double velocidadeMaxima;
    private final int duracaoHoras;
    private final int janelaHoras;
    private final int lote;
    private final int esperaReincidenciaMin;

    public ParametrosRetencao(int ocupacaoMinima, double velocidadeMaxima, int duracaoHoras,
                              int janelaHoras, int lote, int esperaReincidenciaMin) {
        this.ocupacaoMinima = ocupacaoMinima > 0 ? ocupacaoMinima : OCUPACAO_PADRAO;
        this.velocidadeMaxima = velocidadeMaxima > 0 ? velocidadeMaxima : VELOCIDADE_PADRAO;
        this.duracaoHoras = duracaoHoras > 0 ? duracaoHoras : DURACAO_PADRAO;
        this.janelaHoras = janelaHoras > 0 ? janelaHoras : JANELA_PADRAO;
        this.lote = lote > 0 ? lote : LOTE_PADRAO;
        this.esperaReincidenciaMin = esperaReincidenciaMin > 0 ? esperaReincidenciaMin : ESPERA_PADRAO;
    }

    public int getOcupacaoMinima() { return ocupacaoMinima; }
    public double getVelocidadeMaxima() { return velocidadeMaxima; }
    public int getDuracaoHoras() { return duracaoHoras; }
    public int getJanelaHoras() { return janelaHoras; }
    public int getLote() { return lote; }
    public int getEsperaReincidenciaMin() { return esperaReincidenciaMin; }

    public static ParametrosRetencao carregar(Connection conn) throws SQLException {
        return new ParametrosRetencao(
            ConfigChaveValor.obterInt(conn, CHAVE_OCUPACAO_MINIMA, OCUPACAO_PADRAO),
            Double.parseDouble(ConfigChaveValor.obter(conn, CHAVE_VELOCIDADE_MAXIMA, String.valueOf(VELOCIDADE_PADRAO))),
            ConfigChaveValor.obterInt(conn, CHAVE_DURACAO_HORAS, DURACAO_PADRAO),
            ConfigChaveValor.obterInt(conn, CHAVE_JANELA_HORAS, JANELA_PADRAO),
            ConfigChaveValor.obterInt(conn, CHAVE_LOTE, LOTE_PADRAO),
            ConfigChaveValor.obterInt(conn, CHAVE_ESPERA_MIN, ESPERA_PADRAO)
        );
    }
}
