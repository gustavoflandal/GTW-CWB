package muralha.digital.registroDeFato;

public class RegistroDeFatoLinkDTO {

    private Integer id;
    private String url;
    private String detalhamento;
    private String status;       // "novo", "removido", "existente"

    public RegistroDeFatoLinkDTO() {
    }

    public RegistroDeFatoLinkDTO(Integer id, String url, String detalhamento, String status) {
        this.id = id;
        this.url = url;
        this.detalhamento = detalhamento;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
