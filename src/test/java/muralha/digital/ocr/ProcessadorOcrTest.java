package muralha.digital.ocr;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class ProcessadorOcrTest {

    private static final long AGORA = 1_760_000_000_000L;
    private static final Timestamp HA_UMA_HORA = new Timestamp(AGORA - 3_600_000L);
    private static final Timestamp AGORINHA = new Timestamp(AGORA - 60_000L);

    static class RepoFalso implements RepositorioOcr {
        final Map<String, List<LeituraPlaca>> gravadas = new HashMap<>();
        final Map<String, String> dadoBruto = new HashMap<>();
        final Map<String, List<LeituraPlaca>> novas = new HashMap<>();
        final Map<String, RegraRevisao.Avaliacao> avaliacoes = new HashMap<>();
        final Map<String, Double> limiares = new HashMap<>();

        @Override public List<LeituraPlaca> leiturasGravadas(String id) { return gravadas.getOrDefault(id, Collections.emptyList()); }
        @Override public String dadoBruto(String idCaptura) { return dadoBruto.get(idCaptura); }
        @Override public void gravarLeituras(PassagemPendente p, List<LeituraPlaca> l) { novas.put(p.getIdVeiculoTempoReal(), l); }
        @Override public void registrarAvaliacao(PassagemPendente p, RegraRevisao.Avaliacao a, double limiar) {
            avaliacoes.put(p.getIdVeiculoTempoReal(), a);
            limiares.put(p.getIdVeiculoTempoReal(), limiar);
        }
    }

    private static PassagemPendente pp(String id, int local, Timestamp t, String captura, String placa, String frontal) {
        return new PassagemPendente(id, local, t, t, captura, placa, frontal);
    }

    private static ParametrosOcr param(String origem, double limiar) {
        return new ParametrosOcr(OrigemScore.interpretar(origem), limiar, 100, false, true, 24, 500, 10);
    }

    @Test
    public void scoreDoDadoBrutoAbaixoDoLimiarVaiParaAFila() {
        RepoFalso r = new RepoFalso();
        r.dadoBruto.put("c1", "{\"ocr\":{\"score\":[70,95]}}");
        ProcessadorOcr.Resumo res = new ProcessadorOcr(r).processar(
            Collections.singletonList(pp("v1", 10, HA_UMA_HORA, "c1", "ABC1234", "ABC1234")),
            param("DADO_BRUTO:ocr.score", 85), new HashMap<>(), AGORA);
        assertEquals(r.avaliacoes.get("v1").getMotivo(), RegraRevisao.Motivo.SCORE_BAIXO);
        assertEquals(r.novas.get("v1").size(), 2, "tração e frontal gravadas com score");
        assertEquals(r.novas.get("v1").get(0).getScore(), 70.0);
        assertEquals(res.getEncaminhadas(RegraRevisao.Motivo.SCORE_BAIXO), 1);
    }

    @Test
    public void limiarDoEquipamentoPrevaleceSobreOGlobal() {
        RepoFalso r = new RepoFalso();
        r.dadoBruto.put("c1", "{\"score\":80}");
        Map<Integer, ProcessadorOcr.ConfigEquipamento> cfg = new HashMap<>();
        cfg.put(10, new ProcessadorOcr.ConfigEquipamento(null, 75.0));
        new ProcessadorOcr(r).processar(Collections.singletonList(pp("v1", 10, HA_UMA_HORA, "c1", "ABC1234", null)),
            param("DADO_BRUTO:score", 85), cfg, AGORA);
        assertFalse(r.avaliacoes.get("v1").encaminhar());
        assertEquals(r.limiares.get("v1"), 75.0);
    }

    @Test
    public void scoreQueAindaPodeChegarAguarda() {
        RepoFalso r = new RepoFalso();
        ProcessadorOcr.Resumo res = new ProcessadorOcr(r).processar(
            Arrays.asList(pp("novo", 10, AGORINHA, "c1", "ABC1234", null), pp("velho", 10, HA_UMA_HORA, "c2", "ABC1234", null)),
            param("DADO_BRUTO:score", 85), new HashMap<>(), AGORA);
        assertEquals(res.getAguardandoScore(), 1);
        assertFalse(r.avaliacoes.containsKey("novo"));
        assertFalse(r.avaliacoes.get("velho").encaminhar(), "sem score e sem OCR_REVISAR_SEM_SCORE: não vai para revisão");
    }

    @Test
    public void origemAusenteAvaliaNaHoraESemPlacaVaiParaRevisao() {
        RepoFalso r = new RepoFalso();
        new ProcessadorOcr(r).processar(Collections.singletonList(pp("v1", 10, AGORINHA, null, "   ", null)),
            param("AUSENTE", 85), new HashMap<>(), AGORA);
        assertEquals(r.avaliacoes.get("v1").getMotivo(), RegraRevisao.Motivo.SEM_LEITURA);
    }

    @Test
    public void leiturasJaGravadasPelaRecepcaoSaoUsadasENaoRegravadas() {
        RepoFalso r = new RepoFalso();
        List<LeituraPlaca> g = new ArrayList<>();
        g.add(new LeituraPlaca(1, LeituraPlaca.Tipo.TRACAO, "ABC1234", 60.0, LeituraPlaca.Origem.EMBARCADA));
        g.add(new LeituraPlaca(3, LeituraPlaca.Tipo.REBOQUE, "XYZ9876", 99.0, LeituraPlaca.Origem.EMBARCADA));
        r.gravadas.put("v1", g);
        new ProcessadorOcr(r).processar(Collections.singletonList(pp("v1", 10, HA_UMA_HORA, null, "ABC1234", null)),
            param("TABELA_LEITURA", 85), new HashMap<>(), AGORA);
        assertFalse(r.novas.containsKey("v1"));
        assertEquals(r.avaliacoes.get("v1").getMotivo(), RegraRevisao.Motivo.SCORE_BAIXO);
    }

    @Test
    public void falhaNumaPassagemNaoInterrompeOLote() {
        RepoFalso r = new RepoFalso() {
            @Override public List<LeituraPlaca> leiturasGravadas(String id) {
                if ("ruim".equals(id)) throw new IllegalStateException("simulada");
                return super.leiturasGravadas(id);
            }
        };
        ProcessadorOcr.Resumo res = new ProcessadorOcr(r).processar(
            Arrays.asList(pp("ruim", 10, HA_UMA_HORA, null, "ABC1234", null), pp("boa", 10, HA_UMA_HORA, null, "ABC1234", null)),
            param("AUSENTE", 85), new HashMap<>(), AGORA);
        assertEquals(res.getFalhas(), 1);
        assertEquals(res.getAvaliadas(), 1);
    }
}
