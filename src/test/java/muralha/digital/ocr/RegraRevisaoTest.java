package muralha.digital.ocr;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class RegraRevisaoTest {

    private static LeituraPlaca emb(int ordem, LeituraPlaca.Tipo t, String placa, Double score) {
        return new LeituraPlaca(ordem, t, placa, score, LeituraPlaca.Origem.EMBARCADA);
    }

    @Test
    public void scoreAbaixoDoLimiarEncaminhaComOMenorScore() {
        RegraRevisao.Avaliacao a = RegraRevisao.avaliar(Arrays.asList(
            emb(1, LeituraPlaca.Tipo.TRACAO, "ABC1D23", 92.0), emb(2, LeituraPlaca.Tipo.FRONTAL, "ABC1D28", 61.5)), 85, false, true);
        assertEquals(a.getMotivo(), RegraRevisao.Motivo.SCORE_BAIXO);
        assertEquals(a.getScoreMinimo(), 61.5);
        assertTrue(a.getDetalhe().contains("ABC1D28") && a.getDetalhe().contains("61.50") && a.getDetalhe().contains("85"));
    }

    @Test
    public void scoreIgualOuAcimaDoLimiarNaoEncaminha() {
        RegraRevisao.Avaliacao a = RegraRevisao.avaliar(Arrays.asList(emb(1, LeituraPlaca.Tipo.TRACAO, "ABC1234", 85.0)), 85, false, true);
        assertFalse(a.encaminhar());
        assertEquals(a.getScoreMinimo(), 85.0);
    }

    @Test
    public void semPlacaLidaSoVaiParaRevisaoSeConfigurado() {
        List<LeituraPlaca> l = Arrays.asList(emb(1, LeituraPlaca.Tipo.TRACAO, "  ", null));
        assertEquals(RegraRevisao.avaliar(l, 85, false, true).getMotivo(), RegraRevisao.Motivo.SEM_LEITURA);
        assertFalse(RegraRevisao.avaliar(l, 85, false, false).encaminhar());
    }

    @Test
    public void divergenciaEntreEquipamentoECentral() {
        List<LeituraPlaca> l = new ArrayList<>();
        l.add(emb(1, LeituraPlaca.Tipo.TRACAO, "ABC1234", 99.0));
        l.add(new LeituraPlaca(1, LeituraPlaca.Tipo.TRACAO, "ABC1284", 97.0, LeituraPlaca.Origem.CENTRAL));
        RegraRevisao.Avaliacao a = RegraRevisao.avaliar(l, 85, false, true);
        assertEquals(a.getMotivo(), RegraRevisao.Motivo.DIVERGENCIA_CAMPO_CENTRAL);
        assertTrue(a.getDetalhe().contains("ABC1234") && a.getDetalhe().contains("ABC1284"));
    }

    @Test
    public void centralIgualNaoEhDivergencia() {
        List<LeituraPlaca> l = new ArrayList<>();
        l.add(emb(1, LeituraPlaca.Tipo.TRACAO, "abc1234", 99.0));
        l.add(new LeituraPlaca(1, LeituraPlaca.Tipo.TRACAO, "ABC1234", 97.0, LeituraPlaca.Origem.CENTRAL));
        assertFalse(RegraRevisao.avaliar(l, 85, false, true).encaminhar());
    }

    @Test
    public void semScoreSoEncaminhaSeConfigurado() {
        List<LeituraPlaca> l = Arrays.asList(emb(1, LeituraPlaca.Tipo.TRACAO, "ABC1234", null));
        assertFalse(RegraRevisao.avaliar(l, 85, false, true).encaminhar());
        assertEquals(RegraRevisao.avaliar(l, 85, true, true).getMotivo(), RegraRevisao.Motivo.SCORE_AUSENTE);
    }

    @Test
    public void leiturasManuaisNaoEntramNaRegra() {
        List<LeituraPlaca> l = Arrays.asList(emb(1, LeituraPlaca.Tipo.TRACAO, "ABC1234", 99.0),
            new LeituraPlaca(3, LeituraPlaca.Tipo.REBOQUE, "XYZ9A99", 1.0, LeituraPlaca.Origem.MANUAL));
        assertFalse(RegraRevisao.avaliar(l, 85, false, true).encaminhar());
    }

    @Test
    public void leiturasDaPassagemComFrontalEScores() {
        List<LeituraPlaca> l = RegraRevisao.leiturasDaPassagem("ABC1234 ", "ABC1235", new Double[] { 90.0, 70.0 });
        assertEquals(l.size(), 2);
        assertEquals(l.get(0).getTipo(), LeituraPlaca.Tipo.TRACAO);
        assertEquals(l.get(0).getPlaca(), "ABC1234");
        assertEquals(l.get(1).getScore(), 70.0);
        assertEquals(RegraRevisao.leiturasDaPassagem("ABC1234", "  ", null).size(), 1);
    }

    @Test
    public void scoresDoDadoBrutoNumeroListaEscala() {
        assertEquals(RegraRevisao.scoresDoDadoBruto("{\"ocr\":{\"score\":88.5}}", "ocr.score", 100), new Double[] { 88.5 });
        assertEquals(RegraRevisao.scoresDoDadoBruto("{\"s\":[0.9,0.6]}", "s", 1), new Double[] { 90.0, 60.0 });
        assertEquals(RegraRevisao.scoresDoDadoBruto("{\"s\":[150,\"x\"]}", "s", 100), new Double[] { null, null }, "fora de 0-100 ou não numérico é ignorado");
        assertEquals(RegraRevisao.scoresDoDadoBruto("não é json", "s", 100).length, 0);
        assertEquals(RegraRevisao.scoresDoDadoBruto(null, "s", 100).length, 0);
    }
}
