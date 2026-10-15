package muralha.digital.chamado;

import muralha.digital.chamado.CalculadoraSla.Marca;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class ChamadoServiceRegrasTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 15, 8, 0);

    private static Chamado chamadoAlta() {
        Chamado c = new Chamado();
        c.status = StatusChamado.EM_ATENDIMENTO;
        c.dtAbertura = ABERTURA;
        c.minutosResolucaoPrioridade = 480;
        c.dtPrazoAtendimento = CalculadoraSla.prazoAtendimento(ABERTURA, 120);
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(ABERTURA, 480, 0);
        return c;
    }

    @Test
    public void encerrarPausaSomaMinutosEEstendePrazo() {
        Chamado c = chamadoAlta();
        c.dtInicioPausa = ABERTURA.plusHours(1);
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(3));
        assertEquals(c.minutosPausados, 120);
        assertNull(c.dtInicioPausa);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(480 + 120));
    }

    @Test
    public void encerrarPausaSemPausaNaoFazNada() {
        Chamado c = chamadoAlta();
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(3));
        assertEquals(c.minutosPausados, 0);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(480));
    }

    @Test
    public void reclassificacaoDepoisDePausaEReaberturaPreservaPausas() {
        Chamado c = chamadoAlta();
        c.dtInicioPausa = ABERTURA.plusHours(1);              // aguardando solicitante
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(2)); // +60
        c.dtInicioPausa = ABERTURA.plusHours(3);              // resolvido
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(5)); // reaberto: +120
        assertEquals(c.minutosPausados, 180);
        // reclassifica para Critica (240 min): prazo = abertura + 240 + 180
        c.minutosResolucaoPrioridade = 240;
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(c.dtAbertura, 240, c.minutosPausados);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(420));
    }

    @Test
    public void recalcularMarcasUsaPausaDoChamado() {
        Chamado c = chamadoAlta();
        c.slaResolucao = Marca.EM_RISCO;
        c.dtInicioPausa = ABERTURA.plusHours(1);
        ChamadoService.recalcularMarcas(c, ABERTURA.plusDays(2), 80);
        assertEquals(c.slaResolucao, Marca.EM_RISCO, "pausado: marca de resolucao nao evolui");
        assertEquals(c.slaAtendimento, Marca.VENCIDO, "atendimento nao pausa");
    }

    @Test
    public void recalcularMarcasCumpridoAoResolver() {
        Chamado c = chamadoAlta();
        c.dtPrimeiroAtendimento = ABERTURA.plusMinutes(30);
        c.dtResolucao = ABERTURA.plusHours(4);
        ChamadoService.recalcularMarcas(c, ABERTURA.plusHours(4), 80);
        assertEquals(c.slaAtendimento, Marca.CUMPRIDO);
        assertEquals(c.slaResolucao, Marca.CUMPRIDO);
    }
}
