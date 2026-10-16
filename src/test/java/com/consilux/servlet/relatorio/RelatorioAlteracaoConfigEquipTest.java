package com.consilux.servlet.relatorio;

import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.*;

public class RelatorioAlteracaoConfigEquipTest {

    private static String fmt(Date d) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(d);
    }

    @Test
    public void periodoIncluiODiaFinalInteiro() {
        Date[] p = RelatorioAlteracaoConfigEquip.periodo("01/10/2026", "16/10/2026");
        assertNotNull(p);
        assertEquals(fmt(p[0]), "01/10/2026 00:00");
        assertEquals(fmt(p[1]), "17/10/2026 00:00");
    }

    @Test
    public void periodoInvalidoInvertidoOuLongoDemais() {
        assertNull(RelatorioAlteracaoConfigEquip.periodo("31/02/2026", "01/03/2026"));
        assertNull(RelatorioAlteracaoConfigEquip.periodo("16/10/2026", "01/10/2026"));
        assertNull(RelatorioAlteracaoConfigEquip.periodo("01/01/2025", "16/10/2026"));
        assertNull(RelatorioAlteracaoConfigEquip.periodo("", "16/10/2026"));
        assertNull(RelatorioAlteracaoConfigEquip.periodo(null, null));
    }

    @Test
    public void idLocalVazioSignificaTodos() {
        assertEquals(RelatorioAlteracaoConfigEquip.idLocal(null), Integer.valueOf(0));
        assertEquals(RelatorioAlteracaoConfigEquip.idLocal(" "), Integer.valueOf(0));
        assertEquals(RelatorioAlteracaoConfigEquip.idLocal("0"), Integer.valueOf(0));
        assertEquals(RelatorioAlteracaoConfigEquip.idLocal("123"), Integer.valueOf(123));
        assertNull(RelatorioAlteracaoConfigEquip.idLocal("12a"));
        assertNull(RelatorioAlteracaoConfigEquip.idLocal("1; DROP"));
    }

    @Test
    public void consultaParametrizadaEFiltradaPorGrupo() {
        String sql = RelatorioAlteracaoConfigEquip.SQL;
        assertTrue(sql.contains("e.id_grupo_equipamento = ?"));
        assertTrue(sql.contains("(? = 0 OR l.id_local = ?)"));
        assertEquals(sql.chars().filter(c -> c == '?').count(), 7);
    }
}
