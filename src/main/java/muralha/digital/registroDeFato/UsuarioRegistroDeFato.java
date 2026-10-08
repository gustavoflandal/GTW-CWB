package muralha.digital.registroDeFato;

import com.fasterxml.jackson.annotation.*;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "UsuarioRegistroDeFato")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsuarioRegistroDeFato {

    private Integer id_usuario;
    private String usuario;
    private String nome;
    private String email;
    private Boolean ativo;

    @XmlElementWrapper(name = "grupos")
    @XmlElement(name = "grupo")
    @JsonManagedReference // JSON: evita loop
    private List<GrupoRegistroDeFato> grupos;

    public UsuarioRegistroDeFato() {}

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<GrupoRegistroDeFato> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRegistroDeFato> grupos) {
        this.grupos = grupos;
    }
}
