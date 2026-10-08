package muralha.digital.registroDeFato;

public class RegistroDeFatoLink {

    private Integer id;
    private long idRegistroFato;
    private String url;
    private String detalhamento;

    public RegistroDeFatoLink() {
    }

    public RegistroDeFatoLink(Integer id, long idRegistroFato, String url, String detalhamento) {
        this.id = id;
        this.idRegistroFato = idRegistroFato;
        this.url = url;
        this.detalhamento = detalhamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
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
    
    public boolean equalsConteudo(RegistroDeFatoLink outro) {
        if (outro == null) return false;
        return equalsNullable(url, outro.url)
            && equalsNullable(detalhamento, outro.detalhamento);
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
