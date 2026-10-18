package muralha.digital.alerta.operacional;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConfigETratamentoTest {

    @Test
    public void tratamentosValidos() {
        assertEquals(TratamentoAlerta.interpretar("CAMPO"), TratamentoAlerta.CAMPO);
        assertEquals(TratamentoAlerta.interpretar("analise_operacional"), TratamentoAlerta.ANALISE_OPERACIONAL);
        assertEquals(TratamentoAlerta.interpretar("GERAR_OCORRENCIA"), TratamentoAlerta.GERAR_OCORRENCIA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void tratamentoDesconhecidoEhRecusado() {
        TratamentoAlerta.interpretar("INEXISTENTE");
    }

    @Test
    public void tratamentoNuloRetornaNulo() {
        assertNull(TratamentoAlerta.interpretar(null));
        assertNull(TratamentoAlerta.interpretar(""));
    }

    @Test
    public void corValidaAceitaSoAsCoresDoPopup() {
        for (String cor : new String[] {"error", "orange", "warning", "success", "info", "purple", "brown", "pink"}) {
            assertTrue(ConfigTipoAlerta.corValida(cor), cor);
        }
        assertTrue(ConfigTipoAlerta.corValida(null));
        assertTrue(ConfigTipoAlerta.corValida(""));
    }

    @Test
    public void corInvalidaEhRecusada() {
        assertFalse(ConfigTipoAlerta.corValida("#FF5733"));
        assertFalse(ConfigTipoAlerta.corValida("toast-danger"));
        assertFalse(ConfigTipoAlerta.corValida("cor com espaço"));
        assertFalse(ConfigTipoAlerta.corValida("cor;injection"));
    }

    @Test
    public void iconeAceitaSoClassesDoBootstrapIcons() {
        assertTrue(ConfigTipoAlerta.iconeValido("bi-eye"));
        assertTrue(ConfigTipoAlerta.iconeValido("bi-exclamation-triangle-fill"));
        assertTrue(ConfigTipoAlerta.iconeValido(null));
        assertFalse(ConfigTipoAlerta.iconeValido("fa-bell"));
        assertFalse(ConfigTipoAlerta.iconeValido("bi-eye\" onmouseover=\"x"));
        assertFalse(ConfigTipoAlerta.iconeValido("bi-"));
    }

    @Test
    public void tratamentosExecutaveisPeloSistema() {
        assertTrue(TratamentoAlerta.REGISTRO_ESTATISTICO.isExecutavel());
        assertTrue(TratamentoAlerta.PRIORIZAR_AUDITORIA.isExecutavel());
        assertTrue(TratamentoAlerta.GERAR_OCORRENCIA.isExecutavel());
        assertFalse(TratamentoAlerta.CAMPO.isExecutavel());
        assertFalse(TratamentoAlerta.OUTRO.isExecutavel());
    }

    @Test
    public void parametrosDoTratamentoDevemSerObjetoJson() {
        assertTrue(TipoAlertaConfigServlet.parametrosValidos(null));
        assertTrue(TipoAlertaConfigServlet.parametrosValidos("{\"equipe\":\"GTO\"}"));
        assertFalse(TipoAlertaConfigServlet.parametrosValidos("[1,2]"));
        assertFalse(TipoAlertaConfigServlet.parametrosValidos("texto livre"));
    }

    @Test
    public void filtroHistoricoComTodosOsCriterios() {
        FiltroHistorico f = FiltroHistorico.de("10", "1191", "PENDENTE");
        assertTrue(f.temFiltro());
        assertTrue(f.getSqlExtra().contains("id_localidade"));
        assertTrue(f.getSqlExtra().contains("id_usuario"));
        assertTrue(f.getSqlExtra().contains("status"));
        assertEquals(f.getParametros().size(), 3);
    }

    @Test
    public void filtroHistoricoVazio() {
        FiltroHistorico f = FiltroHistorico.de(null, "", null);
        assertFalse(f.temFiltro());
        assertEquals(f.getParametros().size(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filtroHistoricoRecusaResultadoInvalido() {
        FiltroHistorico.de(null, null, "APAGADO");
    }

    @Test
    public void parametrosRetencaoUsaPadraoParaValoresInvalidos() {
        ParametrosRetencao p = new ParametrosRetencao(-1, 0, 0, 0, 0, 0);
        assertEquals(p.getOcupacaoMinima(), ParametrosRetencao.OCUPACAO_PADRAO);
        assertEquals(p.getVelocidadeMaxima(), ParametrosRetencao.VELOCIDADE_PADRAO);
        assertEquals(p.getDuracaoHoras(), ParametrosRetencao.DURACAO_PADRAO);
    }
}
