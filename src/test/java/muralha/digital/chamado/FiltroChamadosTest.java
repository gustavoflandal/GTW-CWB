package muralha.digital.chamado;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class FiltroChamadosTest {

    @Test
    public void semFiltrosNaoRestringe() {
        List<Object> params = new ArrayList<>();
        assertEquals(new FiltroChamados().where(params), " WHERE 1=1");
        assertTrue(params.isEmpty());
    }

    @Test
    public void visibilidadeDoSolicitanteSempreAplicadaPrimeiro() {
        FiltroChamados f = new FiltroChamados();
        f.idSolicitante = 42;
        f.protocolo = "CH-2026";
        List<Object> params = new ArrayList<>();
        String w = f.where(params);
        assertTrue(w.startsWith(" WHERE 1=1 AND c.id_usuario_solicitante = ?"));
        assertEquals(params.get(0), 42);
    }

    @Test
    public void categoriaIncluiSubcategorias() {
        FiltroChamados f = new FiltroChamados();
        f.idCategoria = 7;
        List<Object> params = new ArrayList<>();
        assertTrue(f.where(params).contains("(c.id_categoria = ? OR cat.id_pai = ?)"));
        assertEquals(params, List.of(7, 7));
    }

    @Test
    public void protocoloSoAceitaLetrasNumerosEHifen() {
        FiltroChamados f = new FiltroChamados();
        f.protocolo = "CH-2026%' OR 1=1 --";
        List<Object> params = new ArrayList<>();
        f.where(params);
        assertEquals(params.get(0), "%CH-2026OR11--%");
    }

    @Test
    public void periodoFimEhExclusivoNoDiaSeguinte() {
        FiltroChamados f = new FiltroChamados();
        f.dtInicio = LocalDate.of(2026, 10, 1);
        f.dtFim = LocalDate.of(2026, 10, 31);
        List<Object> params = new ArrayList<>();
        String w = f.where(params);
        assertTrue(w.contains("c.dt_abertura >= ?") && w.contains("c.dt_abertura < ?"));
        assertEquals(params.get(0), Timestamp.valueOf("2026-10-01 00:00:00"));
        assertEquals(params.get(1), Timestamp.valueOf("2026-11-01 00:00:00"));
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void statusInvalidoERejeitado() {
        FiltroChamados f = new FiltroChamados();
        f.status = "QUALQUER'; DROP TABLE x";
        f.where(new ArrayList<>());
    }
}
