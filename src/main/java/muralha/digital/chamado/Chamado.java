package muralha.digital.chamado;

import muralha.digital.chamado.CalculadoraSla.Marca;

import java.time.LocalDateTime;

/** Linha de muralha.chamado + dados de exibicao vindos de joins. */
public class Chamado {
    public long id;
    public String protocolo;
    public String titulo;
    public String descricao;
    public int idCategoria;
    public int idPrioridade;
    public int idPrioridadeSolicitada;
    public StatusChamado status;
    public String origem = "PORTAL";
    public Integer idLocal;
    public int idUsuarioSolicitante;
    public Integer idUsuarioResponsavel;
    public LocalDateTime dtAbertura;
    public LocalDateTime dtPrazoAtendimento;
    public LocalDateTime dtPrazoResolucao;
    public LocalDateTime dtPrimeiroAtendimento;
    public LocalDateTime dtResolucao;
    public LocalDateTime dtFechamento;
    public int minutosPausados;
    public LocalDateTime dtInicioPausa;
    public Marca slaAtendimento = Marca.NO_PRAZO;
    public Marca slaResolucao = Marca.NO_PRAZO;
    public Integer avaliacaoNota;
    public String avaliacaoComentario;
    public LocalDateTime dtAvaliacao;
    public boolean fechadoAutomaticamente;
    public LocalDateTime dtAtualizacao;

    // Somente leitura (joins)
    public String nomeCategoria;
    public String nomePrioridade;
    public String corPrioridade;
    public int minutosResolucaoPrioridade;
    public String nomePrioridadeSolicitada;
    public String nomeLocal;
    public String nomeSolicitante;
    public String emailSolicitante;
    public String nomeResponsavel;
}
