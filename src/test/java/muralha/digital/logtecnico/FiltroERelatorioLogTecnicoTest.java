package muralha.digital.logtecnico;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class FiltroERelatorioLogTecnicoTest {

    @Test
    public void filtroLeParametrosDaTela() {
        FiltroLogTecnico f = FiltroLogTecnico.de("2026-10-01", "2026-10-07", "1, 2,1", "5343", "energia,COMUNICACAO", "alerta", 31);
        assertEquals(f.getInicio(), Timestamp.valueOf("2026-10-01 00:00:00"));
        assertEquals(f.getFim(), Timestamp.valueOf("2026-10-08 00:00:00"));
        assertEquals(new ArrayList<>(f.getIdLocais()), Arrays.asList(1, 2));
        assertTrue(f.restringeEquipamento());
        assertTrue(f.aceita(CategoriaLog.ENERGIA));
        assertFalse(f.aceita(CategoriaLog.SENSOR));
        assertTrue(f.aceita(SeveridadeLog.ALERTA));
        assertFalse(f.aceita(SeveridadeLog.INFO));
    }

    @Test
    public void filtroVazioAceitaTudo() {
        FiltroLogTecnico f = FiltroLogTecnico.de("2026-10-01", "2026-10-01", "", null, null, " ", 31);
        assertFalse(f.restringeEquipamento());
        assertTrue(f.aceita(CategoriaLog.USUARIO));
        assertTrue(f.aceita(SeveridadeLog.DEBUG));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*31 dias.*")
    public void filtroRespeitaPeriodoMaximoParametrizado() {
        FiltroLogTecnico.de("2026-09-01", "2026-10-07", null, null, null, null, 31);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filtroRecusaCategoriaDesconhecida() {
        FiltroLogTecnico.de("2026-10-01", "2026-10-02", null, null, "REDE", null, 31);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filtroRecusaEquipamentoNaoNumerico() {
        FiltroLogTecnico.de("2026-10-01", "2026-10-02", "1;DROP", null, null, null, 31);
    }

    private static RegistroLogTecnico r(String hora, CategoriaLog c, SeveridadeLog s, RegistroLogTecnico.Transicao t, String g) {
        return new RegistroLogTecnico(Timestamp.valueOf("2026-10-16 " + hora), null, 1, "S", c, s, "d " + hora, null,
            "ref" + hora, t, g);
    }

    @Test
    public void relatorioContaPorCategoriaESomaDuracaoDentroDoPeriodo() {
        Timestamp ini = Timestamp.valueOf("2026-10-16 08:00:00"), fim = Timestamp.valueOf("2026-10-16 12:00:00");
        List<RegistroLogTecnico> regs = Arrays.asList(
            r("09:00:00", CategoriaLog.ENERGIA, SeveridadeLog.ALERTA, RegistroLogTecnico.Transicao.INICIO, "ENERGIA"),
            r("09:30:00", CategoriaLog.ENERGIA, SeveridadeLog.INFO, RegistroLogTecnico.Transicao.FIM, "ENERGIA"),
            r("11:00:00", CategoriaLog.ENERGIA, SeveridadeLog.ALERTA, RegistroLogTecnico.Transicao.INICIO, "ENERGIA"),
            r("10:00:00", CategoriaLog.HARDWARE, SeveridadeLog.ERRO, null, null),
            r("10:30:00", CategoriaLog.PARAMETRO, SeveridadeLog.INFO, null, null));
        RelatorioTecnico rel = RelatorioTecnico.montar(regs, ini, fim);
        assertEquals(rel.getTotal(), 5);
        assertEquals(rel.getContagem().get(CategoriaLog.ENERGIA).get(SeveridadeLog.ALERTA), Integer.valueOf(2));
        RelatorioTecnico.ResumoGrupo energia = rel.getGrupos().get("ENERGIA");
        assertEquals(energia.quantidade, 2);
        assertEquals(energia.emAberto, 1);
        assertEquals(energia.duracaoMs, (30 + 60) * 60_000L, "30 min fechados + 60 min em aberto até o fim do período");
        assertEquals(rel.getOcorrencias().size(), 1);
        assertEquals(rel.getParametros().size(), 1);
    }

    @Test
    public void ordenacaoEstavelPorDataEReferencia() {
        List<RegistroLogTecnico> l = new ArrayList<>(Arrays.asList(
            r("10:00:00", CategoriaLog.SENSOR, SeveridadeLog.INFO, null, null),
            r("11:00:00", CategoriaLog.SENSOR, SeveridadeLog.INFO, null, null),
            r("09:00:00", CategoriaLog.SENSOR, SeveridadeLog.INFO, null, null)));
        ServicoLogTecnico.ordenar(l, false);
        assertEquals(l.get(0).getIdReferencia(), "ref11:00:00");
        ServicoLogTecnico.ordenar(l, true);
        assertEquals(l.get(0).getIdReferencia(), "ref09:00:00");
    }

    @Test
    public void csvNeutralizaFormulasEEscapaSeparador() {
        assertEquals(LogTecnicoServlet.csv("=SOMA(A1)"), "'=SOMA(A1)");
        assertEquals(LogTecnicoServlet.csv("a;b"), "\"a;b\"");
        assertEquals(LogTecnicoServlet.csv(null), "");
    }
}
