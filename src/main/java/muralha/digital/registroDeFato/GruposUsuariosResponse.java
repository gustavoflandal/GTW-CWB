package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "GruposUsuariosResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GruposUsuariosResponse {

    @XmlElementWrapper(name = "grupos")
    @XmlElement(name = "grupo")
    private List<GrupoRegistroDeFato> grupos;

    @XmlElementWrapper(name = "usuarios")
    @XmlElement(name = "usuario")
    private List<UsuarioRegistroDeFato> usuarios;

    public GruposUsuariosResponse() {}

    public GruposUsuariosResponse(List<GrupoRegistroDeFato> grupos, List<UsuarioRegistroDeFato> usuarios) {
        this.grupos = grupos;
        this.usuarios = usuarios;
    }

    public List<GrupoRegistroDeFato> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRegistroDeFato> grupos) {
        this.grupos = grupos;
    }

    public List<UsuarioRegistroDeFato> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioRegistroDeFato> usuarios) {
        this.usuarios = usuarios;
    }
}
