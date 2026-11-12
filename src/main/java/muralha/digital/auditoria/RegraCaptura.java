package muralha.digital.auditoria;

/**
 * Regra configurável de captura de log, espelhando dbo.sis_log_regra.
 *
 * A avaliação é feita por {@link MotorRegras}: as regras ativas são ordenadas
 * por {@code ordem} e a PRIMEIRA cujo padrão de rota (e método) casar com a
 * requisição decide se o evento é capturado e como.
 */
public class RegraCaptura {

    private int     id;
    private int     ordem;
    private String  descricao;
    private String  padraoRota;
    private String  metodo;            // GET/POST/... ou null = qualquer
    private boolean capturar;
    private String  nivel;             // AUTO | LEITURA | ALTERACAO | ACESSO | ERRO | EXPORTACAO
    private String  funcionalidade;    // sobrescreve a derivação pela rota (pode ser null)
    private boolean registrarParametros;
    private String  mascararCampos;    // nomes separados por vírgula (pode ser null)
    private boolean garantido;         // true = gravação síncrona (zero perda)
    private boolean ativo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPadraoRota() { return padraoRota; }
    public void setPadraoRota(String padraoRota) { this.padraoRota = padraoRota; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public boolean isCapturar() { return capturar; }
    public void setCapturar(boolean capturar) { this.capturar = capturar; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getFuncionalidade() { return funcionalidade; }
    public void setFuncionalidade(String funcionalidade) { this.funcionalidade = funcionalidade; }

    public boolean isRegistrarParametros() { return registrarParametros; }
    public void setRegistrarParametros(boolean registrarParametros) { this.registrarParametros = registrarParametros; }

    public String getMascararCampos() { return mascararCampos; }
    public void setMascararCampos(String mascararCampos) { this.mascararCampos = mascararCampos; }

    public boolean isGarantido() { return garantido; }
    public void setGarantido(boolean garantido) { this.garantido = garantido; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
