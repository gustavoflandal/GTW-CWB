package muralha.digital.kpi;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ValidadorSqlKpiTest {

    @DataProvider
    public Object[][] validas() {
        return new Object[][] {
            {"SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real WITH (NOLOCK) WHERE CAST(data AS DATE) = CAST(SYSDATETIME() AS DATE)"},
            {"SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK) LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id WHERE vsa.status_analise IS NULL OR vsa.status_analise = 'AGUARDANDO_ANALISE'"},
            {"SELECT CAST(COUNT(*) AS NUMERIC) AS valor\nFROM muralha.vtr_status_analise vsa\nWHERE vsa.status_analise = 'PRE_APROVADA'\n  AND EXISTS (\n    SELECT 1 FROM muralha.infracao_analise ia\n    WHERE ia.id_infracao = vsa.id_veiculo_tempo_real\n      AND ia.dt_analise >= DATEADD(DAY, -7, SYSDATETIME())\n  )"},
            {"SELECT COUNT(*) AS valor FROM muralha.alerta a JOIN muralha.tipo_alerta_ocorrencia t ON a.id_tipo_alerta_ocorrencia = t.id WHERE t.nomeinterno = 'CONGESTIONAMENTO' AND a.data >= DATEADD(DAY,-7,GETDATE())"},
            {"SELECT TOP 1 percentil95_ms AS valor FROM muralha.alerta_sla ORDER BY janela_fim DESC"},
            {"  select 'DELETE; DROP' AS rotulo, 1 AS valor  "},
            {"SELECT data_alt AS valor FROM muralha.veiculo_tempo_real"},
            {"SELECT COUNT(*) AS valor FROM muralha.status_alerta WHERE UPPER(descricao) <> 'PENDENTE'"},
            {"SELECT COUNT(*) AS [valor] FROM muralha.alerta"},
            {"SELECT 1 AS \"valor\""},
            {"SELECT 1 AS [a]]b] FROM muralha.alerta"},
            {"SELECT 'it''s' AS rotulo, 1 AS valor"}
        };
    }

    @Test(dataProvider = "validas")
    public void aceitaConsultasDeKpi(String sql) {
        assertNull(ValidadorSqlKpi.validar(sql), sql);
    }

    @DataProvider
    public Object[][] invalidas() {
        return new Object[][] {
            {null}, {""}, {"   "},
            {"SELECT 1 AS valor; DELETE FROM muralha.kpi_config"},
            {"SELECT * INTO muralha.copia FROM muralha.kpi_config"},
            {"EXEC sp_who"},
            {"SELECT 1 AS valor -- comentario"},
            {"SELECT 1 AS valor /* x */"},
            {"UPDATE muralha.kpi_config SET ativo = 0"},
            {"SELECT 1 AS valor WHERE 1 = 1 AND EXISTS (SELECT 1 FROM OPENROWSET('a','b','c'))"},
            {"SELECT 'aberto AS valor"},
            {"SELECT xp_cmdshell AS valor"},
            {"WITH c AS (SELECT 1 AS valor) SELECT valor FROM c"},
            {"SELECT 1 AS valor WAITFOR DELAY '00:00:10'"},
            {"SELECT 1 AS [a']; DROP TABLE muralha.kpi_config; SELECT 1 AS [b']"},
            {"SELECT 1 AS \"a'\"; DELETE FROM x; SELECT 1 AS \"b'\""},
            {"SELECT 1 AS [aberto"}
        };
    }

    @Test(dataProvider = "invalidas")
    public void recusaConsultasPerigosasOuMalFormadas(String sql) {
        assertNotNull(ValidadorSqlKpi.validar(sql), String.valueOf(sql));
    }

    @Test
    public void recusaConsultaMuitoLonga() {
        StringBuilder sb = new StringBuilder("SELECT 1 AS valor FROM muralha.kpi_config WHERE nome = '");
        while (sb.length() < 4100) sb.append('x');
        sb.append('\'');
        assertNotNull(ValidadorSqlKpi.validar(sb.toString()));
    }
}
