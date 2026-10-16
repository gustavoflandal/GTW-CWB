package muralha.digital.logtecnico;

import java.sql.Timestamp;

/**
 * Registro normalizado do repositório de logs técnicos:
 * data_hora, id_local, origem, categoria, severidade, descrição, usuário, id_referencia.
 * {@code transicao}/{@code grupo} marcam eventos que abrem ou fecham um período
 * (ex.: queda de energia → retorno); {@code dataHoraFim} é preenchida quando a própria
 * fonte já traz o fim (ex.: manutenção com data de conclusão).
 */
public final class RegistroLogTecnico {

    public enum Transicao { INICIO, FIM }

    private final Timestamp dataHora;
    private final Timestamp dataHoraFim;
    private final Integer idLocal;
    private final String origem;
    private final CategoriaLog categoria;
    private final SeveridadeLog severidade;
    private final String descricao;
    private final String usuario;
    private final String idReferencia;
    private final Transicao transicao;
    private final String grupo;

    public RegistroLogTecnico(Timestamp dataHora, Timestamp dataHoraFim, Integer idLocal, String origem,
            CategoriaLog categoria, SeveridadeLog severidade, String descricao, String usuario,
            String idReferencia, Transicao transicao, String grupo) {
        this.dataHora = dataHora;
        this.dataHoraFim = dataHoraFim;
        this.idLocal = idLocal;
        this.origem = origem;
        this.categoria = categoria;
        this.severidade = severidade;
        this.descricao = descricao;
        this.usuario = usuario;
        this.idReferencia = idReferencia;
        this.transicao = transicao;
        this.grupo = grupo;
    }

    /** Registro pontual, sem transição. */
    public static RegistroLogTecnico de(Timestamp dataHora, Integer idLocal, String origem, CategoriaLog categoria,
            SeveridadeLog severidade, String descricao, String usuario, String idReferencia) {
        return new RegistroLogTecnico(dataHora, null, idLocal, origem, categoria, severidade, descricao,
            usuario, idReferencia, null, null);
    }

    public Timestamp getDataHora() { return dataHora; }

    public Timestamp getDataHoraFim() { return dataHoraFim; }

    public Integer getIdLocal() { return idLocal; }

    /** Fonte do registro (ex.: CAPTURA, STATUS_CONEXAO, AUDITORIA). */
    public String getOrigem() { return origem; }

    public CategoriaLog getCategoria() { return categoria; }

    public SeveridadeLog getSeveridade() { return severidade; }

    public String getDescricao() { return descricao; }

    public String getUsuario() { return usuario; }

    /** Chave do registro na tabela de origem. */
    public String getIdReferencia() { return idReferencia; }

    public Transicao getTransicao() { return transicao; }

    public String getGrupo() { return grupo; }
}
