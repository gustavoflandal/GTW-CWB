package muralha.digital.logtecnico;

import org.testng.annotations.Test;

import java.util.EnumSet;

import static org.testng.Assert.*;

public class ClassificacaoCsxTest {

    @Test
    public void eventoTemPrioridadeSobreCategoria() {
        assertEquals(ClassificacaoCsx.classificar(1, 14).categoria, CategoriaLog.SINCRONISMO);
        assertEquals(ClassificacaoCsx.classificar(1, 50).categoria, CategoriaLog.SOFTWARE);
        assertEquals(ClassificacaoCsx.classificar(3, 25).categoria, CategoriaLog.HARDWARE);
        assertEquals(ClassificacaoCsx.classificar(5, 24).categoria, CategoriaLog.SENSOR);
    }

    @Test
    public void transicoesDeEnergiaComunicacaoManutencao() {
        assertEquals(ClassificacaoCsx.classificar(3, 8).transicao, RegistroLogTecnico.Transicao.INICIO);
        assertEquals(ClassificacaoCsx.classificar(3, 7).transicao, RegistroLogTecnico.Transicao.FIM);
        assertEquals(ClassificacaoCsx.classificar(4, 10).grupo, "CONEXAO_SERVIDOR");
        assertEquals(ClassificacaoCsx.classificar(4, 9).transicao, RegistroLogTecnico.Transicao.FIM);
        ClassificacaoCsx.Classe m = ClassificacaoCsx.classificar(2, 15);
        assertEquals(m.categoria, CategoriaLog.INTERVENCAO);
        assertEquals(m.grupo, "MANUTENCAO");
        assertEquals(ClassificacaoCsx.classificar(10005, 1).transicao, RegistroLogTecnico.Transicao.INICIO);
        assertEquals(ClassificacaoCsx.classificar(10006, 1).grupo, "ENERGIA");
    }

    @Test
    public void categoriaDesconhecidaViraSoftware() {
        ClassificacaoCsx.Classe c = ClassificacaoCsx.classificar(99999, 1);
        assertEquals(c.categoria, CategoriaLog.SOFTWARE);
        assertNull(c.transicao);
    }

    @Test
    public void predicadoNuloQuandoTodasAsCategorias() {
        assertNull(ClassificacaoCsx.predicado("e", EnumSet.noneOf(CategoriaLog.class)));
        assertNull(ClassificacaoCsx.predicado("e", EnumSet.allOf(CategoriaLog.class)));
    }

    @Test
    public void predicadoDeHardwareExcluiOsEventosReclassificados() {
        String p = ClassificacaoCsx.predicado("e", EnumSet.of(CategoriaLog.HARDWARE));
        assertTrue(p.contains("(e.id_categoria = 3 AND e.id_evento IN (17,18,43,44))"), p);
        assertTrue(p.contains("(e.id_categoria = 3 AND e.id_evento NOT IN (6,7,8,17,18,43,44))"), p);
        assertFalse(p.contains("e.id_categoria NOT IN"), "SOFTWARE não foi pedido");
    }

    @Test
    public void predicadoDeSoftwareIncluiCategoriasNaoMapeadas() {
        String p = ClassificacaoCsx.predicado("e", EnumSet.of(CategoriaLog.SOFTWARE));
        assertTrue(p.contains("e.id_categoria NOT IN ("), p);
        assertTrue(p.contains("(e.id_categoria = 1 AND e.id_evento NOT IN (14,38,40,41,42))"), p);
    }

    @Test
    public void predicadoSemEventoPossivelNaoQuebraSql() {
        String p = ClassificacaoCsx.predicado("e", EnumSet.of(CategoriaLog.PROCESSAMENTO));
        assertEquals(p, "(e.id_categoria = 10014)");
    }

    @Test
    public void niveisDoCaptura() {
        assertEquals(SeveridadeLog.doNivelCsx(0), SeveridadeLog.INFO);
        assertEquals(SeveridadeLog.doNivelCsx(3), SeveridadeLog.CRITICO);
        assertEquals(SeveridadeLog.doNivelCsx(4), SeveridadeLog.DEBUG);
        assertEquals(SeveridadeLog.niveisCsx(EnumSet.of(SeveridadeLog.INFO, SeveridadeLog.DEBUG)), java.util.Arrays.asList(0, 4));
    }
}
