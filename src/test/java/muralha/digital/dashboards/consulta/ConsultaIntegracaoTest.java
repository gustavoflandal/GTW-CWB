package muralha.digital.dashboards.consulta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Integracao (somente leitura) contra o SQL Server de desenvolvimento.
 * Requer DASH_TESTE_JDBC_URL, DASH_TESTE_USUARIO e DASH_TESTE_SENHA; sem elas o teste e pulado.
 */
public class ConsultaIntegracaoTest {

    private Connection conn;
    private final MontadorSql montador = new MontadorSql(new PeriodoRelativo());
    private final ExecutorConsulta executor = new ExecutorConsulta();

    @BeforeClass
    public void conectar() throws Exception {
        String url = System.getenv("DASH_TESTE_JDBC_URL");
        if (url == null || url.isEmpty()) {
            throw new SkipException("DASH_TESTE_JDBC_URL nao definida");
        }
        conn = DriverManager.getConnection(url, System.getenv("DASH_TESTE_USUARIO"), System.getenv("DASH_TESTE_SENHA"));
    }

    @AfterClass(alwaysRun = true)
    public void desconectar() throws Exception {
        if (conn != null) conn.close();
    }

    private ExecutorConsulta.Resultado executar(int idDataset, String json) throws Exception {
        DatasetResolvido ds = ResolvedorDataset.carregar(conn, idDataset);
        JsonObject q = JsonParser.parseString(json).getAsJsonObject();
        SqlMontado sql = montador.montar(ds, ConsultaDashboard.deJson(q, idDataset));
        long inicio = System.currentTimeMillis();
        ExecutorConsulta.Resultado r = executor.executar(conn, sql);
        System.out.println("[integracao] ds=" + idDataset + " " + (System.currentTimeMillis() - inicio)
                + " ms, " + r.dados.size() + " linhas, cache=" + r.cacheHit + "\n  " + sql.sql);
        return r;
    }

    @Test
    public void datasetsPublicadosSaoValidos() throws Exception {
        List<Integer> ids = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id FROM dash.dataset WHERE status = 'publicado'")) {
            while (rs.next()) ids.add(rs.getInt(1));
        }
        assertFalse(ids.isEmpty());
        for (int id : ids) {
            List<String> erros = montador.validarDataset(ResolvedorDataset.carregar(conn, id));
            assertTrue(erros.isEmpty(), "dataset " + id + ": " + erros);
        }
    }

    @Test
    public void passagensPorEquipamentoHoje() throws Exception {
        ExecutorConsulta.Resultado r = executar(1, "{\"measures\":[\"total_passagens\",\"velocidade_media\"],"
                + "\"dimensions\":[\"equipamento\"],"
                + "\"timeDimensions\":[{\"dimension\":\"data\",\"granularity\":\"day\",\"dateRange\":\"last 7 days\"}],"
                + "\"order\":{\"total_passagens\":\"desc\"},\"limit\":20}");
        assertTrue(r.dados.size() <= 20);
        if (r.dados.size() > 0) {
            JsonObject linha = r.dados.get(0).getAsJsonObject();
            assertTrue(linha.has("data.day") && linha.has("data") && linha.has("equipamento"));
            assertTrue(linha.get("total_passagens").getAsLong() > 0);
        }
    }

    @Test
    public void velocidadeMediaPorMesUltimos12Meses() throws Exception {
        ExecutorConsulta.Resultado r = executar(1, "{\"measures\":[\"dataset_1.velocidade_media\"],"
                + "\"timeDimensions\":[{\"dimension\":\"dataset_1.data\",\"granularity\":\"month\","
                + "\"dateRange\":\"last 12 months\"}]}");
        assertTrue(r.dados.size() <= 12);
        ExecutorConsulta.Resultado repetida = executar(1, "{\"measures\":[\"dataset_1.velocidade_media\"],"
                + "\"timeDimensions\":[{\"dimension\":\"dataset_1.data\",\"granularity\":\"month\","
                + "\"dateRange\":\"last 12 months\"}]}");
        assertTrue(repetida.cacheHit);
    }

    @Test
    public void datasetComBaseEmEquipamentos() throws Exception {
        ExecutorConsulta.Resultado r = executar(2, "{\"dimensions\":[\"nome\",\"placa\"],"
                + "\"filters\":[{\"member\":\"data\",\"operator\":\"inDateRange\","
                + "\"values\":[\"2026-10-01\",\"2026-10-14\"]}],\"limit\":10}");
        assertTrue(r.dados.size() <= 10);
    }
}
