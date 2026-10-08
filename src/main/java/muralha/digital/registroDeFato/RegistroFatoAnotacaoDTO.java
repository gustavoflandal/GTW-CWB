package muralha.digital.registroDeFato;

public class RegistroFatoAnotacaoDTO {

    private int id;            // 0 = novo, >0 = existente
    private String texto;
    private String usuario;
    private String dataIso;    // data/hora da anotação
    private Integer idRegistro;
    private String status;     // "novo", "existente", "removido"

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDataIso() {
        return dataIso;
    }

    public void setDataIso(String dataIso) {
        this.dataIso = dataIso;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
