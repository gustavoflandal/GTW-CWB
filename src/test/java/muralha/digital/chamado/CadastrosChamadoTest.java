package muralha.digital.chamado;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CadastrosChamadoTest {

    private static Prioridade valida() {
        Prioridade p = new Prioridade();
        p.codigo = "CRITICA";
        p.nome = "Crítica";
        p.ordem = 1;
        p.minutosAtendimento = 60;
        p.minutosResolucao = 240;
        p.cor = "#dc3545";
        return p;
    }

    @Test
    public void prioridadeValida() {
        valida().validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void resolucaoMenorQueAtendimento() {
        Prioridade p = valida();
        p.minutosResolucao = 30;
        p.validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void corInvalida() {
        Prioridade p = valida();
        p.cor = "red;background:url(x)";
        p.validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void codigoInvalido() {
        Prioridade p = valida();
        p.codigo = "crit ica";
        p.validar();
    }

    @Test
    public void parametrosSlaNasBordas() {
        ChamadoConfigDao.validarParametrosSla(1, 1);
        ChamadoConfigDao.validarParametrosSla(99, 60);
    }

    @DataProvider
    public Object[][] parametrosSlaInvalidos() {
        return new Object[][] {{0, 5}, {100, 5}, {80, 0}, {80, 61}, {-1, -1}};
    }

    @Test(dataProvider = "parametrosSlaInvalidos", expectedExceptions = RegraChamadoException.class)
    public void parametrosSlaForaDosLimites(int risco, int dias) {
        ChamadoConfigDao.validarParametrosSla(risco, dias);
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void categoriaSemNome() {
        Categoria c = new Categoria();
        c.nome = "  ";
        c.validar();
    }
}
