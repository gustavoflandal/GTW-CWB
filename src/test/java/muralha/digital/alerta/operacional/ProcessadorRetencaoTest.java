package muralha.digital.alerta.operacional;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.*;

public class ProcessadorRetencaoTest {

    static class RepoFalso implements RepositorioRetencao {
        final Map<Integer, List<RegraRetencao.DadoHora>> dados = new HashMap<>();
        final Map<Integer, Boolean> recentes = new HashMap<>();
        final Map<Integer, UUID> alertas = new HashMap<>();

        @Override
        public List<Integer> equipamentosComDados(int janelaHoras, int lote) {
            return new java.util.ArrayList<>(dados.keySet());
        }

        @Override
        public List<RegraRetencao.DadoHora> dadosPorEquipamento(int idLocal, int janelaHoras) {
            return dados.getOrDefault(idLocal, Collections.emptyList());
        }

        @Override
        public boolean alertaRecente(int idLocal, int esperaMinutos) {
            return recentes.getOrDefault(idLocal, false);
        }

        @Override
        public UUID gravarAlerta(int idLocal, RegraRetencao.Avaliacao avaliacao, UUID idTipo) {
            UUID id = UUID.randomUUID();
            alertas.put(idLocal, id);
            return id;
        }
    }

    private static RegraRetencao.DadoHora dado(int local, int hora, double vel, int ocup) {
        return new RegraRetencao.DadoHora(local, hora, vel, ocup, 50);
    }

    private static ParametrosRetencao params() {
        return new ParametrosRetencao(500, 15.0, 1, 3, 100, 60);
    }

    @Test
    public void geraAlertaParaEquipamentoEmRetencao() {
        RepoFalso repo = new RepoFalso();
        repo.dados.put(10, Arrays.asList(dado(10, 10, 8.0, 600), dado(10, 11, 10.0, 700)));
        ProcessadorRetencao.Resumo r = new ProcessadorRetencao(repo).processar(params());
        assertEquals(r.getVerificados(), 1);
        assertEquals(r.getAlertasGerados(), 1);
        assertTrue(repo.alertas.containsKey(10));
    }

    @Test
    public void naoGeraAlertaSeJaExisteRecente() {
        RepoFalso repo = new RepoFalso();
        repo.dados.put(10, Arrays.asList(dado(10, 10, 8.0, 600), dado(10, 11, 10.0, 700)));
        repo.recentes.put(10, true);
        ProcessadorRetencao.Resumo r = new ProcessadorRetencao(repo).processar(params());
        assertEquals(r.getJaAlertados(), 1);
        assertEquals(r.getAlertasGerados(), 0);
    }

    @Test
    public void naoGeraAlertaSemRetencao() {
        RepoFalso repo = new RepoFalso();
        repo.dados.put(10, Arrays.asList(dado(10, 10, 30.0, 200)));
        ProcessadorRetencao.Resumo r = new ProcessadorRetencao(repo).processar(params());
        assertEquals(r.getVerificados(), 1);
        assertEquals(r.getAlertasGerados(), 0);
    }

    @Test
    public void falhaNumEquipamentoNaoParaOLote() {
        RepoFalso repo = new RepoFalso() {
            @Override
            public List<RegraRetencao.DadoHora> dadosPorEquipamento(int idLocal, int janelaHoras) {
                if (idLocal == 99) throw new RuntimeException("simulada");
                return super.dadosPorEquipamento(idLocal, janelaHoras);
            }
        };
        repo.dados.put(99, Collections.emptyList());
        repo.dados.put(10, Arrays.asList(dado(10, 10, 8.0, 600)));
        ProcessadorRetencao.Resumo r = new ProcessadorRetencao(repo).processar(params());
        assertEquals(r.getFalhas(), 1);
        assertEquals(r.getAlertasGerados(), 1);
    }
}
