package muralha.digital.chamado;

import muralha.digital.chamado.CalculadoraSla.Marca;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class CalculadoraSlaTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 15, 8, 0);

    @Test
    public void prazosDaPrioridadeCritica() {
        assertEquals(CalculadoraSla.prazoAtendimento(ABERTURA, 60), LocalDateTime.of(2026, 10, 15, 9, 0));
        assertEquals(CalculadoraSla.prazoResolucao(ABERTURA, 240, 0), LocalDateTime.of(2026, 10, 15, 12, 0));
    }

    @Test
    public void prazoDeResolucaoSomaMinutosPausados() {
        assertEquals(CalculadoraSla.prazoResolucao(ABERTURA, 240, 90), LocalDateTime.of(2026, 10, 15, 13, 30));
    }

    @Test
    public void prazoAtravessaDiasCorridos() {
        // 24x7: Baixa = 4320 min = 3 dias corridos, sem pular fim de semana
        assertEquals(CalculadoraSla.prazoResolucao(ABERTURA, 4320, 0), LocalDateTime.of(2026, 10, 18, 8, 0));
    }

    @Test
    public void minutosEntreNuncaNegativo() {
        assertEquals(CalculadoraSla.minutosEntre(ABERTURA, ABERTURA.plusMinutes(95)), 95);
        assertEquals(CalculadoraSla.minutosEntre(ABERTURA, ABERTURA.minusMinutes(5)), 0);
    }

    @Test
    public void marcaNoPrazoAntesDoLimiarDeRisco() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(79), 80, false, null), Marca.NO_PRAZO);
    }

    @Test
    public void marcaEmRiscoNoLimiar() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(80), 80, false, Marca.NO_PRAZO), Marca.EM_RISCO);
    }

    @Test
    public void marcaVencidoAposPrazo() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(101), 80, false, Marca.EM_RISCO), Marca.VENCIDO);
    }

    @Test
    public void cumpridoNoLimiteExatoEVioladoDepois() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, prazo, prazo.plusDays(1), 80, false, Marca.VENCIDO), Marca.CUMPRIDO);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, prazo.plusSeconds(1), prazo, 80, false, Marca.VENCIDO), Marca.VIOLADO);
    }

    @Test
    public void pausadoMantemMarcaAtual() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(500), 80, true, Marca.EM_RISCO), Marca.EM_RISCO);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(500), 80, true, null), Marca.NO_PRAZO);
    }

    @Test
    public void violadoPermaneceVioladoMesmoComPrazoNovoMaior() {
        LocalDateTime prazoNovo = ABERTURA.plusMinutes(1000);
        LocalDateTime cumpridoEm = ABERTURA.plusMinutes(200);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazoNovo, cumpridoEm, cumpridoEm.plusDays(1), 80, false, Marca.VIOLADO), Marca.VIOLADO);
    }

    @Test
    public void cumpridoPermaneceCumpridoMesmoComPrazoNovoMenor() {
        LocalDateTime prazoNovo = ABERTURA.plusMinutes(50);
        LocalDateTime cumpridoEm = ABERTURA.plusMinutes(200);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazoNovo, cumpridoEm, cumpridoEm.plusDays(1), 80, false, Marca.CUMPRIDO), Marca.CUMPRIDO);
    }

    @Test
    public void primeiraMarcacaoDecidePeloPrazo() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        for (Marca atual : new Marca[]{null, Marca.NO_PRAZO, Marca.EM_RISCO, Marca.VENCIDO}) {
            assertEquals(CalculadoraSla.marca(ABERTURA, prazo, prazo, prazo, 80, false, atual), Marca.CUMPRIDO);
            assertEquals(CalculadoraSla.marca(ABERTURA, prazo, prazo.plusMinutes(1), prazo, 80, false, atual), Marca.VIOLADO);
        }
    }
}
