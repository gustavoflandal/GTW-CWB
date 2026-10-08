package muralha.digital.registroDeFato;

public class RegistroDeFatoDocumentoDTO {

    private Integer id;
    private String tipo;
    private String nome;
    private String detalhamento;
    private String conteudoBase64;
    private String status; // "novo", "removido", "editado", null

    public RegistroDeFatoDocumentoDTO() {
    }

    public RegistroDeFatoDocumentoDTO(Integer id, String tipo, String nome, String detalhamento, String conteudoBase64, String status) {
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
        this.detalhamento = detalhamento;
        this.conteudoBase64 = conteudoBase64;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDetalhamento() { return detalhamento; }
    public void setDetalhamento(String detalhamento) { this.detalhamento = detalhamento; }

    public String getConteudoBase64() { return conteudoBase64; }
    public void setConteudoBase64(String conteudoBase64) { this.conteudoBase64 = conteudoBase64; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
