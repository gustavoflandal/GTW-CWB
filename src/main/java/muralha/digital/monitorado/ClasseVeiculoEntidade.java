package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ClasseVeiculo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClasseVeiculoEntidade {

    private String idClasse;
    private String descricao;
    private Integer idClasseGit;
    private String idClasseTr;

    public String getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(String idClasse) {
        this.idClasse = idClasse;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getIdClasseGit() {
        return idClasseGit;
    }

    public void setIdClasseGit(Integer idClasseGit) {
        this.idClasseGit = idClasseGit;
    }

    public String getIdClasseTr() {
        return idClasseTr;
    }

    public void setIdClasseTr(String idClasseTr) {
        this.idClasseTr = idClasseTr;
    }
}