package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "UsuarioGrupoRegistroDeFato")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsuarioGrupoRegistroDeFato {

    private Integer id_usuario;
    private Integer id_grupo;

    public UsuarioGrupoRegistroDeFato() {}

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(Integer id_grupo) {
        this.id_grupo = id_grupo;
    }
}
