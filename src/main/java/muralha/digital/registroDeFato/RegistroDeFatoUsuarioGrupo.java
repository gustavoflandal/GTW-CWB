package muralha.digital.registroDeFato;

import java.util.Objects;

public class RegistroDeFatoUsuarioGrupo {
    private int id;
    private long idRegistroFato;
    private Integer idUsuario;
    private Integer idGrupo;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }
    
    public boolean equalsConteudo(RegistroDeFatoUsuarioGrupo other) {
        if (other == null) {
            return false;
        }
        // A comparação mais importante e segura é pelo ID único do registro.
        return Objects.equals(this.getId(), other.getId());
    }
}
