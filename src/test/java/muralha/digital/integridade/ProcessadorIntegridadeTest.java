package muralha.digital.integridade;

import muralha.digital.util.HashUtil;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class ProcessadorIntegridadeTest {

    private static final long AGORA = 1_760_000_000_000L;
    private static final Timestamp HA_UMA_HORA = new Timestamp(AGORA - 3_600_000L);
    private static final Timestamp HA_UM_MINUTO = new Timestamp(AGORA - 60_000L);
    private static final byte[] IMG = "imagem".getBytes(StandardCharsets.UTF_8);

    /** Repositório em memória que registra o que o processador fez. */
    static class RepositorioFalso implements RepositorioIntegridade {
        final Map<String, String> payload = new HashMap<>();
        final Map<String, String> dadoBruto = new HashMap<>();
        final Map<String, StatusIntegridade> gravados = new HashMap<>();
        final List<String> alertas = new ArrayList<>();
        final List<String> auditorias = new ArrayList<>();
        final List<String> alertasExistentes = new ArrayList<>();

        @Override public String hashPayload(String idImagem) { return payload.get(idImagem); }
        @Override public String dadoBruto(String idCaptura) { return dadoBruto.get(idCaptura); }
        @Override public void gravar(ImagemPendente img, OrigemHash origem, ResultadoConferencia r) {
            gravados.put(img.getIdImagem(), r.getStatus());
        }
        @Override public boolean existeAlerta(String idVtr) { return alertasExistentes.contains(idVtr); }
        @Override public boolean gerarAlerta(ImagemPendente img, ResultadoConferencia r) {
            alertas.add(img.getIdVeiculoTempoReal());
            return true;
        }
        @Override public void auditar(ImagemPendente img, ResultadoConferencia r) { auditorias.add(img.getIdImagem()); }
    }

    private static ImagemPendente img(String id, String vtr, int indice, int local, Timestamp recepcao, String captura) {
        return new ImagemPendente(id, vtr, indice, local, recepcao, recepcao, captura, IMG);
    }

    private static ParametrosIntegridade parametros(OrigemHash padrao, boolean alerta) {
        return new ParametrosIntegridade(padrao, 200, 24, 10, alerta);
    }

    @Test
    public void hashIgualGravaIntegroSemAlerta() {
        RepositorioFalso repo = new RepositorioFalso();
        repo.payload.put("i1", HashUtil.sha256Hex(IMG));
        ProcessadorIntegridade.Resumo r = new ProcessadorIntegridade(repo).processar(
            Collections.singletonList(img("i1", "v1", 0, 10, HA_UMA_HORA, null)),
            parametros(OrigemHash.interpretar("PAYLOAD_SOAP"), true), new HashMap<>(), AGORA);

        assertEquals(repo.gravados.get("i1"), StatusIntegridade.INTEGRO);
        assertEquals(r.getQuantidade(StatusIntegridade.INTEGRO), 1);
        assertTrue(repo.alertas.isEmpty());
        assertTrue(repo.auditorias.isEmpty());
    }

    @Test
    public void divergenteGeraUmAlertaPorPassagemEAuditoriaPorImagem() {
        RepositorioFalso repo = new RepositorioFalso();
        repo.payload.put("i1", HashUtil.sha256Hex("outra".getBytes(StandardCharsets.UTF_8)));
        repo.payload.put("i2", HashUtil.sha256Hex("outra 2".getBytes(StandardCharsets.UTF_8)));
        List<ImagemPendente> lote = Arrays.asList(
            img("i1", "v1", 0, 10, HA_UMA_HORA, null),
            img("i2", "v1", 1, 10, HA_UMA_HORA, null));

        ProcessadorIntegridade.Resumo r = new ProcessadorIntegridade(repo).processar(
            lote, parametros(OrigemHash.interpretar("PAYLOAD_SOAP"), true), new HashMap<>(), AGORA);

        assertEquals(repo.gravados.get("i1"), StatusIntegridade.DIVERGENTE);
        assertEquals(repo.gravados.get("i2"), StatusIntegridade.DIVERGENTE);
        assertEquals(repo.alertas, Collections.singletonList("v1"));
        assertEquals(repo.auditorias, Arrays.asList("i1", "i2"));
        assertEquals(r.getAlertas(), 1);
    }

    @Test
    public void divergenteNaoDuplicaAlertaJaExistenteNemAlertaSeDesligado() {
        RepositorioFalso repo = new RepositorioFalso();
        repo.payload.put("i1", HashUtil.sha256Hex("outra".getBytes(StandardCharsets.UTF_8)));
        repo.alertasExistentes.add("v1");
        new ProcessadorIntegridade(repo).processar(Collections.singletonList(img("i1", "v1", 0, 10, HA_UMA_HORA, null)),
            parametros(OrigemHash.interpretar("PAYLOAD_SOAP"), true), new HashMap<>(), AGORA);
        assertTrue(repo.alertas.isEmpty());

        RepositorioFalso desligado = new RepositorioFalso();
        desligado.payload.put("i1", HashUtil.sha256Hex("outra".getBytes(StandardCharsets.UTF_8)));
        new ProcessadorIntegridade(desligado).processar(Collections.singletonList(img("i1", "v1", 0, 10, HA_UMA_HORA, null)),
            parametros(OrigemHash.interpretar("PAYLOAD_SOAP"), false), new HashMap<>(), AGORA);
        assertTrue(desligado.alertas.isEmpty());
        assertEquals(desligado.auditorias.size(), 1, "a auditoria é registrada mesmo com alerta desligado");
    }

    @Test
    public void origemAusenteGravaSemConferencia() {
        RepositorioFalso repo = new RepositorioFalso();
        new ProcessadorIntegridade(repo).processar(Collections.singletonList(img("i1", "v1", 0, 10, HA_UM_MINUTO, null)),
            parametros(OrigemHash.AUSENTE, true), new HashMap<>(), AGORA);
        assertEquals(repo.gravados.get("i1"), StatusIntegridade.SEM_CONFERENCIA);
    }

    @Test
    public void hashAindaNoPrazoFicaPendenteEDepoisViraSemConferencia() {
        RepositorioFalso repo = new RepositorioFalso();
        ProcessadorIntegridade.Resumo r = new ProcessadorIntegridade(repo).processar(
            Arrays.asList(img("novo", "v1", 0, 10, HA_UM_MINUTO, null), img("velho", "v2", 0, 10, HA_UMA_HORA, null)),
            parametros(OrigemHash.interpretar("PAYLOAD_SOAP"), true), new HashMap<>(), AGORA);

        assertFalse(repo.gravados.containsKey("novo"), "dentro da espera de 10 min não grava");
        assertEquals(r.getAguardandoHash(), 1);
        assertEquals(repo.gravados.get("velho"), StatusIntegridade.SEM_CONFERENCIA);
    }

    @Test
    public void configuracaoDoEquipamentoPrevaleceSobreOPadrao() {
        RepositorioFalso repo = new RepositorioFalso();
        repo.dadoBruto.put("c1", "{\"hashes\":[\"" + HashUtil.sha256Hex(IMG) + "\"]}");
        Map<Integer, OrigemHash> porEquipamento = new HashMap<>();
        porEquipamento.put(20, OrigemHash.interpretar("DADO_BRUTO:hashes"));

        new ProcessadorIntegridade(repo).processar(
            Arrays.asList(img("i1", "v1", 0, 20, HA_UMA_HORA, "c1"), img("i2", "v2", 0, 30, HA_UMA_HORA, "c2")),
            parametros(OrigemHash.AUSENTE, true), porEquipamento, AGORA);

        assertEquals(repo.gravados.get("i1"), StatusIntegridade.INTEGRO);
        assertEquals(repo.gravados.get("i2"), StatusIntegridade.SEM_CONFERENCIA);
    }

    @Test
    public void falhaEmUmaImagemNaoInterrompeOLote() {
        RepositorioFalso repo = new RepositorioFalso() {
            @Override public void gravar(ImagemPendente img, OrigemHash origem, ResultadoConferencia r) {
                if ("ruim".equals(img.getIdImagem())) throw new IllegalStateException("simulada");
                super.gravar(img, origem, r);
            }
        };
        ProcessadorIntegridade.Resumo r = new ProcessadorIntegridade(repo).processar(
            Arrays.asList(img("ruim", "v1", 0, 10, HA_UMA_HORA, null), img("boa", "v2", 0, 10, HA_UMA_HORA, null)),
            parametros(OrigemHash.AUSENTE, true), new HashMap<>(), AGORA);
        assertEquals(r.getFalhas(), 1);
        assertEquals(repo.gravados.get("boa"), StatusIntegridade.SEM_CONFERENCIA);
    }
}
