package muralha.digital.dashboards;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GramaticaCampoCalculadoTest {

    private final GramaticaCampoCalculado gramatica = new GramaticaCampoCalculado();

    @Test
    public void deveAceitarExpressaoSimples() {
        var r = gramatica.validar("velocidade * 3.6");
        assertTrue(r.valido);
    }

    @Test
    public void deveAceitarCaseWhen() {
        var r = gramatica.validar("CASE WHEN velocidade > 60 THEN 'alta' ELSE 'normal' END");
        assertTrue(r.valido);
    }

    @Test
    public void deveAceitarFuncoesPermitidas() {
        assertTrue(gramatica.validar("YEAR(data_hora)").valido);
        assertTrue(gramatica.validar("UPPER(nome)").valido);
        assertTrue(gramatica.validar("ROUND(velocidade, 2)").valido);
        assertTrue(gramatica.validar("COALESCE(nome, 'desconhecido')").valido);
        assertTrue(gramatica.validar("IIF(velocidade > 60, 'alta', 'normal')").valido);
        assertTrue(gramatica.validar("ABS(velocidade - 50)").valido);
        assertTrue(gramatica.validar("CAST(velocidade AS INT)").valido);
    }

    @Test
    public void deveRejeitarStringAgg() {
        var r = gramatica.validar("STRING_AGG(nome, ',')");
        assertFalse(r.valido);
        assertTrue(r.erro.contains("SQL Server 2016"));
    }

    @Test
    public void deveRejeitarTrim() {
        var r = gramatica.validar("TRIM(nome)");
        assertFalse(r.valido);
    }

    @Test
    public void deveRejeitarConcatWs() {
        var r = gramatica.validar("CONCAT_WS(',', a, b)");
        assertFalse(r.valido);
    }

    @Test
    public void deveRejeitarTranslate() {
        var r = gramatica.validar("TRANSLATE(nome, 'abc', 'xyz')");
        assertFalse(r.valido);
    }

    @Test
    public void deveRejeitarSqlInjection() {
        assertFalse(gramatica.validar("; DROP TABLE usuarios").valido);
        assertFalse(gramatica.validar("nome; DELETE FROM dash.dataset").valido);
        assertFalse(gramatica.validar("1=1 -- comentario").valido);
        assertFalse(gramatica.validar("exec xp_cmdshell 'dir'").valido);
    }

    @Test
    public void deveRejeitarSubquery() {
        assertFalse(gramatica.validar("(SELECT MAX(id) FROM tabela)").valido);
    }

    @Test
    public void deveRejeitarExpressaoVazia() {
        assertFalse(gramatica.validar("").valido);
        assertFalse(gramatica.validar(null).valido);
        assertFalse(gramatica.validar("   ").valido);
    }

    @Test
    public void deveRejeitarParentesesDesbalanceados() {
        assertFalse(gramatica.validar("YEAR(data_hora").valido);
        assertFalse(gramatica.validar("ROUND(x, 2))").valido);
    }

    @Test
    public void deveRejeitarCaseEndDesbalanceados() {
        assertFalse(gramatica.validar("CASE WHEN x > 0 THEN 'ok'").valido);
    }

    @Test
    public void deveAceitarOperadorConcatenacao() {
        var r = gramatica.validar("nome || ' - ' || sobrenome");
        assertTrue(r.valido);
    }

    @Test
    public void deveAceitarExpressaoComplexa() {
        var r = gramatica.validar(
                "CASE WHEN YEAR(data_hora) = 2026 AND MONTH(data_hora) >= 10 " +
                "THEN ROUND(velocidade * 3.6, 1) ELSE 0 END"
        );
        assertTrue(r.valido);
    }
}
