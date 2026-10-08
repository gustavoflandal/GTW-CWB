package muralha.digital.registroDeFato;

public class BoletimApreensao {
    private Integer id;
    private Integer idBoletim;
    private String tipo;
    private String descricao;

    public BoletimApreensao() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdBoletim() {
        return idBoletim;
    }

    public void setIdBoletim(Integer idBoletim) {
        this.idBoletim = idBoletim;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public boolean equalsConteudo(BoletimApreensao outro) {
        if (outro == null) return false;
        return equalsNullable(tipo, outro.tipo)
            && equalsNullable(descricao, outro.descricao);
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
