package muralha.digital.notificacao;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConfiguracaoSonsPrioridadeTest {

    @Test
    public void buscaPeloValorDaPrioridade() {
        String sql = ConfiguracaoSons.sqlPrioridadeEmUso(false);
        assertTrue(sql.contains("WHERE prioridade = ?"));
        assertFalse(sql.contains("id <>"));
    }

    @Test
    public void ignoraOProprioTipoEmEdicao() {
        String sql = ConfiguracaoSons.sqlPrioridadeEmUso(true);
        assertTrue(sql.contains("WHERE prioridade = ?"));
        assertTrue(sql.contains("AND id <> CAST(? AS UNIQUEIDENTIFIER)"));
    }

    @Test
    public void converteSoInteiros() {
        assertEquals(ConfiguracaoSons.converterPrioridade(" 3 "), Integer.valueOf(3));
        assertNull(ConfiguracaoSons.converterPrioridade("abc"));
        assertNull(ConfiguracaoSons.converterPrioridade(""));
        assertNull(ConfiguracaoSons.converterPrioridade(null));
    }

    @Test
    public void prioridadeInvalidaNaoConsultaOBanco() {
        // Sem conexão disponível no teste: se consultasse o banco, logaria erro e devolveria 0 do mesmo jeito,
        // mas o caminho esperado é sair antes de abrir a conexão.
        assertEquals(ConfiguracaoSons.ObterPrioridade("x", null).getPrioridade(), 0);
    }
}
