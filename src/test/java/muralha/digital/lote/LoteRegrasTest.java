package muralha.digital.lote;

import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class LoteRegrasTest {

    @Test
    public void aceitaListaComEspacosERepetidos() {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        List<UUID> ids = LoteRegras.idsValidos(" " + a + " ," + b + "," + a + ",");
        assertEquals(ids.size(), 2);
        assertEquals(ids.get(0), a);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Nenhuma infração.*")
    public void recusaListaVazia() { LoteRegras.idsValidos(" , "); }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Nenhuma infração.*")
    public void recusaNulo() { LoteRegras.idsValidos(null); }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*inválido.*")
    public void recusaUuidInvalido() { LoteRegras.idsValidos(UUID.randomUUID() + ",abc"); }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*500.*")
    public void recusaAcimaDoLimite() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LoteRegras.MAX_ITENS + 1; i++) sb.append(UUID.randomUUID()).append(',');
        LoteRegras.idsValidos(sb.toString());
    }
}
