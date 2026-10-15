package muralha.digital.dashboards.consulta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class MontadorSqlTest {

    private static final int PASSAGENS = 1;
    private static final int EQUIPAMENTOS = 2;

    private final MontadorSql montador = new MontadorSql(new PeriodoRelativo(Clock.fixed(
            ZonedDateTime.of(2026, 10, 14, 10, 0, 0, 0, PeriodoRelativo.FUSO).toInstant(), PeriodoRelativo.FUSO)));

    /** Dataset com base em passagens (N) e join para equipamentos (1). */
    private static DatasetResolvido datasetPassagens() {
        DatasetResolvido ds = new DatasetResolvido(1, PASSAGENS);
        adicionarTabelas(ds);
        ds.adicionarCampo("data", "Data", PASSAGENS, "data", "data_hora", true, "NONE", "tempo", null);
        ds.adicionarCampo("equipamento", "Equipamento", EQUIPAMENTOS, "nome", "texto", true, "NONE", "dimensao", null);
        ds.adicionarCampo("placa", "Placa", PASSAGENS, "placa", "texto", true, "NONE", "dimensao", null);
        ds.adicionarCampo("id_local", "ID Local", PASSAGENS, "id_local", "inteiro", true, "NONE", "dimensao", null);
        ds.adicionarCampo("total_passagens", "Total", PASSAGENS, "id", "inteiro", true, "COUNT", "medida", null);
        ds.adicionarCampo("velocidade_media", "Vel. media", PASSAGENS, "velocidade", "numerico", true, "AVG", "medida", null);
        ds.adicionarCampo("velocidade_kmh", "Vel. km/h", null, null, null, true, "SUM", "medida", "velocidade * 3.6");
        return ds;
    }

    /** Dataset com base em equipamentos (1) e join para passagens (N). */
    private static DatasetResolvido datasetEquipamentos() {
        DatasetResolvido ds = new DatasetResolvido(2, EQUIPAMENTOS);
        adicionarTabelas(ds);
        ds.adicionarCampo("nome", "Nome", EQUIPAMENTOS, "nome", "texto", true, "NONE", "dimensao", null);
        ds.adicionarCampo("qtd_equipamentos", "Qtd", EQUIPAMENTOS, "id_local", "inteiro", true, "COUNT", "medida", null);
        ds.adicionarCampo("equip_distintos", "Distintos", EQUIPAMENTOS, "id_local", "inteiro", true, "COUNT_DISTINCT", "medida", null);
        ds.adicionarCampo("velocidade_media", "Vel. media", PASSAGENS, "velocidade", "numerico", true, "AVG", "medida", null);
        return ds;
    }

    private static void adicionarTabelas(DatasetResolvido ds) {
        if (ds.idTabelaBase == PASSAGENS) {
            ds.adicionarTabela(PASSAGENS, "muralha", "veiculo_tempo_real", "veiculo_tempo_real", "id");
            ds.adicionarTabela(EQUIPAMENTOS, "dbo", "local_vigente", "local_vigente", "id_local");
        } else {
            ds.adicionarTabela(EQUIPAMENTOS, "dbo", "local_vigente", "local_vigente", "id_local");
            ds.adicionarTabela(PASSAGENS, "muralha", "veiculo_tempo_real", "veiculo_tempo_real", "id");
        }
        ds.adicionarColuna(PASSAGENS, "id", "inteiro");
        ds.adicionarColuna(PASSAGENS, "data", "data_hora");
        ds.adicionarColuna(PASSAGENS, "id_local", "inteiro");
        ds.adicionarColuna(PASSAGENS, "placa", "texto");
        ds.adicionarColuna(PASSAGENS, "velocidade", "numerico");
        ds.adicionarColuna(EQUIPAMENTOS, "id_local", "inteiro");
        ds.adicionarColuna(EQUIPAMENTOS, "nome", "texto");
        ds.adicionarRelacao(PASSAGENS, "id_local", EQUIPAMENTOS, "id_local", "many_to_one");
    }

    private static ConsultaDashboard consulta(String json, int idDataset) {
        return ConsultaDashboard.deJson(JsonParser.parseString(json).getAsJsonObject(), idDataset);
    }

    private SqlMontado montar(String json) {
        return montador.montar(datasetPassagens(), consulta(json, 1));
    }

    private static void assertErro(Runnable r, String trecho) {
        try {
            r.run();
            fail("Esperava ErroConsulta contendo: " + trecho);
        } catch (ErroConsulta e) {
            assertTrue(e.getMessage().contains(trecho), "Mensagem: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------

    @Test
    public void medidaDimensaoETempoGeramGroupByCorreto() {
        SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"dimensions\":[\"placa\"],"
                + "\"timeDimensions\":[{\"dimension\":\"data\",\"granularity\":\"day\",\"dateRange\":\"today\"}]}");

        assertEquals(s.sql, "SELECT TOP (?) CAST(t0.[data] AS DATE) AS [c0], t0.[placa] AS [c1], "
                + "COUNT(t0.[id]) AS [c2] FROM [muralha].[veiculo_tempo_real] AS t0 "
                + "WHERE (t0.[data] >= ?) AND (t0.[data] < ?) "
                + "GROUP BY CAST(t0.[data] AS DATE), t0.[placa] ORDER BY [c0] ASC");
        assertEquals(s.parametros.get(0), 10000);
        assertEquals(s.parametros.get(1), Timestamp.valueOf("2026-10-14 00:00:00"));
        assertEquals(s.parametros.get(2), Timestamp.valueOf("2026-10-15 00:00:00"));
        assertEquals(s.colunas.get(0).chaves, List.of("data.day", "data"));
    }

    @Test
    public void joinSoApareceQuandoCampoDaTabelaRelacionadaEUsado() {
        SqlMontado sem = montar("{\"measures\":[\"total_passagens\"]}");
        assertFalse(sem.sql.contains("JOIN"));

        SqlMontado com = montar("{\"measures\":[\"total_passagens\"],\"dimensions\":[\"equipamento\"]}");
        assertTrue(com.sql.contains("LEFT JOIN [dbo].[local_vigente] AS t1 ON t0.[k1] = t1.[id_local]"), com.sql);
        assertTrue(com.sql.contains("t1.[nome] AS [c0]"));
    }

    @Test
    public void medidasDaBaseComJoinNParaUmSaoPreAgregadas() {
        SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"dimensions\":[\"equipamento\"]}");
        assertEquals(s.sql, "SELECT TOP (?) t1.[nome] AS [c0], ISNULL(SUM(t0.[k0]), 0) AS [c1] "
                + "FROM (SELECT COUNT(t0.[id]) AS [k0], t0.[id_local] AS [k1] "
                + "FROM [muralha].[veiculo_tempo_real] AS t0 GROUP BY t0.[id_local]) AS t0 "
                + "LEFT JOIN [dbo].[local_vigente] AS t1 ON t0.[k1] = t1.[id_local] "
                + "GROUP BY t1.[nome] ORDER BY [c1] DESC");
    }

    @Test
    public void preAgregacaoLevaTempoEFiltrosDaBaseParaSubconsulta() {
        SqlMontado s = montar("{\"measures\":[\"velocidade_media\"],\"dimensions\":[\"equipamento\"],"
                + "\"timeDimensions\":[{\"dimension\":\"data\",\"granularity\":\"month\",\"dateRange\":\"this month\"}],"
                + "\"filters\":[{\"member\":\"equipamento\",\"operator\":\"contains\",\"values\":[\"Av\"]},"
                + "{\"member\":\"placa\",\"operator\":\"=\",\"values\":[\"ABC1234\"]},"
                + "{\"member\":\"velocidade_media\",\"operator\":\">\",\"values\":[\"40\"]}]}");
        assertEquals(s.sql, "SELECT TOP (?) t0.[k0] AS [c0], t1.[nome] AS [c1], "
                + "SUM(t0.[k1]) / NULLIF(SUM(t0.[k2]), 0) AS [c2] "
                + "FROM (SELECT DATEFROMPARTS(YEAR(t0.[data]), MONTH(t0.[data]), 1) AS [k0], "
                + "SUM(CAST(t0.[velocidade] AS FLOAT)) AS [k1], COUNT(t0.[velocidade]) AS [k2], t0.[id_local] AS [k3] "
                + "FROM [muralha].[veiculo_tempo_real] AS t0 "
                + "WHERE (t0.[data] >= ?) AND (t0.[data] < ?) AND (t0.[placa] = ?) "
                + "GROUP BY DATEFROMPARTS(YEAR(t0.[data]), MONTH(t0.[data]), 1), t0.[id_local]) AS t0 "
                + "LEFT JOIN [dbo].[local_vigente] AS t1 ON t0.[k3] = t1.[id_local] "
                + "WHERE (t1.[nome] LIKE ? ESCAPE '\\') "
                + "GROUP BY t0.[k0], t1.[nome] "
                + "HAVING (SUM(t0.[k1]) / NULLIF(SUM(t0.[k2]), 0) > ?) ORDER BY [c0] ASC");
        assertEquals(s.parametros, List.of(10000,
                Timestamp.valueOf("2026-10-01 00:00:00"), Timestamp.valueOf("2026-10-15 00:00:00"),
                "ABC1234", "%Av%", new BigDecimal("40")));
    }

    @Test
    public void semPreAgregacaoParaCountDistinctOuMedidaCalculadaForaDaBase() {
        DatasetResolvido ds = datasetPassagens();
        ds.adicionarCampo("placas_distintas", "Placas", PASSAGENS, "placa", "texto", true, "COUNT_DISTINCT",
                "medida", null);
        SqlMontado s = montador.montar(ds, consulta(
                "{\"measures\":[\"placas_distintas\"],\"dimensions\":[\"equipamento\"]}", 1));
        assertTrue(s.sql.startsWith("SELECT TOP (?) t1.[nome] AS [c0], COUNT(DISTINCT t0.[placa]) AS [c1] "
                + "FROM [muralha].[veiculo_tempo_real] AS t0 LEFT JOIN"), s.sql);
    }

    @Test
    public void prefixoDoProprioDatasetEAceitoERepetidoNaResposta() {
        SqlMontado s = montar("{\"measures\":[\"dataset_1.total_passagens\"],"
                + "\"order\":{\"dataset_1.total_passagens\":\"desc\"},\"limit\":20}");
        assertEquals(s.colunas.get(0).chaves, List.of("dataset_1.total_passagens"));
        assertTrue(s.sql.endsWith("ORDER BY [c0] DESC"));
        assertEquals(s.parametros.get(0), 20);
    }

    @Test
    public void ordemPadraoEMedidaDecrescenteSemTempo() {
        SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"dimensions\":[\"placa\"]}");
        assertTrue(s.sql.endsWith("ORDER BY [c1] DESC"), s.sql);
    }

    @Test
    public void membroInexistenteGeraErroComNome() {
        assertErro(() -> montar("{\"measures\":[\"id_count\"]}"), "id_count");
    }

    @Test
    public void prefixoDeOutroDatasetERejeitado() {
        assertErro(() -> montar("{\"measures\":[\"dataset_2.total_passagens\"]}"), "outro dataset");
    }

    @Test
    public void nomeQualificadoPeloCubeERejeitado() {
        assertErro(() -> montar("{\"measures\":[\"veiculo_tempo_real.count\"]}"), "Campo invalido");
    }

    @Test
    public void injecaoEmValorDeFiltroViraParametro() {
        String ataque = "x'; DROP TABLE dbo.sis_usuario--";
        SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"placa\","
                + "\"operator\":\"=\",\"values\":[\"x'; DROP TABLE dbo.sis_usuario--\"]}]}");
        assertFalse(s.sql.contains("DROP"));
        assertTrue(s.sql.contains("t0.[placa] = ?"));
        assertTrue(s.parametros.contains(ataque));
    }

    @Test
    public void injecaoEmNomeDeCampoOuOrdemERejeitada() {
        assertErro(() -> montar("{\"measures\":[\"total_passagens]; DROP TABLE x--\"]}"), "nao existe");
        assertErro(() -> montar("{\"measures\":[\"total_passagens\"],\"order\":{\"placa\":\"desc; DROP\"}}"),
                "Direcao");
        assertErro(() -> montar("{\"measures\":[\"total_passagens\"],\"order\":{\"placa\":\"asc\"}}"),
                "nao esta na consulta");
    }

    @Test
    public void identificadorInvalidoNoCatalogoERejeitado() {
        DatasetResolvido ds = new DatasetResolvido(9, 9);
        ds.adicionarTabela(9, "dbo", "tabela];DROP", null, null);
        ds.adicionarColuna(9, "col", "texto");
        ds.adicionarCampo("col", "Col", 9, "col", "texto", true, "NONE", "dimensao", null);
        assertErro(() -> montador.montar(ds, consulta("{\"dimensions\":[\"col\"]}", 9)), "Identificador invalido");
    }

    @Test
    public void operadoresNasDuasGrafias() {
        String[][] casos = {
                {"=", "t0.[id_local] = ?"}, {"equals", "t0.[id_local] = ?"},
                {"!=", "t0.[id_local] <> ?"}, {"notEquals", "t0.[id_local] <> ?"},
                {">", "t0.[id_local] > ?"}, {"gt", "t0.[id_local] > ?"},
                {">=", "t0.[id_local] >= ?"}, {"gte", "t0.[id_local] >= ?"},
                {"<", "t0.[id_local] < ?"}, {"lt", "t0.[id_local] < ?"},
                {"<=", "t0.[id_local] <= ?"}, {"lte", "t0.[id_local] <= ?"},
                {"IS NULL", "t0.[id_local] IS NULL"}, {"notSet", "t0.[id_local] IS NULL"},
                {"IS NOT NULL", "t0.[id_local] IS NOT NULL"}, {"set", "t0.[id_local] IS NOT NULL"},
        };
        for (String[] c : casos) {
            SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"id_local\","
                    + "\"operator\":\"" + c[0] + "\",\"values\":[\"7\"]}]}");
            assertTrue(s.sql.contains("(" + c[1] + ")"), c[0] + " -> " + s.sql);
        }
        SqlMontado igual = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"id_local\","
                + "\"operator\":\"=\",\"values\":[\"7\"]}]}");
        assertEquals(igual.parametros.get(1), 7L);
    }

    @Test
    public void inENotInAceitamListaSeparadaPorVirgula() {
        SqlMontado in = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"id_local\","
                + "\"operator\":\"IN\",\"values\":[\"1, 2,3\"]}]}");
        assertTrue(in.sql.contains("t0.[id_local] IN (?, ?, ?)"), in.sql);
        assertEquals(in.parametros.subList(1, 4), List.of(1L, 2L, 3L));

        SqlMontado notIn = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"placa\","
                + "\"operator\":\"NOT IN\",\"values\":[\"A\",\"B\"]}]}");
        assertTrue(notIn.sql.contains("t0.[placa] NOT IN (?, ?)"), notIn.sql);
    }

    @Test
    public void inComMaisDe500ValoresERejeitado() {
        StringBuilder vals = new StringBuilder();
        for (int i = 0; i < 501; i++) vals.append(i == 0 ? "" : ",").append('"').append(i).append('"');
        assertErro(() -> montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"id_local\","
                + "\"operator\":\"IN\",\"values\":[" + vals + "]}]}"), "mais de 500");
    }

    @Test
    public void betweenDeDatasIncluiDiaFinal() {
        SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"data\","
                + "\"operator\":\"inDateRange\",\"values\":[\"2026-07-01\",\"2026-07-31\"]}]}");
        assertTrue(s.sql.contains("t0.[data] >= ? AND t0.[data] < ?"), s.sql);
        assertEquals(s.parametros.get(2), Timestamp.valueOf("2026-08-01 00:00:00"));
    }

    @Test
    public void likeEscapaCaracteresCuringa() {
        SqlMontado s = montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"placa\","
                + "\"operator\":\"contains\",\"values\":[\"50%_[x]\"]}]}");
        assertTrue(s.sql.contains("t0.[placa] LIKE ? ESCAPE '\\'"), s.sql);
        assertEquals(s.parametros.get(1), "%50\\%\\_\\[x]%");
    }

    @Test
    public void valorIncompativelComTipoERejeitado() {
        assertErro(() -> montar("{\"measures\":[\"total_passagens\"],\"filters\":[{\"member\":\"id_local\","
                + "\"operator\":\"=\",\"values\":[\"abc\"]}]}"), "invalido");
    }

    @Test
    public void filtroEmMedidaAgregadaVaiParaHaving() {
        SqlMontado s = montar("{\"measures\":[\"velocidade_media\"],\"dimensions\":[\"placa\"],"
                + "\"filters\":[{\"member\":\"velocidade_media\",\"operator\":\">\",\"values\":[\"80\"]}]}");
        assertTrue(s.sql.contains("HAVING (AVG(CAST(t0.[velocidade] AS FLOAT)) > ?)"), s.sql);
        assertEquals(s.parametros.get(1), new BigDecimal("80"));
    }

    @Test
    public void multiplicacaoPorRelacaoUmParaMuitosEBloqueada() {
        DatasetResolvido ds = datasetEquipamentos();
        assertErro(() -> montador.montar(ds, consulta(
                "{\"measures\":[\"qtd_equipamentos\",\"velocidade_media\"]}", 2)), "seria multiplicada");
    }

    @Test
    public void medidasSegurasComRelacaoUmParaMuitosSaoPermitidas() {
        DatasetResolvido ds = datasetEquipamentos();
        SqlMontado s = montador.montar(ds, consulta(
                "{\"measures\":[\"equip_distintos\",\"velocidade_media\"],\"dimensions\":[\"nome\"]}", 2));
        assertTrue(s.sql.contains("FROM [dbo].[local_vigente] AS t0 LEFT JOIN [muralha].[veiculo_tempo_real] AS t1 "
                + "ON t0.[id_local] = t1.[id_local]"), s.sql);
        assertTrue(s.sql.contains("COUNT(DISTINCT t0.[id_local])"));
    }

    @Test
    public void campoCalculadoTemColunasQualificadas() {
        SqlMontado s = montar("{\"measures\":[\"velocidade_kmh\"]}");
        assertTrue(s.sql.contains("SUM((t0.[velocidade] * 3.6)) AS [c0]"), s.sql);
    }

    @Test
    public void campoCalculadoComColunaForaDoDatasetERejeitado() {
        DatasetResolvido ds = datasetPassagens();
        ds.adicionarCampo("invalido", "Inv", null, null, null, true, "NONE", "dimensao", "UPPER(senha)");
        assertErro(() -> montador.montar(ds, consulta("{\"dimensions\":[\"invalido\"]}", 1)), "senha");
    }

    @Test
    public void campoCalculadoComColunaAmbiguaERejeitado() {
        DatasetResolvido ds = datasetPassagens();
        ds.adicionarCampo("amb", "Amb", null, null, null, true, "NONE", "dimensao", "id_local + 1");
        assertErro(() -> montador.montar(ds, consulta("{\"dimensions\":[\"amb\"]}", 1)), "mais de uma tabela");
    }

    @Test
    public void tabelaSemCaminhoDeJoinERejeitada() {
        DatasetResolvido ds = new DatasetResolvido(3, PASSAGENS);
        ds.adicionarTabela(PASSAGENS, "muralha", "veiculo_tempo_real", null, "id");
        ds.adicionarTabela(EQUIPAMENTOS, "dbo", "local_vigente", null, "id_local");
        ds.adicionarColuna(PASSAGENS, "id", "inteiro");
        ds.adicionarColuna(EQUIPAMENTOS, "nome", "texto");
        ds.adicionarRelacao(EQUIPAMENTOS, "id", PASSAGENS, "id", "many_to_one");
        ds.adicionarCampo("nome", "Nome", EQUIPAMENTOS, "nome", "texto", true, "NONE", "dimensao", null);
        assertErro(() -> montador.montar(ds, consulta("{\"dimensions\":[\"nome\"]}", 3)),
                "nao tem relacionamento valido");
        assertEquals(montador.validarDataset(ds).size(), 1);
    }

    @Test
    public void filtroFixoEntraNoWhere() {
        DatasetResolvido ds = datasetPassagens();
        ds.adicionarFiltroFixo("velocidade", ">", "0");
        SqlMontado s = montador.montar(ds, consulta("{\"measures\":[\"total_passagens\"]}", 1));
        assertTrue(s.sql.contains("WHERE (t0.[velocidade] > ?)"), s.sql);
        assertEquals(s.parametros.get(1), new BigDecimal("0"));
    }

    @Test
    public void granularidadesCompativeisComSqlServer2016() {
        for (String g : new String[]{"hour", "day", "week", "month", "quarter", "year"}) {
            SqlMontado s = montar("{\"measures\":[\"total_passagens\"],"
                    + "\"timeDimensions\":[{\"dimension\":\"data\",\"granularity\":\"" + g + "\"}]}");
            assertFalse(s.sql.contains("DATETRUNC"), s.sql);
            assertEquals(s.colunas.get(0).chaves.get(0), "data." + g);
        }
    }

    @Test
    public void campoDeTempoExigeDataHora() {
        assertErro(() -> montar("{\"measures\":[\"total_passagens\"],"
                + "\"timeDimensions\":[{\"dimension\":\"placa\",\"granularity\":\"day\"}]}"), "data/hora");
    }

    @Test
    public void medidaEmDimensoesERejeitada() {
        assertErro(() -> montar("{\"dimensions\":[\"total_passagens\"]}"), "e uma medida");
    }

    @Test
    public void consultaVaziaERejeitada() {
        assertErro(() -> montar("{\"measures\":[],\"dimensions\":[]}"), "ao menos uma");
    }

    @Test
    public void limiteENormalizado() {
        assertEquals(montar("{\"measures\":[\"total_passagens\"],\"limit\":999999}").parametros.get(0), 10000);
        assertEquals(montar("{\"measures\":[\"total_passagens\"],\"limit\":0}").parametros.get(0), 10000);
    }

    @Test
    public void validarDatasetRealSemErros() {
        assertTrue(montador.validarDataset(datasetPassagens()).isEmpty());
        assertTrue(montador.validarDataset(datasetEquipamentos()).isEmpty());
    }

    @Test
    public void validarDatasetApontaCampoSemOrigem() {
        DatasetResolvido ds = datasetPassagens();
        ds.adicionarCampo("orfao", "Orfao", null, null, null, true, "NONE", "dimensao", null);
        List<String> erros = montador.validarDataset(ds);
        assertEquals(erros.size(), 1);
        assertTrue(erros.get(0).contains("orfao"));
    }

    @Test
    public void respostaDeConsultaAntigaDoModeladorTambemEInterpretada() {
        JsonObject q = JsonParser.parseString("{\"dimensions\":[\"placa\"],\"measures\":[],\"limit\":50}")
                .getAsJsonObject();
        SqlMontado s = montador.montar(datasetPassagens(), ConsultaDashboard.deJson(q, 1));
        assertEquals(s.parametros.get(0), 50);
    }
}
