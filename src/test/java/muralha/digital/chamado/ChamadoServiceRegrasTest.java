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

    @Test
    public void resolverReabrirResolverRecalculaMarca() {
        Chamado c = chamadoAlta();                                   // prazo 16:00
        c.dtResolucao = ABERTURA.plusHours(2);                       // resolve 10:00
        c.dtInicioPausa = c.dtResolucao;
        ChamadoService.recalcularMarcas(c, c.dtResolucao, 80);
        assertEquals(c.slaResolucao, Marca.CUMPRIDO);

        LocalDateTime reabertura = ABERTURA.plusHours(3);            // reabre 11:00 (+60 min de pausa)
        ChamadoService.encerrarPausa(c, reabertura);
        c.dtResolucao = null;
        ChamadoService.recalcularMarcas(c, reabertura, 80);
        assertEquals(c.slaResolucao, Marca.NO_PRAZO, "reabrir limpa a marca definitiva");
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(540));

        c.dtResolucao = ABERTURA.plusHours(10);                      // resolve 18:00 (> 17:00)
        c.dtInicioPausa = c.dtResolucao;
        ChamadoService.recalcularMarcas(c, c.dtResolucao, 80);
        assertEquals(c.slaResolucao, Marca.VIOLADO);
    }

    @Test
    public void marcaDefinitivaNaoMudaAoReclassificar() {
        Chamado c = chamadoAlta();
        c.dtResolucao = ABERTURA.plusHours(10);                      // viola o prazo de 8h
        ChamadoService.recalcularMarcas(c, c.dtResolucao, 80);
        assertEquals(c.slaResolucao, Marca.VIOLADO);
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(ABERTURA, 4320, 0); // reclassifica para prazo maior
        ChamadoService.recalcularMarcas(c, c.dtResolucao.plusHours(1), 80);
        assertEquals(c.slaResolucao, Marca.VIOLADO);
    }

    @Test
    public void sairDaPausaNaoPuxaPrazoDaConfiguracaoAtual() {
        Chamado c = chamadoAlta();                                   // prazo gravado = abertura + 480
        c.dtInicioPausa = ABERTURA.plusHours(1);
        c.minutosResolucaoPrioridade = 240;                          // configuracao editada durante a pausa
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(3));
        assertEquals(c.minutosPausados, 120);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(480 + 120));
    }

    @Test
    public void conferirVersao() {
        LocalDateTime v = LocalDateTime.of(2026, 10, 15, 9, 0, 0, 123_000_000);
        Chamado c = new Chamado();
        c.dtAtualizacao = v;
        ChamadoService.conferirVersao(c, v);
        ChamadoService.conferirVersao(c, null);
        try {
            ChamadoService.conferirVersao(c, v.plusNanos(1_000_000));
            fail("deveria lancar");
        } catch (RegraChamadoException esperada) {
            assertTrue(esperada.getMessage().contains("alterado por outro usuário"));
        }
    }
}
