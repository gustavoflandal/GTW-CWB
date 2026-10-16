package muralha.digital.ocr;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/** Justificativa obrigatória, formato de placa, placas adicionais, filtros da fila e origem do score. */
public class CorrecaoEFiltroRevisaoTest {

    @Test
    public void placaAntigaEMercosul() {
        assertEquals(ValidacaoCorrecao.placa(" abc1234 "), "ABC1234");
        assertEquals(ValidacaoCorrecao.placa("ABC1D23"), "ABC1D23");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void placaInvalidaEhRecusada() {
        ValidacaoCorrecao.placa("AB12345");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*motivo.*")
    public void motivoEhObrigatorio() {
        ValidacaoCorrecao.justificativa(null, false, "texto qualquer");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*mínimo 10.*")
    public void motivoQueExigeTextoPedeDezCaracteres() {
        ValidacaoCorrecao.justificativa(7, true, "curto");
    }

    @Test
    public void textoOpcionalQuandoOMotivoNaoExige() {
        assertNull(ValidacaoCorrecao.justificativa(1, false, "   "));
        assertEquals(ValidacaoCorrecao.justificativa(7, true, "  Placa frontal suja  "), "Placa frontal suja");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void justificativaTemLimite() {
        ValidacaoCorrecao.justificativa(1, false, new String(new char[501]).replace('\0', 'x'));
    }

    @Test
    public void placasAdicionaisPorTipo() {
        List<LeituraPlaca> l = CorrecaoPlacaService.adicionais("reboque:abc1234, SEMIRREBOQUE:XYZ9A99");
        assertEquals(l.size(), 2);
        assertEquals(l.get(0).getTipo(), LeituraPlaca.Tipo.REBOQUE);
        assertEquals(l.get(1).getPlaca(), "XYZ9A99");
        assertTrue(CorrecaoPlacaService.adicionais("").isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void placaAdicionalSemTipoEhRecusada() {
        CorrecaoPlacaService.adicionais("ABC1234");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void maximoDeCincoPlacasAdicionais() {
        CorrecaoPlacaService.adicionais("REBOQUE:ABC1231,REBOQUE:ABC1232,REBOQUE:ABC1233,REBOQUE:ABC1234,REBOQUE:ABC1235,REBOQUE:ABC1236");
    }

    @Test
    public void filtroDaFilaComTodosOsCriterios() {
        FiltroRevisao f = FiltroRevisao.de("2026-10-01", "2026-10-07", "10,20", "pendente", "SCORE_BAIXO", "50", "84,5", "FILA", "1191");
        assertEquals(f.getSql(), "r.dt_passagem >= ? AND r.dt_passagem < ? AND r.id_local IN (?,?) AND r.situacao = ? AND r.motivo = ?"
            + " AND r.origem_intervencao = ? AND r.score_minimo >= ? AND r.score_minimo <= ? AND r.id_usuario_tratamento = ?");
        assertEquals(f.getParametros().get(4), "PENDENTE");
        assertEquals(f.getParametros().get(8), 84.5);
        assertEquals(f.getParametros().get(9), 1191);
    }

    @Test
    public void filtroSoComPeriodo() {
        FiltroRevisao f = FiltroRevisao.de("2026-10-01", "2026-10-01", null, "", null, null, null, null, null);
        assertEquals(f.getSql(), "r.dt_passagem >= ? AND r.dt_passagem < ?");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filtroRecusaScoreForaDaFaixa() {
        FiltroRevisao.de("2026-10-01", "2026-10-02", null, null, null, "101", null, null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filtroRecusaSituacaoDesconhecida() {
        FiltroRevisao.de("2026-10-01", "2026-10-02", null, "APAGADA", null, null, null, null, null);
    }

    @Test
    public void origemDoScore() {
        assertEquals(OrigemScore.interpretar("DADO_BRUTO:ocr.score").formatar(), "DADO_BRUTO:ocr.score");
        assertSame(OrigemScore.interpretar(""), OrigemScore.AUSENTE);
        assertEquals(OrigemScore.interpretar("tabela_leitura").getTipo(), OrigemScore.Tipo.TABELA_LEITURA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void origemDadoBrutoSemCampoEhRecusada() {
        OrigemScore.interpretar("DADO_BRUTO:");
    }

    @Test
    public void limiarDaConfiguracao() {
        assertEquals(OcrConfigServlet.limiar("85,5", false), 85.5);
        assertNull(OcrConfigServlet.limiar(" ", true));
        assertTrue(ParametrosOcr.limiarValido(0) && ParametrosOcr.limiarValido(100) && !ParametrosOcr.limiarValido(100.1));
        assertEquals(new ParametrosOcr(null, 150, 7, false, false, 24, 500, 10).getLimiar(), ParametrosOcr.LIMIAR_PADRAO, "limiar inválido volta ao padrão");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void limiarObrigatorioNoPadrao() {
        OcrConfigServlet.limiar("", false);
    }
}
