package muralha.digital.chamado;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ExigeNovaSenhaTest {

    private static boolean exige(String hostN, String portaN, String userN, boolean gravada, String senha, boolean limpar) {
        return ChamadoConfigDao.exigeNovaSenha("smtp.a.com", "587", "u", hostN, portaN, userN, gravada, senha, limpar);
    }

    @Test
    public void mudarHostPortaOuUsuarioComSenhaGravadaESemNovaExige() {
        assertTrue(exige("smtp.b.com", "587", "u", true, "", false));
        assertTrue(exige("smtp.a.com", "25", "u", true, null, false));
        assertTrue(exige("smtp.a.com", "587", "outro", true, "", false));
    }

    @Test
    public void nadaMudouNaoExige() {
        assertFalse(exige("smtp.a.com", "587", "u", true, "", false));
    }

    @Test
    public void senhaNovaInformadaNaoExige() {
        assertFalse(exige("smtp.b.com", "25", "x", true, "nova", false));
    }

    @Test
    public void limparSenhaNaoExige() {
        assertFalse(exige("smtp.b.com", "25", "x", true, "", true));
    }

    @Test
    public void semSenhaGravadaNaoExige() {
        assertFalse(exige("smtp.b.com", "25", "x", false, "", false));
    }
}
