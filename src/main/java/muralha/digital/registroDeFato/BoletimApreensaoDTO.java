package muralha.digital.registroDeFato;

public class BoletimApreensaoDTO {
	private Integer id;
    private String tipo;
    private String descricao;
    private String status; // novo campo para controle do CRUD

    public BoletimApreensaoDTO() {
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
