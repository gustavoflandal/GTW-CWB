package muralha.digital.integridade;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class OrigemHashTest {

    @Test
    public void interpretaOsTresFormatos() {
        assertEquals(OrigemHash.interpretar("PAYLOAD_SOAP").getTipo(), OrigemHash.Tipo.PAYLOAD_SOAP);
        assertSame(OrigemHash.interpretar("ausente"), OrigemHash.AUSENTE);
        OrigemHash d = OrigemHash.interpretar("DADO_BRUTO:integridade.sha256");
        assertEquals(d.getTipo(), OrigemHash.Tipo.DADO_BRUTO);
        assertEquals(d.getCampo(), "integridade.sha256");
        assertEquals(d.formatar(), "DADO_BRUTO:integridade.sha256");
    }

    @Test
    public void vazioOuNuloEhAusente() {
        assertSame(OrigemHash.interpretar(null), OrigemHash.AUSENTE);
        assertSame(OrigemHash.interpretar("  "), OrigemHash.AUSENTE);
    }

    @Test
    public void payloadIgnoraCampo() {
        assertNull(OrigemHash.de(OrigemHash.Tipo.PAYLOAD_SOAP, "qualquer").getCampo());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dadoBrutoExigeCampo() {
        OrigemHash.interpretar("DADO_BRUTO");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void campoNaoAceitaCaracteresDeInjecao() {
        OrigemHash.de(OrigemHash.Tipo.DADO_BRUTO, "hash'; DROP TABLE x--");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void tipoDesconhecidoEhRecusado() {
        OrigemHash.interpretar("FTP");
    }
}
