package muralha.digital.logtecnico;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class CorrelacionadorIntervalosTest {

    private static Timestamp t(String hhmm) { return Timestamp.valueOf("2026-10-16 " + hhmm + ":00"); }

    private static RegistroLogTecnico ev(String hhmm, Integer local, String origem, RegistroLogTecnico.Transicao tr, String grupo) {
        return new RegistroLogTecnico(t(hhmm), null, local, origem, CategoriaLog.ENERGIA, SeveridadeLog.ALERTA,
            "evento " + hhmm, null, origem + hhmm, tr, grupo);
    }

    private static final RegistroLogTecnico.Transicao INI = RegistroLogTecnico.Transicao.INICIO;
    private static final RegistroLogTecnico.Transicao FIM = RegistroLogTecnico.Transicao.FIM;

    @Test
    public void quedaERetornoDeEnergiaFormamUmPeriodo() {
        List<CorrelacionadorIntervalos.Intervalo> r = CorrelacionadorIntervalos.montar(Arrays.asList(
            ev("10:30", 1, "STATUS_ENERGIA", FIM, "ENERGIA"), ev("10:00", 1, "STATUS_ENERGIA", INI, "ENERGIA")));
        assertEquals(r.size(), 1);
        assertEquals(r.get(0).getInicio(), t("10:00"));
        assertEquals(r.get(0).getFim(), t("10:30"));
        assertEquals(r.get(0).duracaoMs(null), 30 * 60_000L);
        assertEquals(r.get(0).getDescricaoFim(), "evento 10:30");
    }

    @Test
    public void segundoInicioEhIgnoradoEFimSemInicioTambem() {
        List<CorrelacionadorIntervalos.Intervalo> r = CorrelacionadorIntervalos.montar(Arrays.asList(
            ev("09:00", 1, "S", FIM, "ENERGIA"),
            ev("10:00", 1, "S", INI, "ENERGIA"), ev("10:10", 1, "S", INI, "ENERGIA"), ev("10:20", 1, "S", FIM, "ENERGIA")));
        assertEquals(r.size(), 1);
        assertEquals(r.get(0).getInicio(), t("10:00"));
    }

    @Test
    public void periodoSemFimFicaEmAberto() {
        List<CorrelacionadorIntervalos.Intervalo> r = CorrelacionadorIntervalos.montar(Arrays.asList(ev("11:00", 1, "S", INI, "COMUNICACAO")));
        assertNull(r.get(0).getFim());
        assertEquals(r.get(0).duracaoMs(t("11:15")), 15 * 60_000L);
    }

    @Test
    public void naoMisturaEquipamentosNemFontes() {
        List<CorrelacionadorIntervalos.Intervalo> r = CorrelacionadorIntervalos.montar(Arrays.asList(
            ev("10:00", 1, "STATUS_ENERGIA", INI, "ENERGIA"), ev("10:05", 2, "STATUS_ENERGIA", FIM, "ENERGIA"),
            ev("10:06", 1, "CAPTURA", FIM, "ENERGIA")));
        assertEquals(r.size(), 1);
        assertNull(r.get(0).getFim(), "fim de outro equipamento ou outra fonte não fecha o período");
    }

    @Test
    public void manutencaoComConclusaoEhPeriodoDireto() {
        RegistroLogTecnico m = new RegistroLogTecnico(t("08:00"), t("09:30"), 5, "MANUTENCAO", CategoriaLog.INTERVENCAO,
            SeveridadeLog.INFO, "Manutenção", null, "manutencao:1", null, "MANUTENCAO");
        RegistroLogTecnico aberta = new RegistroLogTecnico(t("12:00"), null, 5, "MANUTENCAO", CategoriaLog.INTERVENCAO,
            SeveridadeLog.INFO, "Manutenção", null, "manutencao:2", null, "MANUTENCAO");
        List<CorrelacionadorIntervalos.Intervalo> r = CorrelacionadorIntervalos.montar(Arrays.asList(aberta, m));
        assertEquals(r.size(), 2);
        assertEquals(r.get(0).getFim(), t("09:30"));
        assertNull(r.get(1).getFim());
    }

    @Test
    public void eventoSemTransicaoNaoGeraPeriodo() {
        assertTrue(CorrelacionadorIntervalos.montar(Arrays.asList(ev("10:00", 1, "S", null, null))).isEmpty());
    }
}
