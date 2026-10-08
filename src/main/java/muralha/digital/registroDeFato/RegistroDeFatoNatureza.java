package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RegistroDeFatoNatureza")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoNatureza {

    private Integer id;
    private Integer idRegistroTipo;
    private String naturezaDesc;
    private Boolean requerBo;

    public RegistroDeFatoNatureza() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdRegistroTipo() {
        return idRegistroTipo;
    }

    public void setIdRegistroTipo(Integer idRegistroTipo) {
        this.idRegistroTipo = idRegistroTipo;
    }

    public String getNaturezaDesc() {
        return naturezaDesc;
    }

    public void setNaturezaDesc(String naturezaDesc) {
        this.naturezaDesc = naturezaDesc;
    }

    public Boolean getRequerBo() {
        return requerBo;
    }

    public void setRequerBo(Boolean requerBo) {
        this.requerBo = requerBo;
    }
}