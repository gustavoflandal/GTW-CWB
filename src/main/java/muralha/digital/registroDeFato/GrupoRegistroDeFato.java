package muralha.digital.registroDeFato;

import com.fasterxml.jackson.annotation.*;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "GrupoRegistroDeFato")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrupoRegistroDeFato {

    private Integer id_grupo;
    private String descricao;
    private Integer id_grupo_pai;
    private String pagina_inicial;
    private Integer nivel_ligacao;
    private Boolean config_muralha;

    @XmlElementWrapper(name = "usuarios")
    @XmlElement(name = "usuario")
    @JsonBackReference // JSON: evita loop
    private List<UsuarioRegistroDeFato> usuarios;

    public GrupoRegistroDeFato() {}

    public Integer getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(Integer id_grupo) {
        this.id_grupo = id_grupo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getId_grupo_pai() {
        return id_grupo_pai;
    }

    public void setId_grupo_pai(Integer id_grupo_pai) {
        this.id_grupo_pai = id_grupo_pai;
    }

    public String getPagina_inicial() {
        return pagina_inicial;
    }

    public void setPagina_inicial(String pagina_inicial) {
        this.pagina_inicial = pagina_inicial;
    }

    public Integer getNivel_ligacao() {
        return nivel_ligacao;
    }

    public void setNivel_ligacao(Integer nivel_ligacao) {
        this.nivel_ligacao = nivel_ligacao;
    }

    public Boolean getConfig_muralha() {
        return config_muralha;
    }

    public void setConfig_muralha(Boolean config_muralha) {
        this.config_muralha = config_muralha;
    }

    public List<UsuarioRegistroDeFato> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioRegistroDeFato> usuarios) {
        this.usuarios = usuarios;
    }
}
