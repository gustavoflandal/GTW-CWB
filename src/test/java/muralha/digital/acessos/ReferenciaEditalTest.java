package muralha.digital.acessos;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ReferenciaEditalTest {

    @Test
    public void todasAsFuncionalidadesNovasDoMenuTemItensDoEdital() {
        for (int idMenu = 366; idMenu <= 378; idMenu++) {
            assertFalse(ReferenciaEdital.itens(idMenu).isEmpty(), "id_menu " + idMenu + " sem referencia ao edital");
        }
    }

    @Test
    public void menusDoSistemaDeChamadosTemItensDoCapitulo7() {
        for (int idMenu = 379; idMenu <= 382; idMenu++) {
            assertFalse(ReferenciaEdital.itens(idMenu).isEmpty(), "id_menu " + idMenu + " sem referencia ao edital");
        }
        assertTrue(ReferenciaEdital.itens(379).contains("7.2.4"));
        assertTrue(ReferenciaEdital.itens(381).contains("7.2.3.d"));
    }

    @Test
    public void menuLegadoNaoTemReferencia() {
        assertTrue(ReferenciaEdital.itens(1).isEmpty());
        assertEquals(ReferenciaEdital.titulo(1), "");
        Menu m = new Menu();
        m.setIdMenu(1);
        assertFalse(m.isAtendeEdital());
    }

    @Test
    public void tituloListaUmItemPorLinha() {
        assertEquals(ReferenciaEdital.titulo(368), "Itens do edital (TR TRANSALVADOR):<br>5.2.2.3<br>5.2.2.6");
        Menu m = new Menu();
        m.setIdMenu(368);
        assertTrue(m.isAtendeEdital());
        assertEquals(m.getItensEditalTexto(), "5.2.2.3, 5.2.2.6");
    }
}
