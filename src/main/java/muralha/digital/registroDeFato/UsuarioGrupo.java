package muralha.digital.registroDeFato;

public class UsuarioGrupo {
    private Integer id;         // Identificador da relação, se tiver no banco
    private Integer idUsuario;  // ID do usuário
    private Integer idGrupo;    // ID do grupo

    // Construtores
    public UsuarioGrupo() {
    }

    public UsuarioGrupo(Integer idUsuario, Integer idGrupo) {
        this.idUsuario = idUsuario;
        this.idGrupo = idGrupo;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
