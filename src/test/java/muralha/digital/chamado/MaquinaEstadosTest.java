package muralha.digital.chamado;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static muralha.digital.chamado.AcaoChamado.*;
import static muralha.digital.chamado.StatusChamado.*;
import static org.testng.Assert.*;

public class MaquinaEstadosTest {

    private static final Set<PerfilChamado> SOL = EnumSet.of(PerfilChamado.SOLICITANTE);
    private static final Set<PerfilChamado> GES = EnumSet.of(PerfilChamado.GESTOR);
    private static final Set<PerfilChamado> EQU = EnumSet.of(PerfilChamado.EQUIPE);

    @DataProvider
    public Object[][] permitidas() {
        return new Object[][] {
            // atual, acao, perfis, autor, destino esperado
            {ABERTO, ASSUMIR, EQU, false, ABERTO},
            {EM_ATENDIMENTO, ASSUMIR, EQU, false, EM_ATENDIMENTO},
            {ABERTO, INICIAR, EQU, false, EM_ATENDIMENTO},
            {EM_ATENDIMENTO, AGUARDAR, EQU, false, AGUARDANDO_SOLICITANTE},
            {AGUARDANDO_SOLICITANTE, RESPONDER, SOL, true, EM_ATENDIMENTO},
            {AGUARDANDO_SOLICITANTE, RESPONDER, GES, false, EM_ATENDIMENTO},
            {EM_ATENDIMENTO, RESOLVER, EQU, false, RESOLVIDO},
            {RESOLVIDO, AVALIAR, SOL, true, FECHADO},
            {RESOLVIDO, REABRIR, SOL, true, EM_ATENDIMENTO},
            {ABERTO, CANCELAR, SOL, true, CANCELADO},
            {ABERTO, CANCELAR, EQU, false, CANCELADO},
            {EM_ATENDIMENTO, CANCELAR, EQU, false, CANCELADO},
            {AGUARDANDO_SOLICITANTE, CANCELAR, EQU, false, CANCELADO},
            {ABERTO, RECLASSIFICAR, EQU, false, ABERTO},
            {RESOLVIDO, RECLASSIFICAR, EQU, false, RESOLVIDO},
        };
    }

    @Test(dataProvider = "permitidas")
    public void transicaoPermitida(StatusChamado atual, AcaoChamado acao, Set<PerfilChamado> perfis,
                                   boolean autor, StatusChamado esperado) {
        assertEquals(MaquinaEstados.destino(atual, acao, perfis, autor), esperado);
    }

    @DataProvider
    public Object[][] negadas() {
        return new Object[][] {
            {ABERTO, INICIAR, SOL, true},                    // solicitante nao inicia atendimento
            {ABERTO, INICIAR, GES, false},                   // gestor da CONTRATANTE nao atende
            {EM_ATENDIMENTO, CANCELAR, SOL, true},           // autor so cancela enquanto ABERTO
            {RESOLVIDO, AVALIAR, GES, false},                // gestor nao avalia chamado de outro
            {RESOLVIDO, AVALIAR, EQU, false},                // equipe nao se autoavalia
            {RESOLVIDO, REABRIR, EQU, false},
            {AGUARDANDO_SOLICITANTE, RESPONDER, SOL, false}, // solicitante que nao e o autor
            {FECHADO, RECLASSIFICAR, EQU, false},            // terminal
            {CANCELADO, CANCELAR, EQU, false},
            {FECHADO, REABRIR, SOL, true},
            {EM_ATENDIMENTO, RESOLVER, SOL, true},
            {ABERTO, AGUARDAR, EQU, false},
        };
    }

    @Test(dataProvider = "negadas", expectedExceptions = RegraChamadoException.class)
    public void transicaoNegada(StatusChamado atual, AcaoChamado acao, Set<PerfilChamado> perfis, boolean autor) {
        MaquinaEstados.destino(atual, acao, perfis, autor);
    }

    @Test
    public void permitidasParaSolicitanteAutorEmResolvido() {
        List<AcaoChamado> acoes = MaquinaEstados.permitidas(RESOLVIDO, SOL, true);
        assertEquals(acoes, List.of(AVALIAR, REABRIR));
    }

    @Test
    public void uniaoDePerfisSomaPermissoes() {
        Set<PerfilChamado> ambos = EnumSet.of(PerfilChamado.SOLICITANTE, PerfilChamado.EQUIPE);
        assertEquals(MaquinaEstados.destino(ABERTO, INICIAR, ambos, true), EM_ATENDIMENTO);
    }

    @Test
    public void veTodosSomenteGestorOuEquipe() {
        assertFalse(PerfilChamado.veTodos(SOL));
        assertTrue(PerfilChamado.veTodos(GES));
        assertTrue(PerfilChamado.veTodos(EQU));
    }

    @Test
    public void adminVeTodosMasNaoExecutaAcoes() {
        Set<PerfilChamado> adm = EnumSet.of(PerfilChamado.ADMIN);
        assertTrue(PerfilChamado.veTodos(adm));
        for (StatusChamado s : StatusChamado.values()) {
            assertTrue(MaquinaEstados.permitidas(s, adm, false).isEmpty(), "ADMIN sozinho nao age em " + s);
        }
    }

    @Test
    public void codigoDaAcao() {
        assertEquals(AcaoChamado.deCodigo("reclassificar"), RECLASSIFICAR);
        assertNull(AcaoChamado.deCodigo("abrir"));
        assertFalse(ASSUMIR.exigeTexto());
        assertFalse(AVALIAR.exigeTexto());
        assertTrue(RESOLVER.exigeTexto());
    }
}
